package me.almana.innovativecontraptions.content.bioenergy;

import me.almana.innovativecontraptions.content.agriculture.FarmingTransfers;
import me.almana.innovativecontraptions.content.energy.BatteryItem;
import me.almana.innovativecontraptions.content.energy.EuSource;
import me.almana.innovativecontraptions.content.energy.net.EuGrids;
import me.almana.innovativecontraptions.content.fluid.BiogasContent;
import me.almana.innovativecontraptions.content.thermal.ThermalInventory;
import me.almana.innovativecontraptions.registry.ICItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.DelegatingResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public final class LiquidFuelGenerator extends ThermalInventory implements EuSource {
    public static final int CAPACITY = 32000, TANK_CAPACITY = 10000;
    private int energy, fuelCredit, rate, output;
    private final FluidStacksResourceHandler tank = new FluidStacksResourceHandler(1, TANK_CAPACITY) {
        @Override public boolean isValid(int index, FluidResource resource) { return resource.getFluid() == BiogasContent.BIOGAS.get() || resource.getFluid() == BiogasContent.BIOMASS.get(); }
        @Override protected void onContentsChanged(int index, FluidStack previous) { setChanged(); }
    };
    private final ResourceHandler<FluidResource> port = new DelegatingResourceHandler<>(tank) {
        @Override public int extract(int index, FluidResource resource, int amount, TransactionContext transaction) { return 0; }
        @Override public int extract(FluidResource resource, int amount, TransactionContext transaction) { return 0; }
    };
    public LiquidFuelGenerator(BlockPos pos, BlockState state) { super(BioenergyContent.GENERATOR.get(), pos, state, 3); }
    public ResourceHandler<FluidResource> fluids() { return port; }
    public int energy() { return energy; }
    public int fuelCredit() { return fuelCredit; }
    boolean active() { return fuelCredit > 0; }
    @Override public void tick() {
        int charged = BatteryItem.receiveEnergy(items.get(2), Math.min(energy, 32), 1, false);
        energy -= charged;
        BioenergyTransfers.container(this, tank, 0, 1, false);
        output = 0;
        if (fuelCredit == 0 && tank.getAmountAsInt(0) > 0) loadFuel();
        if (fuelCredit > 0 && energy + Math.min(rate, fuelCredit) <= CAPACITY) {
            output = Math.min(rate, fuelCredit);
            fuelCredit -= output;
            energy += output;
        }
        if (charged > 0 || output > 0) setChanged();
    }
    private void loadFuel() {
        ItemStack cell = new ItemStack(tank.getResource(0).is(BiogasContent.BIOGAS.get()) ? ICItems.BIOGAS_CELL.get() : ICItems.BIOMASS_CELL.get());
        var recipes = ((ServerLevel) level).getServer().getRecipeManager().getRecipes();
        for (var holder : recipes) {
            if (!(holder.value() instanceof BioenergyRecipe recipe) || recipe.fermentation() || !recipe.input().test(cell)) continue;
            int nextRate = recipe.outputMb();
            if (energy + nextRate > CAPACITY) return;
            int amount = Math.min(tank.getAmountAsInt(0), Math.max(1, (nextRate + recipe.energy() - 1) / recipe.energy()));
            fuelCredit = amount * recipe.energy();
            rate = nextRate;
            tank.set(0, tank.getResource(0), tank.getAmountAsInt(0) - amount);
            setChanged();
            return;
        }
    }
    @Override public int sourceVoltage() { return 32; }
    @Override public int availableEu() { return energy; }
    @Override public int extractEu(int amount) { int extracted = Math.min(energy, Math.max(0, amount)); energy -= extracted; if (extracted > 0) setChanged(); return extracted; }
    @Override public boolean canPlaceItem(int slot, ItemStack stack) {
        return slot == 0 ? FarmingTransfers.contains(stack, BiogasContent.BIOGAS.get())
                || FarmingTransfers.contains(stack, BiogasContent.BIOMASS.get())
                : slot == 2 && stack.getItem() instanceof BatteryItem battery && battery.rechargeable() && battery.tier().volts <= 32;
    }
    @Override public int[] getSlotsForFace(Direction side) { return side == Direction.DOWN ? new int[]{1, 2} : new int[]{0, 2}; }
    @Override public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) { return slot == 1 || slot == 2; }
    @Override public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) { return new BioenergyMenu(id, inventory, this); }
    @Override protected int displayValue(int index) { return switch (index) { case 0 -> energy; case 1 -> fuelCredit; case 2 -> tank.getAmountAsInt(0); case 3 -> output; case 4 -> output > 0 ? 3 : energy == CAPACITY ? 2 : 0; default -> 0; }; }
    @Override protected void receiveDisplayValue(int index, int value) {}
    @Override public void onLoad() { super.onLoad(); if (!level.isClientSide()) EuGrids.markDirty(level, worldPosition); }
    @Override public void setRemoved() { if (level != null && !level.isClientSide()) EuGrids.markDirty(level, worldPosition); super.setRemoved(); }
    @Override protected void saveAdditional(ValueOutput output) { super.saveAdditional(output); tank.serialize(output.child("tank")); output.putInt("energy", energy); output.putInt("fuel_credit", fuelCredit); output.putInt("rate", rate); }
    @Override protected void loadAdditional(ValueInput input) { super.loadAdditional(input); tank.deserialize(input.childOrEmpty("tank")); energy = input.getIntOr("energy", 0); fuelCredit = input.getIntOr("fuel_credit", 0); rate = input.getIntOr("rate", 0); }
}
