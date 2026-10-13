package me.almana.innovativecontraptions.content.equipment;

import java.util.ArrayList;
import java.util.List;
import me.almana.innovativecontraptions.content.energy.BatteryItem;
import me.almana.innovativecontraptions.registry.ICItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class WearableGeneration {
    public static void tick(Player player) {
        if (player.getItemBySlot(EquipmentSlot.HEAD).is(ICItems.SOLAR_HELMET.get())
                && player.level().dimensionType().hasSkyLight()
                && player.level().canSeeSky(player.blockPosition().above())) charge(player, 1);
        stride(player);
        supply(player);
    }
    private static void stride(Player player) {
        var state = EquipmentControls.state(player);
        if (!player.getItemBySlot(EquipmentSlot.FEET).is(ICItems.STATIC_BOOTS.get())) { state.anchor = null; return; }
        BlockPos position = player.blockPosition();
        if (state.anchor == null || player.isPassenger() || player.isSwimming() || !player.onGround()) { state.anchor = position; return; }
        double distance = Math.hypot(position.getX() - state.anchor.getX(), position.getZ() - state.anchor.getZ());
        if (distance >= 5) {
            charge(player, Math.min(3, (int) (distance / 5)));
            state.anchor = position;
        }
    }
    private static void charge(Player player, int amount) {
        for (ItemStack target : targets(player)) {
            amount -= BatteryItem.receiveEnergy(target, amount, Integer.MAX_VALUE, true);
            if (amount <= 0) return;
        }
    }
    private static void supply(Player player) {
        ItemStack pack = player.getItemBySlot(EquipmentSlot.CHEST);
        if (!(pack.getItem() instanceof WearableBatteryItem battery) || !battery.canSupplyEnergy()) return;
        int budget = Math.min(BatteryItem.stored(pack), battery.transferLimit());
        for (ItemStack target : targets(player)) {
            if (budget <= 0) return;
            if (!(target.getItem() instanceof BatteryItem item) || item.canSupplyEnergy()) continue;
            int accepted = BatteryItem.receiveEnergy(target, budget, battery.tier().ordinal() + 1, false);
            BatteryItem.extract(pack, accepted);
            budget -= accepted;
        }
    }
    private static List<ItemStack> targets(Player player) {
        List<ItemStack> stacks = new ArrayList<>();
        for (EquipmentSlot slot : ArmorProtection.SLOTS) stacks.add(player.getItemBySlot(slot));
        stacks.add(player.getOffhandItem());
        for (int slot = 0; slot < 36; slot++) stacks.add(player.getInventory().getItem(slot));
        return stacks;
    }
}
