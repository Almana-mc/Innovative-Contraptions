package me.almana.innovativecontraptions.content.agriculture;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class WoodenScaffoldBlock extends Block {
    private static final VoxelShape SHAPE = Shapes.or(Block.box(0, 13, 0, 16, 16, 16),
            Block.box(1, 0, 1, 3, 13, 3), Block.box(13, 0, 1, 15, 13, 3), Block.box(1, 0, 13, 3, 13, 15), Block.box(13, 0, 13, 15, 13, 15));
    public WoodenScaffoldBlock(Properties properties) { super(properties); }
    @Override protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) { return SHAPE; }
    @Override public boolean isLadder(BlockState state, LevelReader level, BlockPos pos, LivingEntity entity) { return true; }
}
