package me.almana.innovativecontraptions.content.reactor;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import me.almana.innovativecontraptions.content.energy.EuConnectable;
import me.almana.innovativecontraptions.content.energy.net.EuGrids;
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

public final class CoolantInjectorBlock extends BaseEntityBlock implements EuConnectable, Wrenchable {
    public static final MapCodec<CoolantInjectorBlock> CODEC = simpleCodec(CoolantInjectorBlock::new);

    public CoolantInjectorBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(BlockStateProperties.FACING, Direction.NORTH).setValue(BlockStateProperties.LIT, false));
    }

    @Override protected MapCodec<? extends CoolantInjectorBlock> codec() { return CODEC; }

    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.FACING, BlockStateProperties.LIT);
    }

    @Override public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(BlockStateProperties.FACING, context.getNearestLookingDirection().getOpposite());
    }

    @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new CoolantInjectorBlockEntity(pos, state); }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() ? null : createTickerHelper(type, ReactorContent.INJECTOR.get(), (world, pos, current, injector) -> {
            injector.tick();
            ActiveState.update(world, pos, injector.active());
        });
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer
                && level.getBlockEntity(pos) instanceof CoolantInjectorBlockEntity injector) serverPlayer.openMenu(injector, pos);
        return InteractionResult.SUCCESS;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return WrenchableSupport.useItemOnResult(stack);
    }

    @Override public Direction getFacing(Level level, BlockPos pos) { return level.getBlockState(pos).getValue(BlockStateProperties.FACING); }

    @Override public boolean setFacing(Level level, BlockPos pos, Direction newFacing, Player player) {
        level.setBlock(pos, level.getBlockState(pos).setValue(BlockStateProperties.FACING, newFacing), Block.UPDATE_ALL);
        EuGrids.markDirty(level, pos);
        return true;
    }
}
