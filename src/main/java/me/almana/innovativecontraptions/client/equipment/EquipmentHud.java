package me.almana.innovativecontraptions.client.equipment;

import me.almana.innovativecontraptions.content.energy.BatteryItem;
import me.almana.innovativecontraptions.content.equipment.ArmorProtection;
import me.almana.innovativecontraptions.content.equipment.EquipmentData;
import me.almana.innovativecontraptions.content.equipment.PoweredArmorItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

public final class EquipmentHud {
    public static void render(RenderGuiEvent.Post event) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.options.hideGui) return;
        var head = client.player.getItemBySlot(EquipmentSlot.HEAD);
        if (!(head.getItem() instanceof PoweredArmorItem) || BatteryItem.stored(head) == 0) return;
        int mode = head.getOrDefault(EquipmentData.MODE.get(), 1);
        if (mode == 0) return;
        int y = client.getWindow().getGuiScaledHeight() / 2;
        //? if >=26.1 {
        GuiGraphicsExtractor graphics = event.getGuiGraphics();
        //?} else
        //GuiGraphicsExtractor graphics = new GuiGraphicsExtractor(event.getGuiGraphics());
        for (EquipmentSlot slot : ArmorProtection.SLOTS) {
            var stack = client.player.getItemBySlot(slot);
            if (!(stack.getItem() instanceof BatteryItem battery)) continue;
            String charge = mode == 1 ? Math.round(100F * BatteryItem.stored(stack) / battery.capacity()) + "%"
                    : BatteryItem.stored(stack) + " / " + battery.capacity() + " EU";
            graphics.text(client.font, Component.translatable("hud.innovativecontraptions.equipment." + slot.getName())
                    .append(": " + charge), 8, y, 0xFFE2F2DE);
            y += 11;
        }
    }
}
