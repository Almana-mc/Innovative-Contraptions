package me.almana.innovativecontraptions.world;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.almana.innovativecontraptions.content.rubber.RubberLogBlock;
import me.almana.innovativecontraptions.content.rubber.SapStage;
import me.almana.innovativecontraptions.registry.ICBlocks;
import me.almana.innovativecontraptions.registry.ICTrunkPlacers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
//? if >=26.1 {
import net.minecraft.world.level.WorldGenLevel;
//?} else
//import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class RubberTrunkPlacer extends TrunkPlacer {
    public static final MapCodec<RubberTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec(instance ->
            trunkPlacerParts(instance).apply(instance, RubberTrunkPlacer::new)
    );
    private static final Direction[] HORIZONTAL_FACES = {
            Direction.NORTH,
            Direction.SOUTH,
            Direction.WEST,
            Direction.EAST
    };

    public RubberTrunkPlacer(int baseHeight, int heightRandA, int heightRandB) {
        super(baseHeight, heightRandA, heightRandB);
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return ICTrunkPlacers.RUBBER.get();
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(
            //? if >=26.1 {
            WorldGenLevel level,
            //?} else
            //LevelSimulatedReader level,
            BiConsumer<BlockPos, BlockState> blockSetter,
            RandomSource random,
            int freeTreeHeight,
            BlockPos pos,
            TreeConfiguration config
    ) {
        //? if >=26.1 {
        placeBelowTrunkBlock(level, blockSetter, random, pos.below(), config);
        //?} else
        //setDirtAt(level, blockSetter, random, pos.below(), config);

        int sapPatches = 1 + random.nextInt(2);
        int firstSapHeight = 1 + random.nextInt(Math.max(1, freeTreeHeight - 1));

        for (int y = 0; y < freeTreeHeight; y++) {
            int sapIndex = y == firstSapHeight ? 0 : y == firstSapHeight + 2 && sapPatches == 2 ? 1 : -1;
            Function<BlockState, BlockState> properties = state -> withRubberLogState(state, random, sapIndex);
            placeLog(level, blockSetter, random, pos.above(y), config, properties);
        }

        BlockState leaves = ICBlocks.RUBBER_LEAVES.get().defaultBlockState();
        for (int y = 1; y <= 2; y++) {
            BlockPos spike = pos.above(freeTreeHeight + y);
            if (validTreePos(level, spike)) blockSetter.accept(spike, leaves.setValue(LeavesBlock.DISTANCE, y + 1));
        }

        return ImmutableList.of(new FoliagePlacer.FoliageAttachment(pos.above(freeTreeHeight), 0, false));
    }

    private static BlockState withRubberLogState(BlockState state, RandomSource random, int sapIndex) {
        state = state.setValue(RotatedPillarBlock.AXIS, Direction.Axis.Y);
        if (!state.hasProperty(RubberLogBlock.SAP_STAGE)) {
            return state;
        }
        if (sapIndex < 0) {
            return state.setValue(RubberLogBlock.SAP_STAGE, SapStage.NONE);
        }
        return state
                .setValue(RubberLogBlock.SAP_FACING, HORIZONTAL_FACES[random.nextInt(HORIZONTAL_FACES.length)])
                .setValue(RubberLogBlock.SAP_STAGE, SapStage.READY);
    }
}
