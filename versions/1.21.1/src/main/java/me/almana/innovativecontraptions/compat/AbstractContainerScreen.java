package me.almana.innovativecontraptions.compat;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public abstract class AbstractContainerScreen<T extends AbstractContainerMenu> extends net.minecraft.client.gui.screens.inventory.AbstractContainerScreen<T> {
    private GuiGraphicsExtractor extractor;

    public AbstractContainerScreen(T menu, Inventory inventory, Component title, int imageWidth, int imageHeight) {
        super(menu, inventory, title);
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.inventoryLabelY = imageHeight - 94;
    }

    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {}

    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(font, title, titleLabelX, titleLabelY, 0xFF404040, false);
        graphics.text(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 0xFF404040, false);
    }

    // first deferred tooltip wins
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        extractor = new GuiGraphicsExtractor(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        if (extractor.deferredTooltip == null) renderTooltip(graphics, mouseX, mouseY);
        extractor.extractDeferredElements();
    }

    @Override
    protected final void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        extractBackground(extractor, mouseX, mouseY, partialTick);
    }

    @Override
    protected final void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        extractLabels(extractor, mouseX, mouseY);
    }
}
