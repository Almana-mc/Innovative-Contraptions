package me.almana.innovativecontraptions.datagen;

import me.almana.innovativecontraptions.InnovativeContraptions;
import me.almana.innovativecontraptions.content.thermal.SteelmakingRecipe;
import me.almana.innovativecontraptions.registry.ICItems;
import me.almana.innovativecontraptions.registry.ICTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Recipe.CommonInfo;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.crafting.CompoundIngredient;

public final class ThermalRecipeProvider {
    public static void build(HolderLookup.Provider registries, RecipeOutput output) {
        SteelmakingRecipe recipe = new SteelmakingRecipe(new CommonInfo(true),
                CompoundIngredient.of(RecipeTags.tag(registries, Tags.Items.INGOTS_IRON), RecipeTags.tag(registries, Tags.Items.ORES_IRON),
                        RecipeTags.tag(registries, Tags.Items.RAW_MATERIALS_IRON), RecipeTags.tag(registries, ICTags.DUSTS_IRON),
                        RecipeTags.tag(registries, ICTags.CRUSHED_ORES_IRON), RecipeTags.tag(registries, ICTags.PURIFIED_CRUSHED_ORES_IRON)),
                new ItemStackTemplate(ICItems.STEEL_INGOT.get(), 1), new ItemStackTemplate(ICItems.SLAG.get(), 1), 6000, 6000);
        var id = Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, "steelmaking/steel");
        //? if >=26.1 {
        output.accept(ResourceKey.create(Registries.RECIPE, id), recipe, null);
        //?} else
        //output.accept(id, recipe, null);
    }
    private ThermalRecipeProvider() {}
}
