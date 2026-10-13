package me.almana.innovativecontraptions.datagen;

import me.almana.innovativecontraptions.registry.ICItems;
import me.almana.innovativecontraptions.registry.ICBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;

final class EquipmentRecipeProvider extends RecipeProvider {
    EquipmentRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) { super(registries, output); }

    @Override
    protected void buildRecipes() {
        bronze();
        hazard();
        packs();
        utility();
        nano();
        quantum();
        tools();
    }

    private void bronze() {
        shaped(RecipeCategory.COMBAT, ICItems.BRONZE_HELMET.get()).define('B', ICItems.BRONZE_INGOT.get())
                .pattern("BBB").pattern("B B").unlockedBy("has_bronze", has(ICItems.BRONZE_INGOT.get())).save(output);
        shaped(RecipeCategory.COMBAT, ICItems.BRONZE_CHESTPLATE.get()).define('B', ICItems.BRONZE_INGOT.get())
                .pattern("B B").pattern("BBB").pattern("BBB").unlockedBy("has_bronze", has(ICItems.BRONZE_INGOT.get())).save(output);
        shaped(RecipeCategory.COMBAT, ICItems.BRONZE_LEGGINGS.get()).define('B', ICItems.BRONZE_INGOT.get())
                .pattern("BBB").pattern("B B").pattern("B B").unlockedBy("has_bronze", has(ICItems.BRONZE_INGOT.get())).save(output);
        shaped(RecipeCategory.COMBAT, ICItems.BRONZE_BOOTS.get()).define('B', ICItems.BRONZE_INGOT.get())
                .pattern("B B").pattern("B B").unlockedBy("has_bronze", has(ICItems.BRONZE_INGOT.get())).save(output);
        shaped(RecipeCategory.TOOLS, ICItems.BRONZE_PICKAXE.get()).define('B', ICItems.BRONZE_INGOT.get()).define('S', Items.STICK)
                .pattern("BBB").pattern(" S ").pattern(" S ").unlockedBy("has_bronze", has(ICItems.BRONZE_INGOT.get())).save(output);
        shaped(RecipeCategory.TOOLS, ICItems.BRONZE_AXE.get()).define('B', ICItems.BRONZE_INGOT.get()).define('S', Items.STICK)
                .pattern("BB").pattern("BS").pattern(" S").unlockedBy("has_bronze", has(ICItems.BRONZE_INGOT.get())).save(output);
        shaped(RecipeCategory.TOOLS, ICItems.BRONZE_SHOVEL.get()).define('B', ICItems.BRONZE_INGOT.get()).define('S', Items.STICK)
                .pattern("B").pattern("S").pattern("S").unlockedBy("has_bronze", has(ICItems.BRONZE_INGOT.get())).save(output);
        shaped(RecipeCategory.TOOLS, ICItems.BRONZE_HOE.get()).define('B', ICItems.BRONZE_INGOT.get()).define('S', Items.STICK)
                .pattern("BB").pattern(" S").pattern(" S").unlockedBy("has_bronze", has(ICItems.BRONZE_INGOT.get())).save(output);
        shaped(RecipeCategory.COMBAT, ICItems.BRONZE_SWORD.get()).define('B', ICItems.BRONZE_INGOT.get()).define('S', Items.STICK)
                .pattern("B").pattern("B").pattern("S").unlockedBy("has_bronze", has(ICItems.BRONZE_INGOT.get())).save(output);
    }

    private void hazard() {
        shaped(RecipeCategory.COMBAT, ICItems.HAZMAT_HELMET.get()).define('R', ICItems.RUBBER.get()).define('D', Items.ORANGE_DYE)
                .define('G', Items.GLASS).define('I', Items.IRON_BARS).pattern(" D ").pattern("RGR").pattern("RIR")
                .unlockedBy("has_rubber", has(ICItems.RUBBER.get())).save(output);
        shaped(RecipeCategory.COMBAT, ICItems.HAZMAT_CHESTPLATE.get()).define('R', ICItems.RUBBER.get()).define('D', Items.ORANGE_DYE)
                .pattern("R R").pattern("RDR").pattern("RDR").unlockedBy("has_rubber", has(ICItems.RUBBER.get())).save(output);
        shaped(RecipeCategory.COMBAT, ICItems.HAZMAT_LEGGINGS.get()).define('R', ICItems.RUBBER.get()).define('D', Items.ORANGE_DYE)
                .pattern("RDR").pattern("R R").pattern("R R").unlockedBy("has_rubber", has(ICItems.RUBBER.get())).save(output);
        shaped(RecipeCategory.COMBAT, ICItems.RUBBER_BOOTS.get()).define('R', ICItems.RUBBER.get()).define('W', ItemTags.WOOL)
                .pattern("R R").pattern("R R").pattern("RWR").unlockedBy("has_rubber", has(ICItems.RUBBER.get())).save(output);
        shaped(RecipeCategory.COMBAT, ICItems.ALLOY_CHESTPLATE.get()).define('A', ICItems.ALLOY_INGOT.get())
                .define('I', Items.IRON_CHESTPLATE).define('L', Items.LEATHER_CHESTPLATE)
                .pattern("A A").pattern("AIA").pattern("ALA").unlockedBy("has_alloy", has(ICItems.ALLOY_INGOT.get())).save(output);
        shaped(RecipeCategory.COMBAT, ICItems.ALLOY_CHESTPLATE.get()).define('A', ICItems.ALLOY_INGOT.get())
                .define('I', Items.IRON_CHESTPLATE).define('L', Items.LEATHER_CHESTPLATE)
                .pattern("A A").pattern("ALA").pattern("AIA").unlockedBy("has_alloy", has(ICItems.ALLOY_INGOT.get()))
                .save(output, "innovativecontraptions:alloy_chestplate_reversed");
    }

    private void packs() {
        shaped(RecipeCategory.MISC, ICItems.BATPACK.get()).define('B', ICItems.RE_BATTERY.get())
                .define('E', ICItems.ELECTRONIC_CIRCUIT.get()).define('P', ItemTags.PLANKS)
                .pattern("BEB").pattern("BPB").pattern("B B").unlockedBy("has_battery", has(ICItems.RE_BATTERY.get())).save(output);
        shaped(RecipeCategory.MISC, ICItems.ADVANCED_BATPACK.get()).define('B', ICItems.ADVANCED_RE_BATTERY.get())
                .define('E', ICItems.ELECTRONIC_CIRCUIT.get()).define('P', ICItems.COPPER_CASING.get())
                .pattern("BEB").pattern("BPB").pattern("B B").unlockedBy("has_battery", has(ICItems.ADVANCED_RE_BATTERY.get())).save(output);
        shaped(RecipeCategory.MISC, ICItems.ENERGY_PACK.get()).define('E', ICItems.ENERGY_CRYSTAL.get())
                .define('C', ICItems.ADVANCED_CIRCUIT.get()).define('I', ICItems.IRON_CASING.get())
                .pattern("CIC").pattern("EIE").pattern("I I").unlockedBy("has_crystal", has(ICItems.ENERGY_CRYSTAL.get())).save(output);
        shaped(RecipeCategory.MISC, ICItems.ELECTRIC_JETPACK.get()).define('I', ICItems.IRON_CASING.get())
                .define('B', ICBlocks.BATBOX.get()).define('G', Items.GLOWSTONE_DUST).define('C', ICItems.ADVANCED_CIRCUIT.get())
                .pattern("ICI").pattern("IBI").pattern("G G").unlockedBy("has_storage", has(ICBlocks.BATBOX.get())).save(output);
        shaped(RecipeCategory.MISC, ICItems.JETPACK.get()).define('I', ICItems.IRON_CASING.get())
                .define('R', Items.REDSTONE).define('F', ICItems.EMPTY_CELL.get()).define('C', ICItems.ELECTRONIC_CIRCUIT.get())
                .pattern("ICI").pattern("IFI").pattern("R R").unlockedBy("has_cell", has(ICItems.EMPTY_CELL.get())).save(output);
    }

    private void utility() {
        shaped(RecipeCategory.MISC, ICItems.SOLAR_HELMET.get()).define('I', Items.IRON_INGOT).define('S', ICBlocks.SOLAR_PANEL.get())
                .define('C', ICItems.INSULATED_COPPER_CABLE.get()).pattern("III").pattern("ISI").pattern("CCC")
                .unlockedBy("has_solar", has(ICBlocks.SOLAR_PANEL.get())).save(output);
        shaped(RecipeCategory.MISC, ICItems.STATIC_BOOTS.get()).define('I', Items.IRON_INGOT).define('W', ItemTags.WOOL)
                .define('C', ICItems.INSULATED_COPPER_CABLE.get()).pattern("I I").pattern("IWI").pattern("CCC")
                .unlockedBy("has_cable", has(ICItems.INSULATED_COPPER_CABLE.get())).save(output);
        shaped(RecipeCategory.MISC, ICItems.SOLAR_HELMET.get()).define('H', Items.IRON_HELMET).define('S', ICBlocks.SOLAR_PANEL.get())
                .define('C', ICItems.INSULATED_COPPER_CABLE.get()).pattern(" H ").pattern(" S ").pattern("CCC")
                .unlockedBy("has_solar", has(ICBlocks.SOLAR_PANEL.get())).save(output, "innovativecontraptions:solar_helmet_from_helmet");
        shaped(RecipeCategory.MISC, ICItems.STATIC_BOOTS.get()).define('H', Items.IRON_BOOTS).define('W', ItemTags.WOOL)
                .define('C', ICItems.INSULATED_COPPER_CABLE.get()).pattern(" H ").pattern(" W ").pattern("CCC")
                .unlockedBy("has_cable", has(ICItems.INSULATED_COPPER_CABLE.get())).save(output, "innovativecontraptions:static_boots_from_boots");
        shaped(RecipeCategory.MISC, ICItems.NIGHTVISION_GOGGLES.get()).define('H', ICItems.ADVANCED_HEAT_EXCHANGER.get())
                .define('B', ICItems.ADVANCED_RE_BATTERY.get()).define('L', ICItems.LUMINATOR.get()).define('R', ICItems.RUBBER.get())
                .define('G', ICItems.REINFORCED_GLASS.get()).define('C', ICItems.ADVANCED_CIRCUIT.get())
                .pattern("HBH").pattern("LGL").pattern("RCR")
                .unlockedBy("has_exchanger", has(ICItems.ADVANCED_HEAT_EXCHANGER.get())).save(output);
    }

    private void nano() {
        shaped(RecipeCategory.COMBAT, ICItems.NANO_HELMET.get()).define('C', ICItems.CARBON_PLATE.get())
                .define('E', ICItems.ENERGY_CRYSTAL.get()).define('G', ICItems.NIGHTVISION_GOGGLES.get())
                .pattern("CEC").pattern("CGC").unlockedBy("has_carbon", has(ICItems.CARBON_PLATE.get())).save(output);
        shaped(RecipeCategory.COMBAT, ICItems.NANO_CHESTPLATE.get()).define('C', ICItems.CARBON_PLATE.get()).define('E', ICItems.ENERGY_CRYSTAL.get())
                .pattern("C C").pattern("CEC").pattern("CCC").unlockedBy("has_carbon", has(ICItems.CARBON_PLATE.get())).save(output);
        shaped(RecipeCategory.COMBAT, ICItems.NANO_LEGGINGS.get()).define('C', ICItems.CARBON_PLATE.get()).define('E', ICItems.ENERGY_CRYSTAL.get())
                .pattern("CEC").pattern("C C").pattern("C C").unlockedBy("has_carbon", has(ICItems.CARBON_PLATE.get())).save(output);
        shaped(RecipeCategory.COMBAT, ICItems.NANO_BOOTS.get()).define('C', ICItems.CARBON_PLATE.get()).define('E', ICItems.ENERGY_CRYSTAL.get())
                .pattern("C C").pattern("CEC").unlockedBy("has_carbon", has(ICItems.CARBON_PLATE.get())).save(output);
    }

    private void quantum() {
        shaped(RecipeCategory.COMBAT, ICItems.QUANTUM_HELMET.get()).define('N', ICItems.NANO_HELMET.get()).define('P', ICItems.IRIDIUM_PLATE.get())
                .define('G', ICItems.REINFORCED_GLASS.get()).define('C', ICItems.ADVANCED_CIRCUIT.get())
                .define('S', ICItems.HAZMAT_HELMET.get()).define('L', ICItems.LAPOTRON_CRYSTAL.get())
                .pattern("GNG").pattern("PLP").pattern("CSC").unlockedBy("has_nano", has(ICItems.NANO_HELMET.get())).save(output);
        shaped(RecipeCategory.COMBAT, ICItems.QUANTUM_CHESTPLATE.get()).define('N', ICItems.NANO_CHESTPLATE.get()).define('P', ICItems.IRIDIUM_PLATE.get())
                .define('A', ICItems.ALLOY_INGOT.get()).define('L', ICItems.LAPOTRON_CRYSTAL.get()).define('J', ICItems.ELECTRIC_JETPACK.get())
                .pattern("ANA").pattern("PLP").pattern("PJP").unlockedBy("has_nano", has(ICItems.NANO_CHESTPLATE.get())).save(output);
        shaped(RecipeCategory.COMBAT, ICItems.QUANTUM_LEGGINGS.get()).define('N', ICItems.NANO_LEGGINGS.get()).define('P', ICItems.IRIDIUM_PLATE.get())
                .define('C', ICItems.MACHINE_CASING.get()).define('G', Items.GLOWSTONE_DUST).define('L', ICItems.LAPOTRON_CRYSTAL.get())
                .pattern("CLC").pattern("PNP").pattern("G G").unlockedBy("has_nano", has(ICItems.NANO_LEGGINGS.get())).save(output);
        shaped(RecipeCategory.COMBAT, ICItems.QUANTUM_BOOTS.get()).define('N', ICItems.NANO_BOOTS.get()).define('P', ICItems.IRIDIUM_PLATE.get())
                .define('R', ICItems.RUBBER_BOOTS.get()).define('L', ICItems.LAPOTRON_CRYSTAL.get())
                .pattern("PNP").pattern("RLR").unlockedBy("has_nano", has(ICItems.NANO_BOOTS.get())).save(output);
    }

    private void tools() {
        shaped(RecipeCategory.TOOLS, ICItems.MINING_DRILL.get()).define('I', ICItems.IRON_PLATE.get()).define('P', ICItems.POWER_UNIT.get())
                .pattern(" I ").pattern("III").pattern("IPI").unlockedBy("has_power_unit", has(ICItems.POWER_UNIT.get())).save(output);
        shaped(RecipeCategory.TOOLS, ICItems.DIAMOND_DRILL.get()).define('M', ICItems.MINING_DRILL.get()).define('D', Items.DIAMOND)
                .pattern(" D ").pattern("DMD")
                .unlockedBy("has_drill", has(ICItems.MINING_DRILL.get())).save(output);
        shaped(RecipeCategory.TOOLS, ICItems.IRIDIUM_DRILL.get()).define('D', ICItems.DIAMOND_DRILL.get()).define('C', ICItems.ENERGY_CRYSTAL.get())
                .define('I', ICItems.IRIDIUM_PLATE.get()).pattern(" I ").pattern("IDI").pattern(" C ")
                .unlockedBy("has_iridium", has(ICItems.IRIDIUM_PLATE.get())).save(output);
        shaped(RecipeCategory.TOOLS, ICItems.CHAINSAW.get()).define('I', ICItems.IRON_PLATE.get()).define('P', ICItems.POWER_UNIT.get())
                .pattern(" II").pattern("III").pattern("P  ").unlockedBy("has_power_unit", has(ICItems.POWER_UNIT.get())).save(output);
        shaped(RecipeCategory.TOOLS, ICItems.ELECTRIC_HOE.get()).define('I', ICItems.IRON_PLATE.get()).define('P', ICItems.SMALL_POWER_UNIT.get())
                .pattern("II").pattern(" I").pattern(" P").unlockedBy("has_power_unit", has(ICItems.SMALL_POWER_UNIT.get())).save(output);
        shapeless(RecipeCategory.TOOLS, ICItems.ELECTRIC_TAP.get()).requires(ICItems.TREE_TAP.get()).requires(ICItems.SMALL_POWER_UNIT.get())
                .unlockedBy("has_power_unit", has(ICItems.SMALL_POWER_UNIT.get())).save(output);
        shaped(RecipeCategory.COMBAT, ICItems.ENERGY_BLADE.get()).define('C', ICItems.CARBON_PLATE.get()).define('E', ICItems.ENERGY_CRYSTAL.get())
                .define('A', ICItems.ALLOY_INGOT.get()).define('G', Items.GLOWSTONE_DUST).pattern("GA ").pattern("GA ").pattern("CEC")
                .unlockedBy("has_carbon", has(ICItems.CARBON_PLATE.get())).save(output);
        shaped(RecipeCategory.TOOLS, ICItems.MINING_LASER.get()).define('A', ICItems.ALLOY_INGOT.get()).define('C', ICItems.ADVANCED_CIRCUIT.get())
                .define('E', ICItems.ENERGY_CRYSTAL.get()).define('R', Items.REDSTONE).pattern("RRE").pattern("AAC").pattern(" AA")
                .unlockedBy("has_crystal", has(ICItems.ENERGY_CRYSTAL.get())).save(output);
    }
}
