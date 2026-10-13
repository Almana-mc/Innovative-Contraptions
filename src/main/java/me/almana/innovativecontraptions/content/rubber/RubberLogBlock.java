package me.almana.innovativecontraptions.content.rubber;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class RubberLogBlock extends RotatedPillarBlock {
    public static final MapCodec<RubberLogBlock> CODEC = simpleCodec(RubberLogBlock::new);
    public static final EnumProperty<Direction> SAP_FACING = EnumProperty.create(
            "sap_facing",
            Direction.class,
            Direction.NORTH,
            Direction.SOUTH,
            Direction.WEST,
            Direction.EAST
    );
    public static final EnumProperty<SapStage> SAP_STAGE = EnumProperty.create("sap_stage", SapStage.class);

    public RubberLogBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState()
                .setValue(AXIS, Direction.Axis.Y)
                .setValue(SAP_FACING, Direction.NORTH)
                .setValue(SAP_STAGE, SapStage.NONE));
    }

    @Override
    public MapCodec<? extends RubberLogBlock> codec() {
        return CODEC;
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return state.getValue(SAP_STAGE) == SapStage.EMPTY;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(SAP_STAGE) == SapStage.EMPTY && random.nextInt(200) == 0) {
            level.setBlock(pos, state.setValue(SAP_STAGE, SapStage.READY), Block.UPDATE_ALL);
        }
    }

    public static boolean hasUsableSap(BlockState state, Direction clickedFace) {
        return state.getBlock() instanceof RubberLogBlock
                && state.getValue(AXIS) == Direction.Axis.Y
                && clickedFace == state.getValue(SAP_FACING)
                && state.getValue(SAP_STAGE) != SapStage.NONE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS, SAP_FACING, SAP_STAGE);
    }
}
