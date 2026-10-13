package me.almana.innovativecontraptions.content.processing;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
//? if >=26.1
import net.minecraft.world.item.component.TooltipDisplay;

public final class DescribedItem extends Item {
    private final Function<ItemStack, List<Component>> description;
    public DescribedItem(Properties properties, Function<ItemStack, List<Component>> description) {
        super(properties);
        this.description = description;
    }
    //? if >=26.1 {
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display,
            Consumer<Component> lines, TooltipFlag flag) {
    //?} else {
    /*@Override public void appendHoverText(ItemStack stack, TooltipContext context, java.util.List<Component> components, TooltipFlag flag) {
        Consumer<Component> lines = components::add;
    *///?}
        description.apply(stack).forEach(lines);
    }
}
