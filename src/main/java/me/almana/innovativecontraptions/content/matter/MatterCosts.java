package me.almana.innovativecontraptions.content.matter;

import me.almana.innovativecontraptions.InnovativeContraptions;
import me.almana.innovativecontraptions.content.energy.net.EnergySettings;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import java.util.*;

public final class MatterCosts {
    private static final String PREFIX = "uu_value_";
    private static final Map<RecipeManager, Map<Item, Integer>> CACHE = new WeakHashMap<>();
    public static TagKey<Item> valueTag(int uu) { return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, PREFIX + uu)); }
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
        for (Item item : BuiltInRegistries.ITEM) BuiltInRegistries.ITEM.wrapAsHolder(item).tags().forEach(tag -> {
            int uu = uuValue(tag);
            if (uu > 0) costs.merge(item, uu * 1000, Math::min);
        });
        if (!EnergySettings.DERIVE_UU_VALUES.get()) return Map.copyOf(costs);
        Set<Item> fixed = Set.copyOf(costs.keySet());
        for (int pass = 0; pass < 32; pass++) {
            boolean changed = false;
            for (var holder : recipes.getRecipes()) changed |= MatterDerivation.apply(holder.value(), costs, fixed);
            if (!changed) break;
        }
        return Map.copyOf(costs);
    }
    private static int uuValue(TagKey<Item> tag) {
        String path = tag.location().getPath();
        if (!tag.location().getNamespace().equals(InnovativeContraptions.MODID) || !path.startsWith(PREFIX)) return 0;
        String digits = path.substring(PREFIX.length());
        return digits.matches("[1-9][0-9]{0,5}") ? Integer.parseInt(digits) : 0;
    }
    private MatterCosts() {}
}
