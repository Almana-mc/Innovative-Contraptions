package me.almana.innovativecontraptions.content.agriculture;

import me.almana.innovativecontraptions.registry.ICItems;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
//? if <26.1
//import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;

public final class CoffeeMugItem extends Item {
    private final int strength;
    public CoffeeMugItem(Properties properties, int strength) { super(properties.stacksTo(1)); this.strength = strength; }
    //? if >=26.1 {
    @Override public InteractionResult use(Level level, Player player, InteractionHand hand) { player.startUsingItem(hand); return InteractionResult.CONSUME; }
    //?} else
    //@Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) { player.startUsingItem(hand); return InteractionResultHolder.consume(player.getItemInHand(hand)); }
    @Override public int getUseDuration(ItemStack stack, LivingEntity entity) { return 32; }
    @Override public ItemUseAnimation getUseAnimation(ItemStack stack) { return ItemUseAnimation.DRINK; }
    @Override public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!level.isClientSide()) {
            int dose = Math.max(caffeinate(entity, MobEffects.SPEED), caffeinate(entity, MobEffects.HASTE));
            if (strength == 6) dose -= 2;
            if (dose >= 3) entity.addEffect(new MobEffectInstance(MobEffects.NAUSEA, (dose - 2) * 200));
            //? if >=26.1 {
            if (dose >= 4) MobEffects.INSTANT_DAMAGE.value().applyInstantenousEffect((ServerLevel) level, null, null, entity, dose - 3, 1);
            //?} else
            //if (dose >= 4) MobEffects.INSTANT_DAMAGE.value().applyInstantenousEffect(null, null, entity, dose - 3, 1);
        }
        return entity instanceof Player player && player.hasInfiniteMaterials() ? stack : new ItemStack(ICItems.MUG.get());
    }
    private int caffeinate(LivingEntity entity, Holder<MobEffect> effect) {
        var current = entity.getEffect(effect);
        int amplifier = current == null ? 0 : Math.max(current.getAmplifier(), Math.min(strength, current.getAmplifier() + 1));
        int duration = current == null ? 300 : current.getDuration() + (strength == 1 ? 600 : 1200);
        entity.addEffect(new MobEffectInstance(effect, duration, amplifier));
        return amplifier;
    }
}
