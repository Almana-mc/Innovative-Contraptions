package me.almana.innovativecontraptions.client;

import me.almana.innovativecontraptions.registry.ICItems;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

public final class ItemTints {
    public static void register(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tint) -> tint == 0 ? 0xFF9DD6EB : -1, ICItems.AIR_CELL.get());
    }

    private ItemTints() {
    }
}
