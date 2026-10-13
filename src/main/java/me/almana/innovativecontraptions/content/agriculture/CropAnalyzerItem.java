package me.almana.innovativecontraptions.content.agriculture;

import me.almana.innovativecontraptions.content.energy.BatteryItem;
import me.almana.innovativecontraptions.content.energy.EuTier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
//? if <26.1
//import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
//? if <26.1
//import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class CropAnalyzerItem extends BatteryItem {
    public CropAnalyzerItem(Properties properties) { super(properties, 100_000, EuTier.MV, true, 128, false); }
    //? if >=26.1 {
    @Override public InteractionResult use(Level level, Player player, InteractionHand hand) {
    //?} else {
    /*@Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return new InteractionResultHolder<>(useResult(level, player, hand), player.getItemInHand(hand));
    }

    private InteractionResult useResult(Level level, Player player, InteractionHand hand) {
    *///?}
        if (player instanceof ServerPlayer serverPlayer) serverPlayer.openMenu(new SimpleMenuProvider(
                (id, inventory, user) -> new AnalyzerMenu(id, inventory, hand), player.getItemInHand(hand).getHoverName()), buffer -> buffer.writeEnum(hand));
        return InteractionResult.SUCCESS;
    }
}
