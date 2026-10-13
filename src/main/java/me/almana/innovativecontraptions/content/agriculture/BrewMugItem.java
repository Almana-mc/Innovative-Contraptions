package me.almana.innovativecontraptions.content.agriculture;

import java.util.function.Consumer;
import me.almana.innovativecontraptions.registry.ICItems;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.item.TooltipFlag;
//? if >=26.1
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

public final class BrewMugItem extends Item {
    public BrewMugItem(Properties properties) { super(properties.stacksTo(1)); }
    //? if >=26.1 {
    @Override public InteractionResult use(Level level, Player player, InteractionHand hand) { player.startUsingItem(hand); return InteractionResult.CONSUME; }
    //?} else
    //@Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) { player.startUsingItem(hand); return InteractionResultHolder.consume(player.getItemInHand(hand)); }
    @Override public int getUseDuration(ItemStack stack, LivingEntity entity) { return 32; }
    @Override public ItemUseAnimation getUseAnimation(ItemStack stack) { return ItemUseAnimation.DRINK; }
    @Override public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        var blend = stack.getOrDefault(FarmingContent.BLEND.get(), DrinkBlend.SPOILED);
        if (level instanceof ServerLevel server) {
            if (blend.maturity() == 5) spoiled(server, entity);
            else if (blend.rum()) rum(entity);
            else beer(server, entity, blend);
        }
        return entity instanceof Player player && player.hasInfiniteMaterials() ? stack : new ItemStack(ICItems.MUG.get());
    }
    private static void beer(ServerLevel level, LivingEntity entity, DrinkBlend blend) {
        var fatigue = entity.getEffect(MobEffects.MINING_FATIGUE);
        int previous = fatigue == null ? -1 : fatigue.getAmplifier();
        int maximum = (int) (blend.intensity() * blend.alcohol() * .5);
        amplify(entity, MobEffects.MINING_FATIGUE, blend.duration(), blend.intensity(), maximum);
        if (entity instanceof Player player) player.getFoodData().eat(6 - blend.alcohol(), blend.strength() * .15F);
        if (previous >= 0) amplify(entity, MobEffects.STRENGTH, blend.duration(), blend.intensity(), maximum);
        if (previous >= 1) amplify(entity, MobEffects.SLOWNESS, blend.duration(), blend.intensity(), maximum / 2);
        if (previous >= 2) amplify(entity, MobEffects.RESISTANCE, blend.duration(), blend.intensity(), maximum - 1);
        if (previous >= 3) amplify(entity, MobEffects.NAUSEA, blend.duration(), blend.intensity(), 0);
        if (previous >= 4) damage(level, entity, level.getRandom().nextInt(3));
    }
    private static void rum(LivingEntity entity) {
        var resistance = entity.getEffect(MobEffects.RESISTANCE);
        int previous = resistance == null ? -1 : resistance.getAmplifier();
        amplify(entity, MobEffects.FIRE_RESISTANCE, 600, 2, 0);
        amplify(entity, MobEffects.RESISTANCE, 600, 2, 2);
        if (previous >= 0) amplify(entity, MobEffects.BLINDNESS, 600, 2, 0);
        if (previous >= 1) amplify(entity, MobEffects.NAUSEA, 600, 2, 0);
    }
    private static void amplify(LivingEntity entity, Holder<MobEffect> effect, int duration, double intensity, int maximum) {
        var current = entity.getEffect(effect);
        int amplifier = 0;
        if (current != null) {
            int extra = Math.max(0, ((int) (duration * (1 + 2 * intensity)) - current.getDuration()) / 2);
            duration = current.getDuration() + Math.min(duration, extra);
            amplifier = current.getAmplifier() + (current.getAmplifier() < maximum ? 1 : 0);
        }
        entity.addEffect(new MobEffectInstance(effect, duration, amplifier));
    }
    private static void spoiled(ServerLevel level, LivingEntity entity) {
        switch (level.getRandom().nextInt(6)) {
            case 1 -> entity.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 1200));
            case 2 -> entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 2400));
            case 3 -> entity.addEffect(new MobEffectInstance(MobEffects.POISON, 2400));
            case 4 -> entity.addEffect(new MobEffectInstance(MobEffects.POISON, 200, 2));
            case 5 -> damage(level, entity, level.getRandom().nextInt(4));
        }
    }
    //? if >=26.1 {
    private static void damage(ServerLevel level, LivingEntity entity, int amplifier) { MobEffects.INSTANT_DAMAGE.value().applyInstantenousEffect(level, null, null, entity, amplifier, 1); }
    //?} else
    //private static void damage(ServerLevel level, LivingEntity entity, int amplifier) { MobEffects.INSTANT_DAMAGE.value().applyInstantenousEffect(null, null, entity, amplifier, 1); }
    //? if >=26.1 {
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> lines, TooltipFlag flag) {
    //?} else {
    /*@Override public void appendHoverText(ItemStack stack, TooltipContext context, java.util.List<Component> components, TooltipFlag flag) {
        Consumer<Component> lines = components::add;
    *///?}
        var blend = stack.getOrDefault(FarmingContent.BLEND.get(), DrinkBlend.SPOILED);
        lines.accept(Component.translatable("tooltip.innovativecontraptions.brew", blend.alcohol(), blend.strength(), blend.maturity()));
    }
}
