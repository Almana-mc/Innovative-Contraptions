package me.almana.innovativecontraptions.client.bioenergy;

import java.util.List;
import java.util.Optional;
import me.almana.innovativecontraptions.InnovativeContraptions;
import me.almana.innovativecontraptions.client.ICClientRecipes;
import me.almana.innovativecontraptions.content.bioenergy.*;
import me.almana.innovativecontraptions.registry.ICBlocks;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import mezz.jei.api.recipe.types.IRecipeHolderType;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.registration.*;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.RecipeHolder;

@JeiPlugin
public final class BioenergyJei implements IModPlugin {
    private static final IRecipeHolderType<BioenergyRecipe> DIGESTION = IRecipeHolderType.create(Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, "bio_digestion"));
    private static final IRecipeHolderType<BioenergyRecipe> FUEL = IRecipeHolderType.create(Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, "liquid_fuel_generation"));
    static IRecipeHolderType<BioenergyRecipe> type(boolean digester) { return digester ? DIGESTION : FUEL; }
    @Override public Identifier getPluginUid() { return Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, "bioenergy_jei"); }
    @Override public void registerCategories(IRecipeCategoryRegistration registration) {
        var gui = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new BioenergyCategory(gui, true), new BioenergyCategory(gui, false));
    }
    @Override public void registerRecipes(IRecipeRegistration registration) {
        var recipes = ICClientRecipes.byType(BioenergyContent.RECIPES.get());
        registration.addRecipes(DIGESTION, recipes.stream().filter(recipe -> recipe.value().fermentation()).toList());
        registration.addRecipes(FUEL, recipes.stream().filter(recipe -> !recipe.value().fermentation()).toList());
    }
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addCraftingStation(DIGESTION, ICBlocks.BIO_DIGESTER.get());
        registration.addCraftingStation(FUEL, ICBlocks.LIQUID_FUEL_GENERATOR.get());
    }
    @Override public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(transfer(true));
        registration.addRecipeTransferHandler(transfer(false));
    }
    private static IRecipeTransferInfo<BioenergyMenu, RecipeHolder<BioenergyRecipe>> transfer(boolean digester) {
        return new IRecipeTransferInfo<>() {
            public Class<BioenergyMenu> getContainerClass() { return BioenergyMenu.class; }
            public Optional<MenuType<BioenergyMenu>> getMenuType() { return Optional.of(BioenergyContent.MENU.get()); }
            public IRecipeType<RecipeHolder<BioenergyRecipe>> getRecipeType() { return type(digester); }
            public boolean canHandle(BioenergyMenu menu, RecipeHolder<BioenergyRecipe> recipe) { return menu.digester() == digester; }
            public List<Slot> getRecipeSlots(BioenergyMenu menu, RecipeHolder<BioenergyRecipe> recipe) { return menu.slots.subList(0, 1); }
            public List<Slot> getInventorySlots(BioenergyMenu menu, RecipeHolder<BioenergyRecipe> recipe) { return menu.slots.subList(menu.machineSlots(), menu.machineSlots() + 36); }
        };
    }
}
