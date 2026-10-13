package me.almana.innovativecontraptions.content.agriculture;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public final class FarmingMenu extends AbstractContainerMenu {
    private final FarmingMachineBlockEntity machine;
    private final ContainerData data;
    public FarmingMenu(int id, Inventory inventory, RegistryFriendlyByteBuf buffer) {
        this(id, inventory, (FarmingMachineBlockEntity) inventory.player.level().getBlockEntity(buffer.readBlockPos()));
    }
    public FarmingMenu(int id, Inventory inventory, FarmingMachineBlockEntity machine) {
        super(FarmingContent.MENU.get(), id); this.machine = machine;
        data = inventory.player.level().isClientSide() ? new SimpleContainerData(6) : machine.data;
        for (int index = 0; index < machine.getContainerSize(); index++) {
            int slot = index;
            addSlot(new Slot(machine, slot, slotX(matron(), slot), slotY(matron(), slot)) {
                @Override public boolean mayPlace(ItemStack stack) { return machine.canPlaceItem(slot, stack); }
            });
        }
        for (int row = 0; row < 3; row++) for (int col = 0; col < 9; col++) addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 150 + row * 18));
        for (int col = 0; col < 9; col++) addSlot(new Slot(inventory, col, 8 + col * 18, 208));
        addDataSlots(data);
    }
    public boolean matron() { return machine.matron(); }
    public int value(int index) { return data.get(index); }
    public int machineSlots() { return machine.getContainerSize(); }
    public static int slotX(boolean matron, int slot) {
        if (!matron) return slot < 15 ? 14 + slot % 5 * 18 : slot == 19 ? 140 : 14 + (slot - 15) * 18;
        if (slot < 7) return 14 + slot * 18;
        if (slot >= 11 && slot < 15) return 14 + (slot - 11) * 18;
        return slot == 15 ? 140 : 14 + (slot - 7) * 28;
    }
    public static int slotY(boolean matron, int slot) {
        if (!matron) return slot < 15 ? 40 + slot / 5 * 18 : slot == 19 ? 70 : 104;
        return slot < 7 ? 40 : slot >= 11 && slot < 15 ? 104 : 70;
    }
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
        slot.onTake(player, stack); return original;
    }
}
