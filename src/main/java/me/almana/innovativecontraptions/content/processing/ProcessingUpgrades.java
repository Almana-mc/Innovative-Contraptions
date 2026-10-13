package me.almana.innovativecontraptions.content.processing;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import me.almana.innovativecontraptions.registry.ICItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public final class ProcessingUpgrades {
    public static final double TIME = 0.7, POWER = 1.6;
    public static boolean valid(ItemStack stack) {
        return stack.is(ICItems.OVERCLOCKER_UPGRADE.get()) || stack.is(ICItems.TRANSFORMER_UPGRADE.get())
                || stack.is(ICItems.ENERGY_STORAGE_UPGRADE.get()) || stack.is(ICItems.PULLING_UPGRADE.get())
                || stack.is(ICItems.EJECTOR_UPGRADE.get()) || stack.is(ICItems.REDSTONE_INVERTER_UPGRADE.get())
                || stack.is(ICItems.FLUID_PULLING_UPGRADE.get()) || stack.is(ICItems.FLUID_EJECTOR_UPGRADE.get())
                || stack.is(ICItems.ADVANCED_PULLING_UPGRADE.get()) || stack.is(ICItems.ADVANCED_EJECTOR_UPGRADE.get());
    }
    public static int count(Container machine, int start, ItemLike item) {
        if (start < 0) return 0;
        int count = 0;
        for (int slot = start; slot < Math.min(start + 4, machine.getContainerSize()); slot++) {
            ItemStack stack = machine.getItem(slot);
            if (stack.is(item.asItem())) count += stack.getCount();
        }
        return count;
    }
    public static int ticks(int ticks, int overclockers) {
        return Math.max(1, (int) Math.round(ticks * Math.pow(TIME, overclockers) * batch(ticks, overclockers)));
    }
    public static int batch(int ticks, int overclockers) {
        return (int) Math.min(Integer.MAX_VALUE, Math.ceil(1 / (ticks * Math.pow(TIME, overclockers))));
    }
    public static int energy(int eu, int overclockers) {
        return (int) Math.min(Integer.MAX_VALUE, Math.round(eu * Math.pow(POWER, overclockers)));
    }
    public static List<Component> describe(ItemStack stack) {
        int count = stack.getCount();
        String key = "tooltip.innovativecontraptions.upgrade.";
        if (stack.is(ICItems.OVERCLOCKER_UPGRADE.get())) return List.of(
                Component.translatable(key + "time", percent(Math.pow(TIME, count))),
                Component.translatable(key + "power", percent(Math.pow(POWER, count))));
        if (stack.is(ICItems.TRANSFORMER_UPGRADE.get())) return List.of(Component.translatable(key + "tier", count));
        if (stack.is(ICItems.ENERGY_STORAGE_UPGRADE.get())) return List.of(Component.translatable(key + "storage", 10000 * count));
        return List.of(Component.translatable(key + "redstone"));
    }
    private static String percent(double factor) {
        return new DecimalFormat("0.##", DecimalFormatSymbols.getInstance(Locale.ROOT)).format(100 * factor);
    }
    private ProcessingUpgrades() { }
}
