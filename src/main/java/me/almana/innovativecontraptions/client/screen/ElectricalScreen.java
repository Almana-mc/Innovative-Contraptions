package me.almana.innovativecontraptions.client.screen;

import java.util.stream.IntStream;
import me.almana.innovativecontraptions.content.energy.storage.ElectricalMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class ElectricalScreen extends MachineScreen<ElectricalMenu> {
    private CycleButton<Integer> mode;
    public ElectricalScreen(ElectricalMenu menu, Inventory inventory, Component title) { super(menu, inventory, title, 176, 224); }
    @Override protected void init() {
        super.init();
        titleLabelX = 8;
        titleLabelY = 10;
        inventoryLabelX = 8;
        inventoryLabelY = 130;
        mode = cycle(8, 106, 160, 20, this::modeText, IntStream.range(0, menu.modeCount()).boxed().toList(), menu.mode(),
                (button, value) -> send(value));
    }
    private Component modeText(int value) {
        String kind = menu.kind().transformer ? "transformer" : menu.kind().pad ? "pad" : "storage";
        return Component.translatable("screen.innovativecontraptions." + kind + ".mode." + value);
    }
    @Override protected void containerTick() { super.containerTick(); sync(mode, menu.mode()); }
    @Override protected void extractMachine(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        bar(graphics, 13, 92, 130, 5, menu.energy(), menu.kind().capacity, 0xFFD24A4A);
        String key = menu.kind().transformer ? "transformer.guide" : menu.kind().pad ? "pad.guide" : "storage.guide";
        tooltip(graphics, mouseX, mouseY, 6, 28, 140, 76, Component.translatable("screen.innovativecontraptions." + key));
    }
    @Override protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);
        label(graphics, Component.literal(menu.energy() + " / " + menu.kind().capacity + " EU"), 12, 33);
        if (menu.kind().transformer) transformerLabels(graphics);
        else {
            label(graphics, Component.translatable("screen.innovativecontraptions.electrical.tier", menu.kind().tier, menu.kind().packet()), 12, 46);
            label(graphics, Component.translatable("screen.innovativecontraptions.electrical.charge"), 12, 79);
            label(graphics, Component.translatable("screen.innovativecontraptions.electrical.discharge"), 77, 79);
        }
    }
    private void transformerLabels(GuiGraphicsExtractor graphics) {
        int low = menu.kind().packet();
        int input = menu.stepUp() ? low : low * 4;
        int output = menu.stepUp() ? low * 4 : low;
        int count = menu.stepUp() ? 1 : 4;
        label(graphics, Component.translatable("screen.innovativecontraptions.electrical.input", input), 12, 49);
        label(graphics, Component.translatable("screen.innovativecontraptions.electrical.output", count, output), 12, 64);
        label(graphics, Component.translatable("screen.innovativecontraptions.electrical.rate", low * 4), 12, 79);
    }
}
