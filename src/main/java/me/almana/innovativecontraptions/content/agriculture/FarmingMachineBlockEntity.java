package me.almana.innovativecontraptions.content.agriculture;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import me.almana.innovativecontraptions.content.energy.BatteryItem;
import me.almana.innovativecontraptions.content.energy.DischargeFuel;
import me.almana.innovativecontraptions.content.energy.EuSink;
import me.almana.innovativecontraptions.content.energy.net.EuGrids;
import me.almana.innovativecontraptions.content.thermal.ThermalInventory;
import me.almana.innovativecontraptions.registry.ICBlocks;
import me.almana.innovativecontraptions.registry.ICItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.FarmlandBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;

public final class FarmingMachineBlockEntity extends ThermalInventory implements EuSink {
    public static final int BASE_CAPACITY = 10_000, TANK_CAPACITY = 2000, SCAN_INTERVAL = 10, SCAN_VOLUME = 243;
    public static final int SCAN_COST = 1, CARE_COST = 10, HARVEST_COST = 20;
    private final boolean matron;
    private int energy, cursor, status;
    private final FluidStacksResourceHandler tanks = new FluidStacksResourceHandler(2, TANK_CAPACITY) {
        @Override public boolean isValid(int index, FluidResource fluid) { return fluid.getFluid() == (index == 0 ? Fluids.WATER : FarmingContent.TREATMENT.get()); }
        @Override protected void onContentsChanged(int index, FluidStack previous) { setChanged(); }
    };
    public FarmingMachineBlockEntity(BlockPos pos, BlockState state) {
        super(FarmingContent.MACHINE.get(), pos, state, state.is(ICBlocks.CROP_MATRON.get()) ? 16 : 20);
        matron = state.is(ICBlocks.CROP_MATRON.get());
    }
    public boolean matron() { return matron; }
    public int energy() { return energy; }
    public int cursor() { return cursor; }
    public FluidStacksResourceHandler tanks() { return tanks; }
    public int fluidAmount(int index) { return tanks.getAmountAsInt(index); }
    public int capacity() { return BASE_CAPACITY; }
    public int batterySlot() { return matron ? 15 : 19; }
    public int upgradeStart() { return matron ? 11 : 15; }
    public int upgradeCount(Item item) {
        int count = 0;
        for (int slot = upgradeStart(); slot < upgradeStart() + 4; slot++) if (items.get(slot).is(item)) count += items.get(slot).getCount();
        return count;
    }
    @Override public void tick() {
        discharge();
        if (matron) {
            FarmingTransfers.emptyContainer(this, 7, 8, 0);
            FarmingTransfers.emptyContainer(this, 9, 10, 1);
        }
        FarmingTransfers.upgrades(this);
        if (level.getGameTime() % SCAN_INTERVAL != 0) return;
        scan();
    }
    private void discharge() {
        if (demandedEu() == 0) return;
        ItemStack stack = items.get(batterySlot());
        int fuel = DischargeFuel.burn(stack, demandedEu(), matron);
        if (fuel > 0) { energy += fuel; setChanged(); return; }
        int amount = BatteryItem.extractEnergy(stack, demandedEu(), 1, false);
        if (amount > 0) { energy += amount; setChanged(); }
    }
    public void scan() {
        if (energy < (matron ? 31 : 21)) { status = 1; return; }
        BlockPos target = scanPosition(cursor);
        cursor = (cursor + 1) % SCAN_VOLUME;
        energy -= SCAN_COST; status = 0;
        if (level.hasChunkAt(target)) {
            if (level.getBlockEntity(target) instanceof CropPlotBlockEntity crop) {
                if (matron) care(crop); else harvest(crop);
            } else if (matron) hydrateFarmland(target);
        }
        setChanged();
    }
    public BlockPos scanPosition(int index) { return worldPosition.offset(index % 9 - 4, index / 81 - 1, index / 9 % 9 - 4); }
    private void care(CropPlotBlockEntity crop) {
        for (int slot = 0; slot < 7; slot++) {
            if (items.get(slot).is(ICItems.FERTILIZER.get()) && crop.fertilize(true) > 0) {
                items.get(slot).shrink(1); energy -= CARE_COST; break;
            }
        }
        int used = crop.hydrate(fluidAmount(0));
        if (used > 0) { drain(0, used); energy -= CARE_COST; }
        used = crop.protect(fluidAmount(1), true);
        if (used > 0) { drain(1, used); energy -= CARE_COST; }
    }
    private void hydrateFarmland(BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof FarmlandBlock)) return;
        int amount = Math.min(7 - state.getValue(FarmlandBlock.MOISTURE), fluidAmount(0));
        if (amount == 0) return;
        level.setBlockAndUpdate(pos, state.setValue(FarmlandBlock.MOISTURE, state.getValue(FarmlandBlock.MOISTURE) + amount));
        drain(0, amount); energy -= CARE_COST;
    }
    private void drain(int index, int amount) { tanks.set(index, tanks.getResource(index), fluidAmount(index) - amount); }
    private void harvest(CropPlotBlockEntity crop) {
        if (crop.age() != crop.species().optimal() && crop.age() != crop.species().stages) return;
        if (IntStream.range(0, 15).noneMatch(slot -> items.get(slot).isEmpty() || items.get(slot).getCount() < items.get(slot).getMaxStackSize())) { status = 2; return; }
        var drops = crop.harvestPreview();
        if (energy < drops.size() * HARVEST_COST) { status = 1; return; }
        List<ItemStack> updated = new ArrayList<>();
        for (int slot = 0; slot < 15; slot++) updated.add(items.get(slot).copy());
        for (ItemStack drop : drops) if (!distribute(updated, drop.copy())) { status = 2; return; }
        for (int slot = 0; slot < 15; slot++) items.set(slot, updated.get(slot));
        energy -= drops.size() * HARVEST_COST; crop.finishHarvest();
    }
    private boolean distribute(List<ItemStack> inventory, ItemStack drop) {
        for (int slot = 0; slot < inventory.size() && !drop.isEmpty(); slot++) {
            ItemStack existing = inventory.get(slot);
            if (!existing.isEmpty() && !ItemStack.isSameItemSameComponents(existing, drop)) continue;
            int accepted = Math.min(drop.getCount(), drop.getMaxStackSize() - existing.getCount());
            if (existing.isEmpty()) inventory.set(slot, drop.copyWithCount(accepted)); else existing.grow(accepted);
            drop.shrink(accepted);
        }
        return drop.isEmpty();
    }
    @Override public int demandedEu() { return Math.max(0, capacity() - energy); }
    @Override public int maxInputVoltage() { return 32; }
    @Override public int receiveEu(int voltage, int amount) {
        if (voltage > maxInputVoltage()) return 0;
        int accepted = Math.clamp(amount, 0, demandedEu()); energy += accepted; if (accepted > 0) setChanged(); return accepted;
    }
    @Override public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) { return new FarmingMenu(id, inventory, this); }
    @Override public boolean canPlaceItem(int slot, ItemStack stack) {
        if (slot >= upgradeStart() && slot < upgradeStart() + 4) return FarmingTransfers.validUpgrade(stack, matron);
        if (slot == batterySlot()) return stack.is(ICItems.ENERGIUM_DUST.get()) || stack.is(ICItems.SINGLE_USE_BATTERY.get())
                || matron && stack.is(Items.REDSTONE)
                || stack.getItem() instanceof BatteryItem battery && battery.canSupplyEnergy() && battery.tier().ordinal() == 0;
        if (!matron) return false;
        if (slot < 7) return stack.is(ICItems.FERTILIZER.get());
        return (slot == 7 || slot == 9) && FarmingTransfers.contains(stack, slot == 7 ? Fluids.WATER : FarmingContent.TREATMENT.get());
    }
    @Override public int[] getSlotsForFace(Direction side) {
        return side == Direction.DOWN ? (matron ? new int[]{8, 10} : IntStream.range(0, 15).toArray())
                : matron ? new int[]{0, 1, 2, 3, 4, 5, 6, 7, 9} : new int[0];
    }
    @Override public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) { return matron ? slot == 8 || slot == 10 : slot < 15; }
    @Override protected int displayValue(int index) { return switch (index) { case 0 -> energy; case 1 -> capacity(); case 2 -> cursor; case 3 -> fluidAmount(0); case 4 -> fluidAmount(1); default -> status; }; }
    @Override protected void receiveDisplayValue(int index, int value) { if (index == 0) energy = value; }
    @Override public void onLoad() { super.onLoad(); if (!level.isClientSide()) EuGrids.markDirty(level, worldPosition); }
    @Override public void setRemoved() { if (level != null && !level.isClientSide()) EuGrids.markDirty(level, worldPosition); super.setRemoved(); }
    @Override protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output); output.putInt("energy", energy); output.putInt("cursor", cursor);
        output.putInt("water", fluidAmount(0)); output.putInt("treatment", fluidAmount(1));
    }
    @Override protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input); energy = input.getIntOr("energy", 0); cursor = Math.floorMod(input.getIntOr("cursor", 0), SCAN_VOLUME);
        tanks.set(0, FluidResource.of(Fluids.WATER), Math.clamp(input.getIntOr("water", 0), 0, TANK_CAPACITY));
        tanks.set(1, FluidResource.of(FarmingContent.TREATMENT.get()), Math.clamp(input.getIntOr("treatment", 0), 0, TANK_CAPACITY));
    }
}
