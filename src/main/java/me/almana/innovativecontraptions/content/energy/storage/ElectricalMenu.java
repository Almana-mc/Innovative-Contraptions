package me.almana.innovativecontraptions.content.energy.storage;

import me.almana.innovativecontraptions.registry.ICMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ArmorSlot;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public final class ElectricalMenu extends AbstractContainerMenu {
    private final ElectricalBlockEntity entity;
    public ElectricalMenu(int id, Inventory inventory, RegistryFriendlyByteBuf data) {
        this(id, inventory, (ElectricalBlockEntity) inventory.player.level().getBlockEntity(data.readBlockPos()));
    }
    public ElectricalMenu(int id, Inventory inventory, ElectricalBlockEntity entity) {
        super(ICMenus.ELECTRICAL.get(), id);
        this.entity = entity;
        for (int index = 0; index < entity.getContainerSize(); index++) {
            int slot = index;
            addSlot(new Slot(entity, slot, 30 + 66 * slot, 58) {
                @Override public boolean mayPlace(ItemStack stack) { return entity.canPlaceItem(slot, stack); }
            });
        }
        //? if >=26.1 {
        addStandardInventorySlots(inventory, 8, 142);
        //?} else {
        /*for (int row = 0; row < 3; row++) for (int col = 0; col < 9; col++) addSlot(new Slot(inventory, col + (row + 1) * 9, 8 + col * 18, 142 + row * 18));
        for (int col = 0; col < 9; col++) addSlot(new Slot(inventory, col, 8 + col * 18, 200));
        *///?}
        if (!entity.kind.transformer && !entity.kind.pad) addArmorSlots(inventory);
        addDataSlots(entity.data);
    }
    private void addArmorSlots(Inventory inventory) {
        EquipmentSlot[] parts = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
        Identifier[] icons = {InventoryMenu.EMPTY_ARMOR_SLOT_HELMET, InventoryMenu.EMPTY_ARMOR_SLOT_CHESTPLATE,
                InventoryMenu.EMPTY_ARMOR_SLOT_LEGGINGS, InventoryMenu.EMPTY_ARMOR_SLOT_BOOTS};
        for (int i = 0; i < 4; i++) addSlot(new ArmorSlot(inventory, inventory.player, parts[i], 39 - i, 150, 31 + i * 18, icons[i]));
    }
    public ElectricalKind kind() { return entity.kind; }
    public int energy() { return entity.storedEu(); }
    public int mode() { return entity.mode(); }
    public int modeCount() { return entity.modeCount(); }
    public boolean stepUp() { return entity.active(); }
    public int machineSlots() { return entity.getContainerSize(); }
    @Override public boolean stillValid(Player player) { return entity.stillValid(player); }
    @Override public boolean clickMenuButton(Player player, int id) {
        if (!stillValid(player) || id < 0 || id >= modeCount()) return false;
        entity.selectMode(id);
        return true;
    }
    @Override public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack result = stack.copy();
        int count = machineSlots();
        if (index < count || index >= count + 36) {
            if (!moveItemStackTo(stack, count, count + 36, true)) return ItemStack.EMPTY;
        } else if (count > 0 && entity.canPlaceItem(0, stack)) {
            if (!moveItemStackTo(stack, 0, 1, false)) return ItemStack.EMPTY;
        } else if (count > 1 && entity.canPlaceItem(1, stack)) {
            if (!moveItemStackTo(stack, 1, 2, false)) return ItemStack.EMPTY;
        } else if (!moveItemStackTo(stack, count + 36, slots.size(), false)) return ItemStack.EMPTY;
        if (stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
        else slot.setChanged();
        slot.onTake(player, stack);
        return result;
    }
}
