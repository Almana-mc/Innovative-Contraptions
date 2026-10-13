package me.almana.innovativecontraptions.datagen;

import me.almana.innovativecontraptions.content.gathering.GatheringContent;
import me.almana.innovativecontraptions.registry.ICItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

public final class GatheringRecipeProvider extends RecipeProvider {
    public GatheringRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) { super(registries, output); }
    @Override public void buildRecipes() { gathering(); probes(); terrain(); }
    private void gathering() {
        shaped(RecipeCategory.MISC, GatheringContent.PIPE_ITEM.get(), 16).define('P', ICItems.IRON_PLATE.get()).define('T', ICItems.TREE_TAP.get())
                .pattern("P P").pattern("P P").pattern("PTP").unlockedBy("has_plate", has(ICItems.IRON_PLATE.get())).save(output);
        shaped(RecipeCategory.MISC, ICItems.MINER.get()).define('C', ICItems.ELECTRONIC_CIRCUIT.get()).define('M', ICItems.MACHINE_CASING.get())
                .define('H', Items.CHEST).define('P', GatheringContent.PIPE_ITEM.get()).pattern(" H ").pattern("CMC").pattern(" P ")
                .unlockedBy("has_pipe", has(GatheringContent.PIPE_ITEM.get())).save(output);
        shaped(RecipeCategory.MISC, ICItems.ADVANCED_MINER.get()).define('A', ICItems.ALLOY_INGOT.get()).define('B', ICItems.MINER.get())
                .define('M', ICItems.MFE.get()).define('C', ICItems.ADVANCED_MACHINE_CASING.get()).define('T', GatheringContent.RELOCATION_ITEM.get())
                .pattern("ABA").pattern("MCT").pattern("ABA").unlockedBy("has_miner", has(ICItems.MINER.get())).save(output);
        shaped(RecipeCategory.MISC, ICItems.PUMP.get()).define('F', ICItems.EMPTY_CELL.get()).define('C', ICItems.ELECTRONIC_CIRCUIT.get())
                .define('M', ICItems.MACHINE_CASING.get()).define('T', ICItems.TREE_TAP.get()).define('P', GatheringContent.PIPE_ITEM.get())
                .pattern("FCF").pattern("FMF").pattern("PTP").unlockedBy("has_pipe", has(GatheringContent.PIPE_ITEM.get())).save(output);
        shapeless(RecipeCategory.MISC, GatheringContent.TUNING_CHIP.get()).requires(ICItems.ELECTRONIC_CIRCUIT.get()).requires(ICItems.INSULATED_COPPER_CABLE.get())
                .unlockedBy("has_circuit", has(ICItems.ELECTRONIC_CIRCUIT.get())).save(output);
        shaped(RecipeCategory.MISC, GatheringContent.RELOCATION_ITEM.get()).define('C', ICItems.ADVANCED_CIRCUIT.get()).define('F', ICItems.GLASS_FIBRE_CABLE.get())
                .define('T', GatheringContent.TUNING_CHIP.get()).define('M', ICItems.ADVANCED_MACHINE_CASING.get()).define('D', Ingredient.of(Items.DIAMOND, ICItems.INDUSTRIAL_DIAMOND.get()))
                .pattern("CTC").pattern("FMF").pattern("CDC").unlockedBy("has_tuning", has(GatheringContent.TUNING_CHIP.get())).save(output);
    }
    private void probes() {
        shaped(RecipeCategory.TOOLS, ICItems.OD_SCANNER.get()).define('P', ICItems.GOLD_CASING.get()).define('G', Items.GLOWSTONE_DUST)
                .define('C', ICItems.ELECTRONIC_CIRCUIT.get()).define('B', ICItems.ADVANCED_RE_BATTERY.get()).define('W', ICItems.INSULATED_COPPER_CABLE.get())
                .pattern("PGP").pattern("CBC").pattern("WWW").unlockedBy("has_battery", has(ICItems.ADVANCED_RE_BATTERY.get())).save(output);
        shaped(RecipeCategory.TOOLS, ICItems.OV_SCANNER.get()).define('P', ICItems.GOLD_CASING.get()).define('D', ICItems.ENERGY_CRYSTAL.get())
                .define('G', Items.GLOWSTONE_DUST).define('C', ICItems.ADVANCED_CIRCUIT.get()).define('W', ICItems.DOUBLE_INSULATED_GOLD_CABLE.get()).define('S', ICItems.OD_SCANNER.get())
                .pattern("PDP").pattern("GCG").pattern("WSW").unlockedBy("has_scanner", has(ICItems.OD_SCANNER.get())).save(output);
    }
    private void terrain() {
        shaped(RecipeCategory.MISC, GatheringContent.BLANK.get()).define('C', ICItems.ELECTRONIC_CIRCUIT.get()).define('A', ICItems.ADVANCED_CIRCUIT.get()).define('R', Items.REDSTONE)
                .pattern(" C ").pattern(" A ").pattern("R R").unlockedBy("has_circuit", has(ICItems.ADVANCED_CIRCUIT.get())).save(output);
        shaped(RecipeCategory.MISC, GatheringContent.TERRAIN_ITEM.get()).define('G', Items.GLOWSTONE_DUST).define('T', GatheringContent.BLANK.get())
                .define('D', Ingredient.of(Items.DIRT, Items.COARSE_DIRT, Items.PODZOL)).define('M', ICItems.ADVANCED_MACHINE_CASING.get()).pattern("GTG").pattern("DMD").pattern("GDG")
                .unlockedBy("has_program", has(GatheringContent.BLANK.get())).save(output);
        program(GatheringContent.FROST.get(), Ingredient.of(Items.SNOWBALL));
        program(GatheringContent.PLANTING.get(), Ingredient.of(Items.WHEAT_SEEDS));
        program(GatheringContent.DRYING.get(), Ingredient.of(Items.SAND, Items.RED_SAND));
        program(GatheringContent.LEVELING.get(), Ingredient.of(Items.DIRT, Items.COARSE_DIRT, Items.PODZOL));
        program(GatheringContent.WATERING.get(), Ingredient.of(Items.WATER_BUCKET));
        shaped(RecipeCategory.MISC, GatheringContent.FUNGI.get()).define('P', GatheringContent.BLANK.get()).define('M', Items.MYCELIUM).define('F', Items.BROWN_MUSHROOM)
                .pattern("MFM").pattern("FPF").pattern("MFM").unlockedBy("has_program", has(GatheringContent.BLANK.get())).save(output);
    }
    private void program(Item result, Ingredient ingredient) {
        shaped(RecipeCategory.MISC, result).define('P', GatheringContent.BLANK.get()).define('I', ingredient).pattern(" I ").pattern("IPI").pattern(" I ")
                .unlockedBy("has_program", has(GatheringContent.BLANK.get())).save(output);
    }
}
