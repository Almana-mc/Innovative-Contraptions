package me.almana.innovativecontraptions.content.equipment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;

public record Gear(EquipmentSlot slot, ResourceLocation asset, int enchantment, Optional<Item> repair, boolean damageOnHurt) {
    public static final Codec<Gear> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            EquipmentSlot.CODEC.fieldOf("slot").forGetter(Gear::slot),
            ResourceLocation.CODEC.fieldOf("asset").forGetter(Gear::asset),
            Codec.INT.fieldOf("enchantment").forGetter(Gear::enchantment),
            BuiltInRegistries.ITEM.byNameCodec().optionalFieldOf("repair").forGetter(Gear::repair),
            Codec.BOOL.fieldOf("damage_on_hurt").forGetter(Gear::damageOnHurt)
    ).apply(instance, Gear::new));

    public static Gear of(Item item) {
        return item.components().get(EquipmentData.WORN.get());
    }
}
