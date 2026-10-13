package me.almana.innovativecontraptions.content.processing;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

public final class ProcessingCannerMenu extends AbstractContainerMenu {
    private final ProcessingCannerBlockEntity machine;
    private final int[] values = new int[10];
    public ProcessingCannerMenu(int id, Inventory inventory, RegistryFriendlyByteBuf buffer) { this(id, inventory, (ProcessingCannerBlockEntity) inventory.player.level().getBlockEntity(buffer.readBlockPos())); }
    public ProcessingCannerMenu(int id, Inventory inventory, ProcessingCannerBlockEntity machine) {
        super(ProcessingContent.CANNER_MENU.get(), id); this.machine = machine;
        int[][] positions = {{67, 42}, {19, 42}, {115, 42}, {149, 42}, {31, 104}, {53, 104}, {75, 104}, {97, 104}};
        for (int i = 0; i < 8; i++) {
            final int slot = i;
            addSlot(new Slot(machine, i, positions[i][0], positions[i][1]) { @Override public boolean mayPlace(ItemStack stack) { return machine.canPlaceItem(slot, stack); } });
        }
        for (int row = 0; row < 3; row++) for (int col = 0; col < 9; col++) addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 140 + row * 18));
        for (int col = 0; col < 9; col++) addSlot(new Slot(inventory, col, 8 + col * 18, 198));
        for (int i = 0; i < values.length; i++) { sync(i, 0); sync(i, 16); }
    }
    private void sync(int field, int shift) {
        addDataSlot(new DataSlot() {
            @Override public int get() { return machine.value(field) >>> shift & 65535; }
            @Override public void set(int value) {
                values[field] = values[field] & ~(65535 << shift) | (value & 65535) << shift;
                if (field == 5) machine.setMode(values[field]);
            }
        });
    }
    public int value(int index) { return machine.getLevel().isClientSide() ? values[index] : machine.value(index); }
    @Override public boolean stillValid(Player player) { return machine.stillValid(player); }
    @Override public boolean clickMenuButton(Player player, int id) { return stillValid(player) && (id == 4 ? machine.swapTanks() : machine.setMode(id)); }
    @Override public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem(), original = stack.copy();
        if (index < 8) { if (!moveItemStackTo(stack, 8, 44, true)) return ItemStack.EMPTY; }
        else {
            int destination = machine.canPlaceItem(3, stack) ? 3 : machine.canPlaceItem(4, stack) ? 4 : machine.canPlaceItem(1, stack) ? 1 : 0;
            if (!moveItemStackTo(stack, destination, destination == 4 ? 8 : destination + 1, false)) return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY); else slot.setChanged();
        slot.onTake(player, stack); return original;
    }
}
