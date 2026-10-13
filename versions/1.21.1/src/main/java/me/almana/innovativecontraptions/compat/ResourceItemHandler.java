package me.almana.innovativecontraptions.compat;

import me.almana.innovativecontraptions.compat.transfer.ResourceHandler;
import me.almana.innovativecontraptions.compat.transfer.item.ItemResource;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

record ResourceItemHandler(ResourceHandler<ItemResource> handler) implements IItemHandler {
    @Override
    public int getSlots() {
        return handler.size();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return handler.getResource(slot).toStack(handler.getAmountAsInt(slot));
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) return ItemStack.EMPTY;
        int leftover = stack.getCount() - CapabilityBridge.transact(simulate, transaction -> handler.insert(slot, ItemResource.of(stack), stack.getCount(), transaction));
        return leftover == 0 ? ItemStack.EMPTY : stack.copyWithCount(leftover);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        var resource = handler.getResource(slot);
        if (resource.isEmpty() || amount <= 0) return ItemStack.EMPTY;
        int limit = Math.min(amount, resource.getMaxStackSize());
        return resource.toStack(CapabilityBridge.transact(simulate, transaction -> handler.extract(slot, resource, limit, transaction)));
    }

    @Override
    public int getSlotLimit(int slot) {
        return handler.getCapacityAsInt(slot, ItemResource.EMPTY);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return !stack.isEmpty() && handler.isValid(slot, ItemResource.of(stack));
    }
}
