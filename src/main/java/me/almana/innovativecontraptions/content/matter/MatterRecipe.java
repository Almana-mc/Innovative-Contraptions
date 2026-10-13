package me.almana.innovativecontraptions.content.matter;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.Recipe.CommonInfo;
import net.minecraft.world.item.crafting.SingleItemRecipe;

public final class MatterRecipe extends SingleItemRecipe {
    public static final MapCodec<MatterRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            CommonInfo.MAP_CODEC.forGetter(recipe -> recipe.commonInfo),
            Ingredient.CODEC.fieldOf("ingredient").forGetter(MatterRecipe::input),
            ItemStackTemplate.CODEC.fieldOf("result").forGetter(MatterRecipe::result),
            Codec.intRange(1, 1000000000).fieldOf("matter_units").forGetter(MatterRecipe::cost)
    ).apply(instance, MatterRecipe::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, MatterRecipe> STREAM_CODEC = StreamCodec.of((buffer, recipe) -> {
        CommonInfo.STREAM_CODEC.encode(buffer, recipe.commonInfo);
        Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.input());
        ItemStackTemplate.STREAM_CODEC.encode(buffer, recipe.result());
        buffer.writeVarInt(recipe.cost);
    }, buffer -> new MatterRecipe(CommonInfo.STREAM_CODEC.decode(buffer), Ingredient.CONTENTS_STREAM_CODEC.decode(buffer), ItemStackTemplate.STREAM_CODEC.decode(buffer), buffer.readVarInt()));
    private final int cost;
    public MatterRecipe(CommonInfo info, Ingredient ingredient, ItemStackTemplate result, int cost) { super(info, ingredient, result); this.cost = cost; }
    public int cost() { return cost; }
    @Override public ItemStackTemplate result() { return super.result(); }
    @Override public String group() { return ""; }
    //? if >=26.1
    @Override public RecipeBookCategory recipeBookCategory() { return RecipeBookCategories.CRAFTING_MISC; }
    @Override public RecipeSerializer<MatterRecipe> getSerializer() { return MatterContent.SERIALIZER.get(); }
    @Override public RecipeType<MatterRecipe> getType() { return MatterContent.COSTS.get(); }
}

