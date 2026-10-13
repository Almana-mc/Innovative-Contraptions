package me.almana.innovativecontraptions.content.processing;

import me.almana.innovativecontraptions.registry.ICItems;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.item.WorldlyContainerWrapper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

public final class ProcessingAutomation {
    public static void tick(ProcessingBlockEntity machine, int upgradeStart) {
        if (upgradeStart < 0) return;
        for (int slot = upgradeStart; slot < machine.getContainerSize(); slot++)
            transfer(machine, machine, machine.getItem(slot), machine.kind() == ProcessingKind.WASHING ? machine.waterTank() : null, null);
    }
    public static void transfer(BlockEntity machine, WorldlyContainer inventory, ItemStack stack,
            ResourceHandler<FluidResource> fluidInput, ResourceHandler<FluidResource> fluidOutput) {
        boolean pulling = stack.is(ICItems.PULLING_UPGRADE.get()) || stack.is(ICItems.ADVANCED_PULLING_UPGRADE.get());
        boolean ejecting = stack.is(ICItems.EJECTOR_UPGRADE.get()) || stack.is(ICItems.ADVANCED_EJECTOR_UPGRADE.get());
        boolean fluidPulling = stack.is(ICItems.FLUID_PULLING_UPGRADE.get());
        boolean fluidEjecting = stack.is(ICItems.FLUID_EJECTOR_UPGRADE.get());
        if (!pulling && !ejecting && !fluidPulling && !fluidEjecting) return;
        int amount = 1 << (2 * Math.min(4, stack.getCount() - 1));
        for (Direction side : Direction.values()) {
            if (!DirectionalUpgradeItem.allows(stack, side)) continue;
            var pos = machine.getBlockPos().relative(side);
            if (!machine.getLevel().hasChunkAt(pos)) continue;
            if (pulling || ejecting) {
                var adjacent = machine.getLevel().getCapability(Capabilities.Item.BLOCK, pos, side.getOpposite());
                var own = new WorldlyContainerWrapper(inventory, pulling ? Direction.UP : Direction.DOWN);
                ResourceHandlerUtil.move(pulling ? adjacent : own, pulling ? own : adjacent,
                        item -> ProcessingFilter.accepts(stack, item.toStack()), amount, null);
            } else {
                var adjacent = machine.getLevel().getCapability(Capabilities.Fluid.BLOCK, pos, side.getOpposite());
                ResourceHandlerUtil.move(fluidPulling ? adjacent : fluidOutput, fluidPulling ? fluidInput : adjacent,
                        fluid -> true, amount * 50, null);
            }
        }
    }
    private ProcessingAutomation() { }
}
