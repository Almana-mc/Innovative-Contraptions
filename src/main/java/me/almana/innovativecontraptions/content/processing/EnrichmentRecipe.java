package me.almana.innovativecontraptions.content.processing;

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
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidStackTemplate;

public final class EnrichmentRecipe extends SingleItemRecipe {
    public static final MapCodec<EnrichmentRecipe> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            CommonInfo.MAP_CODEC.forGetter(EnrichmentRecipe::info),
            Ingredient.CODEC.fieldOf("ingredient").forGetter(EnrichmentRecipe::input),
            ItemStackTemplate.CODEC.fieldOf("result").forGetter(EnrichmentRecipe::result),
            Codec.intRange(1, 64).fieldOf("input_count").forGetter(EnrichmentRecipe::inputCount),
            FluidStackTemplate.CODEC.fieldOf("fluid_input").forGetter(recipe -> recipe.fluidInput),
            FluidStackTemplate.CODEC.fieldOf("fluid_output").forGetter(recipe -> recipe.fluidOutput),
            Codec.intRange(1, 72000).fieldOf("process_ticks").forGetter(EnrichmentRecipe::ticks),
            Codec.intRange(1, 16000).fieldOf("eu_per_tick").forGetter(EnrichmentRecipe::eu)
    ).apply(i, EnrichmentRecipe::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, EnrichmentRecipe> STREAM = ByteBufCodecs.fromCodecWithRegistries(CODEC.codec());
    private final int count, ticks, eu;
    private final FluidStackTemplate fluidInput, fluidOutput;
    public EnrichmentRecipe(CommonInfo info, Ingredient ingredient, ItemStackTemplate icon, int count,
            FluidStackTemplate fluidInput, FluidStackTemplate fluidOutput, int ticks, int eu) {
        super(info, ingredient, icon);
        this.count = count; this.fluidInput = fluidInput; this.fluidOutput = fluidOutput; this.ticks = ticks; this.eu = eu;
    }
    public CommonInfo info() { return commonInfo; }
    public int inputCount() { return count; }
    public int ticks() { return ticks; }
    public int eu() { return eu; }
    public FluidStack fluidInput() { return fluidInput.create(); }
    public FluidStack fluidOutput() { return fluidOutput.create(); }
    public boolean sameOperation(EnrichmentRecipe previous) {
        return previous != null && count == previous.count && ticks == previous.ticks && eu == previous.eu
                && fluidInput.equals(previous.fluidInput) && fluidOutput.equals(previous.fluidOutput);
    }
    @Override public boolean matches(SingleRecipeInput input, Level level) { return input.item().getCount() >= count && super.matches(input, level); }
    @Override public String group() { return ""; }
    //? if >=26.1
    @Override public RecipeBookCategory recipeBookCategory() { return RecipeBookCategories.CRAFTING_MISC; }
    @Override public RecipeSerializer<EnrichmentRecipe> getSerializer() { return ProcessingContent.ENRICH_SERIALIZER.get(); }
    @Override public RecipeType<EnrichmentRecipe> getType() { return ProcessingContent.ENRICHMENT.get(); }
}
