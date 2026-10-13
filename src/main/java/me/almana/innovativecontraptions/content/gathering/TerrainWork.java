package me.almana.innovativecontraptions.content.gathering;

import java.util.List;
import me.almana.innovativecontraptions.registry.ICBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.commands.FillBiomeCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.FarmlandBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;

public final class TerrainWork {
    private static final List<Block> PLANTS = List.of(Blocks.SHORT_GRASS, Blocks.SHORT_GRASS, Blocks.FERN, Blocks.POPPY, Blocks.DANDELION, Blocks.TALL_GRASS, Blocks.ROSE_BUSH, Blocks.SUNFLOWER,
            Blocks.OAK_SAPLING, Blocks.BIRCH_SAPLING, Blocks.SPRUCE_SAPLING, Blocks.JUNGLE_SAPLING, Blocks.ACACIA_SAPLING,
            Blocks.DARK_OAK_SAPLING, Blocks.WHEAT, Blocks.RED_MUSHROOM, Blocks.BROWN_MUSHROOM, Blocks.PUMPKIN, Blocks.MELON);
    public static void tick(GatheringBlockEntity machine) {
        if (!(machine.getItem(0).getItem() instanceof TerrainProgramItem item) || item.program == TerrainProgram.BLANK) { machine.status = 4; return; }
        if (machine.energy < item.program.cost) { machine.status = 1; return; }
        var random = machine.getLevel().getRandom();
        int range = machine.lastTerrain == null ? item.program.range * (Math.min(4, machine.failures) + 1) / 5 : item.program.range / 10;
        BlockPos center = machine.lastTerrain == null ? machine.getBlockPos() : machine.lastTerrain;
        BlockPos target = new BlockPos(center.getX() + random.nextInt(range + 1) - random.nextInt(range + 1), machine.getBlockPos().getY(),
                center.getZ() + random.nextInt(range + 1) - random.nextInt(range + 1));
        if (!machine.getLevel().hasChunkAt(target)) { machine.status = 6; return; }
        boolean changed = apply((ServerLevel) machine.getLevel(), target, item.program);
        machine.energy -= changed ? item.program.cost : item.program.cost / 10;
        machine.lastTerrain = changed ? target : null; machine.failures = changed ? 0 : Math.min(4, machine.failures + 1); machine.status = changed ? 0 : 3;
    }
    public static boolean apply(ServerLevel level, BlockPos origin, TerrainProgram program) {
        BlockPos surface = surface(level, origin, program == TerrainProgram.LEVELING || program == TerrainProgram.FUNGI ? 20 : 10,
                program == TerrainProgram.PLANTING || program == TerrainProgram.FUNGI);
        if (surface == null || level.getBlockState(surface).hasBlockEntity()) return false;
        return switch (program) {
            case FROST -> frost(level, surface);
            case PLANTING -> planting(level, surface);
            case DRYING -> drying(level, surface);
            case LEVELING -> leveling(level, surface, origin.getY());
            case WATERING -> watering(level, surface);
            case FUNGI -> fungi(level, surface);
            default -> false;
        };
    }
    private static BlockPos surface(ServerLevel level, BlockPos origin, int above, boolean solid) {
        //? if >=26.1 {
        for (int y = Math.min(level.getMaxY(), origin.getY() + above); y >= level.getMinY(); y--) {
        //?} else
        //for (int y = Math.min(level.getMaxBuildHeight() - 1, origin.getY() + above); y >= level.getMinY(); y--) {
            BlockPos pos = new BlockPos(origin.getX(), y, origin.getZ()); var state = level.getBlockState(pos);
            if (!state.isAir() && (!solid || state.isSolid())) return pos;
        }
        return null;
    }
    private static boolean change(ServerLevel level, BlockPos pos, BlockState state) {
        if (level.isOutsideBuildHeight(pos) || level.getBlockState(pos).equals(state) || level.getBlockState(pos).hasBlockEntity()) return false;
        return level.setBlockAndUpdate(pos, state);
    }
    private static boolean frost(ServerLevel level, BlockPos pos) {
        var state = level.getBlockState(pos);
        if (state.is(Blocks.WATER)) return change(level, pos, Blocks.ICE.defaultBlockState());
        if (state.is(Blocks.ICE) && level.getBlockState(pos.below()).is(Blocks.WATER)) return change(level, pos.below(), Blocks.ICE.defaultBlockState());
        if (state.is(Blocks.SNOW)) {
            int layers = state.getValue(SnowLayerBlock.LAYERS);
            if (surroundingSnow(level, pos)) return change(level, pos, Blocks.SNOW_BLOCK.defaultBlockState());
            if (layers < 8) return change(level, pos, state.setValue(SnowLayerBlock.LAYERS, layers + 1));
        }
        var snow = Blocks.SNOW.defaultBlockState();
        return level.isEmptyBlock(pos.above()) && (snow.canSurvive(level, pos.above()) || state.is(Blocks.ICE)) && change(level, pos.above(), snow);
    }
    private static boolean surroundingSnow(ServerLevel level, BlockPos pos) {
        for (Direction side : Direction.Plane.HORIZONTAL) {
            BlockPos surface = surface(level, pos.relative(side), 16, false);
            if (surface == null || surface.getY() < pos.getY()) return false;
            var state = level.getBlockState(surface);
            if (state.is(Blocks.SNOW) || state.is(Blocks.SNOW_BLOCK)) continue;
            var snow = Blocks.SNOW.defaultBlockState();
            if (level.isEmptyBlock(surface.above()) && (snow.canSurvive(level, surface.above()) || state.is(Blocks.ICE))) change(level, surface.above(), snow);
            return false;
        }
        return true;
    }
    private static boolean planting(ServerLevel level, BlockPos pos) {
        var state = level.getBlockState(pos);
        if (state.is(BlockTags.SAND)) return restoreSoil(level, pos, 1);
        if (state.is(Blocks.END_STONE)) return restoreSoil(level, pos, 4);
        if (state.is(Blocks.DIRT)) return change(level, pos, Blocks.GRASS_BLOCK.defaultBlockState());
        if (!state.is(Blocks.GRASS_BLOCK) || !level.isEmptyBlock(pos.above()) && !(level.getBlockState(pos.above()).is(Blocks.SHORT_GRASS) && level.getRandom().nextInt(4) == 0)) return false;
        int selection = level.getRandom().nextInt(PLANTS.size() + 1);
        Block plant = selection == PLANTS.size() ? ICBlocks.RUBBER_SAPLING.get() : PLANTS.get(selection);
        if (plant instanceof CropBlock) change(level, pos, Blocks.FARMLAND.defaultBlockState());
        if (plant instanceof DoublePlantBlock && level.isEmptyBlock(pos.above(2))) {
            DoublePlantBlock.placeAt(level, plant.defaultBlockState(), pos.above(), 3); return true;
        }
        return change(level, pos.above(), plant.defaultBlockState());
    }
    private static boolean drying(ServerLevel level, BlockPos pos) {
        for (int y = pos.getY(); y >= level.getMinY(); y--) {
            var below = new BlockPos(pos.getX(), y, pos.getZ()); var ground = level.getBlockState(below);
            if (ground.is(Blocks.DIRT) || ground.is(Blocks.GRASS_BLOCK) || ground.is(Blocks.FARMLAND)) {
                change(level, below, Blocks.SAND.defaultBlockState());
                var next = level.getBlockState(below.below());
                if (next.is(Blocks.DIRT)) change(level, below.below(), Blocks.SAND.defaultBlockState());
                return true;
            }
            if (ground.hasBlockEntity()) return false;
        }
        var state = level.getBlockState(pos);
        if (state.is(Blocks.ICE) || state.is(Blocks.SNOW_BLOCK)) return change(level, pos, Blocks.WATER.defaultBlockState());
        if (state.is(Blocks.WATER) || state.is(Blocks.SNOW) || state.is(BlockTags.LEAVES) || state.is(BlockTags.FLOWERS)
                || state.is(BlockTags.SAPLINGS) || state.is(BlockTags.CROPS) || PLANTS.contains(state.getBlock())) {
            if (PLANTS.contains(level.getBlockState(pos.above()).getBlock())) change(level, pos.above(), Blocks.AIR.defaultBlockState());
            return change(level, pos, Blocks.AIR.defaultBlockState());
        }
        return (state.is(BlockTags.LOGS) || state.is(BlockTags.PLANKS)) && level.getRandom().nextInt(15) == 0 && change(level, pos, Blocks.FIRE.defaultBlockState());
    }
    private static boolean leveling(ServerLevel level, BlockPos pos, int height) {
        if (level.getBlockState(pos).is(Blocks.SNOW)) pos = pos.below();
        if (pos.getY() < height) return change(level, pos.above(), Blocks.DIRT.defaultBlockState());
        if (pos.getY() == height) return false;
        var state = level.getBlockState(pos);
        boolean removable = state.is(BlockTags.DIRT) || state.is(BlockTags.BASE_STONE_OVERWORLD) || state.is(BlockTags.SAND)
                || state.is(BlockTags.LOGS) || state.is(BlockTags.LEAVES) || state.is(BlockTags.FLOWERS) || state.is(BlockTags.SAPLINGS)
                || state.is(BlockTags.CROPS) || state.is(Blocks.GRAVEL) || state.is(Blocks.ICE) || state.is(Blocks.SNOW_BLOCK)
                || state.is(Blocks.SHORT_GRASS) || state.is(Blocks.FERN) || state.is(Blocks.RED_MUSHROOM) || state.is(Blocks.BROWN_MUSHROOM)
                || state.is(Blocks.PUMPKIN) || state.is(Blocks.MELON);
        return removable && change(level, pos, Blocks.AIR.defaultBlockState());
    }
    private static boolean watering(ServerLevel level, BlockPos pos) {
        //? if >=26.1 {
        if (level.getRandom().nextInt(48000) == 0) { level.getServer().setWeatherParameters(0, 12000, true, false); return true; }
        //?} else
        //if (level.getRandom().nextInt(48000) == 0) { level.getServer().overworld().setWeatherParameters(0, 12000, true, false); return true; }
        var state = level.getBlockState(pos);
        if (state.is(BlockTags.SAND)) return restoreSoil(level, pos, 2);
        if (state.is(Blocks.FIRE)) return change(level, pos, Blocks.AIR.defaultBlockState());
        if (state.getBlock() instanceof BonemealableBlock plant && plant.isValidBonemealTarget(level, pos, state)) {
            plant.performBonemeal(level, level.getRandom(), pos, state); return true;
        }
        if (state.is(Blocks.FARMLAND)) return change(level, pos, state.setValue(FarmlandBlock.MOISTURE, 7));
        if (state.is(BlockTags.LOGS) && level.isEmptyBlock(pos.above())) return extendTree(level, pos, state);
        if (state.is(Blocks.SHORT_GRASS)) {
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                var neighbor = surface(level, pos.relative(direction), 0, false);
                if (neighbor == null) continue;
                if (level.getBlockState(neighbor).is(Blocks.DIRT)) return change(level, neighbor, Blocks.GRASS_BLOCK.defaultBlockState());
                if (level.getBlockState(neighbor).is(Blocks.GRASS_BLOCK)) return change(level, neighbor.above(), Blocks.SHORT_GRASS.defaultBlockState());
            }
        }
        return false;
    }
    private static boolean restoreSoil(ServerLevel level, BlockPos pos, int amount) {
        Block ground = level.getBlockState(pos).getBlock();
        BlockPos bottom = pos;
        while (bottom.getY() > level.getMinY() && level.getBlockState(bottom.below()).is(ground)) bottom = bottom.below();
        boolean changed = false;
        for (int i = 0; i < amount && bottom.getY() + i <= pos.getY(); i++) changed |= change(level, bottom.above(i), Blocks.DIRT.defaultBlockState());
        return changed;
    }
    private static boolean extendTree(ServerLevel level, BlockPos pos, BlockState wood) {
        change(level, pos.above(), wood);
        for (Direction side : Direction.Plane.HORIZONTAL) {
            var leaves = level.getBlockState(pos.relative(side));
            if (!leaves.is(BlockTags.LEAVES)) continue;
            if (level.isEmptyBlock(pos.above(2))) change(level, pos.above(2), leaves);
            for (Direction direction : Direction.Plane.HORIZONTAL) if (level.isEmptyBlock(pos.above().relative(direction))) change(level, pos.above().relative(direction), leaves);
            break;
        }
        return true;
    }
    private static boolean fungi(ServerLevel level, BlockPos pos) {
        var state = level.getBlockState(pos);
        if (state.is(Blocks.DIRT) || state.is(Blocks.GRASS_BLOCK)) {
            change(level, pos, Blocks.MYCELIUM.defaultBlockState());
            var biome = level.registryAccess().lookupOrThrow(Registries.BIOME).getOrThrow(Biomes.MUSHROOM_FIELDS);
            //? if >=26.1 {
            FillBiomeCommand.fill(level, new BlockPos(pos.getX(), level.getMinY(), pos.getZ()), new BlockPos(pos.getX(), level.getMaxY(), pos.getZ()), biome);
            //?} else
            //FillBiomeCommand.fill(level, new BlockPos(pos.getX(), level.getMinY(), pos.getZ()), new BlockPos(pos.getX(), level.getMaxBuildHeight() - 1, pos.getZ()), biome);
            return true;
        }
        if (!state.is(Blocks.MYCELIUM)) return false;
        var above = level.getBlockState(pos.above());
        if (above.isAir()) return change(level, pos.above(), (level.getRandom().nextBoolean() ? Blocks.RED_MUSHROOM : Blocks.BROWN_MUSHROOM).defaultBlockState());
        if (above.getBlock() instanceof BonemealableBlock mushroom && mushroom.isValidBonemealTarget(level, pos.above(), above)) {
            mushroom.performBonemeal(level, level.getRandom(), pos.above(), above); return true;
        }
        return false;
    }
    private TerrainWork() {}
}

