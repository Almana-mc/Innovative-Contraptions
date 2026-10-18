package me.almana.innovativecontraptions.datagen;

import me.almana.innovativecontraptions.InnovativeContraptions;
import me.almana.innovativecontraptions.content.machine.recipe.MaceratingRecipe;
import me.almana.innovativecontraptions.registry.ICItems;
import me.almana.innovativecontraptions.registry.ICTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.Recipe.CommonInfo;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;

final class ICMaceratingRecipeProvider {
    private static final int PROCESS_TICKS = 400;
    private static final int STORAGE_BLOCK_TICKS = 1_600;
    private static final int EU_PER_TICK = 2;

    private final HolderLookup.Provider registries;
    private final RecipeOutput output;

    ICMaceratingRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        this.registries = registries;
        this.output = output;
    }

    void build() {
        add("iridium_ore", ICItems.IRIDIUM_ORE.get(), ICItems.IRIDIUM_SHARD.get(), 9);
        add("iron_ore", Tags.Items.ORES_IRON, Items.RAW_IRON, 2);
        add("gold_ore", Tags.Items.ORES_GOLD, Items.RAW_GOLD, 2);
        add("copper_ore", Tags.Items.ORES_COPPER, Items.RAW_COPPER, 2);
        add("tin_ore", ICTags.ORES_TIN, ICItems.RAW_TIN.get(), 2);
        add("lead_ore", ICTags.ORES_LEAD, ICItems.RAW_LEAD.get(), 2);
        addWithInputCount("raw_iron", Tags.Items.RAW_MATERIALS_IRON, ICItems.CRUSHED_IRON.get(), 2, 3);
        addWithInputCount("raw_gold", Tags.Items.RAW_MATERIALS_GOLD, ICItems.CRUSHED_GOLD.get(), 2, 3);
        addWithInputCount("raw_copper", Tags.Items.RAW_MATERIALS_COPPER, ICItems.CRUSHED_COPPER.get(), 2, 3);
        addWithInputCount("raw_tin", ICTags.RAW_MATERIALS_TIN, ICItems.CRUSHED_TIN.get(), 2, 3);
        addWithInputCount("raw_lead", ICTags.RAW_MATERIALS_LEAD, ICItems.CRUSHED_LEAD.get(), 2, 3);

        add("coal", Items.COAL, ICItems.COAL_DUST.get(), 1);
        add("charcoal", Items.CHARCOAL, ICItems.CHARCOAL_DUST.get(), 1);
        add("coal_ore", Tags.Items.ORES_COAL, ICItems.COAL_DUST.get(), 2);
        add("coal_block", Tags.Items.STORAGE_BLOCKS_COAL, ICItems.COAL_DUST.get(), 9, STORAGE_BLOCK_TICKS);
        add("iron_ingot", Tags.Items.INGOTS_IRON, ICItems.IRON_DUST.get(), 1);
        add("iron_block", Tags.Items.STORAGE_BLOCKS_IRON, ICItems.IRON_DUST.get(), 9, STORAGE_BLOCK_TICKS);
        add("steel_ingot", ICTags.INGOTS_STEEL, ICItems.IRON_DUST.get(), 1);
        add("gold_ingot", Tags.Items.INGOTS_GOLD, ICItems.GOLD_DUST.get(), 1);
        add("gold_block", Tags.Items.STORAGE_BLOCKS_GOLD, ICItems.GOLD_DUST.get(), 9, STORAGE_BLOCK_TICKS);
        add("redstone_ore", Tags.Items.ORES_REDSTONE, Items.REDSTONE, 16);
        add("silver_ore", ICTags.ORES_SILVER, ICItems.CRUSHED_SILVER.get(), 2);
        add("silver_ingot", ICTags.INGOTS_SILVER, ICItems.SILVER_DUST.get(), 1);
        add("silver_block", ICTags.STORAGE_BLOCKS_SILVER, ICItems.SILVER_DUST.get(), 9, STORAGE_BLOCK_TICKS);
        add("copper_ingot", Tags.Items.INGOTS_COPPER, ICItems.COPPER_DUST.get(), 1);
        add("copper_block", Tags.Items.STORAGE_BLOCKS_COPPER, ICItems.COPPER_DUST.get(), 9, STORAGE_BLOCK_TICKS);
        add("bronze_ingot", ICTags.INGOTS_BRONZE, ICItems.BRONZE_DUST.get(), 1);
        add("bronze_block", ICTags.STORAGE_BLOCKS_BRONZE, ICItems.BRONZE_DUST.get(), 9, STORAGE_BLOCK_TICKS);
        add("tin_ingot", ICTags.INGOTS_TIN, ICItems.TIN_DUST.get(), 1);
        add("tin_block", ICTags.STORAGE_BLOCKS_TIN, ICItems.TIN_DUST.get(), 9, STORAGE_BLOCK_TICKS);
        add("netherrack", Tags.Items.NETHERRACKS, ICItems.GROUND_NETHERRACK.get(), 1);
        add("nether_quartz_ore", Tags.Items.ORES_QUARTZ, Items.QUARTZ, 2);
        add("uranium_ore", ICTags.ORES_URANIUM, ICItems.CRUSHED_URANIUM.get(), 2);
        add("charcoal_block", ICTags.STORAGE_BLOCKS_CHARCOAL, ICItems.CHARCOAL_DUST.get(), 9, STORAGE_BLOCK_TICKS);
        add("blaze_rod", Tags.Items.RODS_BLAZE, Items.BLAZE_POWDER, 5);
        add("spider_eye", Items.SPIDER_EYE, ICItems.CROP_TREATMENT_POWDER.get(), 2);
        add("pufferfish", Items.PUFFERFISH, ICItems.CROP_TREATMENT_POWDER.get(), 3);
        add("poisonous_potato", Items.POISONOUS_POTATO, ICItems.CROP_TREATMENT_POWDER.get(), 2);
        add("wool", ItemTags.WOOL, Items.STRING, 3);
        add("stone", Tags.Items.STONES, Items.GRAVEL, 1);
        add("cobblestone", Tags.Items.COBBLESTONES_NORMAL, Items.SAND, 1);
        add("sandstone", Tags.Items.SANDSTONE_UNCOLORED_BLOCKS, Items.SAND, 1);
        add("red_sandstone", Tags.Items.SANDSTONE_RED_BLOCKS, Items.RED_SAND, 1);
        add("ice", Items.ICE, Items.SNOWBALL, 1);
        add("clay", Items.CLAY, ICItems.CLAY_DUST.get(), 2);
        add("glowstone", Items.GLOWSTONE, Items.GLOWSTONE_DUST, 4);
        add("bone", Tags.Items.BONES, Items.BONE_MEAL, 5);
        add("plant_ball", ICItems.PLANT_BALL.get(), ICItems.BIO_COMPOUND.get(), 1);
        add("coffee_beans", ICItems.COFFEE_BEANS.get(), ICItems.GROUND_COFFEE.get(), 3);
        add("obsidian", Tags.Items.OBSIDIANS_NORMAL, ICItems.OBSIDIAN_DUST.get(), 1);
        add("hay_block", Tags.Items.STORAGE_BLOCKS_WHEAT, ICItems.FLOUR.get(), 9);
        add("bio_compound", ICItems.BIO_COMPOUND.get(), Items.DIRT, 1);
        addWithInputCount("chaff_wheat", Tags.Items.CROPS_WHEAT, ICItems.BIO_COMPOUND.get(), 8, 1);
        addWithInputCount("chaff_cactus", Tags.Items.CROPS_CACTUS, ICItems.BIO_COMPOUND.get(), 8, 1);
        addWithInputCount("chaff_carrot", Tags.Items.CROPS_CARROT, ICItems.BIO_COMPOUND.get(), 8, 1);
        addWithInputCount("chaff_potato", Tags.Items.CROPS_POTATO, ICItems.BIO_COMPOUND.get(), 8, 1);
        addWithInputCount("chaff_pumpkin", Tags.Items.CROPS_PUMPKIN, ICItems.BIO_COMPOUND.get(), 8, 1);
        addWithInputCount("chaff_melon", Tags.Items.CROPS_MELON, ICItems.BIO_COMPOUND.get(), 8, 1);
        addWithInputCount("chaff_sugar_cane", Tags.Items.CROPS_SUGAR_CANE, ICItems.BIO_COMPOUND.get(), 8, 1);
        add("chaff_short_grass", Ingredient.of(Items.SHORT_GRASS), ICItems.BIO_COMPOUND.get(), 8, 1, PROCESS_TICKS);
        addWithInputCount("chaff_wheat_seeds", Tags.Items.SEEDS_WHEAT, ICItems.BIO_COMPOUND.get(), 16, 1);
        addWithInputCount("chaff_melon_seeds", Tags.Items.SEEDS_MELON, ICItems.BIO_COMPOUND.get(), 16, 1);
        addWithInputCount("chaff_pumpkin_seeds", Tags.Items.SEEDS_PUMPKIN, ICItems.BIO_COMPOUND.get(), 16, 1);
        add("chaff_weed", Ingredient.of(ICItems.WEED.get()), ICItems.BIO_COMPOUND.get(), 32, 1, PROCESS_TICKS);
        addWithInputCount("chaff_leaves", ItemTags.LEAVES, ICItems.BIO_COMPOUND.get(), 8, 1);
        addWithInputCount("chaff_saplings", ItemTags.SAPLINGS, ICItems.BIO_COMPOUND.get(), 4, 1);
        add("lead_ingot", ICTags.INGOTS_LEAD, ICItems.LEAD_DUST.get(), 1);
        add("diamond", Tags.Items.GEMS_DIAMOND, ICItems.DIAMOND_DUST.get(), 1);
        add("lapis", Tags.Items.GEMS_LAPIS, ICItems.LAPIS_DUST.get(), 1);
        add("dense_iron_plate", ICTags.DENSE_PLATES_IRON, ICItems.IRON_DUST.get(), 8);
        add("dense_gold_plate", ICTags.DENSE_PLATES_GOLD, ICItems.GOLD_DUST.get(), 8);
        add("dense_copper_plate", ICTags.DENSE_PLATES_COPPER, ICItems.COPPER_DUST.get(), 8);
        add("dense_tin_plate", ICTags.DENSE_PLATES_TIN, ICItems.TIN_DUST.get(), 8);
        add("dense_bronze_plate", ICTags.DENSE_PLATES_BRONZE, ICItems.BRONZE_DUST.get(), 8);
        add("dense_lead_plate", ICTags.DENSE_PLATES_LEAD, ICItems.LEAD_DUST.get(), 8);
        add("dense_steel_plate", ICTags.DENSE_PLATES_STEEL, ICItems.IRON_DUST.get(), 8);
        add("dense_lapis_plate", ICTags.DENSE_PLATES_LAPIS, ICItems.LAPIS_DUST.get(), 8);
        add("dense_obsidian_plate", ICTags.DENSE_PLATES_OBSIDIAN, ICItems.OBSIDIAN_DUST.get(), 8);
    }

    private void add(String name, ItemLike input, ItemLike result, int resultCount) {
        add(name, Ingredient.of(input), result, 1, resultCount, PROCESS_TICKS);
    }

    private void add(String name, TagKey<Item> input, ItemLike result, int resultCount) {
        add(name, RecipeTags.tag(registries, input), result, 1, resultCount, PROCESS_TICKS);
    }

    private void addWithInputCount(String name, TagKey<Item> input, ItemLike result, int inputCount, int resultCount) {
        add(name, RecipeTags.tag(registries, input), result, inputCount, resultCount, PROCESS_TICKS);
    }

    private void add(String name, TagKey<Item> input, ItemLike result, int resultCount, int processTicks) {
        add(name, RecipeTags.tag(registries, input), result, 1, resultCount, processTicks);
    }

    private void add(
            String name,
            Ingredient ingredient,
            ItemLike result,
            int inputCount,
            int resultCount,
            int processTicks
    ) {
        //? if >=26.1 {
        ResourceKey<Recipe<?>> id = ResourceKey.create(
                Registries.RECIPE,
                Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, "macerating/" + name)
        );
        //?} else
        //Identifier id = Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, "macerating/" + name);
        MaceratingRecipe recipe = new MaceratingRecipe(
                new CommonInfo(true),
                ingredient,
                new ItemStackTemplate(result.asItem(), resultCount),
                inputCount,
                processTicks,
                EU_PER_TICK
        );
        output.accept(id, recipe, null);
    }
}
