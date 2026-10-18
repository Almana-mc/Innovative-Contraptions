package me.almana.innovativecontraptions.datagen;

import me.almana.innovativecontraptions.InnovativeContraptions;
import me.almana.innovativecontraptions.content.processing.RecyclingRules;
import me.almana.innovativecontraptions.registry.ICItems;
import me.almana.innovativecontraptions.registry.ICTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
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
        RecipeTags.preferred().forEach((item, key) -> {
            if (BuiltInRegistries.ITEM.getKey(item).getNamespace().equals(InnovativeContraptions.MODID)) tag(key).add(item);
        });
        tag(ICTags.CANNING_CONTAINERS).add(ICItems.TIN_CAN.get(), ICItems.FUEL_ROD.get());
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
