package me.almana.innovativecontraptions.client.agriculture;

import me.almana.innovativecontraptions.content.agriculture.CropGenome;
import me.almana.innovativecontraptions.content.agriculture.CropProducts;
import me.almana.innovativecontraptions.content.agriculture.FarmingContent;
import me.almana.innovativecontraptions.content.agriculture.FarmingMachineBlockEntity;
import me.almana.innovativecontraptions.registry.ICItems;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class FarmingRecipeCategory extends AbstractRecipeCategory<FarmingJeiRecipe> {
    public FarmingRecipeCategory(IGuiHelper gui, boolean care) {
        super(care ? FarmingJeiPlugin.CARE : FarmingJeiPlugin.HARVEST,
                Component.translatable("jei.innovativecontraptions.farming." + (care ? "care" : "harvesting")),
                gui.createDrawableItemLike(care ? ICItems.CROP_MATRON.get() : ICItems.CROP_HARVESTER.get()), 210, 153);
    }
    @Override public void setRecipe(IRecipeLayoutBuilder builder, FarmingJeiRecipe recipe, IFocusGroup focuses) {
        if (recipe.care()) {
            var input = builder.addInputSlot(8, 8).setStandardSlotBackground().add(new ItemStack(recipe.supply() == 0 ? ICItems.FERTILIZER.get()
                    : recipe.supply() == 1 ? ICItems.WATER_CELL.get() : ICItems.CROP_TREATMENT_CELL.get()));
            //? if >=26.1 {
            if (recipe.supply() == 1) input.add(Items.WATER_BUCKET);
            //?} else
            //if (recipe.supply() == 1) input.addItemLike(Items.WATER_BUCKET);
        } else {
            ItemStack seed = new ItemStack(ICItems.GENETIC_SEEDS.get());
            seed.set(FarmingContent.GENOME.get(), new CropGenome(recipe.crop().id(), 1, 1, 1, 4));
            builder.addSlot(RecipeIngredientRole.CRAFTING_STATION, 8, 8).setStandardSlotBackground().add(seed);
            var results = CropProducts.possible(recipe.crop());
            for (int index = 0; index < results.size(); index++) builder.addOutputSlot(66 + index * 20, 8).setStandardSlotBackground().add(results.get(index));
        }
    }
    @Override public void createRecipeExtras(IRecipeExtrasBuilder builder, FarmingJeiRecipe recipe, IFocusGroup focuses) {
        builder.addText(Component.translatable("jei.innovativecontraptions.farming.scan", FarmingMachineBlockEntity.SCAN_COST,
                FarmingMachineBlockEntity.SCAN_INTERVAL), 210, 22).setPosition(0, 33);
        if (recipe.care()) {
            builder.addText(Component.translatable("jei.innovativecontraptions.farming.care_cost", FarmingMachineBlockEntity.CARE_COST,
                    FarmingMachineBlockEntity.SCAN_COST), 210, 22).setPosition(0, 56);
            String supply = recipe.supply() == 0 ? "fertilizer" : recipe.supply() == 1 ? "water" : "treatment";
            builder.addText(Component.translatable("jei.innovativecontraptions.farming." + supply), 210, 28).setPosition(0, 80);
        } else {
            builder.addText(Component.translatable("jei.innovativecontraptions.farming.harvest", FarmingMachineBlockEntity.HARVEST_COST), 210, 22).setPosition(0, 56);
            builder.addText(Component.translatable("jei.innovativecontraptions.farming.maturity", recipe.crop().optimal(), recipe.crop().stages), 210, 22).setPosition(0, 80);
            builder.addText(Component.translatable("jei.innovativecontraptions.farming.environment." + recipe.crop().id()), 210, 45).setPosition(0, 105);
        }
    }
}


