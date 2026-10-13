package me.almana.innovativecontraptions.content.energy.storage;

import me.almana.innovativecontraptions.content.energy.BatteryItem;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

public final class ChargePad {
    public static boolean charge(ChargeStorageBlockEntity pad) {
        var pos = pad.getBlockPos();
        Player player = pad.chargingPlayer();
        AABB surface = new AABB(pos.getX(), pos.getY() + 0.92, pos.getZ(), pos.getX() + 1, pos.getY() + 1.02, pos.getZ() + 1);
        if (player == null || !player.getBoundingBox().intersects(surface) || pad.storedEu() == 0) return false;
        chargePlayer(pad, player);
        if (pad.getLevel() instanceof ServerLevel server) {
            server.sendParticles(ParticleTypes.ELECTRIC_SPARK, pos.getX() + 0.5, pos.getY() + 1.02,
                    pos.getZ() + 0.5, 2, 0.35, 0.05, 0.35, 0);
        }
        return true;
    }

    public static void chargePlayer(ChargeStorageBlockEntity pad, Player player) {
        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD}) {
            chargeItem(pad, player.getItemBySlot(slot));
        }
        for (int slot = 0; slot < 36; slot++) chargeItem(pad, player.getInventory().getItem(slot));
        chargeItem(pad, player.getOffhandItem());
        player.getInventory().setChanged();
    }

    private static void chargeItem(ChargeStorageBlockEntity pad, ItemStack stack) {
        int charged = BatteryItem.receiveEnergy(stack, Math.min(pad.storedEu(), pad.kind.packet() * 2), pad.kind.tier, true);
        pad.extractEu(charged);
    }

    private ChargePad() {}
}
