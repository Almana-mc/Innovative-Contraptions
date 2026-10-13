package me.almana.innovativecontraptions.client.integration.jei;

import me.almana.innovativecontraptions.client.ICClientRecipes;
import me.almana.innovativecontraptions.content.machine.recipe.MaceratingRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeHolderType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.ItemLike;

public final class MaceratorRecipeCategory extends AbstractRecipeCategory<RecipeHolder<MaceratingRecipe>> {
    private static final int WIDTH = 144;
    private static final int HEIGHT = 83;

    public MaceratorRecipeCategory(
            IGuiHelper guiHelper,
            IRecipeHolderType<MaceratingRecipe> recipeType,
            Component title,
            ItemLike icon
    ) {
        super(recipeType, title, guiHelper.createDrawableItemLike(icon), WIDTH, HEIGHT);
    }

    @Override
    public void setRecipe(
            IRecipeLayoutBuilder builder,
            RecipeHolder<MaceratingRecipe> holder,
            IFocusGroup focuses
    ) {
        MaceratingRecipe recipe = holder.value();
        builder.addInputSlot(10, 10)
                .setStandardSlotBackground()
                .addItemStacks(ICClientRecipes.stacks(recipe.input(), recipe.inputCount()));
        builder.addOutputSlot(116, 10)
                .setOutputSlotBackground()
                .add(recipe.assemble(new SingleRecipeInput(ItemStack.EMPTY)));
    }

    @Override
    public void createRecipeExtras(
            IRecipeExtrasBuilder builder,
            RecipeHolder<MaceratingRecipe> holder,
        IFocusGroup focuses
    ) {
        MaceratingRecipe recipe = holder.value();
        builder.addAnimatedRecipeArrow(recipe.processTicks()).setPosition(53, 11);
        builder.addText(Component.translatable("jei.innovativecontraptions.process_time", recipe.processTicks()),
                WIDTH, 10).setPosition(0, 39);
        builder.addText(Component.translatable("jei.innovativecontraptions.energy_rate", recipe.euPerTick()),
                WIDTH, 10).setPosition(0, 50);
        builder.addText(Component.translatable("jei.innovativecontraptions.total_energy",
                recipe.processTicks() * recipe.euPerTick()), WIDTH, 10).setPosition(0, 61);
    }
}
