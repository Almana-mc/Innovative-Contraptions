package me.almana.innovativecontraptions.client.matter;

import me.almana.innovativecontraptions.client.screen.MachineScreen;
import me.almana.innovativecontraptions.content.matter.*;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public final class MatterScreen extends MachineScreen<MatterMenu> {
    public static final int RECIPE_X = 12, RECIPE_Y = 67, RECIPE_WIDTH = 162, RECIPE_HEIGHT = 8;
    public MatterScreen(MatterMenu menu, Inventory inventory, Component title) { super(menu, inventory, title, 188, 240); }
    public static void registerScreens(RegisterMenuScreensEvent event) { event.register(MatterContent.MENU.get(), MatterScreen::new); }
    @Override protected void init() {
        super.init(); titleLabelX = 12; titleLabelY = 10; inventoryLabelX = 12; inventoryLabelY = 145;
        if (menu.kind() == 1) { button(0, "clear", 12, 116, 70); button(1, "save", 86, 116, 88); }
        if (menu.kind() == 2) { button(2, "import", 12, 116, 78); button(3, "export", 96, 116, 78); }
        if (menu.kind() == 3) { button(4, "stop", 12, 116, 48); button(5, "once", 65, 116, 48); button(6, "repeat", 118, 116, 56); }
        if (menu.kind() >= 2) { button(7, "previous", 12, 91, 30); button(8, "next", 144, 91, 30); }
    }
    private void button(int action, String key, int x, int y, int width) {
        addRenderableWidget(Button.builder(Component.translatable("matter.innovativecontraptions." + key), button -> send(action))
                .bounds(leftPos + x, topPos + y, width, 20).build());
    }
    @Override protected void extractMachine(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        bar(graphics, RECIPE_X, RECIPE_Y, RECIPE_WIDTH, RECIPE_HEIGHT, menu.value(2), menu.value(3), 0xFF72B9C5);
        if (menu.value(9) > 0) graphics.item(new ItemStack(BuiltInRegistries.ITEM.byId(menu.value(9))), leftPos + 46, topPos + 92);
        int x = mouseX - leftPos, y = mouseY - topPos;
        if (y >= 36 && y < 54) {
            int slot = (x - 12) / 18;
            if (x >= 12 && slot < 9 && menu.slots.get(slot).isActive() && !menu.slots.get(slot).hasItem()) {
                String key = switch (slot) { case 0 -> menu.kind() == 0 ? "amplifier" : "target"; case 1 -> menu.kind() == 1 || menu.kind() == 2 ? "memory" : "matter_input"; case 2 -> "output"; case 3 -> "matter_output"; case 4 -> "battery"; default -> "upgrade"; };
                graphics.setTooltipForNextFrame(font.split(Component.translatable("matter.innovativecontraptions.slot." + key), 220), mouseX, mouseY);
            }
        }
    }
    @Override protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);
        if (menu.kind() == 0) label(graphics, Component.translatable("matter.innovativecontraptions.progress",
                menu.value(2) * 100L / Math.max(1, menu.value(3))), 12, 23);
        else if (menu.kind() != 2) label(graphics, Component.literal(menu.value(0) + " / " + menu.value(1) + " EU"), 12, 23);
        if (menu.kind() == 1) label(graphics, Component.literal(menu.value(2) * 100L / Math.max(1, menu.value(3)) + "%"), 12, 56);
        label(graphics, Component.translatable("matter.innovativecontraptions.status." + menu.value(5)), 12, 79);
        if (menu.kind() >= 2) {
            label(graphics, Component.literal((menu.value(8) == 0 ? 0 : menu.value(7) + 1) + " / " + menu.value(8)), 66, 97);
            Component cost = Component.literal(menu.value(10) / 1000.0 + " UU, 0 EU");
            label(graphics, cost, 174 - font.width(cost), 56);
        }
        if (menu.kind() == 0) label(graphics, Component.literal(menu.value(4) + " UU"), 12, 56);
        if (menu.kind() == 3) label(graphics, Component.literal((menu.value(4) * 1000L + menu.value(12)) / 1000.0 + " UU"), 12, 56);
        if (menu.kind() == 1 && menu.value(10) > 0) label(graphics, Component.literal(menu.value(10) / 1000.0 + " UU, 0 EU"), 66, 97);
    }
}
