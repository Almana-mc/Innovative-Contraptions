package me.almana.innovativecontraptions.content.energy.storage;

import me.almana.innovativecontraptions.compat.SavedBlockEntity;
import me.almana.innovativecontraptions.content.energy.EuSink;
import me.almana.innovativecontraptions.content.energy.EuSource;
import me.almana.innovativecontraptions.content.energy.net.EuGrids;
import me.almana.innovativecontraptions.registry.ICBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public abstract class ElectricalBlockEntity extends SavedBlockEntity implements EuSink, EuSource, WorldlyContainer, MenuProvider {
    public final ElectricalKind kind;
    protected final NonNullList<ItemStack> items;
    protected int energy;
    protected int mode;
    protected boolean active;
    private int previousSignal;
    private int previousComparator;
    public final ContainerData data = new ContainerData() {
        @Override public int get(int index) {
            return switch (index) {
                case 0 -> energy & 65535;
                case 1 -> energy >>> 16;
                case 2 -> mode;
                case 3 -> active ? 1 : 0;
                default -> 0;
            };
        }
        @Override public void set(int index, int value) {
            switch (index) {
                case 0 -> energy = (energy & 0xffff0000) | (value & 65535);
                case 1 -> energy = (energy & 65535) | ((value & 65535) << 16);
                case 2 -> mode = value;
                case 3 -> active = value != 0;
                default -> {}
            }
        }
        @Override public int getCount() { return 4; }
    };

    protected ElectricalBlockEntity(BlockPos pos, BlockState state) {
        super(ICBlockEntities.ELECTRICAL.get(), pos, state);
        kind = ((ElectricalBlock) state.getBlock()).kind;
        items = NonNullList.withSize(kind.transformer ? 0 : 2, ItemStack.EMPTY);
    }

    public static ElectricalBlockEntity create(BlockPos pos, BlockState state) {
        return ((ElectricalBlock) state.getBlock()).kind.transformer
                ? new TransformerBlockEntity(pos, state) : new ChargeStorageBlockEntity(pos, state);
    }

    public int storedEu() { return energy; }
    public int mode() { return mode; }
    public boolean active() { return active; }
    public int comparator() { return Math.min(15, (int) (15L * energy / kind.capacity)); }
    public Direction facing() { return getBlockState().getValue(BlockStateProperties.FACING); }
    public abstract boolean accepts(Direction side);
    public abstract boolean emits(Direction side);
    public abstract int modeCount();
    public abstract int signal();
    protected abstract void tickEnergy();

    public void selectMode(int value) {
        if (value < 0 || value >= modeCount()) return;
        mode = value;
        setChanged();
        refreshConnections();
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ElectricalBlockEntity entity) {
        entity.tickEnergy();
        entity.updateSignals();
    }

    private void updateSignals() {
        int currentSignal = signal();
        int currentComparator = comparator();
        if (previousSignal != currentSignal || previousComparator != currentComparator) {
            previousSignal = currentSignal;
            previousComparator = currentComparator;
            level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
            level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());
        }
    }

    public void restoreEnergy(int value) { energy = Math.clamp(value, 0, kind.capacity + (kind.transformer ? 0 : 16000)); setChanged(); }

    @Override public int demandedEu() { return Math.max(0, kind.capacity - energy); }
    @Override public int receiveEu(int voltage, int amount) {
        if (voltage > maxInputVoltage()) return 0;
        int accepted = Math.clamp(amount, 0, demandedEu());
        energy += accepted;
        if (accepted > 0) setChanged();
        return accepted;
    }

    @Override public int extractEu(int amount) {
        int drawn = Math.clamp(amount, 0, energy);
        energy -= drawn;
        if (drawn > 0) setChanged();
        return drawn;
    }

    @Override public int availableEu() { return energy >= sourceVoltage() ? energy : 0; }
    @Override public int getContainerSize() { return items.size(); }
    @Override public boolean isEmpty() { return items.stream().allMatch(ItemStack::isEmpty); }
    @Override public ItemStack getItem(int slot) { return items.get(slot); }
    @Override public ItemStack removeItem(int slot, int count) {
        ItemStack result = ContainerHelper.removeItem(items, slot, count);
        if (!result.isEmpty()) setChanged();
        return result;
    }
    @Override public ItemStack removeItemNoUpdate(int slot) { return ContainerHelper.takeItem(items, slot); }
    @Override public void setItem(int slot, ItemStack stack) { items.set(slot, stack); setChanged(); }
    @Override public boolean stillValid(Player player) { return Container.stillValidBlockEntity(this, player); }
    @Override public void clearContent() { items.clear(); setChanged(); }
    @Override public int[] getSlotsForFace(Direction side) {
        return kind.transformer ? new int[0] : side == Direction.UP ? new int[]{0} : side == Direction.DOWN ? new int[]{1} : new int[]{0, 1};
    }
    @Override public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction side) { return canPlaceItem(slot, stack); }
    @Override public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) { return true; }
    @Override public Component getDisplayName() { return getBlockState().getBlock().getName(); }
    @Override public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) { return new ElectricalMenu(id, inventory, this); }

    public void refreshConnections() {
        if (level == null || level.isClientSide()) return;
        level.invalidateCapabilities(worldPosition);
        EuGrids.markDirty(level, worldPosition);
    }
    @Override public void onLoad() { super.onLoad(); refreshConnections(); }
    @Override public void setRemoved() { refreshConnections(); super.setRemoved(); }

    @Override protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("stored_eu", energy);
        output.putInt("control_mode", mode);
        for (int slot = 0; slot < items.size(); slot++) {
            if (!items.get(slot).isEmpty()) output.store("inventory_" + slot, ItemStack.CODEC, items.get(slot));
        }
    }

    @Override protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        energy = Math.clamp(input.getIntOr("stored_eu", 0), 0, kind.capacity + (kind.transformer ? 0 : 16000));
        mode = Math.clamp(input.getIntOr("control_mode", 0), 0, modeCount() - 1);
        for (int slot = 0; slot < items.size(); slot++) {
            items.set(slot, input.read("inventory_" + slot, ItemStack.CODEC).orElse(ItemStack.EMPTY));
        }
    }
}
