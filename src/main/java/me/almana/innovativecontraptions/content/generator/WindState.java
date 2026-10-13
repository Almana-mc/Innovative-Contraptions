package me.almana.innovativecontraptions.content.generator;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public final class WindState {
    public static float strength(Level level, BlockPos pos) {
        if (!level.dimensionType().hasSkyLight() || pos.getY() <= 64) return 0;
        double phase = level.getGameTime() / 2400.0;
        float weather = level.isThundering() ? 1.5F : level.isRaining() ? 1.25F : 1;
        float height = Math.max(0, 1 - Math.abs(pos.getY() - 160) / 96.0F);
        return (float) ((48 + 32 * Math.sin(phase)) * height * weather);
    }

    private WindState() {}
}
