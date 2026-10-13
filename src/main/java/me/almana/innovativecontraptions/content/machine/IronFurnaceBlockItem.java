package me.almana.innovativecontraptions.content.machine;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
//? if >=26.1
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public class IronFurnaceBlockItem extends BlockItem {
    public IronFurnaceBlockItem(IronFurnaceBlock block, Properties properties) {
        super(block, properties);
    }

    @Override
    //? if >=26.1 {
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag flag) {
    //?} else {
    /*public void appendHoverText(ItemStack stack, Item.TooltipContext context, java.util.List<Component> components, TooltipFlag flag) {
        Consumer<Component> builder = components::add;
    *///?}
        int fuelBonus = Math.round((IronFurnaceBlockEntity.FUEL_EFFICIENCY - 1.0F) * 100.0F);
        int speedBonus = Math.round((IronFurnaceBlockEntity.SPEED_MULTIPLIER - 1.0F) * 100.0F);
        builder.accept(Component.literal(speedBonus + "% faster than a furnace").withStyle(ChatFormatting.GRAY));
        builder.accept(Component.literal(fuelBonus + "% more fuel-efficient than a furnace").withStyle(ChatFormatting.GRAY));
    }
}
