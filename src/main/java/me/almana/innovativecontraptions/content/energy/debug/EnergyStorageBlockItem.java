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

public class EnergyStorageBlockItem extends BlockItem {
    public EnergyStorageBlockItem(EnergyStorageBlock block, Properties properties) {
        super(block, properties);
    }

    @Override
    //? if >=26.1 {
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag flag) {
    //?} else {
    /*public void appendHoverText(ItemStack stack, Item.TooltipContext context, java.util.List<Component> components, TooltipFlag flag) {
        Consumer<Component> builder = components::add;
    *///?}
        builder.accept(Component.literal("Capacity: " + String.format("%,d", EnergyStorageBlockEntity.CAPACITY) + " EU")
                .withStyle(ChatFormatting.AQUA));
        builder.accept(Component.literal("Accepts EU from any side").withStyle(ChatFormatting.GRAY));
    }
}
