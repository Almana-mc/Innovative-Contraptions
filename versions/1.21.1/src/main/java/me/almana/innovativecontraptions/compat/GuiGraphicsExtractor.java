package me.almana.innovativecontraptions.compat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class GuiGraphicsExtractor {
    private final GuiGraphics graphics;
    Runnable deferredTooltip;

    public GuiGraphicsExtractor(GuiGraphics graphics) {
        this.graphics = graphics;
    }

    public void fill(int x0, int y0, int x1, int y1, int color) {
        graphics.fill(x0, y0, x1, y1, color);
    }

    public void text(Font font, String text, int x, int y, int color) {
        if (color >>> 24 != 0) graphics.drawString(font, text, x, y, color, true);
    }

    public void text(Font font, Component text, int x, int y, int color) {
        if (color >>> 24 != 0) graphics.drawString(font, text, x, y, color, true);
    }

    public void text(Font font, FormattedCharSequence text, int x, int y, int color) {
        if (color >>> 24 != 0) graphics.drawString(font, text, x, y, color, true);
    }

    public void blit(RenderPipelines pipeline, ResourceLocation texture, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight) {
        graphics.blit(texture, x, y, u, v, width, height, textureWidth, textureHeight);
    }

    public void item(ItemStack stack, int x, int y) {
        graphics.renderItem(stack, x, y);
    }

    public void setTooltipForNextFrame(List<FormattedCharSequence> lines, int x, int y) {
        if (deferredTooltip == null && !lines.isEmpty()) deferredTooltip = () -> graphics.renderTooltip(Minecraft.getInstance().font, lines, x, y);
    }

    void extractDeferredElements() {
        if (deferredTooltip != null) deferredTooltip.run();
    }
}
