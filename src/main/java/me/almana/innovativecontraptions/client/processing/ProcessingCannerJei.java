package me.almana.innovativecontraptions.client.processing;

import java.util.ArrayList;
import java.util.List;
import me.almana.innovativecontraptions.InnovativeContraptions;
import me.almana.innovativecontraptions.client.ICClientRecipes;
import me.almana.innovativecontraptions.content.fluid.FluidCellItem;
import me.almana.innovativecontraptions.content.processing.*;
import me.almana.innovativecontraptions.registry.ICItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.registration.*;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;

@JeiPlugin
public final class ProcessingCannerJei implements IModPlugin {
    static final IRecipeType<ProcessingCannerDisplay> TYPE = IRecipeType.create(InnovativeContraptions.MODID, "fluid_packaging", ProcessingCannerDisplay.class);
    @Override public Identifier getPluginUid() { return Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, "fluid_packager_jei"); }
    @Override public void registerCategories(IRecipeCategoryRegistration registration) { registration.addRecipeCategories(new ProcessingCannerCategory(registration.getJeiHelpers().getGuiHelper())); }
    @Override public void registerRecipes(IRecipeRegistration registration) {
        var recipes = new ArrayList<ProcessingCannerDisplay>();
        ICClientRecipes.byType(ProcessingContent.recipeType(ProcessingKind.CANNING)).forEach(holder -> recipes.add(ProcessingCannerDisplay.solid(holder.value())));
        ICClientRecipes.byType(ProcessingContent.ENRICHMENT.get()).forEach(holder -> recipes.add(ProcessingCannerDisplay.enrich(holder.value())));
        for (var entry : ICItems.ITEMS.getEntries()) if (entry.get() instanceof FluidCellItem cell && cell.fluid() != Fluids.EMPTY)
            addContainers(recipes, new ItemStack(cell), new ItemStack(ICItems.EMPTY_CELL.get()), new FluidStack(cell.fluid(), 1000));
        addContainers(recipes, new ItemStack(Items.WATER_BUCKET), new ItemStack(Items.BUCKET), new FluidStack(Fluids.WATER, 1000));
        addContainers(recipes, new ItemStack(Items.LAVA_BUCKET), new ItemStack(Items.BUCKET), new FluidStack(Fluids.LAVA, 1000));
        registration.addRecipes(TYPE, recipes);
    }
    private static void addContainers(List<ProcessingCannerDisplay> recipes, ItemStack full, ItemStack empty, FluidStack fluid) {
        recipes.add(new ProcessingCannerDisplay(1, List.of(), List.of(full), empty, FluidStack.EMPTY, fluid, ProcessingCannerBlockEntity.BASE_TICKS, ProcessingCannerBlockEntity.BASE_EU));
        recipes.add(new ProcessingCannerDisplay(2, List.of(), List.of(empty), full, fluid, FluidStack.EMPTY, ProcessingCannerBlockEntity.BASE_TICKS, ProcessingCannerBlockEntity.BASE_EU));
    }
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) { registration.addCraftingStation(TYPE, ICItems.CANNING_MACHINE.get()); }
    @Override public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) { registration.addRecipeTransferHandler(new ProcessingCannerTransfer(registration.getTransferHelper()), TYPE); }
    @Override public void registerGuiHandlers(IGuiHandlerRegistration registration) { registration.addRecipeClickArea(ProcessingCannerScreen.class, 47, 65, 82, 5, TYPE); }
}
