package me.almana.innovativecontraptions.client.equipment;

import me.almana.innovativecontraptions.content.energy.BatteryItem;
import me.almana.innovativecontraptions.content.equipment.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

public final class EquipmentTooltips {
    public static void describe(ItemTooltipEvent event) {
        var stack = event.getItemStack();
        var tooltip = event.getToolTip();
        if (stack.getItem() instanceof BatteryItem battery && stack.getItem().getClass().getPackageName().endsWith("equipment")) {
            tooltip.add(Component.translatable("tooltip.innovativecontraptions.equipment_charge", battery.tier().ordinal() + 1, battery.transferLimit()));
            if (battery.canSupplyEnergy()) tooltip.add(Component.translatable("tooltip.innovativecontraptions.backpack_supply"));
            else tooltip.add(Component.translatable("tooltip.innovativecontraptions.equipment_controls"));
        }
        if (FlightMotion.isFlightPack(stack)) tooltip.add(Component.translatable("tooltip.innovativecontraptions.flight_controls"));
        if (stack.getItem() instanceof PoweredArmorItem armor && armor.slot() == EquipmentSlot.HEAD) {
            tooltip.add(Component.translatable("tooltip.innovativecontraptions.helmet_hud"));
        }
        if (stack.getItem() instanceof HazardArmorItem) tooltip.add(Component.translatable("tooltip.innovativecontraptions.hazard_protection"));
        if (stack.getItem() instanceof AirCellItem) tooltip.add(Component.translatable("tooltip.innovativecontraptions.air_remaining", stack.getOrDefault(EquipmentData.AIR.get(), 1000)));
        if (stack.getItem() instanceof MiningLaserItem) tooltip.add(Component.translatable("message.innovativecontraptions.equipment.laser_mode_" + stack.getOrDefault(EquipmentData.MODE.get(), 0)));
    }
}
