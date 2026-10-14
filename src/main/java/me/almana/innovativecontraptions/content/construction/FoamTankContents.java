package me.almana.innovativecontraptions.content.construction;

import me.almana.innovativecontraptions.content.equipment.EquipmentData;
import net.neoforged.neoforge.transfer.ItemAccessResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;

public final class FoamTankContents extends ItemAccessResourceHandler<FluidResource> {
    private final int capacity;

    public FoamTankContents(ItemAccess access, int capacity) {
        super(access, 1);
        this.capacity = capacity;
    }

    @Override
    protected FluidResource getResourceFrom(ItemResource item, int index) {
        return getAmountFrom(item, index) == 0 ? FluidResource.EMPTY : FluidResource.of(ConstructionContent.FOAM_FLUID.get());
    }

    @Override
    protected int getAmountFrom(ItemResource item, int index) {
        return item.getOrDefault(EquipmentData.FOAM.get(), 0);
    }

    @Override
    protected int getCapacity(int index, FluidResource resource) { return capacity; }

    @Override
    public boolean isValid(int index, FluidResource resource) {
        return resource.equals(FluidResource.of(ConstructionContent.FOAM_FLUID.get()));
    }

    @Override
    protected ItemResource update(ItemResource item, int index, FluidResource resource, int amount) {
        return item.with(EquipmentData.FOAM.get(), amount);
    }
}
