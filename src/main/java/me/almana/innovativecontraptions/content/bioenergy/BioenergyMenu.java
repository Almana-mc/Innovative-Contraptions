package me.almana.innovativecontraptions.content.bioenergy;

import me.almana.innovativecontraptions.content.thermal.ThermalInventory;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public final class BioenergyMenu extends AbstractContainerMenu {
    private final ThermalInventory machine;
    private final ContainerData values;
    public BioenergyMenu(int id, Inventory inventory, RegistryFriendlyByteBuf buffer) {
        this(id, inventory, (ThermalInventory) inventory.player.level().getBlockEntity(buffer.readBlockPos()));
    }
    public BioenergyMenu(int id, Inventory inventory, ThermalInventory machine) {
        super(BioenergyContent.MENU.get(), id);
        this.machine = machine;
        values = inventory.player.level().isClientSide() ? new SimpleContainerData(6) : machine.data;
        for (int index = 0; index < machine.getContainerSize(); index++) {
            int slot = index;
            addSlot(new Slot(machine, slot, slotX(slot), slotY(slot)) {
                @Override public boolean mayPlace(ItemStack stack) { return machine.canPlaceItem(slot, stack); }
            });
        }
        //? if >=26.1 {
        addStandardInventorySlots(inventory, 8, 142);
        //?} else {
        /*for (int row = 0; row < 3; row++) for (int col = 0; col < 9; col++) addSlot(new Slot(inventory, col + (row + 1) * 9, 8 + col * 18, 142 + row * 18));
        for (int col = 0; col < 9; col++) addSlot(new Slot(inventory, col, 8 + col * 18, 200));
        *///?}
        addDataSlots(values);
    }
    public boolean digester() { return machine instanceof BioDigester; }
    public int machineSlots() { return machine.getContainerSize(); }
    public int value(int index) { return values.get(index); }
    private int slotX(int slot) { return digester() ? switch (slot) { case 0, 1, 5 -> 16; case 2, 3, 6 -> 142; default -> 79; } : 38 + slot * 42; }
    private int slotY(int slot) { return digester() ? switch (slot) { case 0, 2 -> 42; case 1, 3, 4 -> 69; default -> 93; } : 55; }
    @Override public boolean stillValid(Player player) { return machine.stillValid(player); }
    @Override public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem(), original = stack.copy();
        int size = machineSlots();
        if (index < size) {
            if (!moveItemStackTo(stack, size, size + 36, true)) return ItemStack.EMPTY;
        } else {
            boolean moved = false;
            for (int target = 0; target < size && !stack.isEmpty(); target++) moved |= moveItemStackTo(stack, target, target + 1, false);
            if (!moved) return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY); else slot.setChanged();
        slot.onTake(player, stack);
        return original;
    }
}
