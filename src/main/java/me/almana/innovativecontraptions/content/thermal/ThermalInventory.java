package me.almana.innovativecontraptions.content.thermal;

import me.almana.innovativecontraptions.compat.SavedBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public abstract class ThermalInventory extends SavedBlockEntity implements WorldlyContainer, MenuProvider {
    protected final NonNullList<ItemStack> items;
    public final ContainerData data = new ContainerData() {
        public int get(int index) { return displayValue(index); }
        public void set(int index, int value) { receiveDisplayValue(index, value); }
        public int getCount() { return dataCount(); }
    };

    protected int dataCount() { return 6; }

    protected ThermalInventory(BlockEntityType<?> type, BlockPos pos, BlockState state, int size) {
        super(type, pos, state);
        items = NonNullList.withSize(size, ItemStack.EMPTY);
    }

    protected abstract int displayValue(int index);
    protected abstract void receiveDisplayValue(int index, int value);
    public abstract void tick();
    @Override public Component getDisplayName() { return getBlockState().getBlock().getName(); }
    @Override public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) { return new ThermalMenu(id, inventory, this); }
    @Override public int getContainerSize() { return items.size(); }
    @Override public boolean isEmpty() { return items.stream().allMatch(ItemStack::isEmpty); }
    @Override public ItemStack getItem(int slot) { return items.get(slot); }
    @Override public ItemStack removeItem(int slot, int count) {
        ItemStack stack = ContainerHelper.removeItem(items, slot, count);
        if (!stack.isEmpty()) setChanged();
        return stack;
    }
    @Override public ItemStack removeItemNoUpdate(int slot) { return ContainerHelper.takeItem(items, slot); }
    @Override public void setItem(int slot, ItemStack stack) { items.set(slot, stack); setChanged(); }
    @Override public boolean stillValid(Player player) { return Container.stillValidBlockEntity(this, player); }
    @Override public void clearContent() { items.clear(); setChanged(); }
    @Override public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction side) { return side != Direction.DOWN && canPlaceItem(slot, stack); }

    protected boolean fits(int slot, ItemStack result) {
        ItemStack existing = items.get(slot);
        return result.getCount() <= result.getMaxStackSize() && (existing.isEmpty()
                || ItemStack.isSameItemSameComponents(existing, result)
                && existing.getCount() + result.getCount() <= existing.getMaxStackSize());
    }

    protected void insert(int slot, ItemStack result) {
        if (items.get(slot).isEmpty()) items.set(slot, result.copy());
        else items.get(slot).grow(result.getCount());
    }

    @Override protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, items);
    }

    @Override protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        ContainerHelper.loadAllItems(input, items);
    }
}
