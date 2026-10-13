package me.almana.innovativecontraptions.client.gathering;

import java.util.List;
import java.util.Optional;
import me.almana.innovativecontraptions.content.gathering.GatheringContent;
import me.almana.innovativecontraptions.content.gathering.GatheringMenu;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;

public final class GatheringTransfer implements IRecipeTransferInfo<GatheringMenu, GatheringJeiRecipe> {
    private final IRecipeTransferHandlerHelper helper;
    public GatheringTransfer(IRecipeTransferHandlerHelper helper) { this.helper = helper; }
    @Override public Class<GatheringMenu> getContainerClass() { return GatheringMenu.class; }
    @Override public Optional<MenuType<GatheringMenu>> getMenuType() { return Optional.of(GatheringContent.MENU.get()); }
    @Override public IRecipeType<GatheringJeiRecipe> getRecipeType() { return GatheringJeiPlugin.TYPE; }
    @Override public boolean canHandle(GatheringMenu menu, GatheringJeiRecipe recipe) { return recipe.kind() == (menu.pump() ? 2 : menu.advanced() ? 1 : 0); }
    @Override public IRecipeTransferError getHandlingError(GatheringMenu menu, GatheringJeiRecipe recipe) { return helper.createUserErrorWithTooltip(Component.translatable("jei.innovativecontraptions.gathering.wrong_machine")); }
    @Override public List<Slot> getRecipeSlots(GatheringMenu menu, GatheringJeiRecipe recipe) {
        return recipe.kind() == 0 ? menu.slots.subList(0, 3) : List.of(menu.slots.get(recipe.kind() == 1 ? 2 : 0));
    }
    @Override public List<Slot> getInventorySlots(GatheringMenu menu, GatheringJeiRecipe recipe) { return menu.slots.subList(24, 60); }
}
