package me.almana.innovativecontraptions.content.gathering;

import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
//? if >=26.1
import net.minecraft.world.item.component.TooltipDisplay;

public final class TerrainProgramItem extends Item {
    public final TerrainProgram program;
    public TerrainProgramItem(Properties properties, TerrainProgram program) { super(properties.stacksTo(1)); this.program = program; }
    //? if >=26.1 {
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
    //?} else {
    /*@Override public void appendHoverText(ItemStack stack, TooltipContext context, java.util.List<Component> components, TooltipFlag flag) {
        Consumer<Component> tooltip = components::add;
    *///?}
        tooltip.accept(Component.translatable("tooltip.innovativecontraptions.terrain_program", program.cost, program.range));
    }
}
