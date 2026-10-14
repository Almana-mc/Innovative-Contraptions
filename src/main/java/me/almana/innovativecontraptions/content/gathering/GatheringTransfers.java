package me.almana.innovativecontraptions.content.gathering;

import me.almana.innovativecontraptions.content.processing.DirectionalUpgradeItem;
import me.almana.innovativecontraptions.content.processing.ProcessingFilter;
import me.almana.innovativecontraptions.registry.ICItems;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.VanillaContainerWrapper;
import net.neoforged.neoforge.transfer.item.WorldlyContainerWrapper;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public final class GatheringTransfers {
    public static void drop(GatheringBlockEntity machine, ItemStack stack) {
        var buffer = new SimpleContainer(stack.copy());
        var source = VanillaContainerWrapper.of(buffer);
        for (Direction direction : new Direction[]{Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.DOWN}) {
            var pos = machine.getBlockPos().relative(direction);
            if (!machine.getLevel().hasChunkAt(pos)) continue;
            ResourceHandlerUtil.move(source, machine.getLevel().getCapability(Capabilities.Item.BLOCK, pos, direction.getOpposite()), item -> true, stack.getCount(), null);
            if (buffer.isEmpty()) return;
        }
        ItemStack rest = buffer.getItem(0);
        if (machine.buffered()) store(machine, rest);
        if (rest.isEmpty()) return;
        var pos = machine.getBlockPos();
        Containers.dropItemStack(machine.getLevel(), pos.getX() + .5, pos.getY() + 1, pos.getZ() + .5, rest);
    }
    private static void store(GatheringBlockEntity machine, ItemStack rest) {
        for (int pass = 0; pass < 2; pass++) {
            for (int slot = GatheringBlockEntity.FILTER_START; slot < GatheringBlockEntity.OUTPUT && !rest.isEmpty(); slot++) {
                ItemStack held = machine.getItem(slot);
                if (pass == 1 && held.isEmpty()) machine.setItem(slot, rest.split(rest.getCount()));
                else if (pass == 0 && ItemStack.isSameItemSameComponents(held, rest)) {
                    int moved = Math.min(rest.getCount(), held.getMaxStackSize() - held.getCount());
                    held.grow(moved); rest.shrink(moved);
                }
            }
        }
    }
    public static void fillContainer(GatheringBlockEntity machine) {
        var input = machine.getItem(0);
        if (input.isEmpty() || machine.fluidAmount() == 0) return;
        var access = ItemAccess.forHandlerIndexStrict(VanillaContainerWrapper.of(new SimpleContainer(input.copyWithCount(1))), 0);
        var destination = access.getCapability(Capabilities.Fluid.ITEM);
        if (destination == null) return;
        try (var transaction = Transaction.openRoot()) {
            int moved = ResourceHandlerUtil.move(machine.tank, destination, fluid -> true, 1000, transaction);
            if (moved == 0) return;
            ItemStack filled = access.getResource().toStack();
            ItemStack output = machine.getItem(GatheringBlockEntity.OUTPUT);
            if (!output.isEmpty() && (!ItemStack.isSameItemSameComponents(output, filled) || output.getCount() + filled.getCount() > output.getMaxStackSize())) return;
            transaction.commit(); input.shrink(1);
            if (output.isEmpty()) machine.setItem(GatheringBlockEntity.OUTPUT, filled); else output.grow(filled.getCount());
        }
    }
    public static boolean validUpgrade(GatheringBlockEntity machine, ItemStack stack) {
        if (!machine.advanced() && !machine.pump()) return stack.is(ICItems.PULLING_UPGRADE.get()) || stack.is(ICItems.EJECTOR_UPGRADE.get())
                || stack.is(ICItems.ADVANCED_PULLING_UPGRADE.get()) || stack.is(ICItems.ADVANCED_EJECTOR_UPGRADE.get());
        if (stack.is(ICItems.TRANSFORMER_UPGRADE.get())) return true;
        if (stack.is(ICItems.REDSTONE_INVERTER_UPGRADE.get())) return !machine.pump();
        if (machine.advanced()) return stack.is(ICItems.OVERCLOCKER_UPGRADE.get());
        if (!machine.pump()) return false;
        return stack.is(ICItems.OVERCLOCKER_UPGRADE.get()) || stack.is(ICItems.ENERGY_STORAGE_UPGRADE.get())
                || stack.is(ICItems.EJECTOR_UPGRADE.get()) || stack.is(ICItems.PULLING_UPGRADE.get()) || stack.is(ICItems.FLUID_EJECTOR_UPGRADE.get())
                || stack.is(ICItems.ADVANCED_PULLING_UPGRADE.get()) || stack.is(ICItems.ADVANCED_EJECTOR_UPGRADE.get());
    }
    public static void upgrades(GatheringBlockEntity machine) {
        if (machine.advanced() || machine.terrain()) return;
        for (int slot = 4; slot < 8; slot++) {
            ItemStack stack = machine.getItem(slot);
            boolean eject = stack.is(ICItems.EJECTOR_UPGRADE.get()) || stack.is(ICItems.ADVANCED_EJECTOR_UPGRADE.get());
            boolean pull = stack.is(ICItems.PULLING_UPGRADE.get()) || stack.is(ICItems.ADVANCED_PULLING_UPGRADE.get()), fluid = stack.is(ICItems.FLUID_EJECTOR_UPGRADE.get());
            if (!eject && !pull && !fluid) continue;
            int amount = 1 << (2 * Math.min(4, stack.getCount() - 1));
            for (Direction side : Direction.values()) {
                if (!DirectionalUpgradeItem.allows(stack, side)) continue;
                var pos = machine.getBlockPos().relative(side);
                if (!machine.getLevel().hasChunkAt(pos)) continue;
                if (fluid) ResourceHandlerUtil.move(machine.tank, machine.getLevel().getCapability(Capabilities.Fluid.BLOCK, pos, side.getOpposite()), resource -> true, amount * 50, null);
                else {
                    var adjacent = machine.getLevel().getCapability(Capabilities.Item.BLOCK, pos, side.getOpposite());
                    var own = new WorldlyContainerWrapper(machine, pull ? Direction.UP : Direction.DOWN);
                    ResourceHandlerUtil.move(pull ? adjacent : own, pull ? own : adjacent,
                            item -> ProcessingFilter.accepts(stack, item.toStack()), amount, null);
                }
            }
        }
    }
    private GatheringTransfers() {}
}
