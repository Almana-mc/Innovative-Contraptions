package me.almana.innovativecontraptions.content.equipment;

import me.almana.innovativecontraptions.registry.ICItems;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
//? if <26.1
//import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class FilledFoodCanItem extends Item {
    public FilledFoodCanItem(Properties props) { super(props); }
    @Override
    //? if >=26.1 {
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
    //?} else {
    /*public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return new InteractionResultHolder<>(useResult(level, player, hand), player.getItemInHand(hand));
    }

    private InteractionResult useResult(Level level, Player player, InteractionHand hand) {
    *///?}
        if (!player.getFoodData().needsFood()) return InteractionResult.FAIL;
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        return feed(player, player.getItemInHand(hand)) ? InteractionResult.SUCCESS : InteractionResult.FAIL;
    }
    public static boolean feed(Player player, ItemStack stack) {
        int amount = Math.min(stack.getCount(), 20 - player.getFoodData().getFoodLevel());
        if (amount <= 0 || !canReturn(player, amount)) return false;
        ItemStack empty = new ItemStack(ICItems.TIN_CAN.get(), amount);
        player.getInventory().add(empty);
        stack.shrink(amount);
        player.getFoodData().eat(amount, amount);
        return true;
    }
    private static boolean canReturn(Player player, int amount) {
        int space = 0;
        for (int i = 0; i < 36; i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item.isEmpty()) space += 64;
            else if (item.is(ICItems.TIN_CAN.get())) space += item.getMaxStackSize() - item.getCount();
            if (space >= amount) return true;
        }
        return false;
    }
}
