package me.almana.innovativecontraptions.client.screen;

import me.almana.innovativecontraptions.content.generator.GeneratorMenu;
import me.almana.innovativecontraptions.content.generator.GeothermalGeneratorBlock;
import me.almana.innovativecontraptions.content.generator.WaterMillBlock;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluids;

public class GeneratorScreen extends MachineScreen<GeneratorMenu> {
    public static final int RECIPE_X = 140;
    public static final int RECIPE_Y = 32;
    public static final int RECIPE_WIDTH = 14;
    public static final int RECIPE_HEIGHT = 50;

    public GeneratorScreen(GeneratorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 176, 188);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = 8;
        this.titleLabelY = 10;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = 94;
    }

    @Override
    protected void extractMachine(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        energy(graphics, RECIPE_X, RECIPE_Y, RECIPE_WIDTH, RECIPE_HEIGHT, this.menu.energy(), this.menu.energyMax());
        Block block = this.menu.generatorBlock();
        if (block instanceof WaterMillBlock) bar(graphics, 76, 61, 6, 18, this.menu.burn(), this.menu.burnMax(), 0xFF4995DA);
        else if (block instanceof GeothermalGeneratorBlock) tank(graphics, 76, 61, 6, 18, Fluids.LAVA, this.menu.burn(), this.menu.burnMax());
        else if (this.menu.hasFuel) flame(graphics, 74, 63, this.menu.burn(), this.menu.burnMax());
        tooltip(graphics, mouseX, mouseY, RECIPE_X, RECIPE_Y, RECIPE_WIDTH, RECIPE_HEIGHT,
                Component.literal(this.menu.energy() + " / " + this.menu.energyMax() + (this.menu.kinetic ? " KU/t" : " EU")),
                Component.literal(formatRate(this.menu.outputPerTick()).replace("EU", this.menu.kinetic ? "KU" : "EU")),
                Component.translatable(this.menu.kinetic ? "gui.innovativecontraptions.kinetic.connection" : "gui.innovativecontraptions.generator.output"));
    }

    private static String formatRate(int rawHundredths) {
        if (rawHundredths % 100 == 0) {
            return (rawHundredths / 100) + " EU/t";
        }
        String value = String.format("%.2f", rawHundredths / 100.0);
        if (value.endsWith("0")) {
            value = value.substring(0, value.length() - 1);
        }
        return value + " EU/t";
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);
        if (this.menu.kinetic) label(graphics,
                Component.translatable("gui.innovativecontraptions.kinetic.health", this.menu.burn() / 100), 12, 30);
        if (this.menu.kinetic) label(graphics,
                Component.translatable("gui.innovativecontraptions.kinetic.status." + this.menu.status()), 12, 78);
    }
}
