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

public class EnergyStorageBlock extends BaseEntityBlock implements EuConnectable {
    public static final MapCodec<EnergyStorageBlock> CODEC = simpleCodec(EnergyStorageBlock::new);

    public EnergyStorageBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends EnergyStorageBlock> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EnergyStorageBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof EnergyStorageBlockEntity storage
                && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.sendSystemMessage(
                    Component.literal("Stored EU: " + storage.stored() + " / " + EnergyStorageBlockEntity.CAPACITY),
                    true
            );
        }
        return InteractionResult.SUCCESS;
    }
}
