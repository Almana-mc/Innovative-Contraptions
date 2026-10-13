package me.almana.innovativecontraptions.client.integration.jei;

import me.almana.innovativecontraptions.content.generator.GeneratorMenu;
import me.almana.innovativecontraptions.registry.ICMenus;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.block.Block;
import java.util.List;
import java.util.Optional;

public record GeneratorTransferInfo(IRecipeType<GeneratorJeiRecipe> recipeType, Block block)
        implements IRecipeTransferInfo<GeneratorMenu, GeneratorJeiRecipe> {
    @Override public Class<? extends GeneratorMenu> getContainerClass() { return GeneratorMenu.class; }
    @Override public Optional<MenuType<GeneratorMenu>> getMenuType() { return Optional.of(ICMenus.GENERATOR.get()); }
    @Override public IRecipeType<GeneratorJeiRecipe> getRecipeType() { return recipeType; }
    @Override public boolean canHandle(GeneratorMenu menu, GeneratorJeiRecipe recipe) {
        return menu.generatorBlock() == block && !recipe.inputs().isEmpty();
    }
    @Override public List<Slot> getRecipeSlots(GeneratorMenu menu, GeneratorJeiRecipe recipe) { return List.of(menu.getSlot(1)); }
    @Override public List<Slot> getInventorySlots(GeneratorMenu menu, GeneratorJeiRecipe recipe) {
        return menu.slots.subList(menu.machineSlots(), menu.machineSlots() + 36);
    }
}
