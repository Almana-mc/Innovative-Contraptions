package me.almana.innovativecontraptions.content.equipment;

import me.almana.innovativecontraptions.content.energy.BatteryItem;
import me.almana.innovativecontraptions.registry.ICItems;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public final class AdvancedMotion {
    public static void tick(Player player) {
        ItemStack legs = player.getItemBySlot(EquipmentSlot.LEGS);
        var state = EquipmentControls.state(player);
        boolean jump = EquipmentControls.held(player, EquipmentControls.JUMP);
        if (legs.is(ICItems.QUANTUM_LEGGINGS.get()) && player.isSprinting()
                && EquipmentControls.held(player, EquipmentControls.FORWARD) && (player.onGround() || player.isInWater())
                && BatteryItem.stored(legs) >= 1_000) {
            if (player.tickCount % 10 == 0) BatteryItem.extract(legs, 1_000);
            double speed = player.isInWater() ? 0.1 : 0.22;
            double angle = Math.toRadians(player.getYRot());
            player.setDeltaMovement(player.getDeltaMovement().add(-Math.sin(angle) * speed,
                    player.isInWater() && jump ? 0.1 : 0, Math.cos(angle) * speed));
            player.hurtMarked = true;
        }
        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
        if (!boots.is(ICItems.QUANTUM_BOOTS.get())) { state.jumpImpulse = 0; state.wasJumping = jump; return; }
        if (jump && !state.wasJumping && player.onGround() && EquipmentControls.held(player, EquipmentControls.BOOST)
                && PoweredUse.spend(boots, player, 4_000)) {
            Vec3 velocity = player.getDeltaMovement();
            player.setDeltaMovement(velocity.x * 3.5, Math.max(0.42, velocity.y), velocity.z * 3.5);
            state.jumpImpulse = 0.3;
        }
        if (state.jumpImpulse > 0.01 && jump) {
            player.setDeltaMovement(player.getDeltaMovement().add(0, state.jumpImpulse, 0));
            state.jumpImpulse *= 0.75;
            player.hurtMarked = true;
        } else state.jumpImpulse = 0;
        state.wasJumping = jump;
    }
}
