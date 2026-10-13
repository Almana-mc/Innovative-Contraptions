package me.almana.innovativecontraptions.content.bioenergy;

import com.mojang.serialization.MapCodec;
import me.almana.innovativecontraptions.content.energy.EuConnectable;
import me.almana.innovativecontraptions.content.machine.ActiveState;
import me.almana.innovativecontraptions.content.thermal.ThermalInventory;
import me.almana.innovativecontraptions.content.tool.Wrenchable;
import me.almana.innovativecontraptions.content.tool.WrenchableSupport;
import me.almana.innovativecontraptions.registry.ICBlocks;
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

public final class BioenergyBlock extends BaseEntityBlock implements EuConnectable, Wrenchable {
    public BioenergyBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH).setValue(BlockStateProperties.LIT, false));
    }
    @Override protected MapCodec<? extends BaseEntityBlock> codec() { return simpleCodec(BioenergyBlock::new); }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.LIT); }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) { return defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite()); }
    @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return state.is(ICBlocks.BIO_DIGESTER.get()) ? new BioDigester(pos, state) : new LiquidFuelGenerator(pos, state); }
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        return state.is(ICBlocks.BIO_DIGESTER.get())
                ? createTickerHelper(type, BioenergyContent.DIGESTER.get(), (world, pos, current, machine) -> { machine.tick(); ActiveState.update(world, pos, machine.active()); })
                : createTickerHelper(type, BioenergyContent.GENERATOR.get(), (world, pos, current, machine) -> { machine.tick(); ActiveState.update(world, pos, machine.active()); });
    }
    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (player instanceof ServerPlayer server && level.getBlockEntity(pos) instanceof ThermalInventory machine) server.openMenu(machine, pos);
        return InteractionResult.SUCCESS;
    }
    @Override protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) { return WrenchableSupport.useItemOnResult(stack); }
    @Override public Direction getFacing(Level level, BlockPos pos) { return level.getBlockState(pos).getValue(BlockStateProperties.HORIZONTAL_FACING); }
    @Override public boolean setFacing(Level level, BlockPos pos, Direction facing, Player player) { return WrenchableSupport.setHorizontalFacing(level, pos, BlockStateProperties.HORIZONTAL_FACING, facing); }
}
