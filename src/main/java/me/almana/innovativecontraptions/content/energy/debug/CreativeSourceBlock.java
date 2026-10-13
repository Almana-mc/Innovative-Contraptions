package me.almana.innovativecontraptions.content.energy.debug;

import com.mojang.serialization.MapCodec;
import me.almana.innovativecontraptions.content.energy.EuConnectable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class CreativeSourceBlock extends BaseEntityBlock implements EuConnectable {
    public static final MapCodec<CreativeSourceBlock> CODEC = simpleCodec(CreativeSourceBlock::new);

    public CreativeSourceBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends CreativeSourceBlock> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CreativeSourceBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof CreativeSourceBlockEntity source) {
            source.cycleTier();
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.sendSystemMessage(Component.literal("EU source: " + source.tier() + " (" + source.sourceVoltage() + " V)"), true);
            }
        }
        return InteractionResult.SUCCESS;
    }
}
