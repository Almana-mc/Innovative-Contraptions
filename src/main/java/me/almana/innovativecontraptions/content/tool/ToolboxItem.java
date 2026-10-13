package me.almana.innovativecontraptions.content.tool;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
//? if <26.1
//import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
//? if <26.1
//import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class ToolboxItem extends Item {
    public ToolboxItem(Properties properties) { super(properties); }
    //? if >=26.1 {
    @Override public InteractionResult use(Level level, Player player, InteractionHand hand) {
    //?} else {
    /*@Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return new InteractionResultHolder<>(useResult(level, player, hand), player.getItemInHand(hand));
    }

    private InteractionResult useResult(Level level, Player player, InteractionHand hand) {
    *///?}
        if (player instanceof ServerPlayer serverPlayer) serverPlayer.openMenu(new SimpleMenuProvider(
                (id, inventory, user) -> new ToolboxMenu(id, inventory, hand), player.getItemInHand(hand).getHoverName()), buffer -> buffer.writeEnum(hand));
        return InteractionResult.SUCCESS;
    }
}
