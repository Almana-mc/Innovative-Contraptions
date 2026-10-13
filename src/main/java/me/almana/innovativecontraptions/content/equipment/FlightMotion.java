package me.almana.innovativecontraptions.content.equipment;

import me.almana.innovativecontraptions.content.energy.BatteryItem;
import me.almana.innovativecontraptions.registry.ICItems;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public final class FlightMotion {
    public static boolean isFlightPack(ItemStack stack) {
        return stack.is(ICItems.JETPACK.get()) || stack.is(ICItems.ELECTRIC_JETPACK.get())
                || stack.is(ICItems.QUANTUM_CHESTPLATE.get());
    }
    public static void tick(Player player) {
        ItemStack stack = player.getItemBySlot(EquipmentSlot.CHEST);
        if (!isFlightPack(stack)) return;
        boolean jump = EquipmentControls.held(player, EquipmentControls.JUMP);
        if (jump && EquipmentControls.mode(player) && EquipmentControls.toggleReady(player)) {
            boolean hover = !stack.getOrDefault(EquipmentData.HOVER.get(), false);
            stack.set(EquipmentData.HOVER.get(), hover);
            EquipmentControls.message(player, hover ? "hover_on" : "hover_off");
        }
        boolean hover = stack.getOrDefault(EquipmentData.HOVER.get(), false);
        if (player.onGround() && !jump && hover) {
            stack.set(EquipmentData.HOVER.get(), false);
            hover = false;
        }
        if (player.isPassenger() || player.getAbilities().flying || (!jump && !hover)) return;
        boolean fuel = stack.getItem() instanceof FuelFlightItem;
        int energy = fuel ? FuelFlightItem.fuel(stack) : BatteryItem.stored(stack);
        int cost = fuel ? hover ? 1 : 2 : hover ? 7 : 8;
        if (energy < cost) return;
        if (!player.onGround()) {
            if (fuel) stack.set(EquipmentData.FUEL.get(), energy - cost);
            else BatteryItem.extract(stack, cost);
        }
        applyThrust(player, stack, fuel, hover, energy, jump);
    }
    private static void applyThrust(Player player, ItemStack stack, boolean fuel, boolean hover, int energy, boolean jump) {
        boolean advanced = stack.is(ICItems.QUANTUM_CHESTPLATE.get());
        double power = fuel || advanced ? 1 : 0.7;
        int capacity = fuel ? FuelFlightItem.CAPACITY : ((BatteryItem) stack.getItem()).capacity();
        power *= Math.min(1, energy / (capacity * (fuel ? 0.2 : 0.05)));
        double minimum = player.level().getMinY();
        double ceiling = minimum + player.level().getHeight() / (fuel ? 1 : advanced ? 0.9 : 1.28);
        power *= Math.clamp((ceiling - player.getY()) / 25, 0, 1);
        Vec3 velocity = player.getDeltaMovement();
        double vertical = Math.min(0.6, velocity.y + power * 0.2);
        if (hover) {
            double limit = fuel ? 0.2 : 0.1;
            vertical = jump ? limit : EquipmentControls.held(player, EquipmentControls.SNEAK) ? -limit : 0;
        }
        Vec3 thrust = Vec3.ZERO;
        if (EquipmentControls.held(player, EquipmentControls.FORWARD)) {
            thrust = new Vec3(-Math.sin(Math.toRadians(player.getYRot())), 0, Math.cos(Math.toRadians(player.getYRot()))).scale(power * 0.08);
        }
        player.setDeltaMovement(velocity.x + thrust.x, vertical, velocity.z + thrust.z);
        player.fallDistance = 0;
        player.hurtMarked = true;
    }
}
