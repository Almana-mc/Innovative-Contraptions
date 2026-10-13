package me.almana.innovativecontraptions.content.thermal;

import java.util.stream.IntStream;
import me.almana.innovativecontraptions.content.bioenergy.BioenergyTransfers;
import me.almana.innovativecontraptions.content.fluid.CoolantContent;
import me.almana.innovativecontraptions.content.processing.ProcessingAutomation;
import me.almana.innovativecontraptions.registry.ICItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.DelegatingResourceHandler;
import net.neoforged.neoforge.transfer.RangedResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public final class LiquidHeatExchangerBlockEntity extends ThermalInventory implements HeatSource {
    public static final int CONDUCTORS = 10, HOT_IN = 10, HOT_OUT = 11, EMPTY_IN = 12, COOLANT_OUT = 13, UPGRADES = 14, SLOTS = 17;
    public static final int TANK = 2_000, HU_PER_CONDUCTOR = 10, HU_PER_MB = 20;
    private static final int[] ALL_SLOTS = IntStream.range(0, SLOTS).toArray();
    private final FluidStacksResourceHandler tanks = new FluidStacksResourceHandler(2, TANK) {
        @Override public boolean isValid(int index, FluidResource resource) {
            return resource.getFluid() == (index == 0 ? CoolantContent.HOT_COOLANT.get() : CoolantContent.COOLANT.get());
        }
        @Override protected void onContentsChanged(int index, FluidStack previous) { setChanged(); }
    };
    private final ResourceHandler<FluidResource> port = new DelegatingResourceHandler<>(tanks) {
        @Override public int insert(int index, FluidResource resource, int amount, TransactionContext tx) { return index == 0 ? tanks.insert(index, resource, amount, tx) : 0; }
        @Override public int insert(FluidResource resource, int amount, TransactionContext tx) { return insert(0, resource, amount, tx); }
        @Override public int extract(int index, FluidResource resource, int amount, TransactionContext tx) { return index == 1 ? tanks.extract(index, resource, amount, tx) : 0; }
        @Override public int extract(FluidResource resource, int amount, TransactionContext tx) { return extract(1, resource, amount, tx); }
    };
    private int buffer, sent;
    private long sentTick = Long.MIN_VALUE;

    public LiquidHeatExchangerBlockEntity(BlockPos pos, BlockState state) {
        super(ThermalContent.EXCHANGER.get(), pos, state, SLOTS);
    }

    public ResourceHandler<FluidResource> fluids() { return port; }
    boolean active() { return buffer > 0; }

    public int conductors() {
        int count = 0;
        for (int slot = 0; slot < CONDUCTORS; slot++) if (items.get(slot).is(ICItems.HEAT_CONDUCTOR.get())) count++;
        return count;
    }

    @Override public void tick() {
        var hot = RangedResourceHandler.ofSingleIndex(port, 0);
        var cold = RangedResourceHandler.ofSingleIndex(port, 1);
        BioenergyTransfers.container(this, hot, HOT_IN, HOT_OUT, false);
        BioenergyTransfers.container(this, cold, EMPTY_IN, COOLANT_OUT, true);
        for (int slot = UPGRADES; slot < SLOTS; slot++) ProcessingAutomation.transfer(this, this, items.get(slot), hot, cold);
        int room = conductors() * HU_PER_CONDUCTOR - buffer;
        int mb = Math.min(room / HU_PER_MB, Math.min(tanks.getAmountAsInt(0), TANK - tanks.getAmountAsInt(1)));
        if (mb <= 0) return;
        try (var transaction = Transaction.openRoot()) {
            tanks.extract(0, FluidResource.of(CoolantContent.HOT_COOLANT.get()), mb, transaction);
            tanks.insert(1, FluidResource.of(CoolantContent.COOLANT.get()), mb, transaction);
            transaction.commit();
        }
        buffer += mb * HU_PER_MB;
        setChanged();
    }

    @Override public int supplyHeat(Direction side, int requested) {
        if (side != getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING) || requested <= 0) return 0;
        int supplied = Math.min(requested, buffer);
        buffer -= supplied;
        long now = level.getGameTime();
        if (now != sentTick) {
            sent = 0;
            sentTick = now;
        }
        sent += supplied;
        if (supplied > 0) setChanged();
        return supplied;
    }

    private static boolean upgrade(ItemStack stack) {
        return stack.is(ICItems.EJECTOR_UPGRADE.get()) || stack.is(ICItems.ADVANCED_EJECTOR_UPGRADE.get())
                || stack.is(ICItems.PULLING_UPGRADE.get()) || stack.is(ICItems.ADVANCED_PULLING_UPGRADE.get())
                || stack.is(ICItems.FLUID_EJECTOR_UPGRADE.get()) || stack.is(ICItems.FLUID_PULLING_UPGRADE.get());
    }

    @Override public boolean canPlaceItem(int slot, ItemStack stack) {
        if (slot < CONDUCTORS) return items.get(slot).isEmpty() && stack.is(ICItems.HEAT_CONDUCTOR.get());
        if (slot == HOT_IN) return stack.is(ICItems.HOT_COOLANT_CELL.get());
        if (slot == EMPTY_IN) return stack.is(ICItems.EMPTY_CELL.get());
        return slot >= UPGRADES && upgrade(stack);
    }

    @Override public int getMaxStackSize(ItemStack stack) { return stack.is(ICItems.HEAT_CONDUCTOR.get()) ? 1 : stack.getMaxStackSize(); }
    @Override public int[] getSlotsForFace(Direction side) { return ALL_SLOTS; }
    @Override public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) { return slot == HOT_OUT || slot == COOLANT_OUT; }

    @Override protected int displayValue(int index) {
        return switch (index) {
            case 0 -> tanks.getAmountAsInt(0);
            case 1 -> tanks.getAmountAsInt(1);
            case 2 -> conductors();
            case 3 -> level != null && sentTick >= level.getGameTime() - 1 ? sent : 0;
            default -> 0;
        };
    }
    @Override protected void receiveDisplayValue(int index, int value) { }
    @Override public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) { return new HeatExchangerMenu(id, inventory, this); }

    @Override protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        tanks.serialize(output.child("tanks"));
        output.putInt("buffer", buffer);
    }

    @Override protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        tanks.deserialize(input.childOrEmpty("tanks"));
        buffer = input.getIntOr("buffer", 0);
    }
}
