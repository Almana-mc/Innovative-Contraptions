package me.almana.innovativecontraptions.datagen;

import me.almana.innovativecontraptions.registry.ICBlocks;
import me.almana.innovativecontraptions.registry.ICItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

final class EnergyPartTwoRecipeProvider extends RecipeProvider {
    EnergyPartTwoRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) { super(registries, output); }
    @Override protected void buildRecipes() {
        pad(ICBlocks.BATBOX_PAD.get(), ICBlocks.BATBOX.get());
        pad(ICBlocks.CESU_PAD.get(), ICBlocks.CESU.get());
        pad(ICBlocks.MFE_PAD.get(), ICBlocks.MFE.get());
        pad(ICBlocks.MFSU_PAD.get(), ICBlocks.MFSU.get());
        shaped(RecipeCategory.MISC, ICBlocks.LV_TRANSFORMER.get())
                .define('P', ItemTags.PLANKS).define('C', ICItems.INSULATED_TIN_CABLE.get())
                .define('O', ICItems.COIL.get()).pattern("PCP").pattern("POP").pattern("PCP")
                .unlockedBy("has_coil", has(ICItems.COIL.get())).save(output);
        shaped(RecipeCategory.MISC, ICBlocks.MV_TRANSFORMER.get())
                .define('C', ICItems.INSULATED_COPPER_CABLE.get()).define('M', ICItems.MACHINE_CASING.get())
                .pattern("C").pattern("M").pattern("C")
                .unlockedBy("has_casing", has(ICItems.MACHINE_CASING.get())).save(output);
        transformer(ICBlocks.HV_TRANSFORMER.get(), ICItems.DOUBLE_INSULATED_GOLD_CABLE.get(),
                ICItems.ELECTRONIC_CIRCUIT.get(), ICItems.MV_TRANSFORMER.get(), ICItems.ADVANCED_RE_BATTERY.get());
        transformer(ICBlocks.EV_TRANSFORMER.get(), ICItems.TRIPLE_INSULATED_HV_CABLE.get(),
                ICItems.ADVANCED_CIRCUIT.get(), ICItems.HV_TRANSFORMER.get(), ICItems.LAPOTRON_CRYSTAL.get());
    }
    private void pad(ItemLike result, ItemLike storage) {
        shaped(RecipeCategory.MISC, result).define('R', ICItems.RUBBER.get()).define('S', storage).define('P', Items.STONE_PRESSURE_PLATE)
                .define('C', ICItems.ELECTRONIC_CIRCUIT.get()).pattern("CPC").pattern("RSR")
                .unlockedBy("has_storage", has(storage)).save(output);
    }
    private void transformer(ItemLike result, ItemLike cable, ItemLike circuit, ItemLike core, ItemLike battery) {
        shaped(RecipeCategory.MISC, result).define('C', cable).define('M', core).define('E', circuit).define('B', battery)
                .pattern(" C ").pattern("EMB").pattern(" C ").unlockedBy("has_core", has(core)).save(output);
    }
}
