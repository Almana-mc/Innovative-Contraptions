package me.almana.innovativecontraptions.datagen;

import me.almana.innovativecontraptions.registry.ICBlocks;
import me.almana.innovativecontraptions.registry.ICItems;
import me.almana.innovativecontraptions.registry.ICTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.crafting.CompoundIngredient;

import java.util.concurrent.CompletableFuture;

public class ICRecipeProvider extends RecipeProvider {
    protected ICRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, new ChargedRecipeOutput(output, registries));
    }

    @Override
    protected void buildRecipes() {
        new EnergyPartTwoRecipeProvider(registries, output).buildRecipes();
        new BioenergyRecipeProvider(registries, output).buildRecipes();
        new MatterRecipeProvider(registries, output).buildRecipes();
        new GatheringRecipeProvider(registries, output).buildRecipes();
        new ICMaceratingRecipeProvider(registries, output).build();
        new ICPartOneRecipeProvider(registries, output).buildRecipes();
        new ICPartTwoRecipeProvider(registries, output).buildRecipes();
        new ICPartThreeRecipeProvider(registries, output).buildRecipes();
        new ReactorRecipeProvider(registries, output).buildRecipes();
        new FarmingRecipeProvider(registries, output).buildRecipes();
        new EquipmentRecipeProvider(registries, output).buildRecipes();
        new FoodCanningRecipeProvider(registries, output).buildRecipes();
        new ICProcessingRecipeProvider(registries, output).build();
        ThermalRecipeProvider.build(registries, output);

        shaped(RecipeCategory.TOOLS, ICItems.TREE_TAP.get())
                .define('P', ItemTags.PLANKS)
                .pattern(" P ")
                .pattern("PPP")
                .pattern("P  ")
                .unlockedBy("has_planks", has(ItemTags.PLANKS))
                .save(output);

        shaped(RecipeCategory.TOOLS, ICItems.WRENCH.get())
                .define('B', ICTags.INGOTS_BRONZE)
                .pattern("B B")
                .pattern("BBB")
                .pattern(" B ")
                .unlockedBy("has_bronze_ingot", has(ICTags.INGOTS_BRONZE))
                .save(output);

        shaped(RecipeCategory.TOOLS, ICItems.FORGE_HAMMER.get())
                .define('I', Tags.Items.INGOTS_IRON)
                .define('S', Tags.Items.RODS_WOODEN)
                .pattern("III")
                .pattern("ISI")
                .pattern(" S ")
                .unlockedBy("has_iron_ingot", has(Tags.Items.INGOTS_IRON))
                .save(output);

        shaped(RecipeCategory.TOOLS, ICItems.CUTTER.get())
                .define('P', ICTags.PLATES_IRON)
                .define('I', Tags.Items.INGOTS_IRON)
                .pattern("P P")
                .pattern(" P ")
                .pattern("I I")
                .unlockedBy("has_iron_plate", has(ICTags.PLATES_IRON))
                .save(output);

        woodFromLog(ICBlocks.RUBBER_WOOD.get(), ICBlocks.RUBBER_LOG.get());
        woodFromLog(ICBlocks.STRIPPED_RUBBER_WOOD.get(), ICBlocks.STRIPPED_RUBBER_LOG.get());

        plate(Tags.Items.INGOTS_IRON, ICItems.IRON_PLATE.get());
        plate(Tags.Items.INGOTS_GOLD, ICItems.GOLD_PLATE.get());
        plate(Tags.Items.INGOTS_COPPER, ICItems.COPPER_PLATE.get());
        plate(ICTags.INGOTS_TIN, ICItems.TIN_PLATE.get());
        plate(ICTags.INGOTS_BRONZE, ICItems.BRONZE_PLATE.get());
        plate(ICTags.INGOTS_LEAD, ICItems.LEAD_PLATE.get());
        plate(ICTags.INGOTS_STEEL, ICItems.STEEL_PLATE.get());

        casing(ICTags.PLATES_IRON, ICItems.IRON_CASING.get());
        casing(ICTags.PLATES_BRONZE, ICItems.BRONZE_CASING.get());
        casing(ICTags.PLATES_TIN, ICItems.TIN_CASING.get());
        casing(ICTags.PLATES_COPPER, ICItems.COPPER_CASING.get());
        casing(ICTags.PLATES_GOLD, ICItems.GOLD_CASING.get());
        casing(ICTags.PLATES_LEAD, ICItems.LEAD_CASING.get());
        casing(ICTags.PLATES_STEEL, ICItems.STEEL_CASING.get());

        cable(ICTags.PLATES_TIN, ICItems.TIN_CABLE.get(), 3);
        cable(ICTags.PLATES_COPPER, ICItems.COPPER_CABLE.get(), 2);
        insulate(ICItems.TIN_CABLE.get(), ICItems.INSULATED_TIN_CABLE.get());
        insulate(ICItems.COPPER_CABLE.get(), ICItems.INSULATED_COPPER_CABLE.get());
        cable(ICTags.PLATES_GOLD, ICItems.GOLD_CABLE.get(), 4);
        insulate(ICItems.GOLD_CABLE.get(), ICItems.INSULATED_GOLD_CABLE.get());
        insulate(ICItems.INSULATED_GOLD_CABLE.get(), ICItems.DOUBLE_INSULATED_GOLD_CABLE.get());
        cable(ICTags.PLATES_IRON, ICItems.HV_CABLE.get(), 4);
        insulate(ICItems.HV_CABLE.get(), ICItems.INSULATED_HV_CABLE.get());

        shaped(RecipeCategory.MISC, ICItems.GLASS_FIBRE_CABLE.get(), 6)
                .define('G', Tags.Items.GLASS_BLOCKS_COLORLESS)
                .define('E', ICTags.DUSTS_ENERGIUM)
                .define('S', ICTags.DUSTS_SILVER)
                .pattern("GGG")
                .pattern("ESE")
                .pattern("GGG")
                .unlockedBy("has_energium_dust", has(ICTags.DUSTS_ENERGIUM))
                .save(output);

        shaped(RecipeCategory.MISC, ICBlocks.BASIC_ENERGY_STORAGE.get())
                .define('W', ICBlocks.RUBBER_WOOD.get())
                .define('C', ICItems.INSULATED_COPPER_CABLE.get())
                .define('B', ICItems.RE_BATTERY.get())
                .pattern("WWW")
                .pattern("CBC")
                .pattern("WWW")
                .unlockedBy("has_re_battery", has(ICItems.RE_BATTERY.get()))
                .save(output);

        dustSmelt(ICTags.DUSTS_IRON, Items.IRON_INGOT, "iron_ingot_from_dust");
        dustSmelt(ICTags.DUSTS_GOLD, Items.GOLD_INGOT, "gold_ingot_from_dust");
        dustSmelt(ICTags.DUSTS_COPPER, Items.COPPER_INGOT, "copper_ingot_from_dust");
        dustSmelt(ICTags.DUSTS_TIN, ICItems.TIN_INGOT.get(), "tin_ingot_from_dust");
        dustSmelt(ICTags.DUSTS_BRONZE, ICItems.BRONZE_INGOT.get(), "bronze_ingot_from_dust");
        dustSmelt(ICTags.DUSTS_LEAD, ICItems.LEAD_INGOT.get(), "lead_ingot_from_dust");
        dustSmelt(ICTags.DUSTS_SILVER, ICItems.SILVER_INGOT.get(), "silver_ingot_from_dust");
        dustSmelt(ICTags.CRUSHED_ORES_IRON, Items.IRON_INGOT, "iron_ingot_from_crushed_iron");
        dustSmelt(ICTags.CRUSHED_ORES_GOLD, Items.GOLD_INGOT, "gold_ingot_from_crushed_gold");
        dustSmelt(ICTags.CRUSHED_ORES_COPPER, Items.COPPER_INGOT, "copper_ingot_from_crushed_copper");
        dustSmelt(ICTags.CRUSHED_ORES_TIN, ICItems.TIN_INGOT.get(), "tin_ingot_from_crushed_tin");
        dustSmelt(ICTags.CRUSHED_ORES_LEAD, ICItems.LEAD_INGOT.get(), "lead_ingot_from_crushed_lead");

        combineSmall(ICTags.SMALL_DUSTS_IRON, ICItems.IRON_DUST.get());
        combineSmall(ICTags.SMALL_DUSTS_GOLD, ICItems.GOLD_DUST.get());
        combineSmall(ICTags.SMALL_DUSTS_COPPER, ICItems.COPPER_DUST.get());
        combineSmall(ICTags.SMALL_DUSTS_TIN, ICItems.TIN_DUST.get());
        combineSmall(ICTags.SMALL_DUSTS_BRONZE, ICItems.BRONZE_DUST.get());
        combineSmall(ICTags.SMALL_DUSTS_LEAD, ICItems.LEAD_DUST.get());
        combineSmall(ICTags.SMALL_DUSTS_SILVER, ICItems.SILVER_DUST.get());
        combineSmall(ICTags.SMALL_DUSTS_LAPIS, ICItems.LAPIS_DUST.get());
        combineSmall(ICTags.SMALL_DUSTS_LITHIUM, ICItems.LITHIUM_DUST.get());
        combineSmall(ICTags.SMALL_DUSTS_OBSIDIAN, ICItems.OBSIDIAN_DUST.get());
        combineSmall(ICTags.SMALL_DUSTS_SULFUR, ICItems.SULFUR_DUST.get());
        combineSmall(ICItems.SMALL_URANIUM_235.get(), ICItems.URANIUM_235.get());
        combineSmall(ICItems.SMALL_URANIUM_238.get(), ICItems.URANIUM_238.get());
        combineSmall(ICItems.SMALL_PLUTONIUM.get(), ICItems.PLUTONIUM.get());

        shapeless(RecipeCategory.MISC, ICItems.BRONZE_DUST.get(), 4)
                .requires(CompoundIngredient.of(tag(ICTags.DUSTS_COPPER), tag(ICTags.CRUSHED_ORES_COPPER), tag(ICTags.PURIFIED_CRUSHED_ORES_COPPER)), 3)
                .requires(CompoundIngredient.of(tag(ICTags.DUSTS_TIN), tag(ICTags.CRUSHED_ORES_TIN), tag(ICTags.PURIFIED_CRUSHED_ORES_TIN)))
                .unlockedBy("has_copper_dust", has(ICTags.DUSTS_COPPER))
                .save(output, "innovativecontraptions:bronze_dust_from_copper_tin");

        shaped(RecipeCategory.MISC, ICItems.MIXED_METAL_INGOT.get(), 2)
                .define('I', ICTags.PLATES_IRON)
                .define('B', ICTags.PLATES_BRONZE)
                .define('T', ICTags.PLATES_TIN)
                .pattern("III")
                .pattern("BBB")
                .pattern("TTT")
                .unlockedBy("has_bronze_plate", has(ICTags.PLATES_BRONZE))
                .save(output);

        shaped(RecipeCategory.MISC, ICItems.TIN_CAN.get(), 6)
                .define('P', ICTags.PLATES_TIN)
                .pattern("P P")
                .pattern("PPP")
                .unlockedBy("has_tin_plate", has(ICTags.PLATES_TIN))
                .save(output);

        shapeless(RecipeCategory.MISC, ICItems.PLANT_BALL.get())
                .requires(tag(Tags.Items.CROPS_WHEAT), 8)
                .unlockedBy("has_wheat", has(Tags.Items.CROPS_WHEAT))
                .save(output);

        shaped(RecipeCategory.MISC, ICItems.ELECTRONIC_CIRCUIT.get())
                .define('C', ICItems.INSULATED_COPPER_CABLE.get())
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .define('P', ICTags.PLATES_IRON)
                .pattern("CCC")
                .pattern("RPR")
                .pattern("CCC")
                .unlockedBy("has_insulated_copper_cable", has(ICItems.INSULATED_COPPER_CABLE.get()))
                .save(output);

        shaped(RecipeCategory.MISC, ICItems.ELECTRONIC_CIRCUIT.get())
                .define('C', ICItems.INSULATED_COPPER_CABLE.get())
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .define('P', ICTags.PLATES_IRON)
                .pattern("CRC")
                .pattern("CPC")
                .pattern("CRC")
                .unlockedBy("has_insulated_copper_cable", has(ICItems.INSULATED_COPPER_CABLE.get()))
                .save(output, "innovativecontraptions:electronic_circuit_vertical");

        shaped(RecipeCategory.MISC, ICItems.ADVANCED_CIRCUIT.get())
                .define('E', ICTags.CIRCUITS_BASIC)
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .define('G', Tags.Items.DUSTS_GLOWSTONE)
                .define('L', Tags.Items.GEMS_LAPIS)
                .pattern("RGR")
                .pattern("LEL")
                .pattern("RGR")
                .unlockedBy("has_electronic_circuit", has(ICTags.CIRCUITS_BASIC))
                .save(output);

        shaped(RecipeCategory.MISC, ICItems.ADVANCED_CIRCUIT.get())
                .define('E', ICTags.CIRCUITS_BASIC)
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .define('G', Tags.Items.DUSTS_GLOWSTONE)
                .define('L', Tags.Items.GEMS_LAPIS)
                .pattern("RLR")
                .pattern("GEG")
                .pattern("RLR")
                .unlockedBy("has_electronic_circuit", has(ICTags.CIRCUITS_BASIC))
                .save(output, "innovativecontraptions:advanced_circuit_rotated");

        SimpleCookingRecipeBuilder.smelting(
                Ingredient.of(ICItems.STICKY_RESIN.get()),
                RecipeCategory.MISC,
                CookingBookCategory.MISC,
                ICItems.RUBBER.get(),
                0.3F,
                200
        ).unlockedBy("has_sticky_resin", has(ICItems.STICKY_RESIN.get())).save(output, "innovativecontraptions:rubber_from_smelting_sticky_resin");

        oreSmelt(ICTags.ORES_TIN, ICItems.TIN_INGOT.get(), "tin_ingot_from_smelting");
        oreSmelt(ICTags.ORES_LEAD, ICItems.LEAD_INGOT.get(), "lead_ingot_from_smelting");

        // Component items
        shaped(RecipeCategory.MISC, ICItems.COIL.get())
                .define('C', ICItems.COPPER_CABLE.get())
                .define('I', Tags.Items.INGOTS_IRON)
                .pattern("CCC")
                .pattern("CIC")
                .pattern("CCC")
                .unlockedBy("has_copper_cable", has(ICItems.COPPER_CABLE.get()))
                .save(output);

        shaped(RecipeCategory.MISC, ICItems.ELECTRIC_MOTOR.get())
                .define('C', ICItems.COIL.get())
                .define('T', ICTags.CASINGS_TIN)
                .define('I', Tags.Items.INGOTS_IRON)
                .pattern(" C ")
                .pattern("TIT")
                .pattern(" C ")
                .unlockedBy("has_coil", has(ICItems.COIL.get()))
                .save(output);

        shaped(RecipeCategory.MISC, ICItems.HEAT_CONDUCTOR.get())
                .define('R', ICTags.RUBBERS)
                .define('C', ICTags.PLATES_COPPER)
                .pattern("RCR")
                .pattern("RCR")
                .pattern("RCR")
                .unlockedBy("has_copper_plate", has(ICTags.PLATES_COPPER))
                .save(output);

        shaped(RecipeCategory.MISC, ICItems.RE_BATTERY.get())
                .define('T', ICTags.CASINGS_TIN)
                .define('C', ICItems.INSULATED_TIN_CABLE.get())
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .pattern(" C ")
                .pattern("TRT")
                .pattern("TRT")
                .unlockedBy("has_tin_casing", has(ICTags.CASINGS_TIN))
                .save(output);

        shaped(RecipeCategory.MISC, ICItems.SMALL_POWER_UNIT.get())
                .define('C', ICItems.COPPER_CABLE.get())
                .define('M', ICItems.ELECTRIC_MOTOR.get())
                .define('I', ICTags.CASINGS_IRON)
                .define('E', ICTags.CIRCUITS_BASIC)
                .define('B', ICItems.RE_BATTERY.get())
                .pattern(" CI")
                .pattern("BEM")
                .pattern(" CI")
                .unlockedBy("has_electric_motor", has(ICItems.ELECTRIC_MOTOR.get()))
                .save(output);

        shaped(RecipeCategory.MISC, ICItems.EMPTY_CELL.get(), 4)
                .define('T', ICTags.CASINGS_TIN)
                .define('G', Tags.Items.GLASS_PANES_COLORLESS)
                .pattern(" T ")
                .pattern("TGT")
                .pattern(" T ")
                .unlockedBy("has_tin_casing", has(ICTags.CASINGS_TIN))
                .save(output);

        shaped(RecipeCategory.MISC, ICItems.HEAT_EXCHANGER.get())
                .define('T', ICTags.PLATES_TIN)
                .define('C', ICTags.PLATES_COPPER)
                .define('E', ICTags.CIRCUITS_BASIC)
                .pattern("CEC")
                .pattern("TCT")
                .pattern("CTC")
                .unlockedBy("has_electronic_circuit", has(ICTags.CIRCUITS_BASIC))
                .save(output);

        // Machine blocks
        shaped(RecipeCategory.MISC, ICBlocks.IRON_FURNACE.get())
                .define('P', ICTags.PLATES_IRON)
                .define('F', Tags.Items.PLAYER_WORKSTATIONS_FURNACES)
                .pattern(" P ")
                .pattern("P P")
                .pattern("PFP")
                .unlockedBy("has_iron_plate", has(ICTags.PLATES_IRON))
                .save(output);

        shaped(RecipeCategory.MISC, ICBlocks.GENERATOR.get())
                .define('P', ICTags.PLATES_IRON)
                .define('B', ICItems.RE_BATTERY.get())
                .define('F', ICBlocks.IRON_FURNACE.get())
                .pattern(" B ")
                .pattern("PPP")
                .pattern(" F ")
                .unlockedBy("has_re_battery", has(ICItems.RE_BATTERY.get()))
                .save(output);

        shaped(RecipeCategory.MISC, ICBlocks.GENERATOR.get())
                .define('B', ICItems.RE_BATTERY.get())
                .define('M', ICItems.MACHINE_CASING.get())
                .define('F', Tags.Items.PLAYER_WORKSTATIONS_FURNACES)
                .pattern("B")
                .pattern("M")
                .pattern("F")
                .unlockedBy("has_re_battery", has(ICItems.RE_BATTERY.get()))
                .save(output, "innovativecontraptions:generator_from_casing");

        shaped(RecipeCategory.MISC, ICItems.SCRAP_BOX.get())
                .define('S', ICItems.SCRAP.get())
                .pattern("SSS")
                .pattern("SSS")
                .pattern("SSS")
                .unlockedBy("has_scrap", has(ICItems.SCRAP.get()))
                .save(output);

        shaped(RecipeCategory.MISC, ICBlocks.WATER_MILL.get())
                .define('S', Tags.Items.RODS_WOODEN)
                .define('W', ItemTags.PLANKS)
                .define('G', ICBlocks.GENERATOR.get())
                .pattern("SWS")
                .pattern("WGW")
                .pattern("SWS")
                .unlockedBy("has_generator", has(ICBlocks.GENERATOR.get()))
                .save(output);

        shaped(RecipeCategory.MISC, ICBlocks.WINDMILL.get())
                .define('I', Tags.Items.INGOTS_IRON)
                .define('G', ICBlocks.GENERATOR.get())
                .pattern("I I")
                .pattern(" G ")
                .pattern("I I")
                .unlockedBy("has_generator", has(ICBlocks.GENERATOR.get()))
                .save(output);

        shaped(RecipeCategory.MISC, ICItems.WOOD_ROTOR_BLADE.get())
                .define('P', ItemTags.PLANKS)
                .pattern("PPP")
                .pattern(" P ")
                .unlockedBy("has_planks", has(ItemTags.PLANKS))
                .save(output);

        shaped(RecipeCategory.MISC, ICItems.IRON_ROTOR_BLADE.get())
                .define('P', ICTags.PLATES_IRON)
                .pattern("PPP")
                .pattern(" P ")
                .unlockedBy("has_iron_plate", has(ICTags.PLATES_IRON))
                .save(output);

        rotor(ICItems.WOOD_ROTOR.get(), ICItems.WOOD_ROTOR_BLADE.get(), ICItems.IRON_SHAFT.get());
        rotor(ICItems.IRON_ROTOR.get(), ICItems.IRON_ROTOR_BLADE.get(), ICItems.IRON_SHAFT.get());
        rotor(ICItems.STEEL_ROTOR.get(), ICItems.STEEL_ROTOR_BLADE.get(), ICItems.STEEL_SHAFT.get());
        rotor(ICItems.CARBON_ROTOR.get(), ICItems.CARBON_ROTOR_BLADE.get(), ICItems.STEEL_SHAFT.get());

        shaped(RecipeCategory.MISC, ICBlocks.GEOTHERMAL_GENERATOR.get())
                .define('G', Tags.Items.GLASS_BLOCKS_COLORLESS)
                .define('C', ICItems.EMPTY_CELL.get())
                .define('I', ICTags.CASINGS_IRON)
                .define('e', ICBlocks.GENERATOR.get())
                .pattern("GCG")
                .pattern("IeI")
                .pattern("GCG")
                .unlockedBy("has_empty_cell", has(ICItems.EMPTY_CELL.get()))
                .save(output);

        shaped(RecipeCategory.MISC, ICBlocks.KINETIC_GENERATOR.get())
                .define('I', ICTags.CASINGS_IRON)
                .define('M', ICItems.ELECTRIC_MOTOR.get())
                .define('G', ICBlocks.GENERATOR.get())
                .define('S', ICItems.IRON_SHAFT.get())
                .pattern("IMI")
                .pattern("IGI")
                .pattern("ISI")
                .unlockedBy("has_electric_motor", has(ICItems.ELECTRIC_MOTOR.get()))
                .save(output);

        shaped(RecipeCategory.MISC, ICBlocks.MACERATOR.get())
                .define('F', Items.FLINT)
                .define('C', Tags.Items.COBBLESTONES_NORMAL)
                .define('I', ICItems.MACHINE_CASING.get())
                .define('E', ICTags.CIRCUITS_BASIC)
                .pattern("FFF")
                .pattern("CIC")
                .pattern(" E ")
                .unlockedBy("has_electronic_circuit", has(ICTags.CIRCUITS_BASIC))
                .save(output);

        shaped(RecipeCategory.MISC, ICBlocks.COMPRESSOR.get())
                .define('S', Tags.Items.STONES)
                .define('I', ICItems.MACHINE_CASING.get())
                .define('E', ICTags.CIRCUITS_BASIC)
                .pattern("S S")
                .pattern("SIS")
                .pattern("SES")
                .unlockedBy("has_iron_casing", has(ICTags.CASINGS_IRON))
                .save(output);

        shaped(RecipeCategory.MISC, ICBlocks.EXTRACTOR.get())
                .define('T', ICItems.TREE_TAP.get())
                .define('I', ICItems.MACHINE_CASING.get())
                .define('E', ICTags.CIRCUITS_BASIC)
                .pattern("TIT")
                .pattern("TET")
                .unlockedBy("has_electronic_circuit", has(ICTags.CIRCUITS_BASIC))
                .save(output);

        shaped(RecipeCategory.MISC, ICBlocks.ORE_WASHING_PLANT.get())
                .define('P', ICTags.PLATES_IRON)
                .define('M', ICItems.ELECTRIC_MOTOR.get())
                .define('B', Tags.Items.BUCKETS_EMPTY)
                .define('I', ICItems.MACHINE_CASING.get())
                .define('E', ICTags.CIRCUITS_BASIC)
                .pattern("PPP")
                .pattern("BIB")
                .pattern("MEM")
                .unlockedBy("has_electric_motor", has(ICItems.ELECTRIC_MOTOR.get()))
                .save(output);

        shaped(RecipeCategory.MISC, ICBlocks.BLAST_FURNACE.get())
                .define('I', ICTags.CASINGS_IRON)
                .define('H', ICItems.HEAT_CONDUCTOR.get())
                .define('M', ICItems.MACHINE_CASING.get())
                .pattern("III")
                .pattern("IMI")
                .pattern("IHI")
                .unlockedBy("has_heat_conductor", has(ICItems.HEAT_CONDUCTOR.get()))
                .save(output);

        shaped(RecipeCategory.MISC, ICBlocks.METAL_FORMER.get())
                .define('T', ICItems.TOOLBOX.get())
                .define('C', ICItems.COIL.get())
                .define('I', ICTags.CIRCUITS_BASIC)
                .define('M', ICItems.MACHINE_CASING.get())
                .pattern(" I ")
                .pattern("TMT")
                .pattern("CCC")
                .unlockedBy("has_coil", has(ICItems.COIL.get()))
                .save(output);

        shaped(RecipeCategory.MISC, ICBlocks.BATBOX.get())
                .define('W', ItemTags.PLANKS)
                .define('C', ICItems.INSULATED_TIN_CABLE.get())
                .define('B', ICItems.RE_BATTERY.get())
                .pattern("WCW")
                .pattern("BBB")
                .pattern("WWW")
                .unlockedBy("has_re_battery", has(ICItems.RE_BATTERY.get()))
                .save(output);

        shaped(RecipeCategory.MISC, ICBlocks.CESU.get())
                .define('I', ICTags.PLATES_BRONZE)
                .define('C', ICItems.INSULATED_COPPER_CABLE.get())
                .define('B', ICItems.ADVANCED_RE_BATTERY.get())
                .pattern("ICI")
                .pattern("BBB")
                .pattern("III")
                .unlockedBy("has_advanced_re_battery", has(ICItems.ADVANCED_RE_BATTERY.get()))
                .save(output);

        shaped(RecipeCategory.MISC, ICBlocks.MFE.get())
                .define('B', ICItems.MACHINE_CASING.get())
                .define('C', ICItems.DOUBLE_INSULATED_GOLD_CABLE.get())
                .define('E', ICItems.ENERGY_CRYSTAL.get())
                .pattern("CEC")
                .pattern("EBE")
                .pattern("CEC")
                .unlockedBy("has_energy_crystal", has(ICItems.ENERGY_CRYSTAL.get()))
                .save(output);

        shaped(RecipeCategory.MISC, ICBlocks.MFSU.get())
                .define('S', ICItems.ADVANCED_MACHINE_CASING.get())
                .define('C', ICTags.CIRCUITS_ADVANCED)
                .define('M', ICItems.MFE.get())
                .define('L', ICItems.LAPOTRON_CRYSTAL.get())
                .pattern("LCL")
                .pattern("LML")
                .pattern("LSL")
                .unlockedBy("has_lapotron_crystal", has(ICItems.LAPOTRON_CRYSTAL.get()))
                .save(output);

        // Plates
        plate(Tags.Items.STORAGE_BLOCKS_LAPIS, ICItems.LAPIS_PLATE.get());
        plate(Tags.Items.OBSIDIANS_NORMAL, ICItems.OBSIDIAN_PLATE.get());
        RecipeTags.assertClean();
    }

    private void rotor(ItemLike result, ItemLike blade, ItemLike shaft) {
        shaped(RecipeCategory.MISC, result)
                .define('B', blade)
                .define('S', shaft)
                .pattern(" B ")
                .pattern("BSB")
                .pattern(" B ")
                .unlockedBy("has_shaft", has(shaft))
                .save(output);
    }

    private void woodFromLog(ItemLike wood, ItemLike log) {
        shaped(RecipeCategory.BUILDING_BLOCKS, wood, 3)
                .define('#', log)
                .pattern("##")
                .pattern("##")
                .unlockedBy("has_log", has(log))
                .save(output);
    }

    private void plate(TagKey<Item> ingot, ItemLike plate) {
        shapeless(RecipeCategory.MISC, plate)
                .requires(ingot)
                .requires(ICItems.FORGE_HAMMER.get())
                .unlockedBy("has_forge_hammer", has(ICItems.FORGE_HAMMER.get()))
                .save(output);
    }

    private void casing(TagKey<Item> plate, ItemLike casing) {
        shapeless(RecipeCategory.MISC, casing, 2)
                .requires(plate)
                .requires(ICItems.FORGE_HAMMER.get())
                .unlockedBy("has_forge_hammer", has(ICItems.FORGE_HAMMER.get()))
                .save(output);
    }

    private void cable(TagKey<Item> plate, ItemLike cable, int count) {
        shapeless(RecipeCategory.MISC, cable, count)
                .requires(plate)
                .requires(ICItems.CUTTER.get())
                .unlockedBy("has_cutter", has(ICItems.CUTTER.get()))
                .save(output);
    }

    private void insulate(ItemLike cable, ItemLike insulated) {
        shapeless(RecipeCategory.MISC, insulated)
                .requires(cable)
                .requires(ICTags.RUBBERS)
                .unlockedBy("has_rubber", has(ICTags.RUBBERS))
                .save(output);
    }

    private void dustSmelt(TagKey<Item> dust, ItemLike ingot, String name) {
        SimpleCookingRecipeBuilder.smelting(
                tag(dust), RecipeCategory.MISC, CookingBookCategory.MISC, ingot, 0.3F, 200
        ).unlockedBy("has_dust", has(dust)).save(output, "innovativecontraptions:" + name);
    }

    private void oreSmelt(TagKey<Item> ore, ItemLike ingot, String name) {
        SimpleCookingRecipeBuilder.smelting(
                tag(ore), RecipeCategory.MISC, CookingBookCategory.MISC, ingot, 0.3F, 200
        ).unlockedBy("has_ore", has(ore)).save(output, "innovativecontraptions:" + name);
    }

    private void combineSmall(TagKey<Item> small, ItemLike full) {
        shapeless(RecipeCategory.MISC, full)
                .requires(tag(small), 9)
                .unlockedBy("has_small_dust", has(small))
                .save(output);
    }

    private void combineSmall(ItemLike small, ItemLike full) {
        shapeless(RecipeCategory.MISC, full)
                .requires(small, 9)
                .unlockedBy("has_small_dust", has(small))
                .save(output);
    }

    public static class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
            super(output, registries);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new ICRecipeProvider(registries, output);
        }

        @Override
        public String getName() {
            return "Innovative Contraptions Recipes";
        }
    }
}
