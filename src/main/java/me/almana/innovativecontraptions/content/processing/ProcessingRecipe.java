package me.almana.innovativecontraptions.content.processing;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
//? if <26.1
//import java.util.Arrays;
import java.util.Optional;
import me.almana.innovativecontraptions.registry.ICItems;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.Recipe.CommonInfo;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import net.minecraft.world.level.Level;
import java.util.List;

public final class ProcessingRecipe extends SingleItemRecipe {
    public static final MapCodec<ProcessingRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            CommonInfo.MAP_CODEC.forGetter(ProcessingRecipe::info),
            ProcessingKind.CODEC.fieldOf("operation").forGetter(ProcessingRecipe::kind),
            Ingredient.CODEC.fieldOf("ingredient").forGetter(ProcessingRecipe::input),
            ItemStackTemplate.CODEC.fieldOf("result").forGetter(ProcessingRecipe::result),
            Codec.intRange(1, 64).optionalFieldOf("input_count", 1).forGetter(ProcessingRecipe::inputCount),
            ItemStackTemplate.CODEC.listOf(0, 2).optionalFieldOf("byproducts", List.of()).forGetter(ProcessingRecipe::byproducts),
            Codec.intRange(1, 72000).fieldOf("process_ticks").forGetter(ProcessingRecipe::processTicks),
            Codec.intRange(1, 16000).fieldOf("eu_per_tick").forGetter(ProcessingRecipe::euPerTick),
            Codec.intRange(0, 8000).optionalFieldOf("water_mb", 0).forGetter(ProcessingRecipe::waterMb),
            Codec.intRange(0, 5000).optionalFieldOf("heat", 0).forGetter(ProcessingRecipe::heat),
            Codec.intRange(0, 9).optionalFieldOf("hardness", 0).forGetter(ProcessingRecipe::hardness),
            Ingredient.CODEC.optionalFieldOf("container").forGetter(ProcessingRecipe::container)
    ).apply(instance, ProcessingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ProcessingRecipe> STREAM_CODEC = StreamCodec.of(
            ProcessingRecipe::write, ProcessingRecipe::read);
    private final ProcessingKind kind;
    private final int inputCount;
    private final List<ItemStackTemplate> byproducts;
    private final int processTicks;
    private final int euPerTick;
    private final int waterMb;
    private final int heat;
    private final int hardness;
    private final Optional<Ingredient> container;

    public ProcessingRecipe(CommonInfo info, ProcessingKind kind, Ingredient ingredient,
            ItemStackTemplate result, int inputCount, List<ItemStackTemplate> byproducts,
            int processTicks, int euPerTick, int waterMb) {
        this(info, kind, ingredient, result, inputCount, byproducts, processTicks, euPerTick, waterMb, 0, 0, Optional.empty());
    }

    public ProcessingRecipe(CommonInfo info, ProcessingKind kind, Ingredient ingredient,
            ItemStackTemplate result, int inputCount, List<ItemStackTemplate> byproducts,
            int processTicks, int euPerTick, int waterMb, int heat, int hardness, Optional<Ingredient> container) {
        super(info, ingredient, result);
        this.kind = kind;
        this.inputCount = inputCount;
        this.byproducts = List.copyOf(byproducts);
        this.processTicks = processTicks;
        this.euPerTick = euPerTick;
        this.waterMb = waterMb;
        this.heat = heat;
        this.hardness = hardness;
        this.container = container;
    }

    private static void write(RegistryFriendlyByteBuf buffer, ProcessingRecipe recipe) {
        CommonInfo.STREAM_CODEC.encode(buffer, recipe.info());
        buffer.writeEnum(recipe.kind);
        Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.input());
        ItemStackTemplate.STREAM_CODEC.encode(buffer, recipe.result());
        buffer.writeVarInt(recipe.inputCount);
        buffer.writeVarInt(recipe.byproducts.size());
        for (ItemStackTemplate byproduct : recipe.byproducts) ItemStackTemplate.STREAM_CODEC.encode(buffer, byproduct);
        buffer.writeVarInt(recipe.processTicks);
        buffer.writeVarInt(recipe.euPerTick);
        buffer.writeVarInt(recipe.waterMb);
        buffer.writeVarInt(recipe.heat);
        buffer.writeVarInt(recipe.hardness);
        buffer.writeBoolean(recipe.container.isPresent());
        recipe.container.ifPresent(value -> Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, value));
    }

    private static ProcessingRecipe read(RegistryFriendlyByteBuf buffer) {
        var info = CommonInfo.STREAM_CODEC.decode(buffer);
        var kind = buffer.readEnum(ProcessingKind.class);
        var ingredient = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
        var result = ItemStackTemplate.STREAM_CODEC.decode(buffer);
        int count = buffer.readVarInt();
        int byproductCount = buffer.readVarInt();
        var byproducts = new ArrayList<ItemStackTemplate>(byproductCount);
        for (int i = 0; i < byproductCount; i++) byproducts.add(ItemStackTemplate.STREAM_CODEC.decode(buffer));
        int ticks = buffer.readVarInt(), eu = buffer.readVarInt(), water = buffer.readVarInt();
        int heat = buffer.readVarInt(), hardness = buffer.readVarInt();
        var container = buffer.readBoolean() ? Optional.of(Ingredient.CONTENTS_STREAM_CODEC.decode(buffer)) : Optional.<Ingredient>empty();
        return new ProcessingRecipe(info, kind, ingredient, result, count, byproducts, ticks, eu, water, heat, hardness, container);
    }

    private CommonInfo info() { return commonInfo; }
    @Override public ItemStackTemplate result() { return super.result(); }
    public ProcessingKind kind() { return kind; }
    public int inputCount() { return inputCount; }
    public List<ItemStackTemplate> byproducts() { return byproducts; }
    public int processTicks() { return processTicks; }
    public int euPerTick() { return euPerTick; }
    public int waterMb() { return waterMb; }
    public int heat() { return heat; }
    public int hardness() { return hardness; }
    public Optional<Ingredient> container() { return container; }
    public Ingredient packaging() { return container.orElseGet(() -> Ingredient.of(ICItems.TIN_CAN.get())); }
    public int cansRequired() { return kind == ProcessingKind.CANNING ? result().count() : 0; }
    @Override public Ingredient input() { return kind == ProcessingKind.RECYCLING ? RecyclingRules.inputs() : super.input(); }
    public boolean sameOperation(ProcessingRecipe previous) {
        if (previous == this) return true;
        if (previous == null || kind != previous.kind || inputCount != previous.inputCount
                || processTicks != previous.processTicks || euPerTick != previous.euPerTick || waterMb != previous.waterMb
                || heat != previous.heat || hardness != previous.hardness
                //? if >=26.1 {
                || !packaging().items().toList().equals(previous.packaging().items().toList())
                //?} else
                //|| !Arrays.stream(packaging().getItems()).map(ItemStack::getItem).toList().equals(Arrays.stream(previous.packaging().getItems()).map(ItemStack::getItem).toList())
                || byproducts.size() != previous.byproducts.size()
                || !ItemStack.matches(result().create(), previous.result().create())) return false;
        for (int i = 0; i < byproducts.size(); i++) {
            if (!ItemStack.matches(byproducts.get(i).create(), previous.byproducts.get(i).create())) return false;
        }
        return true;
    }
    @Override public boolean matches(SingleRecipeInput input, Level level) {
        return input.item().getCount() >= inputCount && (kind == ProcessingKind.RECYCLING || super.matches(input, level));
    }
    @Override public String group() { return ""; }
    //? if >=26.1
    @Override public RecipeBookCategory recipeBookCategory() { return RecipeBookCategories.CRAFTING_MISC; }
    @Override public RecipeSerializer<ProcessingRecipe> getSerializer() { return ProcessingContent.SERIALIZER.get(); }
    @Override public RecipeType<ProcessingRecipe> getType() { return ProcessingContent.recipeType(kind); }
}
