package me.almana.innovativecontraptions.content.generator;

import me.almana.innovativecontraptions.registry.ICBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class KineticGeneratorBlockEntity extends GeneratorBlockEntityBase {
    private static final int BUFFER_MAX = 2400;
    private int output;
    private int kineticRemainder;

    public KineticGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ICBlockEntities.KINETIC_GENERATOR.get(), pos, state, BUFFER_MAX, 1);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, KineticGeneratorBlockEntity entity) {
        entity.chargeBattery();
        entity.output = 0;
        Direction input = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        if (level.getBlockEntity(pos.relative(input)) instanceof KineticWindTurbineBlockEntity turbine) {
            int room = BUFFER_MAX - entity.euBuffer;
            int kinetic = turbine.extractKinetic(input.getOpposite(), room * GeneratorRates.KU_PER_EU - entity.kineticRemainder);
            entity.kineticRemainder += kinetic;
            entity.output = entity.kineticRemainder / GeneratorRates.KU_PER_EU;
            entity.kineticRemainder %= GeneratorRates.KU_PER_EU;
            entity.euBuffer += entity.output;
            if (kinetic > 0) entity.setChanged();
        }
    }

    @Override protected float currentOutput() { return output; }

    @Override public int packetsPerTick() { return 4; }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("kinetic_remainder", kineticRemainder);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        kineticRemainder = input.getIntOr("kinetic_remainder", 0);
    }
}
