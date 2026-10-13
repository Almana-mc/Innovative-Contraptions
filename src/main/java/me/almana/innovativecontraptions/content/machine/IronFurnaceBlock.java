package me.almana.innovativecontraptions.content.machine;

import com.mojang.serialization.MapCodec;
import me.almana.innovativecontraptions.registry.ICBlockEntities;
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

import javax.annotation.Nullable;

public class IronFurnaceBlock extends BaseEntityBlock implements Wrenchable {
    public static final MapCodec<IronFurnaceBlock> CODEC = simpleCodec(IronFurnaceBlock::new);

    public IronFurnaceBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    protected MapCodec<? extends IronFurnaceBlock> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new IronFurnaceBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() ? null : createTickerHelper(type, ICBlockEntities.IRON_FURNACE.get(), IronFurnaceBlockEntity::serverTick);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer
                && level.getBlockEntity(pos) instanceof IronFurnaceBlockEntity be) {
            serverPlayer.openMenu(be);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return WrenchableSupport.useItemOnResult(stack);
    }

    @Override
    public Direction getFacing(Level level, BlockPos pos) {
        return WrenchableSupport.getHorizontalFacing(level, pos, BlockStateProperties.HORIZONTAL_FACING);
    }

    @Override
    public boolean setFacing(Level level, BlockPos pos, Direction newFacing, Player player) {
        return WrenchableSupport.setHorizontalFacing(level, pos, BlockStateProperties.HORIZONTAL_FACING, newFacing);
    }
}
