package me.almana.innovativecontraptions.client;

import me.almana.innovativecontraptions.content.matter.MatterCostSync;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
//? if >=26.1 {
import net.minecraft.world.item.crafting.RecipeMap;
import net.neoforged.neoforge.client.event.RecipesReceivedEvent;
//?} else {
/*import me.almana.innovativecontraptions.InnovativeContraptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.client.event.RegisterRecipeBookCategoriesEvent;
*///?}

import java.util.Comparator;
import java.util.List;

public final class ICClientRecipes {
    //? if >=26.1 {
    private static RecipeMap recipes = RecipeMap.EMPTY;

    public static void receive(RecipesReceivedEvent event) {
        recipes = event.getRecipeMap();
    }
    //?} else {
    /*// same fallback, minus warning
    public static void categorise(RegisterRecipeBookCategoriesEvent event) {
        for (RecipeType<?> type : BuiltInRegistries.RECIPE_TYPE) {
            if (BuiltInRegistries.RECIPE_TYPE.getKey(type).getNamespace().equals(InnovativeContraptions.MODID)) {
                event.registerRecipeCategoryFinder(type, recipe -> RecipeBookCategories.UNKNOWN);
            }
        }
    }
    *///?}

    public static void clear(ClientPlayerNetworkEvent.LoggingOut event) {
        //? if >=26.1
        recipes = RecipeMap.EMPTY;
        MatterCostSync.clearClient();
    }

    public static <I extends RecipeInput, T extends Recipe<I>> List<RecipeHolder<T>> byType(RecipeType<T> type) {
        //? if >=26.1 {
        var all = recipes.byType(type);
        //?} else
        //var all = Minecraft.getInstance().level.recipeAccess().getAllRecipesFor(type);
        return all.stream().sorted(Comparator.comparing(RecipeHolder::id)).toList();
    }

    public static List<ItemStack> stacks(Ingredient ingredient, int count) {
        //? if >=26.1 {
        return ingredient.items().map(item -> new ItemStack(item.value(), count)).toList();
        //?} else
        //return ingredient.hasNoItems() ? List.of() : java.util.Arrays.stream(ingredient.getItems()).map(stack -> new ItemStack(stack.getItem(), count)).toList();
    }

    private ICClientRecipes() {
    }
}
