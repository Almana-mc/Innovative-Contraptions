package me.almana.innovativecontraptions.compat.transfer.access;

import me.almana.innovativecontraptions.compat.transfer.ResourceHandler;
import me.almana.innovativecontraptions.compat.transfer.item.ItemResource;
import me.almana.innovativecontraptions.compat.transfer.item.VanillaContainerWrapper;
import me.almana.innovativecontraptions.compat.transfer.transaction.TransactionContext;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

class StackItemAccess implements ItemAccess {
    private final Item item;
    private final ResourceHandler<ItemResource> wrapper;

    StackItemAccess(ItemStack stack) {
        item = stack.getItem();
        // clamps oversized stacks
        wrapper = VanillaContainerWrapper.of(new SimpleContainer(stack));
    }

    @Override
    public ItemResource getResource() {
        return wrapper.getResource(0);
    }

    @Override
    public int getAmount() {
        return wrapper.getAmountAsInt(0);
    }

    @Override
    public int insert(ItemResource resource, int amount, TransactionContext transaction) {
        if (!resource.is(item)) return 0;
        return wrapper.insert(resource, amount, transaction);
    }

    @Override
    public int extract(ItemResource resource, int amount, TransactionContext transaction) {
        return wrapper.extract(resource, amount, transaction);
    }
}
