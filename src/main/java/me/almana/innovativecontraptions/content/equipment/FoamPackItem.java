package me.almana.innovativecontraptions.content.equipment;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
//? if >=26.1
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
//? if >=26.1
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;

//? if >=26.1 {
public final class FoamPackItem extends Item {
//?} else
//public final class FoamPackItem extends WornItem {
    public static final int CAPACITY = 80_000;
    public FoamPackItem(Properties props) { super(GearProperties.worn(props, EquipmentSlot.CHEST, "utility")); }
    @Override
    //? if >=26.1 {
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display,
                                Consumer<Component> tooltip, TooltipFlag flag) {
    //?} else {
    /*public void appendHoverText(ItemStack stack, TooltipContext context, java.util.List<Component> components, TooltipFlag flag) {
        Consumer<Component> tooltip = components::add;
    *///?}
        tooltip.accept(Component.translatable("tooltip.innovativecontraptions.fluid_cell_amount",
                stack.getOrDefault(EquipmentData.FOAM.get(), 0), CAPACITY));
    }
}
