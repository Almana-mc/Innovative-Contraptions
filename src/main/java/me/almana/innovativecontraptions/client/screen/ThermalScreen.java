package me.almana.innovativecontraptions.client.screen;

import me.almana.innovativecontraptions.content.thermal.BlastFurnaceBlockEntity;
import me.almana.innovativecontraptions.content.thermal.ThermalContent;
import me.almana.innovativecontraptions.content.thermal.ThermalMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public final class ThermalScreen extends MachineScreen<ThermalMenu> {
    public static final int RECIPE_X = 52;
    public static final int RECIPE_Y = 40;
    public static final int RECIPE_WIDTH = 24;
    public static final int RECIPE_HEIGHT = 16;

    public ThermalScreen(ThermalMenu menu, Inventory inventory, Component title) { super(menu, inventory, title, 176, 208); }
    public static void registerScreens(RegisterMenuScreensEvent event) { event.register(ThermalContent.MENU.get(), ThermalScreen::new); }

    @Override protected void init() {
        super.init();
        titleLabelX = 8;
        titleLabelY = 10;
        inventoryLabelX = 8;
        inventoryLabelY = 114;
    }

    @Override protected void extractMachine(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        if (menu.heater()) energy(graphics, 153, 34, 8, 52, menu.value(0), menu.value(1));
        else bar(graphics, 153, 34, 8, 52, menu.value(0), 100, 0xFFE0902F);
        tooltip(graphics, mouseX, mouseY, 152, 33, 10, 54, menu.heater() ? Component.literal(menu.value(0) + " / " + menu.value(1) + " EU")
                : Component.translatable("screen.innovativecontraptions.heat", menu.value(0)));
        tooltip(graphics, mouseX, mouseY, 150, 90, 14, 16, Component.translatable("screen.innovativecontraptions.heat_ports"));
        if (menu.heater()) return;
        arrow(graphics, RECIPE_X, RECIPE_Y, menu.value(2), menu.value(3));
        bar(graphics, 44, 73, 40, 7, menu.value(1), BlastFurnaceBlockEntity.AIR_CAPACITY, 0xFF82AFC1);
        tooltip(graphics, mouseX, mouseY, 43, 72, 42, 9,
                Component.literal(menu.value(1) + " / " + BlastFurnaceBlockEntity.AIR_CAPACITY + " mB"));
    }

    @Override protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);
        Component status = menu.heater()
                ? Component.translatable("screen.innovativecontraptions.heater_rate", menu.value(2), menu.value(4))
                : Component.translatable("screen.innovativecontraptions.thermal_status." + menu.value(4));
        label(graphics, status, 12, 94);
        label(graphics, Component.literal("?"), 154, 94);
    }
}
