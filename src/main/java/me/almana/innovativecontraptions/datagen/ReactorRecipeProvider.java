package me.almana.innovativecontraptions.datagen;

import me.almana.innovativecontraptions.InnovativeContraptions;
import me.almana.innovativecontraptions.registry.ICDataComponents;
import me.almana.innovativecontraptions.registry.ICItems;
import me.almana.innovativecontraptions.registry.ICTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
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
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;

final class ReactorRecipeProvider extends RecipeProvider {
    private static final String ID = InnovativeContraptions.MODID + ":";

    ReactorRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        vents();
        cells();
        platingsAndReflectors();
        fuel();
        structure();
    }

    private void vents() {
        shaped(RecipeCategory.MISC, ICItems.HEAT_VENT.get())
                .define('#', Items.IRON_BARS).define('I', ICTags.PLATES_IRON).define('M', ICItems.ELECTRIC_MOTOR.get())
                .pattern("#I#").pattern("IMI").pattern("#I#")
                .unlockedBy("has_motor", has(ICItems.ELECTRIC_MOTOR.get())).save(output);
        shaped(RecipeCategory.MISC, ICItems.ADVANCED_HEAT_VENT.get())
                .define('#', Items.IRON_BARS).define('V', ICItems.HEAT_VENT.get())
                .define('D', CompoundIngredient.of(tag(Tags.Items.GEMS_DIAMOND), Ingredient.of(ICItems.INDUSTRIAL_DIAMOND.get())))
                .pattern("#V#").pattern("#D#").pattern("#V#")
                .unlockedBy("has_vent", has(ICItems.HEAT_VENT.get())).save(output);
        surround(ICItems.REACTOR_HEAT_VENT.get(), tag(ICTags.PLATES_COPPER), ICItems.HEAT_VENT.get());
        plus(ICItems.OVERCLOCKED_HEAT_VENT.get(), ICTags.PLATES_GOLD, ICItems.REACTOR_HEAT_VENT.get());
        shaped(RecipeCategory.MISC, ICItems.COMPONENT_HEAT_VENT.get())
                .define('#', Items.IRON_BARS).define('T', ICTags.PLATES_TIN).define('V', ICItems.HEAT_VENT.get())
                .pattern("#T#").pattern("TVT").pattern("#T#")
                .unlockedBy("has_vent", has(ICItems.HEAT_VENT.get())).save(output);
    }

    private void cells() {
        shaped(RecipeCategory.MISC, ICItems.TRI_HEAT_STORAGE.get())
                .define('T', ICTags.PLATES_TIN).define('C', ICItems.HEAT_STORAGE.get())
                .pattern("TTT").pattern("CCC").pattern("TTT")
                .unlockedBy("has_cell", has(ICItems.HEAT_STORAGE.get())).save(output);
        shaped(RecipeCategory.MISC, ICItems.HEX_HEAT_STORAGE.get())
                .define('T', ICTags.PLATES_TIN).define('C', ICItems.TRI_HEAT_STORAGE.get()).define('I', ICTags.PLATES_IRON)
                .pattern("TCT").pattern("TIT").pattern("TCT")
                .unlockedBy("has_cell", has(ICItems.TRI_HEAT_STORAGE.get())).save(output);
        shaped(RecipeCategory.MISC, ICItems.RSH_CONDENSATOR.get())
                .define('R', Tags.Items.DUSTS_REDSTONE).define('V', ICItems.HEAT_VENT.get()).define('S', ICItems.HEAT_EXCHANGER.get())
                .pattern("RRR").pattern("RVR").pattern("RSR")
                .unlockedBy("has_vent", has(ICItems.HEAT_VENT.get())).save(output);
        shaped(RecipeCategory.MISC, ICItems.LZH_CONDENSATOR.get())
                .define('R', Tags.Items.DUSTS_REDSTONE).define('V', ICItems.REACTOR_HEAT_VENT.get()).define('S', ICItems.REACTOR_HEAT_EXCHANGER.get())
                .define('C', ICItems.RSH_CONDENSATOR.get()).define('L', Tags.Items.STORAGE_BLOCKS_LAPIS)
                .pattern("RVR").pattern("CLC").pattern("RSR")
                .unlockedBy("has_rsh", has(ICItems.RSH_CONDENSATOR.get())).save(output);
        for (int count = 1; count <= 8; count++) {
            refill(ICItems.RSH_CONDENSATOR.get(), Tags.Items.DUSTS_REDSTONE, count, "rsh_condensator_redstone");
            refill(ICItems.LZH_CONDENSATOR.get(), Tags.Items.DUSTS_REDSTONE, count, "lzh_condensator_redstone");
            refill(ICItems.LZH_CONDENSATOR.get(), Tags.Items.GEMS_LAPIS, count, "lzh_condensator_lapis");
        }
    }

    private void refill(Item condensator, TagKey<Item> filler, int count, String name) {
        shapeless(RecipeCategory.MISC, condensator).requires(condensator).requires(tag(filler), count)
                .unlockedBy("has_condensator", has(condensator)).save(output, ID + name + "_" + count);
    }

    private void platingsAndReflectors() {
        shapeless(RecipeCategory.MISC, ICItems.PLATING.get()).requires(ICTags.PLATES_LEAD).requires(ICItems.ALLOY_INGOT.get())
                .unlockedBy("has_alloy", has(ICItems.ALLOY_INGOT.get())).save(output);
        shapeless(RecipeCategory.MISC, ICItems.CONTAINMENT_PLATING.get()).requires(ICItems.PLATING.get()).requires(ICItems.ALLOY_INGOT.get(), 2)
                .unlockedBy("has_plating", has(ICItems.PLATING.get())).save(output);
        surround(ICItems.HEAT_PLATING.get(), tag(ICTags.PLATES_COPPER), ICItems.PLATING.get());
        shaped(RecipeCategory.MISC, ICItems.NEUTRON_REFLECTOR.get())
                .define('T', ICTags.DUSTS_TIN).define('c', ICTags.DUSTS_COAL).define('C', ICTags.PLATES_COPPER)
                .pattern("TcT").pattern("cCc").pattern("TcT")
                .unlockedBy("has_copper_plate", has(ICTags.PLATES_COPPER)).save(output);
        shaped(RecipeCategory.MISC, ICItems.THICK_NEUTRON_REFLECTOR.get())
                .define('C', ICTags.PLATES_COPPER).define('R', ICItems.NEUTRON_REFLECTOR.get())
                .pattern("CRC").pattern("RCR").pattern("CRC")
                .unlockedBy("has_reflector", has(ICItems.NEUTRON_REFLECTOR.get())).save(output);
        shaped(RecipeCategory.MISC, ICItems.IRIDIUM_REFLECTOR.get())
                .define('R', ICItems.THICK_NEUTRON_REFLECTOR.get()).define('C', ICTags.DENSE_PLATES_COPPER).define('I', ICItems.IRIDIUM_PLATE.get())
                .pattern("RRR").pattern("CIC").pattern("RRR")
                .unlockedBy("has_reflector", has(ICItems.THICK_NEUTRON_REFLECTOR.get())).save(output);
        shaped(RecipeCategory.MISC, ICItems.IRIDIUM_REFLECTOR.get())
                .define('R', ICItems.THICK_NEUTRON_REFLECTOR.get()).define('C', ICTags.DENSE_PLATES_COPPER).define('I', ICItems.IRIDIUM_PLATE.get())
                .pattern("RCR").pattern("RIR").pattern("RCR")
                .unlockedBy("has_reflector", has(ICItems.THICK_NEUTRON_REFLECTOR.get())).save(output, ID + "iridium_reflector_vertical");
    }

    private void fuel() {
        sandwich(ICItems.URANIUM.get(), ICItems.SMALL_URANIUM_235.get());
        sandwich(ICItems.MOX.get(), ICItems.PLUTONIUM.get());
        rods(ICItems.FUEL_ROD_URANIUM.get(), ICItems.DUAL_FUEL_ROD_URANIUM.get(), ICItems.QUAD_FUEL_ROD_URANIUM.get(), "quad_fuel_rod_uranium");
        rods(ICItems.FUEL_ROD_MOX.get(), ICItems.DUAL_FUEL_ROD_MOX.get(), ICItems.QUAD_FUEL_ROD_MOX.get(), "quad_fuel_rod_mox");
        shaped(RecipeCategory.BUILDING_BLOCKS, ICItems.URANIUM_BLOCK.get()).define('U', ICItems.URANIUM_238.get())
                .pattern("UUU").pattern("UUU").pattern("UUU")
                .unlockedBy("has_uranium", has(ICItems.URANIUM_238.get())).save(output);
        shapeless(RecipeCategory.MISC, ICItems.URANIUM_238.get(), 9).requires(ICTags.STORAGE_BLOCKS_URANIUM)
                .unlockedBy("has_block", has(ICTags.STORAGE_BLOCKS_URANIUM)).save(output, ID + "uranium_238_from_block");
    }

    private void sandwich(Item result, Item middle) {
        shaped(RecipeCategory.MISC, result).define('U', ICItems.URANIUM_238.get()).define('C', middle)
                .pattern("UUU").pattern("CCC").pattern("UUU")
                .unlockedBy("has_uranium", has(ICItems.URANIUM_238.get())).save(output);
    }

    private void rods(Item single, Item dual, Item quad, String quadName) {
        Ingredient fresh = fresh(single);
        shaped(RecipeCategory.MISC, dual).define('U', fresh).define('I', ICTags.PLATES_IRON)
                .pattern("UIU").unlockedBy("has_rod", has(single)).save(output);
        shaped(RecipeCategory.MISC, quad).define('U', fresh).define('I', ICTags.PLATES_IRON).define('C', ICTags.PLATES_COPPER)
                .pattern("UIU").pattern("CIC").pattern("UIU").unlockedBy("has_rod", has(single)).save(output);
        shaped(RecipeCategory.MISC, quad).define('U', fresh(dual))
                .define('I', ICTags.PLATES_IRON).define('C', ICTags.PLATES_COPPER)
                .pattern(" U ").pattern("CIC").pattern(" U ")
                .unlockedBy("has_dual", has(dual)).save(output, ID + quadName + "_from_dual");
    }

    private static Ingredient fresh(Item rod) {
        //? if >=26.1 {
        return DataComponentIngredient.of(DataComponentPatch.builder().remove(ICDataComponents.REACTOR_DAMAGE.get()).build(), rod);
        //?} else {
        /*return DataComponentIngredient.of(true, new net.minecraft.world.item.ItemStack(rod));
        *///?}
    }

    private void structure() {
        shaped(RecipeCategory.MISC, ICItems.NUCLEAR_REACTOR.get())
                .define('P', ICTags.DENSE_PLATES_LEAD).define('c', ICTags.CIRCUITS_ADVANCED)
                .define('C', ICItems.REACTOR_CHAMBER.get()).define('G', ICItems.GENERATOR.get())
                .pattern("PcP").pattern("CCC").pattern("PGP")
                .unlockedBy("has_chamber", has(ICItems.REACTOR_CHAMBER.get())).save(output);
        plus(ICItems.REACTOR_CHAMBER.get(), ICTags.PLATES_LEAD, ICItems.MACHINE_CASING.get());
        shaped(RecipeCategory.BUILDING_BLOCKS, ICItems.REACTOR_VESSEL.get(), 4)
                .define('P', ICTags.PLATES_LEAD).define('S', CompoundIngredient.of(tag(Tags.Items.STONES),
                        Ingredient.of(Items.POLISHED_GRANITE, Items.POLISHED_DIORITE, Items.POLISHED_ANDESITE)))
                .pattern("PSP").pattern("SPS").pattern("PSP")
                .unlockedBy("has_lead_plate", has(ICTags.PLATES_LEAD)).save(output);
        shaped(RecipeCategory.BUILDING_BLOCKS, ICItems.REACTOR_ACCESS_HATCH.get())
                .define('V', ICItems.REACTOR_VESSEL.get()).define('H', ItemTags.WOODEN_TRAPDOORS)
                .pattern("VVV").pattern("VHV").pattern("VVV")
                .unlockedBy("has_vessel", has(ICItems.REACTOR_VESSEL.get())).save(output);
        surround(ICItems.REACTOR_FLUID_PORT.get(), Ingredient.of(ICItems.REACTOR_VESSEL.get()), ICItems.EMPTY_CELL.get());
        shaped(RecipeCategory.MISC, ICItems.REACTOR_REDSTONE_PORT.get())
                .define('E', ICItems.REACTOR_VESSEL.get()).define('C', Tags.Items.DUSTS_REDSTONE)
                .pattern("EEE").pattern("ECE").pattern("EEE")
                .unlockedBy("has_center", has(Tags.Items.DUSTS_REDSTONE)).save(output);
        injector(ICItems.RSH_COOLANT_INJECTOR.get(), ICItems.RSH_CONDENSATOR.get());
        injector(ICItems.LZH_COOLANT_INJECTOR.get(), ICItems.LZH_CONDENSATOR.get());
        shaped(RecipeCategory.MISC, ICItems.COMPACT_ITEM_BUFFER.get())
                .define('I', ICTags.CASINGS_IRON).define('C', Tags.Items.CHESTS_WOODEN).define('M', ICItems.MACHINE_CASING.get())
                .pattern("ICI").pattern("IMI").pattern("ICI")
                .unlockedBy("has_casing", has(ICItems.MACHINE_CASING.get())).save(output);
        shaped(RecipeCategory.MISC, ICItems.LIQUID_HEAT_EXCHANGER.get())
                .define('g', Tags.Items.GLASS_BLOCKS_COLORLESS).define('C', ICItems.EMPTY_CELL.get()).define('I', ICTags.CASINGS_IRON)
                .define('G', ICItems.HEAT_CONDUCTOR.get())
                .pattern("gCg").pattern("gCg").pattern("IGI")
                .unlockedBy("has_conductor", has(ICItems.HEAT_CONDUCTOR.get())).save(output);
    }

    private void injector(ItemLike result, ItemLike condensator) {
        shaped(RecipeCategory.MISC, result).define('E', ICItems.EJECTOR_UPGRADE.get()).define('C', condensator)
                .define('M', ICItems.ADVANCED_MACHINE_CASING.get()).define('B', ICItems.COMPACT_ITEM_BUFFER.get())
                .pattern("ECE").pattern("CMC").pattern("EBE")
                .unlockedBy("has_condensator", has(condensator)).save(output);
    }

    private void surround(ItemLike result, Ingredient edge, ItemLike center) {
        shaped(RecipeCategory.MISC, result).define('E', edge).define('C', center)
                .pattern("EEE").pattern("ECE").pattern("EEE")
                .unlockedBy("has_center", has(center)).save(output);
    }

    private void plus(ItemLike result, TagKey<Item> edge, ItemLike center) {
        shaped(RecipeCategory.MISC, result).define('E', edge).define('C', center)
                .pattern(" E ").pattern("ECE").pattern(" E ")
                .unlockedBy("has_center", has(center)).save(output);
    }
}
