package me.almana.innovativecontraptions.content.reactor;

import me.almana.innovativecontraptions.content.energy.EuConnectable;
import me.almana.innovativecontraptions.registry.ICBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
//? if >=26.1 {
import net.minecraft.world.level.ScheduledTickAccess;
//?} else
//import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public final class ReactorChamberBlock extends Block implements EuConnectable {
    public ReactorChamberBlock(Properties properties) { super(properties); }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        return ReactorStructure.open(level, player, ReactorStructure.adjacent(level, pos));
    }

    @Override protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        for (Direction side : Direction.values())
            if (level.getBlockState(pos.relative(side)).is(ICBlocks.NUCLEAR_REACTOR.get())) return true;
        return false;
    }

    //? if >=26.1 {
    @Override protected BlockState updateShape(BlockState state, LevelReader level,
            ScheduledTickAccess ticks, BlockPos pos, Direction side,
            BlockPos neighbor, BlockState neighborState, RandomSource random) {
    //?} else {
    /*@Override protected BlockState updateShape(BlockState state, Direction side, BlockState neighborState,
            LevelAccessor level, BlockPos pos, BlockPos neighbor) {
    *///?}
        return state.canSurvive(level, pos) ? state : Blocks.AIR.defaultBlockState();
    }
}
