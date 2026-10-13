package me.almana.innovativecontraptions.client.screen;

import me.almana.innovativecontraptions.InnovativeContraptions;
import me.almana.innovativecontraptions.content.generator.GeneratorMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

public class GeneratorScreen extends AbstractContainerScreen<GeneratorMenu> {
    private static final int OUTER = 0xFF1C1C1C;
    private static final int PANEL = 0xFF313131;
    private static final int BORDER = 0xFF505050;
    private static final int RECESS = 0xFF1A1A1A;
    private static final int INV_PANEL = 0xFF3C3C3C;
    private static final int INV_SLOT_BG = 0xFF2A2A2A;
    private static final int INV_SLOT_BORDER = 0xFF646464;
    private static final int ENERGY_EMPTY = 0xFF3A1A1A;
    private static final int BURN_FILL = 0xFFE0902F;
    private static final int LABEL = 0xFFC8C8C8;
    private static final Identifier POWER_BAR_TEXTURE =
            Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, "textures/gui/power_bar.png");

    public static final int RECIPE_X = 140;
    public static final int RECIPE_Y = 32;
    public static final int RECIPE_WIDTH = 14;
    public static final int RECIPE_HEIGHT = 50;
    private static final int BURN_X0 = 76, BURN_Y0 = 61, BURN_X1 = 82, BURN_Y1 = 79;

    public GeneratorScreen(GeneratorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 176, 188);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = 12;
        this.titleLabelY = 10;
        this.inventoryLabelX = 12;
        this.inventoryLabelY = 92;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        int x = this.leftPos;
        int y = this.topPos;
        int w = this.imageWidth;
        int h = this.imageHeight;

        graphics.fill(x - 1, y - 1, x + w + 1, y + h + 1, OUTER);
        graphics.fill(x, y, x + w, y + h, PANEL);
        frame(graphics, x + 6, y + 6, x + w - 6, y + 22, BORDER);
        frame(graphics, x + 6, y + 26, x + 118, y + 88, BORDER);
        frame(graphics, x + 124, y + 26, x + w - 6, y + 88, BORDER);

        drawInventory(graphics, x, y, w);

        if (this.menu.hasFuel) {
            slotFrame(graphics, x + 53, y + 39);
            slotFrame(graphics, x + 53, y + 61);
        } else {
            slotFrame(graphics, x + 53, y + 48);
        }
        if (this.menu.hasContainerReturn) slotFrame(graphics, x + 97, y + 61);

        int max = Math.max(1, this.menu.energyMax());
        float ratio = Mth.clamp((float) this.menu.energy() / max, 0.0F, 1.0F);
        drawEnergyBar(graphics, x + RECIPE_X, y + RECIPE_Y,
                x + RECIPE_X + RECIPE_WIDTH, y + RECIPE_Y + RECIPE_HEIGHT, ratio);

        if (this.menu.hasFuel) {
            int bmax = Math.max(1, this.menu.burnMax());
            float bratio = this.menu.burn() <= 0 ? 0.0F : Mth.clamp((float) this.menu.burn() / bmax, 0.0F, 1.0F);
            segBar(graphics, x + BURN_X0, y + BURN_Y0, x + BURN_X1, y + BURN_Y1, bratio, BURN_FILL, RECESS, true);
        }

        energyTooltip(graphics, mouseX, mouseY);
    }

    private void energyTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;
        if (mouseX >= x + RECIPE_X && mouseX <= x + RECIPE_X + RECIPE_WIDTH
                && mouseY >= y + RECIPE_Y && mouseY <= y + RECIPE_Y + RECIPE_HEIGHT) {
            graphics.setTooltipForNextFrame(List.of(
                    Component.literal(this.menu.energy() + " / " + this.menu.energyMax() + (this.menu.kinetic ? " KU/t" : " EU")).getVisualOrderText(),
                    Component.literal(formatRate(this.menu.outputPerTick()).replace("EU", this.menu.kinetic ? "KU" : "EU")).getVisualOrderText(),
                    Component.translatable(this.menu.kinetic ? "gui.innovativecontraptions.kinetic.connection" : "gui.innovativecontraptions.generator.output").getVisualOrderText()
            ), mouseX, mouseY);
        }
    }

    private static String formatRate(int rawHundredths) {
        if (rawHundredths % 100 == 0) {
            return (rawHundredths / 100) + " EU/t";
        }
        String value = String.format("%.2f", rawHundredths / 100.0);
        if (value.endsWith("0")) {
            value = value.substring(0, value.length() - 1);
        }
        return value + " EU/t";
    }

    private void drawEnergyBar(GuiGraphicsExtractor g, int x0, int y0, int x1, int y1, float ratio) {
        g.fill(x0 - 1, y0 - 1, x1 + 1, y1 + 1, BORDER);
        g.fill(x0, y0, x1, y1, ENERGY_EMPTY);
        int height = y1 - y0;
        int width = x1 - x0;
        int filled = Math.round(height * ratio);
        if (filled > 0) {
            int top = y1 - filled;
            g.blit(RenderPipelines.GUI_TEXTURED, POWER_BAR_TEXTURE, x0, top, 0.0F, height - filled, width, filled, width, height);
        }
    }

    private void segBar(GuiGraphicsExtractor g, int x0, int y0, int x1, int y1, float ratio, int fill, int empty, boolean segments) {
        g.fill(x0 - 1, y0 - 1, x1 + 1, y1 + 1, BORDER);
        g.fill(x0, y0, x1, y1, empty);
        int height = y1 - y0;
        int filled = Math.round(height * ratio);
        if (filled > 0) {
            g.fill(x0, y1 - filled, x1, y1, fill);
        }
        if (segments) {
            for (int gy = y0 + 2; gy < y1; gy += 3) {
                g.fill(x0, gy, x1, gy + 1, OUTER);
            }
        }
    }

    private void slotFrame(GuiGraphicsExtractor g, int x, int y) {
        g.fill(x, y, x + 18, y + 18, BORDER);
        g.fill(x + 1, y + 1, x + 17, y + 17, RECESS);
    }

    private void frame(GuiGraphicsExtractor g, int x0, int y0, int x1, int y1, int color) {
        g.fill(x0, y0, x1, y0 + 1, color);
        g.fill(x0, y1 - 1, x1, y1, color);
        g.fill(x0, y0, x0 + 1, y1, color);
        g.fill(x1 - 1, y0, x1, y1, color);
    }

    private void drawInventory(GuiGraphicsExtractor g, int x, int y, int w) {
        frame(g, x + 6, y + 102, x + w - 6, y + 184, BORDER);
        g.fill(x + 7, y + 103, x + w - 7, y + 183, INV_PANEL);
        int left = x + 8;
        int top = y + 106;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                invSlot(g, left + col * 18, top + row * 18);
            }
        }
        for (int col = 0; col < 9; col++) {
            invSlot(g, left + col * 18, top + 58);
        }
    }

    private void invSlot(GuiGraphicsExtractor g, int sx, int sy) {
        g.fill(sx - 1, sy - 1, sx + 17, sy + 17, INV_SLOT_BORDER);
        g.fill(sx, sy, sx + 16, sy + 16, INV_SLOT_BG);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(this.font, this.title, this.titleLabelX, this.titleLabelY, 0xFFFFFFFF);
        graphics.text(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, LABEL);
        if (this.menu.kinetic) graphics.text(this.font,
                Component.translatable("gui.innovativecontraptions.kinetic.health", this.menu.burn() / 100), 12, 30, LABEL);
        if (this.menu.kinetic) graphics.text(this.font,
                Component.translatable("gui.innovativecontraptions.kinetic.status." + this.menu.status()), 12, 78, LABEL);
    }
}
