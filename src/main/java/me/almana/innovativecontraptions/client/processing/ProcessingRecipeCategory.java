package me.almana.innovativecontraptions.client.processing;

import java.util.List;
import me.almana.innovativecontraptions.client.ICClientRecipes;
import me.almana.innovativecontraptions.content.processing.ProcessingContent;
import me.almana.innovativecontraptions.content.processing.ProcessingHeat;
import me.almana.innovativecontraptions.content.processing.ProcessingKind;
import me.almana.innovativecontraptions.content.processing.ProcessingRecipe;
import me.almana.innovativecontraptions.content.processing.RecyclingRules;
import me.almana.innovativecontraptions.registry.ICItems;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeHolderType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.material.Fluids;

public final class ProcessingRecipeCategory extends AbstractRecipeCategory<RecipeHolder<ProcessingRecipe>> {
    public ProcessingRecipeCategory(IGuiHelper gui, ProcessingKind kind, IRecipeHolderType<ProcessingRecipe> type) {
        super(type, title(kind), gui.createDrawableItemLike(ProcessingContent.block(kind)), 158, 106);
    }
    private static Component title(ProcessingKind kind) {
        Component title = ProcessingContent.block(kind).getName();
        return kind.forming() ? title.copy().append(" - ").append(Component.translatable("gui.innovativecontraptions.processing.mode." + kind.getSerializedName())) : title;
    }
    @Override public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<ProcessingRecipe> holder, IFocusGroup focuses) {
        ProcessingRecipe recipe = holder.value();
        builder.addInputSlot(8, 10).setStandardSlotBackground().addItemStacks(ICClientRecipes.stacks(recipe.input(), recipe.inputCount()));
        builder.addOutputSlot(88, 10).setOutputSlotBackground().add(recipe.result().create());
        if (recipe.cansRequired() > 0) builder.addInputSlot(30, 10).setStandardSlotBackground()
                .addItemStacks(ICClientRecipes.stacks(recipe.packaging(), recipe.cansRequired()));
        if (recipe.hardness() > 0) builder.addInputSlot(30, 10).setStandardSlotBackground().addItemStacks(List.of(
                ICItems.IRON_SAW_DISC.get(), ICItems.STEEL_SAW_DISC.get(),
                ICItems.DIAMOND_SAW_DISC.get()).stream().map(ItemStack::new)
                .filter(stack -> ProcessingHeat.bladeStrength(stack) >= recipe.hardness()).toList());
        for (int i = 0; i < recipe.byproducts().size(); i++) builder.addOutputSlot(110 + i * 22, 10)
                .setStandardSlotBackground().add(recipe.byproducts().get(i).create());
        if (recipe.waterMb() > 0) builder.addInputSlot(30, 10).setStandardSlotBackground()
                .addFluidStack(Fluids.WATER, recipe.waterMb());
    }
    @Override public void createRecipeExtras(IRecipeExtrasBuilder builder, RecipeHolder<ProcessingRecipe> holder, IFocusGroup focuses) {
        ProcessingRecipe recipe = holder.value();
        builder.addAnimatedRecipeArrow(recipe.processTicks()).setPosition(54, 11);
        builder.addText(Component.translatable("jei.innovativecontraptions.process_time", recipe.processTicks()), 158, 10).setPosition(0, 40);
        builder.addText(Component.translatable("jei.innovativecontraptions.energy_rate", recipe.euPerTick()), 158, 10).setPosition(0, 52);
        builder.addText(Component.translatable("jei.innovativecontraptions.total_energy", recipe.processTicks() * recipe.euPerTick()), 158, 10).setPosition(0, 64);
        if (recipe.waterMb() > 0) builder.addText(Component.translatable("jei.innovativecontraptions.processing.water", recipe.waterMb()), 158, 10).setPosition(0, 76);
        if (recipe.heat() > 0) builder.addText(Component.translatable("jei.innovativecontraptions.processing.heat", recipe.heat()), 158, 22).setPosition(0, 76);
        if (recipe.hardness() > 0) builder.addText(Component.translatable("jei.innovativecontraptions.processing.blade", recipe.hardness()), 158, 22).setPosition(0, 76);
        if (recipe.kind() == ProcessingKind.INDUCTION) builder.addText(Component.translatable("jei.innovativecontraptions.processing.induction"), 158, 30).setPosition(0, 76);
        if (recipe.kind() == ProcessingKind.RECYCLING) builder.addText(Component.translatable(
                "jei.innovativecontraptions.recycling_chance", RecyclingRules.YIELD_DENOMINATOR),
                158, 22).setPosition(0, 76);
        if (recipe.kind().forming()) builder.addText(Component.translatable("jei.innovativecontraptions.processing.mode",
                Component.translatable("gui.innovativecontraptions.processing.mode." + recipe.kind().getSerializedName())), 158, 10).setPosition(0, 76);
    }
}
