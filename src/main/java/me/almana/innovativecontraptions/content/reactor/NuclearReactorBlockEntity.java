package me.almana.innovativecontraptions.content.reactor;

import java.util.stream.IntStream;
import me.almana.innovativecontraptions.content.bioenergy.BioenergyTransfers;
import me.almana.innovativecontraptions.content.energy.EuSource;
import me.almana.innovativecontraptions.content.energy.EuTier;
import me.almana.innovativecontraptions.content.fluid.CoolantContent;
import me.almana.innovativecontraptions.content.machine.ActiveState;
import me.almana.innovativecontraptions.content.thermal.ThermalInventory;
import me.almana.innovativecontraptions.registry.ICBlocks;
import me.almana.innovativecontraptions.registry.ICItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
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

public final class NuclearReactorBlockEntity extends ThermalInventory implements EuSource {
    public static final int GRID = ReactorGrid.ROWS * ReactorGrid.MAX_COLS;
    public static final int COOLANT_IN = GRID, COOLANT_OUT = GRID + 1, HOT_IN = GRID + 2, HOT_OUT = GRID + 3, SLOTS = GRID + 4;
    public static final int TANK = 10_000, CYCLE = 20, DATA = 12, ACTIVE_HEAT = 1_000;
    private static final int[] ALL_SLOTS = IntStream.range(0, SLOTS).toArray();
    private final FluidStacksResourceHandler tanks = new FluidStacksResourceHandler(2, TANK) {
        @Override public boolean isValid(int index, FluidResource resource) {
            return resource.getFluid() == (index == 0 ? CoolantContent.COOLANT.get() : CoolantContent.HOT_COOLANT.get());
        }
        @Override protected void onContentsChanged(int index, FluidStack previous) { setChanged(); }
    };
    private final ResourceHandler<FluidResource> port = new DelegatingResourceHandler<>(tanks) {
        @Override public int insert(int index, FluidResource resource, int amount, TransactionContext tx) { return index == 0 ? tanks.insert(index, resource, amount, tx) : 0; }
        @Override public int insert(FluidResource resource, int amount, TransactionContext tx) { return insert(0, resource, amount, tx); }
        @Override public int extract(int index, FluidResource resource, int amount, TransactionContext tx) { return index == 1 ? tanks.extract(index, resource, amount, tx) : 0; }
        @Override public int extract(FluidResource resource, int amount, TransactionContext tx) { return extract(1, resource, amount, tx); }
    };
    private int heat, maxHeat = ReactorGrid.BASE_MAX_HEAT, cols = ReactorGrid.BASE_COLS, euPerTick, available, huPerSecond, band;
    private boolean fluidMode;

    public NuclearReactorBlockEntity(BlockPos pos, BlockState state) {
        super(ReactorContent.REACTOR.get(), pos, state, SLOTS);
    }

    public boolean fluidMode() { return fluidMode; }
    public ResourceHandler<FluidResource> fluids() { return port; }
    public ResourceHandler<FluidResource> coolantInput() { return RangedResourceHandler.ofSingleIndex(port, 0); }
    public ResourceHandler<FluidResource> hotCoolantOutput() { return RangedResourceHandler.ofSingleIndex(port, 1); }

    @Override public void tick() {
        if (Math.floorMod(level.getGameTime() + worldPosition.hashCode(), CYCLE) == 0) cycle();
        available = euPerTick;
    }

    public void clientTick() {
        particles(worldPosition);
        for (Direction side : Direction.values()) {
            BlockPos at = worldPosition.relative(side);
            if (level.getBlockState(at).is(ICBlocks.REACTOR_CHAMBER.get())) particles(at);
        }
    }

    private void particles(BlockPos pos) {
        RandomSource random = level.getRandom();
        if (random.nextInt(8) != 0 || heat < ACTIVE_HEAT) return;
        int puffs = random.nextInt(heat / ACTIVE_HEAT);
        for (int n = 0; n < puffs; n++)
            level.addParticle(ParticleTypes.SMOKE, pos.getX() + random.nextFloat(), pos.getY() + 0.95, pos.getZ() + random.nextFloat(), 0, 0, 0);
        puffs -= random.nextInt(4) + 3;
        for (int n = 0; n < puffs; n++)
            level.addParticle(ParticleTypes.FLAME, pos.getX() + random.nextFloat(), pos.getY() + 1.0, pos.getZ() + random.nextFloat(), 0, 0, 0);
    }

    private void cycle() {
        if (!level.hasChunksAt(worldPosition.offset(-8, -8, -8), worldPosition.offset(8, 8, 8))) {
            euPerTick = 0;
            return;
        }
        int columns = ReactorGrid.BASE_COLS + ReactorStructure.chambers(level, worldPosition);
        boolean fluid = columns == ReactorGrid.MAX_COLS && ReactorStructure.casingComplete(level, worldPosition)
                && !ReactorStructure.conflict(level, worldPosition);
        if (columns != cols || fluid != fluidMode) ReactorStructure.markGrid(level, worldPosition);
        cols = columns;
        if (fluid != fluidMode) {
            fluidMode = fluid;
            ReactorStructure.invalidate(level, worldPosition);
        }
        ejectUnfitting();
        ReactorGrid grid = load();
        grid.cycle();
        store(grid);
        huPerSecond = fluidMode ? cool(grid) : 0;
        heat = grid.heat();
        maxHeat = grid.maxHeat();
        if (ReactorMeltdown.tick(this, grid)) return;
        euPerTick = fluidMode ? 0 : Math.round(grid.output() * ReactorGrid.EU_PER_OUTPUT);
        ActiveState.update(level, worldPosition, heat >= ACTIVE_HEAT || grid.output() > 0F);
        if (heat / ACTIVE_HEAT != band) {
            band = heat / ACTIVE_HEAT;
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
        setChanged();
    }

    private ReactorGrid load() {
        ReactorGrid grid = new ReactorGrid(cols);
        grid.setHeat(heat);
        grid.setOn(ReactorStructure.powered(level, worldPosition, fluidMode));
        grid.setFluidCooled(fluidMode);
        for (int y = 0; y < ReactorGrid.ROWS; y++) {
            for (int x = 0; x < cols; x++) {
                ItemStack stack = items.get(ReactorGrid.slot(x, y));
                if (stack.getItem() instanceof ReactorComponentItem item) grid.set(x, y, item.part(), ReactorComponentItem.damage(stack));
            }
        }
        return grid;
    }

    private void store(ReactorGrid grid) {
        for (int y = 0; y < ReactorGrid.ROWS; y++) {
            for (int x = 0; x < cols; x++) {
                int slot = ReactorGrid.slot(x, y);
                if (!(items.get(slot).getItem() instanceof ReactorComponentItem item)) continue;
                if (grid.part(x, y) == null) items.set(slot, ItemStack.EMPTY);
                else if (grid.spent(x, y)) items.set(slot, item.spentStack());
                else ReactorComponentItem.storeDamage(items.get(slot), grid.damage(x, y));
            }
        }
    }

    private void ejectUnfitting() {
        for (int slot = 0; slot < (fluidMode ? GRID : SLOTS); slot++) {
            ItemStack stack = items.get(slot);
            if (stack.isEmpty() || slot < GRID && slot % ReactorGrid.MAX_COLS < cols && stack.getItem() instanceof ReactorComponentItem) continue;
            items.set(slot, ItemStack.EMPTY);
            Containers.dropItemStack(level, worldPosition.getX() + 0.5, worldPosition.getY() + 1.0, worldPosition.getZ() + 0.5, stack);
        }
    }

    private int cool(ReactorGrid grid) {
        BioenergyTransfers.container(this, coolantInput(), COOLANT_IN, COOLANT_OUT, false);
        BioenergyTransfers.container(this, hotCoolantOutput(), HOT_IN, HOT_OUT, true);
        int converted = grid.coolWith(tanks.getAmountAsInt(0), TANK - tanks.getAmountAsInt(1));
        if (converted == 0) return 0;
        try (var transaction = Transaction.openRoot()) {
            tanks.extract(0, FluidResource.of(CoolantContent.COOLANT.get()), converted, transaction);
            tanks.insert(1, FluidResource.of(CoolantContent.HOT_COOLANT.get()), converted, transaction);
            transaction.commit();
        }
        return converted * ReactorGrid.HU_PER_MB;
    }

    public static boolean fits(ItemStack stack, boolean fluid) {
        return stack.getItem() instanceof ReactorComponentItem item && item.insertable()
                && !(fluid && item.part().kind == ReactorPart.Kind.STORAGE && ReactorComponentItem.damage(stack) > 0);
    }

    public static boolean cellFits(int slot, ItemStack stack) {
        if (slot == COOLANT_IN) return stack.is(ICItems.COOLANT_CELL.get());
        return slot == HOT_IN && stack.is(ICItems.EMPTY_CELL.get());
    }

    @Override public boolean canPlaceItem(int slot, ItemStack stack) {
        if (slot < GRID) return slot % ReactorGrid.MAX_COLS < cols && fits(stack, fluidMode);
        return fluidMode && cellFits(slot, stack);
    }

    @Override public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) { return new ReactorMenu(id, inventory, this); }

    @Override public int getMaxStackSize(ItemStack stack) { return stack.getItem() instanceof ReactorComponentItem ? 1 : stack.getMaxStackSize(); }
    @Override public int[] getSlotsForFace(Direction side) { return ALL_SLOTS; }
    @Override public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction side) { return canPlaceItem(slot, stack); }
    @Override public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return slot == COOLANT_OUT || slot == HOT_OUT || slot < GRID && stack.getItem() instanceof ReactorComponentItem item && !item.insertable();
    }

    @Override public int sourceVoltage() {
        for (EuTier tier : EuTier.values()) if (euPerTick <= tier.volts) return tier.volts;
        return EuTier.IV.volts;
    }
    @Override public int availableEu() { return available; }
    @Override public int extractEu(int amount) {
        int taken = Math.min(available, Math.max(0, amount));
        available -= taken;
        return taken;
    }
    @Override public int packetsPerTick() { return Math.max(1, Math.ceilDiv(euPerTick, EuTier.IV.volts)); }

    @Override protected int dataCount() { return DATA; }
    @Override protected int displayValue(int index) {
        return switch (index) {
            case 0 -> heat & 0xFFFF;
            case 1 -> heat >>> 16;
            case 2 -> maxHeat & 0xFFFF;
            case 3 -> maxHeat >>> 16;
            case 4 -> euPerTick & 0xFFFF;
            case 5 -> euPerTick >>> 16;
            case 6 -> huPerSecond & 0xFFFF;
            case 7 -> huPerSecond >>> 16;
            case 8 -> cols;
            case 9 -> fluidMode ? 1 : 0;
            case 10 -> tanks.getAmountAsInt(0);
            case 11 -> tanks.getAmountAsInt(1);
            default -> 0;
        };
    }
    @Override protected void receiveDisplayValue(int index, int value) { }

    @Override public void onLoad() {
        super.onLoad();
        if (!level.isClientSide()) ReactorStructure.markGrid(level, worldPosition);
    }

    @Override public void setRemoved() {
        if (level != null && !level.isClientSide()) {
            ReactorStructure.markGrid(level, worldPosition);
            ReactorStructure.invalidate(level, worldPosition);
        }
        super.setRemoved();
    }

    @Override protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        tanks.serialize(output.child("tanks"));
        output.putInt("heat", heat);
        output.putInt("eu", euPerTick);
        output.putInt("columns", cols);
        output.putBoolean("fluid", fluidMode);
    }

    @Override protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        tanks.deserialize(input.childOrEmpty("tanks"));
        heat = input.getIntOr("heat", 0);
        euPerTick = input.getIntOr("eu", 0);
        cols = input.getIntOr("columns", ReactorGrid.BASE_COLS);
        fluidMode = input.getBooleanOr("fluid", false);
    }

    @Override public ClientboundBlockEntityDataPacket getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider registries) { return saveWithoutMetadata(registries); }
}
