package me.almana.innovativecontraptions.content.agriculture;

import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
//? if >=26.1
import net.minecraft.world.item.component.TooltipDisplay;

public final class GeneticSeedsItem extends Item {
    public GeneticSeedsItem(Properties properties) { super(properties.component(FarmingContent.GENOME.get(), new CropGenome("wheat", 1, 1, 1, 0))); }
    //? if >=26.1 {
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display,
                                          Consumer<Component> lines, TooltipFlag flag) {
    //?} else {
    /*@Override public void appendHoverText(ItemStack stack, TooltipContext context, java.util.List<Component> components, TooltipFlag flag) {
        Consumer<Component> lines = components::add;
    *///?}
        describe(stack.getOrDefault(FarmingContent.GENOME.get(), CropGenome.EMPTY), lines);
    }
    public static void describe(CropGenome seed, Consumer<Component> lines) {
        lines.accept(Component.translatable("tooltip.innovativecontraptions.crop_scan", seed.scan()));
        if (seed.scan() > 0) lines.accept(Component.translatable("crop.innovativecontraptions." + seed.species()));
        if (seed.scan() >= 4) lines.accept(Component.translatable("tooltip.innovativecontraptions.crop_genes", seed.growth(), seed.gain(), seed.resistance()));
    }
}
