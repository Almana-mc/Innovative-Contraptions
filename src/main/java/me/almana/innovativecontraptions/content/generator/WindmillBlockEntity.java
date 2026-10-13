package me.almana.innovativecontraptions.content.generator;

import me.almana.innovativecontraptions.registry.ICBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class WindmillBlockEntity extends GeneratorBlockEntityBase implements WindRotorVisual {
    private float output;
    private float remainder;

    public WindmillBlockEntity(BlockPos pos, BlockState state) {
        super(ICBlockEntities.WINDMILL.get(), pos, state, 640, 1);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, WindmillBlockEntity entity) {
        if (level.getGameTime() % 20 == 0) {
            int obstruction = 0;
            for (BlockPos scan : BlockPos.betweenClosed(pos.offset(-4, -3, -4), pos.offset(4, 3, 4))) {
                if (!scan.equals(pos) && !level.getBlockState(scan).isAir()) obstruction++;
            }
            entity.output = Math.max(0, WindState.strength(level, pos) / GeneratorRates.PASSIVE_WIND_DIVISOR
                    - (float) obstruction / GeneratorRates.PASSIVE_WIND_OBSTRUCTION_DIVISOR);
            level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
        }
        entity.chargeBattery();
        if (entity.euBuffer < entity.bufferMax) {
            entity.remainder += entity.output;
            int generated = (int) entity.remainder;
            entity.remainder -= generated;
            entity.euBuffer = Math.min(entity.bufferMax, entity.euBuffer + generated);
            entity.setChanged();
        }
    }

    @Override public float speed() { return output; }
    @Override public boolean hasRotor() { return true; }
    @Override protected float currentOutput() { return output; }
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider registries) { return saveWithoutMetadata(registries); }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putFloat("wind_output", this.output);
        output.putFloat("wind_remainder", remainder);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        output = input.getFloatOr("wind_output", 0);
        remainder = input.getFloatOr("wind_remainder", 0);
    }
}
