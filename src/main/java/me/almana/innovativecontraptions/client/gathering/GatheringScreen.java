package me.almana.innovativecontraptions.client.gathering;

import me.almana.innovativecontraptions.content.gathering.GatheringContent;
import me.almana.innovativecontraptions.content.gathering.GatheringMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public final class GatheringScreen extends AbstractContainerScreen<GatheringMenu> {
    public GatheringScreen(GatheringMenu menu, Inventory inventory, Component title) { super(menu, inventory, title, 176, 232); }
    public static void register(RegisterMenuScreensEvent event) { event.register(GatheringContent.MENU.get(), GatheringScreen::new); }
    @Override protected void init() {
        super.init(); titleLabelX = 8; titleLabelY = 6; inventoryLabelX = 8; inventoryLabelY = 136;
        if (menu.advanced()) {
            button(0, 34, "pause");
            button(1, 52, "whitelist").setTooltip(Tooltip.create(Component.translatable("gui.innovativecontraptions.gathering.filter")));
            button(2, 70, "silk_off");
        }
    }
    private Button button(int id, int y, String name) {
        return addRenderableWidget(Button.builder(Component.translatable("gui.innovativecontraptions.gathering." + name), button -> minecraft.gameMode.handleInventoryButtonClick(menu.containerId, id))
                .bounds(leftPos + 126, topPos + y, 42, 16).build());
    }
    @Override public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        int x = leftPos, y = topPos;
        graphics.fill(x - 1, y - 1, x + 177, y + 233, 0xFF141E26);
        graphics.fill(x, y, x + 176, y + 232, 0xFF2A3640);
        graphics.fill(x + 5, y + 145, x + 171, y + 225, 0xFF394650);
        for (var slot : menu.slots) {
            if (!slot.isActive()) continue;
            graphics.fill(x + slot.x - 1, y + slot.y - 1, x + slot.x + 17, y + slot.y + 17, 0xFF617889);
            graphics.fill(x + slot.x, y + slot.y, x + slot.x + 16, y + slot.y + 16, 0xFF15222C);
        }
        graphics.fill(x + 8, y + 26, x + 168, y + 29, 0xFF111A23);
        graphics.fill(x + 8, y + 26, x + 8 + (int) (160L * menu.value(0) / Math.max(1, menu.value(1))), y + 29, 0xFFE0AE65);
        for (int index = 0; index < 24; index++) {
            var slot = menu.slots.get(index);
            if (!slot.isActive() || slot.hasItem() || mouseX < x + slot.x || mouseX >= x + slot.x + 16 || mouseY < y + slot.y || mouseY >= y + slot.y + 16) continue;
            String name = index == 0 ? menu.pump() ? "container" : "drill" : index == 1 ? "pipes" : index == 2 ? "scanner" : index == 3 ? "battery" : index < 8 ? "upgrades" : index == 23 ? "output" : menu.advanced() ? "ghost" : "buffer";
            graphics.setTooltipForNextFrame(font.split(Component.translatable("gui.innovativecontraptions.gathering.slot." + name), 210), mouseX, mouseY);
        }
    }
    @Override protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(font, title, titleLabelX, titleLabelY, 0xFFFFFFFF);
        graphics.text(font, Component.literal(menu.value(0) + " / " + menu.value(1) + " EU"), 8, 16, 0xFFE0AE65);
        int flags = menu.value(5);
        Component position = Component.literal("XYZ " + menu.value(2) + ", " + menu.value(3) + ", " + menu.value(4));
        graphics.text(font, menu.advanced() ? position : Component.translatable("gui.innovativecontraptions.gathering.status." + (flags & 255)), 8, 93, 0xFFBAD7E2);
        if (menu.pump()) graphics.text(font, Component.literal(menu.value(7) + " / 8000 mB"), 8, 103, 0xFFBAD7E2);
        else if (!menu.advanced()) graphics.text(font, position, 8, 103, 0xFFBAD7E2);
        graphics.text(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 0xFFE3ECF0);
        if (menu.advanced()) {
            var buttons = children().stream().filter(Button.class::isInstance).map(Button.class::cast).toList();
            if (buttons.size() == 3) {
                buttons.get(0).setMessage(Component.translatable("gui.innovativecontraptions.gathering." + ((flags & 1024) == 0 ? "pause" : "resume")));
                buttons.get(1).setMessage(Component.translatable("gui.innovativecontraptions.gathering." + ((flags & 256) == 0 ? "blacklist" : "whitelist")));
                buttons.get(2).setMessage(Component.translatable("gui.innovativecontraptions.gathering." + ((flags & 512) == 0 ? "silk_off" : "silk_on")));
            }
        }
    }
}
