package me.almana.innovativecontraptions.content.tool;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
//? if >=26.1
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;

public class FormingTool extends Item {
    public FormingTool(Properties properties) {
        super(properties);
    }

    //? if >=26.1 {
    @Override
    public ItemStackTemplate getCraftingRemainder(ItemInstance instance) {
    //?} else {
    /*@Override
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return getCraftingRemainder(stack) != null;
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack stack) {
        ItemStackTemplate remainder = getCraftingRemainder(stack);
        return remainder == null ? ItemStack.EMPTY : remainder.create();
    }

    private ItemStackTemplate getCraftingRemainder(ItemStack instance) {
    *///?}
        int max = instance.getOrDefault(DataComponents.MAX_DAMAGE, 0);
        int next = instance.getOrDefault(DataComponents.DAMAGE, 0) + 1;
        if (max <= 0 || next >= max) {
            return null;
        }
        //? if >=26.1 {
        ItemStack damaged = new ItemStack(instance.typeHolder());
        //?} else {
        /*ItemStack damaged = new ItemStack(instance.getItemHolder());
        *///?}
        damaged.set(DataComponents.DAMAGE, next);
        return ItemStackTemplate.fromNonEmptyStack(damaged);
    }
}
