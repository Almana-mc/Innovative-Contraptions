package me.almana.innovativecontraptions.compat.transfer.item;

import java.util.Objects;
import me.almana.innovativecontraptions.compat.transfer.ResourceHandler;
import me.almana.innovativecontraptions.compat.transfer.TransferPreconditions;
import me.almana.innovativecontraptions.compat.transfer.transaction.SnapshotJournal;
import me.almana.innovativecontraptions.compat.transfer.transaction.TransactionContext;
import net.minecraft.world.item.ItemStack;

public abstract class ItemStackResourceHandler extends SnapshotJournal<ItemStack> implements ResourceHandler<ItemResource> {
    protected abstract ItemStack getStack();

    protected abstract void setStack(ItemStack stack);

    protected abstract boolean isValid(ItemResource resource);

    protected abstract int getCapacity(ItemResource resource);

    @Override
    public final int size() {
        return 1;
    }

    @Override
    public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
        Objects.checkIndex(index, size());
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
        ItemStack currentStack = getStack();
        if ((currentStack.isEmpty() || resource.matches(currentStack)) && isValid(resource)) {
            int insertedAmount = Math.min(amount, getCapacity(resource) - currentStack.getCount());
            if (insertedAmount > 0) {
                updateSnapshots(transaction);
                currentStack = getStack();
                if (currentStack.isEmpty()) {
                    currentStack = resource.toStack(insertedAmount);
                } else {
                    currentStack.grow(insertedAmount);
                }
                setStack(currentStack);
                return insertedAmount;
            }
        }
        return 0;
    }

    @Override
    public int extract(int index, ItemResource resource, int amount, TransactionContext transaction) {
        Objects.checkIndex(index, size());
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
        ItemStack currentStack = getStack();
        if (resource.matches(currentStack)) {
            int extracted = Math.min(currentStack.getCount(), amount);
            if (extracted > 0) {
                updateSnapshots(transaction);
                currentStack = getStack();
                currentStack.shrink(extracted);
                setStack(currentStack);
                return extracted;
            }
        }
        return 0;
    }

    @Override
    public ItemResource getResource(int index) {
        Objects.checkIndex(index, size());
        return ItemResource.of(getStack());
    }

    @Override
    public long getAmountAsLong(int index) {
        Objects.checkIndex(index, size());
        return getStack().getCount();
    }

    @Override
    public long getCapacityAsLong(int index, ItemResource resource) {
        Objects.checkIndex(index, size());
        return resource.isEmpty() || isValid(resource) ? getCapacity(resource) : 0;
    }

    @Override
    public boolean isValid(int index, ItemResource resource) {
        Objects.checkIndex(index, size());
        TransferPreconditions.checkNonEmpty(resource);
        return isValid(resource);
    }

    @Override
    protected ItemStack createSnapshot() {
        ItemStack original = getStack();
        setStack(original.copy());
        return original;
    }

    @Override
    protected void revertToSnapshot(ItemStack snapshot) {
        setStack(snapshot);
    }

    @Override
    public String toString() {
        return getClass().getName() + "[" + getStack() + "]";
    }
}
