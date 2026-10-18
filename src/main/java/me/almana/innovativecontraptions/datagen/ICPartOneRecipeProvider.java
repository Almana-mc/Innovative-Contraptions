package me.almana.innovativecontraptions.datagen;

import me.almana.innovativecontraptions.registry.ICItems;
import me.almana.innovativecontraptions.registry.ICBlocks;
import me.almana.innovativecontraptions.registry.ICTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.crafting.CompoundIngredient;

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
                .define('I', ICTags.CASINGS_IRON).define('H', ICItems.HEAT_CONDUCTOR.get())
                .define('C', ICTags.CIRCUITS_BASIC).define('B', ICItems.RE_BATTERY.get())
                .pattern("IHI").pattern("ICI").pattern("IBI")
                .unlockedBy("has_heat_conductor", has(ICItems.HEAT_CONDUCTOR.get())).save(output);
        purifiedSmelting(ICTags.PURIFIED_CRUSHED_ORES_IRON, Items.IRON_INGOT, "iron");
        purifiedSmelting(ICTags.PURIFIED_CRUSHED_ORES_GOLD, Items.GOLD_INGOT, "gold");
        purifiedSmelting(ICTags.PURIFIED_CRUSHED_ORES_COPPER, Items.COPPER_INGOT, "copper");
        purifiedSmelting(ICTags.PURIFIED_CRUSHED_ORES_TIN, ICItems.TIN_INGOT.get(), "tin");
        purifiedSmelting(ICTags.PURIFIED_CRUSHED_ORES_LEAD, ICItems.LEAD_INGOT.get(), "lead");
        shapeless(RecipeCategory.TOOLS, ICItems.ELECTRIC_WRENCH.get())
                .requires(ICItems.WRENCH.get()).requires(ICItems.SMALL_POWER_UNIT.get())
                .unlockedBy("has_power_unit", has(ICItems.SMALL_POWER_UNIT.get())).save(output);
        shapeless(RecipeCategory.MISC, ICItems.HYDRATED_TIN_DUST.get())
                .requires(ICTags.DUSTS_TIN).requires(CompoundIngredient.of(tag(Tags.Items.BUCKETS_WATER), Ingredient.of(ICItems.WATER_CELL.get())))
                .unlockedBy("has_tin_dust", has(ICTags.DUSTS_TIN)).save(output);
        shapeless(RecipeCategory.MISC, ICItems.DOUBLE_INSULATED_GOLD_CABLE.get())
                .requires(ICItems.GOLD_CABLE.get()).requires(tag(ICTags.RUBBERS), 2)
                .unlockedBy("has_rubber", has(ICTags.RUBBERS)).save(output, "innovativecontraptions:double_insulated_gold_cable_direct");
    }

    private void components() {
        shaped(RecipeCategory.MISC, ICItems.MACHINE_CASING.get())
                .define('P', ICTags.PLATES_IRON).pattern("PPP").pattern("P P").pattern("PPP")
                .unlockedBy("has_iron_plate", has(ICTags.PLATES_IRON)).save(output);
        shaped(RecipeCategory.MISC, ICItems.TOOLBOX.get())
                .define('B', ICTags.CASINGS_BRONZE).define('C', Tags.Items.CHESTS_WOODEN)
                .pattern("BCB").pattern("BBB")
                .unlockedBy("has_bronze_casing", has(ICTags.CASINGS_BRONZE)).save(output);
        shaped(RecipeCategory.MISC, ICItems.ENERGIUM_DUST.get(), 9)
                .define('D', ICTags.DUSTS_DIAMOND).define('R', Tags.Items.DUSTS_REDSTONE)
                .pattern("RDR").pattern("DRD").pattern("RDR")
                .unlockedBy("has_diamond_dust", has(ICTags.DUSTS_DIAMOND)).save(output);
    }

    private void batteries() {
        shaped(RecipeCategory.MISC, ICItems.ADVANCED_RE_BATTERY.get())
                .define('C', ICItems.INSULATED_COPPER_CABLE.get()).define('B', ICTags.CASINGS_BRONZE)
                .define('S', ICTags.DUSTS_SULFUR).define('L', ICTags.DUSTS_LEAD)
                .pattern("CBC").pattern("BSB").pattern("BLB")
                .unlockedBy("has_sulfur_dust", has(ICTags.DUSTS_SULFUR)).save(output);
        shaped(RecipeCategory.MISC, ICItems.CHARGING_RE_BATTERY.get())
                .define('C', ICTags.CIRCUITS_BASIC).define('B', ICItems.RE_BATTERY.get())
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
                .define('I', ICTags.INGOTS_STEEL).pattern("III").pattern("III").pattern("III")
                .unlockedBy("has_steel", has(ICTags.INGOTS_STEEL)).save(output);
        shapeless(RecipeCategory.MISC, ICItems.STEEL_INGOT.get(), 9).requires(ICTags.STORAGE_BLOCKS_STEEL)
                .unlockedBy("has_steel_block", has(ICTags.STORAGE_BLOCKS_STEEL)).save(output, "innovativecontraptions:steel_ingots_from_block");
        shaped(RecipeCategory.MISC, ICItems.CARBON_FIBRE.get())
                .define('C', ICTags.DUSTS_COAL).pattern("CC").pattern("CC")
                .unlockedBy("has_coal_dust", has(ICTags.DUSTS_COAL)).save(output);
        shapeless(RecipeCategory.MISC, ICItems.CARBON_MESH.get()).requires(ICItems.CARBON_FIBRE.get(), 2)
                .unlockedBy("has_carbon_fibre", has(ICItems.CARBON_FIBRE.get())).save(output);
        shaped(RecipeCategory.MISC, ICItems.STEEL_ROTOR_BLADE.get())
                .define('P', ICTags.PLATES_STEEL).pattern("PPP").pattern(" P ")
                .unlockedBy("has_steel_plate", has(ICTags.PLATES_STEEL)).save(output);
        shaped(RecipeCategory.MISC, ICItems.CARBON_ROTOR_BLADE.get())
                .define('P', ICTags.PLATES_CARBON).pattern("PPP").pattern(" P ")
                .unlockedBy("has_carbon_plate", has(ICTags.PLATES_CARBON)).save(output);
    }

    private void purifiedSmelting(TagKey<Item> input, ItemLike outputItem, String metal) {
        SimpleCookingRecipeBuilder.smelting(tag(input), RecipeCategory.MISC, CookingBookCategory.MISC,
                outputItem, 0.3F, 200).unlockedBy("has_purified_ore", has(input))
                .save(output, "innovativecontraptions:" + metal + "_ingot_from_purified_crushed");
    }
}
