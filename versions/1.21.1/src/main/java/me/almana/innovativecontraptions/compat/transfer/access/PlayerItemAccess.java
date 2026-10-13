package me.almana.innovativecontraptions.compat.transfer.access;

import me.almana.innovativecontraptions.compat.transfer.ResourceHandler;
import me.almana.innovativecontraptions.compat.transfer.item.ItemResource;
import me.almana.innovativecontraptions.compat.transfer.item.PlayerInventoryWrapper;
import me.almana.innovativecontraptions.compat.transfer.transaction.TransactionContext;

class PlayerItemAccess implements ItemAccess {
    private final PlayerInventoryWrapper inventoryWrapper;
    private final ResourceHandler<ItemResource> slot;

    PlayerItemAccess(PlayerInventoryWrapper inventoryWrapper, ResourceHandler<ItemResource> slot) {
        this.inventoryWrapper = inventoryWrapper;
        this.slot = slot;
    }

    @Override
    public ItemResource getResource() {
        return slot.getResource(0);
    }

    @Override
    public int getAmount() {
        return slot.getAmountAsInt(0);
    }

    @Override
    public int insert(ItemResource resource, int amount, TransactionContext transaction) {
        int inserted = slot.insert(0, resource, amount, transaction);
        if (amount > inserted) {
            inventoryWrapper.placeItemBackInInventory(resource, amount - inserted, transaction);
        }
        // Leftover drops, always accepted
        return amount;
    }

    @Override
    public int extract(ItemResource resource, int amount, TransactionContext transaction) {
        return slot.extract(0, resource, amount, transaction);
    }
}
