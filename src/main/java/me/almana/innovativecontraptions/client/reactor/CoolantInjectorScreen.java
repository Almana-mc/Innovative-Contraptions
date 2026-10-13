package me.almana.innovativecontraptions.client.reactor;

import me.almana.innovativecontraptions.content.reactor.CoolantInjectorBlockEntity;
import me.almana.innovativecontraptions.content.reactor.CoolantInjectorMenu;
import me.almana.innovativecontraptions.content.reactor.ReactorContent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public final class CoolantInjectorScreen extends AbstractContainerScreen<CoolantInjectorMenu> {
    private static final Identifier POWER_BAR = Identifier.fromNamespaceAndPath("innovativecontraptions", "textures/gui/power_bar.png");
    public static final int RECIPE_X = 122, RECIPE_Y = 38, RECIPE_WIDTH = 24, RECIPE_HEIGHT = 16;

    public CoolantInjectorScreen(CoolantInjectorMenu menu, Inventory inventory, Component title) { super(menu, inventory, title, 176, 188); }

    public static void register(RegisterMenuScreensEvent event) { event.register(ReactorContent.INJECTOR_MENU.get(), CoolantInjectorScreen::new); }

    @Override protected void init() {
        super.init();
        titleLabelX = 8;
        titleLabelY = 6;
        inventoryLabelX = 8;
        inventoryLabelY = 96;
    }

    @Override public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        int x = leftPos, y = topPos;
        graphics.fill(x - 1, y - 1, x + imageWidth + 1, y + imageHeight + 1, 0xFF1C1C1C);
        graphics.fill(x, y, x + imageWidth, y + imageHeight, 0xFF313131);
        for (Slot slot : menu.slots) panel(graphics, slot.x - 1, slot.y - 1, 18, 18, 0xFF1A1A1A);
        panel(graphics, 7, 19, 10, 54, 0xFF3A1A1A);
        int height = (int) Math.min(52, 52L * menu.energy() / CoolantInjectorBlockEntity.CAPACITY);
        if (height > 0) graphics.blit(RenderPipelines.GUI_TEXTURED, POWER_BAR, x + 8, y + 72 - height, 0.0F, 52 - height, 8, height, 8, 52);
        panel(graphics, RECIPE_X - 1, RECIPE_Y - 1, RECIPE_WIDTH + 2, RECIPE_HEIGHT + 2, 0xFF1A1A1A);
        graphics.fill(x + RECIPE_X + 2, y + RECIPE_Y + 6, x + RECIPE_X + RECIPE_WIDTH - 2, y + RECIPE_Y + 10, 0xFF6EA8D8);
        int localX = mouseX - x, localY = mouseY - y;
        if (localX >= 7 && localX < 17 && localY >= 19 && localY < 73) graphics.setTooltipForNextFrame(font.split(
                Component.translatable("gui.innovativecontraptions.injector.energy", menu.energy(), CoolantInjectorBlockEntity.CAPACITY), 200), mouseX, mouseY);
    }

    private void panel(GuiGraphicsExtractor graphics, int x, int y, int width, int height, int color) {
        graphics.fill(leftPos + x, topPos + y, leftPos + x + width, topPos + y + height, 0xFF505050);
        graphics.fill(leftPos + x + 1, topPos + y + 1, leftPos + x + width - 1, topPos + y + height - 1, color);
    }

    @Override protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(font, title, titleLabelX, titleLabelY, 0xFFFFFFFF);
        graphics.text(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 0xFFC8C8C8);
    }
}
