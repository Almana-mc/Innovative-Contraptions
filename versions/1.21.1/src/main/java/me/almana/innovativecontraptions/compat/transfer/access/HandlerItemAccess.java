package me.almana.innovativecontraptions.compat.transfer.access;

import java.util.Objects;
import me.almana.innovativecontraptions.compat.transfer.ResourceHandler;
import me.almana.innovativecontraptions.compat.transfer.TransferPreconditions;
import me.almana.innovativecontraptions.compat.transfer.item.ItemResource;
import me.almana.innovativecontraptions.compat.transfer.transaction.TransactionContext;

class HandlerItemAccess implements ItemAccess {
    private final ResourceHandler<ItemResource> handler;
    private final int index;

    HandlerItemAccess(ResourceHandler<ItemResource> handler, int index) {
        Objects.checkIndex(index, handler.size());
        this.handler = handler;
        this.index = index;
    }

    @Override
    public ItemResource getResource() {
        return handler.getResource(index);
    }

    @Override
    public int getAmount() {
        return handler.getAmountAsInt(index);
    }

    @Override
    public int insert(ItemResource resource, int amount, TransactionContext transaction) {
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
        int inserted = handler.insert(index, resource, amount, transaction);
        if (inserted < amount) {
            inserted += handler.insert(resource, amount - inserted, transaction);
        }
        return inserted;
    }

    @Override
    public int extract(ItemResource resource, int amount, TransactionContext transaction) {
        return handler.extract(index, resource, amount, transaction);
    }
}
