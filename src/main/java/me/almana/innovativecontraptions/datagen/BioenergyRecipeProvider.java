package me.almana.innovativecontraptions.datagen;

import me.almana.innovativecontraptions.InnovativeContraptions;
import me.almana.innovativecontraptions.content.bioenergy.BioenergyRecipe;
import me.almana.innovativecontraptions.registry.ICBlocks;
import me.almana.innovativecontraptions.registry.ICItems;
import me.almana.innovativecontraptions.registry.ICTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe.CommonInfo;

public final class BioenergyRecipeProvider extends RecipeProvider {
    public BioenergyRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) { super(registries, output); }
    @Override public void buildRecipes() {
        shaped(RecipeCategory.MISC, ICBlocks.BIO_DIGESTER.get()).pattern("III").pattern("CCC").pattern("IHI")
                .define('I', ICTags.CASINGS_IRON).define('C', ICItems.EMPTY_CELL.get()).define('H', ICItems.HEAT_CONDUCTOR.get())
                .unlockedBy("has_heat_conductor", has(ICItems.HEAT_CONDUCTOR.get())).save(output);
        shaped(RecipeCategory.MISC, ICBlocks.LIQUID_FUEL_GENERATOR.get()).pattern("ICI").pattern("CGC").pattern("ICI")
                .define('I', ICTags.CASINGS_IRON).define('C', ICItems.EMPTY_CELL.get()).define('G', ICBlocks.GEOTHERMAL_GENERATOR.get())
                .unlockedBy("has_generator", has(ICBlocks.GEOTHERMAL_GENERATOR.get())).save(output);
        save("ferment_biomass", new BioenergyRecipe(new CommonInfo(true), Ingredient.of(ICItems.BIOMASS_CELL.get()),
                new ItemStackTemplate(ICItems.BIOGAS_CELL.get()), true, 20, 400, 4000));
        save("burn_biogas", new BioenergyRecipe(new CommonInfo(true), Ingredient.of(ICItems.BIOGAS_CELL.get()),
                new ItemStackTemplate(ICItems.EMPTY_CELL.get()), false, 1, 16, 32));
        save("burn_biomass", new BioenergyRecipe(new CommonInfo(true), Ingredient.of(ICItems.BIOMASS_CELL.get()),
                new ItemStackTemplate(ICItems.EMPTY_CELL.get()), false, 1, 8, 8));
    }
    private void save(String name, BioenergyRecipe recipe) {
        var id = Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, "bioenergy/" + name);
        //? if >=26.1 {
        output.accept(ResourceKey.create(Registries.RECIPE, id), recipe, null);
        //?} else
        //output.accept(id, recipe, null);
    }
}
