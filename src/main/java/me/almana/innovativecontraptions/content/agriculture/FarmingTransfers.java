package me.almana.innovativecontraptions.content.agriculture;

import me.almana.innovativecontraptions.content.processing.ProcessingAutomation;
import me.almana.innovativecontraptions.registry.ICItems;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.VanillaContainerWrapper;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public final class FarmingTransfers {
    public static boolean contains(ItemStack stack, Fluid fluid) {
        if (stack.isEmpty()) return false;
        var handler = ItemAccess.forStack(stack.copyWithCount(1)).getCapability(Capabilities.Fluid.ITEM);
        if (handler == null) return false;
        for (int index = 0; index < handler.size(); index++) if (handler.getResource(index).getFluid() == fluid && handler.getAmountAsInt(index) > 0) return true;
        return false;
    }
    public static void emptyContainer(FarmingMachineBlockEntity machine, int input, int output, int tank) {
        ItemStack stack = machine.getItem(input);
        if (stack.isEmpty()) return;
        var working = new SimpleContainer(stack.copyWithCount(1));
        var access = ItemAccess.forHandlerIndexStrict(VanillaContainerWrapper.of(working), 0);
        var handler = access.getCapability(Capabilities.Fluid.ITEM);
        if (handler == null) return;
        try (var transaction = Transaction.openRoot()) {
            int moved = ResourceHandlerUtil.move(handler, machine.tanks(), fluid -> machine.tanks().isValid(tank, fluid), 1000, transaction);
            if (moved == 0) return;
            ItemStack remainder = access.getResource().toStack();
            ItemStack existing = machine.getItem(output);
            if (!remainder.isEmpty() && !existing.isEmpty() && (!ItemStack.isSameItemSameComponents(existing, remainder)
                    || existing.getCount() + remainder.getCount() > existing.getMaxStackSize())) return;
            transaction.commit();
            stack.shrink(1);
            if (existing.isEmpty()) machine.setItem(output, remainder); else existing.grow(remainder.getCount());
            machine.setChanged();
        }
    }
    public static boolean validUpgrade(ItemStack stack, boolean matron) {
        return stack.is(ICItems.TRANSFORMER_UPGRADE.get()) || stack.is(ICItems.ENERGY_STORAGE_UPGRADE.get())
                || (matron ? stack.is(ICItems.PULLING_UPGRADE.get()) || stack.is(ICItems.ADVANCED_PULLING_UPGRADE.get()) || stack.is(ICItems.FLUID_PULLING_UPGRADE.get())
                : stack.is(ICItems.EJECTOR_UPGRADE.get()) || stack.is(ICItems.ADVANCED_EJECTOR_UPGRADE.get()));
    }
    public static void upgrades(FarmingMachineBlockEntity machine) {
        for (int slot = machine.upgradeStart(); slot < machine.upgradeStart() + 4; slot++) transfer(machine, machine.getItem(slot));
    }
    private static void transfer(FarmingMachineBlockEntity machine, ItemStack upgrade) {
        ProcessingAutomation.transfer(machine, machine, upgrade, machine.tanks(), null);
    }
    private FarmingTransfers() {}
}
