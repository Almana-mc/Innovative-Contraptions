package me.almana.innovativecontraptions.registry;

import me.almana.innovativecontraptions.InnovativeContraptions;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class ICTags {
    public static final TagKey<Item> INGOTS_TIN = common("ingots/tin");
    public static final TagKey<Item> INGOTS_LEAD = common("ingots/lead");
    public static final TagKey<Item> INGOTS_SILVER = common("ingots/silver");
    public static final TagKey<Item> INGOTS_BRONZE = common("ingots/bronze");
    public static final TagKey<Item> INGOTS_STEEL = common("ingots/steel");
    public static final TagKey<Item> ORES_TIN = common("ores/tin");
    public static final TagKey<Item> ORES_LEAD = common("ores/lead");
    public static final TagKey<Item> ORES_SILVER = common("ores/silver");
    public static final TagKey<Item> ORES_URANIUM = common("ores/uranium");
    public static final TagKey<Item> RAW_MATERIALS_TIN = common("raw_materials/tin");
    public static final TagKey<Item> RAW_MATERIALS_LEAD = common("raw_materials/lead");
    public static final TagKey<Item> STORAGE_BLOCKS_TIN = common("storage_blocks/tin");
    public static final TagKey<Item> STORAGE_BLOCKS_BRONZE = common("storage_blocks/bronze");
    public static final TagKey<Item> STORAGE_BLOCKS_SILVER = common("storage_blocks/silver");
    public static final TagKey<Item> STORAGE_BLOCKS_LEAD = common("storage_blocks/lead");
    public static final TagKey<Item> STORAGE_BLOCKS_STEEL = common("storage_blocks/steel");
    public static final TagKey<Item> STORAGE_BLOCKS_URANIUM = common("storage_blocks/uranium");
    public static final TagKey<Item> STORAGE_BLOCKS_CHARCOAL = common("storage_blocks/charcoal");
    public static final TagKey<Item> PLATES_IRON = common("plates/iron");
    public static final TagKey<Item> PLATES_GOLD = common("plates/gold");
    public static final TagKey<Item> PLATES_COPPER = common("plates/copper");
    public static final TagKey<Item> PLATES_TIN = common("plates/tin");
    public static final TagKey<Item> PLATES_BRONZE = common("plates/bronze");
    public static final TagKey<Item> PLATES_LEAD = common("plates/lead");
    public static final TagKey<Item> PLATES_STEEL = common("plates/steel");
    public static final TagKey<Item> PLATES_LAPIS = common("plates/lapis");
    public static final TagKey<Item> PLATES_OBSIDIAN = common("plates/obsidian");
    public static final TagKey<Item> PLATES_CARBON = common("plates/carbon");
    public static final TagKey<Item> DUSTS_IRON = common("dusts/iron");
    public static final TagKey<Item> DUSTS_GOLD = common("dusts/gold");
    public static final TagKey<Item> DUSTS_COPPER = common("dusts/copper");
    public static final TagKey<Item> DUSTS_TIN = common("dusts/tin");
    public static final TagKey<Item> DUSTS_BRONZE = common("dusts/bronze");
    public static final TagKey<Item> DUSTS_LEAD = common("dusts/lead");
    public static final TagKey<Item> DUSTS_SILVER = common("dusts/silver");
    public static final TagKey<Item> DUSTS_COAL = common("dusts/coal");
    public static final TagKey<Item> DUSTS_CHARCOAL = common("dusts/charcoal");
    public static final TagKey<Item> DUSTS_CLAY = common("dusts/clay");
    public static final TagKey<Item> DUSTS_DIAMOND = common("dusts/diamond");
    public static final TagKey<Item> DUSTS_ENERGIUM = common("dusts/energium");
    public static final TagKey<Item> DUSTS_LAPIS = common("dusts/lapis");
    public static final TagKey<Item> DUSTS_LITHIUM = common("dusts/lithium");
    public static final TagKey<Item> DUSTS_OBSIDIAN = common("dusts/obsidian");
    public static final TagKey<Item> DUSTS_SILICON_DIOXIDE = common("dusts/silicon_dioxide");
    public static final TagKey<Item> DUSTS_STONE = common("dusts/stone");
    public static final TagKey<Item> DUSTS_SULFUR = common("dusts/sulfur");
    public static final TagKey<Item> DUSTS_NETHERRACK = common("dusts/netherrack");
    public static final TagKey<Item> DUSTS_ENDER_PEARL = common("dusts/ender_pearl");
    public static final TagKey<Item> CIRCUITS_BASIC = common("circuits/basic");
    public static final TagKey<Item> CIRCUITS_ADVANCED = common("circuits/advanced");
    public static final TagKey<Item> RUBBERS = common("rubbers");
    public static final TagKey<Item> CROPS_HOPS = common("crops/hops");
    public static final TagKey<Item> DENSE_PLATES_IRON = mod("dense_plates/iron");
    public static final TagKey<Item> DENSE_PLATES_GOLD = mod("dense_plates/gold");
    public static final TagKey<Item> DENSE_PLATES_COPPER = mod("dense_plates/copper");
    public static final TagKey<Item> DENSE_PLATES_TIN = mod("dense_plates/tin");
    public static final TagKey<Item> DENSE_PLATES_BRONZE = mod("dense_plates/bronze");
    public static final TagKey<Item> DENSE_PLATES_LEAD = mod("dense_plates/lead");
    public static final TagKey<Item> DENSE_PLATES_STEEL = mod("dense_plates/steel");
    public static final TagKey<Item> DENSE_PLATES_LAPIS = mod("dense_plates/lapis");
    public static final TagKey<Item> DENSE_PLATES_OBSIDIAN = mod("dense_plates/obsidian");
    public static final TagKey<Item> SMALL_DUSTS_IRON = mod("small_dusts/iron");
    public static final TagKey<Item> SMALL_DUSTS_GOLD = mod("small_dusts/gold");
    public static final TagKey<Item> SMALL_DUSTS_COPPER = mod("small_dusts/copper");
    public static final TagKey<Item> SMALL_DUSTS_TIN = mod("small_dusts/tin");
    public static final TagKey<Item> SMALL_DUSTS_BRONZE = mod("small_dusts/bronze");
    public static final TagKey<Item> SMALL_DUSTS_LEAD = mod("small_dusts/lead");
    public static final TagKey<Item> SMALL_DUSTS_SILVER = mod("small_dusts/silver");
    public static final TagKey<Item> SMALL_DUSTS_LAPIS = mod("small_dusts/lapis");
    public static final TagKey<Item> SMALL_DUSTS_LITHIUM = mod("small_dusts/lithium");
    public static final TagKey<Item> SMALL_DUSTS_OBSIDIAN = mod("small_dusts/obsidian");
    public static final TagKey<Item> SMALL_DUSTS_SULFUR = mod("small_dusts/sulfur");
    public static final TagKey<Item> SMALL_DUSTS_DIAMOND = mod("small_dusts/diamond");
    public static final TagKey<Item> CRUSHED_ORES_IRON = mod("crushed_ores/iron");
    public static final TagKey<Item> CRUSHED_ORES_GOLD = mod("crushed_ores/gold");
    public static final TagKey<Item> CRUSHED_ORES_COPPER = mod("crushed_ores/copper");
    public static final TagKey<Item> CRUSHED_ORES_TIN = mod("crushed_ores/tin");
    public static final TagKey<Item> CRUSHED_ORES_LEAD = mod("crushed_ores/lead");
    public static final TagKey<Item> CRUSHED_ORES_SILVER = mod("crushed_ores/silver");
    public static final TagKey<Item> CRUSHED_ORES_URANIUM = mod("crushed_ores/uranium");
    public static final TagKey<Item> PURIFIED_CRUSHED_ORES_IRON = mod("purified_crushed_ores/iron");
    public static final TagKey<Item> PURIFIED_CRUSHED_ORES_GOLD = mod("purified_crushed_ores/gold");
    public static final TagKey<Item> PURIFIED_CRUSHED_ORES_COPPER = mod("purified_crushed_ores/copper");
    public static final TagKey<Item> PURIFIED_CRUSHED_ORES_TIN = mod("purified_crushed_ores/tin");
    public static final TagKey<Item> PURIFIED_CRUSHED_ORES_LEAD = mod("purified_crushed_ores/lead");
    public static final TagKey<Item> PURIFIED_CRUSHED_ORES_SILVER = mod("purified_crushed_ores/silver");
    public static final TagKey<Item> PURIFIED_CRUSHED_ORES_URANIUM = mod("purified_crushed_ores/uranium");
    public static final TagKey<Item> CASINGS_IRON = mod("casings/iron");
    public static final TagKey<Item> CASINGS_BRONZE = mod("casings/bronze");
    public static final TagKey<Item> CASINGS_TIN = mod("casings/tin");
    public static final TagKey<Item> CASINGS_COPPER = mod("casings/copper");
    public static final TagKey<Item> CASINGS_GOLD = mod("casings/gold");
    public static final TagKey<Item> CASINGS_LEAD = mod("casings/lead");
    public static final TagKey<Item> CASINGS_STEEL = mod("casings/steel");
    public static final TagKey<Item> CANNING_CONTAINERS = mod("canning_containers");

    private static TagKey<Item> common(String path) {
        return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", path));
    }

    private static TagKey<Item> mod(String path) {
        return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, path));
    }

    private ICTags() {
    }
}
