package me.almana.innovativecontraptions.content.energy.debug;

import me.almana.innovativecontraptions.compat.SavedBlockEntity;
import me.almana.innovativecontraptions.content.energy.EuSink;
import me.almana.innovativecontraptions.content.energy.net.EuGrids;
import me.almana.innovativecontraptions.registry.ICBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class EnergyStorageBlockEntity extends SavedBlockEntity implements EuSink {
    public static final int CAPACITY = 100_000;

    private int stored;

    public EnergyStorageBlockEntity(BlockPos pos, BlockState state) {
        super(ICBlockEntities.ENERGY_STORAGE.get(), pos, state);
    }

    public int stored() {
        return stored;
    }

    @Override public int demandedEu() { return CAPACITY - stored; }
    @Override public int maxInputVoltage() { return Integer.MAX_VALUE; }

    public int receiveEu(int voltage, int amount) {
        int accepted = Math.min(amount, CAPACITY - stored);
        if (accepted > 0) {
            stored += accepted;
            setChanged();
        }
        return accepted;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level != null && !level.isClientSide()) {
            EuGrids.markDirty(level, worldPosition);
        }
    }

    @Override
    public void setRemoved() {
        if (level != null && !level.isClientSide()) {
            EuGrids.markDirty(level, worldPosition);
        }
        super.setRemoved();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("stored", stored);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        stored = input.getIntOr("stored", 0);
    }
}
