package me.almana.innovativecontraptions.content.energy.debug;

import me.almana.innovativecontraptions.compat.SavedBlockEntity;
import me.almana.innovativecontraptions.content.energy.EuSource;
import me.almana.innovativecontraptions.content.energy.EuTier;
import me.almana.innovativecontraptions.content.energy.net.EuGrids;
import me.almana.innovativecontraptions.registry.ICBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class CreativeSourceBlockEntity extends SavedBlockEntity implements EuSource {
    private EuTier tier = EuTier.LV;

    public CreativeSourceBlockEntity(BlockPos pos, BlockState state) {
        super(ICBlockEntities.CREATIVE_SOURCE.get(), pos, state);
    }

    public EuTier tier() {
        return tier;
    }

    public void cycleTier() {
        tier = tier == EuTier.IV ? EuTier.LV : tier.next();
        setChanged();
    }

    @Override
    public int sourceVoltage() {
        return tier.volts;
    }

    @Override
    public int availableEu() {
        return tier.volts * 32;
    }

    @Override
    public int extractEu(int amount) {
        return amount;
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
        output.putInt("tier", tier.ordinal());
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        tier = EuTier.values()[Math.floorMod(input.getIntOr("tier", 0), EuTier.values().length)];
    }
}
