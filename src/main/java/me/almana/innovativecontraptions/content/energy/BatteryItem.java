package me.almana.innovativecontraptions.content.energy;

import me.almana.innovativecontraptions.registry.ICDataComponents;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
//? if >=26.1
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.network.chat.Component;
import java.util.function.Consumer;

public class BatteryItem extends Item {
    private final int capacity;
    private final EuTier tier;
    private final boolean rechargeable;
    private final int transferLimit;
    private final boolean canSupplyEnergy;

    public BatteryItem(Properties properties, int capacity, EuTier tier, boolean rechargeable) {
        this(properties, capacity, tier, rechargeable, tier == EuTier.LV ? 100 : tier.volts, true);
    }

    public BatteryItem(Properties properties, int capacity, EuTier tier, boolean rechargeable,
                       int transferLimit, boolean canSupplyEnergy) {
        super(properties.stacksTo(1).component(ICDataComponents.ENERGY.get(), 0));
        this.capacity = capacity;
        this.tier = tier;
        this.rechargeable = rechargeable;
        this.transferLimit = transferLimit;
        this.canSupplyEnergy = canSupplyEnergy;
    }

    public int capacity() { return capacity; }
    public EuTier tier() { return tier; }
    public boolean rechargeable() { return rechargeable; }
    public int transferLimit() { return transferLimit; }
    public boolean canSupplyEnergy() { return canSupplyEnergy; }

    public static int receiveEnergy(ItemStack stack, int amount, int chargingTier, boolean bypassTransferLimit) {
        if (!(stack.getItem() instanceof BatteryItem battery)
                || battery.tier.ordinal() + 1 > chargingTier) return 0;
        return receive(stack, bypassTransferLimit ? amount : Math.min(amount, battery.transferLimit));
    }

    public static int extractEnergy(ItemStack stack, int amount, int receivingTier, boolean bypassTransferLimit) {
        if (!(stack.getItem() instanceof BatteryItem battery) || !battery.canSupplyEnergy()
                || battery.tier.ordinal() + 1 > receivingTier) return 0;
        return extract(stack, bypassTransferLimit ? amount : Math.min(amount, battery.transferLimit));
    }

    @Override
    //? if >=26.1 {
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display,
                                Consumer<Component> tooltip, TooltipFlag flag) {
    //?} else {
    /*public void appendHoverText(ItemStack stack, TooltipContext context, java.util.List<Component> components, TooltipFlag flag) {
        Consumer<Component> tooltip = components::add;
    *///?}
        tooltip.accept(Component.translatable("tooltip.innovativecontraptions.battery_energy", stored(stack), capacity));
        if (this instanceof ChargingBatteryItem) {
            tooltip.accept(Component.translatable("tooltip.innovativecontraptions.automatic_charging"));
        }
    }

    public static int stored(ItemStack stack) {
        return stack.getOrDefault(ICDataComponents.ENERGY.get(), 0);
    }

    public static void setStored(ItemStack stack, int value) {
        int maximum = stack.getItem() instanceof BatteryItem battery ? battery.capacity : Integer.MAX_VALUE;
        stack.set(ICDataComponents.ENERGY.get(), Mth.clamp(value, 0, maximum));
    }

    public static int receive(ItemStack stack, int amount) {
        if (!(stack.getItem() instanceof BatteryItem battery) || !battery.rechargeable) return 0;
        int current = stored(stack);
        int accepted = Math.max(0, Math.min(amount, battery.capacity - current));
        if (accepted > 0) setStored(stack, current + accepted);
        return accepted;
    }

    public static int extract(ItemStack stack, int amount) {
        if (!(stack.getItem() instanceof BatteryItem)) {
            return 0;
        }
        int current = stored(stack);
        int taken = Math.max(0, Math.min(amount, current));
        if (taken > 0) {
            setStored(stack, current - taken);
        }
        return taken;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return stored(stack) > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(13f * stored(stack) / capacity);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return Mth.hsvToRgb(stored(stack) / (float) capacity / 3f, 1f, 1f);
    }
}
