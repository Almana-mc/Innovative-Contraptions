package me.almana.innovativecontraptions.client.screen;

import me.almana.innovativecontraptions.content.thermal.BlastFurnaceBlockEntity;
import me.almana.innovativecontraptions.content.thermal.ThermalContent;
import me.almana.innovativecontraptions.content.thermal.ThermalMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public final class ThermalScreen extends AbstractContainerScreen<ThermalMenu> {
    private static final Identifier POWER_BAR = Identifier.fromNamespaceAndPath("innovativecontraptions", "textures/gui/power_bar.png");
    public static final int RECIPE_X = 44;
    public static final int RECIPE_Y = 44;
    public static final int RECIPE_WIDTH = 40;
    public static final int RECIPE_HEIGHT = 7;

    public ThermalScreen(ThermalMenu menu, Inventory inventory, Component title) { super(menu, inventory, title, 176, 208); }
    public static void registerScreens(RegisterMenuScreensEvent event) { event.register(ThermalContent.MENU.get(), ThermalScreen::new); }

    @Override protected void init() {
        super.init();
        titleLabelX = 12;
        titleLabelY = 10;
        inventoryLabelX = 12;
        inventoryLabelY = 112;
    }

    @Override public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(leftPos - 1, topPos - 1, leftPos + imageWidth + 1, topPos + imageHeight + 1, 0xFF1C1C1C);
        graphics.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xFF313131);
        panel(graphics, 6, 6, 164, 16, 0xFF313131);
        panel(graphics, 6, 26, 164, 80, 0xFF313131);
        panel(graphics, 6, 122, 164, 82, 0xFF3C3C3C);
        for (int slot = 0; slot < menu.machineSlots(); slot++) {
            panel(graphics, ThermalMenu.slotX(menu.heater(), slot) - 1, ThermalMenu.slotY(menu.heater(), slot) - 1, 18, 18, 0xFF1A1A1A);
        }
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) panel(graphics, 7 + col * 18, 125 + row * 18, 18, 18, 0xFF2A2A2A);
        }
        for (int col = 0; col < 9; col++) panel(graphics, 7 + col * 18, 183, 18, 18, 0xFF2A2A2A);
        drawMeters(graphics);
        tooltips(graphics, mouseX, mouseY);
    }

    private void drawMeters(GuiGraphicsExtractor graphics) {
        panel(graphics, 152, 33, 10, 54, 0xFF3A1A1A);
        int maximum = menu.heater() ? menu.value(1) : 100;
        int height = Math.min(52, (int) (52.0 * menu.value(0) / Math.max(1, maximum)));
        if (menu.heater() && height > 0) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, POWER_BAR, leftPos + 153, topPos + 86 - height, 0.0F, 52 - height,
                    8, height, 8, 52);
        } else graphics.fill(leftPos + 153, topPos + 86 - height, leftPos + 161, topPos + 86, 0xFFE0902F);
        if (menu.heater()) return;
        panel(graphics, RECIPE_X - 1, RECIPE_Y - 1, RECIPE_WIDTH + 2, RECIPE_HEIGHT + 2, 0xFF1A1A1A);
        int progress = RECIPE_WIDTH * menu.value(2) / Math.max(1, menu.value(3));
        graphics.fill(leftPos + RECIPE_X, topPos + RECIPE_Y, leftPos + RECIPE_X + progress, topPos + RECIPE_Y + RECIPE_HEIGHT, 0xFFE0902F);
        panel(graphics, 43, 72, 42, 9, 0xFF1A1A1A);
        int air = 40 * menu.value(1) / BlastFurnaceBlockEntity.AIR_CAPACITY;
        graphics.fill(leftPos + 44, topPos + 73, leftPos + 44 + air, topPos + 80, 0xFF82AFC1);
    }

    private void tooltips(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        int x = mouseX - leftPos;
        int y = mouseY - topPos;
        Component text = null;
        if (x >= 152 && x < 162 && y >= 33 && y < 87) {
            text = menu.heater() ? Component.literal(menu.value(0) + " / " + menu.value(1) + " EU")
                    : Component.translatable("screen.innovativecontraptions.heat", menu.value(0));
        } else if (!menu.heater() && x >= 43 && x < 85 && y >= 72 && y < 81) {
            text = Component.literal(menu.value(1) + " / " + BlastFurnaceBlockEntity.AIR_CAPACITY + " mB");
        } else if (x >= 150 && x < 164 && y >= 90 && y < 106) text = Component.translatable("screen.innovativecontraptions.heat_ports");
        if (text != null) graphics.setTooltipForNextFrame(font.split(text, 230), mouseX, mouseY);
    }

    private void panel(GuiGraphicsExtractor graphics, int x, int y, int width, int height, int color) {
        graphics.fill(leftPos + x, topPos + y, leftPos + x + width, topPos + y + height, 0xFF505050);
        graphics.fill(leftPos + x + 1, topPos + y + 1, leftPos + x + width - 1, topPos + y + height - 1, color);
    }

    @Override protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(font, title, titleLabelX, titleLabelY, 0xFFFFFFFF);
        graphics.text(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 0xFFC8C8C8);
        Component status = menu.heater()
                ? Component.translatable("screen.innovativecontraptions.heater_rate", menu.value(2), menu.value(4))
                : Component.translatable("screen.innovativecontraptions.thermal_status." + menu.value(4));
        graphics.text(font, status, 12, 94, 0xFFC8C8C8);
        graphics.text(font, Component.literal("?"), 154, 94, 0xFFC8C8C8);
    }
}
