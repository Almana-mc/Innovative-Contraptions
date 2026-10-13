package me.almana.innovativecontraptions.content.matter;

import com.mojang.serialization.MapCodec;
import me.almana.innovativecontraptions.content.energy.EuConnectable;
import me.almana.innovativecontraptions.content.machine.ActiveState;
import me.almana.innovativecontraptions.content.tool.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;

public final class MatterBlock extends BaseEntityBlock implements EuConnectable, Wrenchable {
    public static final MapCodec<MatterBlock> CODEC = simpleCodec(MatterBlock::new);
    public MatterBlock(Properties properties) { super(properties); registerDefaultState(stateDefinition.any().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH).setValue(BlockStateProperties.LIT, false)); }
    @Override protected MapCodec<? extends MatterBlock> codec() { return CODEC; }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.LIT); }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) { return defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite()); }
    @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new MatterBlockEntity(pos, state); }
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() ? null : createTickerHelper(type, MatterContent.ENTITY.get(), (world, pos, current, machine) -> {
            machine.tick();
            ActiveState.update(world, pos, machine.status == 1);
        });
    }
    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (player instanceof ServerPlayer serverPlayer && level.getBlockEntity(pos) instanceof MatterBlockEntity machine) serverPlayer.openMenu(machine, pos);
        return InteractionResult.SUCCESS;
    }
    @Override protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) { return WrenchableSupport.useItemOnResult(stack); }
    @Override public Direction getFacing(Level level, BlockPos pos) { return WrenchableSupport.getHorizontalFacing(level, pos, BlockStateProperties.HORIZONTAL_FACING); }
    @Override public boolean setFacing(Level level, BlockPos pos, Direction facing, Player player) { return WrenchableSupport.setHorizontalFacing(level, pos, BlockStateProperties.HORIZONTAL_FACING, facing); }
}

