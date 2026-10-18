package me.almana.innovativecontraptions.content.matter;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import java.util.*;

public final class MatterCosts {
    private static final Map<RecipeManager, Map<Item, Integer>> CACHE = new WeakHashMap<>();
    public static void invalidate() { CACHE.clear(); }
    public static Map<Item, Integer> values(RecipeManager recipes) { return CACHE.computeIfAbsent(recipes, MatterCosts::build); }
    public static int cost(Level level, ItemStack stack) {
        if (!(level instanceof ServerLevel server)) return clientCost(stack);
        if (stack.isEmpty() || !stack.getComponentsPatch().isEmpty()) return 0;
        return CACHE.computeIfAbsent(server.recipeAccess(), MatterCosts::build).getOrDefault(stack.getItem(), 0);
    }
    public static int clientCost(ItemStack stack) {
        return stack.isEmpty() || !stack.getComponentsPatch().isEmpty() ? 0 : MatterCostSync.clientCosts.getOrDefault(stack.getItem(), 0);
    }
    private static Map<Item, Integer> build(RecipeManager recipes) {
        Map<Item, Integer> costs = new HashMap<>();
        for (var holder : recipes.getRecipes()) if (holder.value() instanceof MatterRecipe seed)
            //? if >=26.1 {
            seed.input().items().forEach(item -> costs.merge(item.value(), seed.cost(), Math::min));
            //?} else
            //Arrays.stream(seed.input().getItems()).filter(stack -> stack.getComponentsPatch().isEmpty()).forEach(stack -> costs.merge(stack.getItem(), seed.cost(), Math::min));
        Set<Item> fixed = Set.of(Items.DIAMOND);
        for (int pass = 0; pass < 32; pass++) {
            boolean changed = false;
            for (var holder : recipes.getRecipes()) changed |= MatterDerivation.apply(holder.value(), costs, fixed);
            if (!changed) break;
        }
        return Map.copyOf(costs);
    }
    private MatterCosts() {}
}
