package me.almana.innovativecontraptions.content.tool;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public interface Wrenchable {
    @Nullable
    Direction getFacing(Level level, BlockPos pos);

    boolean setFacing(Level level, BlockPos pos, Direction newFacing, Player player);
}
