package me.almana.innovativecontraptions.content.thermal;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public final class HeatExchangerMenu extends AbstractContainerMenu {
    public static final int INVENTORY_Y = 118, HOTBAR_Y = 176;
    private final LiquidHeatExchangerBlockEntity exchanger;
    private final ContainerData data;

    public HeatExchangerMenu(int id, Inventory inventory, RegistryFriendlyByteBuf buffer) {
        this(id, inventory, (LiquidHeatExchangerBlockEntity) inventory.player.level().getBlockEntity(buffer.readBlockPos()));
    }

    public HeatExchangerMenu(int id, Inventory inventory, LiquidHeatExchangerBlockEntity exchanger) {
        super(ThermalContent.EXCHANGER_MENU.get(), id);
        this.exchanger = exchanger;
        this.data = inventory.player.level().isClientSide() ? new SimpleContainerData(6) : exchanger.data;
        for (int slot = 0; slot < LiquidHeatExchangerBlockEntity.SLOTS; slot++) {
            int index = slot;
            addSlot(new Slot(exchanger, index, slotX(index), slotY(index)) {
                @Override public boolean mayPlace(ItemStack stack) { return exchanger.canPlaceItem(index, stack); }
                @Override public int getMaxStackSize() { return index < LiquidHeatExchangerBlockEntity.CONDUCTORS ? 1 : super.getMaxStackSize(); }
            });
        }
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, INVENTORY_Y + row * 18));
        }
        for (int col = 0; col < 9; col++) addSlot(new Slot(inventory, col, 8 + col * 18, HOTBAR_Y));
        addDataSlots(data);
    }

    public static int slotX(int slot) {
        if (slot < LiquidHeatExchangerBlockEntity.CONDUCTORS) return 44 + slot % 5 * 18;
        return switch (slot) {
            case LiquidHeatExchangerBlockEntity.HOT_IN, LiquidHeatExchangerBlockEntity.HOT_OUT -> 8;
            case LiquidHeatExchangerBlockEntity.EMPTY_IN, LiquidHeatExchangerBlockEntity.COOLANT_OUT -> 152;
            default -> 62 + (slot - LiquidHeatExchangerBlockEntity.UPGRADES) * 18;
        };
    }

    public static int slotY(int slot) {
        if (slot < LiquidHeatExchangerBlockEntity.CONDUCTORS) return 20 + slot / 5 * 18;
        return slot == LiquidHeatExchangerBlockEntity.HOT_IN || slot == LiquidHeatExchangerBlockEntity.EMPTY_IN ? 20 : 64;
    }

    public int value(int index) { return data.get(index); }

    @Override public boolean stillValid(Player player) { return exchanger.stillValid(player); }

    @Override public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack moved = stack.copy();
        int machine = LiquidHeatExchangerBlockEntity.SLOTS;
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
