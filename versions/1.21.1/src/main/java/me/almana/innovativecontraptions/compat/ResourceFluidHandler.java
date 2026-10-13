package me.almana.innovativecontraptions.compat;

import me.almana.innovativecontraptions.compat.transfer.ResourceHandler;
import me.almana.innovativecontraptions.compat.transfer.ResourceHandlerUtil;
import me.almana.innovativecontraptions.compat.transfer.fluid.FluidResource;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

class ResourceFluidHandler implements IFluidHandler {
    private final ResourceHandler<FluidResource> handler;

    ResourceFluidHandler(ResourceHandler<FluidResource> handler) {
        this.handler = handler;
    }

    @Override
    public int getTanks() {
        return handler.size();
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        return handler.getResource(tank).toStack(handler.getAmountAsInt(tank));
    }

    @Override
    public int getTankCapacity(int tank) {
        return handler.getCapacityAsInt(tank, FluidResource.EMPTY);
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return !stack.isEmpty() && handler.isValid(tank, FluidResource.of(stack));
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (resource.isEmpty()) return 0;
        return CapabilityBridge.transact(action.simulate(), transaction -> handler.insert(FluidResource.of(resource), resource.getAmount(), transaction));
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        if (resource.isEmpty()) return FluidStack.EMPTY;
        var fluid = FluidResource.of(resource);
        return fluid.toStack(CapabilityBridge.transact(action.simulate(), transaction -> handler.extract(fluid, resource.getAmount(), transaction)));
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        if (maxDrain <= 0) return FluidStack.EMPTY;
        var extracted = CapabilityBridge.transact(action.simulate(), transaction -> ResourceHandlerUtil.extractFirst(handler, fluid -> true, maxDrain, transaction));
        return extracted == null ? FluidStack.EMPTY : extracted.resource().toStack(extracted.amount());
    }
}
