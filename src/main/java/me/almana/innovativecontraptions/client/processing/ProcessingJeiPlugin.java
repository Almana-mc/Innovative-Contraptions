package me.almana.innovativecontraptions.client.processing;

import me.almana.innovativecontraptions.InnovativeContraptions;
import me.almana.innovativecontraptions.client.ICClientRecipes;
import me.almana.innovativecontraptions.client.integration.jei.SilentClickArea;
import me.almana.innovativecontraptions.content.processing.*;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.recipe.types.IRecipeHolderType;
import mezz.jei.api.registration.*;
import net.minecraft.resources.Identifier;
import java.util.Collection;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Recipe.CommonInfo;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;

@JeiPlugin
public final class ProcessingJeiPlugin implements IModPlugin {
    @Override public Identifier getPluginUid() { return Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, "processing_jei"); }
    static IRecipeHolderType<ProcessingRecipe> type(ProcessingKind kind) { return IRecipeHolderType.createDeferred(() -> ProcessingContent.recipeType(kind)).get(); }
    @Override public void registerCategories(IRecipeCategoryRegistration registration) {
        for (ProcessingKind kind : ProcessingKind.values()) registration.addRecipeCategories(
                new ProcessingRecipeCategory(registration.getJeiHelpers().getGuiHelper(), kind, type(kind)));
    }
    @Override public void registerRecipes(IRecipeRegistration registration) {
        for (ProcessingKind kind : ProcessingKind.values()) {
            if (kind == ProcessingKind.INDUCTION || kind == ProcessingKind.SMELTING) registration.addRecipes(type(kind), ICClientRecipes.byType(RecipeType.SMELTING).stream()
                    //? if >=26.1 {
                    .filter(holder -> holder.value().input().items().findAny().isPresent())
                    //?} else
                    //.filter(holder -> !holder.value().getIngredients().getFirst().hasNoItems())
                    .map(holder -> new RecipeHolder<>(holder.id(), new ProcessingRecipe(
                            new CommonInfo(true), kind,
                            //? if >=26.1 {
                            holder.value().input(), ItemStackTemplate.fromNonEmptyStack(holder.value().assemble(new SingleRecipeInput(
                                    new ItemStack(holder.value().input().items().findFirst().orElseThrow().value())))),
                            //?} else {
                            /*holder.value().getIngredients().getFirst(), ItemStackTemplate.fromNonEmptyStack(holder.value().assemble(new SingleRecipeInput(
                                    ICClientRecipes.stacks(holder.value().getIngredients().getFirst(), 1).getFirst()), net.minecraft.client.Minecraft.getInstance().level.registryAccess())),
                            *///?}
                            1, List.of(), kind == ProcessingKind.INDUCTION ? ProcessingHeat.hotInductionTicks() : kind.ticks, kind.euPerTick, 0))).toList());
            else registration.addRecipes(type(kind), ICClientRecipes.byType(ProcessingContent.recipeType(kind)));
        }
    }
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        for (ProcessingKind kind : ProcessingKind.values()) registration.addCraftingStation(type(kind), ProcessingContent.block(kind));
    }
    @Override public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        for (ProcessingKind kind : ProcessingKind.values()) registration.addRecipeTransferHandler(
                new ProcessingItemTransfer(kind, registration.getTransferHelper()), type(kind));
    }
    @Override public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGuiContainerHandler(ProcessingScreen.class, new IGuiContainerHandler<>() {
            @Override public Collection<IGuiClickableArea> getGuiClickableAreas(ProcessingScreen screen, double mouseX, double mouseY) {
                return List.of(new SilentClickArea(IGuiClickableArea.createBasic(ProcessingScreen.RECIPE_X, ProcessingScreen.RECIPE_Y,
                        ProcessingScreen.RECIPE_WIDTH, ProcessingScreen.RECIPE_HEIGHT, type(screen.getMenu().kind()))));
            }
        });
    }
}
