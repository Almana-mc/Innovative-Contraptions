package me.almana.innovativecontraptions.content.thermal;

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

public final class SteelmakingRecipe extends SingleItemRecipe {
    public static final MapCodec<SteelmakingRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            CommonInfo.MAP_CODEC.forGetter(SteelmakingRecipe::info),
            Ingredient.CODEC.fieldOf("ingredient").forGetter(SteelmakingRecipe::input),
            ItemStackTemplate.CODEC.fieldOf("result").forGetter(SteelmakingRecipe::result),
            ItemStackTemplate.CODEC.fieldOf("slag").forGetter(SteelmakingRecipe::slag),
            Codec.intRange(1, 30_000).fieldOf("process_ticks").forGetter(SteelmakingRecipe::processTicks),
            Codec.intRange(1, 30_000).fieldOf("air_mb").forGetter(SteelmakingRecipe::airMb)
    ).apply(instance, SteelmakingRecipe::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, SteelmakingRecipe> STREAM_CODEC = StreamCodec.composite(
            CommonInfo.STREAM_CODEC, SteelmakingRecipe::info,
            Ingredient.CONTENTS_STREAM_CODEC, SteelmakingRecipe::input,
            ItemStackTemplate.STREAM_CODEC, SteelmakingRecipe::result,
            ItemStackTemplate.STREAM_CODEC, SteelmakingRecipe::slag,
            ByteBufCodecs.VAR_INT, SteelmakingRecipe::processTicks,
            ByteBufCodecs.VAR_INT, SteelmakingRecipe::airMb, SteelmakingRecipe::new);
    private final ItemStackTemplate slag;
    private final int ticks;
    private final int air;

    public SteelmakingRecipe(CommonInfo info, Ingredient ingredient, ItemStackTemplate result,
                             ItemStackTemplate slag, int ticks, int air) {
        super(info, ingredient, result);
        this.slag = slag;
        this.ticks = ticks;
        this.air = air;
    }
    private CommonInfo info() { return commonInfo; }
    @Override public ItemStackTemplate result() { return super.result(); }
    public ItemStackTemplate slag() { return slag; }
    public int processTicks() { return ticks; }
    public int airMb() { return air; }
    @Override public String group() { return ""; }
    //? if >=26.1
    @Override public RecipeBookCategory recipeBookCategory() { return RecipeBookCategories.CRAFTING_MISC; }
    @Override public RecipeSerializer<SteelmakingRecipe> getSerializer() { return ThermalContent.STEEL_SERIALIZER.get(); }
    @Override public RecipeType<SteelmakingRecipe> getType() { return ThermalContent.STEELMAKING.get(); }
}
