package me.almana.innovativecontraptions.content.energy;

import me.almana.innovativecontraptions.registry.ICItems;
import me.almana.innovativecontraptions.registry.ICTags;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.Tags;

public final class DischargeFuel {
    public static int value(ItemStack stack) {
        if (stack.is(Tags.Items.DUSTS_REDSTONE)) return 800;
        if (stack.is(ICItems.SINGLE_USE_BATTERY.get())) return 1200;
        if (stack.is(ICTags.DUSTS_ENERGIUM)) return 16000;
        return 0;
    }
    public static int burn(ItemStack stack, int freeSpace, boolean allowRedstone) {
        if (freeSpace <= 0 || !allowRedstone && stack.is(Tags.Items.DUSTS_REDSTONE)) return 0;
        int energy = value(stack);
        if (energy > 0) stack.shrink(1);
        return energy;
    }
    private DischargeFuel() {}
}
