package me.almana.innovativecontraptions.datagen;

import me.almana.innovativecontraptions.registry.ICBlocks;
import me.almana.innovativecontraptions.world.ICWorldgenKeys;
import me.almana.innovativecontraptions.world.RubberTrunkPlacer;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class ICDatapackProvider {
    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.CONFIGURED_FEATURE, ICDatapackProvider::configuredFeatures)
            .add(Registries.PLACED_FEATURE, ICDatapackProvider::placedFeatures)
            .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, ICDatapackProvider::biomeModifiers);

    private static void configuredFeatures(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        TreeConfiguration configuration = new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(ICBlocks.RUBBER_LOG.get()),
                new RubberTrunkPlacer(5, 1, 1),
                BlockStateProvider.simple(ICBlocks.RUBBER_LEAVES.get()),
                new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 3),
                new TwoLayersFeatureSize(1, 0, 1)
        ).ignoreVines().build();

        context.register(ICWorldgenKeys.RUBBER_TREE_CONFIGURED, new ConfiguredFeature<>(Feature.TREE, configuration));

        RuleTest stoneReplaceable = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
        context.register(ICWorldgenKeys.TIN_ORE_CONFIGURED, new ConfiguredFeature<>(
                Feature.ORE, new OreConfiguration(stoneReplaceable, ICBlocks.TIN_ORE.get().defaultBlockState(), 8)
        ));
        context.register(ICWorldgenKeys.LEAD_ORE_CONFIGURED, new ConfiguredFeature<>(
                Feature.ORE, new OreConfiguration(stoneReplaceable, ICBlocks.LEAD_ORE.get().defaultBlockState(), 6)
        ));
        context.register(ICWorldgenKeys.URANIUM_ORE_CONFIGURED, new ConfiguredFeature<>(
                Feature.ORE, new OreConfiguration(stoneReplaceable, ICBlocks.URANIUM_ORE.get().defaultBlockState(), 4)
        ));
    }

    private static void placedFeatures(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);
        PlacementUtils.register(
                context,
                ICWorldgenKeys.RUBBER_TREE_PLACED,
                configuredFeatures.getOrThrow(ICWorldgenKeys.RUBBER_TREE_CONFIGURED),
                RarityFilter.onAverageOnceEvery(12),
                InSquarePlacement.spread(),
                PlacementUtils.HEIGHTMAP,
                PlacementUtils.filteredByBlockSurvival(ICBlocks.RUBBER_SAPLING.get()),
                BiomeFilter.biome()
        );

        PlacementUtils.register(
                context,
                ICWorldgenKeys.TIN_ORE_PLACED,
                configuredFeatures.getOrThrow(ICWorldgenKeys.TIN_ORE_CONFIGURED),
                CountPlacement.of(6),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.absolute(-16), VerticalAnchor.absolute(72)),
                BiomeFilter.biome()
        );
        PlacementUtils.register(
                context,
                ICWorldgenKeys.LEAD_ORE_PLACED,
                configuredFeatures.getOrThrow(ICWorldgenKeys.LEAD_ORE_CONFIGURED),
                CountPlacement.of(3),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.absolute(-32), VerticalAnchor.absolute(40)),
                BiomeFilter.biome()
        );
        PlacementUtils.register(
                context,
                ICWorldgenKeys.URANIUM_ORE_PLACED,
                configuredFeatures.getOrThrow(ICWorldgenKeys.URANIUM_ORE_CONFIGURED),
                RarityFilter.onAverageOnceEvery(16),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(8)),
                BiomeFilter.biome()
        );
    }

    private static void biomeModifiers(BootstrapContext<BiomeModifier> context) {
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
        HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        context.register(
                ICWorldgenKeys.ADD_RUBBER_TREE,
                new BiomeModifiers.AddFeaturesBiomeModifier(
                        biomes.getOrThrow(BiomeTags.IS_FOREST),
                        HolderSet.direct(placedFeatures.getOrThrow(ICWorldgenKeys.RUBBER_TREE_PLACED)),
                        GenerationStep.Decoration.VEGETAL_DECORATION
                )
        );

        context.register(
                ICWorldgenKeys.ADD_TIN_ORE,
                new BiomeModifiers.AddFeaturesBiomeModifier(
                        biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                        HolderSet.direct(placedFeatures.getOrThrow(ICWorldgenKeys.TIN_ORE_PLACED)),
                        GenerationStep.Decoration.UNDERGROUND_ORES
                )
        );
        context.register(
                ICWorldgenKeys.ADD_LEAD_ORE,
                new BiomeModifiers.AddFeaturesBiomeModifier(
                        biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                        HolderSet.direct(placedFeatures.getOrThrow(ICWorldgenKeys.LEAD_ORE_PLACED)),
                        GenerationStep.Decoration.UNDERGROUND_ORES
                )
        );
        context.register(
                ICWorldgenKeys.ADD_URANIUM_ORE,
                new BiomeModifiers.AddFeaturesBiomeModifier(
                        biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                        HolderSet.direct(placedFeatures.getOrThrow(ICWorldgenKeys.URANIUM_ORE_PLACED)),
                        GenerationStep.Decoration.UNDERGROUND_ORES
                )
        );
    }

    private ICDatapackProvider() {
    }
}
