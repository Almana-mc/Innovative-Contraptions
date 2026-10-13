package me.almana.innovativecontraptions.compat.transfer.item;

import java.util.ArrayDeque;
import java.util.Deque;
import me.almana.innovativecontraptions.compat.transfer.RangedResourceHandler;
import me.almana.innovativecontraptions.compat.transfer.ResourceHandler;
import me.almana.innovativecontraptions.compat.transfer.ResourceHandlerUtil;
import me.almana.innovativecontraptions.compat.transfer.TransferPreconditions;
import me.almana.innovativecontraptions.compat.transfer.transaction.SnapshotJournal;
import me.almana.innovativecontraptions.compat.transfer.transaction.TransactionContext;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.CommonHooks;

public final class PlayerInventoryWrapper extends VanillaContainerWrapper {
    public static PlayerInventoryWrapper of(Player player) {
        return (PlayerInventoryWrapper) VanillaContainerWrapper.of(player.getInventory());
    }

    private final DroppedItems droppedItems = new DroppedItems();
    private final Inventory inventory;

    PlayerInventoryWrapper(Inventory inventory) {
        super(inventory);
        this.inventory = inventory;
    }

    // armor slot rules omitted
    @Override
    void resize() {
        size = Inventory.SLOT_OFFHAND + 1;
        while (slotWrappers.size() < size) {
            slotWrappers.add(new SlotWrapper(slotWrappers.size()));
        }
    }

    @Override
    void onRootCommit() {
        super.onRootCommit();
        if (!inventory.player.level().isClientSide()) {
            inventory.player.containerMenu.broadcastChanges();
        }
    }

    public ResourceHandler<ItemResource> getHandSlot(InteractionHand hand) {
        return switch (hand) {
            case MAIN_HAND -> {
                if (!Inventory.isHotbarSlot(inventory.selected)) throw new RuntimeException("Unexpected player selected slot: " + inventory.selected);
                yield getSlotWrapper(inventory.selected);
            }
            case OFF_HAND -> getSlotWrapper(Inventory.SLOT_OFFHAND);
        };
    }

    public ResourceHandler<ItemResource> getMainSlots() {
        return RangedResourceHandler.of(this, 0, Inventory.INVENTORY_SIZE);
    }

    @Override
    public int insert(ItemResource resource, int amount, TransactionContext transaction) {
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
        int inserted = 0;
        // Hands first, then main stacking
        for (InteractionHand hand : InteractionHand.values()) {
            var handSlot = getHandSlot(hand);
            if (handSlot.getResource(0).equals(resource)) {
                inserted += handSlot.insert(resource, amount - inserted, transaction);
                if (inserted == amount) return inserted;
            }
        }
        return inserted + ResourceHandlerUtil.insertStacking(getMainSlots(), resource, amount - inserted, transaction);
    }

    public void placeItemBackInInventory(ItemResource resource, int amount, TransactionContext transaction) {
        int inserted = insert(resource, amount, transaction);
        if (inserted < amount) drop(resource, amount - inserted, transaction);
    }

    private void drop(ItemResource resource, int amount, TransactionContext transaction) {
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
        if (amount == 0) return;
        if (!inventory.player.level().isClientSide()) {
            droppedItems.addDrop(resource, amount, transaction);
        }
    }

    @Override
    public String toString() {
        return "PlayerInventoryWrapper{player=%s}".formatted(inventory.player);
    }

    private class DroppedItems extends SnapshotJournal<Integer> {
        final Deque<DropInfo> entries = new ArrayDeque<>();

        void addDrop(ItemResource resource, int amount, TransactionContext transaction) {
            updateSnapshots(transaction);
            entries.add(new DropInfo(resource, amount));
        }

        @Override
        protected Integer createSnapshot() {
            return entries.size();
        }

        @Override
        protected void revertToSnapshot(Integer snapshot) {
            while (entries.size() > snapshot) {
                entries.removeLast();
            }
        }

        @Override
        protected void onRootCommit(Integer originalState) {
            // Drops may enqueue more drops
            while (!entries.isEmpty()) {
                DropInfo dropInfo = entries.removeFirst();
                int remainder = dropInfo.amount;
                int maxStackSize = dropInfo.resource.getMaxStackSize();
                while (remainder > 0) {
                    int dropped = Math.min(maxStackSize, remainder);
                    CommonHooks.onPlayerTossEvent(inventory.player, dropInfo.resource.toStack(dropped), false);
                    remainder -= dropped;
                }
            }
        }

        private record DropInfo(ItemResource resource, int amount) {}
    }
}
