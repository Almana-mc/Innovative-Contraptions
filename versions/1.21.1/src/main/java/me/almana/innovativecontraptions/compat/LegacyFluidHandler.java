package me.almana.innovativecontraptions.compat;

import com.google.common.primitives.Ints;
import com.mojang.logging.LogUtils;
import me.almana.innovativecontraptions.compat.transfer.ResourceHandler;
import me.almana.innovativecontraptions.compat.transfer.TransferPreconditions;
import me.almana.innovativecontraptions.compat.transfer.fluid.FluidResource;
import me.almana.innovativecontraptions.compat.transfer.transaction.SnapshotJournal;
import me.almana.innovativecontraptions.compat.transfer.transaction.TransactionContext;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import org.slf4j.Logger;

final class LegacyFluidHandler extends SnapshotJournal<LegacyFluidHandler.Queued> implements ResourceHandler<FluidResource> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Queued NONE = new Queued(0, FluidResource.EMPTY, 0);

    private final IFluidHandler handler;
    private Queued queued = NONE;

    LegacyFluidHandler(IFluidHandler handler) {
        this.handler = handler;
    }

    @Override
    public int size() {
        return handler.getTanks();
    }

    @Override
    public FluidResource getResource(int index) {
        if (queued.amount() == 0 || queued.index() != index) return FluidResource.of(handler.getFluidInTank(index));
        return getAmountAsLong(index) == 0 ? FluidResource.EMPTY : queued.resource();
    }

    @Override
    public long getAmountAsLong(int index) {
        return handler.getFluidInTank(index).getAmount() + (queued.index() == index ? queued.amount() : 0);
    }

    @Override
    public long getCapacityAsLong(int index, FluidResource resource) {
        return resource.isEmpty() || isValid(index, resource) ? handler.getTankCapacity(index) : 0;
    }

    @Override
    public boolean isValid(int index, FluidResource resource) {
        return handler.isFluidValid(index, resource.toStack(1));
    }

    @Override
    public int insert(int index, FluidResource resource, int amount, TransactionContext transaction) {
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
        // tank-agnostic fill, index 0
        if (index != 0 || queued.amount() < 0 || queued.amount() > 0 && !queued.resource().equals(resource)) return 0;
        int pending = queued.amount();
        int filled = handler.fill(resource.toStack(Ints.saturatedCast((long) pending + amount)), FluidAction.SIMULATE);
        int accepted = Math.max(0, Math.min(amount, filled - pending));
        if (accepted > 0) queue(new Queued(index, resource, pending + accepted), transaction);
        return accepted;
    }

    @Override
    public int extract(int index, FluidResource resource, int amount, TransactionContext transaction) {
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
        if (queued.amount() > 0 || queued.amount() < 0 && (queued.index() != index || !queued.resource().equals(resource)) || !resource.matches(handler.getFluidInTank(index))) return 0;
        int pending = -queued.amount();
        var drained = handler.drain(resource.toStack(Ints.saturatedCast((long) pending + amount)), FluidAction.SIMULATE);
        if (!resource.matches(drained)) return 0;
        int accepted = Math.max(0, Math.min(amount, drained.getAmount() - pending));
        if (accepted > 0) queue(new Queued(index, resource, -pending - accepted), transaction);
        return accepted;
    }

    private void queue(Queued op, TransactionContext transaction) {
        updateSnapshots(transaction);
        queued = op;
    }

    @Override
    protected Queued createSnapshot() {
        return queued;
    }

    @Override
    protected void revertToSnapshot(Queued snapshot) {
        queued = snapshot;
    }

    @Override
    protected void onRootCommit(Queued originalState) {
        var op = queued;
        queued = NONE;
        if (op.amount() == 0) return;
        // trusts sim, drift lost
        int moved = op.amount() > 0
                ? handler.fill(op.resource().toStack(op.amount()), FluidAction.EXECUTE)
                : -handler.drain(op.resource().toStack(-op.amount()), FluidAction.EXECUTE).getAmount();
        if (moved != op.amount()) LOGGER.warn("{} committed {} of simulated {} {}", handler, moved, op.amount(), op.resource());
    }

    record Queued(int index, FluidResource resource, int amount) {}
}
