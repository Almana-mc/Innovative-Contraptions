package me.almana.innovativecontraptions.content.rubber;

import me.almana.innovativecontraptions.registry.ICItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class TreeTapItem extends Item {
    public TreeTapItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction face = context.getClickedFace();
        BlockState state = level.getBlockState(pos);

        if (!RubberLogBlock.hasUsableSap(state, face)) {
            return InteractionResult.PASS;
        }

        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        }

        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        SapStage stage = state.getValue(RubberLogBlock.SAP_STAGE);

        if (stage == SapStage.READY) {
            dropResin(serverLevel, pos, face, 1 + serverLevel.getRandom().nextInt(3));
            serverLevel.setBlock(pos, state.setValue(RubberLogBlock.SAP_STAGE, SapStage.EMPTY), Block.UPDATE_ALL);
            damageTap(stack, player, context);
            serverLevel.playSound(null, pos, SoundEvents.WOOD_HIT, SoundSource.BLOCKS, 0.7F, 1.2F);
            return InteractionResult.SUCCESS;
        }

        if (serverLevel.getRandom().nextInt(5) == 0) {
            dropResin(serverLevel, pos, face, 1);
        }
        if (serverLevel.getRandom().nextInt(5) == 0) {
            serverLevel.setBlock(pos, state.setValue(RubberLogBlock.SAP_STAGE, SapStage.NONE), Block.UPDATE_ALL);
        }
        damageTap(stack, player, context);
        serverLevel.playSound(null, pos, SoundEvents.WOOD_HIT, SoundSource.BLOCKS, 0.5F, 0.8F);
        return InteractionResult.SUCCESS;
    }

    private static void dropResin(ServerLevel level, BlockPos pos, Direction face, int amount) {
        for (int i = 0; i < amount; i++) {
            Block.popResourceFromFace(level, pos, face, new ItemStack(ICItems.STICKY_RESIN.get()));
        }
    }

    private static void damageTap(ItemStack stack, Player player, UseOnContext context) {
        if (player != null && !player.getAbilities().instabuild) {
            EquipmentSlot slot = context.getHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
            stack.hurtAndBreak(1, player, slot);
        }
    }
}
