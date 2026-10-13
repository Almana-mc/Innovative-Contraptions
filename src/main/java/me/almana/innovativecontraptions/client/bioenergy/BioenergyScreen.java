package me.almana.innovativecontraptions.client.bioenergy;

import me.almana.innovativecontraptions.client.screen.MachineScreen;
import me.almana.innovativecontraptions.content.bioenergy.BioenergyContent;
import me.almana.innovativecontraptions.content.bioenergy.BioenergyMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public final class BioenergyScreen extends MachineScreen<BioenergyMenu> {
    public BioenergyScreen(BioenergyMenu menu, Inventory inventory, Component title) { super(menu, inventory, title, 176, 224); }
    public static void register(RegisterMenuScreensEvent event) { event.register(BioenergyContent.MENU.get(), BioenergyScreen::new); }
    @Override protected void init() { super.init(); titleLabelX = 8; titleLabelY = 6; inventoryLabelY = 130; }
    @Override protected void extractMachine(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        int x = leftPos, y = topPos;
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
        super.extractLabels(graphics, mouseX, mouseY);
        label(graphics, Component.translatable("gui.innovativecontraptions.bioenergy.status." + menu.value(4)), 8, 30);
        label(graphics, Component.translatable(menu.digester() ? "gui.innovativecontraptions.bioenergy.heat" : "gui.innovativecontraptions.bioenergy.energy", menu.value(0)), 8, 16);
        label(graphics, Component.translatable("gui.innovativecontraptions.bioenergy.tank", menu.value(2)), 8, 118);
        if (menu.digester()) label(graphics, Component.translatable("gui.innovativecontraptions.bioenergy.gas", menu.value(3)), 91, 118);
    }
}
