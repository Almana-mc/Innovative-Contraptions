package me.almana.innovativecontraptions.content.construction;

import me.almana.innovativecontraptions.content.matter.MatterContent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.Tags;

public final class FoamBlock extends Block {
    public static final BooleanProperty REINFORCED = BooleanProperty.create("reinforced");

    public FoamBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(REINFORCED, false));
    }

    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(REINFORCED); }

    @Override protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int light = level.getMaxLocalRawBrightness(pos);
        for (Direction direction : Direction.values()) light = Math.max(light, level.getMaxLocalRawBrightness(pos.relative(direction)));
        int hardenTime = state.getValue(REINFORCED) ? 600 : 300;
        // ponytail: assumes randomTickSpeed 3
        if (random.nextFloat() * hardenTime * (16 - light) < 68.27F) level.setBlockAndUpdate(pos, hardened(state));
    }

    @Override protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                                   Player player, InteractionHand hand, BlockHitResult hit) {
        if (!stack.is(Tags.Items.SANDS_COLORLESS)) {
            //? if >=26.1 {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
            //?} else
            //return net.minecraft.world.ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (!level.isClientSide()) {
            stack.consume(1, player);
            level.setBlockAndUpdate(pos, hardened(state));
        }
        //? if >=26.1 {
        return InteractionResult.SUCCESS;
        //?} else
        //return net.minecraft.world.ItemInteractionResult.sidedSuccess(level.isClientSide());
    }

    private static BlockState hardened(BlockState state) {
        return state.getValue(REINFORCED) ? MatterContent.REINFORCED_STONE.get().defaultBlockState()
                : ConstructionContent.FOAM_WALL.get().defaultBlockState();
    }
}
