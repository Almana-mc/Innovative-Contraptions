package me.almana.innovativecontraptions.client.screen;

import me.almana.innovativecontraptions.content.machine.MaceratorMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class MaceratorScreen extends MachineScreen<MaceratorMenu> {
    public static final int RECIPE_X = 73;
    public static final int RECIPE_Y = 51;
    public static final int RECIPE_WIDTH = 24;
    public static final int RECIPE_HEIGHT = 16;
    private static final int ENERGY_X = 140, ENERGY_Y = 32, ENERGY_WIDTH = 14, ENERGY_HEIGHT = 50;

    public MaceratorScreen(MaceratorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 176, 212);
    }

    @Override
    protected void init() {
        super.init();
        titleLabelX = 8;
        titleLabelY = 10;
        inventoryLabelX = 8;
        inventoryLabelY = 118;
    }

    @Override
    protected void extractMachine(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        energy(graphics, ENERGY_X, ENERGY_Y, ENERGY_WIDTH, ENERGY_HEIGHT, menu.energy(), menu.energyCapacity());
        arrow(graphics, RECIPE_X, RECIPE_Y, menu.progress(), menu.progressMax());
        tooltip(graphics, mouseX, mouseY, ENERGY_X, ENERGY_Y, ENERGY_WIDTH, ENERGY_HEIGHT,
                Component.literal(menu.energy() + " / " + menu.energyCapacity() + " EU"));
        tooltip(graphics, mouseX, mouseY, RECIPE_X, RECIPE_Y, RECIPE_WIDTH, RECIPE_HEIGHT,
                Component.literal(menu.progress() + " / " + menu.progressMax() + " ticks"));
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);
        Component status = Component.literal(status());
        label(graphics, status, imageWidth - 8 - font.width(status), titleLabelY);
    }

    private String status() {
        return switch (menu.statusCode()) {
            case 0 -> "Ready";
            case 1 -> "No recipe";
            case 2 -> "No power";
            case 3 -> "Output blocked";
            case 4 -> "Processing";
            default -> "No recipe";
        };
    }
}
