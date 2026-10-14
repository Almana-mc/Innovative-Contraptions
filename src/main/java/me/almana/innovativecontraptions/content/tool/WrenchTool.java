package me.almana.innovativecontraptions.content.tool;

import me.almana.innovativecontraptions.content.equipment.EquipmentControls;
import me.almana.innovativecontraptions.registry.ICSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

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
        Direction target = facing == null ? null : target(player, state, facing, context.getClickedFace());

        if (target != null && target != facing && supports(state, target)) {
            if (!payRotateCost(stack, player, hand)) {
                return InteractionResult.PASS;
            }
            if (!wrenchable.setFacing(level, pos, target, player)) {
                return InteractionResult.PASS;
            }
            level.playSound(null, pos, ICSounds.WRENCH.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
            return InteractionResult.SUCCESS;
        }

        boolean lost = MachineLoss.rolls(stack, server, state);
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

    private static Direction target(Player player, BlockState state, Direction facing, Direction clicked) {
        if (!EquipmentControls.held(player, EquipmentControls.ALT)) return player.isShiftKeyDown() ? clicked.getOpposite() : clicked;
        int step = player.isShiftKeyDown() ? 5 : 1;
        Direction next = facing;
        do next = Direction.from3DDataValue(next.get3DDataValue() + step);
        while (!supports(state, next));
        return next;
    }

    private static boolean supports(BlockState state, Direction direction) {
        return direction.getAxis().isHorizontal() || state.hasProperty(BlockStateProperties.FACING);
    }
}
