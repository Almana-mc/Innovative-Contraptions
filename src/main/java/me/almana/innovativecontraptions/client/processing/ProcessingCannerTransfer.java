package me.almana.innovativecontraptions.client.processing;

import java.util.List;
import java.util.Optional;
import me.almana.innovativecontraptions.content.processing.*;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.transfer.*;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;

public final class ProcessingCannerTransfer implements IRecipeTransferHandler<ProcessingCannerMenu, ProcessingCannerDisplay> {
    private final IRecipeTransferHandlerHelper helper;
    private final IRecipeTransferHandler<ProcessingCannerMenu, ProcessingCannerDisplay> items;
    public ProcessingCannerTransfer(IRecipeTransferHandlerHelper helper) {
        this.helper = helper;
        items = helper.createUnregisteredRecipeTransferHandler(new IRecipeTransferInfo<>() {
            @Override public Class<ProcessingCannerMenu> getContainerClass() { return ProcessingCannerMenu.class; }
            @Override public Optional<MenuType<ProcessingCannerMenu>> getMenuType() { return Optional.of(ProcessingContent.CANNER_MENU.get()); }
            @Override public IRecipeType<ProcessingCannerDisplay> getRecipeType() { return ProcessingCannerJei.TYPE; }
            @Override public boolean canHandle(ProcessingCannerMenu menu, ProcessingCannerDisplay recipe) { return menu.value(5) == recipe.mode(); }
            @Override public IRecipeTransferError getHandlingError(ProcessingCannerMenu menu, ProcessingCannerDisplay recipe) { return helper.createUserErrorWithTooltip(Component.translatable("jei.innovativecontraptions.processing.select_machine_mode")); }
            @Override public List<Slot> getRecipeSlots(ProcessingCannerMenu menu, ProcessingCannerDisplay recipe) {
                return recipe.mode() == 0 ? List.of(menu.slots.get(0), menu.slots.get(1)) : List.of(menu.slots.get(recipe.mode() == 3 ? 0 : 1));
            }
            @Override public List<Slot> getInventorySlots(ProcessingCannerMenu menu, ProcessingCannerDisplay recipe) { return menu.slots.subList(8, 44); }
        });
    }
    @Override public Class<ProcessingCannerMenu> getContainerClass() { return ProcessingCannerMenu.class; }
    @Override public Optional<MenuType<ProcessingCannerMenu>> getMenuType() { return Optional.of(ProcessingContent.CANNER_MENU.get()); }
    @Override public IRecipeType<ProcessingCannerDisplay> getRecipeType() { return ProcessingCannerJei.TYPE; }
    @Override public IRecipeTransferError transferRecipe(ProcessingCannerMenu menu, ProcessingCannerDisplay recipe, IRecipeSlotsView slots,
            Player player, boolean maximum, boolean transfer) {
        return items.transferRecipe(menu, recipe, helper.createRecipeSlotsView(slots.getSlotViews().stream()
                .filter(slot -> slot.getRole() != RecipeIngredientRole.INPUT || slot.getItemStacks().findAny().isPresent()).toList()), player, maximum, transfer);
    }
}
