package me.almana.innovativecontraptions.content.machine.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.almana.innovativecontraptions.registry.ICRecipes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe.CommonInfo;
//? if >=26.1 {
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
//?}
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

public final class MaceratingRecipe extends SingleItemRecipe {
    public static final MapCodec<MaceratingRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            CommonInfo.MAP_CODEC.forGetter(MaceratingRecipe::info),
            Ingredient.CODEC.fieldOf("ingredient").forGetter(MaceratingRecipe::input),
            ItemStackTemplate.CODEC.fieldOf("result").forGetter(MaceratingRecipe::result),
            Codec.intRange(1, 64).optionalFieldOf("input_count", 1).forGetter(MaceratingRecipe::inputCount),
            Codec.intRange(1, 72_000).fieldOf("process_ticks").forGetter(MaceratingRecipe::processTicks),
            Codec.intRange(1, 72_000).fieldOf("eu_per_tick").forGetter(MaceratingRecipe::euPerTick)
    ).apply(instance, MaceratingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, MaceratingRecipe> STREAM_CODEC = StreamCodec.composite(
            CommonInfo.STREAM_CODEC, MaceratingRecipe::info,
            Ingredient.CONTENTS_STREAM_CODEC, MaceratingRecipe::input,
            ItemStackTemplate.STREAM_CODEC, MaceratingRecipe::result,
            ByteBufCodecs.VAR_INT, MaceratingRecipe::inputCount,
            ByteBufCodecs.VAR_INT, MaceratingRecipe::processTicks,
            ByteBufCodecs.VAR_INT, MaceratingRecipe::euPerTick,
            MaceratingRecipe::new
    );

    private final int inputCount;
    private final int processTicks;
    private final int euPerTick;

    public MaceratingRecipe(
            CommonInfo info,
            Ingredient ingredient,
            ItemStackTemplate result,
            int inputCount,
            int processTicks,
            int euPerTick
    ) {
        super(info, ingredient, result);
        this.inputCount = inputCount;
        this.processTicks = processTicks;
        this.euPerTick = euPerTick;
    }

    public int inputCount() {
        return inputCount;
    }

    public int processTicks() {
        return processTicks;
    }

    public int euPerTick() {
        return euPerTick;
    }

    private CommonInfo info() {
        return commonInfo;
    }

    @Override public ItemStackTemplate result() { return super.result(); }

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return input.item().getCount() >= inputCount && super.matches(input, level);
    }

    @Override
    public String group() {
        return "";
    }

    //? if >=26.1 {
    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }
    //?}

    @Override
    public RecipeSerializer<MaceratingRecipe> getSerializer() {
        return ICRecipes.MACERATING_SERIALIZER.get();
    }

    @Override
    public RecipeType<MaceratingRecipe> getType() {
        return ICRecipes.MACERATING_TYPE.get();
    }
}
