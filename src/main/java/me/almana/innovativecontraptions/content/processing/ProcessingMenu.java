package me.almana.innovativecontraptions.content.processing;

import me.almana.innovativecontraptions.content.energy.BatteryItem;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;

public final class ProcessingMenu extends AbstractContainerMenu {
    private final ProcessingBlockEntity machine;
    public ProcessingMenu(int id, Inventory inventory, RegistryFriendlyByteBuf buffer) {
        this(id, inventory, (ProcessingBlockEntity) inventory.player.level().getBlockEntity(buffer.readBlockPos()));
    }
    public ProcessingMenu(int id, Inventory inventory, ProcessingBlockEntity machine) {
        super(ProcessingContent.MENU.get(), id);
        this.machine = machine;
        addMachineSlot(0, 54, 40);
        addMachineSlot(1, 54, 62);
        addMachineSlot(2, 98, 40);
        if (washing()) {
            addMachineSlot(3, 98, 58);
            addMachineSlot(4, 98, 76);
            addMachineSlot(5, 12, 40);
            addMachineSlot(6, 12, 62);
        }
        if (kind() == ProcessingKind.CANNING || kind() == ProcessingKind.SLICING) addMachineSlot(3, 12, 40);
        if (kind() == ProcessingKind.INDUCTION) { addMachineSlot(3, 36, 40); addMachineSlot(4, 116, 40); }
        if (kind() == ProcessingKind.CENTRIFUGING) { addMachineSlot(3, 98, 58); addMachineSlot(4, 98, 76); }
        if (machine.upgradeStart() >= 0) {
            for (int slot = 0; slot < machine.upgradeSlots(); slot++) addMachineSlot(machine.upgradeStart() + slot, 12 + slot * 22, 104);
        }
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) addSlot(new Slot(inventory, column + row * 9 + 9, 8 + column * 18, 138 + row * 18));
        }
        for (int column = 0; column < 9; column++) addSlot(new Slot(inventory, column, 8 + column * 18, 196));
        for (int index = 0; index < machine.data.getCount(); index++) {
            addIntegerHalf(index, 0);
            addIntegerHalf(index, 16);
        }
    }
    private void addIntegerHalf(int index, int shift) {
        addDataSlot(new DataSlot() {
            @Override public int get() { return machine.data.get(index) >>> shift & 65535; }
            @Override public void set(int value) {
                machine.data.set(index, machine.data.get(index) & ~(65535 << shift) | (value & 65535) << shift);
            }
        });
    }
    private void addMachineSlot(int index, int x, int y) {
        addSlot(new Slot(machine, index, x, y) {
            @Override public boolean mayPlace(ItemStack stack) { return machine.canPlaceItem(index, stack); }
        });
    }
    public ProcessingKind kind() { return machine.kind(); }
    public boolean washing() { return kind() == ProcessingKind.WASHING; }
    public int machineSlots() { return machine.getContainerSize(); }
    public int value(int index) { return machine.data.get(index); }
    @Override public boolean clickMenuButton(Player player, int id) { return kind() == ProcessingKind.SMELTING && id == 0 ? machine.collectExperience(player) : machine.setMode(id); }
    @Override public boolean stillValid(Player player) { return machine.stillValid(player); }

    @Override public ItemStack quickMoveStack(Player player, int index) {
        if (!(machine.getLevel() instanceof ServerLevel level)) return ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        int playerStart = machineSlots();
        if (index < playerStart) {
            if (!moveItemStackTo(stack, playerStart, playerStart + 36, true)) return ItemStack.EMPTY;
        } else {
            int destination = inputDestination(level, stack);
            if (destination >= 0) {
                int end = destination == machine.upgradeStart() ? destination + machine.upgradeSlots() : destination + 1;
                if (!moveItemStackTo(stack, destination, end, false)) return ItemStack.EMPTY;
            } else if (index < playerStart + 27) {
                if (!moveItemStackTo(stack, playerStart + 27, playerStart + 36, false)) return ItemStack.EMPTY;
            } else if (!moveItemStackTo(stack, playerStart, playerStart + 27, false)) return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
        else slot.setChanged();
        if (original.getCount() == stack.getCount()) return ItemStack.EMPTY;
        slot.onTake(player, stack);
        return original;
    }

    private int inputDestination(ServerLevel level, ItemStack stack) {
        if (machine.canPlaceItem(1, stack)) return 1;
        if (machine.acceptsUpgrade(stack)) return machine.upgradeStart();
        if ((kind() == ProcessingKind.CANNING || kind() == ProcessingKind.SLICING) && machine.canPlaceItem(3, stack)) return 3;
        if (washing() && ProcessingBlockEntity.isWaterContainer(stack)) return 5;
        ItemStack candidate = stack.copyWithCount(stack.getMaxStackSize());
        if (kind() == ProcessingKind.INDUCTION || kind() == ProcessingKind.SMELTING) return level.recipeAccess().getRecipeFor(RecipeType.SMELTING,
                new SingleRecipeInput(candidate), level).isPresent() ? kind() == ProcessingKind.SMELTING || machine.getItem(0).isEmpty() ? 0 : 3 : -1;
        return level.recipeAccess().getRecipeFor(ProcessingContent.recipeType(kind()), new SingleRecipeInput(candidate), level).isPresent() ? 0 : -1;
    }
}
