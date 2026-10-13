package me.almana.innovativecontraptions.content.energy.debug;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
//? if >=26.1
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public class CreativeSourceBlockItem extends BlockItem {
    public CreativeSourceBlockItem(CreativeSourceBlock block, Properties properties) {
        super(block, properties);
    }

    @Override
    //? if >=26.1 {
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag flag) {
    //?} else {
    /*public void appendHoverText(ItemStack stack, Item.TooltipContext context, java.util.List<Component> components, TooltipFlag flag) {
        Consumer<Component> builder = components::add;
    *///?}
        builder.accept(Component.literal("Creative Only").withStyle(ChatFormatting.LIGHT_PURPLE));
        builder.accept(Component.literal("Right-click to cycle voltage tier").withStyle(ChatFormatting.GRAY));
        builder.accept(Component.literal("Infinite EU source for testing").withStyle(ChatFormatting.DARK_GRAY));
    }
}
