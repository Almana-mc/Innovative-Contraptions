package me.almana.innovativecontraptions.compat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public record ItemStackTemplate(Holder<Item> item, int count, DataComponentPatch components) {
    private static final Codec<ItemStackTemplate> FULL_CODEC = RecordCodecBuilder.create(i -> i.group(
            ItemStack.ITEM_NON_AIR_CODEC.fieldOf("id").forGetter(ItemStackTemplate::item),
            ExtraCodecs.intRange(1, 99).optionalFieldOf("count", 1).forGetter(ItemStackTemplate::count),
            DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY).forGetter(ItemStackTemplate::components)
    ).apply(i, ItemStackTemplate::new));
    public static final Codec<ItemStackTemplate> CODEC = Codec.withAlternative(FULL_CODEC, ItemStack.ITEM_NON_AIR_CODEC,
            item -> new ItemStackTemplate(item.value()));
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemStackTemplate> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.holderRegistry(Registries.ITEM), ItemStackTemplate::item,
            ByteBufCodecs.VAR_INT, ItemStackTemplate::count,
            DataComponentPatch.STREAM_CODEC, ItemStackTemplate::components,
            ItemStackTemplate::new);

    public ItemStackTemplate {
        if (count == 0 || item.is(Items.AIR.builtInRegistryHolder())) throw new IllegalStateException("Item must be non-empty");
    }

    public ItemStackTemplate(Item item) {
        this(item, 1);
    }

    public ItemStackTemplate(Item item, int count) {
        this(item.builtInRegistryHolder(), count, DataComponentPatch.EMPTY);
    }

    public static ItemStackTemplate fromNonEmptyStack(ItemStack stack) {
        if (stack.isEmpty()) throw new IllegalStateException("Stack must be non-empty");
        return new ItemStackTemplate(stack.getItemHolder(), stack.getCount(), stack.getComponentsPatch());
    }

    public ItemStackTemplate withCount(int count) {
        return this.count == count ? this : new ItemStackTemplate(item, count, components);
    }

    public ItemStack create() {
        return new ItemStack(item, count, components);
    }
}
