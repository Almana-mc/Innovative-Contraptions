package me.almana.innovativecontraptions.registry;

import me.almana.innovativecontraptions.InnovativeContraptions;
import me.almana.innovativecontraptions.content.agriculture.*;
import me.almana.innovativecontraptions.content.equipment.*;
import me.almana.innovativecontraptions.content.fluid.BiogasContent;
import me.almana.innovativecontraptions.content.fluid.CoolantContent;
import me.almana.innovativecontraptions.content.gathering.OreProbeItem;
import me.almana.innovativecontraptions.content.matter.MatterContent;
import me.almana.innovativecontraptions.content.matter.MatterMemoryItem;
import me.almana.innovativecontraptions.content.processing.DescribedItem;
import me.almana.innovativecontraptions.content.reactor.ReactorComponentItem;
import me.almana.innovativecontraptions.content.reactor.ReactorPart;
import java.util.function.Supplier;
import me.almana.innovativecontraptions.content.processing.DirectionalUpgradeItem;
import me.almana.innovativecontraptions.content.processing.ProcessingHeat;
import me.almana.innovativecontraptions.content.processing.ProcessingUpgrades;
import net.minecraft.world.entity.EquipmentSlot;
import me.almana.innovativecontraptions.content.energy.BatteryItem;
import me.almana.innovativecontraptions.content.energy.ChargingBatteryItem;
import me.almana.innovativecontraptions.content.fluid.FluidCellItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ShovelItem;
//? if <26.1 {
/*import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.SwordItem;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
*///?}
import net.minecraft.world.level.material.Fluids;
import me.almana.innovativecontraptions.content.energy.EuTier;
import me.almana.innovativecontraptions.content.energy.cable.CableBlockItem;
import me.almana.innovativecontraptions.content.energy.debug.CreativeSourceBlockItem;
import me.almana.innovativecontraptions.content.energy.debug.EnergyStorageBlockItem;
import me.almana.innovativecontraptions.content.generator.RotorItem;
import me.almana.innovativecontraptions.content.generator.RotorTier;
import me.almana.innovativecontraptions.content.rubber.TreeTapItem;
import me.almana.innovativecontraptions.content.tool.ElectricWrenchItem;
import me.almana.innovativecontraptions.content.tool.FormingTool;
import me.almana.innovativecontraptions.content.tool.ToolboxItem;
import me.almana.innovativecontraptions.content.tool.WrenchItem;
//? if <26.1
//import me.almana.innovativecontraptions.compat.ItemRegister;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ICItems {
    //? if >=26.1 {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(InnovativeContraptions.MODID);
    //?} else
    //public static final ItemRegister ITEMS = new ItemRegister(InnovativeContraptions.MODID);

    public static final DeferredItem<BlockItem> RUBBER_LOG = ITEMS.registerSimpleBlockItem(ICBlocks.RUBBER_LOG);
    public static final DeferredItem<BlockItem> RUBBER_WOOD = ITEMS.registerSimpleBlockItem(ICBlocks.RUBBER_WOOD);
    public static final DeferredItem<BlockItem> STRIPPED_RUBBER_LOG = ITEMS.registerSimpleBlockItem(ICBlocks.STRIPPED_RUBBER_LOG);
    public static final DeferredItem<BlockItem> STRIPPED_RUBBER_WOOD = ITEMS.registerSimpleBlockItem(ICBlocks.STRIPPED_RUBBER_WOOD);
    public static final DeferredItem<BlockItem> RUBBER_LEAVES = ITEMS.registerSimpleBlockItem(ICBlocks.RUBBER_LEAVES);
    public static final DeferredItem<BlockItem> RUBBER_SAPLING = ITEMS.registerSimpleBlockItem(ICBlocks.RUBBER_SAPLING);

    public static final DeferredItem<BlockItem> TIN_ORE = ITEMS.registerSimpleBlockItem(ICBlocks.TIN_ORE);
    public static final DeferredItem<BlockItem> LEAD_ORE = ITEMS.registerSimpleBlockItem(ICBlocks.LEAD_ORE);
    public static final DeferredItem<BlockItem> SILVER_ORE = ITEMS.registerSimpleBlockItem(ICBlocks.SILVER_ORE);
    public static final DeferredItem<BlockItem> URANIUM_ORE = ITEMS.registerSimpleBlockItem(ICBlocks.URANIUM_ORE);
    public static final DeferredItem<BlockItem> IRIDIUM_ORE = ITEMS.registerSimpleBlockItem(ICBlocks.IRIDIUM_ORE);

    public static final DeferredItem<BlockItem> TIN_BLOCK = ITEMS.registerSimpleBlockItem(ICBlocks.TIN_BLOCK);
    public static final DeferredItem<BlockItem> BRONZE_BLOCK = ITEMS.registerSimpleBlockItem(ICBlocks.BRONZE_BLOCK);
    public static final DeferredItem<BlockItem> LEAD_BLOCK = ITEMS.registerSimpleBlockItem(ICBlocks.LEAD_BLOCK);
    public static final DeferredItem<BlockItem> SILVER_BLOCK = ITEMS.registerSimpleBlockItem(ICBlocks.SILVER_BLOCK);
    public static final DeferredItem<BlockItem> STEEL_BLOCK = ITEMS.registerSimpleBlockItem(ICBlocks.STEEL_BLOCK);
    public static final DeferredItem<BlockItem> URANIUM_BLOCK = ITEMS.registerSimpleBlockItem(ICBlocks.URANIUM_BLOCK);
    public static final DeferredItem<BlockItem> CHARCOAL_BLOCK = ITEMS.registerSimpleBlockItem(ICBlocks.CHARCOAL_BLOCK);

    public static final DeferredItem<BlockItem> IRON_FURNACE = ITEMS.registerSimpleBlockItem(ICBlocks.IRON_FURNACE);
    public static final DeferredItem<BlockItem> ELECTRIC_FURNACE = ITEMS.registerSimpleBlockItem(ICBlocks.ELECTRIC_FURNACE);
    public static final DeferredItem<BlockItem> MACERATOR = ITEMS.registerSimpleBlockItem(ICBlocks.MACERATOR);
    public static final DeferredItem<BlockItem> EXTRACTOR = ITEMS.registerSimpleBlockItem(ICBlocks.EXTRACTOR);
    public static final DeferredItem<BlockItem> COMPRESSOR = ITEMS.registerSimpleBlockItem(ICBlocks.COMPRESSOR);
    public static final DeferredItem<BlockItem> RECYCLER = ITEMS.registerSimpleBlockItem(ICBlocks.RECYCLER);
    public static final DeferredItem<BlockItem> CANNING_MACHINE = ITEMS.registerSimpleBlockItem(ICBlocks.CANNING_MACHINE);
    public static final DeferredItem<BlockItem> SOLID_CANNING_MACHINE = ITEMS.registerSimpleBlockItem(ICBlocks.SOLID_CANNING_MACHINE);

    public static final DeferredItem<BlockItem> BLAST_FURNACE = ITEMS.registerSimpleBlockItem(ICBlocks.BLAST_FURNACE);
    public static final DeferredItem<BlockItem> ELECTRIC_HEATER = ITEMS.registerSimpleBlockItem(ICBlocks.ELECTRIC_HEATER);
    public static final DeferredItem<BlockItem> LIQUID_HEAT_EXCHANGER = ITEMS.registerSimpleBlockItem(ICBlocks.LIQUID_HEAT_EXCHANGER);
    public static final DeferredItem<BlockItem> INDUCTION_FURNACE = ITEMS.registerSimpleBlockItem(ICBlocks.INDUCTION_FURNACE);
    public static final DeferredItem<BlockItem> METAL_FORMER = ITEMS.registerSimpleBlockItem(ICBlocks.METAL_FORMER);
    public static final DeferredItem<BlockItem> ORE_WASHING_PLANT = ITEMS.registerSimpleBlockItem(ICBlocks.ORE_WASHING_PLANT);
    public static final DeferredItem<BlockItem> THERMAL_CENTRIFUGE = ITEMS.registerSimpleBlockItem(ICBlocks.THERMAL_CENTRIFUGE);

    public static final DeferredItem<BlockItem> GENERATOR = ITEMS.registerSimpleBlockItem(ICBlocks.GENERATOR);
    public static final DeferredItem<BlockItem> SOLAR_PANEL = ITEMS.registerSimpleBlockItem(ICBlocks.SOLAR_PANEL);
    public static final DeferredItem<BlockItem> WATER_MILL = ITEMS.registerSimpleBlockItem(ICBlocks.WATER_MILL);
    public static final DeferredItem<BlockItem> WINDMILL = ITEMS.registerSimpleBlockItem(ICBlocks.WINDMILL);
    public static final DeferredItem<BlockItem> KINETIC_WIND_TURBINE = ITEMS.registerSimpleBlockItem(ICBlocks.KINETIC_WIND_TURBINE);
    public static final DeferredItem<BlockItem> GEOTHERMAL_GENERATOR = ITEMS.registerSimpleBlockItem(ICBlocks.GEOTHERMAL_GENERATOR);
    public static final DeferredItem<BlockItem> KINETIC_GENERATOR = ITEMS.registerSimpleBlockItem(ICBlocks.KINETIC_GENERATOR);

    public static final DeferredItem<BlockItem> BATBOX_PAD = ITEMS.registerSimpleBlockItem(ICBlocks.BATBOX_PAD);
    public static final DeferredItem<BlockItem> CESU_PAD = ITEMS.registerSimpleBlockItem(ICBlocks.CESU_PAD);
    public static final DeferredItem<BlockItem> MFE_PAD = ITEMS.registerSimpleBlockItem(ICBlocks.MFE_PAD);
    public static final DeferredItem<BlockItem> MFSU_PAD = ITEMS.registerSimpleBlockItem(ICBlocks.MFSU_PAD);
    public static final DeferredItem<BlockItem> BATBOX = ITEMS.registerSimpleBlockItem(ICBlocks.BATBOX);
    public static final DeferredItem<BlockItem> CESU = ITEMS.registerSimpleBlockItem(ICBlocks.CESU);
    public static final DeferredItem<BlockItem> MFE = ITEMS.registerSimpleBlockItem(ICBlocks.MFE);
    public static final DeferredItem<BlockItem> MFSU = ITEMS.registerSimpleBlockItem(ICBlocks.MFSU);

    public static final DeferredItem<BlockItem> LV_TRANSFORMER = ITEMS.registerSimpleBlockItem(ICBlocks.LV_TRANSFORMER);
    public static final DeferredItem<BlockItem> MV_TRANSFORMER = ITEMS.registerSimpleBlockItem(ICBlocks.MV_TRANSFORMER);
    public static final DeferredItem<BlockItem> HV_TRANSFORMER = ITEMS.registerSimpleBlockItem(ICBlocks.HV_TRANSFORMER);
    public static final DeferredItem<BlockItem> EV_TRANSFORMER = ITEMS.registerSimpleBlockItem(ICBlocks.EV_TRANSFORMER);

    public static final DeferredItem<BlockItem> NUCLEAR_REACTOR = ITEMS.registerSimpleBlockItem(ICBlocks.NUCLEAR_REACTOR);
    public static final DeferredItem<BlockItem> REACTOR_CHAMBER = ITEMS.registerSimpleBlockItem(ICBlocks.REACTOR_CHAMBER);
    public static final DeferredItem<BlockItem> REACTOR_VESSEL = ITEMS.registerSimpleBlockItem(ICBlocks.REACTOR_VESSEL);
    public static final DeferredItem<BlockItem> REACTOR_ACCESS_HATCH = ITEMS.registerSimpleBlockItem(ICBlocks.REACTOR_ACCESS_HATCH);
    public static final DeferredItem<BlockItem> REACTOR_REDSTONE_PORT = ITEMS.registerSimpleBlockItem(ICBlocks.REACTOR_REDSTONE_PORT);
    public static final DeferredItem<BlockItem> REACTOR_FLUID_PORT = ITEMS.registerSimpleBlockItem(ICBlocks.REACTOR_FLUID_PORT);
    public static final DeferredItem<BlockItem> RSH_COOLANT_INJECTOR = ITEMS.registerSimpleBlockItem(ICBlocks.RSH_COOLANT_INJECTOR);
    public static final DeferredItem<BlockItem> LZH_COOLANT_INJECTOR = ITEMS.registerSimpleBlockItem(ICBlocks.LZH_COOLANT_INJECTOR);
    public static final DeferredItem<BlockItem> COMPACT_ITEM_BUFFER = ITEMS.registerSimpleBlockItem(ICBlocks.COMPACT_ITEM_BUFFER);

    public static final DeferredItem<BlockItem> MATTER_FABRICATOR = ITEMS.registerSimpleBlockItem(ICBlocks.MATTER_FABRICATOR);
    public static final DeferredItem<BlockItem> REPLICATOR = ITEMS.registerSimpleBlockItem(ICBlocks.REPLICATOR);
    public static final DeferredItem<BlockItem> PATTERN_STORAGE = ITEMS.registerSimpleBlockItem(ICBlocks.PATTERN_STORAGE);
    public static final DeferredItem<BlockItem> MINER = ITEMS.registerSimpleBlockItem(ICBlocks.MINER);
    public static final DeferredItem<BlockItem> ADVANCED_MINER = ITEMS.registerSimpleBlockItem(ICBlocks.ADVANCED_MINER);
    public static final DeferredItem<BlockItem> CROP_HARVESTER = ITEMS.registerSimpleBlockItem(ICBlocks.CROP_HARVESTER);
    public static final DeferredItem<BlockItem> PUMP = ITEMS.registerSimpleBlockItem(ICBlocks.PUMP);

    public static final DeferredItem<TreeTapItem> TREE_TAP = ITEMS.registerItem(
            "tree_tap",
            TreeTapItem::new,
            () -> new Item.Properties().durability(16)
    );
    public static final DeferredItem<FormingTool> FORGE_HAMMER = ITEMS.registerItem(
            "forge_hammer",
            FormingTool::new,
            () -> new Item.Properties().durability(80)
    );
    public static final DeferredItem<FormingTool> CUTTER = ITEMS.registerItem(
            "cutter",
            FormingTool::new,
            () -> new Item.Properties().durability(60)
    );
    public static final DeferredItem<WrenchItem> WRENCH = ITEMS.registerItem(
            "wrench",
            WrenchItem::new,
            () -> new Item.Properties().durability(80)
    );

    public static final DeferredItem<Item> STICKY_RESIN = ITEMS.registerSimpleItem("sticky_resin");
    public static final DeferredItem<Item> RUBBER = ITEMS.registerSimpleItem("rubber");

    public static final DeferredItem<Item> TIN_INGOT = ITEMS.registerSimpleItem("tin_ingot");
    public static final DeferredItem<Item> LEAD_INGOT = ITEMS.registerSimpleItem("lead_ingot");
    public static final DeferredItem<Item> SILVER_INGOT = ITEMS.registerSimpleItem("silver_ingot");
    public static final DeferredItem<Item> BRONZE_INGOT = ITEMS.registerSimpleItem("bronze_ingot");
    public static final DeferredItem<Item> STEEL_INGOT = ITEMS.registerSimpleItem("steel_ingot");
    public static final DeferredItem<Item> ALLOY_INGOT = ITEMS.registerSimpleItem("alloy_ingot");
    public static final DeferredItem<Item> MIXED_METAL_INGOT = ITEMS.registerSimpleItem("mixed_metal_ingot");

    public static final DeferredItem<Item> RAW_TIN = ITEMS.registerSimpleItem("raw_tin");
    public static final DeferredItem<Item> RAW_LEAD = ITEMS.registerSimpleItem("raw_lead");

    public static final DeferredItem<Item> CRUSHED_IRON = ITEMS.registerSimpleItem("crushed_iron");
    public static final DeferredItem<Item> CRUSHED_GOLD = ITEMS.registerSimpleItem("crushed_gold");
    public static final DeferredItem<Item> CRUSHED_COPPER = ITEMS.registerSimpleItem("crushed_copper");
    public static final DeferredItem<Item> CRUSHED_TIN = ITEMS.registerSimpleItem("crushed_tin");
    public static final DeferredItem<Item> CRUSHED_LEAD = ITEMS.registerSimpleItem("crushed_lead");
    public static final DeferredItem<Item> PURIFIED_CRUSHED_IRON = ITEMS.registerSimpleItem("purified_crushed_iron");
    public static final DeferredItem<Item> PURIFIED_CRUSHED_GOLD = ITEMS.registerSimpleItem("purified_crushed_gold");
    public static final DeferredItem<Item> PURIFIED_CRUSHED_COPPER = ITEMS.registerSimpleItem("purified_crushed_copper");
    public static final DeferredItem<Item> PURIFIED_CRUSHED_TIN = ITEMS.registerSimpleItem("purified_crushed_tin");
    public static final DeferredItem<Item> PURIFIED_CRUSHED_LEAD = ITEMS.registerSimpleItem("purified_crushed_lead");

    public static final DeferredItem<Item> IRON_DUST = ITEMS.registerSimpleItem("iron_dust");
    public static final DeferredItem<Item> GOLD_DUST = ITEMS.registerSimpleItem("gold_dust");
    public static final DeferredItem<Item> COPPER_DUST = ITEMS.registerSimpleItem("copper_dust");
    public static final DeferredItem<Item> TIN_DUST = ITEMS.registerSimpleItem("tin_dust");
    public static final DeferredItem<Item> BRONZE_DUST = ITEMS.registerSimpleItem("bronze_dust");
    public static final DeferredItem<Item> LEAD_DUST = ITEMS.registerSimpleItem("lead_dust");
    public static final DeferredItem<Item> SILVER_DUST = ITEMS.registerSimpleItem("silver_dust");
    public static final DeferredItem<Item> COAL_DUST = ITEMS.registerSimpleItem("coal_dust");
    public static final DeferredItem<Item> CHARCOAL_DUST = ITEMS.registerSimpleItem("charcoal_dust");
    public static final DeferredItem<Item> COAL_FUEL_DUST = ITEMS.registerSimpleItem("coal_fuel_dust");
    public static final DeferredItem<Item> CLAY_DUST = ITEMS.registerSimpleItem("clay_dust");
    public static final DeferredItem<Item> DIAMOND_DUST = ITEMS.registerSimpleItem("diamond_dust");
    public static final DeferredItem<Item> ENERGIUM_DUST = ITEMS.registerSimpleItem("energium_dust");
    public static final DeferredItem<Item> LAPIS_DUST = ITEMS.registerSimpleItem("lapis_dust");
    public static final DeferredItem<Item> LITHIUM_DUST = ITEMS.registerSimpleItem("lithium_dust");
    public static final DeferredItem<Item> OBSIDIAN_DUST = ITEMS.registerSimpleItem("obsidian_dust");
    public static final DeferredItem<Item> GROUND_NETHERRACK = ITEMS.registerSimpleItem("ground_netherrack");
    public static final DeferredItem<Item> SILICON_DIOXIDE_DUST = ITEMS.registerSimpleItem("silicon_dioxide_dust");
    public static final DeferredItem<Item> STONE_DUST = ITEMS.registerSimpleItem("stone_dust");
    public static final DeferredItem<Item> SULFUR_DUST = ITEMS.registerSimpleItem("sulfur_dust");
    public static final DeferredItem<Item> HYDRATED_TIN_DUST = ITEMS.registerSimpleItem("hydrated_tin_dust");

    public static final DeferredItem<Item> SMALL_IRON_DUST = ITEMS.registerSimpleItem("small_iron_dust");
    public static final DeferredItem<Item> SMALL_GOLD_DUST = ITEMS.registerSimpleItem("small_gold_dust");
    public static final DeferredItem<Item> SMALL_COPPER_DUST = ITEMS.registerSimpleItem("small_copper_dust");
    public static final DeferredItem<Item> SMALL_TIN_DUST = ITEMS.registerSimpleItem("small_tin_dust");
    public static final DeferredItem<Item> SMALL_BRONZE_DUST = ITEMS.registerSimpleItem("small_bronze_dust");
    public static final DeferredItem<Item> SMALL_LEAD_DUST = ITEMS.registerSimpleItem("small_lead_dust");
    public static final DeferredItem<Item> SMALL_SILVER_DUST = ITEMS.registerSimpleItem("small_silver_dust");
    public static final DeferredItem<Item> SMALL_LAPIS_DUST = ITEMS.registerSimpleItem("small_lapis_dust");
    public static final DeferredItem<Item> SMALL_LITHIUM_DUST = ITEMS.registerSimpleItem("small_lithium_dust");
    public static final DeferredItem<Item> SMALL_OBSIDIAN_DUST = ITEMS.registerSimpleItem("small_obsidian_dust");
    public static final DeferredItem<Item> SMALL_SULFUR_DUST = ITEMS.registerSimpleItem("small_sulfur_dust");

    public static final DeferredItem<Item> IRON_PLATE = ITEMS.registerSimpleItem("iron_plate");
    public static final DeferredItem<Item> GOLD_PLATE = ITEMS.registerSimpleItem("gold_plate");
    public static final DeferredItem<Item> COPPER_PLATE = ITEMS.registerSimpleItem("copper_plate");
    public static final DeferredItem<Item> TIN_PLATE = ITEMS.registerSimpleItem("tin_plate");
    public static final DeferredItem<Item> BRONZE_PLATE = ITEMS.registerSimpleItem("bronze_plate");
    public static final DeferredItem<Item> LEAD_PLATE = ITEMS.registerSimpleItem("lead_plate");
    public static final DeferredItem<Item> STEEL_PLATE = ITEMS.registerSimpleItem("steel_plate");
    public static final DeferredItem<Item> LAPIS_PLATE = ITEMS.registerSimpleItem("lapis_plate");
    public static final DeferredItem<Item> OBSIDIAN_PLATE = ITEMS.registerSimpleItem("obsidian_plate");

    public static final DeferredItem<Item> DENSE_IRON_PLATE = ITEMS.registerSimpleItem("dense_iron_plate");
    public static final DeferredItem<Item> DENSE_GOLD_PLATE = ITEMS.registerSimpleItem("dense_gold_plate");
    public static final DeferredItem<Item> DENSE_COPPER_PLATE = ITEMS.registerSimpleItem("dense_copper_plate");
    public static final DeferredItem<Item> DENSE_TIN_PLATE = ITEMS.registerSimpleItem("dense_tin_plate");
    public static final DeferredItem<Item> DENSE_BRONZE_PLATE = ITEMS.registerSimpleItem("dense_bronze_plate");
    public static final DeferredItem<Item> DENSE_LEAD_PLATE = ITEMS.registerSimpleItem("dense_lead_plate");
    public static final DeferredItem<Item> DENSE_STEEL_PLATE = ITEMS.registerSimpleItem("dense_steel_plate");
    public static final DeferredItem<Item> DENSE_LAPIS_PLATE = ITEMS.registerSimpleItem("dense_lapis_plate");
    public static final DeferredItem<Item> DENSE_OBSIDIAN_PLATE = ITEMS.registerSimpleItem("dense_obsidian_plate");

    public static final DeferredItem<Item> IRON_CASING = ITEMS.registerSimpleItem("iron_casing");
    public static final DeferredItem<Item> BRONZE_CASING = ITEMS.registerSimpleItem("bronze_casing");
    public static final DeferredItem<Item> TIN_CASING = ITEMS.registerSimpleItem("tin_casing");
    public static final DeferredItem<Item> COPPER_CASING = ITEMS.registerSimpleItem("copper_casing");
    public static final DeferredItem<Item> GOLD_CASING = ITEMS.registerSimpleItem("gold_casing");
    public static final DeferredItem<Item> LEAD_CASING = ITEMS.registerSimpleItem("lead_casing");
    public static final DeferredItem<Item> STEEL_CASING = ITEMS.registerSimpleItem("steel_casing");

    public static final DeferredItem<CableBlockItem> TIN_CABLE = ITEMS.registerItem("tin_cable", props -> new CableBlockItem(ICBlocks.TIN_CABLE.get(), props), () -> new Item.Properties()/*? if >=26.1 {*/.useBlockDescriptionPrefix()/*?}*/);
    public static final DeferredItem<CableBlockItem> INSULATED_TIN_CABLE = ITEMS.registerItem("insulated_tin_cable", props -> new CableBlockItem(ICBlocks.INSULATED_TIN_CABLE.get(), props), () -> new Item.Properties()/*? if >=26.1 {*/.useBlockDescriptionPrefix()/*?}*/);
    public static final DeferredItem<CableBlockItem> COPPER_CABLE = ITEMS.registerItem("copper_cable", props -> new CableBlockItem(ICBlocks.COPPER_CABLE.get(), props), () -> new Item.Properties()/*? if >=26.1 {*/.useBlockDescriptionPrefix()/*?}*/);
    public static final DeferredItem<CableBlockItem> INSULATED_COPPER_CABLE = ITEMS.registerItem("insulated_copper_cable", props -> new CableBlockItem(ICBlocks.INSULATED_COPPER_CABLE.get(), props), () -> new Item.Properties()/*? if >=26.1 {*/.useBlockDescriptionPrefix()/*?}*/);
    public static final DeferredItem<CableBlockItem> GOLD_CABLE = ITEMS.registerItem("gold_cable", props -> new CableBlockItem(ICBlocks.GOLD_CABLE.get(), props), () -> new Item.Properties()/*? if >=26.1 {*/.useBlockDescriptionPrefix()/*?}*/);
    public static final DeferredItem<CableBlockItem> INSULATED_GOLD_CABLE = ITEMS.registerItem("insulated_gold_cable", props -> new CableBlockItem(ICBlocks.INSULATED_GOLD_CABLE.get(), props), () -> new Item.Properties()/*? if >=26.1 {*/.useBlockDescriptionPrefix()/*?}*/);
    public static final DeferredItem<CableBlockItem> DOUBLE_INSULATED_GOLD_CABLE = ITEMS.registerItem("double_insulated_gold_cable", props -> new CableBlockItem(ICBlocks.DOUBLE_INSULATED_GOLD_CABLE.get(), props), () -> new Item.Properties()/*? if >=26.1 {*/.useBlockDescriptionPrefix()/*?}*/);
    public static final DeferredItem<CableBlockItem> HV_CABLE = ITEMS.registerItem("hv_cable", props -> new CableBlockItem(ICBlocks.HV_CABLE.get(), props), () -> new Item.Properties()/*? if >=26.1 {*/.useBlockDescriptionPrefix()/*?}*/);
    public static final DeferredItem<CableBlockItem> INSULATED_HV_CABLE = ITEMS.registerItem("insulated_hv_cable", props -> new CableBlockItem(ICBlocks.INSULATED_HV_CABLE.get(), props), () -> new Item.Properties()/*? if >=26.1 {*/.useBlockDescriptionPrefix()/*?}*/);
    public static final DeferredItem<CableBlockItem> DOUBLE_INSULATED_HV_CABLE = ITEMS.registerItem("double_insulated_hv_cable", props -> new CableBlockItem(ICBlocks.DOUBLE_INSULATED_HV_CABLE.get(), props), () -> new Item.Properties()/*? if >=26.1 {*/.useBlockDescriptionPrefix()/*?}*/);
    public static final DeferredItem<CableBlockItem> TRIPLE_INSULATED_HV_CABLE = ITEMS.registerItem("triple_insulated_hv_cable", props -> new CableBlockItem(ICBlocks.TRIPLE_INSULATED_HV_CABLE.get(), props), () -> new Item.Properties()/*? if >=26.1 {*/.useBlockDescriptionPrefix()/*?}*/);
    public static final DeferredItem<CableBlockItem> GLASS_FIBRE_CABLE = ITEMS.registerItem("glass_fibre_cable", props -> new CableBlockItem(ICBlocks.GLASS_FIBRE_CABLE.get(), props), () -> new Item.Properties()/*? if >=26.1 {*/.useBlockDescriptionPrefix()/*?}*/);
    public static final DeferredItem<CreativeSourceBlockItem> CREATIVE_ENERGY_SOURCE = ITEMS.registerItem("creative_energy_source", props -> new CreativeSourceBlockItem(ICBlocks.CREATIVE_ENERGY_SOURCE.get(), props), () -> new Item.Properties()/*? if >=26.1 {*/.useBlockDescriptionPrefix()/*?}*/);
    public static final DeferredItem<EnergyStorageBlockItem> BASIC_ENERGY_STORAGE = ITEMS.registerItem("basic_energy_storage", props -> new EnergyStorageBlockItem(ICBlocks.BASIC_ENERGY_STORAGE.get(), props), () -> new Item.Properties()/*? if >=26.1 {*/.useBlockDescriptionPrefix()/*?}*/);

    public static final DeferredItem<Item> ELECTRONIC_CIRCUIT = ITEMS.registerSimpleItem("electronic_circuit");
    public static final DeferredItem<Item> ADVANCED_CIRCUIT = ITEMS.registerSimpleItem("advanced_circuit");
    public static final DeferredItem<Item> CARBON_FIBRE = ITEMS.registerSimpleItem("carbon_fibre");
    public static final DeferredItem<Item> CARBON_MESH = ITEMS.registerSimpleItem("carbon_mesh");
    public static final DeferredItem<Item> CARBON_PLATE = ITEMS.registerSimpleItem("carbon_plate");
    public static final DeferredItem<Item> INDUSTRIAL_DIAMOND = ITEMS.registerSimpleItem("industrial_diamond");
    public static final DeferredItem<Item> COAL_BALL = ITEMS.registerSimpleItem("coal_ball");
    public static final DeferredItem<Item> COAL_CHUNK = ITEMS.registerSimpleItem("coal_chunk");
    public static final DeferredItem<Item> TIN_CAN = ITEMS.registerSimpleItem("tin_can");
    public static final DeferredItem<Item> PLANT_BALL = ITEMS.registerSimpleItem("plant_ball");
    public static final DeferredItem<Item> CROP_TREATMENT_POWDER = ITEMS.registerSimpleItem("crop_treatment_powder");
    public static final DeferredItem<Item> COFFEE_BEANS = ITEMS.registerSimpleItem("coffee_beans");
    public static final DeferredItem<Item> GROUND_COFFEE = ITEMS.registerSimpleItem("ground_coffee");
    public static final DeferredItem<Item> FLOUR = ITEMS.registerSimpleItem("flour");
    public static final DeferredItem<Item> BIO_COMPOUND = ITEMS.registerSimpleItem("bio_compound");
    public static final DeferredItem<Item> BIOGAS_CELL = ITEMS.registerItem("biogas_cell", props ->
            new FluidCellItem(props, BiogasContent.BIOGAS.get()));
    public static final DeferredItem<BlockItem> REINFORCED_GLASS = ITEMS.registerSimpleBlockItem(ICBlocks.REINFORCED_GLASS);
    public static final DeferredItem<BlockItem> LUMINATOR = ITEMS.registerSimpleBlockItem(ICBlocks.LUMINATOR);
    public static final DeferredItem<Item> PLANT_FIBRE = ITEMS.registerSimpleItem("plant_fibre");
    public static final DeferredItem<Item> IRIDIUM_PLATE = ITEMS.registerSimpleItem("iridium_plate");

    public static final DeferredItem<Item> ASHES = ITEMS.registerSimpleItem("ashes");
    public static final DeferredItem<Item> IODINE = ITEMS.registerSimpleItem("iodine");
    public static final DeferredItem<Item> SLAG = ITEMS.registerSimpleItem("slag");
    public static final DeferredItem<Item> UU_MATTER = ITEMS.registerSimpleItem("uu_matter");
    public static final DeferredItem<Item> IRIDIUM_SHARD = ITEMS.registerSimpleItem("iridium_shard");

    public static final DeferredItem<Item> URANIUM = ITEMS.registerSimpleItem("uranium");
    public static final DeferredItem<Item> URANIUM_235 = ITEMS.registerSimpleItem("uranium_235");
    public static final DeferredItem<Item> URANIUM_238 = ITEMS.registerSimpleItem("uranium_238");
    public static final DeferredItem<Item> SMALL_URANIUM_235 = ITEMS.registerSimpleItem("small_uranium_235");
    public static final DeferredItem<Item> SMALL_URANIUM_238 = ITEMS.registerSimpleItem("small_uranium_238");
    public static final DeferredItem<Item> PLUTONIUM = ITEMS.registerSimpleItem("plutonium");
    public static final DeferredItem<Item> SMALL_PLUTONIUM = ITEMS.registerSimpleItem("small_plutonium");
    public static final DeferredItem<Item> MOX = ITEMS.registerSimpleItem("mox");
    public static final DeferredItem<Item> MOX_PELLET = ITEMS.registerSimpleItem("mox_pellet");
    public static final DeferredItem<Item> URANIUM_PELLET = ITEMS.registerSimpleItem("uranium_pellet");
    public static final DeferredItem<Item> RTG_PELLET = ITEMS.registerSimpleItem("rtg_pellet");
    public static final DeferredItem<Item> DEPLETED_URANIUM = part("depleted_uranium", ReactorPart.INERT);
    public static final DeferredItem<Item> DEPLETED_MOX = part("depleted_mox", ReactorPart.INERT);
    public static final DeferredItem<Item> DEPLETED_DUAL_URANIUM = part("depleted_dual_uranium", ReactorPart.INERT);
    public static final DeferredItem<Item> DEPLETED_DUAL_MOX = part("depleted_dual_mox", ReactorPart.INERT);
    public static final DeferredItem<Item> DEPLETED_QUAD_URANIUM = part("depleted_quad_uranium", ReactorPart.INERT);
    public static final DeferredItem<Item> DEPLETED_QUAD_MOX = part("depleted_quad_mox", ReactorPart.INERT);

    public static final DeferredItem<BatteryItem> RE_BATTERY = ITEMS.registerItem("re_battery",
            props -> new BatteryItem(props, 10_000, EuTier.LV, true), Item.Properties::new);
    public static final DeferredItem<BatteryItem> ADVANCED_RE_BATTERY = ITEMS.registerItem("advanced_re_battery",
            props -> new BatteryItem(props, 100_000, EuTier.MV, true), Item.Properties::new);
    public static final DeferredItem<BatteryItem> CHARGING_RE_BATTERY = ITEMS.registerItem("charging_re_battery",
            props -> new ChargingBatteryItem(props, 40_000, EuTier.LV), Item.Properties::new);
    public static final DeferredItem<BatteryItem> ADVANCED_CHARGING_RE_BATTERY = ITEMS.registerItem("advanced_charging_re_battery",
            props -> new ChargingBatteryItem(props, 400_000, EuTier.MV), Item.Properties::new);
    public static final DeferredItem<BatteryItem> ENERGY_CRYSTAL = ITEMS.registerItem("energy_crystal",
            props -> new BatteryItem(props, 1_000_000, EuTier.HV, true, 2_048, true), Item.Properties::new);
    public static final DeferredItem<BatteryItem> LAPOTRON_CRYSTAL = ITEMS.registerItem("lapotron_crystal",
            props -> new BatteryItem(props, 10_000_000, EuTier.EV, true, 8_092, true), Item.Properties::new);
    public static final DeferredItem<BatteryItem> SINGLE_USE_BATTERY = ITEMS.registerItem("single_use_battery",
            props -> new BatteryItem(props, 1_000, EuTier.LV, false), Item.Properties::new);

    public static final DeferredItem<Item> COIL = ITEMS.registerSimpleItem("coil");
    public static final DeferredItem<Item> MACHINE_CASING = ITEMS.registerSimpleItem("machine_casing");
    public static final DeferredItem<Item> ADVANCED_MACHINE_CASING = ITEMS.registerSimpleItem("advanced_machine_casing");
    public static final DeferredItem<ToolboxItem> TOOLBOX = ITEMS.registerItem("toolbox", ToolboxItem::new, () -> new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> ELECTRIC_MOTOR = ITEMS.registerSimpleItem("electric_motor");
    public static final DeferredItem<Item> HEAT_CONDUCTOR = ITEMS.registerSimpleItem("heat_conductor");
    public static final DeferredItem<Item> POWER_UNIT = ITEMS.registerSimpleItem("power_unit");
    public static final DeferredItem<Item> SMALL_POWER_UNIT = ITEMS.registerSimpleItem("small_power_unit");
    public static final DeferredItem<Item> SCRAP = ITEMS.registerSimpleItem("scrap");
    public static final DeferredItem<Item> SCRAP_BOX = ITEMS.registerSimpleItem("scrap_box");
    public static final DeferredItem<Item> CRYSTAL_MEMORY = ITEMS.registerItem("crystal_memory", MatterMemoryItem::new);
    public static final DeferredItem<Item> RAW_CRYSTAL_MEMORY = ITEMS.registerSimpleItem("raw_crystal_memory");
    public static final DeferredItem<Item> IRON_SHAFT = ITEMS.registerSimpleItem("iron_shaft");
    public static final DeferredItem<Item> STEEL_SHAFT = ITEMS.registerSimpleItem("steel_shaft");
    public static final DeferredItem<Item> IRON_ROTOR_BLADE = ITEMS.registerSimpleItem("iron_rotor_blade");
    public static final DeferredItem<Item> STEEL_ROTOR_BLADE = ITEMS.registerSimpleItem("steel_rotor_blade");
    public static final DeferredItem<Item> CARBON_ROTOR_BLADE = ITEMS.registerSimpleItem("carbon_rotor_blade");
    public static final DeferredItem<Item> WOOD_ROTOR_BLADE = ITEMS.registerSimpleItem("wood_rotor_blade");
    public static final DeferredItem<Item> FUEL_ROD = ITEMS.registerSimpleItem("fuel_rod");
    public static final DeferredItem<BlockItem> CROP_MATRON = ITEMS.registerSimpleBlockItem(ICBlocks.CROP_MATRON);
    public static final DeferredItem<BlockItem> CROP_STICKS = ITEMS.registerSimpleBlockItem(ICBlocks.CROP_STICKS);
    public static final DeferredItem<BlockItem> WOODEN_SCAFFOLD = ITEMS.registerSimpleBlockItem(ICBlocks.WOODEN_SCAFFOLD);
    public static final DeferredItem<BarrelItem> FERMENTING_BARREL = ITEMS.registerItem("fermenting_barrel", BarrelItem::new, () -> new Item.Properties()/*? if >=26.1 {*/.useBlockDescriptionPrefix()/*?}*/);
    public static final DeferredItem<GeneticSeedsItem> GENETIC_SEEDS = ITEMS.registerItem("genetic_seeds", GeneticSeedsItem::new);
    public static final DeferredItem<CropAnalyzerItem> CROP_ANALYZER = ITEMS.registerItem("crop_analyzer", CropAnalyzerItem::new);
    public static final DeferredItem<Item> WEEDING_TROWEL = ITEMS.registerSimpleItem("weeding_trowel", () -> new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> WEED = ITEMS.registerSimpleItem("weed");
    public static final DeferredItem<Item> HOPS = ITEMS.registerSimpleItem("hops");
    public static final DeferredItem<TerraWartItem> TERRA_WART = ITEMS.registerItem("terra_wart", TerraWartItem::new);
    public static final DeferredItem<Item> MUG = ITEMS.registerSimpleItem("mug", () -> new Item.Properties().stacksTo(1));
    public static final DeferredItem<CoffeeMugItem> COLD_COFFEE = ITEMS.registerItem("cold_coffee", props -> new CoffeeMugItem(props, 1));
    public static final DeferredItem<CoffeeMugItem> DARK_COFFEE = ITEMS.registerItem("dark_coffee", props -> new CoffeeMugItem(props, 5));
    public static final DeferredItem<CoffeeMugItem> COFFEE = ITEMS.registerItem("coffee", props -> new CoffeeMugItem(props, 6));
    public static final DeferredItem<BrewMugItem> BEER = ITEMS.registerItem("beer", BrewMugItem::new);
    public static final DeferredItem<Item> CROP_TREATMENT_CELL = ITEMS.registerItem("crop_treatment_cell", props -> new FluidCellItem(props, FarmingContent.TREATMENT.get()));
    public static final DeferredItem<Item> SMALL_DIAMOND_DUST = ITEMS.registerSimpleItem("small_diamond_dust");
    public static final DeferredItem<Item> ENDER_PEARL_DUST = ITEMS.registerSimpleItem("ender_pearl_dust");
    public static final DeferredItem<Item> EMERALD_BERRY = ITEMS.registerSimpleItem("emerald_berry");
    public static final DeferredItem<Item> MILK_WART = ITEMS.registerSimpleItem("milk_wart");
    public static final DeferredItem<Item> OIL_BERRY = ITEMS.registerSimpleItem("oil_berry");
    public static final DeferredItem<Item> FERTILIZER = ITEMS.registerSimpleItem("fertilizer");

    public static final DeferredItem<RotorItem> WOOD_ROTOR = ITEMS.registerItem("wood_rotor",
            props -> new RotorItem(props, RotorTier.WOOD), () -> new Item.Properties().durability(RotorTier.WOOD.durability));
    public static final DeferredItem<RotorItem> IRON_ROTOR = ITEMS.registerItem("iron_rotor",
            props -> new RotorItem(props, RotorTier.IRON), () -> new Item.Properties().durability(RotorTier.IRON.durability));
    public static final DeferredItem<RotorItem> STEEL_ROTOR = ITEMS.registerItem("steel_rotor",
            props -> new RotorItem(props, RotorTier.STEEL), () -> new Item.Properties().durability(RotorTier.STEEL.durability));
    public static final DeferredItem<RotorItem> CARBON_ROTOR = ITEMS.registerItem("carbon_rotor",
            props -> new RotorItem(props, RotorTier.CARBON), () -> new Item.Properties().durability(RotorTier.CARBON.durability));

    public static final DeferredItem<Item> EMPTY_CELL = ITEMS.registerItem("empty_cell", props -> new FluidCellItem(props, Fluids.EMPTY));
    public static final DeferredItem<AirCellItem> AIR_CELL = ITEMS.registerItem("air_cell", AirCellItem::new);
    public static final DeferredItem<Item> WATER_CELL = ITEMS.registerItem("water_cell", props -> new FluidCellItem(props, Fluids.WATER));
    public static final DeferredItem<Item> LAVA_CELL = ITEMS.registerItem("lava_cell", props -> new FluidCellItem(props, Fluids.LAVA));
    public static final DeferredItem<Item> COOLANT_CELL = ITEMS.registerItem("coolant_cell", props -> new FluidCellItem(props, CoolantContent.COOLANT.get()));
    public static final DeferredItem<Item> HOT_COOLANT_CELL = ITEMS.registerItem("hot_coolant_cell", props -> new FluidCellItem(props, CoolantContent.HOT_COOLANT.get()));
    public static final DeferredItem<Item> UU_MATTER_CELL = ITEMS.registerItem("uu_matter_cell", props -> new FluidCellItem(props, MatterContent.MATTER.get()));
    public static final DeferredItem<Item> BIOMASS_CELL = ITEMS.registerItem("biomass_cell", props -> new FluidCellItem(props, BiogasContent.BIOMASS.get()));
    public static final DeferredItem<FilledFoodCanItem> FILLED_TIN_CAN = ITEMS.registerItem("filled_tin_can", FilledFoodCanItem::new);
    public static final DeferredItem<Item> FUEL_CAN = ITEMS.registerSimpleItem("fuel_can");
    public static final DeferredItem<Item> FUEL_CAN_EMPTY = ITEMS.registerSimpleItem("fuel_can_empty");

    public static final DeferredItem<Item> HEAT_VENT = part("heat_vent", ReactorPart.HEAT_VENT);
    public static final DeferredItem<Item> ADVANCED_HEAT_VENT = part("advanced_heat_vent", ReactorPart.ADVANCED_HEAT_VENT);
    public static final DeferredItem<Item> OVERCLOCKED_HEAT_VENT = part("overclocked_heat_vent", ReactorPart.OVERCLOCKED_HEAT_VENT);
    public static final DeferredItem<Item> COMPONENT_HEAT_VENT = part("component_heat_vent", ReactorPart.COMPONENT_HEAT_VENT);
    public static final DeferredItem<Item> REACTOR_HEAT_VENT = part("reactor_heat_vent", ReactorPart.REACTOR_HEAT_VENT);
    public static final DeferredItem<Item> HEAT_EXCHANGER = part("heat_exchanger", ReactorPart.HEAT_EXCHANGER);
    public static final DeferredItem<Item> ADVANCED_HEAT_EXCHANGER = part("advanced_heat_exchanger", ReactorPart.ADVANCED_HEAT_EXCHANGER);
    public static final DeferredItem<Item> COMPONENT_HEAT_EXCHANGER = part("component_heat_exchanger", ReactorPart.COMPONENT_HEAT_EXCHANGER);
    public static final DeferredItem<Item> REACTOR_HEAT_EXCHANGER = part("reactor_heat_exchanger", ReactorPart.REACTOR_HEAT_EXCHANGER);
    public static final DeferredItem<Item> HEAT_STORAGE = part("heat_storage", ReactorPart.COOLANT_10K);
    public static final DeferredItem<Item> HEX_HEAT_STORAGE = part("hex_heat_storage", ReactorPart.COOLANT_60K);
    public static final DeferredItem<Item> TRI_HEAT_STORAGE = part("tri_heat_storage", ReactorPart.COOLANT_30K);
    public static final DeferredItem<Item> PLATING = part("plating", ReactorPart.PLATING);
    public static final DeferredItem<Item> HEAT_PLATING = part("heat_plating", ReactorPart.HEAT_PLATING);
    public static final DeferredItem<Item> CONTAINMENT_PLATING = part("containment_plating", ReactorPart.CONTAINMENT_PLATING);
    public static final DeferredItem<Item> NEUTRON_REFLECTOR = part("neutron_reflector", ReactorPart.NEUTRON_REFLECTOR);
    public static final DeferredItem<Item> THICK_NEUTRON_REFLECTOR = part("thick_neutron_reflector", ReactorPart.THICK_NEUTRON_REFLECTOR);
    public static final DeferredItem<Item> IRIDIUM_REFLECTOR = part("iridium_reflector", ReactorPart.IRIDIUM_REFLECTOR);
    public static final DeferredItem<Item> LZH_CONDENSATOR = part("lzh_condensator", ReactorPart.LZH_CONDENSATOR);
    public static final DeferredItem<Item> RSH_CONDENSATOR = part("rsh_condensator", ReactorPart.RSH_CONDENSATOR);
    public static final DeferredItem<Item> HEATPACK = ITEMS.registerSimpleItem("heatpack");

    public static final DeferredItem<Item> FUEL_ROD_URANIUM = rod("fuel_rod_uranium", ReactorPart.URANIUM, DEPLETED_URANIUM);
    public static final DeferredItem<Item> DUAL_FUEL_ROD_URANIUM = rod("dual_fuel_rod_uranium", ReactorPart.DUAL_URANIUM, DEPLETED_DUAL_URANIUM);
    public static final DeferredItem<Item> QUAD_FUEL_ROD_URANIUM = rod("quad_fuel_rod_uranium", ReactorPart.QUAD_URANIUM, DEPLETED_QUAD_URANIUM);
    public static final DeferredItem<Item> FUEL_ROD_MOX = rod("fuel_rod_mox", ReactorPart.MOX, DEPLETED_MOX);
    public static final DeferredItem<Item> DUAL_FUEL_ROD_MOX = rod("dual_fuel_rod_mox", ReactorPart.DUAL_MOX, DEPLETED_DUAL_MOX);
    public static final DeferredItem<Item> QUAD_FUEL_ROD_MOX = rod("quad_fuel_rod_mox", ReactorPart.QUAD_MOX, DEPLETED_QUAD_MOX);

    public static final DeferredItem<PoweredArmorItem> NANO_HELMET = ITEMS.registerItem("nano_helmet", props -> new PoweredArmorItem(props, EquipmentSlot.HEAD, false));
    public static final DeferredItem<PoweredArmorItem> NANO_CHESTPLATE = ITEMS.registerItem("nano_chestplate", props -> new PoweredArmorItem(props, EquipmentSlot.CHEST, false));
    public static final DeferredItem<PoweredArmorItem> NANO_LEGGINGS = ITEMS.registerItem("nano_leggings", props -> new PoweredArmorItem(props, EquipmentSlot.LEGS, false));
    public static final DeferredItem<PoweredArmorItem> NANO_BOOTS = ITEMS.registerItem("nano_boots", props -> new PoweredArmorItem(props, EquipmentSlot.FEET, false));
    public static final DeferredItem<PoweredArmorItem> QUANTUM_HELMET = ITEMS.registerItem("quantum_helmet", props -> new PoweredArmorItem(props, EquipmentSlot.HEAD, true));
    public static final DeferredItem<PoweredArmorItem> QUANTUM_CHESTPLATE = ITEMS.registerItem("quantum_chestplate", props -> new PoweredArmorItem(props, EquipmentSlot.CHEST, true));
    public static final DeferredItem<PoweredArmorItem> QUANTUM_LEGGINGS = ITEMS.registerItem("quantum_leggings", props -> new PoweredArmorItem(props, EquipmentSlot.LEGS, true));
    public static final DeferredItem<PoweredArmorItem> QUANTUM_BOOTS = ITEMS.registerItem("quantum_boots", props -> new PoweredArmorItem(props, EquipmentSlot.FEET, true));
    public static final DeferredItem<FuelFlightItem> JETPACK = ITEMS.registerItem("jetpack", FuelFlightItem::new);
    public static final DeferredItem<WearableBatteryItem> ELECTRIC_JETPACK = ITEMS.registerItem("electric_jetpack", props -> new WearableBatteryItem(props, EquipmentSlot.CHEST, 30000, EuTier.LV, 60, false));
    public static final DeferredItem<HazardArmorItem> HAZMAT_HELMET = ITEMS.registerItem("hazmat_helmet", props -> new HazardArmorItem(props, EquipmentSlot.HEAD));
    public static final DeferredItem<HazardArmorItem> HAZMAT_CHESTPLATE = ITEMS.registerItem("hazmat_chestplate", props -> new HazardArmorItem(props, EquipmentSlot.CHEST));
    public static final DeferredItem<HazardArmorItem> HAZMAT_LEGGINGS = ITEMS.registerItem("hazmat_leggings", props -> new HazardArmorItem(props, EquipmentSlot.LEGS));
    public static final DeferredItem<HazardArmorItem> RUBBER_BOOTS = ITEMS.registerItem("rubber_boots", props -> new HazardArmorItem(props, EquipmentSlot.FEET));
    public static final DeferredItem<WearableBatteryItem> BATPACK = ITEMS.registerItem("batpack", props -> new WearableBatteryItem(props, EquipmentSlot.CHEST, 60000, EuTier.LV, 100, true));
    public static final DeferredItem<WearableBatteryItem> ADVANCED_BATPACK = ITEMS.registerItem("advanced_batpack", props -> new WearableBatteryItem(props, EquipmentSlot.CHEST, 600000, EuTier.MV, 1000, true));
    public static final DeferredItem<WearableBatteryItem> LAPPACK = ITEMS.registerItem("lappack", props -> new WearableBatteryItem(props, EquipmentSlot.CHEST, 2000000, EuTier.HV, 1000, true));
    public static final DeferredItem<WearableBatteryItem> ENERGY_PACK = ITEMS.registerItem("energy_pack", props -> new WearableBatteryItem(props, EquipmentSlot.CHEST, 2000000, EuTier.HV, 1000, true));
    public static final DeferredItem<Item> CF_PACK = ITEMS.registerSimpleItem("cf_pack");
    public static final DeferredItem<Item> STATIC_BOOTS = ITEMS.registerItem("static_boots", props -> new Item(GearProperties.worn(props, EquipmentSlot.FEET, "utility")));
    public static final DeferredItem<Item> SOLAR_HELMET = ITEMS.registerItem("solar_helmet", props -> new Item(GearProperties.worn(props, EquipmentSlot.HEAD, "utility")));
    public static final DeferredItem<WearableBatteryItem> NIGHTVISION_GOGGLES = ITEMS.registerItem("nightvision_goggles", props -> new WearableBatteryItem(props, EquipmentSlot.HEAD, 200000, EuTier.LV, 200, false));
    public static final DeferredItem<Item> BRONZE_HELMET = ITEMS.registerItem("bronze_helmet", props -> new Item(GearProperties.armor(props, EquipmentSlot.HEAD, "bronze", 165, 2, 0, 9, BRONZE_INGOT.get())));
    public static final DeferredItem<Item> BRONZE_CHESTPLATE = ITEMS.registerItem("bronze_chestplate", props -> new Item(GearProperties.armor(props, EquipmentSlot.CHEST, "bronze", 240, 6, 0, 9, BRONZE_INGOT.get())));
    public static final DeferredItem<Item> BRONZE_LEGGINGS = ITEMS.registerItem("bronze_leggings", props -> new Item(GearProperties.armor(props, EquipmentSlot.LEGS, "bronze", 225, 5, 0, 9, BRONZE_INGOT.get())));
    public static final DeferredItem<Item> BRONZE_BOOTS = ITEMS.registerItem("bronze_boots", props -> new Item(GearProperties.armor(props, EquipmentSlot.FEET, "bronze", 195, 2, 0, 9, BRONZE_INGOT.get())));
    public static final DeferredItem<Item> ALLOY_CHESTPLATE = ITEMS.registerItem("alloy_chestplate", props -> new Item(GearProperties.armor(props, EquipmentSlot.CHEST, "alloy", 800, 9, 2, 12, ALLOY_INGOT.get())));

    //? if >=26.1 {
    public static final DeferredItem<Item> BRONZE_SWORD = ITEMS.registerItem("bronze_sword", props -> new Item(props.sword(EquipmentContent.BRONZE, 3, -2.4F)));
    public static final DeferredItem<Item> BRONZE_PICKAXE = ITEMS.registerItem("bronze_pickaxe", props -> new Item(props.pickaxe(EquipmentContent.BRONZE, 1, -2.8F)));
    public static final DeferredItem<ShovelItem> BRONZE_SHOVEL = ITEMS.registerItem("bronze_shovel", props -> new ShovelItem(EquipmentContent.BRONZE, 1.5F, -3, props));
    public static final DeferredItem<AxeItem> BRONZE_AXE = ITEMS.registerItem("bronze_axe", props -> new AxeItem(EquipmentContent.BRONZE_AXE, 6, -3.1F, props));
    public static final DeferredItem<HoeItem> BRONZE_HOE = ITEMS.registerItem("bronze_hoe", props -> new HoeItem(EquipmentContent.BRONZE, -2, -1, props));
    //?} else {
    /*public static final DeferredItem<Item> BRONZE_SWORD = ITEMS.registerItem("bronze_sword", props -> new SwordItem(EquipmentContent.BRONZE, props.attributes(SwordItem.createAttributes(EquipmentContent.BRONZE, 3F, -2.4F))) {
        @Override
        public boolean canPerformAction(ItemStack stack, ItemAbility ability) {
            return ability == ItemAbilities.SWORD_SWEEP ? stack.is(ItemTags.SWORDS) : super.canPerformAction(stack, ability);
        }
    });
    public static final DeferredItem<Item> BRONZE_PICKAXE = ITEMS.registerItem("bronze_pickaxe", props -> new PickaxeItem(EquipmentContent.BRONZE, props.attributes(DiggerItem.createAttributes(EquipmentContent.BRONZE, 1, -2.8F))));
    public static final DeferredItem<ShovelItem> BRONZE_SHOVEL = ITEMS.registerItem("bronze_shovel", props -> new ShovelItem(EquipmentContent.BRONZE, props.attributes(DiggerItem.createAttributes(EquipmentContent.BRONZE, 1.5F, -3))));
    public static final DeferredItem<AxeItem> BRONZE_AXE = ITEMS.registerItem("bronze_axe", props -> new AxeItem(EquipmentContent.BRONZE_AXE, props.attributes(DiggerItem.createAttributes(EquipmentContent.BRONZE_AXE, 6, -3.1F))));
    public static final DeferredItem<HoeItem> BRONZE_HOE = ITEMS.registerItem("bronze_hoe", props -> new HoeItem(EquipmentContent.BRONZE, props.attributes(DiggerItem.createAttributes(EquipmentContent.BRONZE, -2, -1))));
    *///?}
    public static final DeferredItem<DrillItem> MINING_DRILL = ITEMS.registerItem("mining_drill", props -> new DrillItem(props, 0));
    public static final DeferredItem<DrillItem> DIAMOND_DRILL = ITEMS.registerItem("diamond_drill", props -> new DrillItem(props, 1));
    public static final DeferredItem<DrillItem> IRIDIUM_DRILL = ITEMS.registerItem("iridium_drill", props -> new DrillItem(props, 2));
    public static final DeferredItem<ChainsawItem> CHAINSAW = ITEMS.registerItem("chainsaw", ChainsawItem::new);
    public static final DeferredItem<PoweredFieldItem> ELECTRIC_HOE = ITEMS.registerItem("electric_hoe", props -> new PoweredFieldItem(props, false));
    public static final DeferredItem<PoweredFieldItem> ELECTRIC_TAP = ITEMS.registerItem("electric_tap", props -> new PoweredFieldItem(props, true));
    public static final DeferredItem<EnergyBladeItem> ENERGY_BLADE = ITEMS.registerItem("energy_blade", EnergyBladeItem::new);
    public static final DeferredItem<MiningLaserItem> MINING_LASER = ITEMS.registerItem("mining_laser", MiningLaserItem::new);
    public static final DeferredItem<ElectricWrenchItem> ELECTRIC_WRENCH = ITEMS.registerItem("electric_wrench",
            props -> new ElectricWrenchItem(props, 100_000, EuTier.LV, true), Item.Properties::new);
    public static final DeferredItem<Item> OD_SCANNER = ITEMS.registerItem("od_scanner", props -> new OreProbeItem(props, false));
    public static final DeferredItem<Item> OV_SCANNER = ITEMS.registerItem("ov_scanner", props -> new OreProbeItem(props, true));

    public static final DeferredItem<Item> OVERCLOCKER_UPGRADE = ITEMS.registerItem("overclocker_upgrade", props -> new DescribedItem(props, ProcessingUpgrades::describe));
    public static final DeferredItem<Item> TRANSFORMER_UPGRADE = ITEMS.registerItem("transformer_upgrade", props -> new DescribedItem(props, ProcessingUpgrades::describe));
    public static final DeferredItem<Item> ENERGY_STORAGE_UPGRADE = ITEMS.registerItem("energy_storage_upgrade", props -> new DescribedItem(props, ProcessingUpgrades::describe));
    public static final DeferredItem<Item> REDSTONE_INVERTER_UPGRADE = ITEMS.registerItem("redstone_inverter_upgrade", props -> new DescribedItem(props, ProcessingUpgrades::describe));
    public static final DeferredItem<Item> EJECTOR_UPGRADE = ITEMS.registerItem("ejector_upgrade", DirectionalUpgradeItem::new);
    public static final DeferredItem<Item> PULLING_UPGRADE = ITEMS.registerItem("pulling_upgrade", DirectionalUpgradeItem::new);
    public static final DeferredItem<Item> FLUID_EJECTOR_UPGRADE = ITEMS.registerItem("fluid_ejector_upgrade", DirectionalUpgradeItem::new);
    public static final DeferredItem<Item> FLUID_PULLING_UPGRADE = ITEMS.registerItem("fluid_pulling_upgrade", DirectionalUpgradeItem::new);

    public static final DeferredItem<BlockItem> BLOCK_SLICER = ITEMS.registerSimpleBlockItem(ICBlocks.BLOCK_SLICER);
    public static final DeferredItem<BlockItem> PATTERN_SCANNER = ITEMS.registerSimpleBlockItem(ICBlocks.PATTERN_SCANNER);
    public static final DeferredItem<Item> IRON_SAW_DISC = ITEMS.registerItem("iron_saw_disc", props -> new DescribedItem(props, ProcessingHeat::describeBlade));
    public static final DeferredItem<Item> STEEL_SAW_DISC = ITEMS.registerItem("steel_saw_disc", props -> new DescribedItem(props, ProcessingHeat::describeBlade));
    public static final DeferredItem<Item> DIAMOND_SAW_DISC = ITEMS.registerItem("diamond_saw_disc", props -> new DescribedItem(props, ProcessingHeat::describeBlade));
    public static final DeferredItem<Item> ADVANCED_EJECTOR_UPGRADE = ITEMS.registerItem("advanced_ejector_upgrade", DirectionalUpgradeItem::new);
    public static final DeferredItem<Item> ADVANCED_PULLING_UPGRADE = ITEMS.registerItem("advanced_pulling_upgrade", DirectionalUpgradeItem::new);

    public static final DeferredItem<BlockItem> BIO_DIGESTER = ITEMS.registerSimpleBlockItem(ICBlocks.BIO_DIGESTER);
    public static final DeferredItem<BlockItem> LIQUID_FUEL_GENERATOR = ITEMS.registerSimpleBlockItem(ICBlocks.LIQUID_FUEL_GENERATOR);
    public static final DeferredItem<Item> CRUSHED_URANIUM = ITEMS.registerSimpleItem("crushed_uranium");
    public static final DeferredItem<Item> PURIFIED_CRUSHED_URANIUM = ITEMS.registerSimpleItem("purified_crushed_uranium");
    public static final DeferredItem<Item> CRUSHED_SILVER = ITEMS.registerSimpleItem("crushed_silver");
    public static final DeferredItem<Item> PURIFIED_CRUSHED_SILVER = ITEMS.registerSimpleItem("purified_crushed_silver");
    private static DeferredItem<Item> part(String name, ReactorPart part) {
        return ITEMS.registerItem(name, props -> new ReactorComponentItem(props, part));
    }

    private static DeferredItem<Item> rod(String name, ReactorPart part, Supplier<? extends Item> spent) {
        return ITEMS.registerItem(name, props -> new ReactorComponentItem(props, part, spent));
    }

    private ICItems() {
    }
}
