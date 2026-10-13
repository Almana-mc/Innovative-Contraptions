package me.almana.innovativecontraptions.compat;

import me.almana.innovativecontraptions.compat.transfer.ResourceHandler;
import me.almana.innovativecontraptions.compat.transfer.fluid.FluidResource;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

final class ResourceFluidHandlerItem extends ResourceFluidHandler implements IFluidHandlerItem {
    private final Container container;

    ResourceFluidHandlerItem(Container container, ResourceHandler<FluidResource> handler) {
        super(handler);
        this.container = container;
    }

    @Override
    public ItemStack getContainer() {
        return container.getItem(0);
    }
}
