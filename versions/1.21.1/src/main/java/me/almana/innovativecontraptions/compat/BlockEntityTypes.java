package me.almana.innovativecontraptions.compat;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class BlockEntityTypes {
    public static <T extends BlockEntity> BlockEntityType<T> of(BlockEntityType.BlockEntitySupplier<? extends T> factory, Block... blocks) {
        return BlockEntityType.Builder.<T>of(factory, blocks).build(null);
    }

    private BlockEntityTypes() {
    }
}
