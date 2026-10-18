package me.almana.innovativecontraptions.content.reactor;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.neoforged.neoforge.common.Tags;

public final class CondensatorRefill {
    public static final int RSH_REDSTONE = 10_000, LZH_REDSTONE = 5_000, LZH_LAPIS = 40_000;

    public static boolean refills(Item item) {
        return item instanceof ReactorComponentItem component && component.part().kind == ReactorPart.Kind.CONDENSATOR;
    }

    public static int value(ReactorPart part, ItemStack stack) {
        if (stack.is(Tags.Items.DUSTS_REDSTONE)) return part == ReactorPart.RSH_CONDENSATOR ? RSH_REDSTONE : LZH_REDSTONE;
        return part == ReactorPart.LZH_CONDENSATOR && stack.is(Tags.Items.GEMS_LAPIS) ? LZH_LAPIS : 0;
    }

    public static ItemStack apply(CraftingInput input, ItemStack result) {
        ReactorPart part = ((ReactorComponentItem) result.getItem()).part();
        ItemStack source = ItemStack.EMPTY;
        int restored = 0;
        for (int slot = 0; slot < input.size(); slot++) {
            ItemStack stack = input.getItem(slot);
            if (stack.is(result.getItem())) source = stack;
            else restored += value(part, stack);
        }
        if (!source.isEmpty()) ReactorComponentItem.storeDamage(result, Math.max(0, ReactorComponentItem.damage(source) - restored));
        return result;
    }

    private CondensatorRefill() {}
}
