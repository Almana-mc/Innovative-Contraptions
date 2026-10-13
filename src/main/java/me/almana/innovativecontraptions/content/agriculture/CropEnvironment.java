package me.almana.innovativecontraptions.content.agriculture;

import java.util.function.Predicate;
import me.almana.innovativecontraptions.registry.ICBlocks;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmlandBlock;
import net.minecraft.world.level.block.state.BlockState;


public final class CropEnvironment {
    public static int duration(CropPlotBlockEntity plot) {
        if (plot.species() != CropSpecies.PREDATOR_VINE) return plot.species().threshold(plot.age());
        var biome = plot.getLevel().getBiome(plot.getBlockPos());
        float multiplier = 1;
        if (biome.is(BiomeTags.IS_MOUNTAIN) || biome.unwrapKey().orElseThrow().identifier().getPath().contains("swamp")) multiplier /= 1.5F;
        multiplier /= 1 + plot.airQuality / 10.0F;
        return (int) (plot.species().points * multiplier);
    }
    public static boolean canGrow(CropPlotBlockEntity plot, CropSpecies crop) {
        if (crop == CropSpecies.EMPTY || plot.age() >= crop.stages) return false;
        int light = plot.getLevel().getRawBrightness(plot.getBlockPos(), 0);
        if (crop.generic() || crop == CropSpecies.WEED || crop == CropSpecies.REED || crop == CropSpecies.STICKREED
                || crop == CropSpecies.NETHER_WART || crop == CropSpecies.TERRA_WART) return true;
        if (crop.flower()) return light >= 12;
        if (crop == CropSpecies.VENOM_BLOOM) return plot.age() == 5 || light >= 12;
        if (crop == CropSpecies.COCOA) return plot.nutrients() >= 3;
        if (crop == CropSpecies.RED_MUSHROOM || crop == CropSpecies.BROWN_MUSHROOM) return plot.water() > 0;
        if (crop == CropSpecies.RED_GRAIN) return light >= 5 && light <= 10;
        if (crop == CropSpecies.PREDATOR_VINE) return light > 10 && (plot.age() < 3 || root(plot, Blocks.LAVA));
        if (crop.commonMetal()) return plot.age() < 3 || metalRoot(plot, crop);
        if (crop == CropSpecies.GOLD_LEAF || crop == CropSpecies.SILVER_LEAF) return plot.age() < 4 || metalRoot(plot, crop);
        return light >= 9;
    }
    public static int growth(CropPlotBlockEntity plot) {
        var random = plot.getLevel().getRandom();
        var genome = plot.genome();
        int required = Math.max(0, (plot.species().tier - 1) * 4 + genome.growth() + genome.gain() + genome.resistance());
        int available = 5 * weighted(plot);
        int base = 3 + random.nextInt(7) + genome.growth();
        if (available >= required) return base * (100 + available - required) / 100;
        int shortage = required - available;
        if (shortage > 25 && random.nextInt(32) > genome.resistance()) return -1;
        return Math.max(0, (int) (base * (1 - shortage * .04)));
    }
    public static int weighted(CropPlotBlockEntity plot) {
        double humidity = plot.humidityQuality, nutrients = plot.nutrientQuality, air = plot.airQuality;
        return (int) switch (plot.species()) {
            case COFFEE -> humidity * .4 + nutrients * 1.4 + air * 1.2;
            case REED, STICKREED -> humidity * 1.2 + nutrients + air * .8;
            case PUMPKIN, MELON -> humidity * 1.1 + nutrients * .9 + air;
            case COCOA -> humidity * .8 + nutrients * 1.3 + air * .9;
            default -> humidity + nutrients + air;
        };
    }
    public static int humidity(CropPlotBlockEntity plot) {
        //? if >=26.1 {
        int moisture = plot.getLevel().getBlockState(plot.getBlockPos().below()).getValueOrElse(FarmlandBlock.MOISTURE, 0);
        //?} else
        //int moisture = plot.getLevel().getBlockState(plot.getBlockPos().below()).getOptionalValue(FarmlandBlock.MOISTURE).orElse(0);
        return plot.climateHumidity + (moisture >= 7 ? 2 : 0) + (plot.water() >= 5 ? 2 : 0) + (plot.water() + 24) / 25;
    }
    private static int climate(CropPlotBlockEntity plot) {
        var level = plot.getLevel(); var pos = plot.getBlockPos(); var biome = level.getBiome(pos).value();
        int rainfall = Math.clamp((int) (25 * biome.getModifiedClimateSettings().downfall() - 12.5), -10, 10);
        //? if >=26.1 {
        double temperature = biome.getTemperature(pos, level.getSeaLevel());
        //?} else
        //double temperature = biome.getTemperature(pos);
        int climate = rainfall + Math.clamp((int) (Math.abs(rainfall) * (-2 * temperature * temperature + 4 * temperature - 1)), -10, 10);
        return climate;
    }
    public static void refresh(CropPlotBlockEntity plot, int update) {
        if (update % 40 == 0) plot.climateHumidity = climate(plot);
        if (update == 0 || update % 4 == 0) plot.humidityQuality = humidity(plot);
        if (update == 0 || update % 4 == 1) plot.nutrientQuality = nutrients(plot);
        if (update == 0 || update % 4 == 2) plot.airQuality = air(plot);
    }
    public static int nutrients(CropPlotBlockEntity plot) {
        var biome = plot.getLevel().getBiome(plot.getBlockPos());
        String id = biome.unwrapKey().orElseThrow().identifier().getPath();
        int bonus = biome.is(BiomeTags.IS_JUNGLE) || id.contains("swamp") ? 10
                : biome.is(BiomeTags.IS_FOREST) || id.equals("mushroom_fields") ? 5 : biome.is(BiomeTags.IS_RIVER) ? 2 : 0;
        for (int depth = 1; depth <= 4; depth++) {
            if (!plot.getLevel().getBlockState(plot.getBlockPos().below(depth)).is(Blocks.DIRT)) break;
            bonus++;
        }
        return bonus + (plot.nutrients() + 19) / 20;
    }
    public static int air(CropPlotBlockEntity plot) {
        var level = plot.getLevel(); var pos = plot.getBlockPos();
        int result = Math.clamp(Math.floorDiv(pos.getY() - 40, 15), 0, 2), freshness = 9;
        for (int x = -1; x <= 0; x++) for (int z = -1; z <= 0; z++) {
            var near = pos.offset(x, 0, z); var state = level.getBlockState(near);
            //? if >=26.1 {
            if (state.isSolidRender() || state.getBlock() instanceof CropPlotBlock) freshness--;
            //?} else
            //if (state.isSolidRender(level, near) || state.getBlock() instanceof CropPlotBlock) freshness--;
        }
        return result + freshness / 2 + (level.canSeeSky(pos.above()) ? 4 : 0);
    }
    public static boolean root(CropPlotBlockEntity plot, Block block) {
        return rootMatches(plot, state -> state.is(block));
    }
    private static boolean metalRoot(CropPlotBlockEntity plot, CropSpecies crop) {
        return rootMatches(plot, state -> switch (crop) {
            case FERRU -> state.is(BlockTags.IRON_ORES) || state.is(Blocks.IRON_BLOCK);
            case GOLD_LEAF -> state.is(BlockTags.GOLD_ORES) || state.is(Blocks.GOLD_BLOCK);
            case COPPER_LEAF -> state.is(BlockTags.COPPER_ORES) || state.is(Blocks.COPPER_BLOCK);
            case TIN_LEAF -> state.is(ICBlocks.TIN_ORE.get()) || state.is(ICBlocks.TIN_BLOCK.get());
            case LEAD_LEAF -> state.is(ICBlocks.LEAD_ORE.get()) || state.is(ICBlocks.LEAD_BLOCK.get());
            case SILVER_LEAF -> state.is(ICBlocks.SILVER_ORE.get()) || state.is(ICBlocks.SILVER_BLOCK.get());
            default -> false;
        });
    }
    private static boolean rootMatches(CropPlotBlockEntity plot, Predicate<BlockState> predicate) {
        for (int depth = 1; depth <= 4; depth++) {
            var state = plot.getLevel().getBlockState(plot.getBlockPos().below(depth));
            if (state.isAir()) return false;
            if (predicate.test(state)) return true;
        }
        return false;
    }
    private CropEnvironment() {}
}

