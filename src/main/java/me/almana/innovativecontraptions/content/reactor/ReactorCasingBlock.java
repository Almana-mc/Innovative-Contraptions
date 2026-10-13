package me.almana.innovativecontraptions.content.reactor;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public final class ReactorCasingBlock extends Block {
    private final boolean hatch;

    public ReactorCasingBlock(Properties properties, boolean hatch) {
        super(properties);
        this.hatch = hatch;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        return hatch ? ReactorStructure.open(level, player, ReactorStructure.enclosing(level, pos)) : InteractionResult.PASS;
    }
}
