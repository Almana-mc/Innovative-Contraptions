package me.almana.innovativecontraptions.content.equipment;

import me.almana.innovativecontraptions.InnovativeContraptions;
//? if >=26.1 {
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.item.equipment.Equippable;
//?} else
//import java.util.Optional;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public final class GearProperties {
    public static Item.Properties worn(Item.Properties props, EquipmentSlot slot, String asset) {
        //? if >=26.1 {
        return props.stacksTo(1).component(DataComponents.EQUIPPABLE, Equippable.builder(slot)
                .setAsset(ResourceKey.create(EquipmentAssets.ROOT_ID,
                        Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, asset)))
                .setDamageOnHurt(false).build());
        //?} else {
        /*return props.stacksTo(1).component(EquipmentData.WORN.get(), new Gear(slot,
                Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, asset), 0, Optional.empty(), false));
        *///?}
    }
    public static Item.Properties armor(Item.Properties props, EquipmentSlot slot, String asset,
                                        int durability, int defense, float toughness, int enchantment, Item repair) {
        Identifier id = Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, "armor_" + slot.getName());
        //? if >=26.1 {
        return worn(props, slot, asset).durability(durability).enchantable(enchantment).repairable(repair)
                .component(DataComponents.EQUIPPABLE, Equippable.builder(slot).setAsset(ResourceKey.create(
                        EquipmentAssets.ROOT_ID, Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, asset))).build())
        //?} else {
        /*return worn(props, slot, asset).durability(durability).component(EquipmentData.WORN.get(), new Gear(slot,
                        Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, asset), enchantment, Optional.of(repair), true))
        *///?}
                .attributes(ItemAttributeModifiers.builder().add(Attributes.ARMOR,
                        new AttributeModifier(id, defense, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.bySlot(slot))
                        .add(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(id, toughness,
                                AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.bySlot(slot)).build());
    }
}
