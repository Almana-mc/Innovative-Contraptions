package me.almana.innovativecontraptions.client.agriculture;

import me.almana.innovativecontraptions.client.screen.MachineScreen;
import me.almana.innovativecontraptions.content.agriculture.FarmingContent;
import me.almana.innovativecontraptions.content.agriculture.FarmingMachineBlockEntity;
import me.almana.innovativecontraptions.content.agriculture.FarmingMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public final class FarmingScreen extends MachineScreen<FarmingMenu> {
    public static final int RECIPE_X = 108, RECIPE_Y = 99, RECIPE_WIDTH = 56, RECIPE_HEIGHT = 20;
    public FarmingScreen(FarmingMenu menu, Inventory inventory, Component title) { super(menu, inventory, title, 176, 232); }
    public static void register(RegisterMenuScreensEvent event) {
        event.register(FarmingContent.MENU.get(), FarmingScreen::new);
        event.register(FarmingContent.ANALYZER.get(), AnalyzerScreen::new);
    }
    @Override protected void init() { super.init(); titleLabelX = 8; titleLabelY = 8; inventoryLabelX = 8; inventoryLabelY = 138; }
    @Override protected void extractMachine(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        bar(graphics, 9, 22, 158, 3, menu.value(0), menu.value(1), 0xFFE3AB55);
        bar(graphics, RECIPE_X + 2, RECIPE_Y + 16, 52, 2, menu.value(2), FarmingMachineBlockEntity.SCAN_VOLUME, 0xFFA4C69B);
    }
    @Override protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);
        label(graphics, Component.translatable("gui.innovativecontraptions.farming.status." + menu.value(5)), 8, 29);
        label(graphics, Component.translatable("gui.innovativecontraptions.farming.energy", menu.value(0), menu.value(1)), 8, 126);
        if (menu.matron()) label(graphics, Component.translatable("gui.innovativecontraptions.farming.fluids", menu.value(3), menu.value(4)), 8, 90);
        label(graphics, Component.translatable("gui.innovativecontraptions.farming.scan", menu.value(2), FarmingMachineBlockEntity.SCAN_VOLUME), RECIPE_X + 2, RECIPE_Y + 3);
    }
}
