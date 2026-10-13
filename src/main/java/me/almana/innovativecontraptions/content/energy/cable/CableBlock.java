package me.almana.innovativecontraptions.content.energy.cable;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.almana.innovativecontraptions.content.energy.EuConnectable;
import me.almana.innovativecontraptions.content.energy.net.EuGrids;
import me.almana.innovativecontraptions.registry.IcCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
//? if >=26.1 {
import net.minecraft.world.level.ScheduledTickAccess;
//?} else
//import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;

public class CableBlock extends Block {
    public static final MapCodec<CableBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            CableSpec.CODEC.fieldOf("spec").forGetter(block -> block.spec),
            propertiesCodec()
    ).apply(instance, CableBlock::new));

    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;

    private static final Map<Direction, BooleanProperty> BY_DIRECTION = Map.of(
            Direction.NORTH, NORTH,
            Direction.SOUTH, SOUTH,
            Direction.EAST, EAST,
            Direction.WEST, WEST,
            Direction.UP, UP,
            Direction.DOWN, DOWN
    );

    private static VoxelShape[] buildShapes(int diameter) {
        double start = (16 - diameter) / 2.0, end = 16 - start;
        VoxelShape core = box(start, start, start, end, end, end);
        VoxelShape[] arms = {
                box(start, 0, start, end, start, end), box(start, end, start, end, 16, end),
                box(start, start, 0, end, end, start), box(start, start, end, end, end, 16),
                box(0, start, start, start, end, end), box(end, start, start, 16, end, end)
        };
        VoxelShape[] shapes = new VoxelShape[64];
        for (int mask = 0; mask < 64; mask++) {
            VoxelShape s = core;
            for (int d = 0; d < 6; d++) {
                if ((mask & (1 << d)) != 0) s = Shapes.or(s, arms[d]);
            }
            shapes[mask] = s;
        }
        return shapes;
    }

    private static int shapeMask(BlockState state) {
        int mask = 0;
        for (Direction dir : Direction.values()) {
            if (state.getValue(BY_DIRECTION.get(dir))) mask |= (1 << dir.ordinal());
        }
        return mask;
    }

    private final CableSpec spec;
    private final VoxelShape[] shapes;

    public CableBlock(CableSpec spec, Properties properties) {
        super(properties);
        this.spec = spec;
        shapes = buildShapes(spec.diameter());
        registerDefaultState(defaultBlockState()
                .setValue(NORTH, false).setValue(SOUTH, false)
                .setValue(EAST, false).setValue(WEST, false)
                .setValue(UP, false).setValue(DOWN, false));
    }

    public CableSpec spec() {
        return spec;
    }

    @Override
    protected MapCodec<? extends CableBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shapes[shapeMask(state)];
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return connectedState(defaultBlockState(), context.getLevel(), context.getClickedPos());
    }

    @Override
    //? if >=26.1 {
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos,
                                     Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState,
                                     RandomSource random) {
    //?} else {
    /*protected BlockState updateShape(BlockState state, Direction directionToNeighbour, BlockState neighbourState,
                                     LevelAccessor level, BlockPos pos, BlockPos neighbourPos) {
    *///?}
        boolean connected = canConnectTo(level, neighbourPos, directionToNeighbour.getOpposite());
        return state.setValue(BY_DIRECTION.get(directionToNeighbour), connected);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (!level.isClientSide()) {
            EuGrids.markDirty(level, pos);
        }
    }

    //? if >=26.1 {
    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        EuGrids.markDirty(level, pos);
        super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
    }
    //?} else {
    /*@Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) EuGrids.markDirty(level, pos);
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
    *///?}

    private BlockState connectedState(BlockState base, LevelReader level, BlockPos pos) {
        BlockState result = base;
        for (Direction dir : Direction.values()) {
            result = result.setValue(BY_DIRECTION.get(dir), canConnectTo(level, pos.relative(dir), dir.getOpposite()));
        }
        return result;
    }

    private static boolean canConnectTo(LevelReader level, BlockPos neighborPos, Direction fromSide) {
        BlockState neighbor = level.getBlockState(neighborPos);
        if (neighbor.getBlock() instanceof CableBlock) {
            return true;
        }
        if (neighbor.getBlock() instanceof EuConnectable) {
            return true;
        }
        if (level instanceof ServerLevel serverLevel) {
            return IcCapabilities.EU_SINK.getCapability(serverLevel, neighborPos, null, null, fromSide) != null
                    || IcCapabilities.EU_SOURCE.getCapability(serverLevel, neighborPos, null, null, fromSide) != null;
        }
        return false;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, SOUTH, EAST, WEST, UP, DOWN);
    }
}
