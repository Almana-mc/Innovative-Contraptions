package me.almana.innovativecontraptions.content.energy.net;

import me.almana.innovativecontraptions.registry.ICBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public final class EuOverload {
    public static void destroy(ServerLevel level, BlockPos pos) {
        if (!EnergySettings.MACHINE_EXPLOSIONS.get()) return;
        float strength = level.getBlockState(pos).is(ICBlocks.MATTER_FABRICATOR.get()) ? 15F : 2.5F;
        level.removeBlock(pos, false);
        level.explode(null, null, null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                strength, false, Level.ExplosionInteraction.BLOCK);
        EuGrids.markDirty(level, pos);
    }
    private EuOverload() {}
}
