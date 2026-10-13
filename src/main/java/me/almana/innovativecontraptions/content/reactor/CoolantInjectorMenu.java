package me.almana.innovativecontraptions.content.reactor;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public final class CoolantInjectorMenu extends AbstractContainerMenu {
    public static final int REFILL_X = 62, REFILL_Y = 20, UPGRADE_X = 152, UPGRADE_Y = 20, INVENTORY_Y = 106, HOTBAR_Y = 164;
    private final CoolantInjectorBlockEntity injector;
    private final ContainerData data;

    public CoolantInjectorMenu(int id, Inventory inventory, RegistryFriendlyByteBuf buffer) {
        this(id, inventory, (CoolantInjectorBlockEntity) inventory.player.level().getBlockEntity(buffer.readBlockPos()));
    }

    public CoolantInjectorMenu(int id, Inventory inventory, CoolantInjectorBlockEntity injector) {
        super(ReactorContent.INJECTOR_MENU.get(), id);
        this.injector = injector;
        this.data = inventory.player.level().isClientSide() ? new SimpleContainerData(6) : injector.data;
        for (int slot = 0; slot < CoolantInjectorBlockEntity.SLOTS; slot++) {
            int index = slot;
            boolean refill = slot < CoolantInjectorBlockEntity.REFILLS;
            int x = refill ? REFILL_X + slot % 3 * 18 : UPGRADE_X;
            int y = refill ? REFILL_Y + slot / 3 * 18 : UPGRADE_Y + (slot - CoolantInjectorBlockEntity.REFILLS) * 18;
            addSlot(new Slot(injector, index, x, y) {
                @Override public boolean mayPlace(ItemStack stack) { return injector.canPlaceItem(index, stack); }
            });
        }
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, INVENTORY_Y + row * 18));
        }
        for (int col = 0; col < 9; col++) addSlot(new Slot(inventory, col, 8 + col * 18, HOTBAR_Y));
        addDataSlots(data);
    }

    public int energy() { return data.get(1) << 16 | data.get(0) & 0xFFFF; }

    @Override public boolean stillValid(Player player) { return injector.stillValid(player); }

    @Override public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack moved = stack.copy();
        int machine = CoolantInjectorBlockEntity.SLOTS;
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
