package me.almana.innovativecontraptions.content.processing;

import java.util.List;
import me.almana.innovativecontraptions.registry.ICItems;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;

public final class ProcessingHeat {
    public static final int INDUCTION_WORK = 4000, INDUCTION_HEAT = 10000, HEAT_DIVISOR = 30, CENTRIFUGE_HEAT = 5000;
    public static int hotInductionTicks() { return (INDUCTION_WORK + INDUCTION_HEAT / HEAT_DIVISOR - 1) / (INDUCTION_HEAT / HEAT_DIVISOR); }
    public static int bladeStrength(ItemStack stack) {
        return stack.is(ICItems.IRON_SAW_DISC.get()) ? 3 : stack.is(ICItems.STEEL_SAW_DISC.get()) ? 6
                : stack.is(ICItems.DIAMOND_SAW_DISC.get()) ? 9 : 0;
    }
    public static List<Component> describeBlade(ItemStack stack) {
        return List.of(Component.translatable("tooltip.innovativecontraptions.blade_hardness", bladeStrength(stack)));
    }
    public static void centrifuge(ProcessingBlockEntity machine, int required, boolean canWork) {
        int target = -1;
        if (machine.energy > 0) {
            if (!machine.heatingSignal() && canWork) {
                target = Math.min(CENTRIFUGE_HEAT, required);
                machine.heat = Math.min(machine.heat, target);
            } else if (machine.heatingSignal() && machine.heat <= CENTRIFUGE_HEAT) target = CENTRIFUGE_HEAT;
        }
        if (target >= 0 && machine.heat <= target) { machine.heat++; machine.energy--; }
        else machine.heat = Math.max(0, machine.heat - 1);
        machine.setChanged();
    }
    public static void induction(ServerLevel level, ProcessingBlockEntity machine) {
        ItemStack first = smelt(level, machine, 0, 2), second = smelt(level, machine, 3, 4);
        if (machine.progress >= INDUCTION_WORK) {
            finish(machine, 0, 2, first);
            finish(machine, 3, 4, second);
            machine.progress = 0;
            first = smelt(level, machine, 0, 2);
            second = smelt(level, machine, 3, 4);
        }
        boolean canWork = !first.isEmpty() || !second.isEmpty();
        if ((canWork || machine.heatingSignal()) && machine.energy > 0) {
            machine.energy--;
            machine.heat = Math.min(INDUCTION_HEAT, machine.heat + 1);
        } else machine.heat = Math.max(0, machine.heat - 4);
        machine.duration = INDUCTION_WORK;
        machine.status = canWork ? 2 : 0;
        if (!canWork) machine.progress = 0;
        else if (machine.energy >= 15) {
            machine.energy -= 15;
            machine.progress += machine.heat / HEAT_DIVISOR;
            machine.status = 4;
        }
        machine.setChanged();
    }
    private static ItemStack smelt(ServerLevel level, ProcessingBlockEntity machine, int input, int output) {
        var contents = new SingleRecipeInput(machine.getItem(input));
        var recipe = level.recipeAccess().getRecipeFor(RecipeType.SMELTING, contents, level).orElse(null);
        if (recipe == null) return ItemStack.EMPTY;
        //? if >=26.1 {
        ItemStack result = recipe.value().assemble(contents);
        //?} else
        //ItemStack result = recipe.value().assemble(contents, level.registryAccess());
        return machine.canAccept(output, result) ? result : ItemStack.EMPTY;
    }
    private static void finish(ProcessingBlockEntity machine, int input, int output, ItemStack result) {
        if (result.isEmpty()) return;
        machine.getItem(input).shrink(1);
        machine.insertOutput(output, result);
    }
    private ProcessingHeat() { }
}
