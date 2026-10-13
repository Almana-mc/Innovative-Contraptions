package me.almana.innovativecontraptions.content.agriculture;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
//? if >=26.1
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class CropPlotBlock extends BaseEntityBlock {
    public static final MapCodec<CropPlotBlock> CODEC = simpleCodec(CropPlotBlock::new);
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 7);
    public static final IntegerProperty SPECIES = IntegerProperty.create("species", 0, CropSpecies.values().length - 1);
    public static final BooleanProperty CROSSING = BooleanProperty.create("crossing");
    public CropPlotBlock(Properties properties) {
        super(properties); registerDefaultState(stateDefinition.any().setValue(AGE, 0).setValue(SPECIES, 0).setValue(CROSSING, false));
    }
    @Override protected MapCodec<? extends CropPlotBlock> codec() { return CODEC; }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(AGE, SPECIES, CROSSING); }
    @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }
    //? if >=26.1 {
    @Override protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) { return Block.column(14, 0, 14); }
    //?} else
    //@Override protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) { return Block.box(1, 0, 1, 15, 14, 15); }
    @Override protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) { return level.getBlockState(pos.below()).is(Blocks.FARMLAND); }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new CropPlotBlockEntity(pos, state); }
    //? if >=26.1 {
    @Override protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity,
                                         InsideBlockEffectApplier effects, boolean precise) {
    //?} else
    //@Override protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (!level.isClientSide() && entity instanceof LivingEntity living) CropHazards.touch((CropPlotBlockEntity) level.getBlockEntity(pos), living);
    }
    @Override protected boolean isSignalSource(BlockState state) { return state.getValue(SPECIES) == CropSpecies.RED_GRAIN.ordinal() && state.getValue(AGE) == 7; }
    @Override protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction side) { return isSignalSource(state) ? 15 : 0; }
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() ? null : createTickerHelper(type, FarmingContent.CROP.get(), (world, pos, current, crop) -> crop.tick());
    }
    @Override protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                                   Player player, InteractionHand hand, BlockHitResult hit) {
        //? if >=26.1 {
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        //?} else
        //if (level.isClientSide()) return net.minecraft.world.ItemInteractionResult.SUCCESS;
        return CropInteractions.use((CropPlotBlockEntity) level.getBlockEntity(pos), player, hand);
    }
    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide()) {
            var crop = (CropPlotBlockEntity) level.getBlockEntity(pos);
            crop.harvest().forEach(stack -> Block.popResource(level, pos, stack));
        }
        return InteractionResult.SUCCESS;
    }
}
