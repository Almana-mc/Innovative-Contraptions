package me.almana.innovativecontraptions.client.processing;

import me.almana.innovativecontraptions.content.processing.ProcessingFilterMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import java.util.ArrayList;
import java.util.List;

public final class ProcessingFilterScreen extends AbstractContainerScreen<ProcessingFilterMenu> {
    private final List<Button> settings = new ArrayList<>();
    public ProcessingFilterScreen(ProcessingFilterMenu menu, Inventory inventory, Component title) { super(menu, inventory, title, 176, 228); }
    @Override protected void init() {
        super.init(); titleLabelX = 8; titleLabelY = 8; inventoryLabelY = 140; settings.clear();
        for (int i = 0; i < 4; i++) {
            int action = i;
            settings.add(addRenderableWidget(Button.builder(label(i), button -> send(action)).bounds(leftPos + 78, topPos + 30 + i * 18, 90, 16).build()));
        }
        settings.add(addRenderableWidget(Button.builder(label(4), button -> send(4)).bounds(leftPos + 8, topPos + 104, 160, 16).build()));
        addRenderableWidget(Button.builder(Component.literal("−"), button -> send(5)).bounds(leftPos + 8, topPos + 122, 20, 14).build());
        addRenderableWidget(Button.builder(Component.literal("+"), button -> send(6)).bounds(leftPos + 148, topPos + 122, 20, 14).build());
    }
    private void send(int action) { minecraft.gameMode.handleInventoryButtonClick(menu.containerId, action); }
    private Component label(int index) {
        if (index == 0) return Component.translatable("gui.innovativecontraptions.routing." + ((menu.flags() & 1) == 0 ? "whitelist" : "blacklist"));
        if (index == 4) return Component.translatable("gui.innovativecontraptions.routing.charge." + (menu.flags() >> 4 & 3));
        return Component.translatable("gui.innovativecontraptions.routing.option." + index).append((menu.flags() & 1 << index) != 0 ? " ✓" : " −");
    }
    @Override protected void containerTick() { super.containerTick(); for (int i = 0; i < settings.size(); i++) settings.get(i).setMessage(label(i)); }
    @Override public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        int x = leftPos, y = topPos;
        graphics.fill(x - 1, y - 1, x + 177, y + 229, 0xFF181818);
        graphics.fill(x, y, x + 176, y + 228, 0xFF313131);
        for (var slot : menu.slots) {
            graphics.fill(x + slot.x - 1, y + slot.y - 1, x + slot.x + 17, y + slot.y + 17, slot.index < 9 ? 0xFF78A760 : 0xFF777777);
            graphics.fill(x + slot.x, y + slot.y, x + slot.x + 16, y + slot.y + 16, 0xFF181818);
        }
        graphics.text(font, Component.literal(menu.charge() + " EU"), x + 48, y + 125, 0xFFFFB866);
    }
    @Override protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(font, title, titleLabelX, titleLabelY, 0xFFFFFFFF);
        graphics.text(font, Component.translatable("gui.innovativecontraptions.routing.ghosts"), 8, 19, 0xFFBFD3B0);
        graphics.text(font, playerInventoryTitle, 8, inventoryLabelY, 0xFFD0D0D0);
    }
}
