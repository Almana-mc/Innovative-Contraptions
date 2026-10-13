package me.almana.innovativecontraptions.world;

import me.almana.innovativecontraptions.InnovativeContraptions;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class ICWorldgenKeys {
    public static final ResourceKey<ConfiguredFeature<?, ?>> RUBBER_TREE_CONFIGURED = ResourceKey.create(
            Registries.CONFIGURED_FEATURE,
            id("rubber_tree")
    );
    public static final ResourceKey<PlacedFeature> RUBBER_TREE_PLACED = ResourceKey.create(
            Registries.PLACED_FEATURE,
            id("rubber_tree")
    );
    public static final ResourceKey<BiomeModifier> ADD_RUBBER_TREE = ResourceKey.create(
            NeoForgeRegistries.Keys.BIOME_MODIFIERS,
            id("add_rubber_tree")
    );

    public static final ResourceKey<ConfiguredFeature<?, ?>> TIN_ORE_CONFIGURED = ResourceKey.create(
            Registries.CONFIGURED_FEATURE, id("tin_ore")
    );
    public static final ResourceKey<PlacedFeature> TIN_ORE_PLACED = ResourceKey.create(
            Registries.PLACED_FEATURE, id("tin_ore")
    );
    public static final ResourceKey<BiomeModifier> ADD_TIN_ORE = ResourceKey.create(
            NeoForgeRegistries.Keys.BIOME_MODIFIERS, id("add_tin_ore")
    );

    public static final ResourceKey<ConfiguredFeature<?, ?>> LEAD_ORE_CONFIGURED = ResourceKey.create(
            Registries.CONFIGURED_FEATURE, id("lead_ore")
    );
    public static final ResourceKey<PlacedFeature> LEAD_ORE_PLACED = ResourceKey.create(
            Registries.PLACED_FEATURE, id("lead_ore")
    );
    public static final ResourceKey<BiomeModifier> ADD_LEAD_ORE = ResourceKey.create(
            NeoForgeRegistries.Keys.BIOME_MODIFIERS, id("add_lead_ore")
    );

    public static final ResourceKey<ConfiguredFeature<?, ?>> URANIUM_ORE_CONFIGURED = ResourceKey.create(
            Registries.CONFIGURED_FEATURE, id("uranium_ore")
    );
    public static final ResourceKey<PlacedFeature> URANIUM_ORE_PLACED = ResourceKey.create(
            Registries.PLACED_FEATURE, id("uranium_ore")
    );
    public static final ResourceKey<BiomeModifier> ADD_URANIUM_ORE = ResourceKey.create(
            NeoForgeRegistries.Keys.BIOME_MODIFIERS, id("add_uranium_ore")
    );

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, path);
    }

    private ICWorldgenKeys() {
    }
}
