package me.almana.innovativecontraptions.client.integration.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.ItemLike;

import java.util.List;

public final class GeneratorRecipeCategory extends AbstractRecipeCategory<GeneratorJeiRecipe> {
    private static final int WIDTH = 168;
    private static final int HEIGHT = 150;

    public GeneratorRecipeCategory(
            IGuiHelper guiHelper,
            IRecipeType<GeneratorJeiRecipe> recipeType,
            Component title,
            ItemLike icon
    ) {
        super(recipeType, title, guiHelper.createDrawableItemLike(icon), WIDTH, HEIGHT);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, GeneratorJeiRecipe recipe, IFocusGroup focuses) {
        if (!recipe.inputs().isEmpty()) {
            builder.addInputSlot(10, 10)
                    .setStandardSlotBackground()
                    .addItemStacks(recipe.inputs());
        }
        if (!recipe.conditions().isEmpty()) {
            builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 44, 10)
                    .setStandardSlotBackground()
                    .addItemStacks(recipe.conditions());
        }
        if (!recipe.outputs().isEmpty()) {
            builder.addOutputSlot(140, 10)
                    .setOutputSlotBackground()
                    .addItemStacks(recipe.outputs());
        }
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, GeneratorJeiRecipe recipe, IFocusGroup focuses) {
        List<FormattedText> details = recipe.details().stream()
                .map(component -> (FormattedText) component)
                .toList();
        builder.addText(details, WIDTH - 8, HEIGHT - 40).setPosition(4, 38);
    }

    @Override
    //? if >=26.1 {
    public Identifier getIdentifier(GeneratorJeiRecipe recipe) {
    //?} else
    //public Identifier getRegistryName(GeneratorJeiRecipe recipe) {
        return recipe.id();
    }
}
