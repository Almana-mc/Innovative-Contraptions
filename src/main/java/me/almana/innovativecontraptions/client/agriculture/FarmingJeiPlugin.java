package me.almana.innovativecontraptions.client.agriculture;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import me.almana.innovativecontraptions.InnovativeContraptions;
import me.almana.innovativecontraptions.content.agriculture.CropSpecies;
import me.almana.innovativecontraptions.registry.ICItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

@JeiPlugin
public final class FarmingJeiPlugin implements IModPlugin {
    public static final IRecipeType<FarmingJeiRecipe> CARE = IRecipeType.create(InnovativeContraptions.MODID, "crop_care", FarmingJeiRecipe.class);
    public static final IRecipeType<FarmingJeiRecipe> HARVEST = IRecipeType.create(InnovativeContraptions.MODID, "crop_harvesting", FarmingJeiRecipe.class);
    @Override public Identifier getPluginUid() { return Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, "farming_jei"); }
    @Override public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new FarmingRecipeCategory(registration.getJeiHelpers().getGuiHelper(), true),
                new FarmingRecipeCategory(registration.getJeiHelpers().getGuiHelper(), false));
    }
    @Override public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(CARE, List.of(new FarmingJeiRecipe(CropSpecies.EMPTY, 0), new FarmingJeiRecipe(CropSpecies.EMPTY, 1), new FarmingJeiRecipe(CropSpecies.EMPTY, 2)));
        registration.addRecipes(HARVEST, Arrays.stream(CropSpecies.values()).filter(crop -> crop != CropSpecies.EMPTY && crop != CropSpecies.WEED)
                .map(crop -> new FarmingJeiRecipe(crop, -1)).toList());
        registration.addIngredientInfo(ICItems.CROP_STICKS.get(), Component.translatable("jei.innovativecontraptions.farming.genetics"));
    }
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addCraftingStation(CARE, ICItems.CROP_MATRON.get());
        registration.addCraftingStation(HARVEST, ICItems.CROP_HARVESTER.get());
    }
    @Override public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(new FarmingTransfer(registration.getTransferHelper()));
    }
    @Override public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGuiContainerHandler(FarmingScreen.class, new IGuiContainerHandler<>() {
            @Override public Collection<IGuiClickableArea> getGuiClickableAreas(FarmingScreen screen, double x, double y) {
                return List.of(IGuiClickableArea.createBasic(FarmingScreen.RECIPE_X, FarmingScreen.RECIPE_Y,
                        FarmingScreen.RECIPE_WIDTH, FarmingScreen.RECIPE_HEIGHT, screen.getMenu().matron() ? CARE : HARVEST));
            }
        });
    }
}

