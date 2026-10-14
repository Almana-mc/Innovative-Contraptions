package me.almana.innovativecontraptions.content.generator;

import me.almana.innovativecontraptions.registry.ICBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class SolarPanelBlockEntity extends GeneratorBlockEntityBase {
    private static final int BUFFER_MAX = 2;

    private int ticks;
    private float sun;
    private float carry;

    public SolarPanelBlockEntity(BlockPos pos, BlockState state) {
        super(ICBlockEntities.SOLAR_PANEL.get(), pos, state, BUFFER_MAX, 1);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, SolarPanelBlockEntity entity) {
        if (entity.ticks++ % 128 == 0) entity.sun = SolarLight.at(level, pos.above());
        entity.carry += entity.sun * GeneratorRates.SOLAR_EU_PER_TICK;
        int whole = (int) entity.carry;
        if (whole > 0) {
            entity.carry -= whole;
            entity.euBuffer = Math.min(entity.euBuffer + whole, BUFFER_MAX);
            entity.setChanged();
        }
        entity.chargeBattery();
    }

    @Override
    protected float currentOutput() { return sun * GeneratorRates.SOLAR_EU_PER_TICK; }

}
