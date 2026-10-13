package me.almana.innovativecontraptions.compat;

import com.google.common.primitives.Ints;
import com.mojang.logging.LogUtils;
import java.util.HashMap;
import java.util.Map;
import me.almana.innovativecontraptions.compat.transfer.ResourceHandler;
import me.almana.innovativecontraptions.compat.transfer.TransferPreconditions;
import me.almana.innovativecontraptions.compat.transfer.item.ItemResource;
import me.almana.innovativecontraptions.compat.transfer.transaction.SnapshotJournal;
import me.almana.innovativecontraptions.compat.transfer.transaction.TransactionContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import org.slf4j.Logger;

final class LegacyItemHandler extends SnapshotJournal<Map<Integer, LegacyItemHandler.Queued>> implements ResourceHandler<ItemResource> {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final IItemHandler handler;
    private Map<Integer, Queued> queued = Map.of();

    LegacyItemHandler(IItemHandler handler) {
        this.handler = handler;
    }

    @Override
    public int size() {
        return handler.getSlots();
    }

    @Override
    public ItemResource getResource(int index) {
        var op = queued.get(index);
        if (op == null) return ItemResource.of(handler.getStackInSlot(index));
        return getAmountAsLong(index) == 0 ? ItemResource.EMPTY : op.resource();
    }

    @Override
    public long getAmountAsLong(int index) {
        var op = queued.get(index);
        return handler.getStackInSlot(index).getCount() + (op == null ? 0 : op.amount());
    }

    @Override
    public long getCapacityAsLong(int index, ItemResource resource) {
        if (resource.isEmpty()) return handler.getSlotLimit(index);
        return isValid(index, resource) ? Math.min(handler.getSlotLimit(index), resource.getMaxStackSize()) : 0;
    }

    @Override
    public boolean isValid(int index, ItemResource resource) {
        return handler.isItemValid(index, resource.toStack());
    }

    @Override
    public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
        var op = queued.get(index);
        if (op != null && (op.amount() < 0 || !op.resource().equals(resource))) return 0;
        int pending = op == null ? 0 : op.amount();
        // Simulate on top of pending
        int total = Ints.saturatedCast((long) pending + amount);
        int rejected = handler.insertItem(index, resource.toStack(total), true).getCount();
        int accepted = Math.max(0, Math.min(amount, total - rejected - pending));
        if (accepted > 0) queue(index, new Queued(resource, pending + accepted), transaction);
        return accepted;
    }

    @Override
    public int extract(int index, ItemResource resource, int amount, TransactionContext transaction) {
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
        var op = queued.get(index);
        if (op != null && (op.amount() > 0 || !op.resource().equals(resource))) return 0;
        int pending = op == null ? 0 : -op.amount();
        ItemStack extracted = handler.extractItem(index, Ints.saturatedCast((long) pending + amount), true);
        if (!resource.matches(extracted)) return 0;
        int accepted = Math.max(0, Math.min(amount, extracted.getCount() - pending));
        if (accepted > 0) queue(index, new Queued(resource, -pending - accepted), transaction);
        return accepted;
    }

    private void queue(int index, Queued op, TransactionContext transaction) {
        updateSnapshots(transaction);
        var next = new HashMap<>(queued);
        next.put(index, op);
        queued = next;
    }

    @Override
    protected Map<Integer, Queued> createSnapshot() {
        return queued;
    }

    @Override
    protected void revertToSnapshot(Map<Integer, Queued> snapshot) {
        queued = snapshot;
    }

    @Override
    protected void onRootCommit(Map<Integer, Queued> originalState) {
        var ops = queued;
        queued = Map.of();
        // trusts sim, drift lost
        ops.forEach((index, op) -> {
            int moved = op.amount() > 0
                    ? op.amount() - handler.insertItem(index, op.resource().toStack(op.amount()), false).getCount()
                    : -handler.extractItem(index, -op.amount(), false).getCount();
            if (moved != op.amount()) LOGGER.warn("{} slot {} committed {} of simulated {} {}", handler, index, moved, op.amount(), op.resource());
        });
    }

    record Queued(ItemResource resource, int amount) {}
}
