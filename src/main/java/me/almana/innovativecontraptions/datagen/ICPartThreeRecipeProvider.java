package me.almana.innovativecontraptions.datagen;

import me.almana.innovativecontraptions.content.gathering.GatheringContent;
import me.almana.innovativecontraptions.registry.ICBlocks;
import me.almana.innovativecontraptions.registry.ICItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

final class ICPartThreeRecipeProvider extends RecipeProvider {
    ICPartThreeRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        machines();
        blades();
        upgrades();
    }

    private void machines() {
        shaped(RecipeCategory.MISC, ICItems.ELECTRIC_FURNACE.get())
                .define('C', ICItems.ELECTRONIC_CIRCUIT.get()).define('R', Items.REDSTONE).define('F', ICBlocks.IRON_FURNACE.get())
                .pattern(" C ").pattern("RFR")
                .unlockedBy("has_iron_furnace", has(ICBlocks.IRON_FURNACE.get())).save(output);
        shaped(RecipeCategory.MISC, ICItems.INDUCTION_FURNACE.get())
                .define('C', Items.COPPER_INGOT).define('F', ICItems.ELECTRIC_FURNACE.get()).define('M', ICItems.ADVANCED_MACHINE_CASING.get())
                .pattern("CCC").pattern("CFC").pattern("CMC")
                .unlockedBy("has_electric_furnace", has(ICItems.ELECTRIC_FURNACE.get())).save(output);
        shaped(RecipeCategory.MISC, ICItems.BLOCK_SLICER.get())
                .define('C', ICItems.ELECTRONIC_CIRCUIT.get()).define('M', ICItems.MACHINE_CASING.get()).define('E', ICItems.ELECTRIC_MOTOR.get())
                .pattern("C").pattern("M").pattern("E")
                .unlockedBy("has_motor", has(ICItems.ELECTRIC_MOTOR.get())).save(output);
        shaped(RecipeCategory.MISC, ICItems.CANNING_MACHINE.get())
                .define('T', ICItems.TIN_CASING.get()).define('C', ICItems.ELECTRONIC_CIRCUIT.get()).define('M', ICItems.MACHINE_CASING.get())
                .pattern("TCT").pattern("TMT").pattern("TTT")
                .unlockedBy("has_tin_casing", has(ICItems.TIN_CASING.get())).save(output);
        // ponytail: not in video or reference
        shaped(RecipeCategory.MISC, ICItems.THERMAL_CENTRIFUGE.get())
                .define('C', ICItems.COIL.get()).define('L', ICItems.MINING_LASER.get()).define('A', ICItems.ALLOY_INGOT.get())
                .define('M', ICItems.ADVANCED_MACHINE_CASING.get()).define('H', ICItems.HEAT_CONDUCTOR.get())
                .pattern("CLC").pattern("AMA").pattern("AHA")
                .unlockedBy("has_advanced_casing", has(ICItems.ADVANCED_MACHINE_CASING.get())).save(output);
    }

    private void blades() {
        blade(ICItems.IRON_SAW_DISC.get(), ICItems.IRON_PLATE.get(), Ingredient.of(Items.STONE, Items.GRANITE, Items.POLISHED_GRANITE,
                Items.DIORITE, Items.POLISHED_DIORITE, Items.ANDESITE, Items.POLISHED_ANDESITE, Items.SMOOTH_STONE));
        blade(ICItems.STEEL_SAW_DISC.get(), ICItems.STEEL_PLATE.get(), Ingredient.of(Items.IRON_INGOT));
        blade(ICItems.DIAMOND_SAW_DISC.get(), Items.DIAMOND, Ingredient.of(ICItems.STEEL_INGOT.get()));
    }

    private void blade(Item result, ItemLike edge, Ingredient core) {
        shaped(RecipeCategory.TOOLS, result).define('E', edge).define('C', core)
                .pattern("EEE").pattern("ECE").pattern("EEE")
                .unlockedBy("has_edge", has(edge)).save(output);
    }

    private void overclockers(Item coolant, int count, String name) {
        shaped(RecipeCategory.MISC, ICItems.OVERCLOCKER_UPGRADE.get(), count)
                .define('C', coolant).define('W', ICItems.INSULATED_COPPER_CABLE.get()).define('E', ICItems.ELECTRONIC_CIRCUIT.get())
                .pattern("CCC").pattern("WEW")
                .unlockedBy("has_coolant", has(coolant)).save(output, "innovativecontraptions:overclocker_upgrade_from_" + name);
    }

    private void upgrades() {
        shaped(RecipeCategory.MISC, ICItems.HEAT_STORAGE.get())
                .define('T', ICItems.TIN_PLATE.get()).define('W', ICItems.COOLANT_CELL.get())
                .pattern(" T ").pattern("TWT").pattern(" T ")
                .unlockedBy("has_coolant_cell", has(ICItems.COOLANT_CELL.get())).save(output);
        shaped(RecipeCategory.MISC, ICItems.OVERCLOCKER_UPGRADE.get())
                .define('C', ICItems.HEAT_STORAGE.get()).define('W', ICItems.INSULATED_COPPER_CABLE.get())
                .define('E', ICItems.ELECTRONIC_CIRCUIT.get()).pattern("CCC").pattern("WEW")
                .unlockedBy("has_coolant", has(ICItems.HEAT_STORAGE.get())).save(output);
        overclockers(ICItems.TRI_HEAT_STORAGE.get(), 6, "tri");
        overclockers(ICItems.HEX_HEAT_STORAGE.get(), 12, "hex");
        shaped(RecipeCategory.MISC, ICItems.TRANSFORMER_UPGRADE.get())
                .define('G', Items.GLASS).define('W', ICItems.INSULATED_GOLD_CABLE.get())
                .define('T', ICItems.MV_TRANSFORMER.get()).define('E', ICItems.ELECTRONIC_CIRCUIT.get())
                .pattern("GGG").pattern("WTW").pattern("GEG")
                .unlockedBy("has_transformer", has(ICItems.MV_TRANSFORMER.get())).save(output);
        shaped(RecipeCategory.MISC, ICItems.ENERGY_STORAGE_UPGRADE.get())
                .define('P', ItemTags.PLANKS).define('W', ICItems.INSULATED_COPPER_CABLE.get())
                .define('B', ICItems.RE_BATTERY.get()).define('E', ICItems.ELECTRONIC_CIRCUIT.get())
                .pattern("PPP").pattern("WBW").pattern("PEP")
                .unlockedBy("has_battery", has(ICItems.RE_BATTERY.get())).save(output);
        shaped(RecipeCategory.MISC, ICItems.REDSTONE_INVERTER_UPGRADE.get())
                .define('T', ICItems.TIN_INGOT.get()).define('R', Items.REDSTONE_TORCH)
                .pattern("T T").pattern(" R ").pattern("T T")
                .unlockedBy("has_tin", has(ICItems.TIN_INGOT.get())).save(output);
        transfer(ICItems.EJECTOR_UPGRADE.get(), Items.PISTON, GatheringContent.PIPE_ITEM.get());
        transfer(ICItems.PULLING_UPGRADE.get(), Items.STICKY_PISTON, GatheringContent.PIPE_ITEM.get());
        transfer(ICItems.FLUID_EJECTOR_UPGRADE.get(), Items.PISTON, Items.BUCKET);
        transfer(ICItems.FLUID_PULLING_UPGRADE.get(), Items.STICKY_PISTON, Items.BUCKET);
    }

    private void transfer(Item result, ItemLike piston, ItemLike core) {
        shaped(RecipeCategory.MISC, result).define('X', Items.COPPER_INGOT).define('C', piston)
                .define('Y', ICItems.ELECTRONIC_CIRCUIT.get()).define('V', core).define('B', Items.REDSTONE_TORCH)
                .pattern("XCX").pattern("YVY").pattern("XBX")
                .unlockedBy("has_circuit", has(ICItems.ELECTRONIC_CIRCUIT.get())).save(output);
    }
}
