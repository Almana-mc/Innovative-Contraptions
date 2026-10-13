package me.almana.innovativecontraptions.client.processing;

import me.almana.innovativecontraptions.client.screen.MachineScreen;
import me.almana.innovativecontraptions.content.processing.ProcessingCannerMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import java.util.List;

public final class ProcessingCannerScreen extends MachineScreen<ProcessingCannerMenu> {
    private CycleButton<Integer> mode;
    public ProcessingCannerScreen(ProcessingCannerMenu menu, Inventory inventory, Component title) { super(menu, inventory, title, 176, 222); }
    @Override protected void init() {
        super.init(); titleLabelX = 8; titleLabelY = 8; inventoryLabelY = 128;
        mode = cycle(8, 21, 98, 18, value -> Component.translatable("gui.innovativecontraptions.packager.mode." + value),
                List.of(0, 1, 2, 3), menu.value(5), (button, value) -> send(value));
        addRenderableWidget(Button.builder(Component.translatable("gui.innovativecontraptions.packager.swap"), button -> send(4)).bounds(leftPos + 108, topPos + 21, 60, 18).build());
    }
    @Override protected void containerTick() { super.containerTick(); sync(mode, menu.value(5)); }
    @Override protected void extractMachine(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        bar(graphics, 47, 65, 82, 5, menu.value(2), menu.value(3), 0xFFE0902F);
        for (int i = 0; i < 2; i++) {
            int x = 12 + i * 82;
            var fluid = BuiltInRegistries.FLUID.byId(menu.value(8 + i));
            tank(graphics, x, 80, 70, 8, fluid, menu.value(6 + i), 8000);
            Component name = menu.value(6 + i) == 0 ? Component.translatable("gui.innovativecontraptions.packager.empty") : fluid.getFluidType().getDescription();
            tooltip(graphics, mouseX, mouseY, x, 75, 70, 27, name, Component.literal(menu.value(6 + i) + " / 8000 mB"));
        }
    }
    @Override protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);
        label(graphics, Component.translatable("gui.innovativecontraptions.packager.input"), 12, 91);
        label(graphics, Component.translatable("gui.innovativecontraptions.packager.output"), 94, 91);
        label(graphics, Component.literal(menu.value(0) + " EU"), 119, 109);
    }
}
