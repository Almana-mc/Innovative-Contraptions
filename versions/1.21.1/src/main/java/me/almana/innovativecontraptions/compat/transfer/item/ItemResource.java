package me.almana.innovativecontraptions.compat.transfer.item;

import java.util.Objects;
import me.almana.innovativecontraptions.compat.transfer.TransferPreconditions;
import me.almana.innovativecontraptions.compat.transfer.resource.Resource;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

public final class ItemResource implements Resource, DataComponentHolder {
    public static final ItemResource EMPTY = new ItemResource(ItemStack.EMPTY);

    public static ItemResource of(ItemStack stack) {
        if (stack.isEmpty() || stack.isComponentsPatchEmpty()) {
            return of(stack.getItem());
        }
        return new ItemResource(stack.copyWithCount(1));
    }

    public static ItemResource of(ItemLike item) {
        Item value = item.asItem();
        if (value == Items.AIR) return EMPTY;
        return new ItemResource(new ItemStack(value));
    }

    private final ItemStack innerStack;

    private ItemResource(ItemStack stack) {
        this.innerStack = stack;
    }

    public Item getItem() {
        return innerStack.getItem();
    }

    @Override
    public boolean isEmpty() {
        return innerStack.isEmpty();
    }

    public boolean matches(ItemStack stack) {
        return ItemStack.isSameItemSameComponents(stack, innerStack);
    }

    public boolean is(ItemLike item) {
        return getItem() == item.asItem();
    }

    public <D> ItemResource with(DataComponentType<D> type, @Nullable D data) {
        if (isEmpty()) return EMPTY;
        if (Objects.equals(get(type), data)) return this;
        ItemStack stack = innerStack.copy();
        stack.set(type, data);
        return of(stack);
    }

    @Override
    public DataComponentMap getComponents() {
        return innerStack.getComponents();
    }

    public ItemStack toStack(int count) {
        TransferPreconditions.checkNonNegative(count);
        if (count == 0) return ItemStack.EMPTY;
        return innerStack.copyWithCount(count);
    }

    public ItemStack toStack() {
        return innerStack.copyWithCount(1);
    }

    public int getMaxStackSize() {
        return innerStack.getMaxStackSize();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        return ItemStack.isSameItemSameComponents(innerStack, ((ItemResource) obj).innerStack);
    }

    @Override
    public int hashCode() {
        return ItemStack.hashItemAndComponents(innerStack);
    }

    @Override
    public String toString() {
        return getItem() + " [" + innerStack.getComponentsPatch().size() + "]";
    }
}
