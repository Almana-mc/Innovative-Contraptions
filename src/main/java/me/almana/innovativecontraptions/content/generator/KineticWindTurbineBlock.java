package me.almana.innovativecontraptions.content.generator;

import com.mojang.serialization.MapCodec;
import me.almana.innovativecontraptions.content.tool.Wrenchable;
import me.almana.innovativecontraptions.content.tool.WrenchableSupport;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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

public final class KineticWindTurbineBlock extends BaseEntityBlock implements Wrenchable {
    public static final MapCodec<KineticWindTurbineBlock> CODEC = simpleCodec(KineticWindTurbineBlock::new);

    public KineticWindTurbineBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH));
    }

    @Override protected MapCodec<? extends BaseEntityBlock> codec() { return CODEC; }
    @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(BlockStateProperties.HORIZONTAL_FACING); }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) { return defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite()); }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new KineticWindTurbineBlockEntity(pos, state); }
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() ? null : createTickerHelper(type, KineticContent.TURBINE.get(), KineticWindTurbineBlockEntity::serverTick);
    }
    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) { return GeneratorBlockEntityBase.openMenu(level, pos, player); }
    @Override protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) { return WrenchableSupport.useItemOnResult(stack); }
    @Override public Direction getFacing(Level level, BlockPos pos) { return WrenchableSupport.getHorizontalFacing(level, pos, BlockStateProperties.HORIZONTAL_FACING); }
    @Override public boolean setFacing(Level level, BlockPos pos, Direction facing, Player player) { return WrenchableSupport.setHorizontalFacing(level, pos, BlockStateProperties.HORIZONTAL_FACING, facing); }
}
