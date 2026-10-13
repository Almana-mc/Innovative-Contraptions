package me.almana.innovativecontraptions.content.matter;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.item.ItemStackTemplate;
//? if >=26.1
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;

public final class MatterMemoryItem extends Item {
    public MatterMemoryItem(Properties properties) { super(properties.stacksTo(1)); }
    public static ItemStack pattern(ItemStack crystal) {
        var template = crystal.get(MatterContent.PATTERN.get());
        return template == null ? ItemStack.EMPTY : template.create();
    }
    public static void record(ItemStack crystal, ItemStack pattern) {
        crystal.set(MatterContent.PATTERN.get(), ItemStackTemplate.fromNonEmptyStack(pattern).withCount(1));
    }
    //? if >=26.1 {
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
    //?} else {
    /*@Override public void appendHoverText(ItemStack stack, TooltipContext context, java.util.List<Component> components, TooltipFlag flag) {
        Consumer<Component> tooltip = components::add;
    *///?}
        ItemStack pattern = pattern(stack);
        tooltip.accept(pattern.isEmpty() ? Component.translatable("matter.innovativecontraptions.blank_memory")
                : Component.translatable("matter.innovativecontraptions.recorded_memory", pattern.getHoverName()));
    }
}
