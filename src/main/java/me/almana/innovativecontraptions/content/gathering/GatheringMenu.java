package me.almana.innovativecontraptions.content.gathering;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public final class GatheringMenu extends AbstractContainerMenu {
    private final GatheringBlockEntity machine;
    private final ContainerData data;
    public GatheringMenu(int id, Inventory inventory, RegistryFriendlyByteBuf buffer) {
        this(id, inventory, (GatheringBlockEntity) inventory.player.level().getBlockEntity(buffer.readBlockPos()));
    }
    public GatheringMenu(int id, Inventory inventory, GatheringBlockEntity machine) {
        super(GatheringContent.MENU.get(), id); this.machine = machine;
        data = inventory.player.level().isClientSide() ? new SimpleContainerData(16) : machine.display;
        for (int slot = 0; slot < 24; slot++) addMachineSlot(slot);
        for (int row = 0; row < 3; row++) for (int col = 0; col < 9; col++) addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 148 + row * 18));
        for (int col = 0; col < 9; col++) addSlot(new Slot(inventory, col, 8 + col * 18, 206));
        addDataSlots(data);
    }
    private void addMachineSlot(int index) {
        addSlot(new Slot(machine, index, slotX(index), slotY(index)) {
            @Override public boolean mayPlace(ItemStack stack) { return visible(index) && machine.canPlaceItem(index, stack); }
            @Override public boolean mayPickup(Player player) { return visible(index) && !(advanced() && index >= 8 && index < 23); }
            @Override public boolean isActive() { return visible(index); }
        });
    }
    public boolean advanced() { return machine.advanced(); }
    public boolean pump() { return machine.pump(); }
    public boolean visible(int slot) {
        if (slot == 3 || slot >= 4 && slot < 8) return pump() || advanced() || slot < 5;
        if (pump()) return slot == 0 || slot == 23;
        return slot >= 8 && slot < 23 || (advanced() ? slot == 2 : slot < 3);
    }
    private int slotX(int slot) {
        if (slot >= 8 && slot < 23) return 32 + (slot - 8) % 5 * 18;
        if (slot >= 4 && slot < 8) return pump() || advanced() ? 8 + (slot - 4) * 18 : 150;
        if (pump()) return slot == 23 ? 44 : 8;
        return slot == 3 && !advanced() ? 150 : 8;
    }
    private int slotY(int slot) {
        if (slot >= 8 && slot < 23) return 34 + (slot - 8) / 5 * 18;
        if (slot >= 4 && slot < 8) return pump() || advanced() ? 114 : 34;
        if (slot == 3) return 70;
        return pump() || advanced() ? 34 : 34 + slot * 18;
    }
    public int value(int index) { return (data.get(index * 2) & 65535) | (data.get(index * 2 + 1) << 16); }
    @Override public boolean stillValid(Player player) { return machine.stillValid(player); }
    @Override public boolean clickMenuButton(Player player, int button) { return stillValid(player) && machine.button(button); }
    @Override public void clicked(int slot, int button, ContainerInput type, Player player) {
        if (advanced() && slot >= 8 && slot < 23) {
            if (type == ContainerInput.PICKUP) machine.setItem(slot, getCarried().isEmpty() ? ItemStack.EMPTY : getCarried().copyWithCount(1));
            return;
        }
        super.clicked(slot, button, type, player);
    }
    @Override public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem() || !slot.mayPickup(player)) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem(), original = stack.copy();
        if (index < 24) {
            if (!moveItemStackTo(stack, 24, 60, true)) return ItemStack.EMPTY;
        } else {
            boolean moved = false;
            for (int target = 0; target < 8 && !stack.isEmpty(); target++) if (visible(target)) moved |= moveItemStackTo(stack, target, target + 1, false);
            if (!moved) return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY); else slot.setChanged();
        slot.onTake(player, stack); return original;
    }
}

