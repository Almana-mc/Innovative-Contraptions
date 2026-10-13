package me.almana.innovativecontraptions.content.gathering;

import java.util.LinkedHashMap;
import java.util.Map;
import me.almana.innovativecontraptions.content.energy.BatteryItem;
import me.almana.innovativecontraptions.content.energy.EuTier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
//? if <26.1
//import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.Tags;

public final class OreProbeItem extends BatteryItem {
    public final boolean deep;
    public OreProbeItem(Properties properties, boolean deep) {
        super(properties, deep ? 1_000_000 : 100_000, deep ? EuTier.MV : EuTier.LV, true, deep ? 512 : 128, false);
        this.deep = deep;
    }
    public int radius() { return deep ? 12 : 6; }
    public int scanCost() { return deep ? 250 : 50; }
    public int minerRadius() { return radius() / 2; }
    public static boolean ore(BlockState state) { return state.is(Tags.Blocks.ORES); }
    public Map<Item, Integer> scan(Level level, BlockPos center) {
        Map<Item, Integer> result = new LinkedHashMap<>();
        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-radius(), -radius(), -radius()), center.offset(radius(), radius(), radius()))) {
            if (level.isOutsideBuildHeight(pos) || !level.hasChunkAt(pos)) continue;
            BlockState state = level.getBlockState(pos);
            if (ore(state)) result.merge(state.getBlock().asItem(), 1, Integer::sum);
        }
        return result;
    }
    //? if >=26.1 {
    @Override public InteractionResult use(Level level, Player player, InteractionHand hand) {
    //?} else {
    /*@Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return new InteractionResultHolder<>(useResult(level, player, hand), player.getItemInHand(hand));
    }

    private InteractionResult useResult(Level level, Player player, InteractionHand hand) {
    *///?}
        var stack = player.getItemInHand(hand);
        if (stored(stack) < scanCost()) return InteractionResult.FAIL;
        if (!level.isClientSide()) {
            extract(stack, scanCost());
            var result = scan(level, player.blockPosition());
            player.sendSystemMessage(Component.translatable("message.innovativecontraptions.ore_probe", result.values().stream().mapToInt(Integer::intValue).sum(), radius()));
            result.forEach((item, count) -> player.sendSystemMessage(Component.literal(count + " × ").append(new ItemStack(item).getHoverName())));
        }
        return InteractionResult.SUCCESS;
    }
}

