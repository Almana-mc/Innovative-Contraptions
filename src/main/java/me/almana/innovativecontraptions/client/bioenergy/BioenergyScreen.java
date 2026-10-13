package me.almana.innovativecontraptions.client.bioenergy;

import me.almana.innovativecontraptions.content.bioenergy.BioenergyContent;
import me.almana.innovativecontraptions.content.bioenergy.BioenergyMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public final class BioenergyScreen extends AbstractContainerScreen<BioenergyMenu> {
    public BioenergyScreen(BioenergyMenu menu, Inventory inventory, Component title) { super(menu, inventory, title, 176, 224); }
    public static void register(RegisterMenuScreensEvent event) { event.register(BioenergyContent.MENU.get(), BioenergyScreen::new); }
    @Override protected void init() { super.init(); titleLabelX = 8; titleLabelY = 6; inventoryLabelY = 130; }
    @Override public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float tick) {
        int x = leftPos, y = topPos;
        graphics.fill(x - 1, y - 1, x + 177, y + 225, 0xFF17261F);
        graphics.fill(x, y, x + 176, y + 224, 0xFF303C34);
        graphics.fill(x + 6, y + 27, x + 170, y + 126, 0xFF202B24);
        for (var slot : menu.slots) {
            graphics.fill(x + slot.x - 1, y + slot.y - 1, x + slot.x + 17, y + slot.y + 17, 0xFF6E8672);
            graphics.fill(x + slot.x, y + slot.y, x + slot.x + 16, y + slot.y + 16, 0xFF101A13);
        }
        for (int index = 0; index < menu.machineSlots(); index++) {
            var slot = menu.slots.get(index);
            if (slot.hasItem() || mouseX < x + slot.x || mouseX >= x + slot.x + 16
                    || mouseY < y + slot.y || mouseY >= y + slot.y + 16) continue;
            String label = menu.digester() ? switch (index) {
                case 0 -> "fuel_cell"; case 1 -> "returned_cell"; case 2 -> "empty_cell";
                case 3 -> "gas_cell"; case 4 -> "fertilizer"; default -> "upgrade";
            } : switch (index) { case 0 -> "fuel_cell"; case 1 -> "returned_cell"; default -> "battery"; };
            graphics.setTooltipForNextFrame(font.split(Component.translatable("gui.innovativecontraptions.bioenergy.slot." + label), 200), mouseX, mouseY);
        }
    }
    @Override protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(font, title, titleLabelX, titleLabelY, 0xFFF1F5ED);
        graphics.text(font, Component.translatable("gui.innovativecontraptions.bioenergy.status." + menu.value(4)), 8, 30, 0xFFCADCBF);
        graphics.text(font, Component.translatable(menu.digester() ? "gui.innovativecontraptions.bioenergy.heat" : "gui.innovativecontraptions.bioenergy.energy", menu.value(0)), 8, 16, 0xFFF1C274);
        graphics.text(font, Component.translatable("gui.innovativecontraptions.bioenergy.tank", menu.value(2)), 10, 118, 0xFFB8DCC1);
        if (menu.digester()) graphics.text(font, Component.translatable("gui.innovativecontraptions.bioenergy.gas", menu.value(3)), 91, 118, 0xFFB8DCC1);
        graphics.text(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 0xFFE0EADC);
    }
}
