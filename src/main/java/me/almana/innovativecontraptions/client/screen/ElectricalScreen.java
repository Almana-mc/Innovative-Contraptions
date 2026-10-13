package me.almana.innovativecontraptions.client.screen;

import me.almana.innovativecontraptions.content.energy.storage.ElectricalMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class ElectricalScreen extends AbstractContainerScreen<ElectricalMenu> {
    private Button modeButton;
    public ElectricalScreen(ElectricalMenu menu, Inventory inventory, Component title) { super(menu, inventory, title, 176, 224); }
    @Override protected void init() {
        super.init();
        titleLabelX = 10;
        titleLabelY = 10;
        inventoryLabelX = 8;
        inventoryLabelY = 130;
        modeButton = addRenderableWidget(Button.builder(modeText(), button ->
                minecraft.gameMode.handleInventoryButtonClick(menu.containerId, (menu.mode() + 1) % menu.modeCount()))
                .bounds(leftPos + 8, topPos + 106, 160, 20).build());
    }
    private Component modeText() {
        String kind = menu.kind().transformer ? "transformer" : menu.kind().pad ? "pad" : "storage";
        return Component.translatable("screen.innovativecontraptions." + kind + ".mode." + menu.mode());
    }
    @Override protected void containerTick() { super.containerTick(); modeButton.setMessage(modeText()); }
    @Override public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(leftPos - 1, topPos - 1, leftPos + 177, topPos + 225, 0xFF161616);
        graphics.fill(leftPos, topPos, leftPos + 176, topPos + 224, 0xFF313131);
        panel(graphics, 6, 6, 164, 18);
        panel(graphics, 6, 28, 164, 76);
        panel(graphics, 6, 140, 164, 80);
        for (var slot : menu.slots) {
            graphics.fill(leftPos + slot.x - 1, topPos + slot.y - 1, leftPos + slot.x + 17, topPos + slot.y + 17, 0xFF686868);
            graphics.fill(leftPos + slot.x, topPos + slot.y, leftPos + slot.x + 16, topPos + slot.y + 16, 0xFF191919);
        }
        graphics.fill(leftPos + 12, topPos + 91, leftPos + 144, topPos + 98, 0xFF1B1111);
        int width = Math.min(130, (int) (130L * menu.energy() / menu.kind().capacity));
        graphics.fill(leftPos + 13, topPos + 92, leftPos + 13 + width, topPos + 97, 0xFFD24A4A);
        if (mouseX >= leftPos + 6 && mouseX < leftPos + 146 && mouseY >= topPos + 28 && mouseY < topPos + 104) {
            String key = menu.kind().transformer ? "transformer.guide" : menu.kind().pad ? "pad.guide" : "storage.guide";
            graphics.setTooltipForNextFrame(font.split(Component.translatable("screen.innovativecontraptions." + key), 260), mouseX, mouseY);
        }
    }
    private void panel(GuiGraphicsExtractor graphics, int x, int y, int width, int height) {
        graphics.fill(leftPos + x, topPos + y, leftPos + x + width, topPos + y + height, 0xFF505050);
        graphics.fill(leftPos + x + 1, topPos + y + 1, leftPos + x + width - 1, topPos + y + height - 1, 0xFF272727);
    }
    @Override protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(font, title, titleLabelX, titleLabelY, 0xFFFFFFFF);
        graphics.text(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 0xFFCCCCCC);
        graphics.text(font, Component.literal(menu.energy() + " / " + menu.kind().capacity + " EU"), 12, 33, 0xFFFFFFFF);
        if (menu.kind().transformer) transformerLabels(graphics);
        else {
            graphics.text(font, Component.translatable("screen.innovativecontraptions.electrical.tier", menu.kind().tier, menu.kind().packet()), 12, 46, 0xFFCCCCCC);
            graphics.text(font, Component.translatable("screen.innovativecontraptions.electrical.charge"), 12, 79, 0xFFCCCCCC);
            graphics.text(font, Component.translatable("screen.innovativecontraptions.electrical.discharge"), 77, 79, 0xFFCCCCCC);
        }
    }
    private void transformerLabels(GuiGraphicsExtractor graphics) {
        int low = menu.kind().packet();
        int input = menu.stepUp() ? low : low * 4;
        int output = menu.stepUp() ? low * 4 : low;
        int count = menu.stepUp() ? 1 : 4;
        graphics.text(font, Component.translatable("screen.innovativecontraptions.electrical.input", input), 12, 49, 0xFFCCCCCC);
        graphics.text(font, Component.translatable("screen.innovativecontraptions.electrical.output", count, output), 12, 64, 0xFFCCCCCC);
        graphics.text(font, Component.translatable("screen.innovativecontraptions.electrical.rate", low * 4), 12, 79, 0xFFCCCCCC);
    }
}
