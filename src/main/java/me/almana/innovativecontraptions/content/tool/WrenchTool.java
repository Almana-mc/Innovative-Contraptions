package me.almana.innovativecontraptions.content.tool;

import me.almana.innovativecontraptions.content.energy.storage.ElectricalBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public interface WrenchTool {
    boolean payRotateCost(ItemStack stack, Player player, InteractionHand hand);

    boolean payPickupCost(ItemStack stack, Player player, InteractionHand hand);

    default InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof Wrenchable wrenchable)) {
            return InteractionResult.PASS;
        }

        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }

        if (!(level instanceof ServerLevel server)) {
            return InteractionResult.SUCCESS;
        }

        ItemStack stack = context.getItemInHand();
        InteractionHand hand = context.getHand();
        Direction facing = wrenchable.getFacing(level, pos);
        Direction clickedFace = context.getClickedFace();

        boolean rotate = !player.isShiftKeyDown()
                && facing != null
                && (clickedFace.getAxis().isHorizontal() || state.getBlock() instanceof ElectricalBlock)
                && clickedFace != facing;

        if (rotate) {
            if (!payRotateCost(stack, player, hand)) {
                return InteractionResult.PASS;
            }
            if (!wrenchable.setFacing(level, pos, clickedFace, player)) {
                return InteractionResult.PASS;
            }
            return InteractionResult.SUCCESS;
        }

        boolean lost = MachineLoss.rolls(stack, server);
        if (!payPickupCost(stack, player, hand)) {
            return InteractionResult.PASS;
        }
        if (lost) {
            MachineLoss.destroy(server, pos);
            return InteractionResult.SUCCESS;
        }
        Block.dropResources(state, level, pos, level.getBlockEntity(pos), player, stack);
        level.removeBlock(pos, false);
        return InteractionResult.SUCCESS;
    }
}
