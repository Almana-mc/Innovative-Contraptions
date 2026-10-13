package me.almana.innovativecontraptions.content.reactor;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import me.almana.innovativecontraptions.content.tool.WrenchableSupport;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public final class ReactorFluidPortBlock extends BaseEntityBlock {
    public static final MapCodec<ReactorFluidPortBlock> CODEC = simpleCodec(ReactorFluidPortBlock::new);

    public ReactorFluidPortBlock(Properties properties) { super(properties); }

    @Override protected MapCodec<? extends ReactorFluidPortBlock> codec() { return CODEC; }
    @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new ReactorFluidPortBlockEntity(pos, state); }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() ? null : createTickerHelper(type, ReactorContent.FLUID_PORT.get(), (world, pos, current, port) -> port.tick());
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return WrenchableSupport.useItemOnResult(stack);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof ReactorFluidPortBlockEntity port)) return InteractionResult.PASS;
        if (!level.isClientSide()) port.swap(player);
        return InteractionResult.SUCCESS;
    }
}
