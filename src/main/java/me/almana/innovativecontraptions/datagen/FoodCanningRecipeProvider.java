package me.almana.innovativecontraptions.datagen;

import java.util.List;
import me.almana.innovativecontraptions.InnovativeContraptions;
import me.almana.innovativecontraptions.content.processing.ProcessingKind;
import me.almana.innovativecontraptions.content.processing.ProcessingRecipe;
import me.almana.innovativecontraptions.registry.ICItems;
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

public final class FoodCanningRecipeProvider extends RecipeProvider {
    public FoodCanningRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) { super(registries, output); }
    @Override
    public void buildRecipes() {
        shaped(RecipeCategory.MISC, ICItems.SOLID_CANNING_MACHINE.get()).define('T', ICItems.TIN_CAN.get())
                .define('C', ICItems.ELECTRONIC_CIRCUIT.get()).define('M', ICItems.MACHINE_CASING.get())
                .pattern(" T ").pattern(" T ").pattern("CMC").unlockedBy("has_cans", has(ICItems.TIN_CAN.get())).save(output);
        save("extruding/tin_can", ProcessingKind.EXTRUDING, Ingredient.of(ICItems.TIN_CASING.get()), 1, ICItems.TIN_CAN.get(), 1);
        can("potato", 1, 1, Items.POTATO);
        can("cookie", 1, 2, Items.COOKIE);
        can("melon", 1, 2, Items.MELON_SLICE);
        can("raw_fish", 1, 2, Items.COD, Items.SALMON, Items.PUFFERFISH, Items.TROPICAL_FISH);
        can("raw_chicken", 1, 2, Items.CHICKEN);
        can("raw_pork", 1, 3, Items.PORKCHOP);
        can("raw_beef", 1, 3, Items.BEEF);
        can("apple", 1, 4, Items.APPLE);
        can("carrot", 1, 4, Items.CARROT);
        can("bread", 1, 5, Items.BREAD);
        can("cooked_fish", 1, 5, Items.COOKED_COD, Items.COOKED_SALMON);
        can("cooked_chicken", 1, 6, Items.COOKED_CHICKEN);
        can("baked_potato", 1, 6, Items.BAKED_POTATO);
        can("mushroom_stew", 1, 6, Items.MUSHROOM_STEW);
        can("pumpkin_pie", 1, 6, Items.PUMPKIN_PIE);
        can("cooked_pork", 1, 8, Items.COOKED_PORKCHOP);
        can("steak", 1, 8, Items.COOKED_BEEF);
        can("cake", 1, 12, Items.CAKE);
        can("poisonous_potato", 2, 1, Items.POISONOUS_POTATO);
        can("rotten_flesh", 2, 1, Items.ROTTEN_FLESH);
    }
    private void can(String name, int foodCount, int cans, Item... food) {
        save("canning/" + name, ProcessingKind.CANNING, Ingredient.of(food), foodCount, ICItems.FILLED_TIN_CAN.get(), cans);
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
