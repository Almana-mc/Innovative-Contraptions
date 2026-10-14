package me.almana.innovativecontraptions.content.agriculture;

import java.util.List;
import me.almana.innovativecontraptions.content.energy.BatteryItem;
import me.almana.innovativecontraptions.registry.ICItems;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

public final class AnalyzerMenu extends AbstractContainerMenu {
    public static final int[] SCAN_COSTS = {10, 90, 900, 9000};
    private final Player player;
    private final InteractionHand hand;
    private final ItemStack analyzer;
    private final SimpleContainer contents;
    private int displayedEnergy;
    private final int lockedSlot;
    public AnalyzerMenu(int id, Inventory inventory, RegistryFriendlyByteBuf buffer) { this(id, inventory, buffer.readEnum(InteractionHand.class)); }
    public AnalyzerMenu(int id, Inventory inventory, InteractionHand hand) {
        super(FarmingContent.ANALYZER.get(), id); this.player = inventory.player; this.hand = hand;
        //? if >=26.1 {
        analyzer = player.getItemInHand(hand); lockedSlot = hand == InteractionHand.MAIN_HAND ? inventory.getSelectedSlot() : 40;
        //?} else
        //analyzer = player.getItemInHand(hand); lockedSlot = hand == InteractionHand.MAIN_HAND ? inventory.selected : 40;
        var stored = NonNullList.withSize(3, ItemStack.EMPTY);
        analyzer.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(stored);
        contents = new SimpleContainer(stored.toArray(ItemStack[]::new)) {
            @Override public void setChanged() { super.setChanged(); persist(); }
        };
        addSlot(new Slot(contents, 0, 28, 39) { @Override public boolean mayPlace(ItemStack stack) { return stack.is(ICItems.GENETIC_SEEDS.get()) && stack.has(FarmingContent.GENOME.get()); } });
        addSlot(new Slot(contents, 1, 104, 39) { @Override public boolean mayPlace(ItemStack stack) { return false; } });
        addSlot(new Slot(contents, 2, 140, 39) { @Override public boolean mayPlace(ItemStack stack) { return stack.getItem() instanceof BatteryItem item && item.canSupplyEnergy() && item.tier().ordinal() <= 1; } });
        for (int row = 0; row < 3; row++) for (int col = 0; col < 9; col++) playerSlot(inventory, col + row * 9 + 9, 8 + col * 18, 158 + row * 18);
        for (int col = 0; col < 9; col++) playerSlot(inventory, col, 8 + col * 18, 216);
        energySlot(0); energySlot(16);
    }
    private void energySlot(int shift) {
        addDataSlot(new DataSlot() {
            @Override public int get() { return BatteryItem.stored(analyzer) >>> shift & 65535; }
            @Override public void set(int value) { displayedEnergy = displayedEnergy & ~(65535 << shift) | (value & 65535) << shift; }
        });
    }
    private void playerSlot(Inventory inventory, int index, int x, int y) {
        addSlot(new Slot(inventory, index, x, y) {
            @Override public boolean mayPickup(Player player) { return index != lockedSlot; }
            @Override public boolean mayPlace(ItemStack stack) { return index != lockedSlot; }
        });
    }
    public int energy() { return player.level().isClientSide() ? displayedEnergy : BatteryItem.stored(analyzer); }
    public SimpleContainer contents() { return contents; }
    public void scan() {
        if (!stillValid(player) || player.level().isClientSide()) return;
        int charged = BatteryItem.extractEnergy(contents.getItem(2), Math.min(128, 100_000 - BatteryItem.stored(analyzer)), 2, false);
        BatteryItem.receive(analyzer, charged);
        ItemStack input = contents.getItem(0);
        if (input.isEmpty() || !contents.getItem(1).isEmpty() || !input.has(FarmingContent.GENOME.get())) { if (charged > 0) persist(); return; }
        CropGenome seed = input.get(FarmingContent.GENOME.get());
        int cost = seed.scan() == 4 ? 0 : SCAN_COSTS[seed.scan()];
        if (BatteryItem.stored(analyzer) < cost) { if (charged > 0) persist(); return; }
        BatteryItem.extract(analyzer, cost);
        ItemStack result = input.copyWithCount(1); result.set(FarmingContent.GENOME.get(), seed.scanned());
        input.shrink(1); contents.setItem(1, result); persist();
    }
    private void persist() { if (!player.level().isClientSide()) analyzer.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(List.of(contents.getItem(0), contents.getItem(1), contents.getItem(2)))); }
    @Override public void broadcastChanges() { scan(); super.broadcastChanges(); }
    @Override public boolean stillValid(Player player) { return player.getItemInHand(hand) == analyzer; }
    @Override public void clicked(int slot, int button, ContainerInput action, Player player) {
        if (action == ContainerInput.SWAP && button == lockedSlot) return;
        super.clicked(slot, button, action, player);
    }
    @Override public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem() || !slot.mayPickup(player)) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem(), copy = stack.copy();
        if (index < 3) { if (!moveItemStackTo(stack, 3, 39, true)) return ItemStack.EMPTY; }
        else if (stack.is(ICItems.GENETIC_SEEDS.get())) { if (!moveItemStackTo(stack, 0, 1, false)) return ItemStack.EMPTY; }
        else if (!moveItemStackTo(stack, 2, 3, false)) return ItemStack.EMPTY;
        if (stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY); else slot.setChanged();
        slot.onTake(player, stack); persist(); return copy;
    }
}
