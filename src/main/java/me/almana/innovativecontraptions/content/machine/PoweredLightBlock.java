package me.almana.innovativecontraptions.content.machine;

import com.mojang.serialization.MapCodec;
import me.almana.innovativecontraptions.content.energy.EuConnectable;
import me.almana.innovativecontraptions.content.energy.EuSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
//? if >=26.1
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
//? if >=26.1 {
import net.minecraft.world.level.ScheduledTickAccess;
//?} else
//import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class PoweredLightBlock extends BaseEntityBlock implements EuConnectable {
    public static final MapCodec<PoweredLightBlock> CODEC = simpleCodec(PoweredLightBlock::new);
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;

    public PoweredLightBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(LIT, false).setValue(FACING, Direction.UP));
    }

    @Override public MapCodec<PoweredLightBlock> codec() { return CODEC; }
    @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(LIT, FACING); }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = defaultBlockState().setValue(FACING, context.getClickedFace());
        return state.canSurvive(context.getLevel(), context.getClickedPos()) ? state : null;
    }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new PoweredLightBlockEntity(pos, state); }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
            Player player, BlockHitResult hit) {
        if (!level.isClientSide()) interact(level, pos, player, ItemStack.EMPTY);
        return InteractionResult.SUCCESS;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state,
            Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide()) interact(level, pos, player, stack);
        //? if >=26.1 {
        return InteractionResult.SUCCESS;
        //?} else
        //return net.minecraft.world.ItemInteractionResult.SUCCESS;
    }

    private void interact(Level level, BlockPos pos, Player player, ItemStack stack) {
        var light = (PoweredLightBlockEntity) level.getBlockEntity(pos);
        light.interact(stack);
        var mode = Component.translatable("screen.innovativecontraptions.lamp.mode." + (light.inverted() ? 1 : 0));
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.sendSystemMessage(Component.translatable("screen.innovativecontraptions.lamp.status", light.energy(), mode), true);
        }
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case UP -> Block.box(2, 0, 2, 14, 1, 14);
            case DOWN -> Block.box(2, 15, 2, 14, 16, 14);
            case NORTH -> Block.box(2, 2, 15, 14, 14, 16);
            case SOUTH -> Block.box(2, 2, 0, 14, 14, 1);
            case EAST -> Block.box(0, 2, 2, 1, 14, 14);
            case WEST -> Block.box(15, 2, 2, 16, 14, 14);
        };
    }

    @Override protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction face = state.getValue(FACING);
        BlockPos support = pos.relative(face.getOpposite());
        return level.getBlockState(support).isFaceSturdy(level, support, face)
                || level.getBlockEntity(support) instanceof EuSource;
    }

    //? if >=26.1 {
    @Override protected BlockState updateShape(BlockState state, LevelReader level,
            ScheduledTickAccess ticks, BlockPos pos, Direction side,
            BlockPos neighbor, BlockState neighborState, RandomSource random) {
    //?} else {
    /*@Override protected BlockState updateShape(BlockState state, Direction side, BlockState neighborState,
            LevelAccessor level, BlockPos pos, BlockPos neighbor) {
    *///?}
        return state.canSurvive(level, pos) ? state : Blocks.AIR.defaultBlockState();
    }

    @Override protected boolean hasAnalogOutputSignal(BlockState state) { return true; }
    //? if >=26.1 {
    @Override protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction side) {
    //?} else
    //@Override protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof PoweredLightBlockEntity light ? light.comparator() : 0;
    }

    //? if >=26.1 {
    @Override protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity,
            InsideBlockEffectApplier effects, boolean precise) {
    //?} else
    //@Override protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (!level.isClientSide() && state.getValue(LIT)) igniteHostile(entity);
    }

    public static void igniteHostile(Entity entity) {
        if (entity instanceof Enemy) {
            //? if >=26.1 {
            entity.igniteForSeconds(entity.is(EntityTypeTags.UNDEAD) ? 20 : 10);
            //?} else
            //entity.igniteForSeconds(entity.getType().is(EntityTypeTags.UNDEAD) ? 20 : 10);
        }
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() ? null : createTickerHelper(type, PoweredLightContent.ENTITY.get(), PoweredLightBlockEntity::tick);
    }
}
