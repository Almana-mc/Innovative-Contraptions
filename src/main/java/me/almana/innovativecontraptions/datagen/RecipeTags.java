package me.almana.innovativecontraptions.datagen;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import me.almana.innovativecontraptions.content.processing.ProcessingKind;
import me.almana.innovativecontraptions.content.processing.ProcessingRecipe;
import me.almana.innovativecontraptions.registry.ICItems;
import me.almana.innovativecontraptions.registry.ICTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;

final class RecipeTags {
    //? if >=26.1 {
    static final List<String> WOODS = List.of("oak", "spruce", "birch", "jungle", "acacia", "dark_oak", "mangrove", "cherry", "pale_oak");
    //?} else
    //static final List<String> WOODS = List.of("oak", "spruce", "birch", "jungle", "acacia", "dark_oak", "mangrove", "cherry");

    private static final List<String> INGREDIENT_FIELDS = List.of("key", "ingredients", "ingredient", "container");
    private static final List<String> VIOLATIONS = new ArrayList<>();
    private static Map<String, TagKey<Item>> byId;

    static Ingredient tag(HolderLookup.Provider registries, TagKey<Item> tag) {
        //? if >=26.1 {
        return Ingredient.of(registries.lookupOrThrow(Registries.ITEM).getOrThrow(tag));
        //?} else
        //return Ingredient.of(tag);
    }

    static TagKey<Item> logs(String wood) {
        return TagKey.create(Registries.ITEM, Identifier.withDefaultNamespace(wood + "_logs"));
    }

    static Map<Item, TagKey<Item>> preferred() {
        Map<Item, TagKey<Item>> map = new LinkedHashMap<>();
        put(map, ICTags.INGOTS_TIN, ICItems.TIN_INGOT.get());
        put(map, ICTags.INGOTS_LEAD, ICItems.LEAD_INGOT.get());
        put(map, ICTags.INGOTS_SILVER, ICItems.SILVER_INGOT.get());
        put(map, ICTags.INGOTS_BRONZE, ICItems.BRONZE_INGOT.get());
        put(map, ICTags.INGOTS_STEEL, ICItems.STEEL_INGOT.get());
        put(map, ICTags.ORES_TIN, ICItems.TIN_ORE.get());
        put(map, ICTags.ORES_LEAD, ICItems.LEAD_ORE.get());
        put(map, ICTags.ORES_SILVER, ICItems.SILVER_ORE.get());
        put(map, ICTags.ORES_URANIUM, ICItems.URANIUM_ORE.get());
        put(map, ICTags.RAW_MATERIALS_TIN, ICItems.RAW_TIN.get());
        put(map, ICTags.RAW_MATERIALS_LEAD, ICItems.RAW_LEAD.get());
        put(map, ICTags.STORAGE_BLOCKS_TIN, ICItems.TIN_BLOCK.get());
        put(map, ICTags.STORAGE_BLOCKS_BRONZE, ICItems.BRONZE_BLOCK.get());
        put(map, ICTags.STORAGE_BLOCKS_SILVER, ICItems.SILVER_BLOCK.get());
        put(map, ICTags.STORAGE_BLOCKS_LEAD, ICItems.LEAD_BLOCK.get());
        put(map, ICTags.STORAGE_BLOCKS_STEEL, ICItems.STEEL_BLOCK.get());
        put(map, ICTags.STORAGE_BLOCKS_URANIUM, ICItems.URANIUM_BLOCK.get());
        put(map, ICTags.STORAGE_BLOCKS_CHARCOAL, ICItems.CHARCOAL_BLOCK.get());
        put(map, ICTags.PLATES_IRON, ICItems.IRON_PLATE.get());
        put(map, ICTags.PLATES_GOLD, ICItems.GOLD_PLATE.get());
        put(map, ICTags.PLATES_COPPER, ICItems.COPPER_PLATE.get());
        put(map, ICTags.PLATES_TIN, ICItems.TIN_PLATE.get());
        put(map, ICTags.PLATES_BRONZE, ICItems.BRONZE_PLATE.get());
        put(map, ICTags.PLATES_LEAD, ICItems.LEAD_PLATE.get());
        put(map, ICTags.PLATES_STEEL, ICItems.STEEL_PLATE.get());
        put(map, ICTags.PLATES_LAPIS, ICItems.LAPIS_PLATE.get());
        put(map, ICTags.PLATES_OBSIDIAN, ICItems.OBSIDIAN_PLATE.get());
        put(map, ICTags.PLATES_CARBON, ICItems.CARBON_PLATE.get());
        put(map, ICTags.DUSTS_IRON, ICItems.IRON_DUST.get());
        put(map, ICTags.DUSTS_GOLD, ICItems.GOLD_DUST.get());
        put(map, ICTags.DUSTS_COPPER, ICItems.COPPER_DUST.get());
        put(map, ICTags.DUSTS_TIN, ICItems.TIN_DUST.get());
        put(map, ICTags.DUSTS_BRONZE, ICItems.BRONZE_DUST.get());
        put(map, ICTags.DUSTS_LEAD, ICItems.LEAD_DUST.get());
        put(map, ICTags.DUSTS_SILVER, ICItems.SILVER_DUST.get());
        put(map, ICTags.DUSTS_COAL, ICItems.COAL_DUST.get());
        put(map, ICTags.DUSTS_CHARCOAL, ICItems.CHARCOAL_DUST.get());
        put(map, ICTags.DUSTS_CLAY, ICItems.CLAY_DUST.get());
        put(map, ICTags.DUSTS_DIAMOND, ICItems.DIAMOND_DUST.get());
        put(map, ICTags.DUSTS_ENERGIUM, ICItems.ENERGIUM_DUST.get());
        put(map, ICTags.DUSTS_LAPIS, ICItems.LAPIS_DUST.get());
        put(map, ICTags.DUSTS_LITHIUM, ICItems.LITHIUM_DUST.get());
        put(map, ICTags.DUSTS_OBSIDIAN, ICItems.OBSIDIAN_DUST.get());
        put(map, ICTags.DUSTS_SILICON_DIOXIDE, ICItems.SILICON_DIOXIDE_DUST.get());
        put(map, ICTags.DUSTS_STONE, ICItems.STONE_DUST.get());
        put(map, ICTags.DUSTS_SULFUR, ICItems.SULFUR_DUST.get());
        put(map, ICTags.DUSTS_NETHERRACK, ICItems.GROUND_NETHERRACK.get());
        put(map, ICTags.DUSTS_ENDER_PEARL, ICItems.ENDER_PEARL_DUST.get());
        put(map, ICTags.CIRCUITS_BASIC, ICItems.ELECTRONIC_CIRCUIT.get());
        put(map, ICTags.CIRCUITS_ADVANCED, ICItems.ADVANCED_CIRCUIT.get());
        put(map, ICTags.RUBBERS, ICItems.RUBBER.get());
        put(map, ICTags.CROPS_HOPS, ICItems.HOPS.get());
        put(map, ICTags.DENSE_PLATES_IRON, ICItems.DENSE_IRON_PLATE.get());
        put(map, ICTags.DENSE_PLATES_GOLD, ICItems.DENSE_GOLD_PLATE.get());
        put(map, ICTags.DENSE_PLATES_COPPER, ICItems.DENSE_COPPER_PLATE.get());
        put(map, ICTags.DENSE_PLATES_TIN, ICItems.DENSE_TIN_PLATE.get());
        put(map, ICTags.DENSE_PLATES_BRONZE, ICItems.DENSE_BRONZE_PLATE.get());
        put(map, ICTags.DENSE_PLATES_LEAD, ICItems.DENSE_LEAD_PLATE.get());
        put(map, ICTags.DENSE_PLATES_STEEL, ICItems.DENSE_STEEL_PLATE.get());
        put(map, ICTags.DENSE_PLATES_LAPIS, ICItems.DENSE_LAPIS_PLATE.get());
        put(map, ICTags.DENSE_PLATES_OBSIDIAN, ICItems.DENSE_OBSIDIAN_PLATE.get());
        put(map, ICTags.SMALL_DUSTS_IRON, ICItems.SMALL_IRON_DUST.get());
        put(map, ICTags.SMALL_DUSTS_GOLD, ICItems.SMALL_GOLD_DUST.get());
        put(map, ICTags.SMALL_DUSTS_COPPER, ICItems.SMALL_COPPER_DUST.get());
        put(map, ICTags.SMALL_DUSTS_TIN, ICItems.SMALL_TIN_DUST.get());
        put(map, ICTags.SMALL_DUSTS_BRONZE, ICItems.SMALL_BRONZE_DUST.get());
        put(map, ICTags.SMALL_DUSTS_LEAD, ICItems.SMALL_LEAD_DUST.get());
        put(map, ICTags.SMALL_DUSTS_SILVER, ICItems.SMALL_SILVER_DUST.get());
        put(map, ICTags.SMALL_DUSTS_LAPIS, ICItems.SMALL_LAPIS_DUST.get());
        put(map, ICTags.SMALL_DUSTS_LITHIUM, ICItems.SMALL_LITHIUM_DUST.get());
        put(map, ICTags.SMALL_DUSTS_OBSIDIAN, ICItems.SMALL_OBSIDIAN_DUST.get());
        put(map, ICTags.SMALL_DUSTS_SULFUR, ICItems.SMALL_SULFUR_DUST.get());
        put(map, ICTags.SMALL_DUSTS_DIAMOND, ICItems.SMALL_DIAMOND_DUST.get());
        put(map, ICTags.CRUSHED_ORES_IRON, ICItems.CRUSHED_IRON.get());
        put(map, ICTags.CRUSHED_ORES_GOLD, ICItems.CRUSHED_GOLD.get());
        put(map, ICTags.CRUSHED_ORES_COPPER, ICItems.CRUSHED_COPPER.get());
        put(map, ICTags.CRUSHED_ORES_TIN, ICItems.CRUSHED_TIN.get());
        put(map, ICTags.CRUSHED_ORES_LEAD, ICItems.CRUSHED_LEAD.get());
        put(map, ICTags.CRUSHED_ORES_SILVER, ICItems.CRUSHED_SILVER.get());
        put(map, ICTags.CRUSHED_ORES_URANIUM, ICItems.CRUSHED_URANIUM.get());
        put(map, ICTags.PURIFIED_CRUSHED_ORES_IRON, ICItems.PURIFIED_CRUSHED_IRON.get());
        put(map, ICTags.PURIFIED_CRUSHED_ORES_GOLD, ICItems.PURIFIED_CRUSHED_GOLD.get());
        put(map, ICTags.PURIFIED_CRUSHED_ORES_COPPER, ICItems.PURIFIED_CRUSHED_COPPER.get());
        put(map, ICTags.PURIFIED_CRUSHED_ORES_TIN, ICItems.PURIFIED_CRUSHED_TIN.get());
        put(map, ICTags.PURIFIED_CRUSHED_ORES_LEAD, ICItems.PURIFIED_CRUSHED_LEAD.get());
        put(map, ICTags.PURIFIED_CRUSHED_ORES_SILVER, ICItems.PURIFIED_CRUSHED_SILVER.get());
        put(map, ICTags.PURIFIED_CRUSHED_ORES_URANIUM, ICItems.PURIFIED_CRUSHED_URANIUM.get());
        put(map, ICTags.CASINGS_IRON, ICItems.IRON_CASING.get());
        put(map, ICTags.CASINGS_BRONZE, ICItems.BRONZE_CASING.get());
        put(map, ICTags.CASINGS_TIN, ICItems.TIN_CASING.get());
        put(map, ICTags.CASINGS_COPPER, ICItems.COPPER_CASING.get());
        put(map, ICTags.CASINGS_GOLD, ICItems.GOLD_CASING.get());
        put(map, ICTags.CASINGS_LEAD, ICItems.LEAD_CASING.get());
        put(map, ICTags.CASINGS_STEEL, ICItems.STEEL_CASING.get());
        vanilla(map);
        return map;
    }

    static void check(String id, Recipe<?> recipe, HolderLookup.Provider registries) {
        if (recipe instanceof ProcessingRecipe processing && processing.kind() == ProcessingKind.RECYCLING) return;
        if (byId == null) {
            byId = new HashMap<>();
            preferred().forEach((item, tag) -> byId.put(BuiltInRegistries.ITEM.getKey(item).toString(), tag));
        }
        JsonObject json = Recipe.CODEC.encodeStart(registries.createSerializationContext(JsonOps.INSTANCE), recipe)
                .getOrThrow().getAsJsonObject();
        for (String field : INGREDIENT_FIELDS) if (json.has(field)) scan(json.get(field), id);
    }

    static void assertClean() {
        if (!VIOLATIONS.isEmpty()) throw new IllegalStateException("Recipe ingredients must use tags:\n" + String.join("\n", VIOLATIONS));
    }

    // 1.21.1 tag objects hold ids
    private static void scan(JsonElement json, String id) {
        if (json.isJsonArray()) json.getAsJsonArray().forEach(element -> scan(element, id));
        else if (json.isJsonObject()) json.getAsJsonObject().entrySet().stream()
                .filter(entry -> !entry.getKey().equals("tag")).forEach(entry -> scan(entry.getValue(), id));
        else if (json.isJsonPrimitive() && byId.containsKey(json.getAsString()))
            VIOLATIONS.add(id + ": " + json.getAsString() + " -> #" + byId.get(json.getAsString()).location());
    }

    private static void vanilla(Map<Item, TagKey<Item>> map) {
        put(map, Tags.Items.INGOTS_IRON, Items.IRON_INGOT);
        put(map, Tags.Items.INGOTS_GOLD, Items.GOLD_INGOT);
        put(map, Tags.Items.INGOTS_COPPER, Items.COPPER_INGOT);
        put(map, Tags.Items.INGOTS_NETHERITE, Items.NETHERITE_INGOT);
        put(map, Tags.Items.NUGGETS_IRON, Items.IRON_NUGGET);
        put(map, Tags.Items.NUGGETS_GOLD, Items.GOLD_NUGGET);
        put(map, Tags.Items.RAW_MATERIALS_IRON, Items.RAW_IRON);
        put(map, Tags.Items.RAW_MATERIALS_GOLD, Items.RAW_GOLD);
        put(map, Tags.Items.RAW_MATERIALS_COPPER, Items.RAW_COPPER);
        put(map, Tags.Items.ORES_IRON, Items.IRON_ORE, Items.DEEPSLATE_IRON_ORE);
        put(map, Tags.Items.ORES_GOLD, Items.GOLD_ORE, Items.DEEPSLATE_GOLD_ORE);
        put(map, Tags.Items.ORES_COPPER, Items.COPPER_ORE, Items.DEEPSLATE_COPPER_ORE);
        put(map, Tags.Items.ORES_COAL, Items.COAL_ORE, Items.DEEPSLATE_COAL_ORE);
        put(map, Tags.Items.ORES_REDSTONE, Items.REDSTONE_ORE, Items.DEEPSLATE_REDSTONE_ORE);
        put(map, Tags.Items.ORES_QUARTZ, Items.NETHER_QUARTZ_ORE);
        put(map, Tags.Items.STORAGE_BLOCKS_IRON, Items.IRON_BLOCK);
        put(map, Tags.Items.STORAGE_BLOCKS_GOLD, Items.GOLD_BLOCK);
        put(map, Tags.Items.STORAGE_BLOCKS_COPPER, Items.COPPER_BLOCK);
        put(map, Tags.Items.STORAGE_BLOCKS_COAL, Items.COAL_BLOCK);
        put(map, Tags.Items.STORAGE_BLOCKS_LAPIS, Items.LAPIS_BLOCK);
        put(map, Tags.Items.STORAGE_BLOCKS_REDSTONE, Items.REDSTONE_BLOCK);
        put(map, Tags.Items.STORAGE_BLOCKS_DIAMOND, Items.DIAMOND_BLOCK);
        put(map, Tags.Items.STORAGE_BLOCKS_WHEAT, Items.HAY_BLOCK);
        put(map, Tags.Items.GEMS_DIAMOND, Items.DIAMOND);
        put(map, Tags.Items.GEMS_EMERALD, Items.EMERALD);
        put(map, Tags.Items.GEMS_LAPIS, Items.LAPIS_LAZULI);
        put(map, Tags.Items.GEMS_QUARTZ, Items.QUARTZ);
        put(map, Tags.Items.GEMS_AMETHYST, Items.AMETHYST_SHARD);
        put(map, Tags.Items.DUSTS_REDSTONE, Items.REDSTONE);
        put(map, Tags.Items.DUSTS_GLOWSTONE, Items.GLOWSTONE_DUST);
        put(map, Tags.Items.DYES_ORANGE, Items.ORANGE_DYE);
        put(map, Tags.Items.TOOLS_SHEAR, Items.SHEARS);
        put(map, Tags.Items.BUCKETS_MILK, Items.MILK_BUCKET);
        put(map, Tags.Items.FERTILIZERS, Items.BONE_MEAL);
        put(map, Tags.Items.RODS_WOODEN, Items.STICK);
        put(map, Tags.Items.RODS_BLAZE, Items.BLAZE_ROD);
        put(map, Tags.Items.STRINGS, Items.STRING);
        put(map, Tags.Items.LEATHERS, Items.LEATHER);
        put(map, Tags.Items.FEATHERS, Items.FEATHER);
        put(map, Tags.Items.BONES, Items.BONE);
        put(map, Tags.Items.GUNPOWDERS, Items.GUNPOWDER);
        put(map, Tags.Items.SLIME_BALLS, Items.SLIME_BALL);
        put(map, Tags.Items.ENDER_PEARLS, Items.ENDER_PEARL);
        put(map, Tags.Items.GLASS_BLOCKS_COLORLESS, Items.GLASS);
        put(map, Tags.Items.GLASS_PANES_COLORLESS, Items.GLASS_PANE);
        put(map, Tags.Items.COBBLESTONES_NORMAL, Items.COBBLESTONE);
        put(map, Tags.Items.STONES, Items.STONE);
        put(map, Tags.Items.OBSIDIANS_NORMAL, Items.OBSIDIAN);
        put(map, Tags.Items.SANDS_COLORLESS, Items.SAND);
        put(map, Tags.Items.SANDS_RED, Items.RED_SAND);
        put(map, Tags.Items.GRAVELS, Items.GRAVEL);
        put(map, Tags.Items.NETHERRACKS, Items.NETHERRACK);
        put(map, Tags.Items.SANDSTONE_UNCOLORED_BLOCKS, Items.SANDSTONE);
        put(map, Tags.Items.SANDSTONE_RED_BLOCKS, Items.RED_SANDSTONE);
        put(map, Tags.Items.CHESTS_WOODEN, Items.CHEST);
        put(map, Tags.Items.PLAYER_WORKSTATIONS_FURNACES, Items.FURNACE);
        put(map, Tags.Items.PLAYER_WORKSTATIONS_CRAFTING_TABLES, Items.CRAFTING_TABLE);
        put(map, Tags.Items.BUCKETS_EMPTY, Items.BUCKET);
        put(map, Tags.Items.BUCKETS_WATER, Items.WATER_BUCKET);
        put(map, Tags.Items.BUCKETS_LAVA, Items.LAVA_BUCKET);
        put(map, Tags.Items.CROPS_WHEAT, Items.WHEAT);
        put(map, Tags.Items.CROPS_BEETROOT, Items.BEETROOT);
        put(map, Tags.Items.CROPS_CACTUS, Items.CACTUS);
        put(map, Tags.Items.CROPS_CARROT, Items.CARROT);
        put(map, Tags.Items.CROPS_COCOA_BEAN, Items.COCOA_BEANS);
        put(map, Tags.Items.CROPS_MELON, Items.MELON);
        put(map, Tags.Items.CROPS_NETHER_WART, Items.NETHER_WART);
        put(map, Tags.Items.CROPS_POTATO, Items.POTATO);
        put(map, Tags.Items.CROPS_PUMPKIN, Items.PUMPKIN);
        put(map, Tags.Items.CROPS_SUGAR_CANE, Items.SUGAR_CANE);
        put(map, Tags.Items.SEEDS_WHEAT, Items.WHEAT_SEEDS);
        put(map, Tags.Items.SEEDS_MELON, Items.MELON_SEEDS);
        put(map, Tags.Items.SEEDS_PUMPKIN, Items.PUMPKIN_SEEDS);
        put(map, Tags.Items.SEEDS_BEETROOT, Items.BEETROOT_SEEDS);
        put(map, Tags.Items.FOODS_BREAD, Items.BREAD);
        put(map, Tags.Items.FOODS_COOKIE, Items.COOKIE);
        put(map, Tags.Items.FOODS_PIE, Items.PUMPKIN_PIE);
        put(map, Tags.Items.FOODS_RAW_FISH, Items.COD, Items.SALMON, Items.TROPICAL_FISH);
        put(map, Tags.Items.FOODS_COOKED_FISH, Items.COOKED_COD, Items.COOKED_SALMON);
        for (String wood : WOODS) {
            put(map, logs(wood), item(wood + "_log"), item(wood + "_wood"));
            put(map, ItemTags.PLANKS, item(wood + "_planks"));
        }
    }

    private static Item item(String path) {
        return BuiltInRegistries.ITEM.getValue(Identifier.withDefaultNamespace(path));
    }

    private static void put(Map<Item, TagKey<Item>> map, TagKey<Item> tag, ItemLike... items) {
        for (ItemLike item : items) map.put(item.asItem(), tag);
    }

    private RecipeTags() {
    }
}
