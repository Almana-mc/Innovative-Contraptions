package me.almana.innovativecontraptions.compat;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

public final class ContainerHelper extends net.minecraft.world.ContainerHelper {
    public static void saveAllItems(ValueOutput output, NonNullList<ItemStack> items) {
        saveAllItems(output.tag(), items, output.registries());
    }

    public static void loadAllItems(ValueInput input, NonNullList<ItemStack> items) {
        loadAllItems(input.tag(), items, input.registries());
    }
}
