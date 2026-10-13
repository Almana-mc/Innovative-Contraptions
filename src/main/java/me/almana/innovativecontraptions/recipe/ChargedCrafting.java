package me.almana.innovativecontraptions.recipe;

import java.util.Optional;
import me.almana.innovativecontraptions.content.energy.BatteryItem;
import me.almana.innovativecontraptions.content.reactor.CondensatorRefill;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
//? if >=26.1 {
import net.minecraft.world.item.crafting.display.SlotDisplay;
//?} else {
/*import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
*///?}
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ChargedCrafting {
    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(
            BuiltInRegistries.RECIPE_SERIALIZER, "innovativecontraptions");
    //? if >=26.1 {
    private static final RecipeSerializer<ShapedRecipe> SHAPED = new RecipeSerializer<>(
            ShapedRecipe.MAP_CODEC.xmap(Shaped::new, recipe -> ((Shaped) recipe).original),
            ShapedRecipe.STREAM_CODEC.map(Shaped::new, recipe -> ((Shaped) recipe).original));
    private static final RecipeSerializer<ShapelessRecipe> SHAPELESS = new RecipeSerializer<>(
            ShapelessRecipe.MAP_CODEC.xmap(Shapeless::new, recipe -> ((Shapeless) recipe).original),
            ShapelessRecipe.STREAM_CODEC.map(Shapeless::new, recipe -> ((Shapeless) recipe).original));
    //?} else {
    /*private static final RecipeSerializer<ShapedRecipe> SHAPED = new RecipeSerializer<>(
            RecipeSerializer.SHAPED_RECIPE.codec().xmap(Shaped::new, recipe -> ((Shaped) recipe).original),
            RecipeSerializer.SHAPED_RECIPE.streamCodec().map(Shaped::new, recipe -> ((Shaped) recipe).original));
    private static final RecipeSerializer<ShapelessRecipe> SHAPELESS = new RecipeSerializer<>(
            RecipeSerializer.SHAPELESS_RECIPE.codec().xmap(Shapeless::new, recipe -> ((Shapeless) recipe).original),
            RecipeSerializer.SHAPELESS_RECIPE.streamCodec().map(Shapeless::new, recipe -> ((Shapeless) recipe).original));
    *///?}

    public static void register(IEventBus bus) {
        SERIALIZERS.register("charged_shaped", () -> SHAPED);
        SERIALIZERS.register("charged_shapeless", () -> SHAPELESS);
        SERIALIZERS.register(bus);
    }

    public static Recipe<?> wrap(Recipe<?> recipe) {
        if (!(recipe instanceof ShapedRecipe || recipe instanceof ShapelessRecipe)) return recipe;
        Item result = template(recipe).item().value();
        if (!(result instanceof BatteryItem) && !CondensatorRefill.refills(result)) return recipe;
        return recipe instanceof ShapedRecipe shaped ? new Shaped(shaped) : new Shapeless((ShapelessRecipe) recipe);
    }

    private static ItemStackTemplate template(Recipe<?> recipe) {
        //? if >=26.1 {
        return ((SlotDisplay.ItemStackSlotDisplay)
                recipe.display().getFirst().result()).stack();
        //?} else
        //return ItemStackTemplate.fromNonEmptyStack(recipe.getResultItem(RegistryAccess.EMPTY));
    }

    private static ItemStack transfer(CraftingInput input, ItemStack result) {
        if (result.getCount() != 1) return result;
        if (CondensatorRefill.refills(result.getItem())) return CondensatorRefill.apply(input, result);
        long energy = 0;
        for (int slot = 0; slot < input.size(); slot++) {
            ItemStack ingredient = input.getItem(slot);
            if (ingredient.getCount() > 0 && ingredient.getItem() instanceof BatteryItem) energy += BatteryItem.stored(ingredient);
        }
        if (energy > 0) BatteryItem.setStored(result, (int) Math.min(Integer.MAX_VALUE, energy));
        return result;
    }

    private static final class Shaped extends ShapedRecipe {
        private final ShapedRecipe original;
        Shaped(ShapedRecipe original) {
            //? if >=26.1 {
            super(new CommonInfo(original.showNotification()), new CraftingBookInfo(original.category(), original.group()),
                    new ShapedRecipePattern(original.getWidth(), original.getHeight(), original.getIngredients(), Optional.empty()),
                    template(original));
            //?} else {
            /*super(original.getGroup(), original.category(),
                    new ShapedRecipePattern(original.getWidth(), original.getHeight(), original.getIngredients(), Optional.empty()),
                    template(original).create(), original.showNotification());
            *///?}
            this.original = original;
        }
        //? if >=26.1 {
        @Override public ItemStack assemble(CraftingInput input) { return transfer(input, original.assemble(input)); }
        //?} else
        //@Override public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) { return transfer(input, original.assemble(input, registries)); }
        @Override public RecipeSerializer<ShapedRecipe> getSerializer() { return SHAPED; }
    }

    private static final class Shapeless extends ShapelessRecipe {
        private final ShapelessRecipe original;
        Shapeless(ShapelessRecipe original) {
            //? if >=26.1 {
            super(new CommonInfo(original.showNotification()), new CraftingBookInfo(original.category(), original.group()),
                    template(original), original.placementInfo().ingredients());
            //?} else
            //super(original.getGroup(), original.category(), template(original).create(), original.getIngredients());
            this.original = original;
        }
        //? if >=26.1 {
        @Override public ItemStack assemble(CraftingInput input) { return transfer(input, original.assemble(input)); }
        //?} else
        //@Override public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) { return transfer(input, original.assemble(input, registries)); }
        @Override public RecipeSerializer<ShapelessRecipe> getSerializer() { return SHAPELESS; }
    }
}
