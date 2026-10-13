package me.almana.innovativecontraptions.compat;

import me.almana.innovativecontraptions.compat.transfer.ResourceHandler;
import me.almana.innovativecontraptions.compat.transfer.TransferPreconditions;
import me.almana.innovativecontraptions.compat.transfer.access.ItemAccess;
import me.almana.innovativecontraptions.compat.transfer.fluid.FluidResource;
import me.almana.innovativecontraptions.compat.transfer.item.ItemResource;
import me.almana.innovativecontraptions.compat.transfer.transaction.TransactionContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities.FluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.Nullable;

record LegacyFluidHandlerItem(ItemAccess access) implements ResourceHandler<FluidResource> {
    @Nullable
    private IFluidHandlerItem handler() {
        // Fresh copy per operation
        return access.getResource().toStack().getCapability(FluidHandler.ITEM);
    }

    @Override
    public int size() {
        var handler = handler();
        return handler == null ? 0 : handler.getTanks();
    }

    @Override
    public FluidResource getResource(int index) {
        var handler = handler();
        return handler == null ? FluidResource.EMPTY : FluidResource.of(handler.getFluidInTank(index));
    }

    @Override
    public long getAmountAsLong(int index) {
        var handler = handler();
        return handler == null ? 0 : (long) access.getAmount() * handler.getFluidInTank(index).getAmount();
    }

    @Override
    public long getCapacityAsLong(int index, FluidResource resource) {
        var handler = handler();
        if (handler == null || !resource.isEmpty() && !isValid(index, resource)) return 0;
        return (long) access.getAmount() * handler.getTankCapacity(index);
    }

    @Override
    public boolean isValid(int index, FluidResource resource) {
        var handler = handler();
        return handler != null && handler.isFluidValid(index, resource.toStack(1));
    }

    @Override
    public int insert(int index, FluidResource resource, int amount, TransactionContext transaction) {
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
        int count = access.getAmount();
        var handler = handler();
        if (count == 0 || handler == null || index != 0) return 0;
        // linked storage executes early
        int filled = handler.fill(resource.toStack(amount / count), FluidAction.EXECUTE);
        return filled == 0 ? 0 : filled * replace(handler.getContainer(), count, transaction);
    }

    @Override
    public int extract(int index, FluidResource resource, int amount, TransactionContext transaction) {
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
        int count = access.getAmount();
        var handler = handler();
        if (count == 0 || handler == null || !resource.matches(handler.getFluidInTank(index))) return 0;
        var drained = handler.drain(resource.toStack(amount / count), FluidAction.EXECUTE);
        return resource.matches(drained) ? drained.getAmount() * replace(handler.getContainer(), count, transaction) : 0;
    }

    private int replace(ItemStack container, int count, TransactionContext transaction) {
        if (container.isEmpty()) return access.extract(access.getResource(), count, transaction);
        return access.exchange(ItemResource.of(container), count, transaction);
    }
}
