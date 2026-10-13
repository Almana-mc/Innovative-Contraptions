package me.almana.innovativecontraptions.compat;

import java.util.concurrent.CompletableFuture;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

public abstract class RecipeProvider {
    protected final HolderLookup.Provider registries;
    protected final RecipeOutput output;

    protected RecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        this.registries = registries;
        this.output = output;
    }

    protected abstract void buildRecipes();

    protected Criterion<InventoryChangeTrigger.TriggerInstance> has(ItemLike item) {
        return InventoryChangeTrigger.TriggerInstance.hasItems(item);
    }

    protected Criterion<InventoryChangeTrigger.TriggerInstance> has(TagKey<Item> tag) {
        return InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(tag));
    }

    protected ShapedRecipeBuilder shaped(RecipeCategory category, ItemLike item) {
        return ShapedRecipeBuilder.shaped(category, item);
    }

    protected ShapedRecipeBuilder shaped(RecipeCategory category, ItemLike item, int count) {
        return ShapedRecipeBuilder.shaped(category, item, count);
    }

    protected ShapelessRecipeBuilder shapeless(RecipeCategory category, ItemLike item) {
        return ShapelessRecipeBuilder.shapeless(category, item);
    }

    protected ShapelessRecipeBuilder shapeless(RecipeCategory category, ItemLike item, int count) {
        return ShapelessRecipeBuilder.shapeless(category, item, count);
    }

    public abstract static class Runner implements DataProvider {
        private final net.minecraft.data.recipes.RecipeProvider vanilla;

        protected Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
            vanilla = new net.minecraft.data.recipes.RecipeProvider(packOutput, registries) {
                @Override
                protected void buildRecipes(RecipeOutput output, HolderLookup.Provider lookup) {
                    createRecipeProvider(lookup, output).buildRecipes();
                }
            };
        }

        @Override
        public final CompletableFuture<?> run(CachedOutput cache) {
            return vanilla.run(cache);
        }

        protected abstract RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output);
    }
}
