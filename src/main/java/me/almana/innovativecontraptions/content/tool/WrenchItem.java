package me.almana.innovativecontraptions.content.tool;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
//? if <26.1
//import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;

public class WrenchItem extends Item implements WrenchTool {
    public WrenchItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return WrenchTool.super.useOn(context);
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        return state.getBlock() instanceof Wrenchable || super.isCorrectToolForDrops(stack, state);
    }

    @Override
    public boolean payRotateCost(ItemStack stack, Player player, InteractionHand hand) {
        //? if >=26.1 {
        stack.hurtAndBreak(WrenchCost.MECH_ROTATE_DAMAGE, player, hand);
        //?} else
        //stack.hurtAndBreak(WrenchCost.MECH_ROTATE_DAMAGE, player, LivingEntity.getSlotForHand(hand));
        return true;
    }

    @Override
    public boolean payPickupCost(ItemStack stack, Player player, InteractionHand hand) {
        //? if >=26.1 {
        stack.hurtAndBreak(WrenchCost.MECH_PICKUP_DAMAGE, player, hand);
        //?} else
        //stack.hurtAndBreak(WrenchCost.MECH_PICKUP_DAMAGE, player, LivingEntity.getSlotForHand(hand));
        return true;
    }
}
