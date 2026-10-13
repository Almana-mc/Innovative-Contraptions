package me.almana.innovativecontraptions.client.reactor;

import java.util.Locale;
import me.almana.innovativecontraptions.content.reactor.NuclearReactorBlockEntity;
import me.almana.innovativecontraptions.content.reactor.ReactorContent;
import me.almana.innovativecontraptions.content.reactor.ReactorMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public final class ReactorScreen extends AbstractContainerScreen<ReactorMenu> {
    public static final int BAR_X = 26, BAR_Y = 129, BAR_WIDTH = 162, BAR_HEIGHT = 6, TEXT_Y = 138, TANK_Y = 39, TANK_HEIGHT = 66;

    public ReactorScreen(ReactorMenu menu, Inventory inventory, Component title) { super(menu, inventory, title, 212, 232); }

    public static void register(RegisterMenuScreensEvent event) { event.register(ReactorContent.MENU.get(), ReactorScreen::new); }

    @Override protected void init() {
        super.init();
        titleLabelX = 26;
        titleLabelY = 6;
    }

    @Override public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        int x = leftPos, y = topPos;
        graphics.fill(x - 1, y - 1, x + imageWidth + 1, y + imageHeight + 1, 0xFF1C1C1C);
        graphics.fill(x, y, x + imageWidth, y + imageHeight, 0xFF313131);
        for (int index = 0; index < menu.slots.size(); index++) {
            Slot slot = menu.slots.get(index);
            if (slot.isActive()) panel(graphics, slot.x - 1, slot.y - 1, 18, 18, 0xFF1A1A1A);
            else if (index < NuclearReactorBlockEntity.GRID) graphics.fill(x + slot.x, y + slot.y, x + slot.x + 16, y + slot.y + 16, 0xFF0E0E0E);
        }
        if (menu.fluid()) {
            tank(graphics, 8, menu.coolant(), 0xFF3A7FC1);
            tank(graphics, 190, menu.hotCoolant(), 0xFFC1553A);
        }
        panel(graphics, BAR_X - 1, BAR_Y - 1, BAR_WIDTH + 2, BAR_HEIGHT + 2, 0xFF1A1A1A);
        float ratio = Math.min(1F, (float) menu.heat() / Math.max(1, menu.maxHeat()));
        graphics.fill(x + BAR_X, y + BAR_Y, x + BAR_X + Math.round(BAR_WIDTH * ratio), y + BAR_Y + BAR_HEIGHT,
                0xFF000000 | Mth.hsvToRgb((1F - ratio) / 3F, 0.8F, 0.85F));
        tooltips(graphics, mouseX - x, mouseY - y, mouseX, mouseY);
    }

    private void tank(GuiGraphicsExtractor graphics, int left, int amount, int color) {
        panel(graphics, left, TANK_Y - 1, 16, TANK_HEIGHT + 2, 0xFF1A1A1A);
        int height = TANK_HEIGHT * amount / NuclearReactorBlockEntity.TANK;
        graphics.fill(leftPos + left + 1, topPos + TANK_Y + TANK_HEIGHT - height, leftPos + left + 15, topPos + TANK_Y + TANK_HEIGHT, color);
    }

    private void tooltips(GuiGraphicsExtractor graphics, int x, int y, int mouseX, int mouseY) {
        Component text = null;
        if (x >= BAR_X && x < BAR_X + BAR_WIDTH && y >= BAR_Y && y < BAR_Y + BAR_HEIGHT) {
            text = Component.translatable("gui.innovativecontraptions.reactor.temperature",
                    String.format(Locale.ROOT, "%.2f", 100.0 * menu.heat() / Math.max(1, menu.maxHeat())));
        } else if (x >= BAR_X && x < BAR_X + BAR_WIDTH && y >= TEXT_Y && y < TEXT_Y + 9) {
            text = Component.translatable(menu.fluid() ? "gui.innovativecontraptions.reactor.mode.fluid" : "gui.innovativecontraptions.reactor.mode.electric");
        } else if (menu.fluid() && y >= TANK_Y && y < TANK_Y + TANK_HEIGHT && (x >= 8 && x < 24 || x >= 190 && x < 206)) {
            text = Component.literal((x < 100 ? menu.coolant() : menu.hotCoolant()) + " / " + NuclearReactorBlockEntity.TANK + " mB");
        }
        if (text != null) graphics.setTooltipForNextFrame(font.split(text, 200), mouseX, mouseY);
    }

    private void panel(GuiGraphicsExtractor graphics, int x, int y, int width, int height, int color) {
        graphics.fill(leftPos + x, topPos + y, leftPos + x + width, topPos + y + height, 0xFF505050);
        graphics.fill(leftPos + x + 1, topPos + y + 1, leftPos + x + width - 1, topPos + y + height - 1, color);
    }

    @Override protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(font, title, titleLabelX, titleLabelY, 0xFFFFFFFF);
        Component output = menu.fluid()
                ? Component.translatable("gui.innovativecontraptions.reactor.hu_output", menu.huPerSecond())
                : Component.translatable("gui.innovativecontraptions.reactor.eu_output", menu.euPerTick());
        graphics.text(font, output, BAR_X, TEXT_Y, 0xFFC8C8C8);
    }
}
