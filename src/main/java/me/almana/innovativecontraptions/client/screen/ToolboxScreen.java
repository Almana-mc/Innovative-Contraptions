package me.almana.innovativecontraptions.client.screen;

import me.almana.innovativecontraptions.content.tool.ToolboxMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class ToolboxScreen extends MachineScreen<ToolboxMenu> {
    public ToolboxScreen(ToolboxMenu menu, Inventory inventory, Component title) { super(menu, inventory, title, 176, 149); }
    @Override protected void init() {
        super.init();
        titleLabelY = 4;
    }
}
