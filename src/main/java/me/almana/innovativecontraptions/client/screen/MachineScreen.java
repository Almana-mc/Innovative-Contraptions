package me.almana.innovativecontraptions.client.screen;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import me.almana.innovativecontraptions.InnovativeContraptions;
import me.almana.innovativecontraptions.client.FluidSprites;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public abstract class MachineScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {
    private static final int TEXT = 0xFF404040;
    protected static final Identifier DISABLED_SLOT = Identifier.withDefaultNamespace("container/crafter/disabled_slot");
    private static final Identifier SLOT = Identifier.withDefaultNamespace("container/slot");
    private static final Identifier ARROW = Identifier.withDefaultNamespace("container/furnace/burn_progress");
    private static final Identifier FLAME = Identifier.withDefaultNamespace("container/furnace/lit_progress");
    private static final Identifier PANEL = sprite("panel");
    private static final Identifier INSET = sprite("inset");
    private static final Identifier ENERGY = sprite("energy");
    private static final Identifier ARROW_EMPTY = sprite("arrow_empty");
    private static final Identifier FLAME_EMPTY = sprite("flame_empty");
    private int clicked = -10;
    private int ticks;

    protected MachineScreen(T menu, Inventory inventory, Component title, int width, int height) {
        super(menu, inventory, title, width, height);
    }

    private static Identifier sprite(String name) {
        return Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, "container/" + name);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, PANEL, leftPos, topPos, imageWidth, imageHeight);
        for (Slot slot : menu.slots) {
            if (slot.isActive()) graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT, leftPos + slot.x - 1, topPos + slot.y - 1, 18, 18);
        }
        extractMachine(graphics, mouseX, mouseY);
    }

    protected void extractMachine(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {}

    private void inset(GuiGraphicsExtractor graphics, int x, int y, int width, int height) {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, INSET, leftPos + x - 1, topPos + y - 1, width + 2, height + 2);
    }

    protected void energy(GuiGraphicsExtractor graphics, int x, int y, int width, int height, long value, long max) {
        inset(graphics, x, y, width, height);
        int filled = scaled(height, value, max);
        if (filled > 0) graphics.blitSprite(RenderPipelines.GUI_TEXTURED, ENERGY, width, height, 0, height - filled,
                leftPos + x, topPos + y + height - filled, width, filled);
    }

    // fills along the longer axis
    protected void bar(GuiGraphicsExtractor graphics, int x, int y, int width, int height, long value, long max, int color) {
        inset(graphics, x, y, width, height);
        int left = leftPos + x, top = topPos + y;
        if (height >= width) graphics.fill(left, top + height - scaled(height, value, max), left + width, top + height, color);
        else graphics.fill(left, top, left + scaled(width, value, max), top + height, color);
    }

    protected void arrow(GuiGraphicsExtractor graphics, int x, int y, long value, long max) {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, ARROW_EMPTY, leftPos + x, topPos + y, 24, 16);
        int filled = scaled(24, value, max);
        if (filled > 0) graphics.blitSprite(RenderPipelines.GUI_TEXTURED, ARROW, 24, 16, 0, 0, leftPos + x, topPos + y, filled, 16);
    }

    protected void flame(GuiGraphicsExtractor graphics, int x, int y, long value, long max) {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, FLAME_EMPTY, leftPos + x, topPos + y, 14, 14);
        if (value <= 0) return;
        int filled = scaled(13, value, max) + 1;
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, FLAME, 14, 14, 0, 14 - filled, leftPos + x, topPos + y + 14 - filled, 14, filled);
    }

    protected void tank(GuiGraphicsExtractor graphics, int x, int y, int width, int height, Fluid fluid, long amount, long capacity) {
        inset(graphics, x, y, width, height);
        if (amount <= 0 || fluid == Fluids.EMPTY) return;
        int left = leftPos + x, top = topPos + y;
        if (height >= width) graphics.enableScissor(left, top + height - scaled(height, amount, capacity), left + width, top + height);
        else graphics.enableScissor(left, top, left + scaled(width, amount, capacity), top + height);
        TextureAtlasSprite sprite = FluidSprites.sprite(fluid);
        int tint = FluidSprites.tint(fluid);
        for (int tileY = top; tileY < top + height; tileY += 16) {
            for (int tileX = left; tileX < left + width; tileX += 16) {
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, tileX, tileY, 16, 16, tint);
            }
        }
        graphics.disableScissor();
    }

    protected void label(GuiGraphicsExtractor graphics, Component text, int x, int y) {
        graphics.text(font, text, x, y, TEXT, false);
    }

    protected void label(GuiGraphicsExtractor graphics, FormattedCharSequence text, int x, int y) {
        graphics.text(font, text, x, y, TEXT, false);
    }

    protected void tooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY, int x, int y, int width, int height, Component... lines) {
        if (!isHovering(x, y, width, height, mouseX, mouseY)) return;
        graphics.setTooltipForNextFrame(Arrays.stream(lines).flatMap(line -> font.split(line, 220).stream()).toList(), mouseX, mouseY);
    }

    protected <V> CycleButton<V> cycle(int x, int y, int width, int height, Function<V, Component> text, List<V> values, V initial,
                                       CycleButton.OnValueChange<V> change) {
        return cycle(x, y, width, height, text, values, initial, change, null);
    }

    protected <V> CycleButton<V> cycle(int x, int y, int width, int height, Function<V, Component> text, List<V> values, V initial,
                                       CycleButton.OnValueChange<V> change, Tooltip tooltip) {
        //? if >=26.1 {
        var builder = CycleButton.builder(text, initial).withValues(values);
        //?} else
        //var builder = CycleButton.builder(text).withValues(values).withInitialValue(initial);
        builder.displayOnlyValue().withCustomNarration(button -> Component.translatable("gui.narrate.button", button.getMessage()));
        builder.withTooltip(value -> tooltip);
        CycleButton.OnValueChange<V> clickThenChange = (button, value) -> {
            clicked = ticks;
            change.onValueChange(button, value);
        };
        return addRenderableWidget(builder.create(leftPos + x, topPos + y, width, height, Component.empty(), clickThenChange));
    }

    protected <V> void sync(CycleButton<V> button, V value) {
        if (ticks - clicked > 10) button.setValue(value);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        ticks++;
    }

    protected void send(int action) {
        minecraft.gameMode.handleInventoryButtonClick(menu.containerId, action);
    }

    private static int scaled(int size, long value, long max) {
        return (int) (size * Math.max(0, Math.min(value, max)) / Math.max(1, max));
    }
}
