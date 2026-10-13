package me.almana.innovativecontraptions.compat;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class SavedBlockEntity extends BlockEntity {
    protected SavedBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    protected void saveAdditional(ValueOutput output) {}

    protected void loadAdditional(ValueInput input) {}

    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        if (this instanceof Container container && level != null) Containers.dropContents(level, pos, container);
    }

    @Override
    protected final void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        saveAdditional(new ValueOutput(tag, registries));
    }

    @Override
    protected final void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        loadAdditional(new ValueInput(tag, registries));
    }
}
