package me.almana.innovativecontraptions.content.agriculture;

import me.almana.innovativecontraptions.registry.ICBlocks;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.UseOnContext;

public final class BarrelItem extends BlockItem {
    public BarrelItem(Properties properties) { super(ICBlocks.FERMENTING_BARREL.get(), properties); }
    @Override public InteractionResult useOn(UseOnContext context) {
        var level = context.getLevel(); var pos = context.getClickedPos();
        if (!level.getBlockState(pos).is(ICBlocks.WOODEN_SCAFFOLD.get())) return InteractionResult.FAIL;
        if (!level.isClientSide()) {
            level.setBlockAndUpdate(pos, ICBlocks.FERMENTING_BARREL.get().defaultBlockState());
            context.getItemInHand().consume(1, context.getPlayer());
        }
        return InteractionResult.SUCCESS;
    }
}
