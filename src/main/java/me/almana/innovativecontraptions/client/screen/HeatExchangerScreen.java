package me.almana.innovativecontraptions.client.screen;

import me.almana.innovativecontraptions.content.thermal.HeatExchangerMenu;
import me.almana.innovativecontraptions.content.thermal.LiquidHeatExchangerBlockEntity;
import me.almana.innovativecontraptions.content.thermal.ThermalContent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public final class HeatExchangerScreen extends AbstractContainerScreen<HeatExchangerMenu> {
    public static final int RECIPE_X = 44, RECIPE_Y = 86, RECIPE_WIDTH = 88, RECIPE_HEIGHT = 9;
    private static final int HOT_TANK_X = 28, COOLANT_TANK_X = 136, TANK_Y = 20, TANK_WIDTH = 12, TANK_HEIGHT = 60;

    public HeatExchangerScreen(HeatExchangerMenu menu, Inventory inventory, Component title) { super(menu, inventory, title, 176, 200); }

    public static void register(RegisterMenuScreensEvent event) { event.register(ThermalContent.EXCHANGER_MENU.get(), HeatExchangerScreen::new); }

    @Override protected void init() {
        super.init();
        titleLabelX = 8;
        titleLabelY = 6;
        inventoryLabelX = 8;
        inventoryLabelY = 107;
    }

    @Override public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        int x = leftPos, y = topPos;
        graphics.fill(x - 1, y - 1, x + imageWidth + 1, y + imageHeight + 1, 0xFF1C1C1C);
        graphics.fill(x, y, x + imageWidth, y + imageHeight, 0xFF313131);
        for (Slot slot : menu.slots) panel(graphics, slot.x - 1, slot.y - 1, 18, 18, 0xFF1A1A1A);
        tank(graphics, HOT_TANK_X, menu.value(0), 0xFFC1553A);
        tank(graphics, COOLANT_TANK_X, menu.value(1), 0xFF3A7FC1);
        int localX = mouseX - x, localY = mouseY - y;
        if (localY < TANK_Y || localY >= TANK_Y + TANK_HEIGHT) return;
        int amount = localX >= HOT_TANK_X && localX < HOT_TANK_X + TANK_WIDTH ? menu.value(0)
                : localX >= COOLANT_TANK_X && localX < COOLANT_TANK_X + TANK_WIDTH ? menu.value(1) : -1;
        if (amount >= 0) graphics.setTooltipForNextFrame(font.split(
                Component.literal(amount + " / " + LiquidHeatExchangerBlockEntity.TANK + " mB"), 200), mouseX, mouseY);
    }

    private void tank(GuiGraphicsExtractor graphics, int left, int amount, int color) {
        panel(graphics, left - 1, TANK_Y - 1, TANK_WIDTH + 2, TANK_HEIGHT + 2, 0xFF1A1A1A);
        int height = TANK_HEIGHT * amount / LiquidHeatExchangerBlockEntity.TANK;
        graphics.fill(leftPos + left, topPos + TANK_Y + TANK_HEIGHT - height, leftPos + left + TANK_WIDTH, topPos + TANK_Y + TANK_HEIGHT, color);
    }

    private void panel(GuiGraphicsExtractor graphics, int x, int y, int width, int height, int color) {
        graphics.fill(leftPos + x, topPos + y, leftPos + x + width, topPos + y + height, 0xFF505050);
        graphics.fill(leftPos + x + 1, topPos + y + 1, leftPos + x + width - 1, topPos + y + height - 1, color);
    }

    @Override protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(font, title, titleLabelX, titleLabelY, 0xFFFFFFFF);
        graphics.text(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 0xFFC8C8C8);
        graphics.text(font, Component.translatable("gui.innovativecontraptions.exchanger.heat", menu.value(3),
                menu.value(2) * LiquidHeatExchangerBlockEntity.HU_PER_CONDUCTOR), RECIPE_X, RECIPE_Y, 0xFFF1C274);
    }
}
