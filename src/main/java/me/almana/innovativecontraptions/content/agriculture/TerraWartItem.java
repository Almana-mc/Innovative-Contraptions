package me.almana.innovativecontraptions.content.agriculture;

import java.util.List;
import me.almana.innovativecontraptions.content.equipment.EquipmentContent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
//? if <26.1
//import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;

public final class TerraWartItem extends Item {
    public TerraWartItem(Properties properties) { super(properties); }
    //? if >=26.1 {
    @Override public InteractionResult use(Level level, Player player, InteractionHand hand) { player.startUsingItem(hand); return InteractionResult.CONSUME; }
    //?} else
    //@Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) { player.startUsingItem(hand); return InteractionResultHolder.consume(player.getItemInHand(hand)); }
    @Override public int getUseDuration(ItemStack stack, LivingEntity entity) { return 32; }
    @Override public ItemUseAnimation getUseAnimation(ItemStack stack) { return ItemUseAnimation.EAT; }
    @Override public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!level.isClientSide()) {
            for (var effect : List.of(MobEffects.NAUSEA, MobEffects.MINING_FATIGUE, MobEffects.HUNGER,
                    MobEffects.SLOWNESS, MobEffects.WEAKNESS, MobEffects.BLINDNESS, MobEffects.POISON, MobEffects.WITHER)) entity.removeEffect(effect);
            var radiation = entity.getEffect(EquipmentContent.RADIATION);
            if (radiation != null) {
                entity.removeEffect(EquipmentContent.RADIATION);
                if (radiation.getDuration() > 600) entity.addEffect(new MobEffectInstance(EquipmentContent.RADIATION,
                        radiation.getDuration() - 600, radiation.getAmplifier()));
            }
        }
        if (!(entity instanceof Player player) || !player.hasInfiniteMaterials()) stack.shrink(1);
        return stack;
    }
}
