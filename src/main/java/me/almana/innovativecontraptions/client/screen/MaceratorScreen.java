package me.almana.innovativecontraptions.client.screen;

import me.almana.innovativecontraptions.InnovativeContraptions;
import me.almana.innovativecontraptions.content.machine.MaceratorMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

public final class MaceratorScreen extends AbstractContainerScreen<MaceratorMenu> {
    private static final int OUTER = 0xFF1C1C1C;
    private static final int PANEL = 0xFF313131;
    private static final int BORDER = 0xFF505050;
    private static final int RECESS = 0xFF1A1A1A;
    private static final int INVENTORY_PANEL = 0xFF3C3C3C;
    private static final int INVENTORY_SLOT = 0xFF2A2A2A;
    private static final int INVENTORY_BORDER = 0xFF646464;
    private static final int ENERGY_EMPTY = 0xFF3A1A1A;
    private static final int PROGRESS_FILL = 0xFFE0902F;
    private static final int LABEL = 0xFFC8C8C8;
    private static final Identifier POWER_BAR_TEXTURE =
            Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, "textures/gui/power_bar.png");

    private static final int ENERGY_X0 = 140;
    private static final int ENERGY_Y0 = 32;
    private static final int ENERGY_X1 = 154;
    private static final int ENERGY_Y1 = 82;
    public static final int RECIPE_X = 76;
    public static final int RECIPE_Y = 55;
    public static final int RECIPE_WIDTH = 17;
    public static final int RECIPE_HEIGHT = 7;

    public MaceratorScreen(MaceratorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 176, 212);
    }

    @Override
    protected void init() {
        super.init();
        titleLabelX = 12;
        titleLabelY = 10;
        inventoryLabelX = 12;
        inventoryLabelY = 116;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        int x = leftPos;
        int y = topPos;
        graphics.fill(x - 1, y - 1, x + imageWidth + 1, y + imageHeight + 1, OUTER);
        graphics.fill(x, y, x + imageWidth, y + imageHeight, PANEL);
        frame(graphics, x + 6, y + 6, x + imageWidth - 6, y + 22);
        frame(graphics, x + 6, y + 26, x + 118, y + 88);
        frame(graphics, x + 124, y + 26, x + imageWidth - 6, y + 88);
        drawInventory(graphics, x, y);
        slotFrame(graphics, x + MaceratorMenu.INPUT_SLOT_X - 1, y + MaceratorMenu.INPUT_SLOT_Y - 1);
        slotFrame(graphics, x + MaceratorMenu.BATTERY_SLOT_X - 1, y + MaceratorMenu.BATTERY_SLOT_Y - 1);
        slotFrame(graphics, x + MaceratorMenu.OUTPUT_SLOT_X - 1, y + MaceratorMenu.OUTPUT_SLOT_Y - 1);
        for (int slot = 0; slot < 4; slot++) slotFrame(graphics, x + 13 + slot * 22, y + 93);

        float energy = Mth.clamp((float) menu.energy() / Math.max(1, menu.energyCapacity()), 0.0F, 1.0F);
        drawEnergyBar(graphics, x + ENERGY_X0, y + ENERGY_Y0, x + ENERGY_X1, y + ENERGY_Y1, energy);
        float progress = Mth.clamp((float) menu.progress() / Math.max(1, menu.progressMax()), 0.0F, 1.0F);
        drawProgressBar(graphics, x + RECIPE_X, y + RECIPE_Y,
                x + RECIPE_X + RECIPE_WIDTH, y + RECIPE_Y + RECIPE_HEIGHT, progress);
        tooltips(graphics, mouseX, mouseY);
    }

    private void tooltips(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        int x = leftPos;
        int y = topPos;
        if (inside(mouseX, mouseY, x + ENERGY_X0, y + ENERGY_Y0, x + ENERGY_X1, y + ENERGY_Y1)) {
            graphics.setTooltipForNextFrame(List.of(Component.literal(
                    menu.energy() + " / " + menu.energyCapacity() + " EU").getVisualOrderText()), mouseX, mouseY);
        }
        if (inside(mouseX, mouseY, x + RECIPE_X, y + RECIPE_Y,
                x + RECIPE_X + RECIPE_WIDTH, y + RECIPE_Y + RECIPE_HEIGHT)) {
            graphics.setTooltipForNextFrame(List.of(Component.literal(
                    menu.progress() + " / " + menu.progressMax() + " ticks").getVisualOrderText()), mouseX, mouseY);
        }
    }

    private boolean inside(int mouseX, int mouseY, int x0, int y0, int x1, int y1) {
        return mouseX >= x0 && mouseX <= x1 && mouseY >= y0 && mouseY <= y1;
    }

    private void drawEnergyBar(GuiGraphicsExtractor graphics, int x0, int y0, int x1, int y1, float ratio) {
        graphics.fill(x0 - 1, y0 - 1, x1 + 1, y1 + 1, BORDER);
        graphics.fill(x0, y0, x1, y1, ENERGY_EMPTY);
        int height = y1 - y0;
        int filled = Math.round(height * ratio);
        if (filled > 0) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, POWER_BAR_TEXTURE, x0, y1 - filled, 0.0F, height - filled,
                    x1 - x0, filled, x1 - x0, height);
        }
    }

    private void drawProgressBar(GuiGraphicsExtractor graphics, int x0, int y0, int x1, int y1, float ratio) {
        graphics.fill(x0 - 1, y0 - 1, x1 + 1, y1 + 1, BORDER);
        graphics.fill(x0, y0, x1, y1, RECESS);
        int filled = Math.round((x1 - x0) * ratio);
        if (filled > 0) {
            graphics.fill(x0, y0, x0 + filled, y1, PROGRESS_FILL);
        }
    }

    private void drawInventory(GuiGraphicsExtractor graphics, int x, int y) {
        frame(graphics, x + 6, y + 126, x + imageWidth - 6, y + 208);
        graphics.fill(x + 7, y + 127, x + imageWidth - 7, y + 207, INVENTORY_PANEL);
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                inventorySlot(graphics, x + 8 + column * 18, y + 130 + row * 18);
            }
        }
        for (int column = 0; column < 9; column++) {
            inventorySlot(graphics, x + 8 + column * 18, y + 188);
        }
    }

    private void inventorySlot(GuiGraphicsExtractor graphics, int x, int y) {
        graphics.fill(x - 1, y - 1, x + 17, y + 17, INVENTORY_BORDER);
        graphics.fill(x, y, x + 16, y + 16, INVENTORY_SLOT);
    }

    private void slotFrame(GuiGraphicsExtractor graphics, int x, int y) {
        graphics.fill(x, y, x + 18, y + 18, BORDER);
        graphics.fill(x + 1, y + 1, x + 17, y + 17, RECESS);
    }

    private void frame(GuiGraphicsExtractor graphics, int x0, int y0, int x1, int y1) {
        graphics.fill(x0, y0, x1, y0 + 1, BORDER);
        graphics.fill(x0, y1 - 1, x1, y1, BORDER);
        graphics.fill(x0, y0, x0 + 1, y1, BORDER);
        graphics.fill(x1 - 1, y0, x1, y1, BORDER);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(font, title, titleLabelX, titleLabelY, 0xFFFFFFFF);
        String statusText = status();
        int statusX = imageWidth - 12 - font.width(statusText);
        graphics.text(font, statusText, statusX, titleLabelY, LABEL);
        graphics.text(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, LABEL);
    }

    private String status() {
        return switch (menu.statusCode()) {
            case 0 -> "Ready";
            case 1 -> "No recipe";
            case 2 -> "No power";
            case 3 -> "Output blocked";
            case 4 -> "Processing";
            default -> "No recipe";
        };
    }
}
