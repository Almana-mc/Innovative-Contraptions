package me.almana.innovativecontraptions.compat.transfer;

import java.util.Objects;
import me.almana.innovativecontraptions.compat.transfer.access.ItemAccess;
import me.almana.innovativecontraptions.compat.transfer.item.ItemResource;
import me.almana.innovativecontraptions.compat.transfer.resource.Resource;
import me.almana.innovativecontraptions.compat.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.Nullable;

public abstract class ItemAccessResourceHandler<T extends Resource> implements ResourceHandler<T> {
    protected final ItemAccess itemAccess;
    protected final int size;

    protected ItemAccessResourceHandler(ItemAccess itemAccess, int size) {
        this.itemAccess = itemAccess;
        this.size = size;
    }

    protected abstract T getResourceFrom(ItemResource accessResource, int index);

    protected abstract int getAmountFrom(ItemResource accessResource, int index);

    @Nullable
    protected abstract ItemResource update(ItemResource accessResource, int index, T newResource, int newAmount);

    @Override
    public boolean isValid(int index, T resource) {
        return true;
    }

    protected abstract int getCapacity(int index, T resource);

    @Override
    public int size() {
        return size;
    }

    @Override
    public T getResource(int index) {
        Objects.checkIndex(index, size());
        return getResourceFrom(itemAccess.getResource(), index);
    }

    @Override
    public long getAmountAsLong(int index) {
        Objects.checkIndex(index, size());
        return (long) itemAccess.getAmount() * getAmountFrom(itemAccess.getResource(), index);
    }

    @Override
    public long getCapacityAsLong(int index, T resource) {
        Objects.checkIndex(index, size());
        if (resource.isEmpty() || isValid(index, resource)) {
            return (long) itemAccess.getAmount() * getCapacity(index, resource);
        }
        return 0;
    }

    @Override
    public int insert(int index, T resource, int amount, TransactionContext transaction) {
        Objects.checkIndex(index, size());
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
        int accessAmount = itemAccess.getAmount();
        if (accessAmount == 0) return 0;
        int amountPerItem = amount / accessAmount;
        ItemResource accessResource = itemAccess.getResource();
        int currentAmountPerItem = getAmountFrom(accessResource, index);
        if ((currentAmountPerItem == 0 || resource.equals(getResourceFrom(accessResource, index))) && isValid(index, resource)) {
            int insertedPerItem = Math.min(amountPerItem, getCapacity(index, resource) - currentAmountPerItem);
            if (insertedPerItem > 0) {
                return insertedPerItem * replace(update(accessResource, index, resource, insertedPerItem + currentAmountPerItem), accessAmount, transaction);
            }
        }
        return 0;
    }

    @Override
    public int extract(int index, T resource, int amount, TransactionContext transaction) {
        Objects.checkIndex(index, size());
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
        int accessAmount = itemAccess.getAmount();
        if (accessAmount == 0) return 0;
        ItemResource accessResource = itemAccess.getResource();
        if (resource.equals(getResourceFrom(accessResource, index))) {
            int currentAmountPerItem = getAmountFrom(accessResource, index);
            int extractedPerItem = Math.min(amount / accessAmount, currentAmountPerItem);
            if (extractedPerItem > 0) {
                return extractedPerItem * replace(update(accessResource, index, resource, currentAmountPerItem - extractedPerItem), accessAmount, transaction);
            }
        }
        return 0;
    }

    private int replace(@Nullable ItemResource updated, int accessAmount, TransactionContext transaction) {
        if (updated == null) return itemAccess.extract(itemAccess.getResource(), accessAmount, transaction);
        return updated.isEmpty() ? 0 : itemAccess.exchange(updated, accessAmount, transaction);
    }
}
