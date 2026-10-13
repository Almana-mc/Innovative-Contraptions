package me.almana.innovativecontraptions.content.machine;

import com.mojang.serialization.MapCodec;
import me.almana.innovativecontraptions.content.tool.Wrenchable;
import me.almana.innovativecontraptions.content.tool.WrenchableSupport;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public class MachineBlock extends HorizontalDirectionalBlock implements Wrenchable {
    public static final MapCodec<MachineBlock> CODEC = simpleCodec(MachineBlock::new);

    public MachineBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @Override
    public MapCodec<? extends MachineBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public Direction getFacing(Level level, BlockPos pos) {
        return WrenchableSupport.getHorizontalFacing(level, pos, FACING);
    }

    @Override
    public boolean setFacing(Level level, BlockPos pos, Direction newFacing, Player player) {
        return WrenchableSupport.setHorizontalFacing(level, pos, FACING, newFacing);
    }
}
