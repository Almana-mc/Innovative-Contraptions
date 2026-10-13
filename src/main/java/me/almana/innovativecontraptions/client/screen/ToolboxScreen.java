package me.almana.innovativecontraptions.client.screen;

import me.almana.innovativecontraptions.content.tool.ToolboxMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class ToolboxScreen extends AbstractContainerScreen<ToolboxMenu> {
    public ToolboxScreen(ToolboxMenu menu, Inventory inventory, Component title) { super(menu, inventory, title, 176, 149); }
    @Override public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        int x = leftPos, y = topPos;
        graphics.fill(x - 1, y - 1, x + 177, y + 150, 0xFF2A2520);
        graphics.fill(x, y, x + 176, y + 149, 0xFF3A332B);
        graphics.fill(x + 48, y + 13, x + 128, y + 55, 0xFF2A241E);
        for (var slot : menu.slots) {
            graphics.fill(x + slot.x - 1, y + slot.y - 1, x + slot.x + 17, y + slot.y + 17, 0xFF8A7458);
            graphics.fill(x + slot.x, y + slot.y, x + slot.x + 16, y + slot.y + 16, 0xFF1E1A15);
        }
    }
    @Override protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(font, title, 8, 4, 0xFFF1E6D6);
        graphics.text(font, playerInventoryTitle, 8, 57, 0xFFD8C8B0);
    }
}
