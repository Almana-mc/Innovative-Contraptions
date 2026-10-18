package me.almana.innovativecontraptions.datagen;

import java.util.List;
import me.almana.innovativecontraptions.InnovativeContraptions;
import me.almana.innovativecontraptions.content.processing.ProcessingKind;
import me.almana.innovativecontraptions.content.processing.ProcessingRecipe;
import me.almana.innovativecontraptions.content.processing.RecyclingRules;
import me.almana.innovativecontraptions.registry.ICItems;
import me.almana.innovativecontraptions.registry.ICTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe.CommonInfo;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;

public final class ICProcessingRecipeProvider {
    private final RecipeOutput output;
    private final HolderLookup.Provider registries;
    public ICProcessingRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) { this.output = output; this.registries = registries; }
    public void build() {
        new ProcessingPartThreeRecipes(registries, output).build();
        //? if >=26.1 {
        output.accept(ResourceKey.create(Registries.RECIPE,
                Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, "recycling/mixed_materials")),
        //?} else
        //output.accept(Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, "recycling/mixed_materials"),
                new ProcessingRecipe(new CommonInfo(true), ProcessingKind.RECYCLING,
                        RecyclingRules.inputs(),
                        new ItemStackTemplate(ICItems.SCRAP.get()), 1, List.of(), ProcessingKind.RECYCLING.ticks,
                        ProcessingKind.RECYCLING.euPerTick, 0), null);
        metal("iron", Tags.Items.INGOTS_IRON, ICTags.PLATES_IRON, ICItems.IRON_PLATE.get(), ICItems.IRON_CASING.get(), ICItems.DENSE_IRON_PLATE.get());
        metal("gold", Tags.Items.INGOTS_GOLD, ICTags.PLATES_GOLD, ICItems.GOLD_PLATE.get(), ICItems.GOLD_CASING.get(), ICItems.DENSE_GOLD_PLATE.get());
        metal("copper", Tags.Items.INGOTS_COPPER, ICTags.PLATES_COPPER, ICItems.COPPER_PLATE.get(), ICItems.COPPER_CASING.get(), ICItems.DENSE_COPPER_PLATE.get());
        metal("tin", ICTags.INGOTS_TIN, ICTags.PLATES_TIN, ICItems.TIN_PLATE.get(), ICItems.TIN_CASING.get(), ICItems.DENSE_TIN_PLATE.get());
        metal("lead", ICTags.INGOTS_LEAD, ICTags.PLATES_LEAD, ICItems.LEAD_PLATE.get(), ICItems.LEAD_CASING.get(), ICItems.DENSE_LEAD_PLATE.get());
        metal("bronze", ICTags.INGOTS_BRONZE, ICTags.PLATES_BRONZE, ICItems.BRONZE_PLATE.get(), ICItems.BRONZE_CASING.get(), ICItems.DENSE_BRONZE_PLATE.get());
        metal("steel", ICTags.INGOTS_STEEL, ICTags.PLATES_STEEL, ICItems.STEEL_PLATE.get(), ICItems.STEEL_CASING.get(), ICItems.DENSE_STEEL_PLATE.get());
        compression();
        extraction();
        cables("copper", Tags.Items.INGOTS_COPPER, ICTags.PLATES_COPPER, ICItems.COPPER_CABLE.get(), 2, 3);
        cables("gold", Tags.Items.INGOTS_GOLD, ICTags.PLATES_GOLD, ICItems.GOLD_CABLE.get(), 4, 4);
        cables("tin", ICTags.INGOTS_TIN, ICTags.PLATES_TIN, ICItems.TIN_CABLE.get(), 3, 3);
        cables("iron", Tags.Items.INGOTS_IRON, ICTags.PLATES_IRON, ICItems.HV_CABLE.get(), 4, 4);
        add(ProcessingKind.EXTRUDING, "iron_shaft", tag(Tags.Items.STORAGE_BLOCKS_IRON), 1, ICItems.IRON_SHAFT.get(), 1);
        add(ProcessingKind.EXTRUDING, "steel_shaft", tag(ICTags.STORAGE_BLOCKS_STEEL), 1, ICItems.STEEL_SHAFT.get(), 1);
        wash("iron", ICTags.CRUSHED_ORES_IRON, ICItems.PURIFIED_CRUSHED_IRON.get(), ICItems.SMALL_IRON_DUST.get());
        wash("gold", ICTags.CRUSHED_ORES_GOLD, ICItems.PURIFIED_CRUSHED_GOLD.get(), ICItems.SMALL_GOLD_DUST.get());
        wash("copper", ICTags.CRUSHED_ORES_COPPER, ICItems.PURIFIED_CRUSHED_COPPER.get(), ICItems.SMALL_COPPER_DUST.get());
        wash("tin", ICTags.CRUSHED_ORES_TIN, ICItems.PURIFIED_CRUSHED_TIN.get(), ICItems.SMALL_TIN_DUST.get());
        wash("silver", ICTags.CRUSHED_ORES_SILVER, ICItems.PURIFIED_CRUSHED_SILVER.get(), ICItems.SMALL_SILVER_DUST.get());
        wash("uranium", ICTags.CRUSHED_ORES_URANIUM, ICItems.PURIFIED_CRUSHED_URANIUM.get(), ICItems.SMALL_LEAD_DUST.get());
        save(ProcessingKind.WASHING, "lead", tag(ICTags.CRUSHED_ORES_LEAD), 1, ICItems.PURIFIED_CRUSHED_LEAD.get(), 1,
                List.of(new ItemStackTemplate(ICItems.SMALL_SULFUR_DUST.get(), 3), new ItemStackTemplate(ICItems.STONE_DUST.get(), 1)), 1000);
    }
    private void metal(String name, TagKey<Item> ingot, TagKey<Item> plate, ItemLike plateItem, ItemLike casing, ItemLike dense) {
        add(ProcessingKind.ROLLING, name + "_plate", tag(ingot), 1, plateItem, 1);
        add(ProcessingKind.ROLLING, name + "_casing", tag(plate), 1, casing, 2);
        add(ProcessingKind.COMPRESSING, "dense_" + name + "_plate", tag(plate), 9, dense, 1);
    }
    private void cables(String name, TagKey<Item> ingot, TagKey<Item> plate, ItemLike cable, int cut, int extrude) {
        add(ProcessingKind.CUTTING, name + "_cable", tag(plate), 1, cable, cut);
        add(ProcessingKind.EXTRUDING, name + "_cable", tag(ingot), 1, cable, extrude);
    }
    private void extraction() {
        add(ProcessingKind.EXTRACTING, "rubber_from_resin", Ingredient.of(ICItems.STICKY_RESIN.get()), 1, ICItems.RUBBER.get(), 3);
        add(ProcessingKind.EXTRACTING, "rubber_from_log", Ingredient.of(ICItems.RUBBER_LOG.get()), 1, ICItems.RUBBER.get(), 1);
        add(ProcessingKind.EXTRACTING, "rubber_from_wood", Ingredient.of(ICItems.RUBBER_WOOD.get()), 1, ICItems.RUBBER.get(), 1);
        add(ProcessingKind.EXTRACTING, "rubber_from_sapling", Ingredient.of(ICItems.RUBBER_SAPLING.get()), 1, ICItems.RUBBER.get(), 1);
        add(ProcessingKind.EXTRACTING, "sulfur", tag(Tags.Items.GUNPOWDERS), 1, ICItems.SULFUR_DUST.get(), 1);
        add(ProcessingKind.EXTRACTING, "empty_air_cell", Ingredient.of(ICItems.AIR_CELL.get()), 1, ICItems.EMPTY_CELL.get(), 1);
    }
    private void compression() {
        add(ProcessingKind.COMPRESSING, "iridium_ore", Ingredient.of(ICItems.IRIDIUM_SHARD.get()), 9, ICItems.IRIDIUM_ORE.get(), 1);
        add(ProcessingKind.COMPRESSING, "air_cell", Ingredient.of(ICItems.EMPTY_CELL.get()), 1, ICItems.AIR_CELL.get(), 1);
        add(ProcessingKind.COMPRESSING, "lapis_plate", tag(ICTags.DUSTS_LAPIS), 1, ICItems.LAPIS_PLATE.get(), 1);
        add(ProcessingKind.COMPRESSING, "obsidian_plate", tag(ICTags.DUSTS_OBSIDIAN), 1, ICItems.OBSIDIAN_PLATE.get(), 1);
        add(ProcessingKind.COMPRESSING, "dense_lapis_plate", tag(ICTags.PLATES_LAPIS), 9, ICItems.DENSE_LAPIS_PLATE.get(), 1);
        add(ProcessingKind.COMPRESSING, "dense_obsidian_plate", tag(ICTags.PLATES_OBSIDIAN), 9, ICItems.DENSE_OBSIDIAN_PLATE.get(), 1);
        tiny("iron", ICTags.SMALL_DUSTS_IRON, ICItems.IRON_DUST.get());
        tiny("gold", ICTags.SMALL_DUSTS_GOLD, ICItems.GOLD_DUST.get());
        tiny("copper", ICTags.SMALL_DUSTS_COPPER, ICItems.COPPER_DUST.get());
        tiny("tin", ICTags.SMALL_DUSTS_TIN, ICItems.TIN_DUST.get());
        tiny("lead", ICTags.SMALL_DUSTS_LEAD, ICItems.LEAD_DUST.get());
        tiny("bronze", ICTags.SMALL_DUSTS_BRONZE, ICItems.BRONZE_DUST.get());
        tiny("silver", ICTags.SMALL_DUSTS_SILVER, ICItems.SILVER_DUST.get());
        tiny("lapis", ICTags.SMALL_DUSTS_LAPIS, ICItems.LAPIS_DUST.get());
        tiny("obsidian", ICTags.SMALL_DUSTS_OBSIDIAN, ICItems.OBSIDIAN_DUST.get());
        tiny("sulfur", ICTags.SMALL_DUSTS_SULFUR, ICItems.SULFUR_DUST.get());
        tiny("lithium", ICTags.SMALL_DUSTS_LITHIUM, ICItems.LITHIUM_DUST.get());
        add(ProcessingKind.COMPRESSING, "alloy", Ingredient.of(ICItems.MIXED_METAL_INGOT.get()), 1, ICItems.ALLOY_INGOT.get(), 1);
        add(ProcessingKind.COMPRESSING, "carbon_plate", Ingredient.of(ICItems.CARBON_MESH.get()), 1, ICItems.CARBON_PLATE.get(), 1);
        add(ProcessingKind.COMPRESSING, "energy_crystal", tag(ICTags.DUSTS_ENERGIUM), 9, ICItems.ENERGY_CRYSTAL.get(), 1);
    }
    private void tiny(String name, TagKey<Item> input, ItemLike result) { add(ProcessingKind.COMPRESSING, name + "_dust", tag(input), 9, result, 1); }
    private void wash(String name, TagKey<Item> input, ItemLike purified, ItemLike dust) {
        save(ProcessingKind.WASHING, name, tag(input), 1, purified, 1,
                List.of(new ItemStackTemplate(dust.asItem(), 2), new ItemStackTemplate(ICItems.STONE_DUST.get(), 1)), 1000);
    }
    private Ingredient tag(TagKey<Item> tag) { return RecipeTags.tag(registries, tag); }
    private void add(ProcessingKind kind, String name, Ingredient input, int count, ItemLike result, int amount) {
        save(kind, name, input, count, result, amount, List.of(), 0);
    }
    private void save(ProcessingKind kind, String name, Ingredient input, int count, ItemLike result, int amount,
            List<ItemStackTemplate> byproducts, int water) {
        //? if >=26.1 {
        var id = ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, kind.getSerializedName() + "/" + name));
        //?} else
        //var id = Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, kind.getSerializedName() + "/" + name);
        output.accept(id, new ProcessingRecipe(new CommonInfo(true), kind, input,
                new ItemStackTemplate(result.asItem(), amount), count, byproducts, kind.ticks, kind.euPerTick, water), null);
    }
}
