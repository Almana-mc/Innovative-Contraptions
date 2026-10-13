package me.almana.innovativecontraptions.content.gathering;

import java.util.stream.IntStream;
import me.almana.innovativecontraptions.content.energy.BatteryItem;
import me.almana.innovativecontraptions.content.energy.EuSink;
import me.almana.innovativecontraptions.content.energy.EuTier;
import me.almana.innovativecontraptions.content.energy.net.EuGrids;
import me.almana.innovativecontraptions.content.equipment.DrillItem;
import me.almana.innovativecontraptions.content.thermal.ThermalInventory;
import me.almana.innovativecontraptions.registry.ICBlocks;
import me.almana.innovativecontraptions.registry.ICItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;

public final class GatheringBlockEntity extends ThermalInventory implements EuSink {
    public static final int PUMP_TANK = 8000, REMOTE_CAPACITY = 4_000_000, TERRAIN_CAPACITY = 100_000;
    public static final int OUTPUT = 23, SCANNER = 2, BATTERY = 3, UPGRADE_START = 4, FILTER_START = 8;
    private static final int[] BUFFER = IntStream.range(FILTER_START, OUTPUT).toArray();
    private final boolean advanced, pump, terrain;
    int energy, progress, cursor, depth, scanDepth = Integer.MIN_VALUE, status, clock, failures;
    BlockPos target, lastTerrain;
    boolean whitelist, silk, paused, acceptingFluid;
    final FluidStacksResourceHandler tank = new FluidStacksResourceHandler(1, PUMP_TANK) {
        @Override public boolean isValid(int index, FluidResource fluid) { return acceptingFluid; }
        @Override protected void onContentsChanged(int index, FluidStack previous) { setChanged(); }
    };
    public final ContainerData display = new ContainerData() {
        public int get(int index) {
            int value = switch (index / 2) {
                case 0 -> energy; case 1 -> capacity(); case 2 -> target == null ? worldPosition.getX() : target.getX();
                case 3 -> target == null ? depth : target.getY(); case 4 -> target == null ? worldPosition.getZ() : target.getZ();
                case 5 -> status | (whitelist ? 256 : 0) | (silk ? 512 : 0) | (paused ? 1024 : 0);
                case 6 -> progress; default -> tank.getAmountAsInt(0);
            };
            return index % 2 == 0 ? value & 65535 : value >>> 16;
        }
        public void set(int index, int value) {}
        public int getCount() { return 16; }
    };
    public GatheringBlockEntity(BlockPos pos, BlockState state) {
        super(GatheringContent.MACHINES.get(), pos, state, 24);
        advanced = state.is(ICBlocks.ADVANCED_MINER.get()); pump = state.is(ICBlocks.PUMP.get()); terrain = state.is(GatheringContent.TERRAIN.get());
        depth = pos.getY();
    }
    public boolean advanced() { return advanced; }
    public boolean pump() { return pump; }
    public boolean terrain() { return terrain; }
    public int energy() { return energy; }
    public int progress() { return progress; }
    public int depth() { return depth; }
    public int status() { return status; }
    public BlockPos target() { return target; }
    public FluidStacksResourceHandler fluidOutput() { return tank; }
    public int fluidAmount() { return tank.getAmountAsInt(0); }
    public boolean whitelist() { return whitelist; }
    public boolean silk() { return silk; }
    public int countUpgrade(Item item) {
        int count = 0;
        for (int slot = 4; slot < 8; slot++) if (items.get(slot).is(item)) count += items.get(slot).getCount();
        return count;
    }
    public int capacity() {
        int base = terrain ? TERRAIN_CAPACITY : advanced ? REMOTE_CAPACITY : pump ? (int) Math.min(Integer.MAX_VALUE, 20L + (long) PumpWork.duration(this) * PumpWork.power(this)) : 10_000;
        return advanced || terrain ? base : (int) Math.min(Integer.MAX_VALUE, base + 10_000L * countUpgrade(ICItems.ENERGY_STORAGE_UPGRADE.get()));
    }
    public int inputTier() { return Math.min(5, (terrain ? 4 : advanced ? 3 : 1) + countUpgrade(ICItems.TRANSFORMER_UPGRADE.get())); }
    @Override public int maxInputVoltage() { return EuTier.values()[inputTier() - 1].volts; }
    @Override public int demandedEu() { return Math.max(0, capacity() - energy); }
    @Override public int receiveEu(int voltage, int amount) {
        if (voltage > maxInputVoltage()) return 0;
        int accepted = Math.clamp(amount, 0, demandedEu()); energy += accepted;
        if (accepted > 0) setChanged();
        return accepted;
    }
    @Override public void tick() {
        if (level.isClientSide()) return;
        energy += BatteryItem.extractEnergy(items.get(BATTERY), demandedEu(), inputTier(), false);
        if (terrain) TerrainWork.tick(this);
        else if (pump) PumpWork.tick(this);
        else {
            charge(SCANNER, advanced ? inputTier() : 2);
            if (!advanced) charge(0, 3);
            if (advanced) MiningWork.remote(this); else MiningWork.bore(this);
        }
        if (!terrain) GatheringTransfers.upgrades(this);
        setChanged();
    }
    private void charge(int slot, int tier) {
        int amount = BatteryItem.receiveEnergy(items.get(slot), energy, tier, false); energy -= amount;
    }
    public boolean stopped() { return paused || level.hasNeighborSignal(worldPosition) != (countUpgrade(ICItems.REDSTONE_INVERTER_UPGRADE.get()) % 2 == 1); }
    public boolean button(int button) {
        if (!advanced) return false;
        if (button == 0) { paused = !paused; setChanged(); return true; }
        if (!paused && !stopped() && status == 0) return false;
        switch (button) {
            case 1 -> whitelist = !whitelist;
            case 2 -> silk = !silk;
            case 3 -> { cursor = 0; depth = worldPosition.getY(); target = null; }
            default -> { return false; }
        }
        setChanged(); return true;
    }
    public void programInteraction(Player player, InteractionHand hand) {
        if (!terrain) return;
        if (!items.get(0).isEmpty()) {
            Containers.dropItemStack(level, worldPosition.getX() + .5, worldPosition.getY() + 1, worldPosition.getZ() + .5, removeItem(0, 1));
        } else if (player.getItemInHand(hand).getItem() instanceof TerrainProgramItem) {
            setItem(0, player.getItemInHand(hand).split(1));
        }
        lastTerrain = null; failures = 0;
    }
    @Override public boolean canPlaceItem(int slot, ItemStack stack) {
        if (terrain) return slot == 0 && stack.getItem() instanceof TerrainProgramItem;
        if (slot >= 4 && slot < 8) return (pump || advanced || slot == 4) && GatheringTransfers.validUpgrade(this, stack);
        if (slot == BATTERY) return stack.getItem() instanceof BatteryItem battery && battery.canSupplyEnergy() && battery.tier().ordinal() < inputTier();
        if (pump) return slot == 0 && ItemAccess.forStack(stack.copyWithCount(1)).getCapability(Capabilities.Fluid.ITEM) != null;
        if (slot == SCANNER) return stack.getItem() instanceof OreProbeItem;
        if (advanced) return slot >= FILTER_START && slot < OUTPUT;
        return slot == 0 ? stack.getItem() instanceof DrillItem : slot == 1 && stack.is(GatheringContent.PIPE_ITEM.get());
    }
    public boolean buffered() { return !advanced && !pump && !terrain; }
    @Override public int[] getSlotsForFace(Direction side) {
        if (terrain) return new int[0];
        if (side == Direction.DOWN) return buffered() ? BUFFER : new int[]{OUTPUT};
        return pump ? new int[]{0} : advanced ? new int[]{SCANNER} : new int[]{0, 1, SCANNER};
    }
    @Override public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return buffered() ? slot >= FILTER_START && slot < OUTPUT : slot == OUTPUT;
    }
    @Override public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) { return new GatheringMenu(id, inventory, this); }
    @Override protected int displayValue(int index) { return 0; }
    @Override protected void receiveDisplayValue(int index, int value) {}
    @Override public void onLoad() { super.onLoad(); if (!level.isClientSide()) EuGrids.markDirty(level, worldPosition); }
    @Override public void setRemoved() { if (level != null && !level.isClientSide()) EuGrids.markDirty(level, worldPosition); super.setRemoved(); }
    @Override public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        if (advanced) for (int slot = FILTER_START; slot < OUTPUT; slot++) items.set(slot, ItemStack.EMPTY);
        super.preRemoveSideEffects(pos, state);
    }
    @Override protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("energy", energy); output.putInt("progress", progress); output.putInt("cursor", cursor); output.putInt("depth", depth);
        output.putInt("scan_depth", scanDepth); output.putInt("clock", clock); output.putBoolean("whitelist", whitelist); output.putBoolean("silk", silk); output.putBoolean("paused", paused);
        if (target != null) output.putLong("target", target.asLong());
        if (lastTerrain != null) output.putLong("terrain_last", lastTerrain.asLong());
        output.putInt("failures", failures); output.putInt("fluid_amount", fluidAmount());
        output.putString("fluid", BuiltInRegistries.FLUID.getKey(tank.getResource(0).getFluid()).toString());
    }
    @Override protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        energy = Math.clamp(input.getIntOr("energy", 0), 0, capacity()); progress = input.getIntOr("progress", 0); cursor = input.getIntOr("cursor", 0);
        depth = input.getIntOr("depth", worldPosition.getY()); scanDepth = input.getIntOr("scan_depth", Integer.MIN_VALUE); clock = input.getIntOr("clock", 0);
        whitelist = input.getBooleanOr("whitelist", false); silk = input.getBooleanOr("silk", false); paused = input.getBooleanOr("paused", false);
        target = input.getLong("target").map(BlockPos::of).orElse(null); lastTerrain = input.getLong("terrain_last").map(BlockPos::of).orElse(null); failures = input.getIntOr("failures", 0);
        var fluid = BuiltInRegistries.FLUID.getValue(Identifier.parse(input.getStringOr("fluid", "minecraft:empty")));
        tank.set(0, FluidResource.of(fluid), fluid == Fluids.EMPTY ? 0 : Math.clamp(input.getIntOr("fluid_amount", 0), 0, PUMP_TANK));
    }
}
