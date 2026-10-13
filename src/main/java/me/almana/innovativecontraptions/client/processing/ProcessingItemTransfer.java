package me.almana.innovativecontraptions.client.processing;

import me.almana.innovativecontraptions.content.processing.ProcessingKind;
import me.almana.innovativecontraptions.content.processing.ProcessingMenu;
import me.almana.innovativecontraptions.content.processing.ProcessingRecipe;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.RecipeHolder;
import java.util.Optional;

public final class ProcessingItemTransfer implements IRecipeTransferHandler<ProcessingMenu, RecipeHolder<ProcessingRecipe>> {
    private final IRecipeTransferHandler<ProcessingMenu, RecipeHolder<ProcessingRecipe>> items;
    private final IRecipeTransferHandlerHelper helper;

    public ProcessingItemTransfer(ProcessingKind kind, IRecipeTransferHandlerHelper helper) {
        this.helper = helper;
        items = helper.createUnregisteredRecipeTransferHandler(new ProcessingTransfer(kind, helper));
    }

    @Override public Class<? extends ProcessingMenu> getContainerClass() { return items.getContainerClass(); }
    @Override public Optional<MenuType<ProcessingMenu>> getMenuType() { return items.getMenuType(); }
    @Override public IRecipeType<RecipeHolder<ProcessingRecipe>> getRecipeType() { return items.getRecipeType(); }
    @Override public IRecipeTransferError transferRecipe(ProcessingMenu menu, RecipeHolder<ProcessingRecipe> recipe,
            IRecipeSlotsView slots, Player player, boolean maximum, boolean transfer) {
        var itemSlots = slots.getSlotViews().stream()
                .filter(slot -> slot.getRole() != RecipeIngredientRole.INPUT || slot.getItemStacks().findAny().isPresent())
                .toList();
        return items.transferRecipe(menu, recipe, helper.createRecipeSlotsView(itemSlots), player, maximum, transfer);
    }
}
