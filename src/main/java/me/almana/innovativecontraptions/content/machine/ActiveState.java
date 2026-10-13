package me.almana.innovativecontraptions.content.machine;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public final class ActiveState {
    public static void update(Level level, BlockPos pos, boolean active) {
        BlockState state = level.getBlockState(pos);
        if (state.hasProperty(BlockStateProperties.LIT) && state.getValue(BlockStateProperties.LIT) != active)
            level.setBlock(pos, state.setValue(BlockStateProperties.LIT, active), Block.UPDATE_CLIENTS);
    }
    private ActiveState() {}
}
