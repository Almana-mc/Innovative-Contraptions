package me.almana.innovativecontraptions.client.agriculture;

import me.almana.innovativecontraptions.content.agriculture.FarmingContent;
import me.almana.innovativecontraptions.content.agriculture.FarmingMachineBlockEntity;
import me.almana.innovativecontraptions.content.agriculture.FarmingMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public final class FarmingScreen extends AbstractContainerScreen<FarmingMenu> {
    public static final int RECIPE_X = 108, RECIPE_Y = 99, RECIPE_WIDTH = 56, RECIPE_HEIGHT = 20;
    public FarmingScreen(FarmingMenu menu, Inventory inventory, Component title) { super(menu, inventory, title, 176, 232); }
    public static void register(RegisterMenuScreensEvent event) {
        event.register(FarmingContent.MENU.get(), FarmingScreen::new);
        event.register(FarmingContent.ANALYZER.get(), AnalyzerScreen::new);
    }
    @Override protected void init() { super.init(); titleLabelX = 10; titleLabelY = 8; inventoryLabelX = 8; inventoryLabelY = 138; }
    @Override public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        int x = leftPos, y = topPos;
        graphics.fill(x - 1, y - 1, x + 177, y + 233, 0xFF182421);
        graphics.fill(x, y, x + 176, y + 232, 0xFF303633);
        graphics.fill(x + 6, y + 28, x + 170, y + 121, 0xFF252B28);
        graphics.fill(x + 5, y + 147, x + 171, y + 227, 0xFF3E4540);
        for (var slot : menu.slots) {
            graphics.fill(x + slot.x - 1, y + slot.y - 1, x + slot.x + 17, y + slot.y + 17, 0xFF637566);
            graphics.fill(x + slot.x, y + slot.y, x + slot.x + 16, y + slot.y + 16, 0xFF171E19);
        }
        graphics.fill(x + 9, y + 22, x + 167, y + 25, 0xFF131C16);
        graphics.fill(x + 9, y + 22, x + 9 + 158 * Math.min(menu.value(0), menu.value(1)) / Math.max(1, menu.value(1)), y + 25, 0xFFE3AB55);
        graphics.fill(x + RECIPE_X, y + RECIPE_Y, x + RECIPE_X + RECIPE_WIDTH, y + RECIPE_Y + RECIPE_HEIGHT, 0xFF46604D);
        graphics.fill(x + RECIPE_X + 2, y + RECIPE_Y + 16, x + RECIPE_X + 2 + 52 * menu.value(2) / FarmingMachineBlockEntity.SCAN_VOLUME, y + RECIPE_Y + 18, 0xFFA4C69B);
    }
    @Override protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(font, title, titleLabelX, titleLabelY, 0xFFEFF3ED);
        graphics.text(font, Component.translatable("gui.innovativecontraptions.farming.status." + menu.value(5)), 10, 29, 0xFFBBD4B6);
        graphics.text(font, Component.translatable("gui.innovativecontraptions.farming.energy", menu.value(0), menu.value(1)), 10, 126, 0xFFE3AB55);
        if (menu.matron()) graphics.text(font, Component.translatable("gui.innovativecontraptions.farming.fluids", menu.value(3), menu.value(4)), 10, 90, 0xFFCAD9CA);
        graphics.text(font, Component.translatable("gui.innovativecontraptions.farming.scan", menu.value(2), FarmingMachineBlockEntity.SCAN_VOLUME), RECIPE_X + 2, RECIPE_Y + 3, 0xFFFFFFFF);
        graphics.text(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY + 1, 0xFFD8E0D6);
    }
}
