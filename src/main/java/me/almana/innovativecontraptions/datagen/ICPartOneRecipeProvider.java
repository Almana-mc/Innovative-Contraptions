package me.almana.innovativecontraptions.datagen;

import me.almana.innovativecontraptions.registry.ICItems;
import me.almana.innovativecontraptions.registry.ICBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

final class ICPartOneRecipeProvider extends RecipeProvider {
    ICPartOneRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        components();
        batteries();
        rotorMaterials();
        shaped(RecipeCategory.MISC, ICBlocks.KINETIC_WIND_TURBINE.get())
                .define('S', ICItems.IRON_SHAFT.get()).define('M', ICItems.MACHINE_CASING.get())
                .pattern("S S").pattern(" M ")
                .unlockedBy("has_shaft", has(ICItems.IRON_SHAFT.get())).save(output);
        shaped(RecipeCategory.MISC, ICBlocks.ELECTRIC_HEATER.get())
                .define('I', ICItems.IRON_CASING.get()).define('H', ICItems.HEAT_CONDUCTOR.get())
                .define('C', ICItems.ELECTRONIC_CIRCUIT.get()).define('B', ICItems.RE_BATTERY.get())
                .pattern("IHI").pattern("ICI").pattern("IBI")
                .unlockedBy("has_heat_conductor", has(ICItems.HEAT_CONDUCTOR.get())).save(output);
        purifiedSmelting(ICItems.PURIFIED_CRUSHED_IRON.get(), Items.IRON_INGOT, "iron");
        purifiedSmelting(ICItems.PURIFIED_CRUSHED_GOLD.get(), Items.GOLD_INGOT, "gold");
        purifiedSmelting(ICItems.PURIFIED_CRUSHED_COPPER.get(), Items.COPPER_INGOT, "copper");
        purifiedSmelting(ICItems.PURIFIED_CRUSHED_TIN.get(), ICItems.TIN_INGOT.get(), "tin");
        purifiedSmelting(ICItems.PURIFIED_CRUSHED_LEAD.get(), ICItems.LEAD_INGOT.get(), "lead");
        shapeless(RecipeCategory.TOOLS, ICItems.ELECTRIC_WRENCH.get())
                .requires(ICItems.WRENCH.get()).requires(ICItems.SMALL_POWER_UNIT.get())
                .unlockedBy("has_power_unit", has(ICItems.SMALL_POWER_UNIT.get())).save(output);
        shapeless(RecipeCategory.MISC, ICItems.HYDRATED_TIN_DUST.get())
                .requires(ICItems.TIN_DUST.get()).requires(Ingredient.of(Items.WATER_BUCKET, ICItems.WATER_CELL.get()))
                .unlockedBy("has_tin_dust", has(ICItems.TIN_DUST.get())).save(output);
        shapeless(RecipeCategory.MISC, ICItems.DOUBLE_INSULATED_GOLD_CABLE.get())
                .requires(ICItems.GOLD_CABLE.get()).requires(ICItems.RUBBER.get(), 2)
                .unlockedBy("has_rubber", has(ICItems.RUBBER.get())).save(output, "innovativecontraptions:double_insulated_gold_cable_direct");
    }

    private void components() {
        shaped(RecipeCategory.MISC, ICItems.MACHINE_CASING.get())
                .define('P', ICItems.IRON_PLATE.get()).pattern("PPP").pattern("P P").pattern("PPP")
                .unlockedBy("has_iron_plate", has(ICItems.IRON_PLATE.get())).save(output);
        shaped(RecipeCategory.MISC, ICItems.TOOLBOX.get())
                .define('B', ICItems.BRONZE_CASING.get()).define('C', Items.CHEST)
                .pattern("BCB").pattern("BBB")
                .unlockedBy("has_bronze_casing", has(ICItems.BRONZE_CASING.get())).save(output);
        shaped(RecipeCategory.MISC, ICItems.ENERGIUM_DUST.get(), 9)
                .define('D', ICItems.DIAMOND_DUST.get()).define('R', Items.REDSTONE)
                .pattern("RDR").pattern("DRD").pattern("RDR")
                .unlockedBy("has_diamond_dust", has(ICItems.DIAMOND_DUST.get())).save(output);
    }

    private void batteries() {
        shaped(RecipeCategory.MISC, ICItems.ADVANCED_RE_BATTERY.get())
                .define('C', ICItems.INSULATED_COPPER_CABLE.get()).define('B', ICItems.BRONZE_CASING.get())
                .define('S', ICItems.SULFUR_DUST.get()).define('L', ICItems.LEAD_DUST.get())
                .pattern("CBC").pattern("BSB").pattern("BLB")
                .unlockedBy("has_sulfur_dust", has(ICItems.SULFUR_DUST.get())).save(output);
        shaped(RecipeCategory.MISC, ICItems.CHARGING_RE_BATTERY.get())
                .define('C', ICItems.ELECTRONIC_CIRCUIT.get()).define('B', ICItems.RE_BATTERY.get())
                .pattern("CBC").pattern("B B").pattern("CBC")
                .unlockedBy("has_re_battery", has(ICItems.RE_BATTERY.get())).save(output);
        shaped(RecipeCategory.MISC, ICItems.ADVANCED_CHARGING_RE_BATTERY.get())
                .define('B', ICItems.ADVANCED_RE_BATTERY.get()).define('H', ICItems.HEAT_EXCHANGER.get())
                .define('C', ICItems.CHARGING_RE_BATTERY.get())
                .pattern("HBH").pattern("BCB").pattern("HBH")
                .unlockedBy("has_advanced_battery", has(ICItems.ADVANCED_RE_BATTERY.get())).save(output);
    }

    private void rotorMaterials() {
        shaped(RecipeCategory.BUILDING_BLOCKS, ICBlocks.STEEL_BLOCK.get())
                .define('I', ICItems.STEEL_INGOT.get()).pattern("III").pattern("III").pattern("III")
                .unlockedBy("has_steel", has(ICItems.STEEL_INGOT.get())).save(output);
        shapeless(RecipeCategory.MISC, ICItems.STEEL_INGOT.get(), 9).requires(ICBlocks.STEEL_BLOCK.get())
                .unlockedBy("has_steel_block", has(ICBlocks.STEEL_BLOCK.get())).save(output, "innovativecontraptions:steel_ingots_from_block");
        shaped(RecipeCategory.MISC, ICItems.CARBON_FIBRE.get())
                .define('C', ICItems.COAL_DUST.get()).pattern("CC").pattern("CC")
                .unlockedBy("has_coal_dust", has(ICItems.COAL_DUST.get())).save(output);
        shapeless(RecipeCategory.MISC, ICItems.CARBON_MESH.get()).requires(ICItems.CARBON_FIBRE.get(), 2)
                .unlockedBy("has_carbon_fibre", has(ICItems.CARBON_FIBRE.get())).save(output);
        shaped(RecipeCategory.MISC, ICItems.STEEL_ROTOR_BLADE.get())
                .define('P', ICItems.STEEL_PLATE.get()).pattern("PPP").pattern(" P ")
                .unlockedBy("has_steel_plate", has(ICItems.STEEL_PLATE.get())).save(output);
        shaped(RecipeCategory.MISC, ICItems.CARBON_ROTOR_BLADE.get())
                .define('P', ICItems.CARBON_PLATE.get()).pattern("PPP").pattern(" P ")
                .unlockedBy("has_carbon_plate", has(ICItems.CARBON_PLATE.get())).save(output);
    }

    private void purifiedSmelting(ItemLike input, ItemLike outputItem, String metal) {
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(input), RecipeCategory.MISC, CookingBookCategory.MISC,
                outputItem, 0.3F, 200).unlockedBy("has_purified_ore", has(input))
                .save(output, "innovativecontraptions:" + metal + "_ingot_from_purified_crushed");
    }
}
