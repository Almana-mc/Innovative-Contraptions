package me.almana.innovativecontraptions.content.bioenergy;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.Recipe.CommonInfo;
import net.minecraft.world.item.crafting.SingleItemRecipe;

public final class BioenergyRecipe extends SingleItemRecipe {
    public static final MapCodec<BioenergyRecipe> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            CommonInfo.MAP_CODEC.forGetter(BioenergyRecipe::info),
            Ingredient.CODEC.fieldOf("ingredient").forGetter(BioenergyRecipe::input),
            ItemStackTemplate.CODEC.fieldOf("result").forGetter(BioenergyRecipe::result),
            Codec.BOOL.fieldOf("fermentation").forGetter(BioenergyRecipe::fermentation),
            Codec.intRange(1, 10000).fieldOf("input_mb").forGetter(BioenergyRecipe::inputMb),
            Codec.intRange(1, 10000).fieldOf("output_mb").forGetter(BioenergyRecipe::outputMb),
            Codec.intRange(1, 1000000).fieldOf("energy").forGetter(BioenergyRecipe::energy)
    ).apply(i, BioenergyRecipe::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, BioenergyRecipe> STREAM = ByteBufCodecs.fromCodecWithRegistries(CODEC.codec());
    private final boolean fermentation;
    private final int inputMb, outputMb, energy;
    public BioenergyRecipe(CommonInfo info, Ingredient input, ItemStackTemplate result, boolean fermentation, int inputMb, int outputMb, int energy) {
        super(info, input, result);
        this.fermentation = fermentation;
        this.inputMb = inputMb;
        this.outputMb = outputMb;
        this.energy = energy;
    }
    public CommonInfo info() { return commonInfo; }
    @Override public ItemStackTemplate result() { return super.result(); }
    public boolean fermentation() { return fermentation; }
    public int inputMb() { return inputMb; }
    public int outputMb() { return outputMb; }
    public int energy() { return energy; }
    @Override public String group() { return ""; }
    //? if >=26.1
    @Override public RecipeBookCategory recipeBookCategory() { return RecipeBookCategories.CRAFTING_MISC; }
    @Override public RecipeSerializer<BioenergyRecipe> getSerializer() { return BioenergyContent.SERIALIZER.get(); }
    @Override public RecipeType<BioenergyRecipe> getType() { return BioenergyContent.RECIPES.get(); }
}
