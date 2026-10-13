package me.almana.innovativecontraptions.content.equipment;

import me.almana.innovativecontraptions.content.energy.BatteryItem;
import me.almana.innovativecontraptions.content.energy.EuTier;
import me.almana.innovativecontraptions.content.rubber.RubberLogBlock;
import me.almana.innovativecontraptions.content.rubber.SapStage;
import me.almana.innovativecontraptions.registry.ICDataComponents;
import me.almana.innovativecontraptions.registry.ICItems;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
//? if >=26.1
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

public final class PoweredFieldItem extends BatteryItem {
    private final boolean tap;
    public PoweredFieldItem(Properties props, boolean tap) {
        super(props, 10_000, EuTier.LV, true, 100, false);
        this.tap = tap;
    }
    @Override
    //? if >=26.1 {
    public boolean canPerformAction(ItemInstance stack, ItemAbility ability) {
    //?} else {
    /*public boolean canPerformAction(ItemStack stack, ItemAbility ability) {
    *///?}
        return !tap && ability == ItemAbilities.HOE_TILL
                && stack.getOrDefault(ICDataComponents.ENERGY.get(), 0) >= 50;
    }
    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getPlayer() == null) return InteractionResult.PASS;
        return tap ? tap(context) : till(context);
    }
    private InteractionResult till(UseOnContext context) {
        var level = context.getLevel();
        var pos = context.getClickedPos();
        var state = level.getBlockState(pos);
        PoweredUse.recharge(context.getItemInHand(), context.getPlayer());
        var result = state.getToolModifiedState(context, ItemAbilities.HOE_TILL, true);
        if (result == null) return InteractionResult.PASS;
        if (!state.is(Blocks.ROOTED_DIRT) && (context.getClickedFace() == Direction.DOWN || !level.isEmptyBlock(pos.above()))) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        if (!PoweredUse.spend(context.getItemInHand(), context.getPlayer(), 50)) return InteractionResult.FAIL;
        level.setBlock(pos, result, Block.UPDATE_ALL);
        if (state.is(Blocks.ROOTED_DIRT)) Block.popResource(level, pos, new ItemStack(Items.HANGING_ROOTS));
        level.playSound(null, pos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1, 1);
        return InteractionResult.SUCCESS;
    }
    private InteractionResult tap(UseOnContext context) {
        var level = context.getLevel();
        var pos = context.getClickedPos();
        var face = context.getClickedFace();
        var state = level.getBlockState(pos);
        if (!RubberLogBlock.hasUsableSap(state, face)) return InteractionResult.PASS;
        if (!(level instanceof ServerLevel server)) return InteractionResult.SUCCESS;
        if (!PoweredUse.available(context.getItemInHand(), context.getPlayer(), 50)) return InteractionResult.FAIL;
        boolean wet = state.getValue(RubberLogBlock.SAP_STAGE) == SapStage.READY;
        int drops = wet ? 1 + server.getRandom().nextInt(3) : server.getRandom().nextInt(5) == 0 ? 1 : 0;
        if (wet || server.getRandom().nextInt(5) == 0) {
            level.setBlock(pos, state.setValue(RubberLogBlock.SAP_STAGE, wet ? SapStage.EMPTY : SapStage.NONE), Block.UPDATE_ALL);
        }
        if (drops > 0) {
            PoweredUse.spend(context.getItemInHand(), context.getPlayer(), 50);
            Block.popResourceFromFace(level, pos, face, new ItemStack(ICItems.STICKY_RESIN.get(), drops));
        }
        level.playSound(null, pos, SoundEvents.WOOD_HIT, SoundSource.BLOCKS, 0.7F, 1.2F);
        return InteractionResult.SUCCESS;
    }
}
