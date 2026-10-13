package me.almana.innovativecontraptions.client.agriculture;

import java.util.List;
import java.util.Optional;
import me.almana.innovativecontraptions.content.agriculture.FarmingContent;
import me.almana.innovativecontraptions.content.agriculture.FarmingMenu;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;

public final class FarmingTransfer implements IRecipeTransferInfo<FarmingMenu, FarmingJeiRecipe> {
    private final IRecipeTransferHandlerHelper helper;
    public FarmingTransfer(IRecipeTransferHandlerHelper helper) { this.helper = helper; }
    @Override public Class<FarmingMenu> getContainerClass() { return FarmingMenu.class; }
    @Override public Optional<MenuType<FarmingMenu>> getMenuType() { return Optional.of(FarmingContent.MENU.get()); }
    @Override public IRecipeType<FarmingJeiRecipe> getRecipeType() { return FarmingJeiPlugin.CARE; }
    @Override public boolean canHandle(FarmingMenu menu, FarmingJeiRecipe recipe) { return menu.matron(); }
    @Override public IRecipeTransferError getHandlingError(FarmingMenu menu, FarmingJeiRecipe recipe) { return helper.createUserErrorWithTooltip(Component.translatable("jei.innovativecontraptions.farming.wrong_machine")); }
    @Override public List<Slot> getRecipeSlots(FarmingMenu menu, FarmingJeiRecipe recipe) {
        return recipe.supply() == 0 ? menu.slots.subList(0, 7) : List.of(menu.slots.get(recipe.supply() == 1 ? 7 : 9));
    }
    @Override public List<Slot> getInventorySlots(FarmingMenu menu, FarmingJeiRecipe recipe) { return menu.slots.subList(menu.machineSlots(), menu.machineSlots() + 36); }
}
