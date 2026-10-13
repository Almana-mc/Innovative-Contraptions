package me.almana.innovativecontraptions.content.equipment;

import me.almana.innovativecontraptions.content.energy.BatteryItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public final class PoweredUse {
    public static void recharge(ItemStack target, LivingEntity owner) {
        if (owner.level().isClientSide()) return;
        if (!(target.getItem() instanceof BatteryItem battery)) return;
        ItemStack source = owner.getItemBySlot(EquipmentSlot.CHEST);
        if (source == target || !(source.getItem() instanceof BatteryItem supplier)
                || !supplier.canSupplyEnergy() || supplier.tier().ordinal() < battery.tier().ordinal()) return;
        int amount = Math.min(BatteryItem.stored(source), battery.capacity() - BatteryItem.stored(target));
        int accepted = BatteryItem.receiveEnergy(target, amount, supplier.tier().ordinal() + 1, true);
        BatteryItem.extract(source, accepted);
    }
    public static boolean available(ItemStack stack, LivingEntity owner, int cost) {
        recharge(stack, owner);
        return BatteryItem.stored(stack) >= cost;
    }
    public static boolean spend(ItemStack stack, LivingEntity owner, int cost) {
        if (owner.level().isClientSide()) return BatteryItem.stored(stack) >= cost;
        if (!available(stack, owner, cost)) return false;
        BatteryItem.extract(stack, cost);
        recharge(stack, owner);
        return true;
    }
}
