package me.almana.innovativecontraptions.datagen;

import java.util.List;
import me.almana.innovativecontraptions.InnovativeContraptions;
import me.almana.innovativecontraptions.content.processing.ProcessingKind;
import me.almana.innovativecontraptions.content.processing.ProcessingRecipe;
import me.almana.innovativecontraptions.registry.ICItems;
import me.almana.innovativecontraptions.registry.ICTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe.CommonInfo;
import net.neoforged.neoforge.common.Tags;

public final class FoodCanningRecipeProvider extends RecipeProvider {
    public FoodCanningRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) { super(registries, output); }
    @Override
    public void buildRecipes() {
        shaped(RecipeCategory.MISC, ICItems.SOLID_CANNING_MACHINE.get()).define('T', ICItems.TIN_CAN.get())
                .define('C', ICTags.CIRCUITS_BASIC).define('M', ICItems.MACHINE_CASING.get())
                .pattern(" T ").pattern(" T ").pattern("CMC").unlockedBy("has_cans", has(ICItems.TIN_CAN.get())).save(output);
        save("extruding/tin_can", ProcessingKind.EXTRUDING, tag(ICTags.CASINGS_TIN), 1, ICItems.TIN_CAN.get(), 1);
        can("potato", 1, 1, tag(Tags.Items.CROPS_POTATO));
        can("cookie", 1, 2, tag(Tags.Items.FOODS_COOKIE));
        can("melon", 1, 2, Ingredient.of(Items.MELON_SLICE));
        can("raw_fish", 1, 2, tag(Tags.Items.FOODS_RAW_FISH));
        can("raw_chicken", 1, 2, Ingredient.of(Items.CHICKEN));
        can("raw_pork", 1, 3, Ingredient.of(Items.PORKCHOP));
        can("raw_beef", 1, 3, Ingredient.of(Items.BEEF));
        can("apple", 1, 4, Ingredient.of(Items.APPLE));
        can("carrot", 1, 4, tag(Tags.Items.CROPS_CARROT));
        can("bread", 1, 5, tag(Tags.Items.FOODS_BREAD));
        can("cooked_fish", 1, 5, tag(Tags.Items.FOODS_COOKED_FISH));
        can("cooked_chicken", 1, 6, Ingredient.of(Items.COOKED_CHICKEN));
        can("baked_potato", 1, 6, Ingredient.of(Items.BAKED_POTATO));
        can("mushroom_stew", 1, 6, Ingredient.of(Items.MUSHROOM_STEW));
        can("pumpkin_pie", 1, 6, tag(Tags.Items.FOODS_PIE));
        can("cooked_pork", 1, 8, Ingredient.of(Items.COOKED_PORKCHOP));
        can("steak", 1, 8, Ingredient.of(Items.COOKED_BEEF));
        can("cake", 1, 12, Ingredient.of(Items.CAKE));
        can("poisonous_potato", 2, 1, Ingredient.of(Items.POISONOUS_POTATO));
        can("rotten_flesh", 2, 1, Ingredient.of(Items.ROTTEN_FLESH));
    }
    private void can(String name, int foodCount, int cans, Ingredient food) {
        save("canning/" + name, ProcessingKind.CANNING, food, foodCount, ICItems.FILLED_TIN_CAN.get(), cans);
    }
    private void save(String name, ProcessingKind kind, Ingredient input, int count, Item result, int amount) {
        //? if >=26.1 {
        var id = ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, name));
        //?} else
        //var id = Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, name);
        output.accept(id, new ProcessingRecipe(new CommonInfo(true), kind, input, new ItemStackTemplate(result, amount),
                count, List.of(), kind.ticks, kind.euPerTick, 0), null);
    }
}
