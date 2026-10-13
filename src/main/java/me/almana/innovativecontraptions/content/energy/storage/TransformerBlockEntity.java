package me.almana.innovativecontraptions.content.energy.storage;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public final class TransformerBlockEntity extends ElectricalBlockEntity {
    public TransformerBlockEntity(BlockPos pos, BlockState state) { super(pos, state); }
    public boolean stepUp() { return mode == 2 || mode == 0 && level.hasNeighborSignal(worldPosition); }
    @Override public boolean accepts(Direction side) { return (side == facing()) != stepUp(); }
    @Override public boolean emits(Direction side) { return !accepts(side); }
    @Override public int modeCount() { return 3; }
    @Override public int signal() { return 0; }
    @Override public int sourceVoltage() { return kind.packet() * (stepUp() ? 4 : 1); }
    @Override public int maxInputVoltage() { return kind.packet() * (stepUp() ? 1 : 4); }
    @Override public int packetsPerTick() { return stepUp() ? 1 : 4; }
    @Override public boolean canPlaceItem(int slot, ItemStack stack) { return false; }
    @Override protected void tickEnergy() {
        if (active == stepUp()) return;
        active = stepUp();
        refreshConnections();
    }
}
