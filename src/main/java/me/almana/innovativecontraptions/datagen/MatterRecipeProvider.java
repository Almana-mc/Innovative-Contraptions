package me.almana.innovativecontraptions.datagen;

import me.almana.innovativecontraptions.InnovativeContraptions;
import me.almana.innovativecontraptions.content.matter.MatterRecipe;
import me.almana.innovativecontraptions.content.matter.MatterContent;
import me.almana.innovativecontraptions.content.gathering.GatheringContent;
import me.almana.innovativecontraptions.registry.ICBlocks;
import me.almana.innovativecontraptions.registry.ICItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.*;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.*;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.Recipe.CommonInfo;
import net.minecraft.world.level.ItemLike;

public final class MatterRecipeProvider extends RecipeProvider {
    public MatterRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) { super(registries, output); }
    @Override public void buildRecipes() {
        shaped(RecipeCategory.BUILDING_BLOCKS, MatterContent.REINFORCED_STONE.get(), 4).define('S', Items.STONE).define('I', ICItems.IRON_PLATE.get())
                .pattern("SIS").pattern("ISI").pattern("SIS").unlockedBy("has_plate", has(ICItems.IRON_PLATE.get())).save(output);
        shaped(RecipeCategory.MISC, ICBlocks.PATTERN_STORAGE.get()).define('S', MatterContent.REINFORCED_STONE.get()).define('C', ICItems.CRYSTAL_MEMORY.get())
                .define('M', ICItems.MINING_LASER.get()).define('A', ICItems.ADVANCED_CIRCUIT.get()).define('F', ICItems.ADVANCED_MACHINE_CASING.get())
                .pattern("SSS").pattern("CFC").pattern("MAM").unlockedBy("has_memory", has(ICItems.CRYSTAL_MEMORY.get())).save(output);
        shaped(RecipeCategory.MISC, ICBlocks.REPLICATOR.get()).define('S', MatterContent.REINFORCED_STONE.get()).define('G', ICItems.REINFORCED_GLASS.get())
                .define('T', GatheringContent.RELOCATION_CORE.get()).define('H', ICItems.HV_TRANSFORMER.get()).define('F', ICItems.MFE.get())
                .pattern("SGS").pattern("TTT").pattern("HFH").unlockedBy("has_core", has(GatheringContent.RELOCATION_CORE.get())).save(output);
        shaped(RecipeCategory.MISC, ICBlocks.MATTER_FABRICATOR.get()).define('A', ICItems.ADVANCED_MACHINE_CASING.get())
                .define('G', Items.GLOWSTONE_DUST).define('C', ICItems.ADVANCED_CIRCUIT.get()).define('L', ICItems.LAPOTRON_CRYSTAL.get())
                .pattern("GCG").pattern("ALA").pattern("GCG").unlockedBy("has_crystal", has(ICItems.LAPOTRON_CRYSTAL.get())).save(output);
        shaped(RecipeCategory.MISC, ICBlocks.PATTERN_SCANNER.get()).define('I', ICItems.IRON_PLATE.get()).define('C', ICItems.ADVANCED_CIRCUIT.get())
                .define('M', ICItems.ELECTRIC_MOTOR.get()).define('A', ICItems.ADVANCED_MACHINE_CASING.get()).define('L', ICItems.LUMINATOR.get())
                .define('G', ICItems.REINFORCED_GLASS.get()).pattern("IGI").pattern("MLM").pattern("CAC")
                .unlockedBy("has_casing", has(ICItems.ADVANCED_MACHINE_CASING.get())).save(output);
        shaped(RecipeCategory.MISC, ICItems.RAW_CRYSTAL_MEMORY.get()).define('O', ICItems.OBSIDIAN_DUST.get()).define('S', ICItems.SILICON_DIOXIDE_DUST.get())
                .pattern("SOS").pattern("OSO").pattern("SOS").unlockedBy("has_silicon", has(ICItems.SILICON_DIOXIDE_DUST.get())).save(output);
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(ICItems.RAW_CRYSTAL_MEMORY.get()), RecipeCategory.MISC, CookingBookCategory.MISC, ICItems.CRYSTAL_MEMORY.get(), 0.0F, 200)
                .unlockedBy("has_memory", has(ICItems.RAW_CRYSTAL_MEMORY.get())).save(output);
        seed(Items.DIAMOND, 24100);
        seed(Items.COBBLESTONE, 10);
        seed(Items.DIRT, 149);
        seed(Items.SAND, 1070);
        seed(Items.GRAVEL, 445);
        seed(Items.COAL, 607);
        seed(Items.REDSTONE, 798);
        seed(Items.IRON_ORE, 1073);
        seed(Items.COPPER_ORE, 1111);
        seed(ICItems.TIN_ORE.get(), 1361);
        seed(Items.GOLD_ORE, 10151);
        seed(Items.CLAY_BALL, 17345);
        seed(Items.OAK_LOG, 35055);
        seed(Items.SPRUCE_LOG, 29389);
        seed(Items.BIRCH_LOG, 56050);
        seed(Items.LAPIS_LAZULI, 4166);
        seed(Items.EMERALD, 267542);
    }
    private void seed(ItemLike item, int cost) {
        var recipe = new MatterRecipe(new CommonInfo(true), Ingredient.of(item), new ItemStackTemplate(item.asItem(), 1), cost);
        var id = Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID,
                "matter_cost/" + BuiltInRegistries.ITEM.getKey(item.asItem()).getPath());
        //? if >=26.1 {
        output.accept(ResourceKey.create(Registries.RECIPE, id), recipe, null);
        //?} else
        //output.accept(id, recipe, null);
    }
}
