package me.almana.innovativecontraptions.content.equipment;

import me.almana.innovativecontraptions.content.energy.EuTier;
import me.almana.innovativecontraptions.registry.ICDataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
//? if <26.1
//import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
//? if >=26.1
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.IShearable;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

public final class ChainsawItem extends PoweredMiningItem {
    public ChainsawItem(Properties props) {
        super(props, 30_000, EuTier.LV, 100, 100, 12, false, false);
    }
    @Override
    public float getAttackDamageBonus(Entity victim, float damage,
                                      DamageSource source) {
        return source.getEntity() instanceof LivingEntity attacker
                && PoweredUse.available(attacker.getMainHandItem(), attacker, 100) ? 9 : 0;
    }
    @Override
    //? if >=26.1 {
    public boolean canPerformAction(ItemInstance stack, ItemAbility ability) {
    //?} else {
    /*public boolean canPerformAction(ItemStack stack, ItemAbility ability) {
    *///?}
        return stack.getOrDefault(ICDataComponents.ENERGY.get(), 0) >= 100
                && !stack.getOrDefault(EquipmentData.ACTIVE.get(), false) && ItemAbilities.DEFAULT_SHEARS_ACTIONS.contains(ability);
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
        if (!EquipmentControls.mode(player)) return InteractionResult.PASS;
        if (!level.isClientSide()) {
            ItemStack stack = player.getItemInHand(hand);
            stack.set(EquipmentData.ACTIVE.get(), !stack.getOrDefault(EquipmentData.ACTIVE.get(), false));
            EquipmentControls.message(player, stack.getOrDefault(EquipmentData.ACTIVE.get(), false) ? "shearing_off" : "shearing_on");
        }
        return InteractionResult.SUCCESS;
    }
    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (stack.getOrDefault(EquipmentData.ACTIVE.get(), false) || !(target instanceof IShearable shearable)
                || !shearable.isShearable(player, stack, player.level(), target.blockPosition())) return InteractionResult.PASS;
        if (player.level() instanceof ServerLevel level && PoweredUse.spend(stack, player, 100)) {
            for (ItemStack drop : shearable.onSheared(player, stack, level, target.blockPosition())) {
                shearable.spawnShearedDrop(level, target.blockPosition(), drop);
            }
            return InteractionResult.SUCCESS;
        }
        return player.level().isClientSide() ? InteractionResult.SUCCESS : InteractionResult.FAIL;
    }
}
