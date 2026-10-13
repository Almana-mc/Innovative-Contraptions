package me.almana.innovativecontraptions.compat;

import java.util.function.Function;
import me.almana.innovativecontraptions.compat.transfer.ResourceHandler;
import me.almana.innovativecontraptions.compat.transfer.access.ItemAccess;
import me.almana.innovativecontraptions.compat.transfer.fluid.FluidResource;
import me.almana.innovativecontraptions.compat.transfer.item.VanillaContainerWrapper;
import me.almana.innovativecontraptions.compat.transfer.transaction.Transaction;
import me.almana.innovativecontraptions.compat.transfer.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities.FluidHandler;
import net.neoforged.neoforge.capabilities.Capabilities.ItemHandler;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

public final class CapabilityBridge {
    public static void register(RegisterCapabilitiesEvent event) {
        // Disjoint block sets prevent recursion
        for (Block block : BuiltInRegistries.BLOCK) {
            if (event.isBlockRegistered(Capabilities.Item.BLOCK, block)) event.registerBlock(ItemHandler.BLOCK, CapabilityBridge::exposeItems, block);
            else event.registerBlock(Capabilities.Item.BLOCK, CapabilityBridge::adoptItems, block);
            if (event.isBlockRegistered(Capabilities.Fluid.BLOCK, block)) event.registerBlock(FluidHandler.BLOCK, CapabilityBridge::exposeFluids, block);
            else event.registerBlock(Capabilities.Fluid.BLOCK, CapabilityBridge::adoptFluids, block);
        }
        for (Item item : BuiltInRegistries.ITEM) {
            if (event.isItemRegistered(Capabilities.Fluid.ITEM, item)) event.registerItem(FluidHandler.ITEM, CapabilityBridge::exposeItemFluids, item);
            else event.registerItem(Capabilities.Fluid.ITEM, CapabilityBridge::adoptItemFluids, item);
        }
    }

    static <R> R transact(boolean simulate, Function<TransactionContext, R> operation) {
        // Nests inside reentrant transfers
        try (var transaction = Transaction.open(Transaction.getCurrentOpenedTransaction())) {
            R result = operation.apply(transaction);
            if (!simulate) transaction.commit();
            return result;
        }
    }

    @Nullable
    private static IItemHandler exposeItems(Level level, BlockPos pos, BlockState state, @Nullable BlockEntity entity, @Nullable Direction side) {
        var handler = level.getCapability(Capabilities.Item.BLOCK, pos, state, entity, side);
        return handler == null ? null : new ResourceItemHandler(handler);
    }

    @Nullable
    private static IFluidHandler exposeFluids(Level level, BlockPos pos, BlockState state, @Nullable BlockEntity entity, @Nullable Direction side) {
        var handler = level.getCapability(Capabilities.Fluid.BLOCK, pos, state, entity, side);
        return handler == null ? null : new ResourceFluidHandler(handler);
    }

    @Nullable
    private static IFluidHandlerItem exposeItemFluids(ItemStack stack, @Nullable Void context) {
        var container = new SimpleContainer(stack);
        var handler = ItemAccess.forHandlerIndexStrict(VanillaContainerWrapper.of(container), 0).getCapability(Capabilities.Fluid.ITEM);
        return handler == null ? null : new ResourceFluidHandlerItem(container, handler);
    }

    @Nullable
    private static LegacyItemHandler adoptItems(Level level, BlockPos pos, BlockState state, @Nullable BlockEntity entity, @Nullable Direction side) {
        var handler = level.getCapability(ItemHandler.BLOCK, pos, state, entity, side);
        return handler == null ? null : new LegacyItemHandler(handler);
    }

    @Nullable
    private static LegacyFluidHandler adoptFluids(Level level, BlockPos pos, BlockState state, @Nullable BlockEntity entity, @Nullable Direction side) {
        var handler = level.getCapability(FluidHandler.BLOCK, pos, state, entity, side);
        return handler == null ? null : new LegacyFluidHandler(handler);
    }

    @Nullable
    private static ResourceHandler<FluidResource> adoptItemFluids(ItemStack stack, ItemAccess access) {
        return stack.getCapability(FluidHandler.ITEM) == null ? null : new LegacyFluidHandlerItem(access);
    }

    private CapabilityBridge() {}
}
