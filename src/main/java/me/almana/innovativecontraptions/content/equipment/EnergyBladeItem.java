package me.almana.innovativecontraptions.content.equipment;

import me.almana.innovativecontraptions.content.energy.BatteryItem;
import me.almana.innovativecontraptions.content.energy.EuTier;
//? if >=26.1
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
//? if <26.1
//import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class EnergyBladeItem extends BatteryItem {
    public EnergyBladeItem(Properties props) { super(props, 160_000, EuTier.HV, true, 500, false); }
    public static boolean active(ItemStack stack) { return stack.getOrDefault(EquipmentData.ACTIVE.get(), false); }
    public static float attackDamage(ItemStack stack) { return active(stack) && stored(stack) >= 400 ? 21 : 5; }
    @Override
    public float getAttackDamageBonus(Entity victim, float damage, DamageSource source) {
        if (!(source.getEntity() instanceof LivingEntity attacker)) return 4;
        ItemStack stack = attacker.getMainHandItem();
        PoweredUse.recharge(stack, attacker);
        return attackDamage(stack) - 1;
    }
    @Override
    //? if >=26.1 {
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
    //?} else {
    /*public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return new InteractionResultHolder<>(useResult(level, player, hand), player.getItemInHand(hand));
    }

    private InteractionResult useResult(Level level, Player player, InteractionHand hand) {
    *///?}
        if (!level.isClientSide()) {
            ItemStack stack = player.getItemInHand(hand);
            boolean enabled = !active(stack) && PoweredUse.available(stack, player, 16);
            stack.set(EquipmentData.ACTIVE.get(), enabled);
            EquipmentControls.message(player, enabled ? "blade_on" : "blade_off");
        }
        return InteractionResult.SUCCESS;
    }
    @Override
    //? if >=26.1 {
    public void hurtEnemy(ItemStack stack, LivingEntity victim, LivingEntity attacker) {
    //?} else {
    /*public boolean hurtEnemy(ItemStack stack, LivingEntity victim, LivingEntity attacker) {
        drain(stack, victim, attacker);
        return false;
    }

    private void drain(ItemStack stack, LivingEntity victim, LivingEntity attacker) {
    *///?}
        if (!active(stack) || !PoweredUse.spend(stack, attacker, 400)) return;
        for (EquipmentSlot slot : ArmorProtection.SLOTS) {
            ItemStack armor = victim.getItemBySlot(slot);
            if (armor.getItem() instanceof PoweredArmorItem item && !item.advanced()
                    && PoweredUse.spend(stack, attacker, 2_000)) {
                extract(armor, 48_000);
                if (stored(armor) == 0) victim.setItemSlot(slot, ItemStack.EMPTY);
            }
        }
    }
    @Override
    //? if >=26.1 {
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity owner, EquipmentSlot slot) {
    //?} else {
    /*public void inventoryTick(ItemStack stack, Level level, Entity owner, int slot, boolean selected) {
        if (level.isClientSide()) return;
    *///?}
        if (!(owner instanceof Player player) || !active(stack)) return;
        boolean hotbar = false;
        for (int i = 0; i < 9; i++) if (player.getInventory().getItem(i) == stack) hotbar = true;
        int period = hotbar ? 16 : 64;
        if (level.getGameTime() % period == 0 && !PoweredUse.spend(stack, player, hotbar ? 64 : 16)) {
            stack.set(EquipmentData.ACTIVE.get(), false);
        }
    }
    @Override
    public boolean isFoil(ItemStack stack) { return active(stack); }
}
