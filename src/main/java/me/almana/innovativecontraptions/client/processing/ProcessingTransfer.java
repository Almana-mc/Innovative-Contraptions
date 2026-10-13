package me.almana.innovativecontraptions.client.processing;

import me.almana.innovativecontraptions.content.processing.*;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.RecipeHolder;
import java.util.List;
import java.util.Optional;

public final class ProcessingTransfer implements IRecipeTransferInfo<ProcessingMenu, RecipeHolder<ProcessingRecipe>> {
    private final ProcessingKind kind;
    private final IRecipeTransferHandlerHelper helper;
    public ProcessingTransfer(ProcessingKind kind, IRecipeTransferHandlerHelper helper) { this.kind = kind; this.helper = helper; }
    @Override public Class<ProcessingMenu> getContainerClass() { return ProcessingMenu.class; }
    @Override public Optional<MenuType<ProcessingMenu>> getMenuType() { return Optional.of(ProcessingContent.MENU.get()); }
    @Override public IRecipeType<RecipeHolder<ProcessingRecipe>> getRecipeType() { return ProcessingJeiPlugin.type(kind); }
    @Override public boolean canHandle(ProcessingMenu menu, RecipeHolder<ProcessingRecipe> recipe) { return menu.kind() == kind; }
    @Override public IRecipeTransferError getHandlingError(ProcessingMenu menu, RecipeHolder<ProcessingRecipe> recipe) {
        return helper.createUserErrorWithTooltip(Component.translatable("jei.innovativecontraptions.processing.select_machine_mode"));
    }
    @Override public List<Slot> getRecipeSlots(ProcessingMenu menu, RecipeHolder<ProcessingRecipe> recipe) {
        if (kind == ProcessingKind.INDUCTION) return List.of(menu.slots.get(menu.slots.get(0).hasItem() && !menu.slots.get(3).hasItem() ? 3 : 0));
        return kind == ProcessingKind.CANNING || kind == ProcessingKind.SLICING ? List.of(menu.slots.get(0), menu.slots.get(3)) : List.of(menu.slots.get(0));
    }
    @Override public List<Slot> getInventorySlots(ProcessingMenu menu, RecipeHolder<ProcessingRecipe> recipe) {
        return menu.slots.subList(menu.machineSlots(), menu.machineSlots() + 36);
    }
}
