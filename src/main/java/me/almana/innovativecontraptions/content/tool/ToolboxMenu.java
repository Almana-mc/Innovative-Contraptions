package me.almana.innovativecontraptions.content.tool;

import me.almana.innovativecontraptions.registry.ICMenus;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

public final class ToolboxMenu extends AbstractContainerMenu {
    public static final int SIZE = 8;
    private final Player player;
    private final InteractionHand hand;
    private final ItemStack toolbox;
    private final SimpleContainer contents;
    private final int lockedSlot;
    public ToolboxMenu(int id, Inventory inventory, RegistryFriendlyByteBuf buffer) { this(id, inventory, buffer.readEnum(InteractionHand.class)); }
    public ToolboxMenu(int id, Inventory inventory, InteractionHand hand) {
        super(ICMenus.TOOLBOX.get(), id); this.player = inventory.player; this.hand = hand;
        //? if >=26.1 {
        toolbox = player.getItemInHand(hand); lockedSlot = hand == InteractionHand.MAIN_HAND ? inventory.getSelectedSlot() : 40;
        //?} else
        //toolbox = player.getItemInHand(hand); lockedSlot = hand == InteractionHand.MAIN_HAND ? inventory.selected : 40;
        var stored = NonNullList.withSize(SIZE, ItemStack.EMPTY);
        toolbox.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(stored);
        contents = new SimpleContainer(stored.toArray(ItemStack[]::new)) {
            @Override public void setChanged() { super.setChanged(); persist(); }
        };
        for (int index = 0; index < SIZE; index++) addSlot(new Slot(contents, index, 53 + index % 4 * 18, 18 + index / 4 * 18) {
            @Override public boolean mayPlace(ItemStack stack) { return !(stack.getItem() instanceof ToolboxItem) && stack.canFitInsideContainerItems(); }
        });
        for (int row = 0; row < 3; row++) for (int col = 0; col < 9; col++) playerSlot(inventory, col + row * 9 + 9, 8 + col * 18, 67 + row * 18);
        for (int col = 0; col < 9; col++) playerSlot(inventory, col, 8 + col * 18, 125);
    }
    private void playerSlot(Inventory inventory, int index, int x, int y) {
        addSlot(new Slot(inventory, index, x, y) {
            @Override public boolean mayPickup(Player player) { return index != lockedSlot; }
            @Override public boolean mayPlace(ItemStack stack) { return index != lockedSlot; }
        });
    }
    private void persist() { if (!player.level().isClientSide()) toolbox.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(contents.getItems())); }
    @Override public boolean stillValid(Player player) { return player.getItemInHand(hand) == toolbox; }
    @Override public void clicked(int slot, int button, ContainerInput action, Player player) {
        if (action == ContainerInput.SWAP && button == lockedSlot) return;
        super.clicked(slot, button, action, player);
    }
    @Override public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem() || !slot.mayPickup(player)) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem(), copy = stack.copy();
        if (index < SIZE ? !moveItemStackTo(stack, SIZE, slots.size(), true) : !moveItemStackTo(stack, 0, SIZE, false)) return ItemStack.EMPTY;
        if (stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY); else slot.setChanged();
        slot.onTake(player, stack); persist(); return copy;
    }
}
