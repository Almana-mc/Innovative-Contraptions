package me.almana.innovativecontraptions.content.matter;

import me.almana.innovativecontraptions.content.machine.recipe.MaceratingRecipe;
import me.almana.innovativecontraptions.content.processing.*;
import me.almana.innovativecontraptions.content.thermal.SteelmakingRecipe;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
//? if >=26.1 {
import net.minecraft.world.item.crafting.display.SlotDisplay;
//?} else
//import net.minecraft.core.RegistryAccess;
import java.util.*;

final class MatterDerivation {
    static boolean apply(Recipe<?> recipe, Map<Item, Integer> costs, Set<Item> fixed) {
        boolean crafting = recipe instanceof ShapedRecipe || recipe instanceof ShapelessRecipe;
        if (!crafting && !(recipe instanceof AbstractCookingRecipe || recipe instanceof ProcessingRecipe || recipe instanceof MaceratingRecipe || recipe instanceof SteelmakingRecipe)) return false;
        if (recipe.isSpecial()) return false;
        ItemStack result = switch (recipe) {
            case ProcessingRecipe processing -> processing.result().create();
            case MaceratingRecipe grinding -> grinding.result().create();
            case SteelmakingRecipe steel -> steel.result().create();
            default -> ItemStack.EMPTY;
        };
        //? if >=26.1 {
        if (result.isEmpty()) for (var display : recipe.display()) if (display.result() instanceof SlotDisplay.ItemStackSlotDisplay output) { result = output.stack().create(); break; }
        //?} else {
        /*if (result.isEmpty() && (crafting || recipe instanceof AbstractCookingRecipe)) result = recipe.getResultItem(RegistryAccess.EMPTY).copy();
        *///?}
        if (result.isEmpty()) return false;
        long input = inputs(recipe, costs);
        if (input == Long.MAX_VALUE) return false;
        int overhead = crafting ? 10 : 140;
        if (recipe instanceof ProcessingRecipe processing) {
            if (processing.kind() == ProcessingKind.CANNING) overhead = 0;
            if (processing.kind() == ProcessingKind.RECYCLING) overhead = (int) Math.ceil(55.0 * RecyclingRules.YIELD_DENOMINATOR / 4000 * 107 * 10);
        }
        long total = input + overhead;
        boolean changed = offer(result, total, costs, fixed);
        if (recipe instanceof ProcessingRecipe processing) for (var extra : processing.byproducts()) changed |= offer(extra.create(), total, costs, fixed);
        if (recipe instanceof SteelmakingRecipe steel) changed |= offer(steel.slag().create(), total, costs, fixed);
        return changed;
    }
    private static long inputs(Recipe<?> recipe, Map<Item, Integer> costs) {
        if (recipe instanceof ProcessingRecipe processing && processing.kind() == ProcessingKind.RECYCLING) {
            int minimum = costs.entrySet().stream().filter(entry -> RecyclingRules.canProduceScrap(new ItemStack(entry.getKey())))
                    .mapToInt(Map.Entry::getValue).min().orElse(Integer.MAX_VALUE);
            return minimum == Integer.MAX_VALUE ? Long.MAX_VALUE : (long) minimum * RecyclingRules.YIELD_DENOMINATOR;
        }
        //? if >=26.1 {
        List<Ingredient> ingredients = recipe.placementInfo().ingredients();
        //?} else
        //List<Ingredient> ingredients = recipe.getIngredients().stream().filter(ingredient -> !ingredient.isEmpty()).toList();
        if (ingredients.isEmpty()) return Long.MAX_VALUE;
        long total = 0;
        for (Ingredient ingredient : ingredients) {
            int minimum = minimum(ingredient, costs);
            if (minimum == Integer.MAX_VALUE) return Long.MAX_VALUE;
            total += minimum;
        }
        if (recipe instanceof MaceratingRecipe grinding) total *= grinding.inputCount();
        if (recipe instanceof ProcessingRecipe processing) {
            total *= processing.inputCount();
            if (processing.kind() == ProcessingKind.CANNING) {
                int packaging = minimum(processing.packaging(), costs);
                if (packaging == Integer.MAX_VALUE) return Long.MAX_VALUE;
                total += (long) packaging * processing.cansRequired();
            }
        }
        return total;
    }
    private static int minimum(Ingredient ingredient, Map<Item, Integer> costs) {
        //? if >=26.1 {
        return ingredient.items().mapToInt(item -> costs.getOrDefault(item.value(), Integer.MAX_VALUE)).min().orElse(Integer.MAX_VALUE);
        //?} else
        //return Arrays.stream(ingredient.getItems()).mapToInt(stack -> costs.getOrDefault(stack.getItem(), Integer.MAX_VALUE)).min().orElse(Integer.MAX_VALUE);
    }
    private static boolean offer(ItemStack output, long total, Map<Item, Integer> costs, Set<Item> fixed) {
        if (output.isEmpty() || fixed.contains(output.getItem()) || !output.getComponentsPatch().isEmpty()) return false;
        int candidate = (int) Math.min(Integer.MAX_VALUE, Math.max(1L, Math.ceilDiv(total, output.getCount())));
        if (candidate >= costs.getOrDefault(output.getItem(), Integer.MAX_VALUE)) return false;
        costs.put(output.getItem(), candidate); return true;
    }
    private MatterDerivation() {}
}
