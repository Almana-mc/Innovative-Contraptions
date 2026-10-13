package me.almana.innovativecontraptions.datagen;

import me.almana.innovativecontraptions.InnovativeContraptions;
import me.almana.innovativecontraptions.content.processing.*;
import me.almana.innovativecontraptions.content.fluid.BiogasContent;
import me.almana.innovativecontraptions.content.fluid.CoolantContent;
import me.almana.innovativecontraptions.registry.ICItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe.CommonInfo;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import java.util.List;
import java.util.Optional;
import net.neoforged.neoforge.fluids.FluidStackTemplate;

public final class ProcessingPartThreeRecipes {
    private final RecipeOutput output;
    private final HolderLookup.Provider registries;
    public ProcessingPartThreeRecipes(HolderLookup.Provider registries, RecipeOutput output) { this.output = output; this.registries = registries; }
    public void build() { extraction(); slicing(); centrifuging(); packaging(); enrichment(); }
    private void enrichment() {
        enrich("biomass", Ingredient.of(ICItems.BIO_COMPOUND.get()), 1, ICItems.BIOMASS_CELL.get(), BiogasContent.BIOMASS.getKey());
        enrich("coolant", Ingredient.of(ICItems.LAPIS_DUST.get()), 8, ICItems.COOLANT_CELL.get(), CoolantContent.COOLANT.getKey());
    }
    private void enrich(String name, Ingredient additive, int count, Item icon, ResourceKey<Fluid> result) {
        //? if >=26.1 {
        var id = ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, "fluid_enrichment/" + name));
        //?} else
        //var id = Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, "fluid_enrichment/" + name);
        output.accept(id, new EnrichmentRecipe(new CommonInfo(true), additive, new ItemStackTemplate(icon), count,
                new FluidStackTemplate(registries.lookupOrThrow(Registries.FLUID).getOrThrow(ResourceKey.create(Registries.FLUID, Identifier.withDefaultNamespace("water"))), 1000),
                new FluidStackTemplate(registries.lookupOrThrow(Registries.FLUID).getOrThrow(result), 1000),
                ProcessingCannerBlockEntity.BASE_TICKS, ProcessingCannerBlockEntity.BASE_EU), null);
    }
    private void extraction() {
        simple(ProcessingKind.EXTRACTING, "reclaim_can", ICItems.FILLED_TIN_CAN.get(), 1, ICItems.TIN_CAN.get(), 1);
        simple(ProcessingKind.EXTRACTING, "brick_recovery", Items.BRICKS, 1, Items.BRICK, 4);
        simple(ProcessingKind.EXTRACTING, "nether_brick_recovery", Items.NETHER_BRICKS, 1, Items.NETHER_BRICK, 4);
        simple(ProcessingKind.EXTRACTING, "clay_recovery", Items.CLAY, 1, Items.CLAY_BALL, 4);
        simple(ProcessingKind.EXTRACTING, "snow_recovery", Items.SNOW_BLOCK, 1, Items.SNOWBALL, 4);
        simple(ProcessingKind.EXTRACTING, "iodine_recovery", ICItems.HYDRATED_TIN_DUST.get(), 1, ICItems.IODINE.get(), 1);
        simple(ProcessingKind.EXTRACTING, "netherrack_sulfur", ICItems.GROUND_NETHERRACK.get(), 1, ICItems.SMALL_SULFUR_DUST.get(), 1);
        for (var dye : DyeColor.values()) {
            if (dye == DyeColor.WHITE) continue;
            simple(ProcessingKind.EXTRACTING, "whiten_" + dye.getName(),
                    BuiltInRegistries.ITEM.getValue(Identifier.withDefaultNamespace(dye.getName() + "_wool")), 1, Items.WHITE_WOOL, 1);
        }
        simple(ProcessingKind.EXTRUDING, "fuel_sleeve", ICItems.IRON_PLATE.get(), 1, ICItems.FUEL_ROD.get(), 1);
        simple(ProcessingKind.EXTRUDING, "iron_fence", ICItems.IRON_CASING.get(), 1, Items.IRON_BARS, 1);
    }
    private void slicing() {
        cut("iron_block", Items.IRON_BLOCK, ICItems.IRON_PLATE.get(), 9, 5);
        cut("gold_block", Items.GOLD_BLOCK, ICItems.GOLD_PLATE.get(), 9, 2);
        cut("copper_block", Items.COPPER_BLOCK, ICItems.COPPER_PLATE.get(), 9, 2);
        cut("tin_block", ICItems.TIN_BLOCK.get(), ICItems.TIN_PLATE.get(), 9, 2);
        cut("bronze_block", ICItems.BRONZE_BLOCK.get(), ICItems.BRONZE_PLATE.get(), 9, 2);
        cut("lead_block", ICItems.LEAD_BLOCK.get(), ICItems.LEAD_PLATE.get(), 9, 2);
        cut("steel_block", ICItems.STEEL_BLOCK.get(), ICItems.STEEL_PLATE.get(), 9, 8);
        cut("lapis_block", Items.LAPIS_BLOCK, ICItems.LAPIS_PLATE.get(), 9, 5);
        cut("obsidian", Items.OBSIDIAN, ICItems.OBSIDIAN_PLATE.get(), 4, 8);
        //? if >=26.1 {
        for (String wood : List.of("oak", "spruce", "birch", "jungle", "acacia", "dark_oak", "mangrove", "cherry", "pale_oak")) {
        //?} else
        //for (String wood : List.of("oak", "spruce", "birch", "jungle", "acacia", "dark_oak", "mangrove", "cherry")) {
            ItemLike planks = BuiltInRegistries.ITEM.getValue(Identifier.withDefaultNamespace(wood + "_planks"));
            for (String suffix : List.of("_log", "_wood")) {
                cut(wood + suffix, BuiltInRegistries.ITEM.getValue(Identifier.withDefaultNamespace(wood + suffix)), planks, 6, 1);
            }
            save(ProcessingKind.SLICING, wood + "_sticks", planks, 2, Items.STICK, 6, List.of(), 0, 1, Optional.empty());
        }
        cut("rubber_log", ICItems.RUBBER_LOG.get(), Items.JUNGLE_PLANKS, 4, 1);
        cut("rubber_wood", ICItems.RUBBER_WOOD.get(), Items.JUNGLE_PLANKS, 4, 1);
    }
    private void centrifuging() {
        ore("iron", ICItems.CRUSHED_IRON.get(), ICItems.PURIFIED_CRUSHED_IRON.get(), ICItems.IRON_DUST.get(), ICItems.SMALL_GOLD_DUST.get(), 1500);
        ore("gold", ICItems.CRUSHED_GOLD.get(), ICItems.PURIFIED_CRUSHED_GOLD.get(), ICItems.GOLD_DUST.get(), ICItems.SMALL_SILVER_DUST.get(), 2000);
        ore("copper", ICItems.CRUSHED_COPPER.get(), ICItems.PURIFIED_CRUSHED_COPPER.get(), ICItems.COPPER_DUST.get(), ICItems.SMALL_TIN_DUST.get(), 500);
        ore("tin", ICItems.CRUSHED_TIN.get(), ICItems.PURIFIED_CRUSHED_TIN.get(), ICItems.TIN_DUST.get(), ICItems.SMALL_IRON_DUST.get(), 1000);
        separate("raw_lead", ICItems.CRUSHED_LEAD.get(), 1, ICItems.LEAD_DUST.get(), 1, 2000, new ItemStackTemplate(ICItems.STONE_DUST.get()));
        separate("purified_lead", ICItems.PURIFIED_CRUSHED_LEAD.get(), 1, ICItems.LEAD_DUST.get(), 1, 2000, new ItemStackTemplate(ICItems.SMALL_COPPER_DUST.get()));
        separate("raw_silver", ICItems.CRUSHED_SILVER.get(), 1, ICItems.SILVER_DUST.get(), 1, 2000, new ItemStackTemplate(ICItems.STONE_DUST.get()));
        separate("purified_silver", ICItems.PURIFIED_CRUSHED_SILVER.get(), 1, ICItems.SILVER_DUST.get(), 1, 1000, new ItemStackTemplate(ICItems.SMALL_LEAD_DUST.get()));
        separate("raw_uranium", ICItems.CRUSHED_URANIUM.get(), 1, ICItems.URANIUM_238.get(), 4, 3000,
                new ItemStackTemplate(ICItems.SMALL_URANIUM_235.get()), new ItemStackTemplate(ICItems.STONE_DUST.get()));
        separate("purified_uranium", ICItems.PURIFIED_CRUSHED_URANIUM.get(), 1, ICItems.URANIUM_238.get(), 6, 3000, new ItemStackTemplate(ICItems.SMALL_URANIUM_235.get()));
        separate("clay_silica", ICItems.CLAY_DUST.get(), 4, ICItems.SILICON_DIOXIDE_DUST.get(), 1, 250);
        separate("stone_dust", Items.COBBLESTONE, 1, ICItems.STONE_DUST.get(), 1, 100);
        separate("quartz_lithium", Items.QUARTZ, 2, ICItems.SMALL_LITHIUM_DUST.get(), 1, 500);
        separate("slag_recovery", ICItems.SLAG.get(), 1, ICItems.COAL_DUST.get(), 5, 1500, new ItemStackTemplate(ICItems.SMALL_GOLD_DUST.get()));
        separate("rtg_recovery", ICItems.RTG_PELLET.get(), 1, ICItems.PLUTONIUM.get(), 3, 5000, new ItemStackTemplate(ICItems.IRON_DUST.get(), 54));
        depleted("uranium", ICItems.DEPLETED_URANIUM.get(), ICItems.URANIUM_238.get(), 1, 4000);
        depleted("dual_uranium", ICItems.DEPLETED_DUAL_URANIUM.get(), ICItems.URANIUM_238.get(), 2, 4000);
        depleted("quad_uranium", ICItems.DEPLETED_QUAD_URANIUM.get(), ICItems.URANIUM_238.get(), 4, 4000);
        depleted("mox", ICItems.DEPLETED_MOX.get(), ICItems.PLUTONIUM.get(), 1, 5000);
        depleted("dual_mox", ICItems.DEPLETED_DUAL_MOX.get(), ICItems.PLUTONIUM.get(), 2, 5000);
        depleted("quad_mox", ICItems.DEPLETED_QUAD_MOX.get(), ICItems.PLUTONIUM.get(), 4, 5000);
        separate("fuel_enrichment", ICItems.URANIUM.get(), 20, ICItems.URANIUM_238.get(), 56, 4000,
                new ItemStackTemplate(ICItems.URANIUM_238.get(), 56), new ItemStackTemplate(ICItems.URANIUM_235.get(), 7));
    }
    private void packaging() {
        save(ProcessingKind.CANNING, "uranium_sleeve", ICItems.URANIUM.get(), 1, ICItems.FUEL_ROD_URANIUM.get(), 1,
                List.of(), 0, 0, Optional.of(Ingredient.of(ICItems.FUEL_ROD.get())));
        save(ProcessingKind.CANNING, "mixed_sleeve", ICItems.MOX.get(), 1, ICItems.FUEL_ROD_MOX.get(), 1,
                List.of(), 0, 0, Optional.of(Ingredient.of(ICItems.FUEL_ROD.get())));
    }
    private void depleted(String name, ItemLike input, ItemLike isotope, int multiplier, int heat) {
        separate(name + "_recovery", input, 1, isotope, (heat == 4000 ? 4 : 3) * multiplier, heat,
                new ItemStackTemplate(ICItems.SMALL_PLUTONIUM.get(), multiplier), new ItemStackTemplate(ICItems.IRON_DUST.get(), 2 * multiplier - 1));
    }
    private void ore(String name, ItemLike raw, ItemLike purified, ItemLike dust, ItemLike secondary, int heat) {
        separate("raw_" + name, raw, 1, dust, 1, heat, new ItemStackTemplate(secondary.asItem(), 1), new ItemStackTemplate(ICItems.STONE_DUST.get()));
        separate("purified_" + name, purified, 1, dust, 1, heat, new ItemStackTemplate(secondary.asItem(), 1));
    }
    private void separate(String name, ItemLike input, int count, ItemLike result, int amount, int heat, ItemStackTemplate... extra) {
        save(ProcessingKind.CENTRIFUGING, name, input, count, result, amount, List.of(extra), heat, 0, Optional.empty());
    }
    private void cut(String name, ItemLike input, ItemLike result, int amount, int hardness) {
        save(ProcessingKind.SLICING, name, input, 1, result, amount, List.of(), 0, hardness, Optional.empty());
    }
    private void simple(ProcessingKind kind, String name, ItemLike input, int count, ItemLike result, int amount) {
        save(kind, name, input, count, result, amount, List.of(), 0, 0, Optional.empty());
    }
    private void save(ProcessingKind kind, String name, ItemLike input, int count, ItemLike result, int amount,
            List<ItemStackTemplate> extra, int heat, int hardness, Optional<Ingredient> container) {
        //? if >=26.1 {
        var id = ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, kind.getSerializedName() + "/" + name));
        //?} else
        //var id = Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, kind.getSerializedName() + "/" + name);
        output.accept(id, new ProcessingRecipe(new CommonInfo(true), kind, Ingredient.of(input),
                new ItemStackTemplate(result.asItem(), amount), count, extra, kind.ticks, kind.euPerTick, 0, heat, hardness, container), null);
    }
}

