package me.almana.innovativecontraptions.datagen;

import me.almana.innovativecontraptions.registry.ICItems;
import me.almana.innovativecontraptions.registry.ICBlocks;
import me.almana.innovativecontraptions.registry.ICTags;
import net.minecraft.world.item.Items;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;

final class ICPartTwoRecipeProvider extends RecipeProvider {
    ICPartTwoRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        exchangers();
        structuralMaterials();
        insulation();
        shaped(RecipeCategory.MISC, ICItems.ADVANCED_MACHINE_CASING.get())
                .define('S', ICTags.PLATES_STEEL).define('C', ICTags.PLATES_CARBON)
                .define('A', ICItems.ALLOY_INGOT.get()).define('M', ICItems.MACHINE_CASING.get())
                .pattern("SCS").pattern("AMA").pattern("SCS")
                .unlockedBy("has_carbon", has(ICTags.PLATES_CARBON)).save(output);
        shaped(RecipeCategory.MISC, ICItems.ADVANCED_MACHINE_CASING.get())
                .define('S', ICTags.PLATES_STEEL).define('C', ICTags.PLATES_CARBON)
                .define('A', ICItems.ALLOY_INGOT.get()).define('M', ICItems.MACHINE_CASING.get())
                .pattern("SAS").pattern("CMC").pattern("SAS")
                .unlockedBy("has_carbon", has(ICTags.PLATES_CARBON))
                .save(output, "innovativecontraptions:advanced_machine_casing_reversed");
        shaped(RecipeCategory.MISC, ICItems.RECYCLER.get())
                .define('G', Tags.Items.DUSTS_GLOWSTONE).define('D', Items.DIRT)
                .define('C', ICItems.COMPRESSOR.get()).define('I', Tags.Items.INGOTS_IRON)
                .pattern(" G ").pattern("DCD").pattern("IDI")
                .unlockedBy("has_compressor", has(ICItems.COMPRESSOR.get())).save(output);
        shaped(RecipeCategory.MISC, ICItems.LAPOTRON_CRYSTAL.get())
                .define('L', ICTags.DUSTS_LAPIS).define('C', ICTags.CIRCUITS_ADVANCED)
                .define('E', ICItems.ENERGY_CRYSTAL.get())
                .pattern("LCL").pattern("LEL").pattern("LCL")
                .unlockedBy("has_energy_crystal", has(ICItems.ENERGY_CRYSTAL.get())).save(output);
        shaped(RecipeCategory.MISC, ICItems.POWER_UNIT.get())
                .define('B', ICItems.RE_BATTERY.get()).define('E', ICTags.CIRCUITS_BASIC)
                .define('C', ICItems.COPPER_CABLE.get()).define('M', ICItems.ELECTRIC_MOTOR.get())
                .define('I', ICTags.CASINGS_IRON).pattern("BCI").pattern("BEM").pattern("BCI")
                .unlockedBy("has_motor", has(ICItems.ELECTRIC_MOTOR.get())).save(output);
    }

    private void exchangers() {
        shaped(RecipeCategory.MISC, ICItems.REACTOR_HEAT_EXCHANGER.get())
                .define('C', ICTags.PLATES_COPPER).define('H', ICItems.HEAT_EXCHANGER.get())
                .pattern("CCC").pattern("CHC").pattern("CCC")
                .unlockedBy("has_exchanger", has(ICItems.HEAT_EXCHANGER.get())).save(output);
        shaped(RecipeCategory.MISC, ICItems.COMPONENT_HEAT_EXCHANGER.get())
                .define('G', ICTags.PLATES_GOLD).define('H', ICItems.HEAT_EXCHANGER.get())
                .pattern(" G ").pattern("GHG").pattern(" G ")
                .unlockedBy("has_exchanger", has(ICItems.HEAT_EXCHANGER.get())).save(output);
        shaped(RecipeCategory.MISC, ICItems.ADVANCED_HEAT_EXCHANGER.get())
                .define('G', ICTags.PLATES_LAPIS).define('c', ICTags.CIRCUITS_BASIC)
                .define('S', ICItems.HEAT_EXCHANGER.get()).define('C', ICTags.PLATES_COPPER)
                .pattern("GcG").pattern("SCS").pattern("GcG")
                .unlockedBy("has_exchanger", has(ICItems.HEAT_EXCHANGER.get())).save(output);
    }

    private void insulation() {
        insulated(ICItems.DOUBLE_INSULATED_HV_CABLE.get(), ICItems.HV_CABLE.get(), 2, "double_hv_from_bare");
        insulated(ICItems.TRIPLE_INSULATED_HV_CABLE.get(), ICItems.HV_CABLE.get(), 3, "triple_hv_from_bare");
        insulated(ICItems.DOUBLE_INSULATED_HV_CABLE.get(), ICItems.INSULATED_HV_CABLE.get(), 1, "double_hv_from_single");
        insulated(ICItems.TRIPLE_INSULATED_HV_CABLE.get(), ICItems.INSULATED_HV_CABLE.get(), 2, "triple_hv_from_single");
        insulated(ICItems.TRIPLE_INSULATED_HV_CABLE.get(), ICItems.DOUBLE_INSULATED_HV_CABLE.get(), 1, "triple_hv_from_double");
    }

    private void insulated(ItemLike result, ItemLike cable, int rubber, String id) {
        shapeless(RecipeCategory.MISC, result).requires(cable).requires(tag(ICTags.RUBBERS), rubber)
                .unlockedBy("has_cable", has(cable)).save(output, "innovativecontraptions:" + id);
    }

    private void structuralMaterials() {
        shaped(RecipeCategory.BUILDING_BLOCKS, ICItems.LUMINATOR.get(), 8)
                .define('G', Tags.Items.GLASS_BLOCKS_COLORLESS).define('I', ICTags.CASINGS_IRON)
                .define('C', ICItems.INSULATED_COPPER_CABLE.get()).define('T', ICItems.TIN_CABLE.get())
                .pattern("ICI").pattern("GTG").pattern("GGG")
                .unlockedBy("has_cable", has(ICItems.INSULATED_COPPER_CABLE.get())).save(output);
        shaped(RecipeCategory.BUILDING_BLOCKS, ICBlocks.REINFORCED_GLASS.get(), 7)
                .define('G', Tags.Items.GLASS_BLOCKS_COLORLESS).define('A', ICItems.ALLOY_INGOT.get())
                .pattern("GAG").pattern("GGG").pattern("GAG")
                .unlockedBy("has_alloy", has(ICItems.ALLOY_INGOT.get())).save(output);
        shaped(RecipeCategory.BUILDING_BLOCKS, ICBlocks.REINFORCED_GLASS.get(), 7)
                .define('G', Tags.Items.GLASS_BLOCKS_COLORLESS).define('A', ICItems.ALLOY_INGOT.get())
                .pattern("GGG").pattern("AGA").pattern("GGG")
                .unlockedBy("has_alloy", has(ICItems.ALLOY_INGOT.get())).save(output, "innovativecontraptions:reinforced_glass_rotated");
        shaped(RecipeCategory.MISC, ICItems.IRIDIUM_PLATE.get())
                .define('I', ICItems.IRIDIUM_ORE.get()).define('A', ICItems.ALLOY_INGOT.get())
                .define('D', Tags.Items.GEMS_DIAMOND).pattern("IAI").pattern("ADA").pattern("IAI")
                .unlockedBy("has_iridium", has(ICItems.IRIDIUM_SHARD.get())).save(output);
    }
}
