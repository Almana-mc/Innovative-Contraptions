package me.almana.innovativecontraptions.content.processing;

import me.almana.innovativecontraptions.InnovativeContraptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

public final class RecyclingRules {
    public static final int YIELD_DENOMINATOR = 8;
    public static final TagKey<Item> NO_SCRAP = TagKey.create(Registries.ITEM,
            Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, "recycling_no_scrap"));

    public static boolean canProduceScrap(ItemStack input) { return !input.is(NO_SCRAP); }
    public static Ingredient inputs() {
        return Ingredient.of(BuiltInRegistries.ITEM.stream().filter(item -> item != Items.AIR).toArray(Item[]::new));
    }
    private RecyclingRules() {}
}
