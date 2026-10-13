package me.almana.innovativecontraptions.content.processing;

import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

public final class ProcessingFilterMenu extends AbstractContainerMenu {
    private final Player player;
    private final InteractionHand hand;
    private final ItemStack upgrade;
    private final int lockedSlot;
    private final SimpleContainer filters;
    private int flags, charge;
    public ProcessingFilterMenu(int id, Inventory inventory, RegistryFriendlyByteBuf buffer) { this(id, inventory, buffer.readEnum(InteractionHand.class)); }
    public ProcessingFilterMenu(int id, Inventory inventory, InteractionHand hand) {
        super(ProcessingContent.FILTER_MENU.get(), id);
        player = inventory.player; this.hand = hand; upgrade = player.getItemInHand(hand);
        //? if >=26.1 {
        lockedSlot = hand == InteractionHand.MAIN_HAND ? inventory.getSelectedSlot() : 40;
        //?} else
        //lockedSlot = hand == InteractionHand.MAIN_HAND ? inventory.selected : 40;
        var stored = NonNullList.withSize(9, ItemStack.EMPTY);
        upgrade.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(stored);
        filters = new SimpleContainer(stored.toArray(ItemStack[]::new));
        for (int i = 0; i < 9; i++) addSlot(new Slot(filters, i, 12 + i % 3 * 20, 30 + i / 3 * 20) {
            @Override public boolean mayPickup(Player user) { return false; }
            @Override public boolean mayPlace(ItemStack stack) { return false; }
        });
        for (int row = 0; row < 3; row++) for (int col = 0; col < 9; col++) playerSlot(inventory, col + row * 9 + 9, 8 + col * 18, 150 + row * 18);
        for (int col = 0; col < 9; col++) playerSlot(inventory, col, 8 + col * 18, 208);
        sync(0, 0); sync(1, 0); sync(1, 16);
    }
    private void playerSlot(Inventory inventory, int index, int x, int y) {
        addSlot(new Slot(inventory, index, x, y) {
            @Override public boolean mayPickup(Player user) { return index != lockedSlot; }
            @Override public boolean mayPlace(ItemStack stack) { return index != lockedSlot; }
        });
    }
    private void sync(int field, int shift) {
        addDataSlot(new DataSlot() {
            @Override public int get() { return (field == 0 ? upgrade.getOrDefault(ProcessingContent.FILTER_FLAGS.get(), 0) : upgrade.getOrDefault(ProcessingContent.FILTER_CHARGE.get(), 0)) >>> shift & 65535; }
            @Override public void set(int value) { if (field == 0) flags = value; else charge = charge & ~(65535 << shift) | (value & 65535) << shift; }
        });
    }
    public int flags() { return player.level().isClientSide() ? flags : upgrade.getOrDefault(ProcessingContent.FILTER_FLAGS.get(), 0); }
    public int charge() { return player.level().isClientSide() ? charge : upgrade.getOrDefault(ProcessingContent.FILTER_CHARGE.get(), 0); }
    @Override public boolean clickMenuButton(Player user, int button) {
        if (!stillValid(user) || user.level().isClientSide()) return false;
        int value = flags();
        if (button >= 0 && button < 4) upgrade.set(ProcessingContent.FILTER_FLAGS.get(), value ^ 1 << button);
        else if (button == 4) upgrade.set(ProcessingContent.FILTER_FLAGS.get(), value & 15 | ((value >> 4 & 3) + 1) % 4 << 4);
        else if (button == 5 || button == 6) upgrade.set(ProcessingContent.FILTER_CHARGE.get(), (int) Math.clamp((long) charge() + (button == 5 ? -1000 : 1000), 0, Integer.MAX_VALUE));
        else return false;
        return true;
    }
    @Override public void clicked(int slot, int button, ContainerInput action, Player user) {
        if (!stillValid(user)) return;
        if (slot >= 0 && slot < 9) {
            if (action == ContainerInput.PICKUP) {
                filters.setItem(slot, button == 1 ? ItemStack.EMPTY : getCarried().copyWithCount(1));
                persist();
            }
            return;
        }
        if (action == ContainerInput.SWAP && button == lockedSlot) return;
        super.clicked(slot, button, action, user);
    }
    private void persist() {
        if (player.level().isClientSide()) return;
        var values = NonNullList.withSize(9, ItemStack.EMPTY);
        for (int i = 0; i < 9; i++) values.set(i, filters.getItem(i).copyWithCount(1));
        upgrade.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(values));
    }
    @Override public boolean stillValid(Player user) { return user.getItemInHand(hand) == upgrade && ProcessingFilter.advanced(upgrade); }
    @Override public ItemStack quickMoveStack(Player user, int index) {
        if (index < 9 || !slots.get(index).mayPickup(user)) return ItemStack.EMPTY;
        ItemStack stack = slots.get(index).getItem();
        for (int i = 0; i < 9; i++) if (filters.getItem(i).isEmpty()) { filters.setItem(i, stack.copyWithCount(1)); persist(); break; }
        return ItemStack.EMPTY;
    }
}
