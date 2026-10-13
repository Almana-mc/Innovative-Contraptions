package me.almana.innovativecontraptions.client.gathering;

import java.util.List;
import me.almana.innovativecontraptions.client.screen.MachineScreen;
import me.almana.innovativecontraptions.content.gathering.GatheringContent;
import me.almana.innovativecontraptions.content.gathering.GatheringMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public final class GatheringScreen extends MachineScreen<GatheringMenu> {
    private CycleButton<Boolean> pause, list, silk;
    public GatheringScreen(GatheringMenu menu, Inventory inventory, Component title) { super(menu, inventory, title, 176, 232); }
    public static void register(RegisterMenuScreensEvent event) { event.register(GatheringContent.MENU.get(), GatheringScreen::new); }
    @Override protected void init() {
        super.init(); titleLabelX = 8; titleLabelY = 6; inventoryLabelX = 8; inventoryLabelY = 136;
        if (!menu.advanced()) return;
        int flags = menu.value(5);
        pause = cycle(126, 34, 42, 16, value -> Component.translatable("gui.innovativecontraptions.gathering." + (value ? "resume" : "pause")),
                List.of(false, true), (flags & 1024) != 0, (button, value) -> send(0));
        list = cycle(126, 52, 42, 16, value -> Component.translatable("gui.innovativecontraptions.gathering." + (value ? "whitelist" : "blacklist")),
                List.of(false, true), (flags & 256) != 0, (button, value) -> send(1),
                Tooltip.create(Component.translatable("gui.innovativecontraptions.gathering.filter")));
        silk = cycle(126, 70, 42, 16, value -> Component.translatable("gui.innovativecontraptions.gathering." + (value ? "silk_on" : "silk_off")),
                List.of(false, true), (flags & 512) != 0, (button, value) -> send(2));
    }
    @Override protected void containerTick() {
        super.containerTick();
        if (!menu.advanced()) return;
        int flags = menu.value(5);
        sync(pause, (flags & 1024) != 0); sync(list, (flags & 256) != 0); sync(silk, (flags & 512) != 0);
    }
    @Override protected void extractMachine(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        int x = leftPos, y = topPos;
        bar(graphics, 8, 26, 160, 3, menu.value(0), menu.value(1), 0xFFE0AE65);
        for (int index = 0; index < 24; index++) {
            var slot = menu.slots.get(index);
            if (!slot.isActive() || slot.hasItem() || mouseX < x + slot.x || mouseX >= x + slot.x + 16 || mouseY < y + slot.y || mouseY >= y + slot.y + 16) continue;
            String name = index == 0 ? menu.pump() ? "container" : "drill" : index == 1 ? "pipes" : index == 2 ? "scanner" : index == 3 ? "battery" : index < 8 ? "upgrades" : index == 23 ? "output" : menu.advanced() ? "ghost" : "buffer";
            graphics.setTooltipForNextFrame(font.split(Component.translatable("gui.innovativecontraptions.gathering.slot." + name), 210), mouseX, mouseY);
        }
    }
    @Override protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);
        label(graphics, Component.literal(menu.value(0) + " / " + menu.value(1) + " EU"), 8, 16);
        Component position = Component.literal("XYZ " + menu.value(2) + ", " + menu.value(3) + ", " + menu.value(4));
        label(graphics, menu.advanced() ? position : Component.translatable("gui.innovativecontraptions.gathering.status." + (menu.value(5) & 255)), 8, 93);
        if (menu.pump()) label(graphics, Component.literal(menu.value(7) + " / 8000 mB"), 8, 103);
        else if (!menu.advanced()) label(graphics, position, 8, 103);
    }
}
