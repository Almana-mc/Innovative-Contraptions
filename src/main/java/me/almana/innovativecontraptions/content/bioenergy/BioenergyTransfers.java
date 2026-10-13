package me.almana.innovativecontraptions.content.bioenergy;

import me.almana.innovativecontraptions.content.thermal.ThermalInventory;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.VanillaContainerWrapper;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public final class BioenergyTransfers {
    public static boolean container(ThermalInventory machine, ResourceHandler<FluidResource> tank, int input, int output, boolean filling) {
        ItemStack stack = machine.getItem(input);
        if (stack.isEmpty()) return false;
        var working = new SimpleContainer(stack.copyWithCount(1));
        var access = ItemAccess.forHandlerIndexStrict(VanillaContainerWrapper.of(working), 0);
        var fluid = access.getCapability(Capabilities.Fluid.ITEM);
        if (fluid == null) return false;
        try (var transaction = Transaction.openRoot()) {
            int moved = ResourceHandlerUtil.move(filling ? tank : fluid, filling ? fluid : tank, resource -> true, 1000, transaction);
            if (moved == 0) return false;
            ItemStack remainder = access.getResource().toStack();
            boolean partial = false;
            for (int index = 0; index < fluid.size(); index++) {
                int present = fluid.getAmountAsInt(index);
                partial |= filling ? present > 0 && present < fluid.getCapacityAsInt(index, fluid.getResource(index)) : present > 0;
            }
            if (partial) {
                if (stack.getCount() != 1) return false;
                transaction.commit();
                machine.setItem(input, remainder);
                return true;
            }
            ItemStack target = machine.getItem(output);
            if (!remainder.isEmpty() && !target.isEmpty() && (!ItemStack.isSameItemSameComponents(target, remainder)
                    || target.getCount() + remainder.getCount() > target.getMaxStackSize())) return false;
            transaction.commit();
            stack.shrink(1);
            if (target.isEmpty()) machine.setItem(output, remainder); else target.grow(remainder.getCount());
            machine.setChanged();
            return true;
        }
    }
    private BioenergyTransfers() {}
}
