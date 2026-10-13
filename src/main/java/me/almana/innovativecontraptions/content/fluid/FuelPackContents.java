package me.almana.innovativecontraptions.content.fluid;

import me.almana.innovativecontraptions.content.equipment.EquipmentData;
import me.almana.innovativecontraptions.content.equipment.FuelFlightItem;
import net.neoforged.neoforge.transfer.ItemAccessResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;

public final class FuelPackContents extends ItemAccessResourceHandler<FluidResource> {
    public FuelPackContents(ItemAccess access) { super(access, 1); }

    @Override
    protected FluidResource getResourceFrom(ItemResource item, int index) {
        return getAmountFrom(item, index) == 0 ? FluidResource.EMPTY : FluidResource.of(BiogasContent.BIOGAS.get());
    }

    @Override
    protected int getAmountFrom(ItemResource item, int index) {
        return item.getOrDefault(EquipmentData.FUEL.get(), 0);
    }

    @Override
    protected int getCapacity(int index, FluidResource resource) { return FuelFlightItem.CAPACITY; }

    @Override
    public boolean isValid(int index, FluidResource resource) {
        return resource.equals(FluidResource.of(BiogasContent.BIOGAS.get()));
    }

    @Override
    protected ItemResource update(ItemResource item, int index, FluidResource resource, int amount) {
        return item.with(EquipmentData.FUEL.get(), amount);
    }
}
