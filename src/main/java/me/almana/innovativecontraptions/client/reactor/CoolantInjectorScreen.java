package me.almana.innovativecontraptions.client.reactor;

import me.almana.innovativecontraptions.client.screen.MachineScreen;
import me.almana.innovativecontraptions.content.reactor.CoolantInjectorBlockEntity;
import me.almana.innovativecontraptions.content.reactor.CoolantInjectorMenu;
import me.almana.innovativecontraptions.content.reactor.ReactorContent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public final class CoolantInjectorScreen extends MachineScreen<CoolantInjectorMenu> {
    public static final int RECIPE_X = 122, RECIPE_Y = 38, RECIPE_WIDTH = 24, RECIPE_HEIGHT = 16;

    public CoolantInjectorScreen(CoolantInjectorMenu menu, Inventory inventory, Component title) { super(menu, inventory, title, 176, 188); }

    public static void register(RegisterMenuScreensEvent event) { event.register(ReactorContent.INJECTOR_MENU.get(), CoolantInjectorScreen::new); }

    @Override protected void extractMachine(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        energy(graphics, 8, 20, 8, 52, menu.energy(), CoolantInjectorBlockEntity.CAPACITY);
        arrow(graphics, RECIPE_X, RECIPE_Y, 0, 1);
        tooltip(graphics, mouseX, mouseY, 7, 19, 10, 54,
                Component.translatable("gui.innovativecontraptions.injector.energy", menu.energy(), CoolantInjectorBlockEntity.CAPACITY));
    }
}
