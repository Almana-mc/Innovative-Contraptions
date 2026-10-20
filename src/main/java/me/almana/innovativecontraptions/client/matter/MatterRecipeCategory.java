package me.almana.innovativecontraptions.client.matter;

import me.almana.innovativecontraptions.content.matter.*;
import me.almana.innovativecontraptions.registry.*;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public final class MatterRecipeCategory extends AbstractRecipeCategory<MatterScanDisplay> {
    public MatterRecipeCategory(IGuiHelper gui) { super(MatterJeiPlugin.SCANNING, Component.translatable("matter.innovativecontraptions.costs"), gui.createDrawableItemLike(ICBlocks.PATTERN_SCANNER.get()), 178, 150); }
    @Override public void setRecipe(IRecipeLayoutBuilder builder, MatterScanDisplay recipe, IFocusGroup focus) {
        builder.addInputSlot(8, 8).setStandardSlotBackground().add(new ItemStack(recipe.item()));
        if (recipe.archive()) builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 32, 8).add(new ItemStack(ICItems.PATTERN_STORAGE.get()));
        else builder.addInputSlot(32, 8).setStandardSlotBackground().add(new ItemStack(ICItems.CRYSTAL_MEMORY.get()));
        ItemStack memory = new ItemStack(ICItems.CRYSTAL_MEMORY.get()); MatterMemoryItem.record(memory, new ItemStack(recipe.item()));
        builder.addSlot(recipe.archive() ? RecipeIngredientRole.RENDER_ONLY : RecipeIngredientRole.OUTPUT, 145, 8).setOutputSlotBackground().add(memory);
    }
    @Override public void createRecipeExtras(IRecipeExtrasBuilder builder, MatterScanDisplay recipe, IFocusGroup focus) {
        int ticks = Math.ceilDiv(recipe.cost(), 100);
        builder.addText(Component.literal("Scan: " + MatterBlockEntity.SCAN_TICKS + " ticks, " + MatterBlockEntity.SCAN_EU + " EU/t = " + MatterBlockEntity.SCAN_TICKS * MatterBlockEntity.SCAN_EU + " EU. Consumes target."), 178, 34).setPosition(0, 34);
        builder.addText(Component.literal("Replica: " + recipe.cost() / 1000.0 + " UU, " + ticks + " ticks, " + MatterBlockEntity.REPLICA_EU + " EU/t = " + (long) ticks * MatterBlockEntity.REPLICA_EU + " EU."), 178, 32).setPosition(0, 72);
        builder.addText(Component.literal(recipe.archive() ? "Tier 4. Save into adjacent archive; no memory item needed. Record preview only." : "Tier 4. Save writes the memory. An adjacent archive can replace the memory; see other variant."), 178, 40).setPosition(0, 108);
    }
    //? if >=26.1 {
    @Override public Identifier getIdentifier(MatterScanDisplay recipe) { return recipe.id(); }
    //?} else
    //@Override public Identifier getRegistryName(MatterScanDisplay recipe) { return recipe.id(); }
}

