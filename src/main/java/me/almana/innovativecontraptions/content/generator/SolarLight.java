package me.almana.innovativecontraptions.content.generator;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
//? if >=26.1
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.neoforged.neoforge.common.Tags;

public final class SolarLight {
    public static float at(Level level, BlockPos pos) {
        if (!level.dimensionType().hasSkyLight()) return 0;
        //? if >=26.1 {
        float angle = level.environmentAttributes().getValue(EnvironmentAttributes.SUN_ANGLE, pos) * Mth.DEG_TO_RAD;
        //?} else
        //float angle = level.getSunAngle(1.0F);
        float sun = Mth.clamp(Mth.cos(angle) * 2 + 0.2F, 0, 1);
        if (!level.getBiome(pos).is(Tags.Biomes.IS_SANDY)) {
            sun = Mth.clamp(sun * (1 - level.getRainLevel(1.0F) * 5 / 16F) * (1 - level.getThunderLevel(1.0F) * 5 / 16F), 0, 1);
        }
        return sun * level.getBrightness(LightLayer.SKY, pos) / 15F;
    }

    private SolarLight() {}
}
