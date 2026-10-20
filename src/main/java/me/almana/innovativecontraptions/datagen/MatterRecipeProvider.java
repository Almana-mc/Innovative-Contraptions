package me.almana.innovativecontraptions.datagen;

import me.almana.innovativecontraptions.content.construction.ConstructionContent;
import me.almana.innovativecontraptions.content.matter.MatterContent;
import me.almana.innovativecontraptions.content.gathering.GatheringContent;
import me.almana.innovativecontraptions.registry.ICBlocks;
import me.almana.innovativecontraptions.registry.ICItems;
import me.almana.innovativecontraptions.registry.ICTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.*;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.neoforged.neoforge.common.Tags;

public final class MatterRecipeProvider extends RecipeProvider {
    public MatterRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) { super(registries, output); }
    @Override public void buildRecipes() {
        shaped(RecipeCategory.REDSTONE, ConstructionContent.REINFORCED_DOOR_ITEM.get()).define('I', ICTags.PLATES_IRON)
                .define('L', ICTags.PLATES_LEAD).pattern("ILI").pattern("ILI").pattern("ILI")
                .unlockedBy("has_lead_plate", has(ICTags.PLATES_LEAD)).save(output);
        shaped(RecipeCategory.MISC, ConstructionContent.CF_POWDER.get()).define('S', ICTags.DUSTS_STONE).define('A', Tags.Items.SANDS_COLORLESS)
                .define('C', Items.CLAY_BALL).pattern("SAS").pattern("SCS").pattern("SAS")
                .unlockedBy("has_stone_dust", has(ICTags.DUSTS_STONE)).save(output);
        shaped(RecipeCategory.TOOLS, ConstructionContent.FOAM_SPRAYER.get()).define('C', ICTags.CASINGS_IRON).define('F', ICItems.EMPTY_CELL.get())
                .pattern("C  ").pattern(" C ").pattern(" FC")
                .unlockedBy("has_iron_casing", has(ICTags.CASINGS_IRON)).save(output);
        shaped(RecipeCategory.TOOLS, ICItems.CF_PACK.get()).define('S', ConstructionContent.FOAM_SPRAYER.get())
                .define('C', ICTags.CIRCUITS_BASIC).define('F', ICItems.EMPTY_CELL.get()).define('T', ICTags.CASINGS_IRON)
                .pattern("SCS").pattern("FTF").pattern("F F")
                .unlockedBy("has_sprayer", has(ConstructionContent.FOAM_SPRAYER.get())).save(output);
        shaped(RecipeCategory.BUILDING_BLOCKS, ConstructionContent.IRON_SCAFFOLD.get(), 16).define('P', ICTags.PLATES_IRON)
                .define('s', Items.IRON_BARS).pattern("PPP").pattern("sss").pattern("PPP")
                .unlockedBy("has_iron_plate", has(ICTags.PLATES_IRON)).save(output);
        shaped(RecipeCategory.MISC, ICBlocks.PATTERN_STORAGE.get()).define('S', MatterContent.REINFORCED_STONE.get()).define('C', ICItems.CRYSTAL_MEMORY.get())
                .define('M', ICItems.MINING_LASER.get()).define('A', ICTags.CIRCUITS_ADVANCED).define('F', ICItems.ADVANCED_MACHINE_CASING.get())
                .pattern("SSS").pattern("CFC").pattern("MAM").unlockedBy("has_memory", has(ICItems.CRYSTAL_MEMORY.get())).save(output);
        shaped(RecipeCategory.MISC, ICBlocks.REPLICATOR.get()).define('S', MatterContent.REINFORCED_STONE.get()).define('G', ICItems.REINFORCED_GLASS.get())
                .define('T', GatheringContent.RELOCATION_CORE.get()).define('H', ICItems.HV_TRANSFORMER.get()).define('F', ICItems.MFE.get())
                .pattern("SGS").pattern("TTT").pattern("HFH").unlockedBy("has_core", has(GatheringContent.RELOCATION_CORE.get())).save(output);
        shaped(RecipeCategory.MISC, ICBlocks.MATTER_FABRICATOR.get()).define('A', ICItems.ADVANCED_MACHINE_CASING.get())
                .define('G', Tags.Items.DUSTS_GLOWSTONE).define('C', ICTags.CIRCUITS_ADVANCED).define('L', ICItems.LAPOTRON_CRYSTAL.get())
                .pattern("GCG").pattern("ALA").pattern("GCG").unlockedBy("has_crystal", has(ICItems.LAPOTRON_CRYSTAL.get())).save(output);
        shaped(RecipeCategory.MISC, ICBlocks.PATTERN_SCANNER.get()).define('I', ICTags.PLATES_IRON).define('C', ICTags.CIRCUITS_ADVANCED)
                .define('M', ICItems.ELECTRIC_MOTOR.get()).define('A', ICItems.ADVANCED_MACHINE_CASING.get()).define('L', ICItems.LUMINATOR.get())
                .define('G', ICItems.REINFORCED_GLASS.get()).pattern("IGI").pattern("MLM").pattern("CAC")
                .unlockedBy("has_casing", has(ICItems.ADVANCED_MACHINE_CASING.get())).save(output);
        shaped(RecipeCategory.MISC, ICItems.RAW_CRYSTAL_MEMORY.get()).define('O', ICTags.DUSTS_OBSIDIAN).define('S', ICTags.DUSTS_SILICON_DIOXIDE)
                .pattern("SOS").pattern("OSO").pattern("SOS").unlockedBy("has_silicon", has(ICTags.DUSTS_SILICON_DIOXIDE)).save(output);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(ICItems.RAW_CRYSTAL_MEMORY.get()), RecipeCategory.MISC, CookingBookCategory.MISC, ICItems.CRYSTAL_MEMORY.get(), 0.0F, 200)
                .unlockedBy("has_memory", has(ICItems.RAW_CRYSTAL_MEMORY.get())).save(output);
    }
}
