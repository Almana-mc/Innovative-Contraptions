package me.almana.innovativecontraptions.compat.transfer.access;

import me.almana.innovativecontraptions.compat.transfer.RangedResourceHandler;
import me.almana.innovativecontraptions.compat.transfer.ResourceHandler;
import me.almana.innovativecontraptions.compat.transfer.TransferPreconditions;
import me.almana.innovativecontraptions.compat.transfer.item.ItemResource;
import me.almana.innovativecontraptions.compat.transfer.item.PlayerInventoryWrapper;
import me.almana.innovativecontraptions.compat.transfer.transaction.Transaction;
import me.almana.innovativecontraptions.compat.transfer.transaction.TransactionContext;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.ItemCapability;
import org.jetbrains.annotations.Nullable;

public interface ItemAccess {
    static ItemAccess forPlayerInteraction(Player player, InteractionHand hand) {
        if (player.hasInfiniteMaterials()) {
            ItemStack contents = player.getItemInHand(hand);
            return new InfiniteMaterialsItemAccess(player, ItemResource.of(contents), contents.getCount());
        }
        return forPlayerSlot(player, switch (hand) {
            case MAIN_HAND -> player.getInventory().selected;
            case OFF_HAND -> Inventory.SLOT_OFFHAND;
        });
    }

    static ItemAccess forPlayerSlot(Player player, int slot) {
        var inventoryWrapper = PlayerInventoryWrapper.of(player);
        return new PlayerItemAccess(inventoryWrapper, RangedResourceHandler.ofSingleIndex(inventoryWrapper, slot));
    }

    static ItemAccess forHandlerIndexStrict(ResourceHandler<ItemResource> handler, int index) {
        return new HandlerItemAccess(RangedResourceHandler.ofSingleIndex(handler, index), 0);
    }

    static ItemAccess forStack(ItemStack stack) {
        if (stack.isEmpty()) {
            throw new IllegalArgumentException("Expected stack to be non-empty.");
        }
        return new StackItemAccess(stack);
    }

    default ItemAccess oneByOne() {
        return new OneByOneItemAccess(this);
    }

    @Nullable
    default <T> T getCapability(ItemCapability<T, ItemAccess> capability) {
        return capability.getCapability(getResource().toStack(), this);
    }

    ItemResource getResource();

    int getAmount();

    int insert(ItemResource resource, int amount, TransactionContext transaction);

    int extract(ItemResource resource, int amount, TransactionContext transaction);

    default int exchange(ItemResource newResource, int amount, @Nullable TransactionContext transaction) {
        TransferPreconditions.checkNonEmptyNonNegative(newResource, amount);
        var currentResource = getResource();
        TransferPreconditions.checkNonEmpty(currentResource);
        try (Transaction subTransaction = Transaction.open(transaction)) {
            int extracted = extract(currentResource, amount, subTransaction);
            if (extracted > 0 && insert(newResource, extracted, subTransaction) == extracted) {
                subTransaction.commit();
                return extracted;
            }
        }
        return 0;
    }
}
