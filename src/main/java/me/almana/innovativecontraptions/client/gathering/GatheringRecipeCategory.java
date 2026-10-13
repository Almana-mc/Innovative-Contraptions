package me.almana.innovativecontraptions.client.gathering;

import me.almana.innovativecontraptions.content.gathering.GatheringContent;
import me.almana.innovativecontraptions.content.gathering.MiningWork;
import me.almana.innovativecontraptions.content.gathering.OreProbeItem;
import me.almana.innovativecontraptions.content.gathering.PumpWork;
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

public final class GatheringRecipeCategory extends AbstractRecipeCategory<GatheringJeiRecipe> {
    public GatheringRecipeCategory(IGuiHelper gui) {
        super(GatheringJeiPlugin.TYPE, Component.translatable("jei.innovativecontraptions.gathering.title"), gui.createDrawableItemLike(ICItems.MINER.get()), 210, 153);
    }
    @Override public void setRecipe(IRecipeLayoutBuilder builder, GatheringJeiRecipe recipe, IFocusGroup focuses) {
        var station = switch (recipe.kind()) { case 0 -> ICItems.MINER.get(); case 1 -> ICItems.ADVANCED_MINER.get(); case 2 -> ICItems.PUMP.get(); default -> GatheringContent.TERRAIN_ITEM.get(); };
        builder.addSlot(RecipeIngredientRole.CRAFTING_STATION, 181, 8).add(new ItemStack(station));
        if (recipe.kind() == 0) {
            builder.addInputSlot(8, 8).setStandardSlotBackground().add(new ItemStack(recipe.tool()));
            builder.addInputSlot(32, 8).setStandardSlotBackground().add(new ItemStack(GatheringContent.PIPE_ITEM.get()));
            builder.addInputSlot(56, 8).setStandardSlotBackground().add(new ItemStack(recipe.scanner()));
        } else builder.addInputSlot(8, 8).setStandardSlotBackground().add(new ItemStack(recipe.kind() == 1 ? recipe.scanner() : recipe.tool()));
        if (recipe.kind() == 2) builder.addOutputSlot(95, 8).setStandardSlotBackground().add(new ItemStack(recipe.tool() == Items.BUCKET ? Items.WATER_BUCKET : ICItems.WATER_CELL.get()))
                .add(new ItemStack(recipe.tool() == Items.BUCKET ? Items.LAVA_BUCKET : ICItems.LAVA_CELL.get()));
    }
    @Override public void createRecipeExtras(IRecipeExtrasBuilder builder, GatheringJeiRecipe recipe, IFocusGroup focuses) {
        Component text = switch (recipe.kind()) {
            case 0 -> Component.translatable("jei.innovativecontraptions.gathering.bore", MiningWork.drillDuration(new ItemStack(recipe.tool())), MiningWork.drillPower(new ItemStack(recipe.tool())), ((OreProbeItem) recipe.scanner()).minerRadius());
            case 1 -> Component.translatable("jei.innovativecontraptions.gathering.remote", MiningWork.REMOTE_INTERVAL, MiningWork.REMOTE_SCAN_COST, MiningWork.REMOTE_MINE_COST, ((OreProbeItem) recipe.scanner()).minerRadius());
            case 2 -> Component.translatable("jei.innovativecontraptions.gathering.pump", PumpWork.BUCKET, PumpWork.BASE_DURATION, PumpWork.BASE_POWER);
            default -> Component.translatable("jei.innovativecontraptions.gathering.terrain", recipe.program().program.cost, recipe.program().program.range);
        };
        builder.addText(text, 210, 76).setPosition(0, 36);
        builder.addText(Component.translatable("jei.innovativecontraptions.gathering.environment"), 210, 36).setPosition(0, 114);
    }
}
