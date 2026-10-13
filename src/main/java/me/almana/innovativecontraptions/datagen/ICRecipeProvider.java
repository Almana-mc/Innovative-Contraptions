package me.almana.innovativecontraptions.datagen;

import me.almana.innovativecontraptions.registry.ICBlocks;
import me.almana.innovativecontraptions.registry.ICItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.concurrent.CompletableFuture;

public class ICRecipeProvider extends RecipeProvider {
    protected ICRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, new ChargedRecipeOutput(output));
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
        ThermalRecipeProvider.build(output);

        shaped(RecipeCategory.TOOLS, ICItems.TREE_TAP.get())
                .define('P', ItemTags.PLANKS)
                .pattern(" P ")
                .pattern("PPP")
                .pattern("P  ")
                .unlockedBy("has_planks", has(ItemTags.PLANKS))
                .save(output);

        shaped(RecipeCategory.TOOLS, ICItems.WRENCH.get())
                .define('B', ICItems.BRONZE_INGOT.get())
                .pattern("B B")
                .pattern("BBB")
                .pattern(" B ")
                .unlockedBy("has_bronze_ingot", has(ICItems.BRONZE_INGOT.get()))
                .save(output);

        shaped(RecipeCategory.TOOLS, ICItems.FORGE_HAMMER.get())
                .define('I', Items.IRON_INGOT)
                .define('S', Items.STICK)
                .pattern("III")
                .pattern("ISI")
                .pattern(" S ")
                .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
                .save(output);

        shaped(RecipeCategory.TOOLS, ICItems.CUTTER.get())
                .define('P', ICItems.IRON_PLATE.get())
                .define('I', Items.IRON_INGOT)
                .pattern("P P")
                .pattern(" P ")
                .pattern("I I")
                .unlockedBy("has_iron_plate", has(ICItems.IRON_PLATE.get()))
                .save(output);

        woodFromLog(ICBlocks.RUBBER_WOOD.get(), ICBlocks.RUBBER_LOG.get());
        woodFromLog(ICBlocks.STRIPPED_RUBBER_WOOD.get(), ICBlocks.STRIPPED_RUBBER_LOG.get());

        plate(Items.IRON_INGOT, ICItems.IRON_PLATE.get());
        plate(Items.GOLD_INGOT, ICItems.GOLD_PLATE.get());
        plate(Items.COPPER_INGOT, ICItems.COPPER_PLATE.get());
        plate(ICItems.TIN_INGOT.get(), ICItems.TIN_PLATE.get());
        plate(ICItems.BRONZE_INGOT.get(), ICItems.BRONZE_PLATE.get());
        plate(ICItems.LEAD_INGOT.get(), ICItems.LEAD_PLATE.get());
        plate(ICItems.STEEL_INGOT.get(), ICItems.STEEL_PLATE.get());

        casing(ICItems.IRON_PLATE.get(), ICItems.IRON_CASING.get());
        casing(ICItems.BRONZE_PLATE.get(), ICItems.BRONZE_CASING.get());
        casing(ICItems.TIN_PLATE.get(), ICItems.TIN_CASING.get());
        casing(ICItems.COPPER_PLATE.get(), ICItems.COPPER_CASING.get());
        casing(ICItems.GOLD_PLATE.get(), ICItems.GOLD_CASING.get());
        casing(ICItems.LEAD_PLATE.get(), ICItems.LEAD_CASING.get());
        casing(ICItems.STEEL_PLATE.get(), ICItems.STEEL_CASING.get());

        cable(ICItems.TIN_PLATE.get(), ICItems.TIN_CABLE.get(), 3);
        cable(ICItems.COPPER_PLATE.get(), ICItems.COPPER_CABLE.get(), 2);
        insulate(ICItems.TIN_CABLE.get(), ICItems.INSULATED_TIN_CABLE.get());
        insulate(ICItems.COPPER_CABLE.get(), ICItems.INSULATED_COPPER_CABLE.get());
        cable(ICItems.GOLD_PLATE.get(), ICItems.GOLD_CABLE.get(), 4);
        insulate(ICItems.GOLD_CABLE.get(), ICItems.INSULATED_GOLD_CABLE.get());
        insulate(ICItems.INSULATED_GOLD_CABLE.get(), ICItems.DOUBLE_INSULATED_GOLD_CABLE.get());
        cable(ICItems.IRON_PLATE.get(), ICItems.HV_CABLE.get(), 4);
        insulate(ICItems.HV_CABLE.get(), ICItems.INSULATED_HV_CABLE.get());

        shaped(RecipeCategory.MISC, ICItems.GLASS_FIBRE_CABLE.get(), 6)
                .define('G', Items.GLASS)
                .define('E', ICItems.ENERGIUM_DUST.get())
                .define('S', ICItems.SILVER_DUST.get())
                .pattern("GGG")
                .pattern("ESE")
                .pattern("GGG")
                .unlockedBy("has_energium_dust", has(ICItems.ENERGIUM_DUST.get()))
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

        dustSmelt(ICItems.IRON_DUST.get(), Items.IRON_INGOT, "iron_ingot_from_dust");
        dustSmelt(ICItems.GOLD_DUST.get(), Items.GOLD_INGOT, "gold_ingot_from_dust");
        dustSmelt(ICItems.COPPER_DUST.get(), Items.COPPER_INGOT, "copper_ingot_from_dust");
        dustSmelt(ICItems.TIN_DUST.get(), ICItems.TIN_INGOT.get(), "tin_ingot_from_dust");
        dustSmelt(ICItems.BRONZE_DUST.get(), ICItems.BRONZE_INGOT.get(), "bronze_ingot_from_dust");
        dustSmelt(ICItems.LEAD_DUST.get(), ICItems.LEAD_INGOT.get(), "lead_ingot_from_dust");
        dustSmelt(ICItems.SILVER_DUST.get(), ICItems.SILVER_INGOT.get(), "silver_ingot_from_dust");
        dustSmelt(ICItems.CRUSHED_IRON.get(), Items.IRON_INGOT, "iron_ingot_from_crushed_iron");
        dustSmelt(ICItems.CRUSHED_GOLD.get(), Items.GOLD_INGOT, "gold_ingot_from_crushed_gold");
        dustSmelt(ICItems.CRUSHED_COPPER.get(), Items.COPPER_INGOT, "copper_ingot_from_crushed_copper");
        dustSmelt(ICItems.CRUSHED_TIN.get(), ICItems.TIN_INGOT.get(), "tin_ingot_from_crushed_tin");
        dustSmelt(ICItems.CRUSHED_LEAD.get(), ICItems.LEAD_INGOT.get(), "lead_ingot_from_crushed_lead");

        combineSmall(ICItems.SMALL_IRON_DUST.get(), ICItems.IRON_DUST.get());
        combineSmall(ICItems.SMALL_GOLD_DUST.get(), ICItems.GOLD_DUST.get());
        combineSmall(ICItems.SMALL_COPPER_DUST.get(), ICItems.COPPER_DUST.get());
        combineSmall(ICItems.SMALL_TIN_DUST.get(), ICItems.TIN_DUST.get());
        combineSmall(ICItems.SMALL_BRONZE_DUST.get(), ICItems.BRONZE_DUST.get());
        combineSmall(ICItems.SMALL_LEAD_DUST.get(), ICItems.LEAD_DUST.get());
        combineSmall(ICItems.SMALL_SILVER_DUST.get(), ICItems.SILVER_DUST.get());
        combineSmall(ICItems.SMALL_LAPIS_DUST.get(), ICItems.LAPIS_DUST.get());
        combineSmall(ICItems.SMALL_LITHIUM_DUST.get(), ICItems.LITHIUM_DUST.get());
        combineSmall(ICItems.SMALL_OBSIDIAN_DUST.get(), ICItems.OBSIDIAN_DUST.get());
        combineSmall(ICItems.SMALL_SULFUR_DUST.get(), ICItems.SULFUR_DUST.get());
        combineSmall(ICItems.SMALL_URANIUM_235.get(), ICItems.URANIUM_235.get());
        combineSmall(ICItems.SMALL_URANIUM_238.get(), ICItems.URANIUM_238.get());
        combineSmall(ICItems.SMALL_PLUTONIUM.get(), ICItems.PLUTONIUM.get());

        shapeless(RecipeCategory.MISC, ICItems.BRONZE_DUST.get(), 4)
                .requires(Ingredient.of(ICItems.COPPER_DUST.get(), ICItems.CRUSHED_COPPER.get(), ICItems.PURIFIED_CRUSHED_COPPER.get()), 3)
                .requires(Ingredient.of(ICItems.TIN_DUST.get(), ICItems.CRUSHED_TIN.get(), ICItems.PURIFIED_CRUSHED_TIN.get()))
                .unlockedBy("has_copper_dust", has(ICItems.COPPER_DUST.get()))
                .save(output, "innovativecontraptions:bronze_dust_from_copper_tin");

        shaped(RecipeCategory.MISC, ICItems.MIXED_METAL_INGOT.get(), 2)
                .define('I', ICItems.IRON_PLATE.get())
                .define('B', ICItems.BRONZE_PLATE.get())
                .define('T', ICItems.TIN_PLATE.get())
                .pattern("III")
                .pattern("BBB")
                .pattern("TTT")
                .unlockedBy("has_bronze_plate", has(ICItems.BRONZE_PLATE.get()))
                .save(output);

        shaped(RecipeCategory.MISC, ICItems.TIN_CAN.get(), 6)
                .define('P', ICItems.TIN_PLATE.get())
                .pattern("P P")
                .pattern("PPP")
                .unlockedBy("has_tin_plate", has(ICItems.TIN_PLATE.get()))
                .save(output);

        shapeless(RecipeCategory.MISC, ICItems.PLANT_BALL.get())
                .requires(Items.WHEAT, 8)
                .unlockedBy("has_wheat", has(Items.WHEAT))
                .save(output);

        shaped(RecipeCategory.MISC, ICItems.ELECTRONIC_CIRCUIT.get())
                .define('C', ICItems.INSULATED_COPPER_CABLE.get())
                .define('R', Items.REDSTONE)
                .define('P', ICItems.IRON_PLATE.get())
                .pattern("CCC")
                .pattern("RPR")
                .pattern("CCC")
                .unlockedBy("has_insulated_copper_cable", has(ICItems.INSULATED_COPPER_CABLE.get()))
                .save(output);

        shaped(RecipeCategory.MISC, ICItems.ADVANCED_CIRCUIT.get())
                .define('E', ICItems.ELECTRONIC_CIRCUIT.get())
                .define('R', Items.REDSTONE)
                .define('G', Items.GLOWSTONE_DUST)
                .define('L', Items.LAPIS_LAZULI)
                .pattern("RGR")
                .pattern("LEL")
                .pattern("RGR")
                .unlockedBy("has_electronic_circuit", has(ICItems.ELECTRONIC_CIRCUIT.get()))
                .save(output);

        SimpleCookingRecipeBuilder.smelting(
                Ingredient.of(ICItems.STICKY_RESIN.get()),
                RecipeCategory.MISC,
                CookingBookCategory.MISC,
                ICItems.RUBBER.get(),
                0.3F,
                200
        ).unlockedBy("has_sticky_resin", has(ICItems.STICKY_RESIN.get())).save(output, "innovativecontraptions:rubber_from_smelting_sticky_resin");

        oreSmelt(ICBlocks.TIN_ORE.get(), ICItems.TIN_INGOT.get(), "tin_ingot_from_smelting");
        oreSmelt(ICBlocks.LEAD_ORE.get(), ICItems.LEAD_INGOT.get(), "lead_ingot_from_smelting");

        // Component items
        shaped(RecipeCategory.MISC, ICItems.COIL.get())
                .define('C', ICItems.COPPER_CABLE.get())
                .define('I', Items.IRON_INGOT)
                .pattern("CCC")
                .pattern("CIC")
                .pattern("CCC")
                .unlockedBy("has_copper_cable", has(ICItems.COPPER_CABLE.get()))
                .save(output);

        shaped(RecipeCategory.MISC, ICItems.ELECTRIC_MOTOR.get())
                .define('C', ICItems.COIL.get())
                .define('T', ICItems.TIN_CASING.get())
                .define('I', Items.IRON_INGOT)
                .pattern(" C ")
                .pattern("TIT")
                .pattern(" C ")
                .unlockedBy("has_coil", has(ICItems.COIL.get()))
                .save(output);

        shaped(RecipeCategory.MISC, ICItems.HEAT_CONDUCTOR.get())
                .define('R', ICItems.RUBBER.get())
                .define('C', ICItems.COPPER_PLATE.get())
                .pattern("RCR")
                .pattern("RCR")
                .pattern("RCR")
                .unlockedBy("has_copper_plate", has(ICItems.COPPER_PLATE.get()))
                .save(output);

        shaped(RecipeCategory.MISC, ICItems.RE_BATTERY.get())
                .define('T', ICItems.TIN_CASING.get())
                .define('C', ICItems.INSULATED_TIN_CABLE.get())
                .define('R', Items.REDSTONE)
                .pattern(" C ")
                .pattern("TRT")
                .pattern("TRT")
                .unlockedBy("has_tin_casing", has(ICItems.TIN_CASING.get()))
                .save(output);

        shaped(RecipeCategory.MISC, ICItems.SMALL_POWER_UNIT.get())
                .define('C', ICItems.COPPER_CABLE.get())
                .define('M', ICItems.ELECTRIC_MOTOR.get())
                .define('I', ICItems.IRON_CASING.get())
                .define('E', ICItems.ELECTRONIC_CIRCUIT.get())
                .define('B', ICItems.RE_BATTERY.get())
                .pattern(" CI")
                .pattern("BEM")
                .pattern(" CI")
                .unlockedBy("has_electric_motor", has(ICItems.ELECTRIC_MOTOR.get()))
                .save(output);

        shaped(RecipeCategory.MISC, ICItems.EMPTY_CELL.get(), 4)
                .define('T', ICItems.TIN_CASING.get())
                .define('G', Items.GLASS_PANE)
                .pattern(" T ")
                .pattern("TGT")
                .pattern(" T ")
                .unlockedBy("has_tin_casing", has(ICItems.TIN_CASING.get()))
                .save(output);

        shaped(RecipeCategory.MISC, ICItems.HEAT_EXCHANGER.get())
                .define('T', ICItems.TIN_PLATE.get())
                .define('C', ICItems.COPPER_PLATE.get())
                .define('E', ICItems.ELECTRONIC_CIRCUIT.get())
                .pattern("CEC")
                .pattern("TCT")
                .pattern("CTC")
                .unlockedBy("has_electronic_circuit", has(ICItems.ELECTRONIC_CIRCUIT.get()))
                .save(output);

        // Machine blocks
        shaped(RecipeCategory.MISC, ICBlocks.IRON_FURNACE.get())
                .define('P', ICItems.IRON_PLATE.get())
                .define('F', Items.FURNACE)
                .pattern(" P ")
                .pattern("P P")
                .pattern("PFP")
                .unlockedBy("has_iron_plate", has(ICItems.IRON_PLATE.get()))
                .save(output);

        shaped(RecipeCategory.MISC, ICBlocks.GENERATOR.get())
                .define('P', ICItems.IRON_PLATE.get())
                .define('B', ICItems.RE_BATTERY.get())
                .define('F', ICBlocks.IRON_FURNACE.get())
                .pattern(" B ")
                .pattern("PPP")
                .pattern(" F ")
                .unlockedBy("has_re_battery", has(ICItems.RE_BATTERY.get()))
                .save(output);

        shaped(RecipeCategory.MISC, ICBlocks.WATER_MILL.get())
                .define('S', Items.STICK)
                .define('W', ItemTags.PLANKS)
                .define('G', ICBlocks.GENERATOR.get())
                .pattern("SWS")
                .pattern("WGW")
                .pattern("SWS")
                .unlockedBy("has_generator", has(ICBlocks.GENERATOR.get()))
                .save(output);

        shaped(RecipeCategory.MISC, ICBlocks.WINDMILL.get())
                .define('I', Items.IRON_INGOT)
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
                .define('P', ICItems.IRON_PLATE.get())
                .pattern("PPP")
                .pattern(" P ")
                .unlockedBy("has_iron_plate", has(ICItems.IRON_PLATE.get()))
                .save(output);

        rotor(ICItems.WOOD_ROTOR.get(), ICItems.WOOD_ROTOR_BLADE.get(), ICItems.IRON_SHAFT.get());
        rotor(ICItems.IRON_ROTOR.get(), ICItems.IRON_ROTOR_BLADE.get(), ICItems.IRON_SHAFT.get());
        rotor(ICItems.STEEL_ROTOR.get(), ICItems.STEEL_ROTOR_BLADE.get(), ICItems.STEEL_SHAFT.get());
        rotor(ICItems.CARBON_ROTOR.get(), ICItems.CARBON_ROTOR_BLADE.get(), ICItems.STEEL_SHAFT.get());

        shaped(RecipeCategory.MISC, ICBlocks.GEOTHERMAL_GENERATOR.get())
                .define('G', Items.GLASS)
                .define('C', ICItems.EMPTY_CELL.get())
                .define('I', ICItems.IRON_CASING.get())
                .define('e', ICBlocks.GENERATOR.get())
                .pattern("GCG")
                .pattern("IeI")
                .pattern("GCG")
                .unlockedBy("has_empty_cell", has(ICItems.EMPTY_CELL.get()))
                .save(output);

        shaped(RecipeCategory.MISC, ICBlocks.KINETIC_GENERATOR.get())
                .define('I', ICItems.IRON_CASING.get())
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
                .define('C', Items.COBBLESTONE)
                .define('I', ICItems.MACHINE_CASING.get())
                .define('E', ICItems.ELECTRONIC_CIRCUIT.get())
                .pattern("FFF")
                .pattern("CIC")
                .pattern(" E ")
                .unlockedBy("has_electronic_circuit", has(ICItems.ELECTRONIC_CIRCUIT.get()))
                .save(output);

        shaped(RecipeCategory.MISC, ICBlocks.COMPRESSOR.get())
                .define('S', Items.STONE)
                .define('I', ICItems.MACHINE_CASING.get())
                .define('E', ICItems.ELECTRONIC_CIRCUIT.get())
                .pattern("S S")
                .pattern("SIS")
                .pattern("SES")
                .unlockedBy("has_iron_casing", has(ICItems.IRON_CASING.get()))
                .save(output);

        shaped(RecipeCategory.MISC, ICBlocks.EXTRACTOR.get())
                .define('T', ICItems.TREE_TAP.get())
                .define('I', ICItems.MACHINE_CASING.get())
                .define('E', ICItems.ELECTRONIC_CIRCUIT.get())
                .pattern("TIT")
                .pattern("TET")
                .unlockedBy("has_electronic_circuit", has(ICItems.ELECTRONIC_CIRCUIT.get()))
                .save(output);

        shaped(RecipeCategory.MISC, ICBlocks.ORE_WASHING_PLANT.get())
                .define('P', ICItems.IRON_PLATE.get())
                .define('M', ICItems.ELECTRIC_MOTOR.get())
                .define('B', Items.BUCKET)
                .define('I', ICItems.MACHINE_CASING.get())
                .define('E', ICItems.ELECTRONIC_CIRCUIT.get())
                .pattern("PPP")
                .pattern("BIB")
                .pattern("MEM")
                .unlockedBy("has_electric_motor", has(ICItems.ELECTRIC_MOTOR.get()))
                .save(output);

        shaped(RecipeCategory.MISC, ICBlocks.BLAST_FURNACE.get())
                .define('I', ICItems.IRON_CASING.get())
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
                .define('I', ICItems.ELECTRONIC_CIRCUIT.get())
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
                .define('I', ICItems.BRONZE_PLATE.get())
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
                .define('C', ICItems.ADVANCED_CIRCUIT.get())
                .define('M', ICItems.MFE.get())
                .define('L', ICItems.LAPOTRON_CRYSTAL.get())
                .pattern("LCL")
                .pattern("LML")
                .pattern("LSL")
                .unlockedBy("has_lapotron_crystal", has(ICItems.LAPOTRON_CRYSTAL.get()))
                .save(output);

        // Plates
        plate(Items.LAPIS_BLOCK, ICItems.LAPIS_PLATE.get());
        plate(Items.OBSIDIAN, ICItems.OBSIDIAN_PLATE.get());
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

    private void plate(ItemLike ingot, ItemLike plate) {
        shapeless(RecipeCategory.MISC, plate)
                .requires(ingot)
                .requires(ICItems.FORGE_HAMMER.get())
                .unlockedBy("has_forge_hammer", has(ICItems.FORGE_HAMMER.get()))
                .save(output);
    }

    private void casing(ItemLike plate, ItemLike casing) {
        shapeless(RecipeCategory.MISC, casing, 2)
                .requires(plate)
                .requires(ICItems.FORGE_HAMMER.get())
                .unlockedBy("has_forge_hammer", has(ICItems.FORGE_HAMMER.get()))
                .save(output);
    }

    private void cable(ItemLike plate, ItemLike cable, int count) {
        shapeless(RecipeCategory.MISC, cable, count)
                .requires(plate)
                .requires(ICItems.CUTTER.get())
                .unlockedBy("has_cutter", has(ICItems.CUTTER.get()))
                .save(output);
    }

    private void insulate(ItemLike cable, ItemLike insulated) {
        shapeless(RecipeCategory.MISC, insulated)
                .requires(cable)
                .requires(ICItems.RUBBER.get())
                .unlockedBy("has_rubber", has(ICItems.RUBBER.get()))
                .save(output);
    }

    private void dustSmelt(ItemLike dust, ItemLike ingot, String name) {
        SimpleCookingRecipeBuilder.smelting(
                Ingredient.of(dust), RecipeCategory.MISC, CookingBookCategory.MISC, ingot, 0.3F, 200
        ).unlockedBy("has_dust", has(dust)).save(output, "innovativecontraptions:" + name);
    }

    private void oreSmelt(ItemLike ore, ItemLike ingot, String name) {
        SimpleCookingRecipeBuilder.smelting(
                Ingredient.of(ore), RecipeCategory.MISC, CookingBookCategory.MISC, ingot, 0.3F, 200
        ).unlockedBy("has_ore", has(ore)).save(output, "innovativecontraptions:" + name);
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
