package me.almana.innovativecontraptions.content.reactor;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public final class ReactorMenu extends AbstractContainerMenu {
    public static final int GRID_X = 26, GRID_Y = 18, INVENTORY_X = 26, INVENTORY_Y = 150, HOTBAR_Y = 208;
    public static final int[][] CELLS = {{8, 18}, {8, 108}, {190, 18}, {190, 108}};
    private final NuclearReactorBlockEntity reactor;
    private final ContainerData data;

    public ReactorMenu(int id, Inventory inventory, RegistryFriendlyByteBuf buffer) {
        this(id, inventory, (NuclearReactorBlockEntity) inventory.player.level().getBlockEntity(buffer.readBlockPos()));
    }

    public ReactorMenu(int id, Inventory inventory, NuclearReactorBlockEntity reactor) {
        super(ReactorContent.MENU.get(), id);
        this.reactor = reactor;
        this.data = inventory.player.level().isClientSide() ? new SimpleContainerData(NuclearReactorBlockEntity.DATA) : reactor.data;
        for (int slot = 0; slot < NuclearReactorBlockEntity.GRID; slot++) {
            int column = slot % ReactorGrid.MAX_COLS;
            addSlot(new Slot(reactor, slot, GRID_X + column * 18, GRID_Y + slot / ReactorGrid.MAX_COLS * 18) {
                @Override public boolean mayPlace(ItemStack stack) { return column < columns() && NuclearReactorBlockEntity.fits(stack, fluid()); }
                @Override public int getMaxStackSize() { return 1; }
                @Override public boolean isActive() { return column < columns(); }
            });
        }
        for (int cell = 0; cell < CELLS.length; cell++) {
            int slot = NuclearReactorBlockEntity.COOLANT_IN + cell;
            addSlot(new Slot(reactor, slot, CELLS[cell][0], CELLS[cell][1]) {
                @Override public boolean mayPlace(ItemStack stack) { return fluid() && NuclearReactorBlockEntity.cellFits(slot, stack); }
                @Override public boolean isActive() { return fluid(); }
            });
        }
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) addSlot(new Slot(inventory, col + row * 9 + 9, INVENTORY_X + col * 18, INVENTORY_Y + row * 18));
        }
        for (int col = 0; col < 9; col++) addSlot(new Slot(inventory, col, INVENTORY_X + col * 18, HOTBAR_Y));
        addDataSlots(data);
    }

    public int heat() { return wide(0); }
    public int maxHeat() { return wide(2); }
    public int euPerTick() { return wide(4); }
    public int huPerSecond() { return wide(6); }
    public int columns() { return data.get(8); }
    public boolean fluid() { return data.get(9) == 1; }
    public int coolant() { return data.get(10); }
    public int hotCoolant() { return data.get(11); }
    private int wide(int index) { return data.get(index + 1) << 16 | data.get(index) & 0xFFFF; }

    @Override public boolean stillValid(Player player) { return reactor.stillValid(player); }

    @Override public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack moved = stack.copy();
        int machine = NuclearReactorBlockEntity.SLOTS;
        if (index < machine) {
            if (!moveItemStackTo(stack, machine, machine + 36, true)) return ItemStack.EMPTY;
        } else if (!moveItemStackTo(stack, 0, machine, false)) return ItemStack.EMPTY;
        if (stack.getCount() == moved.getCount()) return ItemStack.EMPTY;
        if (stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
        else slot.setChanged();
        slot.onTake(player, stack);
        return moved;
    }
}
