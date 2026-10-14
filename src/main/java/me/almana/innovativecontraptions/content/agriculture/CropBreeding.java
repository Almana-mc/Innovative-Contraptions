package me.almana.innovativecontraptions.content.agriculture;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;

public final class CropBreeding {
    public static boolean attempt(CropPlotBlockEntity destination) {
        var random = destination.getLevel().getRandom();
        if (random.nextInt(3) == 0) {
            List<CropPlotBlockEntity> parents = neighbors(destination).stream().filter(crop -> eligible(crop, destination, random)).toList();
            if (parents.size() >= 2 && cross(destination, parents, random)) return true;
        }
        var neighbors = neighbors(destination);
        if (neighbors.size() == 1 && eligible(neighbors.getFirst(), destination, random)) {
            destination.breed(neighbors.getFirst().genome()); return true;
        }
        return false;
    }
    private static List<CropPlotBlockEntity> neighbors(CropPlotBlockEntity destination) {
        List<CropPlotBlockEntity> result = new ArrayList<>();
        for (Direction side : Direction.Plane.HORIZONTAL) {
            var pos = destination.getBlockPos().relative(side);
            if (destination.getLevel().hasChunkAt(pos) && destination.getLevel().getBlockEntity(pos) instanceof CropPlotBlockEntity crop) result.add(crop);
        }
        return result;
    }
    private static boolean eligible(CropPlotBlockEntity parent, CropPlotBlockEntity destination, RandomSource random) {
        int ready = parent.species().generic() ? parent.species().stages - 1 : 3;
        if (parent.empty() || parent.age() < ready || !CropEnvironment.canGrow(destination, parent.species())) return false;
        var genes = parent.genome();
        int chance = 4 + (genes.growth() >= 16 ? 1 : 0) + (genes.growth() >= 30 ? 1 : 0) - Math.max(0, genes.resistance() - 27);
        return random.nextInt(16) <= chance;
    }
    private static boolean cross(CropPlotBlockEntity destination, List<CropPlotBlockEntity> parents, RandomSource random) {
        int[] weights = new int[CropSpecies.values().length];
        int total = 0;
        for (CropSpecies candidate : CropSpecies.values()) {
            if (candidate == CropSpecies.EMPTY || !CropEnvironment.canGrow(destination, candidate)) continue;
            for (var parent : parents) weights[candidate.ordinal()] += affinity(parent.species(), candidate);
            total += weights[candidate.ordinal()];
        }
        if (total <= 0) return false;
        int choice = random.nextInt(total);
        for (CropSpecies candidate : CropSpecies.values()) {
            choice -= weights[candidate.ordinal()];
            if (choice >= 0) continue;
            destination.breed(new CropGenome(candidate.id(), gene(parents, 0, random), gene(parents, 1, random), gene(parents, 2, random), 0));
            return true;
        }
        return false;
    }
    public static int affinity(CropSpecies parent, CropSpecies candidate) {
        if (parent == candidate) return 500;
        int weight = 0;
        for (int index = 0; index < 5; index++) weight += 2 - Math.abs(parent.traits[index] - candidate.traits[index]);
        for (String trait : parent.attributes) if (candidate.attributes.contains(trait)) weight += 5;
        int gap = candidate.tier - parent.tier;
        if (gap > 1) weight -= 2 * gap;
        else if (gap < -3) weight += gap;
        return Math.max(0, weight);
    }
    private static int gene(List<CropPlotBlockEntity> parents, int index, RandomSource random) {
        int total = 0;
        for (var parent : parents) total += switch (index) { case 0 -> parent.genome().growth(); case 1 -> parent.genome().gain(); default -> parent.genome().resistance(); };
        return Math.clamp(total / parents.size() + random.nextInt(parents.size() * 2 + 1) - parents.size(), 0, 31);
    }
    public static void spreadWeed(CropPlotBlockEntity source) {
        var level = source.getLevel();
        var pos = source.getBlockPos().relative(Direction.Plane.HORIZONTAL.getRandomDirection(level.getRandom()));
        if (!level.hasChunkAt(pos)) return;
        if (level.getBlockEntity(pos) instanceof CropPlotBlockEntity crop) {
            if (crop.species() == CropSpecies.WEED || crop.resistWeed()) return;
            int growth = Math.max(source.genome().growth(), crop.genome().growth());
            if (growth < 31 && level.getRandom().nextBoolean()) growth++;
            crop.clearPlant(); crop.breed(new CropGenome("weed", growth, 0, 0, 0));
        } else if (level.isEmptyBlock(pos)) {
            var soil = level.getBlockState(pos.below());
            if (soil.is(Blocks.DIRT) || soil.is(Blocks.GRASS_BLOCK) || soil.is(Blocks.FARMLAND)) {
                level.setBlockAndUpdate(pos.below(), Blocks.GRASS_BLOCK.defaultBlockState());
                level.setBlockAndUpdate(pos, Blocks.SHORT_GRASS.defaultBlockState());
            }
        }
    }
    private CropBreeding() {}
}
