package me.almana.innovativecontraptions.client.gathering;

import java.util.ArrayList;
import java.util.List;
import me.almana.innovativecontraptions.InnovativeContraptions;
import me.almana.innovativecontraptions.content.gathering.GatheringContent;
import me.almana.innovativecontraptions.registry.ICItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;

@JeiPlugin
public final class GatheringJeiPlugin implements IModPlugin {
    public static final IRecipeType<GatheringJeiRecipe> TYPE = IRecipeType.create(InnovativeContraptions.MODID, "resource_gathering", GatheringJeiRecipe.class);
    @Override public Identifier getPluginUid() { return Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, "gathering_jei"); }
    @Override public void registerCategories(IRecipeCategoryRegistration registration) { registration.addRecipeCategories(new GatheringRecipeCategory(registration.getJeiHelpers().getGuiHelper())); }
    @Override public void registerRecipes(IRecipeRegistration registration) {
        List<GatheringJeiRecipe> recipes = new ArrayList<>();
        for (var scanner : List.of(ICItems.OD_SCANNER.get(), ICItems.OV_SCANNER.get())) {
            for (var drill : List.of(ICItems.MINING_DRILL.get(), ICItems.DIAMOND_DRILL.get(), ICItems.IRIDIUM_DRILL.get())) recipes.add(new GatheringJeiRecipe(0, drill, scanner, null));
            recipes.add(new GatheringJeiRecipe(1, Items.AIR, scanner, null));
        }
        recipes.add(new GatheringJeiRecipe(2, ICItems.EMPTY_CELL.get(), Items.AIR, null));
        recipes.add(new GatheringJeiRecipe(2, Items.BUCKET, Items.AIR, null));
        for (var program : List.of(GatheringContent.FROST.get(), GatheringContent.PLANTING.get(), GatheringContent.DRYING.get(), GatheringContent.LEVELING.get(), GatheringContent.WATERING.get(), GatheringContent.FUNGI.get())) {
            recipes.add(new GatheringJeiRecipe(3, program, Items.AIR, program));
        }
        registration.addRecipes(TYPE, recipes);
    }
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addCraftingStation(TYPE, ICItems.MINER.get()); registration.addCraftingStation(TYPE, ICItems.ADVANCED_MINER.get());
        registration.addCraftingStation(TYPE, ICItems.PUMP.get()); registration.addCraftingStation(TYPE, GatheringContent.TERRAIN_ITEM.get());
    }
    @Override public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) { registration.addRecipeTransferHandler(new GatheringTransfer(registration.getTransferHelper())); }
}
