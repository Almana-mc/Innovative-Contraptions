package me.almana.innovativecontraptions.client.processing;

import me.almana.innovativecontraptions.registry.ICBlocks;
import me.almana.innovativecontraptions.registry.ICItems;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.network.chat.Component;

public final class ProcessingCannerCategory extends AbstractRecipeCategory<ProcessingCannerDisplay> {
    public ProcessingCannerCategory(IGuiHelper gui) { super(ProcessingCannerJei.TYPE, ICBlocks.CANNING_MACHINE.get().getName(), gui.createDrawableItemLike(ICItems.CANNING_MACHINE.get()), 160, 115); }
    @Override public void setRecipe(IRecipeLayoutBuilder builder, ProcessingCannerDisplay recipe, IFocusGroup focuses) {
        if (!recipe.ingredient().isEmpty()) builder.addInputSlot(8, 20).setStandardSlotBackground().addItemStacks(recipe.ingredient());
        if (!recipe.container().isEmpty()) builder.addInputSlot(32, 20).setStandardSlotBackground().addItemStacks(recipe.container());
        if (!recipe.inputFluid().isEmpty()) builder.addInputSlot(56, 20).setStandardSlotBackground().addFluidStack(recipe.inputFluid().getFluid(), recipe.inputFluid().getAmount());
        if (!recipe.result().isEmpty()) builder.addOutputSlot(112, 20).setOutputSlotBackground().add(recipe.result());
        if (!recipe.outputFluid().isEmpty()) builder.addOutputSlot(136, 20).setStandardSlotBackground().addFluidStack(recipe.outputFluid().getFluid(), recipe.outputFluid().getAmount());
    }
    @Override public void createRecipeExtras(IRecipeExtrasBuilder builder, ProcessingCannerDisplay recipe, IFocusGroup focuses) {
        builder.addText(Component.translatable("gui.innovativecontraptions.packager.mode." + recipe.mode()), 160, 10).setPosition(0, 2);
        builder.addAnimatedRecipeArrow(recipe.ticks()).setPosition(82, 20);
        builder.addText(Component.translatable("jei.innovativecontraptions.process_time", recipe.ticks()), 160, 10).setPosition(0, 48);
        builder.addText(Component.translatable("jei.innovativecontraptions.energy_rate", recipe.eu()), 160, 10).setPosition(0, 60);
        builder.addText(Component.translatable("jei.innovativecontraptions.total_energy", recipe.ticks() * recipe.eu()), 160, 10).setPosition(0, 72);
        builder.addText(Component.translatable("jei.innovativecontraptions.packager.tanks"), 160, 30).setPosition(0, 86);
    }
}
