package me.almana.innovativecontraptions.datagen;

import me.almana.innovativecontraptions.InnovativeContraptions;
import me.almana.innovativecontraptions.content.processing.ProcessingKind;
import me.almana.innovativecontraptions.content.processing.ProcessingRecipe;
import me.almana.innovativecontraptions.content.processing.RecyclingRules;
import me.almana.innovativecontraptions.registry.ICItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe.CommonInfo;
import net.minecraft.world.level.ItemLike;
import java.util.List;

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
        metal("iron", Items.IRON_INGOT, ICItems.IRON_PLATE.get(), ICItems.IRON_CASING.get(), ICItems.DENSE_IRON_PLATE.get());
        metal("gold", Items.GOLD_INGOT, ICItems.GOLD_PLATE.get(), ICItems.GOLD_CASING.get(), ICItems.DENSE_GOLD_PLATE.get());
        metal("copper", Items.COPPER_INGOT, ICItems.COPPER_PLATE.get(), ICItems.COPPER_CASING.get(), ICItems.DENSE_COPPER_PLATE.get());
        metal("tin", ICItems.TIN_INGOT.get(), ICItems.TIN_PLATE.get(), ICItems.TIN_CASING.get(), ICItems.DENSE_TIN_PLATE.get());
        metal("lead", ICItems.LEAD_INGOT.get(), ICItems.LEAD_PLATE.get(), ICItems.LEAD_CASING.get(), ICItems.DENSE_LEAD_PLATE.get());
        metal("bronze", ICItems.BRONZE_INGOT.get(), ICItems.BRONZE_PLATE.get(), ICItems.BRONZE_CASING.get(), ICItems.DENSE_BRONZE_PLATE.get());
        metal("steel", ICItems.STEEL_INGOT.get(), ICItems.STEEL_PLATE.get(), ICItems.STEEL_CASING.get(), ICItems.DENSE_STEEL_PLATE.get());
        compression();
        extraction();
        cables("copper", Items.COPPER_INGOT, ICItems.COPPER_PLATE.get(), ICItems.COPPER_CABLE.get(), 2, 3);
        cables("gold", Items.GOLD_INGOT, ICItems.GOLD_PLATE.get(), ICItems.GOLD_CABLE.get(), 4, 4);
        cables("tin", ICItems.TIN_INGOT.get(), ICItems.TIN_PLATE.get(), ICItems.TIN_CABLE.get(), 3, 3);
        cables("iron", Items.IRON_INGOT, ICItems.IRON_PLATE.get(), ICItems.HV_CABLE.get(), 4, 4);
        add(ProcessingKind.EXTRUDING, "iron_shaft", Items.IRON_BLOCK, 1, ICItems.IRON_SHAFT.get(), 1);
        add(ProcessingKind.EXTRUDING, "steel_shaft", ICItems.STEEL_BLOCK.get(), 1, ICItems.STEEL_SHAFT.get(), 1);
        wash("iron", ICItems.CRUSHED_IRON.get(), ICItems.PURIFIED_CRUSHED_IRON.get(), ICItems.SMALL_IRON_DUST.get());
        wash("gold", ICItems.CRUSHED_GOLD.get(), ICItems.PURIFIED_CRUSHED_GOLD.get(), ICItems.SMALL_GOLD_DUST.get());
        wash("copper", ICItems.CRUSHED_COPPER.get(), ICItems.PURIFIED_CRUSHED_COPPER.get(), ICItems.SMALL_COPPER_DUST.get());
        wash("tin", ICItems.CRUSHED_TIN.get(), ICItems.PURIFIED_CRUSHED_TIN.get(), ICItems.SMALL_TIN_DUST.get());
        wash("silver", ICItems.CRUSHED_SILVER.get(), ICItems.PURIFIED_CRUSHED_SILVER.get(), ICItems.SMALL_SILVER_DUST.get());
        wash("uranium", ICItems.CRUSHED_URANIUM.get(), ICItems.PURIFIED_CRUSHED_URANIUM.get(), ICItems.SMALL_LEAD_DUST.get());
        save(ProcessingKind.WASHING, "lead", ICItems.CRUSHED_LEAD.get(), 1, ICItems.PURIFIED_CRUSHED_LEAD.get(), 1,
                List.of(new ItemStackTemplate(ICItems.SMALL_SULFUR_DUST.get(), 3), new ItemStackTemplate(ICItems.STONE_DUST.get(), 1)), 1000);
    }
    private void metal(String name, ItemLike ingot, ItemLike plate, ItemLike casing, ItemLike dense) {
        add(ProcessingKind.ROLLING, name + "_plate", ingot, 1, plate, 1);
        add(ProcessingKind.ROLLING, name + "_casing", plate, 1, casing, 2);
        add(ProcessingKind.COMPRESSING, "dense_" + name + "_plate", plate, 9, dense, 1);
    }
    private void cables(String name, ItemLike ingot, ItemLike plate, ItemLike cable, int cut, int extrude) {
        add(ProcessingKind.CUTTING, name + "_cable", plate, 1, cable, cut);
        add(ProcessingKind.EXTRUDING, name + "_cable", ingot, 1, cable, extrude);
    }
    private void extraction() {
        add(ProcessingKind.EXTRACTING, "rubber_from_resin", ICItems.STICKY_RESIN.get(), 1, ICItems.RUBBER.get(), 3);
        add(ProcessingKind.EXTRACTING, "rubber_from_log", ICItems.RUBBER_LOG.get(), 1, ICItems.RUBBER.get(), 1);
        add(ProcessingKind.EXTRACTING, "rubber_from_wood", ICItems.RUBBER_WOOD.get(), 1, ICItems.RUBBER.get(), 1);
        add(ProcessingKind.EXTRACTING, "rubber_from_sapling", ICItems.RUBBER_SAPLING.get(), 1, ICItems.RUBBER.get(), 1);
        add(ProcessingKind.EXTRACTING, "sulfur", Items.GUNPOWDER, 1, ICItems.SULFUR_DUST.get(), 1);
        add(ProcessingKind.EXTRACTING, "empty_air_cell", ICItems.AIR_CELL.get(), 1, ICItems.EMPTY_CELL.get(), 1);
    }
    private void compression() {
        add(ProcessingKind.COMPRESSING, "iridium_ore", ICItems.IRIDIUM_SHARD.get(), 9, ICItems.IRIDIUM_ORE.get(), 1);
        add(ProcessingKind.COMPRESSING, "air_cell", ICItems.EMPTY_CELL.get(), 1, ICItems.AIR_CELL.get(), 1);
        add(ProcessingKind.COMPRESSING, "lapis_plate", ICItems.LAPIS_DUST.get(), 1, ICItems.LAPIS_PLATE.get(), 1);
        add(ProcessingKind.COMPRESSING, "obsidian_plate", ICItems.OBSIDIAN_DUST.get(), 1, ICItems.OBSIDIAN_PLATE.get(), 1);
        add(ProcessingKind.COMPRESSING, "dense_lapis_plate", ICItems.LAPIS_PLATE.get(), 9, ICItems.DENSE_LAPIS_PLATE.get(), 1);
        add(ProcessingKind.COMPRESSING, "dense_obsidian_plate", ICItems.OBSIDIAN_PLATE.get(), 9, ICItems.DENSE_OBSIDIAN_PLATE.get(), 1);
        tiny("iron", ICItems.SMALL_IRON_DUST.get(), ICItems.IRON_DUST.get());
        tiny("gold", ICItems.SMALL_GOLD_DUST.get(), ICItems.GOLD_DUST.get());
        tiny("copper", ICItems.SMALL_COPPER_DUST.get(), ICItems.COPPER_DUST.get());
        tiny("tin", ICItems.SMALL_TIN_DUST.get(), ICItems.TIN_DUST.get());
        tiny("lead", ICItems.SMALL_LEAD_DUST.get(), ICItems.LEAD_DUST.get());
        tiny("bronze", ICItems.SMALL_BRONZE_DUST.get(), ICItems.BRONZE_DUST.get());
        tiny("silver", ICItems.SMALL_SILVER_DUST.get(), ICItems.SILVER_DUST.get());
        tiny("lapis", ICItems.SMALL_LAPIS_DUST.get(), ICItems.LAPIS_DUST.get());
        tiny("obsidian", ICItems.SMALL_OBSIDIAN_DUST.get(), ICItems.OBSIDIAN_DUST.get());
        tiny("sulfur", ICItems.SMALL_SULFUR_DUST.get(), ICItems.SULFUR_DUST.get());
        tiny("lithium", ICItems.SMALL_LITHIUM_DUST.get(), ICItems.LITHIUM_DUST.get());
        add(ProcessingKind.COMPRESSING, "alloy", ICItems.MIXED_METAL_INGOT.get(), 1, ICItems.ALLOY_INGOT.get(), 1);
        add(ProcessingKind.COMPRESSING, "carbon_plate", ICItems.CARBON_MESH.get(), 1, ICItems.CARBON_PLATE.get(), 1);
        add(ProcessingKind.COMPRESSING, "energy_crystal", ICItems.ENERGIUM_DUST.get(), 9, ICItems.ENERGY_CRYSTAL.get(), 1);
    }
    private void tiny(String name, ItemLike input, ItemLike result) { add(ProcessingKind.COMPRESSING, name + "_dust", input, 9, result, 1); }
    private void wash(String name, ItemLike input, ItemLike purified, ItemLike dust) {
        save(ProcessingKind.WASHING, name, input, 1, purified, 1,
                List.of(new ItemStackTemplate(dust.asItem(), 2), new ItemStackTemplate(ICItems.STONE_DUST.get(), 1)), 1000);
    }
    private void add(ProcessingKind kind, String name, ItemLike input, int count, ItemLike result, int amount) {
        save(kind, name, input, count, result, amount, List.of(), 0);
    }
    private void save(ProcessingKind kind, String name, ItemLike input, int count, ItemLike result, int amount,
            List<ItemStackTemplate> byproducts, int water) {
        //? if >=26.1 {
        var id = ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, kind.getSerializedName() + "/" + name));
        //?} else
        //var id = Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, kind.getSerializedName() + "/" + name);
        output.accept(id, new ProcessingRecipe(new CommonInfo(true), kind, Ingredient.of(input),
                new ItemStackTemplate(result.asItem(), amount), count, byproducts, kind.ticks, kind.euPerTick, water), null);
    }
}
