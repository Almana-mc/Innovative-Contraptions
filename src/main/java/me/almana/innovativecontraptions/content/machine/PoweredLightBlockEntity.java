package me.almana.innovativecontraptions.content.machine;

import me.almana.innovativecontraptions.compat.SavedBlockEntity;
import me.almana.innovativecontraptions.content.energy.BatteryItem;
import me.almana.innovativecontraptions.content.energy.EuSink;
import me.almana.innovativecontraptions.content.energy.EuTier;
import me.almana.innovativecontraptions.content.energy.net.EuGrids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public final class PoweredLightBlockEntity extends SavedBlockEntity implements EuSink {
    public static final int CAPACITY = 5;
    public static final int MANUAL_CAPACITY = 10_000;
    public static final int TICKS_PER_EU = 4;
    private int quarterEu;
    private boolean inverted;
    private int previousComparator;

    public PoweredLightBlockEntity(BlockPos pos, BlockState state) { super(PoweredLightContent.ENTITY.get(), pos, state); }
    public double energy() { return quarterEu / 4.0; }
    public boolean inverted() { return inverted; }
    public int comparator() { return Math.min(15, quarterEu * 15 / (CAPACITY * 4)); }
    @Override public int maxInputVoltage() { return EuTier.EV.volts; }
    @Override public int demandedEu() { return Math.max(0, (CAPACITY * 4 - quarterEu) / 4); }
    public boolean canConnect(Direction side) { return side == getBlockState().getValue(PoweredLightBlock.FACING).getOpposite(); }
    @Override public int receiveEu(int voltage, int amount) {
        if (voltage > maxInputVoltage()) return 0;
        int accepted = Math.max(0, Math.min(amount, demandedEu()));
        quarterEu += accepted * 4;
        if (accepted > 0) setChanged();
        return accepted;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, PoweredLightBlockEntity light) {
        boolean lit = light.quarterEu > 0 && level.hasNeighborSignal(pos) == light.inverted;
        if (lit) { light.quarterEu--; light.setChanged(); }
        if (state.getValue(PoweredLightBlock.LIT) != lit) level.setBlock(pos, state.setValue(PoweredLightBlock.LIT, lit), 3);
        if (light.previousComparator != light.comparator()) {
            light.previousComparator = light.comparator();
            level.updateNeighbourForOutputSignal(pos, state.getBlock());
        }
    }

    public void interact(ItemStack stack) {
        int accepted = BatteryItem.extractEnergy(stack,
                (MANUAL_CAPACITY * 4 - quarterEu) / 4, 1, true);
        if (accepted > 0) quarterEu += accepted * 4;
        else inverted = !inverted;
        setChanged();
    }

    @Override public void onLoad() { super.onLoad(); if (!level.isClientSide()) EuGrids.markDirty(level, worldPosition); }
    @Override public void setRemoved() { if (level != null && !level.isClientSide()) EuGrids.markDirty(level, worldPosition); super.setRemoved(); }
    @Override protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("quarter_eu", quarterEu);
        output.putBoolean("inverted", inverted);
    }
    @Override protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        quarterEu = Math.clamp(input.getIntOr("quarter_eu", 0), 0, MANUAL_CAPACITY * 4);
        inverted = input.getBooleanOr("inverted", false);
    }
}
