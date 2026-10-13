package me.almana.innovativecontraptions.content.energy;

import me.almana.innovativecontraptions.registry.ICItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class DischargeFuel {
    public static int value(ItemStack stack) {
        if (stack.is(Items.REDSTONE)) return 800;
        if (stack.is(ICItems.SINGLE_USE_BATTERY.get())) return 1200;
        if (stack.is(ICItems.ENERGIUM_DUST.get())) return 16000;
        return 0;
    }
    public static int burn(ItemStack stack, int freeSpace, boolean allowRedstone) {
        if (freeSpace <= 0 || !allowRedstone && stack.is(Items.REDSTONE)) return 0;
        int energy = value(stack);
        if (energy > 0) stack.shrink(1);
        return energy;
    }
    private DischargeFuel() {}
}
