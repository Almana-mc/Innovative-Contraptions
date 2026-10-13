package me.almana.innovativecontraptions.client.integration.jei;

import me.almana.innovativecontraptions.client.ICClientRecipes;
import me.almana.innovativecontraptions.content.thermal.BlastFurnaceBlockEntity;
import me.almana.innovativecontraptions.content.thermal.SteelmakingRecipe;
import me.almana.innovativecontraptions.registry.ICBlocks;
import me.almana.innovativecontraptions.registry.ICItems;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

public final class SteelmakingCategory extends AbstractRecipeCategory<RecipeHolder<SteelmakingRecipe>> {
    public SteelmakingCategory(IGuiHelper gui) {
        super(ThermalJei.STEEL.get(), Component.translatable("jei.innovativecontraptions.category.steelmaking"),
                gui.createDrawableItemLike(ICBlocks.BLAST_FURNACE.get()), 170, 118);
    }
    @Override public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<SteelmakingRecipe> holder, IFocusGroup focuses) {
        SteelmakingRecipe recipe = holder.value();
        builder.addInputSlot(4, 5).setStandardSlotBackground().addItemStacks(ICClientRecipes.stacks(recipe.input(), 1));
        int cells = Math.ceilDiv(recipe.airMb(), BlastFurnaceBlockEntity.CELL_AIR);
        builder.addInputSlot(4, 27).setStandardSlotBackground().add(new ItemStack(ICItems.AIR_CELL.get(), cells));
        builder.addOutputSlot(112, 5).setOutputSlotBackground().add(recipe.result().create());
        builder.addOutputSlot(140, 5).setOutputSlotBackground().add(recipe.slag().create());
        builder.addOutputSlot(112, 27).setStandardSlotBackground().add(new ItemStack(ICItems.EMPTY_CELL.get(), cells));
    }
    @Override public void createRecipeExtras(IRecipeExtrasBuilder builder, RecipeHolder<SteelmakingRecipe> holder, IFocusGroup focuses) {
        SteelmakingRecipe recipe = holder.value();
        builder.addAnimatedRecipeArrow(recipe.processTicks()).setPosition(62, 6);
        builder.addText(Component.translatable("jei.innovativecontraptions.process_time", recipe.processTicks()), 170, 10).setPosition(0, 53);
        builder.addText(Component.translatable("jei.innovativecontraptions.steel_air", recipe.airMb()), 170, 20).setPosition(0, 65);
        builder.addText(Component.translatable("jei.innovativecontraptions.steel_heat", BlastFurnaceBlockEntity.HEAT_CAPACITY), 170, 24).setPosition(0, 91);
    }
}
