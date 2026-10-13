package me.almana.innovativecontraptions.content.generator;

import me.almana.innovativecontraptions.registry.ICBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;

public class SolarPanelBlockEntity extends GeneratorBlockEntityBase {
    private static final int BUFFER_MAX = 64;

    private boolean active;

    public SolarPanelBlockEntity(BlockPos pos, BlockState state) {
        super(ICBlockEntities.SOLAR_PANEL.get(), pos, state, BUFFER_MAX, 1);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, SolarPanelBlockEntity entity) {
        int skyLight = level.getBrightness(LightLayer.SKY, pos.above());
        entity.active = skyLight >= 15 && level.isBrightOutside() && !level.isRaining();
        if (entity.active) {
            entity.euBuffer = Math.min(entity.euBuffer + GeneratorRates.SOLAR_EU_PER_TICK, BUFFER_MAX);
            entity.setChanged();
        }
        entity.chargeBattery();
    }

    @Override
    protected float currentOutput() { return active ? GeneratorRates.SOLAR_EU_PER_TICK : 0.0F; }

}
