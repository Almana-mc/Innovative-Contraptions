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
public final class FuelFlightItem extends Item {
//?} else
//public final class FuelFlightItem extends WornItem {
    public static final int CAPACITY = 30_000;
    public FuelFlightItem(Properties props) { super(GearProperties.worn(props, EquipmentSlot.CHEST, "utility")); }
    public static int fuel(ItemStack stack) { return stack.getOrDefault(EquipmentData.FUEL.get(), 0); }
    public static int receiveFuel(ItemStack stack, int amount) {
        int accepted = Math.max(0, Math.min(amount, CAPACITY - fuel(stack)));
        stack.set(EquipmentData.FUEL.get(), fuel(stack) + accepted);
        return accepted;
    }
    @Override
    public boolean isBarVisible(ItemStack stack) { return true; }
    @Override
    public int getBarWidth(ItemStack stack) { return Math.round(13F * fuel(stack) / CAPACITY); }
    @Override
    public int getBarColor(ItemStack stack) { return 0x63B45C; }
    @Override
    //? if >=26.1 {
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display,
                                Consumer<Component> tooltip, TooltipFlag flag) {
    //?} else {
    /*public void appendHoverText(ItemStack stack, TooltipContext context, java.util.List<Component> components, TooltipFlag flag) {
        Consumer<Component> tooltip = components::add;
    *///?}
        tooltip.accept(Component.translatable("tooltip.innovativecontraptions.flight_fuel", fuel(stack), CAPACITY));
    }
}
