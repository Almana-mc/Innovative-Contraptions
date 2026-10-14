package me.almana.innovativecontraptions.datagen;

import me.almana.innovativecontraptions.InnovativeContraptions;
import me.almana.innovativecontraptions.content.processing.RecyclingRules;
import me.almana.innovativecontraptions.registry.ICItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
//? if >=26.1 {
import net.neoforged.neoforge.common.data.BlockTagCopyingItemTagProvider;
//?} else
//import net.minecraft.data.tags.ItemTagsProvider;

import java.util.concurrent.CompletableFuture;

//? if >=26.1 {
public class ICItemTagsProvider extends BlockTagCopyingItemTagProvider {
    public ICItemTagsProvider(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> lookupProvider,
            CompletableFuture<TagsProvider.TagLookup<Block>> blockTags
    ) {
        super(output, lookupProvider, blockTags, InnovativeContraptions.MODID);
    }
//?} else {
/*public class ICItemTagsProvider extends ItemTagsProvider {
    public ICItemTagsProvider(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> lookupProvider,
            CompletableFuture<TagsProvider.TagLookup<Block>> blockTags,
            net.neoforged.neoforge.common.data.ExistingFileHelper existingFileHelper
    ) {
        super(output, lookupProvider, blockTags, InnovativeContraptions.MODID, existingFileHelper);
    }
*///?}

    public static final TagKey<Item> CABLES = TagKey.create(
            Registries.ITEM,
            Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, "cables")
    );
    private static final TagKey<Item> TIN_ORES = TagKey.create(
            Registries.ITEM,
            Identifier.fromNamespaceAndPath("c", "ores/tin")
    );
    private static final TagKey<Item> LEAD_ORES = TagKey.create(
            Registries.ITEM,
            Identifier.fromNamespaceAndPath("c", "ores/lead")
    );
    private static final TagKey<Item> RAW_TIN = TagKey.create(
            Registries.ITEM,
            Identifier.fromNamespaceAndPath("c", "raw_materials/tin")
    );
    private static final TagKey<Item> RAW_LEAD = TagKey.create(
            Registries.ITEM,
            Identifier.fromNamespaceAndPath("c", "raw_materials/lead")
    );
    private static final TagKey<Item> SILVER_ORES = commonTag("ores/silver");
    private static final TagKey<Item> URANIUM_ORES = commonTag("ores/uranium");
    private static final TagKey<Item> TIN_INGOTS = commonTag("ingots/tin");
    private static final TagKey<Item> LEAD_INGOTS = commonTag("ingots/lead");
    private static final TagKey<Item> SILVER_INGOTS = commonTag("ingots/silver");
    private static final TagKey<Item> BRONZE_INGOTS = commonTag("ingots/bronze");
    private static final TagKey<Item> STEEL_INGOTS = commonTag("ingots/steel");
    private static final TagKey<Item> TIN_STORAGE_BLOCKS = commonTag("storage_blocks/tin");
    private static final TagKey<Item> BRONZE_STORAGE_BLOCKS = commonTag("storage_blocks/bronze");
    private static final TagKey<Item> SILVER_STORAGE_BLOCKS = commonTag("storage_blocks/silver");
    private static final TagKey<Item> CHARCOAL_STORAGE_BLOCKS = commonTag("storage_blocks/charcoal");

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(RecyclingRules.NO_SCRAP)
                .add(Items.STICK, Items.SNOWBALL,
                        Items.SNOW, Items.SNOW_BLOCK)
                .addOptionalTag(commonTag("glass_panes"));
        tag(RecyclingRules.NO_SCRAP).add(ICItems.WOODEN_SCAFFOLD.get());
        copy(BlockTags.LOGS, ItemTags.LOGS);
        copy(BlockTags.LOGS_THAT_BURN, ItemTags.LOGS_THAT_BURN);
        copy(BlockTags.LEAVES, ItemTags.LEAVES);
        copy(BlockTags.SAPLINGS, ItemTags.SAPLINGS);
        copy(BlockTags.DOORS, ItemTags.DOORS);
        copy(ICBlockTagsProvider.CABLES, CABLES);
        tag(TIN_ORES).add(ICItems.TIN_ORE.get());
        tag(LEAD_ORES).add(ICItems.LEAD_ORE.get());
        tag(RAW_TIN).add(ICItems.RAW_TIN.get());
        tag(RAW_LEAD).add(ICItems.RAW_LEAD.get());
        copy(ICBlockTagsProvider.SILVER_ORES, SILVER_ORES);
        copy(ICBlockTagsProvider.URANIUM_ORES, URANIUM_ORES);
        copy(ICBlockTagsProvider.TIN_STORAGE_BLOCKS, TIN_STORAGE_BLOCKS);
        copy(ICBlockTagsProvider.BRONZE_STORAGE_BLOCKS, BRONZE_STORAGE_BLOCKS);
        copy(ICBlockTagsProvider.SILVER_STORAGE_BLOCKS, SILVER_STORAGE_BLOCKS);
        copy(ICBlockTagsProvider.CHARCOAL_STORAGE_BLOCKS, CHARCOAL_STORAGE_BLOCKS);
        tag(TIN_INGOTS).add(ICItems.TIN_INGOT.get());
        tag(LEAD_INGOTS).add(ICItems.LEAD_INGOT.get());
        tag(SILVER_INGOTS).add(ICItems.SILVER_INGOT.get());
        tag(BRONZE_INGOTS).add(ICItems.BRONZE_INGOT.get());
        tag(STEEL_INGOTS).add(ICItems.STEEL_INGOT.get());
        tag(ItemTags.SWORDS).add(ICItems.BRONZE_SWORD.get());
        tag(ItemTags.PICKAXES).add(ICItems.BRONZE_PICKAXE.get());
        tag(ItemTags.SHOVELS).add(ICItems.BRONZE_SHOVEL.get());
        tag(ItemTags.AXES).add(ICItems.BRONZE_AXE.get());
        tag(ItemTags.HOES).add(ICItems.BRONZE_HOE.get());
        tag(ItemTags.HEAD_ARMOR).add(ICItems.BRONZE_HELMET.get());
        tag(ItemTags.CHEST_ARMOR).add(ICItems.BRONZE_CHESTPLATE.get(), ICItems.ALLOY_CHESTPLATE.get());
        tag(ItemTags.LEG_ARMOR).add(ICItems.BRONZE_LEGGINGS.get());
        tag(ItemTags.FOOT_ARMOR).add(ICItems.BRONZE_BOOTS.get());
    }

    private static TagKey<Item> commonTag(String path) {
        return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", path));
    }
}
