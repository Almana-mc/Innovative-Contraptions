package me.almana.innovativecontraptions.content.reactor;

import javax.annotation.Nullable;
import me.almana.innovativecontraptions.content.processing.ProcessingAutomation;
import me.almana.innovativecontraptions.content.thermal.ThermalInventory;
import me.almana.innovativecontraptions.registry.ICItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

public final class ReactorFluidPortBlockEntity extends ThermalInventory {
    private static final int[] NO_SLOTS = {};
    @Nullable private BlockPos reactorPos;

    public ReactorFluidPortBlockEntity(BlockPos pos, BlockState state) {
        super(ReactorContent.FLUID_PORT.get(), pos, state, 1);
    }

    @Nullable
    public NuclearReactorBlockEntity reactor() {
        if (reactorPos != null && level.getBlockEntity(reactorPos) instanceof NuclearReactorBlockEntity cached && cached.fluidMode()) return cached;
        NuclearReactorBlockEntity found = ReactorStructure.enclosing(level, worldPosition);
        reactorPos = found == null ? null : found.getBlockPos();
        return found;
    }

    @Nullable
    public ResourceHandler<FluidResource> reactorFluids() {
        NuclearReactorBlockEntity reactor = reactor();
        return reactor == null ? null : reactor.fluids();
    }

    @Override public void tick() {
        NuclearReactorBlockEntity reactor = items.get(0).isEmpty() ? null : reactor();
        if (reactor != null) ProcessingAutomation.transfer(this, this, items.get(0), reactor.coolantInput(), reactor.hotCoolantOutput());
    }

    void swap(Player player) {
        ItemStack held = player.getMainHandItem();
        if (items.get(0).isEmpty() && canPlaceItem(0, held)) {
            items.set(0, held.copy());
            player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            setChanged();
        } else if (held.isEmpty() && !items.get(0).isEmpty()) {
            player.setItemInHand(InteractionHand.MAIN_HAND, items.get(0));
            items.set(0, ItemStack.EMPTY);
            setChanged();
        }
    }

    @Override public boolean canPlaceItem(int slot, ItemStack stack) {
        return stack.is(ICItems.FLUID_EJECTOR_UPGRADE.get()) || stack.is(ICItems.FLUID_PULLING_UPGRADE.get());
    }
    @Override public int[] getSlotsForFace(Direction side) { return NO_SLOTS; }
    @Override public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) { return false; }
    @Override protected int displayValue(int index) { return 0; }
    @Override protected void receiveDisplayValue(int index, int value) { }
}
