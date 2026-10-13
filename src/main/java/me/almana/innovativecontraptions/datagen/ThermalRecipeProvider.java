package me.almana.innovativecontraptions.datagen;

import me.almana.innovativecontraptions.InnovativeContraptions;
import me.almana.innovativecontraptions.content.thermal.SteelmakingRecipe;
import me.almana.innovativecontraptions.registry.ICItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe.CommonInfo;

public final class ThermalRecipeProvider {
    public static void build(RecipeOutput output) {
        SteelmakingRecipe recipe = new SteelmakingRecipe(new CommonInfo(true),
                Ingredient.of(Items.IRON_INGOT, Items.IRON_ORE, Items.DEEPSLATE_IRON_ORE, Items.RAW_IRON,
                        ICItems.IRON_DUST.get(), ICItems.CRUSHED_IRON.get(), ICItems.PURIFIED_CRUSHED_IRON.get()),
                new ItemStackTemplate(ICItems.STEEL_INGOT.get(), 1), new ItemStackTemplate(ICItems.SLAG.get(), 1), 6000, 6000);
        var id = Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, "steelmaking/steel");
        //? if >=26.1 {
        output.accept(ResourceKey.create(Registries.RECIPE, id), recipe, null);
        //?} else
        //output.accept(id, recipe, null);
    }
    private ThermalRecipeProvider() {}
}
