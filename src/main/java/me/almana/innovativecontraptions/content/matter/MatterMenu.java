package me.almana.innovativecontraptions.content.matter;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

public final class MatterMenu extends AbstractContainerMenu {
    private final MatterBlockEntity machine;
    private final ContainerData data;
    public MatterMenu(int id, Inventory inventory, RegistryFriendlyByteBuf buffer) { this(id, inventory, (MatterBlockEntity) inventory.player.level().getBlockEntity(buffer.readBlockPos())); }
    public MatterMenu(int id, Inventory inventory, MatterBlockEntity machine) {
        super(MatterContent.MENU.get(), id); this.machine = machine;
        data = inventory.player.level().isClientSide() ? new SimpleContainerData(13) : machine.display;
        for (int index = 0; index < 9; index++) {
            int slot = index;
            addSlot(new Slot(machine, slot, 12 + slot * 18, 36) {
                @Override public boolean mayPlace(ItemStack stack) { return machine.canPlaceItem(slot, stack); }
                @Override public boolean isActive() { return switch (slot) { case 0 -> kind() <= 1; case 1 -> true; case 2 -> kind() == 3; case 3 -> kind() == 0 || kind() == 3; case 4 -> kind() != 2; default -> kind() == 0 || kind() == 3; }; }
                @Override public int getMaxStackSize() { return slot == 1 && (kind() == 1 || kind() == 2) ? 1 : super.getMaxStackSize(); }
            });
        }
        for (int row = 0; row < 3; row++) for (int col = 0; col < 9; col++) addSlot(new Slot(inventory, col + row * 9 + 9, 12 + col * 18, 157 + row * 18));
        for (int col = 0; col < 9; col++) addSlot(new Slot(inventory, col, 12 + col * 18, 215));
        addDataSlots(data);
    }
    public int kind() { return machine.kind(); }
    public int value(int index) { return data.get(index); }
    @Override public boolean stillValid(Player player) { return machine.stillValid(player); }
    @Override public boolean clickMenuButton(Player player, int id) { return stillValid(player) && machine.action(id); }
    @Override public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem(), original = stack.copy();
        if (index < 9) { if (!moveItemStackTo(stack, 9, 45, true)) return ItemStack.EMPTY; }
        else if (!moveItemStackTo(stack, 0, 9, false)) return ItemStack.EMPTY;
        if (stack.getCount() == original.getCount()) return ItemStack.EMPTY;
        if (stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY); else slot.setChanged();
        slot.onTake(player, stack); return original;
    }
}
