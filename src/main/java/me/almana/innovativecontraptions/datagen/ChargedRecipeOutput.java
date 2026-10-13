package me.almana.innovativecontraptions.datagen;

import me.almana.innovativecontraptions.recipe.ChargedCrafting;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.common.conditions.ICondition;

record ChargedRecipeOutput(RecipeOutput destination) implements RecipeOutput {
    //? if >=26.1 {
    @Override public void accept(ResourceKey<Recipe<?>> id, Recipe<?> recipe, AdvancementHolder advancement,
                                 ICondition... conditions) {
    //?} else {
    /*@Override public void accept(net.minecraft.resources.Identifier id, Recipe<?> recipe, AdvancementHolder advancement,
                                 ICondition... conditions) {
    *///?}
        destination.accept(id, ChargedCrafting.wrap(recipe), advancement, conditions);
    }
    @Override public Advancement.Builder advancement() { return destination.advancement(); }
    //? if >=26.1
    @Override public void includeRootAdvancement() { destination.includeRootAdvancement(); }
}
