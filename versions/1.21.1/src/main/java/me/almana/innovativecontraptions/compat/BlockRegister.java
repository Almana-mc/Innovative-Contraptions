package me.almana.innovativecontraptions.compat;

import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class BlockRegister extends DeferredRegister.Blocks {
    public BlockRegister(String namespace) {
        super(namespace);
    }

    public <B extends Block> DeferredBlock<B> registerBlock(String name, Function<BlockBehaviour.Properties, ? extends B> func, Supplier<BlockBehaviour.Properties> properties) {
        return register(name, () -> func.apply(properties.get()));
    }
}
