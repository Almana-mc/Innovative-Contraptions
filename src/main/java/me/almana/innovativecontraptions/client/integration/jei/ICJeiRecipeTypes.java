package me.almana.innovativecontraptions.client.integration.jei;

import me.almana.innovativecontraptions.InnovativeContraptions;
import me.almana.innovativecontraptions.content.machine.recipe.MaceratingRecipe;
import me.almana.innovativecontraptions.registry.ICRecipes;
import mezz.jei.api.recipe.types.IRecipeHolderType;
import mezz.jei.api.recipe.types.IRecipeType;

import java.util.function.Supplier;

public final class ICJeiRecipeTypes {
    public static final IRecipeType<GeneratorJeiRecipe> GENERATOR = generator("generator");
    public static final IRecipeType<GeneratorJeiRecipe> SOLAR_PANEL = generator("solar_panel");
    public static final IRecipeType<GeneratorJeiRecipe> WATER_MILL = generator("water_mill");
    public static final IRecipeType<GeneratorJeiRecipe> WINDMILL = generator("windmill");
    public static final IRecipeType<GeneratorJeiRecipe> GEOTHERMAL_GENERATOR = generator("geothermal_generator");
    public static final IRecipeType<GeneratorJeiRecipe> KINETIC_GENERATOR = generator("kinetic_generator");
    public static final Supplier<IRecipeHolderType<MaceratingRecipe>> MACERATING =
            IRecipeHolderType.createDeferred(ICRecipes.MACERATING_TYPE);

    private static IRecipeType<GeneratorJeiRecipe> generator(String path) {
        return IRecipeType.create(InnovativeContraptions.MODID, path, GeneratorJeiRecipe.class);
    }

    private ICJeiRecipeTypes() {
    }
}
