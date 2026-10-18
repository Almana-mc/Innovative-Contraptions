package me.almana.innovativecontraptions.datagen;

import me.almana.innovativecontraptions.content.gathering.GatheringContent;
import me.almana.innovativecontraptions.registry.ICBlocks;
import me.almana.innovativecontraptions.registry.ICItems;
import me.almana.innovativecontraptions.registry.ICTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.crafting.CompoundIngredient;

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
                .define('C', ICTags.CIRCUITS_BASIC).define('R', Tags.Items.DUSTS_REDSTONE).define('F', ICBlocks.IRON_FURNACE.get())
                .pattern(" C ").pattern("RFR")
                .unlockedBy("has_iron_furnace", has(ICBlocks.IRON_FURNACE.get())).save(output);
        shaped(RecipeCategory.MISC, ICItems.INDUCTION_FURNACE.get())
                .define('C', Tags.Items.INGOTS_COPPER).define('F', ICItems.ELECTRIC_FURNACE.get()).define('M', ICItems.ADVANCED_MACHINE_CASING.get())
                .pattern("CCC").pattern("CFC").pattern("CMC")
                .unlockedBy("has_electric_furnace", has(ICItems.ELECTRIC_FURNACE.get())).save(output);
        shaped(RecipeCategory.MISC, ICItems.BLOCK_SLICER.get())
                .define('C', ICTags.CIRCUITS_BASIC).define('M', ICItems.MACHINE_CASING.get()).define('E', ICItems.ELECTRIC_MOTOR.get())
                .pattern("C").pattern("M").pattern("E")
                .unlockedBy("has_motor", has(ICItems.ELECTRIC_MOTOR.get())).save(output);
        shaped(RecipeCategory.MISC, ICItems.CANNING_MACHINE.get())
                .define('T', ICTags.CASINGS_TIN).define('C', ICTags.CIRCUITS_BASIC).define('M', ICItems.MACHINE_CASING.get())
                .pattern("TCT").pattern("TMT").pattern("TTT")
                .unlockedBy("has_tin_casing", has(ICTags.CASINGS_TIN)).save(output);
        // ponytail: not in video or reference
        shaped(RecipeCategory.MISC, ICItems.THERMAL_CENTRIFUGE.get())
                .define('C', ICItems.COIL.get()).define('L', ICItems.MINING_LASER.get()).define('A', ICItems.ALLOY_INGOT.get())
                .define('M', ICItems.ADVANCED_MACHINE_CASING.get()).define('H', ICItems.HEAT_CONDUCTOR.get())
                .pattern("CLC").pattern("AMA").pattern("AHA")
                .unlockedBy("has_advanced_casing", has(ICItems.ADVANCED_MACHINE_CASING.get())).save(output);
    }

    private void blades() {
        blade(ICItems.IRON_SAW_DISC.get(), ICTags.PLATES_IRON, CompoundIngredient.of(tag(Tags.Items.STONES),
                Ingredient.of(Items.POLISHED_GRANITE, Items.POLISHED_DIORITE, Items.POLISHED_ANDESITE, Items.SMOOTH_STONE)));
        blade(ICItems.STEEL_SAW_DISC.get(), ICTags.PLATES_STEEL, tag(Tags.Items.INGOTS_IRON));
        blade(ICItems.DIAMOND_SAW_DISC.get(), Tags.Items.GEMS_DIAMOND, tag(ICTags.INGOTS_STEEL));
    }

    private void blade(Item result, TagKey<Item> edge, Ingredient core) {
        shaped(RecipeCategory.TOOLS, result).define('E', edge).define('C', core)
                .pattern("EEE").pattern("ECE").pattern("EEE")
                .unlockedBy("has_edge", has(edge)).save(output);
    }

    private void overclockers(Item coolant, int count, String name) {
        shaped(RecipeCategory.MISC, ICItems.OVERCLOCKER_UPGRADE.get(), count)
                .define('C', coolant).define('W', ICItems.INSULATED_COPPER_CABLE.get()).define('E', ICTags.CIRCUITS_BASIC)
                .pattern("CCC").pattern("WEW")
                .unlockedBy("has_coolant", has(coolant)).save(output, "innovativecontraptions:overclocker_upgrade_from_" + name);
    }

    private void upgrades() {
        shaped(RecipeCategory.MISC, ICItems.HEAT_STORAGE.get())
                .define('T', ICTags.PLATES_TIN).define('W', ICItems.COOLANT_CELL.get())
                .pattern(" T ").pattern("TWT").pattern(" T ")
                .unlockedBy("has_coolant_cell", has(ICItems.COOLANT_CELL.get())).save(output);
        shaped(RecipeCategory.MISC, ICItems.OVERCLOCKER_UPGRADE.get())
                .define('C', ICItems.HEAT_STORAGE.get()).define('W', ICItems.INSULATED_COPPER_CABLE.get())
                .define('E', ICTags.CIRCUITS_BASIC).pattern("CCC").pattern("WEW")
                .unlockedBy("has_coolant", has(ICItems.HEAT_STORAGE.get())).save(output);
        overclockers(ICItems.TRI_HEAT_STORAGE.get(), 6, "tri");
        overclockers(ICItems.HEX_HEAT_STORAGE.get(), 12, "hex");
        shaped(RecipeCategory.MISC, ICItems.TRANSFORMER_UPGRADE.get())
                .define('G', Tags.Items.GLASS_BLOCKS_COLORLESS).define('W', ICItems.INSULATED_GOLD_CABLE.get())
                .define('T', ICItems.MV_TRANSFORMER.get()).define('E', ICTags.CIRCUITS_BASIC)
                .pattern("GGG").pattern("WTW").pattern("GEG")
                .unlockedBy("has_transformer", has(ICItems.MV_TRANSFORMER.get())).save(output);
        shaped(RecipeCategory.MISC, ICItems.ENERGY_STORAGE_UPGRADE.get())
                .define('P', ItemTags.PLANKS).define('W', ICItems.INSULATED_COPPER_CABLE.get())
                .define('B', ICItems.RE_BATTERY.get()).define('E', ICTags.CIRCUITS_BASIC)
                .pattern("PPP").pattern("WBW").pattern("PEP")
                .unlockedBy("has_battery", has(ICItems.RE_BATTERY.get())).save(output);
        shaped(RecipeCategory.MISC, ICItems.REDSTONE_INVERTER_UPGRADE.get())
                .define('T', ICTags.INGOTS_TIN).define('R', Items.REDSTONE_TORCH)
                .pattern("T T").pattern(" R ").pattern("T T")
                .unlockedBy("has_tin", has(ICTags.INGOTS_TIN)).save(output);
        transfer(ICItems.EJECTOR_UPGRADE.get(), Items.PISTON, Ingredient.of(GatheringContent.PIPE_ITEM.get()));
        transfer(ICItems.PULLING_UPGRADE.get(), Items.STICKY_PISTON, Ingredient.of(GatheringContent.PIPE_ITEM.get()));
        transfer(ICItems.FLUID_EJECTOR_UPGRADE.get(), Items.PISTON, tag(Tags.Items.BUCKETS_EMPTY));
        transfer(ICItems.FLUID_PULLING_UPGRADE.get(), Items.STICKY_PISTON, tag(Tags.Items.BUCKETS_EMPTY));
    }

    private void transfer(Item result, ItemLike piston, Ingredient core) {
        shaped(RecipeCategory.MISC, result).define('X', Tags.Items.INGOTS_COPPER).define('C', piston)
                .define('Y', ICTags.CIRCUITS_BASIC).define('V', core).define('B', Items.REDSTONE_TORCH)
                .pattern("XCX").pattern("YVY").pattern("XBX")
                .unlockedBy("has_circuit", has(ICTags.CIRCUITS_BASIC)).save(output);
    }
}
