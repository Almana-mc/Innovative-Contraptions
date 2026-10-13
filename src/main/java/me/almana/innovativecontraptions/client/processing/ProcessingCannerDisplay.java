package me.almana.innovativecontraptions.client.processing;

import java.util.List;
import me.almana.innovativecontraptions.client.ICClientRecipes;
import me.almana.innovativecontraptions.content.processing.*;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

public record ProcessingCannerDisplay(int mode, List<ItemStack> ingredient, List<ItemStack> container,
        ItemStack result, FluidStack inputFluid, FluidStack outputFluid, int ticks, int eu) {
    public static ProcessingCannerDisplay solid(ProcessingRecipe recipe) {
        return new ProcessingCannerDisplay(0, ICClientRecipes.stacks(recipe.input(), recipe.inputCount()),
                ICClientRecipes.stacks(recipe.packaging(), recipe.cansRequired()), recipe.result().create(),
                FluidStack.EMPTY, FluidStack.EMPTY, recipe.processTicks(), ProcessingCannerBlockEntity.BASE_EU);
    }
    public static ProcessingCannerDisplay enrich(EnrichmentRecipe recipe) {
        return new ProcessingCannerDisplay(3, ICClientRecipes.stacks(recipe.input(), recipe.inputCount()),
                List.of(), ItemStack.EMPTY, recipe.fluidInput(), recipe.fluidOutput(), recipe.ticks(), recipe.eu());
    }
}
