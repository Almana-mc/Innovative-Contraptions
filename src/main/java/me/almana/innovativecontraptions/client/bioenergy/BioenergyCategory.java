package me.almana.innovativecontraptions.client.bioenergy;

import java.util.ArrayList;
import me.almana.innovativecontraptions.client.ICClientRecipes;
import me.almana.innovativecontraptions.content.bioenergy.BioDigester;
import me.almana.innovativecontraptions.content.bioenergy.BioenergyRecipe;
import me.almana.innovativecontraptions.registry.ICBlocks;
import me.almana.innovativecontraptions.registry.ICItems;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

public final class BioenergyCategory extends AbstractRecipeCategory<RecipeHolder<BioenergyRecipe>> {
    public BioenergyCategory(IGuiHelper gui, boolean digester) {
        super(BioenergyJei.type(digester), Component.translatable("jei.innovativecontraptions.bioenergy." + (digester ? "digestion_title" : "fuel_title")),
                gui.createDrawableItemLike(digester ? ICBlocks.BIO_DIGESTER.get() : ICBlocks.LIQUID_FUEL_GENERATOR.get()), 220, 110);
    }
    @Override public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<BioenergyRecipe> holder, IFocusGroup focus) {
        var recipe = holder.value();
        builder.addInputSlot(8, 8).setStandardSlotBackground().addItemStacks(ICClientRecipes.stacks(recipe.input(), 1));
        builder.addOutputSlot(168, 8).setStandardSlotBackground().add(recipe.result().create());
        if (recipe.fermentation()) builder.addOutputSlot(193, 8).setStandardSlotBackground().add(new ItemStack(ICItems.FERTILIZER.get()));
    }
    @Override public void createRecipeExtras(IRecipeExtrasBuilder builder, RecipeHolder<BioenergyRecipe> holder, IFocusGroup focus) {
        var recipe = holder.value();
        builder.addAnimatedRecipeArrow(recipe.fermentation() ? recipe.energy() / BioDigester.MAX_HEAT : 20).setPosition(96, 8);
        var details = new ArrayList<FormattedText>();
        if (recipe.fermentation()) {
            details.add(Component.translatable("jei.innovativecontraptions.bioenergy.fermentation", recipe.inputMb(), recipe.energy(), recipe.outputMb()));
            details.add(Component.translatable("jei.innovativecontraptions.bioenergy.fertilizer", BioDigester.FERTILIZER_INTERVAL));
            details.add(Component.translatable("jei.innovativecontraptions.bioenergy.heat", BioDigester.MAX_HEAT));
        } else {
            details.add(Component.translatable("jei.innovativecontraptions.bioenergy.fuel", recipe.energy(), recipe.outputMb(), recipe.energy() * 1000));
        }
        details.add(Component.translatable("jei.innovativecontraptions.bioenergy.container"));
        builder.addText(details, 212, 76).setPosition(4, 34);
    }
}
