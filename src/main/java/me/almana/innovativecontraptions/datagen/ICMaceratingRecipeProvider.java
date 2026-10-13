package me.almana.innovativecontraptions.datagen;

import me.almana.innovativecontraptions.InnovativeContraptions;
import me.almana.innovativecontraptions.content.machine.recipe.MaceratingRecipe;
import me.almana.innovativecontraptions.registry.ICItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
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
        add("iron_ore", commonTag("ores/iron"), Items.RAW_IRON, 2);
        add("gold_ore", commonTag("ores/gold"), Items.RAW_GOLD, 2);
        add("copper_ore", commonTag("ores/copper"), Items.RAW_COPPER, 2);
        add("tin_ore", commonTag("ores/tin"), ICItems.RAW_TIN.get(), 2);
        add("lead_ore", commonTag("ores/lead"), ICItems.RAW_LEAD.get(), 2);
        addWithInputCount("raw_iron", commonTag("raw_materials/iron"), ICItems.CRUSHED_IRON.get(), 2, 3);
        addWithInputCount("raw_gold", commonTag("raw_materials/gold"), ICItems.CRUSHED_GOLD.get(), 2, 3);
        addWithInputCount("raw_copper", commonTag("raw_materials/copper"), ICItems.CRUSHED_COPPER.get(), 2, 3);
        addWithInputCount("raw_tin", commonTag("raw_materials/tin"), ICItems.CRUSHED_TIN.get(), 2, 3);
        addWithInputCount("raw_lead", commonTag("raw_materials/lead"), ICItems.CRUSHED_LEAD.get(), 2, 3);

        add("coal", Items.COAL, ICItems.COAL_DUST.get(), 1);
        add("charcoal", Items.CHARCOAL, ICItems.CHARCOAL_DUST.get(), 1);
        add("coal_ore", commonTag("ores/coal"), ICItems.COAL_DUST.get(), 2);
        add("coal_block", commonTag("storage_blocks/coal"), ICItems.COAL_DUST.get(), 9, STORAGE_BLOCK_TICKS);
        add("iron_ingot", commonTag("ingots/iron"), ICItems.IRON_DUST.get(), 1);
        add("iron_block", commonTag("storage_blocks/iron"), ICItems.IRON_DUST.get(), 9, STORAGE_BLOCK_TICKS);
        add("steel_ingot", commonTag("ingots/steel"), ICItems.IRON_DUST.get(), 1);
        add("gold_ingot", commonTag("ingots/gold"), ICItems.GOLD_DUST.get(), 1);
        add("gold_block", commonTag("storage_blocks/gold"), ICItems.GOLD_DUST.get(), 9, STORAGE_BLOCK_TICKS);
        add("redstone_ore", Items.REDSTONE_ORE, Items.REDSTONE, 16);
        add("silver_ore", commonTag("ores/silver"), ICItems.CRUSHED_SILVER.get(), 2);
        add("silver_ingot", commonTag("ingots/silver"), ICItems.SILVER_DUST.get(), 1);
        add("silver_block", commonTag("storage_blocks/silver"), ICItems.SILVER_DUST.get(), 9, STORAGE_BLOCK_TICKS);
        add("copper_ingot", commonTag("ingots/copper"), ICItems.COPPER_DUST.get(), 1);
        add("copper_block", commonTag("storage_blocks/copper"), ICItems.COPPER_DUST.get(), 9, STORAGE_BLOCK_TICKS);
        add("bronze_ingot", commonTag("ingots/bronze"), ICItems.BRONZE_DUST.get(), 1);
        add("bronze_block", commonTag("storage_blocks/bronze"), ICItems.BRONZE_DUST.get(), 9, STORAGE_BLOCK_TICKS);
        add("tin_ingot", commonTag("ingots/tin"), ICItems.TIN_DUST.get(), 1);
        add("tin_block", commonTag("storage_blocks/tin"), ICItems.TIN_DUST.get(), 9, STORAGE_BLOCK_TICKS);
        add("netherrack", Items.NETHERRACK, ICItems.GROUND_NETHERRACK.get(), 1);
        add("nether_quartz_ore", Items.NETHER_QUARTZ_ORE, Items.QUARTZ, 2);
        add("uranium_ore", commonTag("ores/uranium"), ICItems.CRUSHED_URANIUM.get(), 2);
        add("charcoal_block", commonTag("storage_blocks/charcoal"), ICItems.CHARCOAL_DUST.get(), 9, STORAGE_BLOCK_TICKS);
        add("blaze_rod", Items.BLAZE_ROD, Items.BLAZE_POWDER, 5);
        add("spider_eye", Items.SPIDER_EYE, ICItems.CROP_TREATMENT_POWDER.get(), 2);
        add("pufferfish", Items.PUFFERFISH, ICItems.CROP_TREATMENT_POWDER.get(), 3);
        add("poisonous_potato", Items.POISONOUS_POTATO, ICItems.CROP_TREATMENT_POWDER.get(), 2);
        add("wool", ItemTags.WOOL, Items.STRING, 3);
        add("stone", Items.STONE, Items.GRAVEL, 1);
        add("cobblestone", Items.COBBLESTONE, Items.SAND, 1);
        add("sandstone", Items.SANDSTONE, Items.SAND, 1);
        add("red_sandstone", Items.RED_SANDSTONE, Items.RED_SAND, 1);
        add("ice", Items.ICE, Items.SNOWBALL, 1);
        add("clay", Items.CLAY, ICItems.CLAY_DUST.get(), 2);
        add("glowstone", Items.GLOWSTONE, Items.GLOWSTONE_DUST, 4);
        add("bone", Items.BONE, Items.BONE_MEAL, 5);
        add("plant_ball", ICItems.PLANT_BALL.get(), ICItems.BIO_COMPOUND.get(), 1);
        add("coffee_beans", ICItems.COFFEE_BEANS.get(), ICItems.GROUND_COFFEE.get(), 3);
        add("obsidian", Items.OBSIDIAN, ICItems.OBSIDIAN_DUST.get(), 1);
        add("hay_block", Items.HAY_BLOCK, ICItems.FLOUR.get(), 9);
        add("bio_compound", ICItems.BIO_COMPOUND.get(), Items.DIRT, 1);
        for (var plant : new ItemLike[]{Items.WHEAT, Items.CACTUS, Items.CARROT, Items.POTATO, Items.PUMPKIN, Items.MELON, Items.SUGAR_CANE, Items.SHORT_GRASS}) {
            String name = BuiltInRegistries.ITEM.getKey(plant.asItem()).getPath();
            add("chaff_" + name, Ingredient.of(plant), ICItems.BIO_COMPOUND.get(), 8, 1, PROCESS_TICKS);
        }
        for (var seed : new ItemLike[]{Items.WHEAT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS}) {
            String name = BuiltInRegistries.ITEM.getKey(seed.asItem()).getPath();
            add("chaff_" + name, Ingredient.of(seed), ICItems.BIO_COMPOUND.get(), 16, 1, PROCESS_TICKS);
        }
        add("chaff_weed", Ingredient.of(ICItems.WEED.get()), ICItems.BIO_COMPOUND.get(), 32, 1, PROCESS_TICKS);
        addWithInputCount("chaff_leaves", ItemTags.LEAVES, ICItems.BIO_COMPOUND.get(), 8, 1);
        addWithInputCount("chaff_saplings", ItemTags.SAPLINGS, ICItems.BIO_COMPOUND.get(), 4, 1);
        add("lead_ingot", commonTag("ingots/lead"), ICItems.LEAD_DUST.get(), 1);
        add("diamond", Items.DIAMOND, ICItems.DIAMOND_DUST.get(), 1);
        add("lapis", Items.LAPIS_LAZULI, ICItems.LAPIS_DUST.get(), 1);
        add("dense_iron_plate", ICItems.DENSE_IRON_PLATE.get(), ICItems.IRON_DUST.get(), 8);
        add("dense_gold_plate", ICItems.DENSE_GOLD_PLATE.get(), ICItems.GOLD_DUST.get(), 8);
        add("dense_copper_plate", ICItems.DENSE_COPPER_PLATE.get(), ICItems.COPPER_DUST.get(), 8);
        add("dense_tin_plate", ICItems.DENSE_TIN_PLATE.get(), ICItems.TIN_DUST.get(), 8);
        add("dense_bronze_plate", ICItems.DENSE_BRONZE_PLATE.get(), ICItems.BRONZE_DUST.get(), 8);
        add("dense_lead_plate", ICItems.DENSE_LEAD_PLATE.get(), ICItems.LEAD_DUST.get(), 8);
        add("dense_steel_plate", ICItems.DENSE_STEEL_PLATE.get(), ICItems.IRON_DUST.get(), 8);
        add("dense_lapis_plate", ICItems.DENSE_LAPIS_PLATE.get(), ICItems.LAPIS_DUST.get(), 8);
        add("dense_obsidian_plate", ICItems.DENSE_OBSIDIAN_PLATE.get(), ICItems.OBSIDIAN_DUST.get(), 8);
    }

    private void add(String name, ItemLike input, ItemLike result, int resultCount) {
        add(name, Ingredient.of(input), result, 1, resultCount, PROCESS_TICKS);
    }

    private void add(String name, ItemLike input, ItemLike result, int resultCount, int processTicks) {
        add(name, Ingredient.of(input), result, 1, resultCount, processTicks);
    }

    private void add(String name, TagKey<Item> input, ItemLike result, int resultCount) {
        add(name, ingredient(input), result, 1, resultCount, PROCESS_TICKS);
    }

    private void addWithInputCount(String name, TagKey<Item> input, ItemLike result, int inputCount, int resultCount) {
        add(name, ingredient(input), result, inputCount, resultCount, PROCESS_TICKS);
    }

    private void add(String name, TagKey<Item> input, ItemLike result, int resultCount, int processTicks) {
        add(name, ingredient(input), result, 1, resultCount, processTicks);
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

    private Ingredient ingredient(TagKey<Item> tag) {
        //? if >=26.1 {
        return Ingredient.of(registries.lookupOrThrow(Registries.ITEM).getOrThrow(tag));
        //?} else
        //return Ingredient.of(tag);
    }

    private static TagKey<Item> commonTag(String path) {
        return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", path));
    }
}
