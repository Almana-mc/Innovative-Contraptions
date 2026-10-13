package me.almana.innovativecontraptions.client.processing;

import me.almana.innovativecontraptions.client.screen.MachineScreen;
import me.almana.innovativecontraptions.content.processing.ProcessingBlockEntity;
import me.almana.innovativecontraptions.content.processing.ProcessingHeat;
import me.almana.innovativecontraptions.content.processing.ProcessingKind;
import me.almana.innovativecontraptions.content.processing.ProcessingMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.material.Fluids;
import java.util.List;

public final class ProcessingScreen extends MachineScreen<ProcessingMenu> {
    public static final int RECIPE_X = 72, RECIPE_Y = 40, RECIPE_WIDTH = 24, RECIPE_HEIGHT = 16;
    private CycleButton<Integer> mode;
    public ProcessingScreen(ProcessingMenu menu, Inventory inventory, Component title) { super(menu, inventory, title, 176, 220); }
    @Override protected void init() {
        super.init();
        titleLabelX = 8;
        titleLabelY = 10;
        inventoryLabelX = 8;
        inventoryLabelY = 126;
        if (menu.kind() == ProcessingKind.SMELTING) {
            addRenderableWidget(Button.builder(Component.translatable("gui.innovativecontraptions.processing.collect_xp"), button -> send(0))
                    .bounds(leftPos + 12, topPos + 83, 122, 18).build());
        }
        if (menu.kind().forming()) {
            mode = cycle(12, 83, 122, 18, value -> Component.translatable("gui.innovativecontraptions.processing.mode."
                    + ProcessingKind.values()[ProcessingKind.EXTRUDING.ordinal() + value].getSerializedName()),
                    List.of(0, 1, 2), menu.value(5), (button, value) -> send(value));
        }
    }
    @Override protected void containerTick() {
        super.containerTick();
        if (mode != null) sync(mode, menu.value(5));
    }
    @Override protected void extractMachine(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        energy(graphics, 150, 35, 12, 60, menu.value(0), menu.value(1));
        arrow(graphics, RECIPE_X, RECIPE_Y, menu.value(2), menu.value(3));
        if (menu.washing()) tank(graphics, 36, 40, 8, 38, Fluids.WATER, menu.value(6), ProcessingBlockEntity.WATER_CAPACITY);
        if (menu.kind() == ProcessingKind.CENTRIFUGING) {
            int heat = Math.clamp(menu.value(7), 0, ProcessingHeat.CENTRIFUGE_HEAT);
            bar(graphics, 12, 86, 82, 6, heat, ProcessingHeat.CENTRIFUGE_HEAT, 0xFFE0482F);
            tooltip(graphics, mouseX, mouseY, 12, 86, 82, 6,
                    Component.translatable("gui.innovativecontraptions.processing.heat_value", heat, ProcessingHeat.CENTRIFUGE_HEAT));
        }
        tooltip(graphics, mouseX, mouseY, 146, 30, 20, 70,
                Component.translatable("gui.innovativecontraptions.processing.energy", menu.value(0), menu.value(1)));
        if (menu.washing()) tooltip(graphics, mouseX, mouseY, 32, 36, 16, 46,
                Component.translatable("gui.innovativecontraptions.processing.water", menu.value(6), ProcessingBlockEntity.WATER_CAPACITY));
        tooltip(graphics, mouseX, mouseY, RECIPE_X, RECIPE_Y, RECIPE_WIDTH, RECIPE_HEIGHT, Component.translatable(menu.kind() == ProcessingKind.INDUCTION
                ? "gui.innovativecontraptions.processing.work" : "gui.innovativecontraptions.processing.progress", menu.value(2), menu.value(3)));
    }
    @Override protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);
        label(graphics, Component.translatable("gui.innovativecontraptions.processing.status." + menu.value(4)), 12, 29);
        if (menu.kind() == ProcessingKind.INDUCTION) label(graphics, Component.translatable("gui.innovativecontraptions.processing.heat",
                menu.value(7) * 100 / ProcessingHeat.INDUCTION_HEAT), 12, 85);
    }
}
