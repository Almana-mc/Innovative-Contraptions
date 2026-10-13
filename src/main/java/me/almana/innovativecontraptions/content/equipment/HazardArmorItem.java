package me.almana.innovativecontraptions.content.equipment;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
//? if >=26.1
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;

//? if >=26.1 {
public final class HazardArmorItem extends Item {
//?} else
//public final class HazardArmorItem extends WornItem {
    private final EquipmentSlot slot;
    public HazardArmorItem(Properties props, EquipmentSlot slot) {
        super(GearProperties.worn(props, slot, "hazmat").durability(64).attributes(
                ItemAttributeModifiers.builder().add(
                        Attributes.ARMOR,
                        new AttributeModifier(
                                Identifier.fromNamespaceAndPath("innovativecontraptions", "hazard_" + slot.getName()),
                                1, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.bySlot(slot)).build()));
        this.slot = slot;
    }
    public EquipmentSlot slot() { return slot; }
}
