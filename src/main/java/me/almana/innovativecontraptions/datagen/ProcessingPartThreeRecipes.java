package me.almana.innovativecontraptions.datagen;

import java.util.List;
import java.util.Optional;
import me.almana.innovativecontraptions.InnovativeContraptions;
import me.almana.innovativecontraptions.content.construction.ConstructionContent;
import me.almana.innovativecontraptions.content.fluid.BiogasContent;
import me.almana.innovativecontraptions.content.fluid.CoolantContent;
import me.almana.innovativecontraptions.content.processing.*;
import me.almana.innovativecontraptions.registry.ICItems;
import me.almana.innovativecontraptions.registry.ICTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe.CommonInfo;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.FluidStackTemplate;

public final class ProcessingPartThreeRecipes {
    private final RecipeOutput output;
    private final HolderLookup.Provider registries;
    public ProcessingPartThreeRecipes(HolderLookup.Provider registries, RecipeOutput output) { this.output = output; this.registries = registries; }
    public void build() { extraction(); slicing(); centrifuging(); packaging(); enrichment(); }
    private void enrichment() {
        enrich("biomass", Ingredient.of(ICItems.BIO_COMPOUND.get()), 1, ICItems.BIOMASS_CELL.get(), BiogasContent.BIOMASS.getKey());
        enrich("coolant", tag(ICTags.DUSTS_LAPIS), 8, ICItems.COOLANT_CELL.get(), CoolantContent.COOLANT.getKey());
        enrich("construction_foam", Ingredient.of(ConstructionContent.CF_POWDER.get()), 1, ConstructionContent.FOAM_CELL.get(), ConstructionContent.FOAM_FLUID.getKey());
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
        simple(ProcessingKind.EXTRACTING, "reclaim_can", Ingredient.of(ICItems.FILLED_TIN_CAN.get()), 1, ICItems.TIN_CAN.get(), 1);
        simple(ProcessingKind.EXTRACTING, "brick_recovery", Ingredient.of(Items.BRICKS), 1, Items.BRICK, 4);
        simple(ProcessingKind.EXTRACTING, "nether_brick_recovery", Ingredient.of(Items.NETHER_BRICKS), 1, Items.NETHER_BRICK, 4);
        simple(ProcessingKind.EXTRACTING, "clay_recovery", Ingredient.of(Items.CLAY), 1, Items.CLAY_BALL, 4);
        simple(ProcessingKind.EXTRACTING, "snow_recovery", Ingredient.of(Items.SNOW_BLOCK), 1, Items.SNOWBALL, 4);
        simple(ProcessingKind.EXTRACTING, "iodine_recovery", Ingredient.of(ICItems.HYDRATED_TIN_DUST.get()), 1, ICItems.IODINE.get(), 1);
        simple(ProcessingKind.EXTRACTING, "netherrack_sulfur", tag(ICTags.DUSTS_NETHERRACK), 1, ICItems.SMALL_SULFUR_DUST.get(), 1);
        for (var dye : DyeColor.values()) {
            if (dye == DyeColor.WHITE) continue;
            simple(ProcessingKind.EXTRACTING, "whiten_" + dye.getName(),
                    Ingredient.of(BuiltInRegistries.ITEM.getValue(Identifier.withDefaultNamespace(dye.getName() + "_wool"))), 1, Items.WHITE_WOOL, 1);
        }
        simple(ProcessingKind.EXTRUDING, "fuel_sleeve", tag(ICTags.PLATES_IRON), 1, ICItems.FUEL_ROD.get(), 1);
        simple(ProcessingKind.EXTRUDING, "iron_fence", tag(ICTags.CASINGS_IRON), 1, Items.IRON_BARS, 1);
    }
    private void slicing() {
        cut("iron_block", tag(Tags.Items.STORAGE_BLOCKS_IRON), ICItems.IRON_PLATE.get(), 9, 5);
        cut("gold_block", tag(Tags.Items.STORAGE_BLOCKS_GOLD), ICItems.GOLD_PLATE.get(), 9, 2);
        cut("copper_block", tag(Tags.Items.STORAGE_BLOCKS_COPPER), ICItems.COPPER_PLATE.get(), 9, 2);
        cut("tin_block", tag(ICTags.STORAGE_BLOCKS_TIN), ICItems.TIN_PLATE.get(), 9, 2);
        cut("bronze_block", tag(ICTags.STORAGE_BLOCKS_BRONZE), ICItems.BRONZE_PLATE.get(), 9, 2);
        cut("lead_block", tag(ICTags.STORAGE_BLOCKS_LEAD), ICItems.LEAD_PLATE.get(), 9, 2);
        cut("steel_block", tag(ICTags.STORAGE_BLOCKS_STEEL), ICItems.STEEL_PLATE.get(), 9, 8);
        cut("lapis_block", tag(Tags.Items.STORAGE_BLOCKS_LAPIS), ICItems.LAPIS_PLATE.get(), 9, 5);
        cut("obsidian", tag(Tags.Items.OBSIDIANS_NORMAL), ICItems.OBSIDIAN_PLATE.get(), 4, 8);
        for (String wood : RecipeTags.WOODS) {
            ItemLike planks = BuiltInRegistries.ITEM.getValue(Identifier.withDefaultNamespace(wood + "_planks"));
            cut(wood + "_logs", tag(RecipeTags.logs(wood)), planks, 6, 1);
        }
        save(ProcessingKind.SLICING, "sticks", tag(ItemTags.PLANKS), 2, Items.STICK, 6, List.of(), 0, 1, Optional.empty());
        cut("rubber_log", Ingredient.of(ICItems.RUBBER_LOG.get()), Items.JUNGLE_PLANKS, 4, 1);
        cut("rubber_wood", Ingredient.of(ICItems.RUBBER_WOOD.get()), Items.JUNGLE_PLANKS, 4, 1);
    }
    private void centrifuging() {
        ore("iron", ICTags.CRUSHED_ORES_IRON, ICTags.PURIFIED_CRUSHED_ORES_IRON, ICItems.IRON_DUST.get(), ICItems.SMALL_GOLD_DUST.get(), 1500);
        ore("gold", ICTags.CRUSHED_ORES_GOLD, ICTags.PURIFIED_CRUSHED_ORES_GOLD, ICItems.GOLD_DUST.get(), ICItems.SMALL_SILVER_DUST.get(), 2000);
        ore("copper", ICTags.CRUSHED_ORES_COPPER, ICTags.PURIFIED_CRUSHED_ORES_COPPER, ICItems.COPPER_DUST.get(), ICItems.SMALL_TIN_DUST.get(), 500);
        ore("tin", ICTags.CRUSHED_ORES_TIN, ICTags.PURIFIED_CRUSHED_ORES_TIN, ICItems.TIN_DUST.get(), ICItems.SMALL_IRON_DUST.get(), 1000);
        separate("raw_lead", tag(ICTags.CRUSHED_ORES_LEAD), 1, ICItems.LEAD_DUST.get(), 1, 2000, new ItemStackTemplate(ICItems.STONE_DUST.get()));
        separate("purified_lead", tag(ICTags.PURIFIED_CRUSHED_ORES_LEAD), 1, ICItems.LEAD_DUST.get(), 1, 2000, new ItemStackTemplate(ICItems.SMALL_COPPER_DUST.get()));
        separate("raw_silver", tag(ICTags.CRUSHED_ORES_SILVER), 1, ICItems.SILVER_DUST.get(), 1, 2000, new ItemStackTemplate(ICItems.STONE_DUST.get()));
        separate("purified_silver", tag(ICTags.PURIFIED_CRUSHED_ORES_SILVER), 1, ICItems.SILVER_DUST.get(), 1, 1000, new ItemStackTemplate(ICItems.SMALL_LEAD_DUST.get()));
        separate("raw_uranium", tag(ICTags.CRUSHED_ORES_URANIUM), 1, ICItems.URANIUM_238.get(), 4, 3000,
                new ItemStackTemplate(ICItems.SMALL_URANIUM_235.get()), new ItemStackTemplate(ICItems.STONE_DUST.get()));
        separate("purified_uranium", tag(ICTags.PURIFIED_CRUSHED_ORES_URANIUM), 1, ICItems.URANIUM_238.get(), 6, 3000, new ItemStackTemplate(ICItems.SMALL_URANIUM_235.get()));
        separate("clay_silica", tag(ICTags.DUSTS_CLAY), 4, ICItems.SILICON_DIOXIDE_DUST.get(), 1, 250);
        separate("stone_dust", tag(Tags.Items.COBBLESTONES_NORMAL), 1, ICItems.STONE_DUST.get(), 1, 100);
        separate("quartz_lithium", tag(Tags.Items.GEMS_QUARTZ), 2, ICItems.SMALL_LITHIUM_DUST.get(), 1, 500);
        separate("slag_recovery", Ingredient.of(ICItems.SLAG.get()), 1, ICItems.COAL_DUST.get(), 5, 1500, new ItemStackTemplate(ICItems.SMALL_GOLD_DUST.get()));
        separate("rtg_recovery", Ingredient.of(ICItems.RTG_PELLET.get()), 1, ICItems.PLUTONIUM.get(), 3, 5000, new ItemStackTemplate(ICItems.IRON_DUST.get(), 54));
        depleted("uranium", ICItems.DEPLETED_URANIUM.get(), ICItems.URANIUM_238.get(), 1, 4000);
        depleted("dual_uranium", ICItems.DEPLETED_DUAL_URANIUM.get(), ICItems.URANIUM_238.get(), 2, 4000);
        depleted("quad_uranium", ICItems.DEPLETED_QUAD_URANIUM.get(), ICItems.URANIUM_238.get(), 4, 4000);
        depleted("mox", ICItems.DEPLETED_MOX.get(), ICItems.PLUTONIUM.get(), 1, 5000);
        depleted("dual_mox", ICItems.DEPLETED_DUAL_MOX.get(), ICItems.PLUTONIUM.get(), 2, 5000);
        depleted("quad_mox", ICItems.DEPLETED_QUAD_MOX.get(), ICItems.PLUTONIUM.get(), 4, 5000);
        separate("fuel_enrichment", Ingredient.of(ICItems.URANIUM.get()), 20, ICItems.URANIUM_238.get(), 56, 4000,
                new ItemStackTemplate(ICItems.URANIUM_238.get(), 56), new ItemStackTemplate(ICItems.URANIUM_235.get(), 7));
    }
    private void packaging() {
        save(ProcessingKind.CANNING, "uranium_sleeve", Ingredient.of(ICItems.URANIUM.get()), 1, ICItems.FUEL_ROD_URANIUM.get(), 1,
                List.of(), 0, 0, Optional.of(Ingredient.of(ICItems.FUEL_ROD.get())));
        save(ProcessingKind.CANNING, "mixed_sleeve", Ingredient.of(ICItems.MOX.get()), 1, ICItems.FUEL_ROD_MOX.get(), 1,
                List.of(), 0, 0, Optional.of(Ingredient.of(ICItems.FUEL_ROD.get())));
    }
    private void depleted(String name, ItemLike input, ItemLike isotope, int multiplier, int heat) {
        separate(name + "_recovery", Ingredient.of(input), 1, isotope, (heat == 4000 ? 4 : 3) * multiplier, heat,
                new ItemStackTemplate(ICItems.SMALL_PLUTONIUM.get(), multiplier), new ItemStackTemplate(ICItems.IRON_DUST.get(), 2 * multiplier - 1));
    }
    private void ore(String name, TagKey<Item> raw, TagKey<Item> purified, ItemLike dust, ItemLike secondary, int heat) {
        separate("raw_" + name, tag(raw), 1, dust, 1, heat, new ItemStackTemplate(secondary.asItem(), 1), new ItemStackTemplate(ICItems.STONE_DUST.get()));
        separate("purified_" + name, tag(purified), 1, dust, 1, heat, new ItemStackTemplate(secondary.asItem(), 1));
    }
    private Ingredient tag(TagKey<Item> tag) { return RecipeTags.tag(registries, tag); }
    private void separate(String name, Ingredient input, int count, ItemLike result, int amount, int heat, ItemStackTemplate... extra) {
        save(ProcessingKind.CENTRIFUGING, name, input, count, result, amount, List.of(extra), heat, 0, Optional.empty());
    }
    private void cut(String name, Ingredient input, ItemLike result, int amount, int hardness) {
        save(ProcessingKind.SLICING, name, input, 1, result, amount, List.of(), 0, hardness, Optional.empty());
    }
    private void simple(ProcessingKind kind, String name, Ingredient input, int count, ItemLike result, int amount) {
        save(kind, name, input, count, result, amount, List.of(), 0, 0, Optional.empty());
    }
    private void save(ProcessingKind kind, String name, Ingredient input, int count, ItemLike result, int amount,
            List<ItemStackTemplate> extra, int heat, int hardness, Optional<Ingredient> container) {
        //? if >=26.1 {
        var id = ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, kind.getSerializedName() + "/" + name));
        //?} else
        //var id = Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, kind.getSerializedName() + "/" + name);
        output.accept(id, new ProcessingRecipe(new CommonInfo(true), kind, input,
                new ItemStackTemplate(result.asItem(), amount), count, extra, kind.ticks, kind.euPerTick, 0, heat, hardness, container), null);
    }
}

