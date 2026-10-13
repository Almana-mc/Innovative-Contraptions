package me.almana.innovativecontraptions.compat.transfer.access;

import me.almana.innovativecontraptions.compat.transfer.ResourceHandler;
import me.almana.innovativecontraptions.compat.transfer.ResourceHandlerUtil;
import me.almana.innovativecontraptions.compat.transfer.TransferPreconditions;
import me.almana.innovativecontraptions.compat.transfer.item.ItemResource;
import me.almana.innovativecontraptions.compat.transfer.item.PlayerInventoryWrapper;
import me.almana.innovativecontraptions.compat.transfer.transaction.TransactionContext;
import net.minecraft.world.entity.player.Player;

class InfiniteMaterialsItemAccess implements ItemAccess {
    private final ResourceHandler<ItemResource> mainSlots;
    private final ItemResource resource;
    private final int amount;

    InfiniteMaterialsItemAccess(Player player, ItemResource resource, int amount) {
        this.mainSlots = PlayerInventoryWrapper.of(player).getMainSlots();
        this.resource = resource;
        this.amount = amount;
    }

    @Override
    public ItemResource getResource() {
        return resource;
    }

    @Override
    public int getAmount() {
        return amount;
    }

    @Override
    public int insert(ItemResource resource, int amount, TransactionContext transaction) {
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
        // skip items already held
        if (amount > 0 && !ResourceHandlerUtil.contains(mainSlots, resource)) {
            mainSlots.insert(resource, 1, transaction);
        }
        return amount;
    }

    @Override
    public int extract(ItemResource resource, int amount, TransactionContext transaction) {
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
        return amount;
    }
}
