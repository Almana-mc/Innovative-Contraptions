package me.almana.innovativecontraptions.content.thermal;

import me.almana.innovativecontraptions.content.energy.BatteryItem;
import me.almana.innovativecontraptions.content.energy.DischargeFuel;
import me.almana.innovativecontraptions.content.energy.EuSink;
import me.almana.innovativecontraptions.content.energy.net.EuGrids;
import me.almana.innovativecontraptions.registry.ICItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public final class ElectricHeaterBlockEntity extends ThermalInventory implements EuSink, HeatSource {
    public static final int MAX_COILS = 10;
    public static final int HEAT_PER_COIL = 10;
    public static final int CAPACITY = 10_000;
    public static final int BATTERY_SLOT = MAX_COILS;
    private static final int[] COILS = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    private static final int[] BATTERY = {BATTERY_SLOT};
    private int energy;
    private int transferred;
    private long transferTick = Long.MIN_VALUE;

    public ElectricHeaterBlockEntity(BlockPos pos, BlockState state) {
        super(ThermalContent.HEATER.get(), pos, state, MAX_COILS + 1);
    }

    @Override public void tick() {
        int space = Math.max(0, CAPACITY - energy);
        int amount = DischargeFuel.burn(items.get(BATTERY_SLOT), space, true);
        if (amount == 0) amount = BatteryItem.extractEnergy(items.get(BATTERY_SLOT), space, 4, false);
        if (amount > 0) { energy += amount; setChanged(); }
    }

    @Override
    public int supplyHeat(Direction side, int requested) {
        if (side != getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING) || requested <= 0) return 0;
        long now = level.getGameTime();
        if (now != transferTick) { transferred = 0; transferTick = now; }
        int supplied = Math.max(0, Math.min(requested, Math.min(energy, coilCount() * HEAT_PER_COIL - transferred)));
        energy -= supplied;
        transferred += supplied;
        if (supplied > 0) setChanged();
        return supplied;
    }

    public int coilCount() {
        int count = 0;
        for (int slot = 0; slot < MAX_COILS; slot++) if (items.get(slot).is(ICItems.COIL.get())) count++;
        return count;
    }

    public int energy() { return energy; }
    boolean active() { return transferred > 0 && level.getGameTime() - transferTick <= 1; }
    @Override public int demandedEu() { return Math.max(0, CAPACITY - energy); }

    @Override public int receiveEu(int voltage, int amount) {
        int accepted = Math.max(0, Math.min(amount, CAPACITY - energy));
        if (accepted > 0) { energy += accepted; setChanged(); }
        return accepted;
    }
    @Override public boolean canPlaceItem(int slot, ItemStack stack) {
        if (slot != BATTERY_SLOT) return items.get(slot).isEmpty() && stack.is(ICItems.COIL.get());
        return stack.getItem() instanceof BatteryItem battery && battery.canSupplyEnergy() || DischargeFuel.value(stack) > 0;
    }
    @Override public int getMaxStackSize(ItemStack stack) { return stack.is(ICItems.COIL.get()) ? 1 : stack.getMaxStackSize(); }
    @Override public int[] getSlotsForFace(Direction side) { return side == Direction.UP ? COILS : BATTERY; }
    @Override public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) { return slot == BATTERY_SLOT; }
    @Override protected int displayValue(int index) {
        return switch (index) {
            case 0 -> energy;
            case 1 -> CAPACITY;
            case 2 -> coilCount();
            case 3 -> MAX_COILS;
            case 4 -> level != null && transferTick >= level.getGameTime() - 1 ? transferred : 0;
            default -> 0;
        };
    }
    @Override protected void receiveDisplayValue(int index, int value) { if (index == 0) energy = value; }
    @Override public void onLoad() {
        super.onLoad();
        if (level != null && !level.isClientSide()) EuGrids.markDirty(level, worldPosition);
    }
    @Override public void setRemoved() {
        if (level != null && !level.isClientSide()) EuGrids.markDirty(level, worldPosition);
        super.setRemoved();
    }
    @Override protected void saveAdditional(ValueOutput output) { super.saveAdditional(output); output.putInt("energy", energy); }
    @Override protected void loadAdditional(ValueInput input) { super.loadAdditional(input); energy = input.getIntOr("energy", 0); }
}
