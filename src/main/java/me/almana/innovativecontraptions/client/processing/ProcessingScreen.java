package me.almana.innovativecontraptions.client.processing;

import me.almana.innovativecontraptions.content.processing.ProcessingBlockEntity;
import me.almana.innovativecontraptions.content.processing.ProcessingHeat;
import me.almana.innovativecontraptions.content.processing.ProcessingKind;
import me.almana.innovativecontraptions.content.processing.ProcessingMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import java.util.List;

public final class ProcessingScreen extends AbstractContainerScreen<ProcessingMenu> {
    private static final Identifier POWER_BAR = Identifier.fromNamespaceAndPath("innovativecontraptions", "textures/gui/power_bar.png");
    public static final int RECIPE_X = 76, RECIPE_Y = 45, RECIPE_WIDTH = 17, RECIPE_HEIGHT = 7;
    private Button modeButton;
    public ProcessingScreen(ProcessingMenu menu, Inventory inventory, Component title) { super(menu, inventory, title, 176, 220); }
    @Override protected void init() {
        super.init();
        titleLabelX = 12;
        titleLabelY = 10;
        inventoryLabelX = 12;
        inventoryLabelY = 124;
        if (menu.kind() == ProcessingKind.SMELTING) {
            addRenderableWidget(Button.builder(Component.translatable("gui.innovativecontraptions.processing.collect_xp"), button ->
                    minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 0)).bounds(leftPos + 12, topPos + 83, 122, 18).build());
        }
        if (menu.kind().forming()) {
            modeButton = addRenderableWidget(Button.builder(modeLabel(), button -> {
                minecraft.gameMode.handleInventoryButtonClick(menu.containerId, (menu.value(5) + 1) % 3);
            }).bounds(leftPos + 12, topPos + 83, 122, 18).build());
        }
    }
    private Component modeLabel() { return Component.translatable("gui.innovativecontraptions.processing.mode." + menu.kind().getSerializedName()); }
    @Override protected void containerTick() {
        super.containerTick();
        if (modeButton != null) modeButton.setMessage(modeLabel());
    }
    @Override public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        int x = leftPos, y = topPos;
        graphics.fill(x - 1, y - 1, x + 177, y + 221, 0xFF1C1C1C);
        graphics.fill(x, y, x + 176, y + 220, 0xFF313131);
        panel(graphics, x + 6, y + 6, 164, 18);
        panel(graphics, x + 6, y + 28, 132, 74);
        panel(graphics, x + 142, y + 28, 28, 74);
        graphics.fill(x + 5, y + 134, x + 171, y + 216, 0xFF3C3C3C);
        for (var slot : menu.slots) {
            graphics.fill(x + slot.x - 1, y + slot.y - 1, x + slot.x + 17, y + slot.y + 17, 0xFF646464);
            graphics.fill(x + slot.x, y + slot.y, x + slot.x + 16, y + slot.y + 16, 0xFF1A1A1A);
        }
        bar(graphics, x + 150, y + 35, 12, 60, menu.value(0), menu.value(1), 0xFFE0902F);
        graphics.fill(x + RECIPE_X, y + RECIPE_Y, x + RECIPE_X + RECIPE_WIDTH, y + RECIPE_Y + RECIPE_HEIGHT, 0xFF1A1A1A);
        graphics.fill(x + RECIPE_X, y + RECIPE_Y, x + RECIPE_X + RECIPE_WIDTH * menu.value(2) / Math.max(1, menu.value(3)),
                y + RECIPE_Y + RECIPE_HEIGHT, 0xFFE0902F);
        if (menu.washing()) bar(graphics, x + 36, y + 40, 8, 38, menu.value(6), ProcessingBlockEntity.WATER_CAPACITY, 0xFF4995DA);
        if (menu.kind() == ProcessingKind.INDUCTION)
            graphics.text(font, Component.translatable("gui.innovativecontraptions.processing.heat",
                    menu.value(7) * 100 / ProcessingHeat.INDUCTION_HEAT), x + 12, y + 85, 0xFFFFB866);
        if (menu.kind() == ProcessingKind.CENTRIFUGING) {
            int heat = Math.clamp(menu.value(7), 0, ProcessingHeat.CENTRIFUGE_HEAT);
            graphics.fill(x + 12, y + 86, x + 134, y + 92, 0xFF1A1A1A);
            graphics.fill(x + 12, y + 86, x + 12 + 122 * heat / ProcessingHeat.CENTRIFUGE_HEAT, y + 92, 0xFFE0482F);
            if (mouseX >= x + 12 && mouseX < x + 134 && mouseY >= y + 86 && mouseY < y + 92)
                tooltip(graphics, Component.translatable("gui.innovativecontraptions.processing.heat_value", heat, ProcessingHeat.CENTRIFUGE_HEAT), mouseX, mouseY);
        }
        if (mouseX >= x + 146 && mouseX < x + 166 && mouseY >= y + 30 && mouseY < y + 100)
            tooltip(graphics, Component.translatable("gui.innovativecontraptions.processing.energy", menu.value(0), menu.value(1)), mouseX, mouseY);
        if (menu.washing() && mouseX >= x + 32 && mouseX < x + 48 && mouseY >= y + 36 && mouseY < y + 82)
            tooltip(graphics, Component.translatable("gui.innovativecontraptions.processing.water", menu.value(6), ProcessingBlockEntity.WATER_CAPACITY), mouseX, mouseY);
        if (mouseX >= x + RECIPE_X && mouseX < x + RECIPE_X + RECIPE_WIDTH && mouseY >= y + RECIPE_Y && mouseY < y + RECIPE_Y + RECIPE_HEIGHT)
            tooltip(graphics, Component.translatable(menu.kind() == ProcessingKind.INDUCTION
                    ? "gui.innovativecontraptions.processing.work" : "gui.innovativecontraptions.processing.progress", menu.value(2), menu.value(3)), mouseX, mouseY);
    }
    private void tooltip(GuiGraphicsExtractor graphics, Component text, int x, int y) { graphics.setTooltipForNextFrame(List.of(text.getVisualOrderText()), x, y); }
    private void panel(GuiGraphicsExtractor graphics, int x, int y, int width, int height) {
        graphics.fill(x, y, x + width, y + height, 0xFF505050);
        graphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, 0xFF313131);
    }
    private void bar(GuiGraphicsExtractor graphics, int x, int y, int width, int height, int value, int maximum, int color) {
        graphics.fill(x - 1, y - 1, x + width + 1, y + height + 1, 0xFF505050);
        graphics.fill(x, y, x + width, y + height, 0xFF1A1A1A);
        int filled = (int) ((long) height * Math.clamp(value, 0, maximum) / Math.max(1, maximum));
        if (color == 0xFFE0902F && filled > 0) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, POWER_BAR, x, y + height - filled, 0.0F, height - filled,
                    width, filled, width, height);
        } else graphics.fill(x, y + height - filled, x + width, y + height, color);
    }
    @Override protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(font, title, titleLabelX, titleLabelY, 0xFFFFFFFF);
        graphics.text(font, Component.translatable("gui.innovativecontraptions.processing.status." + menu.value(4)), 12, 29, 0xFFC8C8C8);
        graphics.text(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 0xFFC8C8C8);
    }
}
