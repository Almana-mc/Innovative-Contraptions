package me.almana.innovativecontraptions.compat;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BaseEntityBlock extends net.minecraft.world.level.block.BaseEntityBlock {
    public BaseEntityBlock(Properties properties) {
        super(properties);
    }

    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
    }

    // 26.1 removal order
    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        boolean replaced = !state.is(newState.getBlock());
        if (replaced) {
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof SavedBlockEntity saved) saved.preRemoveSideEffects(pos, state);
            else if (entity instanceof Container container) Containers.dropContents(level, pos, container);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
        if (replaced && level instanceof ServerLevel server) affectNeighborsAfterRemoval(state, server, pos, movedByPiston);
    }
}
