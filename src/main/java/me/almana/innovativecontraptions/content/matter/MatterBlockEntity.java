package me.almana.innovativecontraptions.content.matter;

import me.almana.innovativecontraptions.content.energy.*;
import me.almana.innovativecontraptions.content.energy.net.EuGrids;
import me.almana.innovativecontraptions.content.processing.ProcessingUpgrades;
import me.almana.innovativecontraptions.content.thermal.ThermalInventory;
import me.almana.innovativecontraptions.registry.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
//? if >=26.1
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import java.util.ArrayList;
import java.util.List;

public final class MatterBlockEntity extends ThermalInventory implements EuSink {
    public static final int SCAN_TICKS = 3300, SCAN_EU = 256, SYNTHESIS_EU = 1000000, REPLICA_EU = 512;
    private final int kind;
    int energy, progress, amplifier, fraction, mode, selected, status;
    ItemStack pattern = ItemStack.EMPTY;
    final List<ItemStack> patterns = new ArrayList<>();
    public final ContainerData display = new ContainerData() {
        @Override public int get(int index) { return displayValue(index); }
        @Override public void set(int index, int value) {}
        @Override public int getCount() { return 13; }
    };
    public MatterBlockEntity(BlockPos pos, BlockState state) {
        super(MatterContent.ENTITY.get(), pos, state, 9);
        kind = state.is(ICBlocks.MATTER_FABRICATOR.get()) ? 0 : state.is(ICBlocks.PATTERN_SCANNER.get()) ? 1 : state.is(ICBlocks.PATTERN_STORAGE.get()) ? 2 : 3;
    }
    public int kind() { return kind; }
    public int energy() { return energy; }
    public int progress() { return progress; }
    public int amplifier() { return amplifier; }
    public int fraction() { return fraction; }
    public int upgrades(ItemLike item) { return kind == 0 || kind == 3 ? ProcessingUpgrades.count(this, 5, item) : 0; }
    public int tier() { return Math.min(5, (kind == 0 ? 3 : 4) + upgrades(ICItems.TRANSFORMER_UPGRADE.get())); }
    public int capacity() { return (int) Math.min(Integer.MAX_VALUE, (kind == 0 ? SYNTHESIS_EU : kind == 1 ? 512000L : kind == 2 ? 0 : 2000000L) + (kind == 3 ? 10000L * upgrades(ICItems.ENERGY_STORAGE_UPGRADE.get()) : 0)); }
    @Override public int maxInputVoltage() { return EuTier.values()[tier() - 1].volts; }
    @Override public int demandedEu() { return kind == 2 || kind == 0 && paused() ? 0 : Math.max(0, capacity() - energy - (kind == 0 ? progress : 0)); }
    @Override public int receiveEu(int voltage, int amount) {
        if (voltage > maxInputVoltage() || voltage < 1) return 0;
        int accepted = Math.max(0, Math.min(amount, demandedEu()));
        if (accepted > 0) { energy += accepted; setChanged(); }
        return accepted;
    }
    boolean paused() { return level.hasNeighborSignal(worldPosition) != (upgrades(ICItems.REDSTONE_INVERTER_UPGRADE.get()) % 2 == 1); }
    @Override public void tick() {
        int supplied = BatteryItem.extractEnergy(items.get(4), Math.max(0, demandedEu()), tier(), false);
        energy += supplied;
        switch (kind) {
            case 0 -> MatterCycles.synthesize(this);
            case 1 -> MatterCycles.scan(this);
            case 2 -> refreshPattern();
            case 3 -> MatterCycles.replicate(this);
        }
        MatterAutomation.tick(this);
        if (supplied > 0) setChanged();
    }
    boolean outputFits(int slot, ItemStack stack) { return fits(slot, stack); }
    void output(int slot, ItemStack stack) { insert(slot, stack); setChanged(); }
    MatterBlockEntity archive() {
        for (Direction direction : Direction.values()) {
            if (level.getBlockEntity(worldPosition.relative(direction)) instanceof MatterBlockEntity machine && machine.kind == 2) return machine;
        }
        return null;
    }
    void refreshPattern() {
        var archive = kind == 2 ? this : archive();
        List<ItemStack> available = archive == null ? List.of() : archive.patterns;
        selected = available.isEmpty() ? 0 : Math.floorMod(selected, available.size());
        ItemStack next = available.isEmpty() ? ItemStack.EMPTY : available.get(selected);
        if (!ItemStack.isSameItemSameComponents(next, pattern)) { pattern = next.copy(); progress = 0; mode = 0; setChanged(); }
    }
    boolean addPattern(ItemStack stack) {
        if (stack.isEmpty() || MatterCosts.cost(level, stack) <= 0 || patterns.stream().anyMatch(entry -> ItemStack.isSameItemSameComponents(entry, stack))) return false;
        patterns.add(stack.copyWithCount(1)); refreshPattern(); setChanged(); return true;
    }
    public boolean action(int button) {
        boolean changed = MatterCycles.action(this, button);
        if (changed) setChanged();
        return changed;
    }
    @Override protected int displayValue(int index) {
        return switch (index) {
            case 0 -> energy; case 1 -> capacity(); case 2 -> progress;
            case 3 -> kind == 0 ? SYNTHESIS_EU : kind == 1 ? SCAN_TICKS : Math.max(1, MatterCosts.cost(level, pattern));
            case 4 -> getItem(kind == 0 ? 3 : 1).getCount(); case 5 -> status; case 6 -> mode; case 7 -> selected;
            case 8 -> kind == 2 ? patterns.size() : archive() == null ? 0 : archive().patterns.size();
            case 9 -> pattern.isEmpty() ? 0 : BuiltInRegistries.ITEM.getId(pattern.getItem());
            case 10 -> MatterCosts.cost(level, pattern); case 11 -> amplifier; case 12 -> fraction;
            default -> 0;
        };
    }
    @Override protected void receiveDisplayValue(int index, int value) {}
    @Override public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) { return new MatterMenu(id, inventory, this); }
    @Override public boolean canPlaceItem(int slot, ItemStack stack) {
        if (slot == 4) return kind != 2 && stack.getItem() instanceof BatteryItem;
        if (slot >= 5) return (kind == 0 || kind == 3) && ProcessingUpgrades.valid(stack)
                && !stack.is(ICItems.FLUID_PULLING_UPGRADE.get()) && !stack.is(ICItems.FLUID_EJECTOR_UPGRADE.get())
                && (kind != 0 || !stack.is(ICItems.OVERCLOCKER_UPGRADE.get()) && !stack.is(ICItems.ENERGY_STORAGE_UPGRADE.get()))
                && (kind != 3 || !stack.is(ICItems.REDSTONE_INVERTER_UPGRADE.get()));
        if (slot == 1) return kind == 1 || kind == 2 ? stack.is(ICItems.CRYSTAL_MEMORY.get()) : kind == 3 && stack.is(ICItems.UU_MATTER.get());
        return slot == 0 && (kind == 0 ? stack.is(ICItems.SCRAP.get()) || stack.is(ICItems.SCRAP_BOX.get()) : kind == 1 && MatterCosts.cost(level, stack) > 0);
    }
    @Override public int[] getSlotsForFace(Direction direction) { return direction == Direction.DOWN ? new int[]{2, 3, 1} : new int[]{0, 1, 4}; }
    @Override public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction direction) { return slot == 2 || slot == 3 || slot == 1 && (kind == 1 || kind == 2); }
    @Override public void onLoad() { super.onLoad(); if (!level.isClientSide()) EuGrids.markDirty(level, worldPosition); }
    @Override public void setRemoved() { if (level != null && !level.isClientSide()) EuGrids.markDirty(level, worldPosition); super.setRemoved(); }
    @Override protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("charge", energy); output.putInt("work", progress); output.putInt("amplifier", amplifier);
        output.putInt("fraction", fraction); output.putInt("mode", mode); output.putInt("selected", selected);
        if (!pattern.isEmpty()) output.store("pattern", ItemStack.CODEC, pattern);
        output.store("patterns", ItemStack.CODEC.listOf(), patterns);
    }
    @Override protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        energy = Math.clamp(input.getIntOr("charge", 0), 0, capacity()); progress = Math.max(0, input.getIntOr("work", 0));
        amplifier = Math.max(0, input.getIntOr("amplifier", 0)); fraction = Math.clamp(input.getIntOr("fraction", 0), 0, 999);
        mode = Math.clamp(input.getIntOr("mode", 0), 0, 2); selected = Math.max(0, input.getIntOr("selected", 0));
        pattern = input.read("pattern", ItemStack.CODEC).orElse(ItemStack.EMPTY);
        patterns.clear(); patterns.addAll(input.read("patterns", ItemStack.CODEC.listOf()).orElse(List.of()));
    }
    //? if >=26.1 {
    @Override protected void applyImplicitComponents(DataComponentGetter components) {
    //?} else {
    /*@Override protected void applyImplicitComponents(DataComponentInput components) {
    *///?}
        super.applyImplicitComponents(components);
        if (kind == 2) { patterns.clear(); components.getOrDefault(MatterContent.ARCHIVED_PATTERNS.get(), List.<ItemStackTemplate>of()).forEach(template -> patterns.add(template.create())); }
    }
    @Override protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        if (kind == 2 && !patterns.isEmpty()) components.set(MatterContent.ARCHIVED_PATTERNS.get(), patterns.stream().map(ItemStackTemplate::fromNonEmptyStack).toList());
    }
}

