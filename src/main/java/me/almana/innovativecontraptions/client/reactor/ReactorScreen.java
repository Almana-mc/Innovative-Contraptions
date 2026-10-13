package me.almana.innovativecontraptions.client.reactor;

import java.util.Locale;
import me.almana.innovativecontraptions.client.screen.MachineScreen;
import me.almana.innovativecontraptions.content.fluid.CoolantContent;
import me.almana.innovativecontraptions.content.reactor.NuclearReactorBlockEntity;
import me.almana.innovativecontraptions.content.reactor.ReactorContent;
import me.almana.innovativecontraptions.content.reactor.ReactorMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public final class ReactorScreen extends MachineScreen<ReactorMenu> {
    public static final int BAR_X = 26, BAR_Y = 129, BAR_WIDTH = 162, BAR_HEIGHT = 6, TEXT_Y = 138, TANK_Y = 39, TANK_HEIGHT = 66;

    public ReactorScreen(ReactorMenu menu, Inventory inventory, Component title) { super(menu, inventory, title, 212, 232); }

    public static void register(RegisterMenuScreensEvent event) { event.register(ReactorContent.MENU.get(), ReactorScreen::new); }

    @Override protected void init() {
        super.init();
        titleLabelX = 26;
        titleLabelY = 6;
    }

    @Override protected void extractMachine(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        for (int index = 0; index < NuclearReactorBlockEntity.GRID; index++) {
            Slot slot = menu.slots.get(index);
            if (!slot.isActive()) graphics.blitSprite(RenderPipelines.GUI_TEXTURED, DISABLED_SLOT, leftPos + slot.x - 1, topPos + slot.y - 1, 18, 18);
        }
        if (menu.fluid()) {
            tank(graphics, 9, TANK_Y, 14, TANK_HEIGHT, CoolantContent.COOLANT.get(), menu.coolant(), NuclearReactorBlockEntity.TANK);
            tank(graphics, 191, TANK_Y, 14, TANK_HEIGHT, CoolantContent.HOT_COOLANT.get(), menu.hotCoolant(), NuclearReactorBlockEntity.TANK);
            tooltip(graphics, mouseX, mouseY, 8, TANK_Y, 16, TANK_HEIGHT, Component.literal(menu.coolant() + " / " + NuclearReactorBlockEntity.TANK + " mB"));
            tooltip(graphics, mouseX, mouseY, 190, TANK_Y, 16, TANK_HEIGHT, Component.literal(menu.hotCoolant() + " / " + NuclearReactorBlockEntity.TANK + " mB"));
        }
        float ratio = Math.min(1F, (float) menu.heat() / Math.max(1, menu.maxHeat()));
        bar(graphics, BAR_X, BAR_Y, BAR_WIDTH, BAR_HEIGHT, menu.heat(), menu.maxHeat(), 0xFF000000 | Mth.hsvToRgb((1F - ratio) / 3F, 0.8F, 0.85F));
        tooltip(graphics, mouseX, mouseY, BAR_X, BAR_Y, BAR_WIDTH, BAR_HEIGHT, Component.translatable("gui.innovativecontraptions.reactor.temperature",
                String.format(Locale.ROOT, "%.2f", 100.0 * menu.heat() / Math.max(1, menu.maxHeat()))));
        tooltip(graphics, mouseX, mouseY, BAR_X, TEXT_Y, BAR_WIDTH, 9,
                Component.translatable(menu.fluid() ? "gui.innovativecontraptions.reactor.mode.fluid" : "gui.innovativecontraptions.reactor.mode.electric"));
    }

    @Override protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        label(graphics, title, titleLabelX, titleLabelY);
        Component output = menu.fluid()
                ? Component.translatable("gui.innovativecontraptions.reactor.hu_output", menu.huPerSecond())
                : Component.translatable("gui.innovativecontraptions.reactor.eu_output", menu.euPerTick());
        label(graphics, output, BAR_X, TEXT_Y);
    }
}
