package me.almana.innovativecontraptions.client.screen;

import me.almana.innovativecontraptions.content.fluid.CoolantContent;
import me.almana.innovativecontraptions.content.thermal.HeatExchangerMenu;
import me.almana.innovativecontraptions.content.thermal.LiquidHeatExchangerBlockEntity;
import me.almana.innovativecontraptions.content.thermal.ThermalContent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public final class HeatExchangerScreen extends MachineScreen<HeatExchangerMenu> {
    public static final int RECIPE_X = 44, RECIPE_Y = 86, RECIPE_WIDTH = 88, RECIPE_HEIGHT = 9;
    private static final int HOT_TANK_X = 28, COOLANT_TANK_X = 136, TANK_Y = 20, TANK_WIDTH = 12, TANK_HEIGHT = 60;

    public HeatExchangerScreen(HeatExchangerMenu menu, Inventory inventory, Component title) { super(menu, inventory, title, 176, 200); }

    public static void register(RegisterMenuScreensEvent event) { event.register(ThermalContent.EXCHANGER_MENU.get(), HeatExchangerScreen::new); }

    @Override protected void init() {
        super.init();
        titleLabelX = 8;
        titleLabelY = 6;
        inventoryLabelX = 8;
        inventoryLabelY = 106;
    }

    @Override protected void extractMachine(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        tank(graphics, HOT_TANK_X, TANK_Y, TANK_WIDTH, TANK_HEIGHT, CoolantContent.HOT_COOLANT.get(), menu.value(0), LiquidHeatExchangerBlockEntity.TANK);
        tank(graphics, COOLANT_TANK_X, TANK_Y, TANK_WIDTH, TANK_HEIGHT, CoolantContent.COOLANT.get(), menu.value(1), LiquidHeatExchangerBlockEntity.TANK);
        tooltip(graphics, mouseX, mouseY, HOT_TANK_X, TANK_Y, TANK_WIDTH, TANK_HEIGHT,
                Component.literal(menu.value(0) + " / " + LiquidHeatExchangerBlockEntity.TANK + " mB"));
        tooltip(graphics, mouseX, mouseY, COOLANT_TANK_X, TANK_Y, TANK_WIDTH, TANK_HEIGHT,
                Component.literal(menu.value(1) + " / " + LiquidHeatExchangerBlockEntity.TANK + " mB"));
    }

    @Override protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);
        label(graphics, Component.translatable("gui.innovativecontraptions.exchanger.heat", menu.value(3),
                menu.value(2) * LiquidHeatExchangerBlockEntity.HU_PER_CONDUCTOR), RECIPE_X, RECIPE_Y);
    }
}
