package me.almana.innovativecontraptions.content.energy.storage;

import com.mojang.serialization.MapCodec;
import me.almana.innovativecontraptions.content.energy.EuConnectable;
import me.almana.innovativecontraptions.content.energy.net.EnergySettings;
import me.almana.innovativecontraptions.content.energy.net.EuGrids;
import me.almana.innovativecontraptions.content.tool.Wrenchable;
import me.almana.innovativecontraptions.content.tool.WrenchableSupport;
import me.almana.innovativecontraptions.registry.ICBlockEntities;
import me.almana.innovativecontraptions.registry.ICDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
//? if >=26.1
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
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
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import java.util.List;

public final class ElectricalBlock extends BaseEntityBlock implements EuConnectable, Wrenchable {
    public final ElectricalKind kind;
    public ElectricalBlock(Properties properties, ElectricalKind kind) {
        super(properties);
        this.kind = kind;
        registerDefaultState(stateDefinition.any().setValue(BlockStateProperties.FACING, Direction.NORTH));
    }
    @Override protected MapCodec<? extends BaseEntityBlock> codec() { return simpleCodec(properties -> new ElectricalBlock(properties, kind)); }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(BlockStateProperties.FACING); }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(BlockStateProperties.FACING, context.getNearestLookingDirection().getOpposite());
    }
    @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }
    @Override protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Block.box(0, 0, 0, 16, kind.pad ? 15 : 16, 16);
    }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return ElectricalBlockEntity.create(pos, state); }
    //? if >=26.1 {
    @Override protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity,
                                          InsideBlockEffectApplier effects, boolean precise) {
    //?} else
    //@Override protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (kind.pad && entity instanceof ServerPlayer player && level.getBlockEntity(pos) instanceof ChargeStorageBlockEntity pad) {
            pad.contact(player);
        }
    }
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() ? null : createTickerHelper(type, ICBlockEntities.ELECTRICAL.get(), ElectricalBlockEntity::serverTick);
    }
    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (player instanceof ServerPlayer serverPlayer && level.getBlockEntity(pos) instanceof ElectricalBlockEntity entity) {
            serverPlayer.openMenu(entity, pos);
        }
        return InteractionResult.SUCCESS;
    }
    @Override protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                                  Player player, InteractionHand hand, BlockHitResult hit) {
        return WrenchableSupport.useItemOnResult(stack);
    }
    @Override public Direction getFacing(Level level, BlockPos pos) { return level.getBlockState(pos).getValue(BlockStateProperties.FACING); }
    @Override public boolean setFacing(Level level, BlockPos pos, Direction facing, Player player) {
        level.setBlock(pos, level.getBlockState(pos).setValue(BlockStateProperties.FACING, facing), Block.UPDATE_ALL);
        ((ElectricalBlockEntity) level.getBlockEntity(pos)).refreshConnections();
        return true;
    }
    @Override protected boolean isSignalSource(BlockState state) { return true; }
    @Override protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction side) {
        return level.getBlockEntity(pos) instanceof ElectricalBlockEntity entity ? entity.signal() : 0;
    }
    @Override protected boolean hasAnalogOutputSignal(BlockState state) { return !kind.transformer; }
    //? if >=26.1 {
    @Override protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction side) {
    //?} else
    //@Override protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof ElectricalBlockEntity entity ? entity.comparator() : 0;
    }
    @Override public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity player, ItemStack stack) {
        ((ElectricalBlockEntity) level.getBlockEntity(pos)).restoreEnergy(stack.getOrDefault(ICDataComponents.ENERGY.get(), 0));
    }
    @Override protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        List<ItemStack> drops = super.getDrops(state, params);
        if (params.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof ElectricalBlockEntity entity && !kind.transformer) {
            for (ItemStack stack : drops) {
                if (stack.is(asItem())) stack.set(ICDataComponents.ENERGY.get(), (int) (entity.storedEu() * EnergySettings.retention()));
            }
        }
        return drops;
    }
    @Override protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean moved) {
        EuGrids.markDirty(level, pos);
        super.affectNeighborsAfterRemoval(state, level, pos, moved);
    }
}
