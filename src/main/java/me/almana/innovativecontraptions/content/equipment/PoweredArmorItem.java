package me.almana.innovativecontraptions.content.equipment;

import me.almana.innovativecontraptions.content.energy.BatteryItem;
import me.almana.innovativecontraptions.content.energy.EuTier;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.component.ItemAttributeModifiers;
//? if <26.1 {
/*import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
*///?}

//? if >=26.1 {
public final class PoweredArmorItem extends BatteryItem {
//?} else
//public final class PoweredArmorItem extends BatteryItem implements Equipable {
    private final boolean advanced;
    private final EquipmentSlot slot;
    public PoweredArmorItem(Properties props, EquipmentSlot slot, boolean advanced) {
        super(GearProperties.worn(props, slot, advanced ? "quantum" : "nano").attributes(
                        ItemAttributeModifiers.builder().add(Attributes.ARMOR, new AttributeModifier(
                                Identifier.fromNamespaceAndPath("innovativecontraptions", "powered_" + slot.getName()),
                                display(slot, advanced), AttributeModifier.Operation.ADD_VALUE),
                                EquipmentSlotGroup.bySlot(slot)).build()),
                advanced ? 10_000_000 : 1_000_000, advanced ? EuTier.EV : EuTier.HV,
                true, advanced ? 12_000 : 1_600, false);
        this.advanced = advanced;
        this.slot = slot;
    }
    public boolean advanced() { return advanced; }
    public EquipmentSlot slot() { return slot; }
    //? if <26.1 {
    /*@Override
    public EquipmentSlot getEquipmentSlot() { return slot; }
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return swapWithEquipmentSlot(this, level, player, hand);
    }
    *///?}
    public int protectionCost() { return advanced ? 20_000 : 5_000; }
    public int displayedArmor() { return display(slot, advanced); }
    public float protectionWeight() { return weight(slot, advanced); }
    private static int display(EquipmentSlot slot, boolean advanced) { return Math.round(20 * weight(slot, advanced)); }
    private static float weight(EquipmentSlot slot, boolean advanced) {
        return switch (slot) {
            case HEAD, FEET -> advanced ? 0.15F : 0.135F;
            case CHEST -> advanced ? 0.48F : 0.36F;
            case LEGS -> advanced ? 0.3F : 0.27F;
            default -> 0;
        };
    }
}
