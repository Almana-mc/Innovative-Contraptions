package me.almana.innovativecontraptions.content.gathering;

import com.mojang.serialization.MapCodec;
import me.almana.innovativecontraptions.content.energy.EuConnectable;
import me.almana.innovativecontraptions.content.machine.ActiveState;
import me.almana.innovativecontraptions.content.tool.Wrenchable;
import me.almana.innovativecontraptions.content.tool.WrenchableSupport;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;

public final class GatheringBlock extends BaseEntityBlock implements EuConnectable, Wrenchable {
    public static final MapCodec<GatheringBlock> CODEC = simpleCodec(GatheringBlock::new);
    public GatheringBlock(Properties properties) { super(properties); registerDefaultState(stateDefinition.any().setValue(BlockStateProperties.FACING, Direction.NORTH).setValue(BlockStateProperties.LIT, false)); }
    @Override protected MapCodec<? extends GatheringBlock> codec() { return CODEC; }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(BlockStateProperties.FACING, BlockStateProperties.LIT); }
    @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) { return defaultBlockState().setValue(BlockStateProperties.FACING, context.getNearestLookingDirection().getOpposite()); }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new GatheringBlockEntity(pos, state); }
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() ? null : createTickerHelper(type, GatheringContent.MACHINES.get(), (world, pos, current, machine) -> {
            machine.tick();
            ActiveState.update(world, pos, machine.status() == 0 || machine.terrain() && machine.status() == 3);
        });
    }
    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.getBlockEntity(pos) instanceof GatheringBlockEntity machine) {
            if (machine.terrain()) {
                if (!player.getMainHandItem().isEmpty()) return InteractionResult.PASS;
                if (!level.isClientSide() && !player.isShiftKeyDown()) machine.programInteraction(player, InteractionHand.MAIN_HAND);
            }
            else if (player instanceof ServerPlayer serverPlayer) serverPlayer.openMenu(machine, pos);
        }
        return InteractionResult.SUCCESS;
    }
    @Override protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (state.is(GatheringContent.TERRAIN.get()) && stack.getItem() instanceof TerrainProgramItem) {
            if (!level.isClientSide() && !player.isShiftKeyDown()) ((GatheringBlockEntity) level.getBlockEntity(pos)).programInteraction(player, hand);
            //? if >=26.1 {
            return InteractionResult.SUCCESS;
            //?} else
            //return net.minecraft.world.ItemInteractionResult.SUCCESS;
        }
        return WrenchableSupport.useItemOnResult(stack);
    }
    @Override public Direction getFacing(Level level, BlockPos pos) { return level.getBlockState(pos).getValue(BlockStateProperties.FACING); }
    @Override public boolean setFacing(Level level, BlockPos pos, Direction direction, Player player) { level.setBlockAndUpdate(pos, level.getBlockState(pos).setValue(BlockStateProperties.FACING, direction)); return true; }
}
