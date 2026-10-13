package me.almana.innovativecontraptions.content.energy.cable;

import me.almana.innovativecontraptions.content.energy.EuTier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
//? if >=26.1
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public class CableBlockItem extends BlockItem {
    public CableBlockItem(CableBlock block, Properties properties) {
        super(block, properties);
    }

    @Override
    //? if >=26.1 {
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag flag) {
    //?} else {
    /*public void appendHoverText(ItemStack stack, Item.TooltipContext context, java.util.List<Component> components, TooltipFlag flag) {
        Consumer<Component> builder = components::add;
    *///?}
        CableSpec spec = ((CableBlock) getBlock()).spec();
        EuTier tier = EuTier.fromVolts(spec.maxVoltage());
        String tierLabel = tier != null ? " (" + tier.name() + ")" : "";
        builder.accept(Component.literal("Max: " + spec.maxVoltage() + " EU" + tierLabel)
                .withStyle(ChatFormatting.GOLD));
        builder.accept(Component.literal("Loss: " + spec.lossPerBlock() + " EU/block")
                .withStyle(ChatFormatting.GRAY));
        if (spec.insulated()) {
            builder.accept(Component.literal("Insulated").withStyle(ChatFormatting.GREEN));
        } else {
            builder.accept(Component.literal("Uninsulated — shocks entities").withStyle(ChatFormatting.RED));
        }
    }
}
