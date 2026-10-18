package me.almana.innovativecontraptions.content.processing;

import me.almana.innovativecontraptions.content.energy.*;
import me.almana.innovativecontraptions.content.energy.net.EuGrids;
import me.almana.innovativecontraptions.content.thermal.ThermalInventory;
import me.almana.innovativecontraptions.registry.ICItems;
import me.almana.innovativecontraptions.registry.ICTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.*;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.VanillaContainerWrapper;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public final class ProcessingCannerBlockEntity extends ThermalInventory implements EuSink {
    public static final int TANK_CAPACITY = 8000, BASE_TICKS = 200, BASE_EU = 4;
    public static final int ADDITIVE = 0, CONTAINER = 1, OUTPUT = 2, BATTERY = 3, UPGRADES = 4;
    private int energy, progress, duration = BASE_TICKS, status, mode, baseTicks = BASE_TICKS, baseEu = BASE_EU;
    private ItemStack previousInput = ItemStack.EMPTY, previousContainer = ItemStack.EMPTY;
    private ProcessingRecipe activeSolid;
    private EnrichmentRecipe activeEnrichment;
    private final FluidStacksResourceHandler tanks = new FluidStacksResourceHandler(2, TANK_CAPACITY) {
        @Override protected void onContentsChanged(int index, FluidStack previous) { setChanged(); }
    };
    private final ResourceHandler<FluidResource> port = new DelegatingResourceHandler<>(tanks) {
        @Override public int insert(int index, FluidResource fluid, int amount, TransactionContext tx) { return index == 0 ? tanks.insert(index, fluid, amount, tx) : 0; }
        @Override public int insert(FluidResource fluid, int amount, TransactionContext tx) { return insert(0, fluid, amount, tx); }
        @Override public int extract(int index, FluidResource fluid, int amount, TransactionContext tx) { return index == 1 ? tanks.extract(index, fluid, amount, tx) : 0; }
        @Override public int extract(FluidResource fluid, int amount, TransactionContext tx) { return extract(1, fluid, amount, tx); }
    };
    public ProcessingCannerBlockEntity(BlockPos pos, BlockState state) { super(ProcessingContent.CANNER.get(), pos, state, 8); }
    public ResourceHandler<FluidResource> fluids() { return port; }
    public FluidStacksResourceHandler tanks() { return tanks; }
    public int upgrades(ItemLike item) { return ProcessingUpgrades.count(this, UPGRADES, item); }
    public int capacity() {
        int clocks = upgrades(ICItems.OVERCLOCKER_UPGRADE.get());
        return (int) Math.min(Integer.MAX_VALUE, 800L + 10000L * upgrades(ICItems.ENERGY_STORAGE_UPGRADE.get())
                + (long) ProcessingUpgrades.ticks(BASE_TICKS, clocks) * ProcessingUpgrades.energy(BASE_EU, clocks));
    }
    public int tier() { return Math.min(5, 1 + upgrades(ICItems.TRANSFORMER_UPGRADE.get())); }
    public int mode() { return mode; }
    public boolean setMode(int next) {
        if (next < 0 || next > 3) return false;
        if (mode != next) { mode = next; progress = 0; setChanged(); }
        return true;
    }
    public boolean swapTanks() {
        if (progress != 0) return false;
        FluidResource first = tanks.getResource(0);
        int amount = tanks.getAmountAsInt(0);
        tanks.set(0, tanks.getResource(1), tanks.getAmountAsInt(1));
        tanks.set(1, first, amount);
        setChanged();
        return true;
    }
    boolean active() { return status == 4; }
    @Override public void tick() {
        discharge();
        for (int slot = UPGRADES; slot < 8; slot++) ProcessingAutomation.transfer(this, this, items.get(slot),
                RangedResourceHandler.ofSingleIndex(port, 0), RangedResourceHandler.ofSingleIndex(port, 1));
        if (!ItemStack.isSameItemSameComponents(previousInput, items.get(ADDITIVE))
                || !ItemStack.isSameItemSameComponents(previousContainer, items.get(CONTAINER))) progress = 0;
        previousInput = items.get(ADDITIVE).copy(); previousContainer = items.get(CONTAINER).copy();
        baseTicks = BASE_TICKS; baseEu = BASE_EU;
        if (!operate(false)) { progress = 0; status = 1; setChanged(); return; }
        int clocks = upgrades(ICItems.OVERCLOCKER_UPGRADE.get());
        int nextDuration = ProcessingUpgrades.ticks(baseTicks, clocks);
        if (duration != nextDuration) progress = (int) ((long) progress * nextDuration / Math.max(1, duration));
        duration = nextDuration;
        int cost = ProcessingUpgrades.energy(baseEu, clocks);
        if (energy < cost) { status = 2; return; }
        energy -= cost; status = 4;
        if (++progress >= duration) {
            for (int operation = 0; operation < ProcessingUpgrades.batch(baseTicks, clocks) && operate(true); operation++) { }
            progress = 0;
        }
        previousInput = items.get(ADDITIVE).copy(); previousContainer = items.get(CONTAINER).copy();
        setChanged();
    }
    private void discharge() {
        ItemStack stack = items.get(BATTERY);
        int room = Math.max(0, capacity() - energy);
        int accepted = DischargeFuel.burn(stack, room, true);
        if (accepted == 0 && stack.getCount() == 1) accepted = BatteryItem.extractEnergy(stack, room, tier(), false);
        energy = (int) Math.min(Integer.MAX_VALUE, (long) energy + accepted);
    }
    private boolean operate(boolean commit) {
        return switch (mode) { case 0 -> packageSolid(commit); case 1, 2 -> transferContainer(commit); default -> enrich(commit); };
    }
    private boolean packageSolid(boolean commit) {
        var holder = ((ServerLevel) level).recipeAccess().getRecipeFor(ProcessingContent.recipeType(ProcessingKind.CANNING), new SingleRecipeInput(items.get(ADDITIVE)), level).orElse(null);
        if (holder == null) return false;
        ProcessingRecipe recipe = holder.value();
        if (!recipe.sameOperation(activeSolid)) { progress = 0; activeSolid = recipe; }
        ItemStack container = items.get(CONTAINER), result = recipe.result().create();
        if (!recipe.packaging().test(container) || container.getCount() < recipe.cansRequired() || !fits(OUTPUT, result)) return false;
        baseTicks = recipe.processTicks();
        if (commit) { items.get(ADDITIVE).shrink(recipe.inputCount()); container.shrink(recipe.cansRequired()); insert(OUTPUT, result); }
        return true;
    }
    private boolean transferContainer(boolean commit) {
        ItemStack container = items.get(CONTAINER);
        if (container.isEmpty()) return false;
        var working = new SimpleContainer(container.copyWithCount(1));
        var access = ItemAccess.forHandlerIndexStrict(VanillaContainerWrapper.of(working), 0);
        var handler = access.getCapability(Capabilities.Fluid.ITEM);
        if (handler == null) return false;
        boolean filling = mode == 2;
        try (var tx = Transaction.openRoot()) {
            var tank = RangedResourceHandler.ofSingleIndex(tanks, filling ? 0 : 1);
            FluidResource selected = filling ? tank.getResource(0) : ResourceHandlerUtil.findExtractableResource(handler, fluid -> true, tx);
            if (selected == null || selected.isEmpty()) return false;
            int amount = Integer.MAX_VALUE;
            if (!filling) try (var probe = Transaction.open(tx)) { amount = handler.extract(selected, Integer.MAX_VALUE, probe); }
            int moved = ResourceHandlerUtil.move(filling ? tank : handler, filling ? handler : tank, selected::equals, amount, tx);
            if (moved == 0 || !filling && moved < amount) return false;
            ItemStack result = access.getResource().toStack();
            boolean keep = filling ? !full(handler, selected, tx) : ResourceHandlerUtil.findExtractableResource(handler, fluid -> true, tx) != null;
            if (keep ? container.getCount() != 1 : !fits(OUTPUT, result)) return false;
            if (commit) {
                tx.commit();
                if (keep) items.set(CONTAINER, result);
                else { container.shrink(1); insert(OUTPUT, result); }
            }
            return true;
        }
    }
    private static boolean full(ResourceHandler<FluidResource> handler, FluidResource fluid, TransactionContext tx) {
        try (var probe = Transaction.open(tx)) { return handler.insert(fluid, Integer.MAX_VALUE, probe) == 0; }
    }
    private boolean enrich(boolean commit) {
        var holder = ((ServerLevel) level).recipeAccess().getRecipeFor(ProcessingContent.ENRICHMENT.get(), new SingleRecipeInput(items.get(ADDITIVE)), level).orElse(null);
        if (holder == null) return false;
        EnrichmentRecipe recipe = holder.value();
        if (!recipe.sameOperation(activeEnrichment)) { progress = 0; activeEnrichment = recipe; }
        FluidStack required = recipe.fluidInput(), produced = recipe.fluidOutput();
        if (!tanks.getResource(0).equals(FluidResource.of(required.getFluid())) || tanks.getAmountAsInt(0) < required.getAmount()) return false;
        baseTicks = recipe.ticks(); baseEu = recipe.eu();
        try (var tx = Transaction.openRoot()) {
            ItemStack container = items.get(CONTAINER);
            var working = new SimpleContainer(container.copyWithCount(1));
            var access = ItemAccess.forHandlerIndexStrict(VanillaContainerWrapper.of(working), 0);
            var handler = access.getCapability(Capabilities.Fluid.ITEM);
            int bottled = 0;
            boolean keep = false;
            ItemStack result = ItemStack.EMPTY;
            if (handler != null) {
                try (var bottle = Transaction.open(tx)) {
                    int inserted = handler.insert(FluidResource.of(produced.getFluid()), produced.getAmount(), bottle);
                    ItemStack candidate = access.getResource().toStack();
                    boolean partial = !full(handler, FluidResource.of(produced.getFluid()), bottle);
                    if (inserted > 0 && (partial ? container.getCount() == 1 : fits(OUTPUT, candidate))) {
                        bottled = inserted; result = candidate; keep = partial; bottle.commit();
                    }
                }
            }
            int remainder = produced.getAmount() - bottled;
            if (tanks.insert(1, FluidResource.of(produced.getFluid()), remainder, tx) != remainder) return false;
            tanks.extract(0, FluidResource.of(required.getFluid()), required.getAmount(), tx);
            if (commit) {
                tx.commit(); items.get(ADDITIVE).shrink(recipe.inputCount());
                if (bottled > 0) {
                    if (keep) items.set(CONTAINER, result);
                    else { container.shrink(1); insert(OUTPUT, result); }
                }
            }
            return true;
        }
    }
    public int value(int index) {
        return switch (index) { case 0 -> energy; case 1 -> capacity(); case 2 -> progress; case 3 -> duration; case 4 -> status; case 5 -> mode;
            case 6, 7 -> tanks.getAmountAsInt(index - 6); case 8, 9 -> BuiltInRegistries.FLUID.getId(tanks.getResource(index - 8).getFluid()); default -> 0; };
    }
    @Override protected int displayValue(int index) { return value(index); }
    @Override protected void receiveDisplayValue(int index, int value) { }
    @Override public int demandedEu() { return Math.max(0, capacity() - energy); }
    @Override public int maxInputVoltage() { return EuTier.values()[tier() - 1].volts; }
    @Override public int receiveEu(int voltage, int amount) {
        if (voltage <= 0 || voltage > maxInputVoltage()) return 0;
        int received = Math.clamp(amount, 0, demandedEu()); energy += received; if (received > 0) setChanged(); return received;
    }
    @Override public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) { return new ProcessingCannerMenu(id, inventory, this); }
    @Override public boolean canPlaceItem(int slot, ItemStack stack) {
        if (slot >= UPGRADES) return ProcessingUpgrades.valid(stack) && !stack.is(ICItems.REDSTONE_INVERTER_UPGRADE.get());
        if (slot == BATTERY) return DischargeFuel.value(stack) > 0 || stack.getItem() instanceof BatteryItem battery && battery.canSupplyEnergy() && battery.tier().ordinal() < tier();
        if (slot == CONTAINER) return mode == 0 ? stack.is(ICTags.CANNING_CONTAINERS)
                : ItemAccess.forStack(stack.copyWithCount(1)).getCapability(Capabilities.Fluid.ITEM) != null;
        if (slot != ADDITIVE || mode == 1 || mode == 2) return false;
        if (!(level instanceof ServerLevel world)) return !canPlaceItem(CONTAINER, stack);
        var input = new SingleRecipeInput(stack.copyWithCount(stack.getMaxStackSize()));
        return mode == 0 ? world.recipeAccess().getRecipeFor(ProcessingContent.recipeType(ProcessingKind.CANNING), input, world).isPresent()
                : world.recipeAccess().getRecipeFor(ProcessingContent.ENRICHMENT.get(), input, world).isPresent();
    }
    @Override public int[] getSlotsForFace(Direction side) { return side == Direction.DOWN ? new int[]{OUTPUT} : side == Direction.UP ? new int[]{ADDITIVE, CONTAINER} : new int[]{CONTAINER, BATTERY}; }
    @Override public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) { return slot == OUTPUT || slot == BATTERY && side.getAxis().isHorizontal(); }
    @Override public void onLoad() { super.onLoad(); if (!level.isClientSide()) EuGrids.markDirty(level, worldPosition); }
    @Override public void setRemoved() { if (level != null && !level.isClientSide()) EuGrids.markDirty(level, worldPosition); super.setRemoved(); }
    @Override protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output); output.putInt("energy", energy); output.putInt("progress", progress); output.putInt("mode", mode); output.putInt("duration", duration);
        if (activeSolid != null) output.store("solid_recipe", ProcessingRecipe.MAP_CODEC.codec(), activeSolid);
        if (activeEnrichment != null) output.store("enrich_recipe", EnrichmentRecipe.CODEC.codec(), activeEnrichment);
        for (int i = 0; i < 2; i++) output.store("tank_" + i, FluidStack.OPTIONAL_CODEC, tanks.getResource(i).toStack(tanks.getAmountAsInt(i)));
    }
    @Override protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input); energy = Math.max(0, input.getIntOr("energy", 0)); progress = Math.max(0, input.getIntOr("progress", 0)); mode = Math.clamp(input.getIntOr("mode", 0), 0, 3);
        duration = Math.max(1, input.getIntOr("duration", BASE_TICKS));
        activeSolid = input.read("solid_recipe", ProcessingRecipe.MAP_CODEC.codec()).orElse(null);
        activeEnrichment = input.read("enrich_recipe", EnrichmentRecipe.CODEC.codec()).orElse(null);
        for (int i = 0; i < 2; i++) { FluidStack fluid = input.read("tank_" + i, FluidStack.OPTIONAL_CODEC).orElse(FluidStack.EMPTY); tanks.set(i, FluidResource.of(fluid.getFluid()), Math.min(TANK_CAPACITY, fluid.getAmount())); }
        previousInput = items.get(ADDITIVE).copy(); previousContainer = items.get(CONTAINER).copy();
    }
}
