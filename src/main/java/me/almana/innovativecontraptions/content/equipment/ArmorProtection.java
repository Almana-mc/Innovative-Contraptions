package me.almana.innovativecontraptions.content.equipment;

import java.util.Arrays;
import java.util.Comparator;
import me.almana.innovativecontraptions.content.energy.BatteryItem;
import me.almana.innovativecontraptions.registry.ICItems;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

public final class ArmorProtection {
    public static final EquipmentSlot[] SLOTS = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
    public static boolean fullHazardProtection(LivingEntity entity) {
        for (EquipmentSlot slot : SLOTS) {
            ItemStack stack = entity.getItemBySlot(slot);
            if (stack.getItem() instanceof HazardArmorItem) continue;
            if (stack.getItem() instanceof PoweredArmorItem armor && armor.advanced() && BatteryItem.stored(stack) > 0) continue;
            return false;
        }
        return true;
    }
    public static void incoming(LivingIncomingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        DamageSource source = event.getSource();
        if (entity.getItemBySlot(EquipmentSlot.HEAD).is(ICItems.HAZMAT_HELMET.get())
                && fullHazardProtection(entity) && environmental(source)) {
            event.setCanceled(true);
            return;
        }
        if (source.is(DamageTypeTags.IS_FALL)) cushionFall(event);
        if (source.is(DamageTypeTags.BYPASSES_ARMOR)) return;
        float damage = event.getAmount();
        ignoreDisplayedArmor(event);
        event.setAmount(normalProtection(entity, source, damage));
    }
    private static void cushionFall(LivingIncomingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        ItemStack boots = entity.getItemBySlot(EquipmentSlot.FEET);
        if (!boots.is(ICItems.RUBBER_BOOTS.get())) return;
        float damage = event.getAmount();
        float absorbed = damage < 8 ? damage : damage * 0.875F;
        boots.hurtAndBreak((int) Math.max(1, absorbed) * 2, entity, EquipmentSlot.FEET);
        if (absorbed >= damage) event.setCanceled(true);
        else event.setAmount(damage - absorbed);
    }
    private static void ignoreDisplayedArmor(LivingIncomingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        int count = 0;
        for (EquipmentSlot slot : SLOTS) {
            ItemStack stack = entity.getItemBySlot(slot);
            if (stack.getItem() instanceof HazardArmorItem) count++;
            else if (stack.getItem() instanceof PoweredArmorItem armor) count += armor.displayedArmor();
        }
        if (count == 0) return;
        int displayedArmor = count;
        event.addReductionModifier(DamageContainer.Reduction.ARMOR,
                (container, reduction) -> {
                    float damage = container.getNewDamage();
                    float armor = entity.getArmorValue();
                    float toughness = (float) entity.getAttributeValue(Attributes.ARMOR_TOUGHNESS);
                    float withDisplay = CombatRules.getDamageAfterAbsorb(entity, damage, event.getSource(), armor, toughness);
                    float withoutDisplay = CombatRules.getDamageAfterAbsorb(entity, damage, event.getSource(),
                            Math.max(0, armor - displayedArmor), toughness);
                    return Math.max(0, reduction + withDisplay - withoutDisplay);
                });
    }
    private static boolean environmental(DamageSource source) {
        return source.is(DamageTypeTags.IS_FIRE) || source.is(DamageTypes.IN_WALL)
                || source.is(DamageTypes.LIGHTNING_BOLT) || source.is(EquipmentContent.RADIATION_DAMAGE);
    }
    private static float normalProtection(LivingEntity entity, DamageSource source, float damage) {
        double[] shares = normalShares(entity, source, damage);
        float absorbed = 0;
        for (int index = 0; index < SLOTS.length; index++) {
            EquipmentSlot slot = SLOTS[index];
            ItemStack stack = entity.getItemBySlot(slot);
            float share = (float) (damage * shares[index]);
            if (share <= 0) continue;
            int callback = (int) Math.max(1, share);
            if (stack.getItem() instanceof PoweredArmorItem armor) {
                dischargeProtection(stack, share, armor.protectionCost());
                absorbed += share;
            } else if (stack.getItem() instanceof HazardArmorItem) {
                stack.hurtAndBreak(callback * 2, entity, slot);
                absorbed += share;
            }
        }
        return Math.max(0, damage - absorbed);
    }
    private static double[] normalShares(LivingEntity entity, DamageSource source, float damage) {
        double[] shares = new double[4];
        double[] caps = new double[4];
        Integer[] order = {0, 1, 2, 3};
        for (int i = 0; i < 4; i++) {
            ItemStack stack = entity.getItemBySlot(SLOTS[i]);
            shares[i] = weight(stack, source);
            caps[i] = stack.getItem() instanceof PoweredArmorItem armor ? electricCap(stack, armor.protectionCost())
                    : stack.getItem() instanceof HazardArmorItem ? ((stack.getMaxDamage() - stack.getDamageValue() + 2) / 2) * 25.0 : 0;
        }
        Arrays.sort(order, Comparator.comparingDouble(i -> shares[i] == 0 ? 0 : caps[i] / shares[i]));
        double total = Arrays.stream(shares).sum();
        for (int cursor = 0; cursor < order.length; cursor++) {
            int i = order[cursor];
            double ratio = shares[i] / Math.max(1, total);
            if (damage * ratio <= caps[i]) { shares[i] = ratio; continue; }
            shares[i] = damage > 0 ? caps[i] / damage : 0;
            total = Arrays.stream(shares).sum();
        }
        return shares;
    }
    private static int electricCap(ItemStack stack, int cost) {
        return (int) Math.min(Integer.MAX_VALUE, 25L * BatteryItem.stored(stack) / cost);
    }
    private static void dischargeProtection(ItemStack stack, float absorbed, int cost) {
        if (absorbed <= 0) return;
        long requested = (long) Math.max(1, absorbed) * cost;
        BatteryItem.extract(stack, (int) Math.min(BatteryItem.stored(stack), requested));
    }
    private static float weight(ItemStack stack, DamageSource source) {
        if (source.is(DamageTypeTags.IS_FALL)) return 0;
        if (stack.getItem() instanceof PoweredArmorItem armor) {
            return armor.advanced() || !source.is(DamageTypeTags.BYPASSES_ARMOR) ? armor.protectionWeight() : 0;
        }
        return stack.getItem() instanceof HazardArmorItem && !source.is(DamageTypeTags.BYPASSES_ARMOR) ? 0.05F : 0;
    }
    public static void landing(LivingFallEvent event) {
        ItemStack boots = event.getEntity().getItemBySlot(EquipmentSlot.FEET);
        if (boots.is(ICItems.NANO_BOOTS.get())) {
            int damage = Math.max(0, (int) Math.floor(event.getDistance()) - 3);
            if (damage < 8) {
                if (BatteryItem.stored(boots) >= damage * 5_000) {
                    BatteryItem.extract(boots, damage * 5_000);
                    event.setCanceled(true);
                }
                return;
            }
            float absorbed = Math.min(damage * 0.875F, BatteryItem.stored(boots) / 5_000F);
            BatteryItem.extract(boots, (int) Math.ceil(absorbed) * 5_000);
            event.setDamageMultiplier(event.getDamageMultiplier() * (damage - absorbed) / damage);
            return;
        }
        if (!boots.is(ICItems.QUANTUM_BOOTS.get())) return;
        int cost = (int) Math.min(Integer.MAX_VALUE, Math.max(0, Math.floor(event.getDistance()) - 10) * 20_000);
        if (BatteryItem.stored(boots) >= cost) {
            BatteryItem.extract(boots, cost);
            event.setCanceled(true);
        }
    }
}
