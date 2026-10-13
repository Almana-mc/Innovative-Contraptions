package me.almana.innovativecontraptions.datagen;

import me.almana.innovativecontraptions.registry.ICDataComponents;
import me.almana.innovativecontraptions.registry.ICItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.CompoundIngredient;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;

final class FarmingRecipeProvider extends RecipeProvider {
    FarmingRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) { super(registries, output); }
    @Override protected void buildRecipes() {
        supports(); supplies(); machines(); coffee();
        shapeless(RecipeCategory.MISC, ICItems.DIAMOND_DUST.get()).requires(ICItems.SMALL_DIAMOND_DUST.get(), 9)
                .unlockedBy("has_dust", has(ICItems.SMALL_DIAMOND_DUST.get())).save(output, "innovativecontraptions:diamond_dust_from_small");
    }
    private void supports() {
        shaped(RecipeCategory.MISC, ICItems.CROP_STICKS.get(), 2).define('S', Items.STICK)
                .pattern("S S").pattern("S S").unlockedBy("has_stick", has(Items.STICK)).save(output);
        shaped(RecipeCategory.BUILDING_BLOCKS, ICItems.WOODEN_SCAFFOLD.get(), 4).define('P', ItemTags.PLANKS).define('S', Items.STICK)
                .pattern("PPP").pattern(" S ").pattern("S S").unlockedBy("has_planks", has(ItemTags.PLANKS)).save(output);
        shaped(RecipeCategory.MISC, ICItems.FERMENTING_BARREL.get()).define('P', ItemTags.PLANKS).define('W', ICItems.RUBBER_WOOD.get())
                .pattern("P").pattern("W").pattern("P").unlockedBy("has_rubber_wood", has(ICItems.RUBBER_WOOD.get())).save(output);
        shaped(RecipeCategory.TOOLS, ICItems.WEEDING_TROWEL.get()).define('I', Items.IRON_INGOT).define('R', ICItems.RUBBER.get())
                .pattern("I I").pattern(" I ").pattern("RIR").unlockedBy("has_rubber", has(ICItems.RUBBER.get())).save(output);
        shapeless(RecipeCategory.MISC, ICItems.FERMENTING_BARREL.get()).requires(ICItems.FERMENTING_BARREL.get())
                .unlockedBy("has_barrel", has(ICItems.FERMENTING_BARREL.get())).save(output, "innovativecontraptions:fermenting_barrel_reset");
    }
    private void supplies() {
        shapeless(RecipeCategory.MISC, ICItems.FERTILIZER.get(), 2).requires(ICItems.SCRAP.get()).requires(Items.BONE_MEAL)
                .unlockedBy("has_scrap", has(ICItems.SCRAP.get())).save(output);
        shapeless(RecipeCategory.MISC, ICItems.FERTILIZER.get(), 2).requires(ICItems.SCRAP.get(), 2).requires(ICItems.FERTILIZER.get())
                .unlockedBy("has_fertilizer", has(ICItems.FERTILIZER.get())).save(output, "innovativecontraptions:fertilizer_from_fertilizer");
        shaped(RecipeCategory.MISC, ICItems.CROP_TREATMENT_CELL.get()).define('P', ICItems.CROP_TREATMENT_POWDER.get())
                .define('R', Items.REDSTONE).define('C', ICItems.EMPTY_CELL.get()).pattern("R").pattern("P").pattern("C")
                .unlockedBy("has_treatment_powder", has(ICItems.CROP_TREATMENT_POWDER.get())).save(output);
        shaped(RecipeCategory.TOOLS, ICItems.CROP_ANALYZER.get()).define('C', ICItems.INSULATED_COPPER_CABLE.get())
                .define('R', Items.REDSTONE).define('G', Items.GLASS).define('E', ICItems.ELECTRONIC_CIRCUIT.get())
                .pattern("CC ").pattern("RGR").pattern("RER").unlockedBy("has_circuit", has(ICItems.ELECTRONIC_CIRCUIT.get())).save(output);
    }
    private void machines() {
        shaped(RecipeCategory.MISC, ICItems.CROP_MATRON.get()).define('C', ICItems.ELECTRONIC_CIRCUIT.get()).define('H', Items.CHEST)
                .define('F', ICItems.EMPTY_CELL.get()).define('M', ICItems.MACHINE_CASING.get()).define('S', ICItems.CROP_STICKS.get())
                .pattern("CHC").pattern("FMF").pattern("SSS").unlockedBy("has_crops", has(ICItems.CROP_STICKS.get())).save(output);
        shaped(RecipeCategory.MISC, ICItems.CROP_HARVESTER.get()).define('C', ICItems.ELECTRONIC_CIRCUIT.get()).define('H', Items.CHEST)
                .define('S', Items.SHEARS).define('M', ICItems.MACHINE_CASING.get()).define('K', ICItems.CROP_STICKS.get())
                .pattern("CHC").pattern("SMS").pattern("KKK").unlockedBy("has_crops", has(ICItems.CROP_STICKS.get())).save(output);
    }
    private Ingredient water() {
        var amount = ICDataComponents.FLUID_AMOUNT.get();
        return CompoundIngredient.of(Ingredient.of(Items.WATER_BUCKET),
                //? if >=26.1 {
                DataComponentIngredient.of(
                        DataComponentPatch.builder().remove(amount).build(), ICItems.WATER_CELL.get()),
                DataComponentIngredient.of(amount, 1000, ICItems.WATER_CELL.get()));
                //?} else {
                /*DataComponentIngredient.of(true, net.minecraft.core.component.DataComponentPredicate.EMPTY, ICItems.WATER_CELL.get()),
                DataComponentIngredient.of(false, amount, 1000, ICItems.WATER_CELL.get()));
                *///?}
    }
    private void coffee() {
        shaped(RecipeCategory.MISC, ICItems.MUG.get()).define('S', Ingredient.of(Items.STONE, Items.ANDESITE, Items.DIORITE, Items.GRANITE))
                .pattern("SS ").pattern("SSS").pattern("SS ").unlockedBy("has_stone", has(Items.STONE)).save(output);
        shapeless(RecipeCategory.MISC, ICItems.GROUND_COFFEE.get()).requires(ICItems.COFFEE_BEANS.get())
                .unlockedBy("has_coffee", has(ICItems.COFFEE_BEANS.get())).save(output);
        shapeless(RecipeCategory.FOOD, ICItems.COLD_COFFEE.get()).requires(ICItems.MUG.get()).requires(ICItems.GROUND_COFFEE.get())
                .requires(water())
                .unlockedBy("has_coffee", has(ICItems.GROUND_COFFEE.get())).save(output);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(ICItems.COLD_COFFEE.get()), RecipeCategory.FOOD, CookingBookCategory.FOOD,
                ICItems.DARK_COFFEE.get(), .1F, 200).unlockedBy("has_coffee", has(ICItems.COLD_COFFEE.get())).save(output);
        shapeless(RecipeCategory.FOOD, ICItems.COFFEE.get()).requires(ICItems.DARK_COFFEE.get()).requires(Items.SUGAR).requires(Items.MILK_BUCKET)
                .unlockedBy("has_coffee", has(ICItems.DARK_COFFEE.get())).save(output);
    }
}

