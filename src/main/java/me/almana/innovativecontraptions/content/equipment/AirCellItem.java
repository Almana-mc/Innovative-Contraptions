package me.almana.innovativecontraptions.content.equipment;

import me.almana.innovativecontraptions.registry.ICItems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class AirCellItem extends Item {
    public AirCellItem(Properties props) { super(props); }
    public static void breathe(Player player) {
        if (player.getAirSupply() > 100) return;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.is(ICItems.AIR_CELL.get())) continue;
            int remaining = stack.getOrDefault(EquipmentData.AIR.get(), 1000);
            int drained = Math.min(remaining, (int) Math.ceil((300 - player.getAirSupply()) * 1000 / 150.0));
            if (drained < 7) continue;
            ItemStack remainder = drained == remaining ? new ItemStack(ICItems.EMPTY_CELL.get()) : stack.copyWithCount(1);
            if (drained < remaining) remainder.set(EquipmentData.AIR.get(), remaining - drained);
            if (stack.getCount() == 1) player.getInventory().setItem(i, remainder);
            else {
                stack.shrink(1);
                if (!player.getInventory().add(remainder)) player.drop(remainder, false);
            }
            player.setAirSupply(Math.min(300, player.getAirSupply() + drained * 150 / 1000));
            if (player.getAirSupply() >= 300) return;
            if (player.getInventory().getItem(i).is(ICItems.AIR_CELL.get())) i--;
        }
    }
}
