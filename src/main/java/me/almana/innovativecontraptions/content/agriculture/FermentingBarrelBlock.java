package me.almana.innovativecontraptions.content.agriculture;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

public final class FermentingBarrelBlock extends BaseEntityBlock {
    public static final MapCodec<FermentingBarrelBlock> CODEC = simpleCodec(FermentingBarrelBlock::new);
    public static final BooleanProperty TAPPED = BooleanProperty.create("tapped");
    public FermentingBarrelBlock(Properties properties) {
        super(properties); registerDefaultState(stateDefinition.any().setValue(TAPPED, false).setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH));
    }
    @Override protected MapCodec<? extends FermentingBarrelBlock> codec() { return CODEC; }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(TAPPED, BlockStateProperties.HORIZONTAL_FACING); }
    @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new FermentingBarrelBlockEntity(pos, state); }
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() ? null : createTickerHelper(type, FarmingContent.BARREL.get(), (world, pos, current, barrel) -> barrel.tick());
    }
    @Override protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        //? if >=26.1 {
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        //?} else
        //if (level.isClientSide()) return net.minecraft.world.ItemInteractionResult.SUCCESS;
        return BarrelInteractions.use((FermentingBarrelBlockEntity) level.getBlockEntity(pos), player, hand, hit.getDirection());
    }
    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide()) {
            var barrel = (FermentingBarrelBlockEntity) level.getBlockEntity(pos);
            if (player instanceof ServerPlayer serverPlayer) serverPlayer.sendSystemMessage(Component.translatable("message.innovativecontraptions.barrel",
                    barrel.water(), barrel.wheat(), barrel.hops(), barrel.maturity(), barrel.age()), true);
        }
        return InteractionResult.SUCCESS;
    }
}
