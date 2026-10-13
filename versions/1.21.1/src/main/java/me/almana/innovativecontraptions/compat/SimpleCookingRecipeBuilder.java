package me.almana.innovativecontraptions.compat;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.ItemLike;

public final class SimpleCookingRecipeBuilder {
    public static net.minecraft.data.recipes.SimpleCookingRecipeBuilder smelting(
            Ingredient input, RecipeCategory category, CookingBookCategory book, ItemLike result, float experience, int time) {
        // keep explicit book category
        return net.minecraft.data.recipes.SimpleCookingRecipeBuilder.generic(input, category, result, experience, time,
                net.minecraft.world.item.crafting.RecipeSerializer.SMELTING_RECIPE,
                (group, ignored, ingredient, stack, xp, ticks) -> new SmeltingRecipe(group, book, ingredient, stack, xp, ticks));
    }

    private SimpleCookingRecipeBuilder() {
    }
}
