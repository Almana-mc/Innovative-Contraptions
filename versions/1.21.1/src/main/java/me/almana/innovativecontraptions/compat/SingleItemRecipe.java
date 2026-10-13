package me.almana.innovativecontraptions.compat;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

public abstract class SingleItemRecipe implements Recipe<SingleRecipeInput> {
    protected final CommonInfo commonInfo;
    private final Ingredient input;
    private final ItemStackTemplate result;

    public SingleItemRecipe(CommonInfo commonInfo, Ingredient input, ItemStackTemplate result) {
        this.commonInfo = commonInfo;
        this.input = input;
        this.result = result;
    }

    @Override
    public abstract RecipeSerializer<? extends SingleItemRecipe> getSerializer();

    @Override
    public abstract RecipeType<? extends SingleItemRecipe> getType();

    public abstract String group();

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return this.input.test(input.item());
    }

    @Override
    public boolean showNotification() {
        return commonInfo.showNotification();
    }

    public Ingredient input() {
        return input;
    }

    protected ItemStackTemplate result() {
        return result;
    }

    public ItemStack assemble(SingleRecipeInput input) {
        return result.create();
    }

    @Override
    public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) {
        return assemble(input);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return result.create();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY, input);
    }

    @Override
    public String getGroup() {
        return group();
    }
}
