package me.almana.innovativecontraptions.content.matter;

import com.mojang.serialization.Codec;
import me.almana.innovativecontraptions.InnovativeContraptions;
import me.almana.innovativecontraptions.content.energy.net.EnergySettings;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import java.util.*;

public final class MatterCosts {
    public static final DataMapType<Item, Double> UU_VALUE = DataMapType.builder(Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, "uu_value"),
            Registries.ITEM, Codec.doubleRange(0.001, 999999)).build();
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
        BuiltInRegistries.ITEM.getDataMap(UU_VALUE).forEach((key, uu) -> costs.put(BuiltInRegistries.ITEM.getValue(key), (int) Math.max(1, Math.round(uu * 1000))));
        if (!EnergySettings.DERIVE_UU_VALUES.get()) return Map.copyOf(costs);
        Set<Item> fixed = Set.copyOf(costs.keySet());
        for (int pass = 0; pass < 32; pass++) {
            boolean changed = false;
            for (var holder : recipes.getRecipes()) changed |= MatterDerivation.apply(holder.value(), costs, fixed);
            if (!changed) break;
        }
        return Map.copyOf(costs);
    }
    private MatterCosts() {}
}
