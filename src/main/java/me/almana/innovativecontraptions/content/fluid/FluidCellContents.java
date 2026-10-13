package me.almana.innovativecontraptions.content.fluid;

import me.almana.innovativecontraptions.registry.ICItems;
import me.almana.innovativecontraptions.registry.ICDataComponents;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.transfer.ItemAccessResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;

public final class FluidCellContents extends ItemAccessResourceHandler<FluidResource> {
    public static final int CAPACITY = 1000;

    public FluidCellContents(ItemAccess access) {
        super(access, 1);
    }

    @Override
    protected FluidResource getResourceFrom(ItemResource item, int index) {
        return item.getItem() instanceof FluidCellItem cell ? FluidResource.of(cell.fluid()) : FluidResource.EMPTY;
    }

    @Override
    protected int getAmountFrom(ItemResource item, int index) {
        return getResourceFrom(item, index).isEmpty() ? 0 : item.getOrDefault(ICDataComponents.FLUID_AMOUNT.get(), CAPACITY);
    }

    @Override
    public boolean isValid(int index, FluidResource resource) {
        return itemAccess.getResource().getItem() instanceof FluidCellItem
                && ICItems.ITEMS.getEntries().stream().anyMatch(entry -> entry.get() instanceof FluidCellItem cell && cell.fluid() == resource.getFluid());
    }

    @Override
    protected int getCapacity(int index, FluidResource resource) {
        return CAPACITY;
    }

    @Override
    protected ItemResource update(ItemResource item, int index, FluidResource resource, int amount) {
        if (amount == 0) return ItemResource.of(ICItems.EMPTY_CELL.get());
        for (var entry : ICItems.ITEMS.getEntries()) {
            if (entry.get() instanceof FluidCellItem cell && cell.fluid() == resource.getFluid()) {
                var result = ItemResource.of(cell);
                return amount == CAPACITY ? result : result.with(ICDataComponents.FLUID_AMOUNT.get(), amount);
            }
        }
        return ItemResource.EMPTY;
    }
}
