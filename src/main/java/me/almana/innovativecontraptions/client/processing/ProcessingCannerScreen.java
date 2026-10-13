package me.almana.innovativecontraptions.client.processing;

import me.almana.innovativecontraptions.content.processing.ProcessingCannerMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import java.util.List;

public final class ProcessingCannerScreen extends AbstractContainerScreen<ProcessingCannerMenu> {
    private Button mode;
    public ProcessingCannerScreen(ProcessingCannerMenu menu, Inventory inventory, Component title) { super(menu, inventory, title, 176, 222); }
    @Override protected void init() {
        super.init(); titleLabelX = 8; titleLabelY = 8; inventoryLabelY = 128;
        mode = addRenderableWidget(Button.builder(modeLabel(), button -> send((menu.value(5) + 1) % 4)).bounds(leftPos + 8, topPos + 21, 98, 18).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.innovativecontraptions.packager.swap"), button -> send(4)).bounds(leftPos + 108, topPos + 21, 60, 18).build());
    }
    private void send(int value) { minecraft.gameMode.handleInventoryButtonClick(menu.containerId, value); }
    private Component modeLabel() { return Component.translatable("gui.innovativecontraptions.packager.mode." + menu.value(5)); }
    @Override protected void containerTick() { super.containerTick(); mode.setMessage(modeLabel()); }
    @Override public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        int x = leftPos, y = topPos;
        graphics.fill(x - 1, y - 1, x + 177, y + 223, 0xFF181818);
        graphics.fill(x, y, x + 176, y + 222, 0xFF313131);
        graphics.fill(x + 5, y + 136, x + 171, y + 218, 0xFF414141);
        for (var slot : menu.slots) {
            graphics.fill(x + slot.x - 1, y + slot.y - 1, x + slot.x + 17, y + slot.y + 17, 0xFF777777);
            graphics.fill(x + slot.x, y + slot.y, x + slot.x + 16, y + slot.y + 16, 0xFF181818);
        }
        graphics.fill(x + 47, y + 65, x + 129, y + 70, 0xFF181818);
        graphics.fill(x + 47, y + 65, x + 47 + 82 * menu.value(2) / Math.max(1, menu.value(3)), y + 70, 0xFFE0902F);
        for (int tank = 0; tank < 2; tank++) {
            int start = x + 12 + tank * 82;
            graphics.fill(start, y + 80, start + 70, y + 88, 0xFF181818);
            graphics.fill(start, y + 80, start + 70 * menu.value(6 + tank) / 8000, y + 88, tank == 0 ? 0xFF4995DA : 0xFF78A760);
            if (mouseX >= start && mouseX < start + 70 && mouseY >= y + 75 && mouseY < y + 102) {
                var fluid = BuiltInRegistries.FLUID.byId(menu.value(8 + tank));
                Component name = menu.value(6 + tank) == 0 ? Component.translatable("gui.innovativecontraptions.packager.empty") : fluid.getFluidType().getDescription();
                graphics.setTooltipForNextFrame(List.of(name.getVisualOrderText(), Component.literal(menu.value(6 + tank) + " / 8000 mB").getVisualOrderText()), mouseX, mouseY);
            }
        }
        graphics.text(font, Component.literal(menu.value(0) + " EU"), x + 119, y + 109, 0xFFFFB866);
    }
    @Override protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(font, title, titleLabelX, titleLabelY, 0xFFFFFFFF);
        graphics.text(font, Component.translatable("gui.innovativecontraptions.packager.input"), 12, 91, 0xFFB5D9F4);
        graphics.text(font, Component.translatable("gui.innovativecontraptions.packager.output"), 94, 91, 0xFFC5E4AF);
        graphics.text(font, playerInventoryTitle, 8, inventoryLabelY, 0xFFD0D0D0);
    }
}
