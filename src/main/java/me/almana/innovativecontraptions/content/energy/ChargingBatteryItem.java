package me.almana.innovativecontraptions.content.energy;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
//? if <26.1
//import net.minecraft.world.level.Level;

public final class ChargingBatteryItem extends BatteryItem {
    public ChargingBatteryItem(Properties properties, int capacity, EuTier tier) {
        super(properties, capacity, tier, true);
    }

    @Override
    //? if >=26.1 {
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity owner, EquipmentSlot slot) {
    //?} else {
    /*public void inventoryTick(ItemStack stack, Level level, Entity owner, int slot, boolean selected) {
        if (level.isClientSide()) return;
    *///?}
        if (!(owner instanceof Player player)) return;
        int budget = Math.min(stored(stack), tier().volts);
        for (int index = 0; index < 9 && budget > 0; index++) {
            ItemStack target = player.getInventory().getItem(index);
            if (target == stack || !(target.getItem() instanceof BatteryItem battery)
                    || battery instanceof ChargingBatteryItem || battery.tier().volts > tier().volts) continue;
            int accepted = receiveEnergy(target, budget, tier().ordinal() + 1, false);
            extract(stack, accepted);
            budget -= accepted;
            if (accepted > 0) player.getInventory().setChanged();
        }
    }
}
