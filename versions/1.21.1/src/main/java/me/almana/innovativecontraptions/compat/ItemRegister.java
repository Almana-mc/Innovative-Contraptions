package me.almana.innovativecontraptions.compat;

import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ItemRegister extends DeferredRegister.Items {
    public ItemRegister(String namespace) {
        super(namespace);
    }

    public <I extends Item> DeferredItem<I> registerItem(String name, Function<Item.Properties, ? extends I> func, Supplier<Item.Properties> properties) {
        return register(name, () -> func.apply(properties.get()));
    }

    public DeferredItem<Item> registerSimpleItem(String name, Supplier<Item.Properties> properties) {
        return registerItem(name, Item::new, properties);
    }
}
