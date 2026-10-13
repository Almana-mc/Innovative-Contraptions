package me.almana.innovativecontraptions.content.bioenergy;

import me.almana.innovativecontraptions.content.agriculture.FarmingTransfers;
import me.almana.innovativecontraptions.content.fluid.BiogasContent;
import me.almana.innovativecontraptions.content.processing.ProcessingAutomation;
import me.almana.innovativecontraptions.content.thermal.HeatSource;
import me.almana.innovativecontraptions.content.thermal.ThermalInventory;
import me.almana.innovativecontraptions.registry.ICItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SingleRecipeInput;
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
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public final class BioDigester extends ThermalInventory {
    public static final int INPUT_CAPACITY = 10000, OUTPUT_CAPACITY = 2000, FERTILIZER_INTERVAL = 500, MAX_HEAT = 100;
    private int heat, fermented, status;
    private final FluidStacksResourceHandler tanks = new FluidStacksResourceHandler(2, INPUT_CAPACITY) {
        @Override public boolean isValid(int index, FluidResource resource) { return resource.getFluid() == (index == 0 ? BiogasContent.BIOMASS.get() : BiogasContent.BIOGAS.get()); }
        @Override protected int getCapacity(int index, FluidResource resource) { return index == 0 ? INPUT_CAPACITY : OUTPUT_CAPACITY; }
        @Override protected void onContentsChanged(int index, FluidStack previous) { setChanged(); }
    };
    private final ResourceHandler<FluidResource> port = new DelegatingResourceHandler<>(tanks) {
        @Override public int insert(int index, FluidResource resource, int amount, TransactionContext tx) { return index == 0 ? tanks.insert(index, resource, amount, tx) : 0; }
        @Override public int insert(FluidResource resource, int amount, TransactionContext tx) { return insert(0, resource, amount, tx); }
        @Override public int extract(int index, FluidResource resource, int amount, TransactionContext tx) { return index == 1 ? tanks.extract(index, resource, amount, tx) : 0; }
        @Override public int extract(FluidResource resource, int amount, TransactionContext tx) { return extract(1, resource, amount, tx); }
    };
    public BioDigester(BlockPos pos, BlockState state) { super(BioenergyContent.DIGESTER.get(), pos, state, 7); }
    public ResourceHandler<FluidResource> fluids() { return port; }
    public int inputAmount() { return tanks.getAmountAsInt(0); }
    public int outputAmount() { return tanks.getAmountAsInt(1); }
    public int heat() { return heat; }
    public int fermented() { return fermented; }
    boolean active() { return status == 3; }
    @Override public void tick() {
        BioenergyTransfers.container(this, RangedResourceHandler.ofSingleIndex(port, 0), 0, 1, false);
        BioenergyTransfers.container(this, RangedResourceHandler.ofSingleIndex(port, 1), 2, 3, true);
        for (int slot = 5; slot < 7; slot++) ProcessingAutomation.transfer(this, this, items.get(slot), RangedResourceHandler.ofSingleIndex(port, 0), RangedResourceHandler.ofSingleIndex(port, 1));
        var match = ((ServerLevel) level).getServer().getRecipeManager().getRecipes().stream().map(holder -> holder.value())
                .filter(value -> value instanceof BioenergyRecipe recipe && recipe.fermentation() && recipe.input().test(new ItemStack(ICItems.BIOMASS_CELL.get())))
                .map(value -> (BioenergyRecipe) value).findFirst();
        if (match.isEmpty()) { status = 0; return; }
        BioenergyRecipe recipe = match.get();
        if (inputAmount() < recipe.inputMb()) { status = 0; return; }
        if (outputAmount() + recipe.outputMb() > OUTPUT_CAPACITY || !fits(4, new ItemStack(ICItems.FERTILIZER.get()))) { status = 2; return; }
        Direction facing = getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        int supplied = level.getBlockEntity(worldPosition.relative(facing)) instanceof HeatSource heater
                ? heater.supplyHeat(facing.getOpposite(), Math.min(MAX_HEAT, Math.max(0, recipe.energy() - heat))) : 0;
        heat += supplied;
        status = supplied > 0 ? 3 : 1;
        if (heat >= recipe.energy()) {
            heat -= recipe.energy();
            tanks.set(0, FluidResource.of(BiogasContent.BIOMASS.get()), inputAmount() - recipe.inputMb());
            tanks.set(1, FluidResource.of(BiogasContent.BIOGAS.get()), outputAmount() + recipe.outputMb());
            fermented += recipe.inputMb();
            if (fermented >= FERTILIZER_INTERVAL) { insert(4, new ItemStack(ICItems.FERTILIZER.get())); fermented -= FERTILIZER_INTERVAL; }
        }
        if (supplied > 0) setChanged();
    }
    @Override public boolean canPlaceItem(int slot, ItemStack stack) {
        if (slot == 0) return FarmingTransfers.contains(stack, BiogasContent.BIOMASS.get());
        if (slot == 2) return stack.is(ICItems.EMPTY_CELL.get()) || stack.is(ICItems.BIOGAS_CELL.get());
        return slot >= 5 && (stack.is(ICItems.EJECTOR_UPGRADE.get()) || stack.is(ICItems.PULLING_UPGRADE.get())
                || stack.is(ICItems.FLUID_EJECTOR_UPGRADE.get()) || stack.is(ICItems.FLUID_PULLING_UPGRADE.get())
                || stack.is(ICItems.ADVANCED_EJECTOR_UPGRADE.get()) || stack.is(ICItems.ADVANCED_PULLING_UPGRADE.get()));
    }
    @Override public int[] getSlotsForFace(Direction side) { return side == Direction.DOWN ? new int[]{1, 3, 4} : new int[]{0, 2}; }
    @Override public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) { return slot == 1 || slot == 3 || slot == 4; }
    @Override public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) { return new BioenergyMenu(id, inventory, this); }
    @Override protected int displayValue(int index) { return switch (index) { case 0 -> heat; case 1 -> fermented; case 2 -> inputAmount(); case 3 -> outputAmount(); case 4 -> status; default -> 0; }; }
    @Override protected void receiveDisplayValue(int index, int value) {}
    @Override protected void saveAdditional(ValueOutput output) { super.saveAdditional(output); tanks.serialize(output.child("tanks")); output.putInt("heat", heat); output.putInt("fermented", fermented); }
    @Override protected void loadAdditional(ValueInput input) { super.loadAdditional(input); tanks.deserialize(input.childOrEmpty("tanks")); heat = input.getIntOr("heat", 0); fermented = input.getIntOr("fermented", 0); }
}

