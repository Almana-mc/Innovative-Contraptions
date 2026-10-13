package me.almana.innovativecontraptions.content.thermal;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public final class ThermalMenu extends AbstractContainerMenu {
    private final ThermalInventory machine;
    private final ContainerData data;

    public ThermalMenu(int id, Inventory inventory, RegistryFriendlyByteBuf buffer) {
        this(id, inventory, (ThermalInventory) inventory.player.level().getBlockEntity(buffer.readBlockPos()));
    }

    public ThermalMenu(int id, Inventory inventory, ThermalInventory machine) {
        super(ThermalContent.MENU.get(), id);
        this.machine = machine;
        this.data = inventory.player.level().isClientSide() ? new SimpleContainerData(6) : machine.data;
        addMachineSlots();
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 126 + row * 18));
        }
        for (int col = 0; col < 9; col++) addSlot(new Slot(inventory, col, 8 + col * 18, 184));
        addDataSlots(data);
    }

    private void addMachineSlots() {
        for (int index = 0; index < machine.getContainerSize(); index++) {
            int slot = index;
            addSlot(new Slot(machine, slot, slotX(heater(), slot), slotY(heater(), slot)) {
                @Override public boolean mayPlace(ItemStack stack) { return machine.canPlaceItem(slot, stack); }
                @Override public int getMaxStackSize() { return heater() && slot != ElectricHeaterBlockEntity.BATTERY_SLOT ? 1 : super.getMaxStackSize(); }
            });
        }
    }

    public static int slotX(boolean heater, int slot) {
        if (heater) return slot == 10 ? 116 : 16 + slot % 5 * 18;
        return switch (slot) { case 0, 1 -> 16; case 2, 4 -> 88; case 3 -> 106; default -> 130; };
    }
    public static int slotY(boolean heater, int slot) {
        if (heater) return slot == 10 ? 49 : 38 + slot / 5 * 22;
        return switch (slot) { case 1, 4 -> 70; case 6 -> 56; default -> 38; };
    }
    public boolean heater() { return machine instanceof ElectricHeaterBlockEntity; }
    public int value(int index) { return data.get(index); }
    public int machineSlots() { return machine.getContainerSize(); }
    @Override public boolean stillValid(Player player) { return machine.stillValid(player); }

    @Override public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack moved = stack.copy();
        int count = machineSlots();
        if (index < count) {
            if (!moveItemStackTo(stack, count, count + 36, true)) return ItemStack.EMPTY;
        } else if (!moveIntoMachine(stack)) {
            if (index < count + 27) {
                if (!moveItemStackTo(stack, count + 27, count + 36, false)) return ItemStack.EMPTY;
            } else if (!moveItemStackTo(stack, count, count + 27, false)) return ItemStack.EMPTY;
        }
        if (stack.getCount() == moved.getCount()) return ItemStack.EMPTY;
        if (stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
        else slot.setChanged();
        slot.onTake(player, stack);
        return moved;
    }

    private boolean moveIntoMachine(ItemStack stack) {
        if (!heater()) return moveItemStackTo(stack, 0, 2, false) || moveItemStackTo(stack, BlastFurnaceBlockEntity.UPGRADES, machineSlots(), false);
        boolean moved = false;
        for (int slot = 0; slot < machineSlots() && !stack.isEmpty(); slot++) {
            moved |= moveItemStackTo(stack, slot, slot + 1, false);
        }
        return moved;
    }
}
