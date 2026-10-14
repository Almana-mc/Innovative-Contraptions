package me.almana.innovativecontraptions.content.equipment;

import me.almana.innovativecontraptions.registry.ICItems;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LightLayer;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public final class WornEquipmentTick {
    public static void tick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;
        ItemStack head = player.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        selectHud(player, head);
        if (head.is(ICItems.HAZMAT_HELMET.get()) && ArmorProtection.fullHazardProtection(player)) {
            player.clearFire();
            if (player.isInLava()) player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 20));
        }
        if (head.is(ICItems.HAZMAT_HELMET.get())) AirCellItem.breathe(player);
        if (head.is(ICItems.NIGHTVISION_GOGGLES.get()) || head.getItem() instanceof PoweredArmorItem) nightVision(player, head);
        if (head.is(ICItems.QUANTUM_HELMET.get())) advancedHelmet(player, head);
        if (chest.is(ICItems.QUANTUM_CHESTPLATE.get())) player.clearFire();
        WearableGeneration.tick(player);
        AdvancedMotion.tick(player);
        FlightMotion.tick(player);
    }
    private static void selectHud(Player player, ItemStack head) {
        if (!(head.getItem() instanceof PoweredArmorItem) || !EquipmentControls.mode(player)
                || !EquipmentControls.held(player, EquipmentControls.SNEAK)
                || EquipmentControls.held(player, EquipmentControls.JUMP | EquipmentControls.ALT)
                || !EquipmentControls.toggleReady(player)) return;
        int mode = (head.getOrDefault(EquipmentData.MODE.get(), 1) + 1) % 3;
        head.set(EquipmentData.MODE.get(), mode);
        EquipmentControls.message(player, "hud_" + mode);
    }
    private static void nightVision(Player player, ItemStack stack) {
        if (EquipmentControls.mode(player) && EquipmentControls.held(player, EquipmentControls.ALT)
                && EquipmentControls.toggleReady(player)) {
            boolean active = !stack.getOrDefault(EquipmentData.ACTIVE.get(), false);
            stack.set(EquipmentData.ACTIVE.get(), active);
            EquipmentControls.message(player, active ? "vision_on" : "vision_off");
        }
        if (!stack.getOrDefault(EquipmentData.ACTIVE.get(), false) || !PoweredUse.spend(stack, player, 1)) return;
        boolean bright = player.level().getMaxLocalRawBrightness(player.blockPosition()) > 8;
        player.removeEffect(bright ? MobEffects.NIGHT_VISION : MobEffects.BLINDNESS);
        player.addEffect(new MobEffectInstance(bright ? MobEffects.BLINDNESS : MobEffects.NIGHT_VISION, bright ? 100 : 300, 0, false, false));
    }
    private static void advancedHelmet(Player player, ItemStack stack) {
        if (player.getAirSupply() < 100 && PoweredUse.spend(stack, player, 1_000)) {
            player.setAirSupply(Math.min(player.getMaxAirSupply(), player.getAirSupply() + 200));
        }
        if (player.getFoodData().needsFood() && PoweredUse.available(stack, player, 1_000)) {
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack food = player.getInventory().getItem(i);
                if (!food.is(ICItems.FILLED_TIN_CAN.get())) continue;
                if (FilledFoodCanItem.feed(player, food)) PoweredUse.spend(stack, player, 1_000);
                break;
            }
        }
        cleanse(player, stack, MobEffects.POISON, 10_000);
        cleanse(player, stack, EquipmentContent.RADIATION, 10_000);
        cleanse(player, stack, MobEffects.WITHER, 25_000);
    }
    private static void cleanse(Player player, ItemStack stack, Holder<MobEffect> effect, int cost) {
        MobEffectInstance current = player.getEffect(effect);
        if (current != null && PoweredUse.spend(stack, player, cost * (current.getAmplifier() + 1))) player.removeEffect(effect);
    }
}
