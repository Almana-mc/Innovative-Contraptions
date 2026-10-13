package me.almana.innovativecontraptions.content.generator;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

public final class WindTurbineMath {
    public static boolean clearRotor(Level level, BlockPos pos, Direction facing, int radius) {
        BlockPos hub = pos.relative(facing);
        Direction across = facing.getClockWise();
        for (int horizontal = -radius; horizontal <= radius; horizontal++) {
            for (int vertical = -radius; vertical <= radius; vertical++) {
                BlockPos blade = hub.relative(across, horizontal).above(vertical);
                if (!level.hasChunkAt(blade) || !level.getBlockState(blade).isAir()) return false;
            }
        }
        for (int distance = 1; distance <= radius * 3; distance++) {
            BlockPos behind = pos.relative(facing.getOpposite(), distance);
            if (level.hasChunkAt(behind) && level.getBlockEntity(behind) instanceof KineticWindTurbineBlockEntity) return false;
        }
        return clearOtherRotors(level, pos, facing, radius);
    }

    private static boolean clearOtherRotors(Level level, BlockPos pos, Direction facing, int radius) {
        Direction across = facing.getClockWise();
        int reach = radius + RotorTier.CARBON.radius;
        for (int horizontal = -reach; horizontal <= reach; horizontal++) {
            for (int vertical = -reach; vertical <= reach; vertical++) {
                if (horizontal == 0 && vertical == 0) continue;
                BlockPos otherPos = pos.relative(across, horizontal).above(vertical);
                if (!level.hasChunkAt(otherPos)) continue;
                if (level.getBlockEntity(otherPos) instanceof KineticWindTurbineBlockEntity other
                        && other.facing() == facing && other.hasRotor()) {
                    int combined = radius + ((RotorItem) other.getItem(0).getItem()).tier().radius;
                    if (Math.abs(horizontal) <= combined && Math.abs(vertical) <= combined) return false;
                }
            }
        }
        return true;
    }

    private WindTurbineMath() {}
}
