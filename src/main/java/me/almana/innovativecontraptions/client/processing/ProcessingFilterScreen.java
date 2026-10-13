package me.almana.innovativecontraptions.client.processing;

import me.almana.innovativecontraptions.client.screen.MachineScreen;
import me.almana.innovativecontraptions.content.processing.ProcessingFilterMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import java.util.ArrayList;
import java.util.List;

public final class ProcessingFilterScreen extends MachineScreen<ProcessingFilterMenu> {
    private CycleButton<Boolean> list;
    private final List<CycleButton<Boolean>> options = new ArrayList<>();
    private Button charge;
    public ProcessingFilterScreen(ProcessingFilterMenu menu, Inventory inventory, Component title) { super(menu, inventory, title, 176, 228); }
    @Override protected void init() {
        super.init(); titleLabelX = 8; titleLabelY = 8; inventoryLabelY = 140;
        list = cycle(78, 30, 90, 16, value -> Component.translatable("gui.innovativecontraptions.routing." + (value ? "blacklist" : "whitelist")),
                List.of(false, true), (menu.flags() & 1) != 0, (button, value) -> send(0));
        options.clear();
        for (int i = 1; i < 4; i++) {
            int action = i;
            options.add(cycle(78, 30 + i * 18, 90, 16, value -> Component.translatable("gui.innovativecontraptions.routing.option." + action).append(value ? " ✓" : " −"),
                    List.of(false, true), (menu.flags() & 1 << i) != 0, (button, value) -> send(action)));
        }
        charge = addRenderableWidget(Button.builder(chargeText(), button -> send(4)).bounds(leftPos + 8, topPos + 104, 160, 16).build());
        addRenderableWidget(Button.builder(Component.literal("−"), button -> send(5)).bounds(leftPos + 8, topPos + 122, 20, 14).build());
        addRenderableWidget(Button.builder(Component.literal("+"), button -> send(6)).bounds(leftPos + 148, topPos + 122, 20, 14).build());
    }
    private Component chargeText() { return Component.translatable("gui.innovativecontraptions.routing.charge." + (menu.flags() >> 4 & 3)); }
    @Override protected void containerTick() {
        super.containerTick();
        sync(list, (menu.flags() & 1) != 0);
        for (int i = 0; i < options.size(); i++) sync(options.get(i), (menu.flags() & 1 << i + 1) != 0);
        charge.setMessage(chargeText());
    }
    @Override protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);
        label(graphics, Component.translatable("gui.innovativecontraptions.routing.ghosts"), 8, 19);
        label(graphics, Component.literal(menu.charge() + " EU"), 48, 125);
    }
}
