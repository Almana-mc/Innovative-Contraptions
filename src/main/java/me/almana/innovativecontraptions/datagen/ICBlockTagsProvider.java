package me.almana.innovativecontraptions.datagen;

import me.almana.innovativecontraptions.InnovativeContraptions;
import me.almana.innovativecontraptions.content.gathering.GatheringContent;
import me.almana.innovativecontraptions.content.tool.Wrenchable;
import me.almana.innovativecontraptions.registry.ICBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

import java.util.concurrent.CompletableFuture;

public class ICBlockTagsProvider extends BlockTagsProvider {
    public static final TagKey<Block> CABLES = TagKey.create(
            Registries.BLOCK,
            Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, "cables")
    );
    public static final TagKey<Block> SILVER_ORES = commonTag("ores/silver");
    public static final TagKey<Block> URANIUM_ORES = commonTag("ores/uranium");
    public static final TagKey<Block> TIN_ORES = commonTag("ores/tin");
    public static final TagKey<Block> LEAD_ORES = commonTag("ores/lead");
    public static final TagKey<Block> TIN_STORAGE_BLOCKS = commonTag("storage_blocks/tin");
    public static final TagKey<Block> BRONZE_STORAGE_BLOCKS = commonTag("storage_blocks/bronze");
    public static final TagKey<Block> SILVER_STORAGE_BLOCKS = commonTag("storage_blocks/silver");
    public static final TagKey<Block> CHARCOAL_STORAGE_BLOCKS = commonTag("storage_blocks/charcoal");

    //? if >=26.1 {
    public ICBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, InnovativeContraptions.MODID);
    }
    //?} else {
    /*public ICBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                               net.neoforged.neoforge.common.data.ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, InnovativeContraptions.MODID, existingFileHelper);
    }
    *///?}

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(commonTag("glass_blocks")).add(ICBlocks.REINFORCED_GLASS.get());
        tag(BlockTags.LOGS).add(
                ICBlocks.RUBBER_LOG.get(),
                ICBlocks.RUBBER_WOOD.get(),
                ICBlocks.STRIPPED_RUBBER_LOG.get(),
                ICBlocks.STRIPPED_RUBBER_WOOD.get()
        );
        tag(BlockTags.LOGS_THAT_BURN).add(
                ICBlocks.RUBBER_LOG.get(),
                ICBlocks.RUBBER_WOOD.get(),
                ICBlocks.STRIPPED_RUBBER_LOG.get(),
                ICBlocks.STRIPPED_RUBBER_WOOD.get()
        );
        tag(BlockTags.LEAVES).add(ICBlocks.RUBBER_LEAVES.get());
        tag(BlockTags.SAPLINGS).add(ICBlocks.RUBBER_SAPLING.get());
        tag(BlockTags.MAINTAINS_FARMLAND).add(ICBlocks.CROP_STICKS.get());
        tag(SILVER_ORES).add(ICBlocks.SILVER_ORE.get());
        tag(URANIUM_ORES).add(ICBlocks.URANIUM_ORE.get());
        tag(TIN_ORES).add(ICBlocks.TIN_ORE.get());
        tag(LEAD_ORES).add(ICBlocks.LEAD_ORE.get());
        tag(Tags.Blocks.ORES).addTag(TIN_ORES).addTag(LEAD_ORES).addTag(SILVER_ORES).addTag(URANIUM_ORES).add(ICBlocks.IRIDIUM_ORE.get());
        tag(TIN_STORAGE_BLOCKS).add(ICBlocks.TIN_BLOCK.get());
        tag(BRONZE_STORAGE_BLOCKS).add(ICBlocks.BRONZE_BLOCK.get());
        tag(SILVER_STORAGE_BLOCKS).add(ICBlocks.SILVER_BLOCK.get());
        tag(CHARCOAL_STORAGE_BLOCKS).add(ICBlocks.CHARCOAL_BLOCK.get());

        Block[] cableBlocks = {
                ICBlocks.TIN_CABLE.get(), ICBlocks.INSULATED_TIN_CABLE.get(),
                ICBlocks.COPPER_CABLE.get(), ICBlocks.INSULATED_COPPER_CABLE.get(),
                ICBlocks.GOLD_CABLE.get(), ICBlocks.INSULATED_GOLD_CABLE.get(),
                ICBlocks.DOUBLE_INSULATED_GOLD_CABLE.get(), ICBlocks.HV_CABLE.get(),
                ICBlocks.INSULATED_HV_CABLE.get(), ICBlocks.DOUBLE_INSULATED_HV_CABLE.get(),
                ICBlocks.TRIPLE_INSULATED_HV_CABLE.get(), ICBlocks.GLASS_FIBRE_CABLE.get()
        };
        tag(CABLES).add(cableBlocks);
        tag(BlockTags.MINEABLE_WITH_AXE).add(cableBlocks);
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ICBlocks.BASIC_ENERGY_STORAGE.get());

        Block[] pickaxeBlocks = ICBlocks.BLOCKS.getEntries().stream()
                .map(holder -> (Block) holder.value())
                .filter(block -> block instanceof Wrenchable || block.getClass() == Block.class)
                .toArray(Block[]::new);
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(pickaxeBlocks);
        tag(BlockTags.NEEDS_STONE_TOOL).add(pickaxeBlocks);
        Block[] reactorParts = {ICBlocks.REACTOR_CHAMBER.get(), ICBlocks.REACTOR_VESSEL.get(), ICBlocks.REACTOR_ACCESS_HATCH.get(),
                ICBlocks.REACTOR_REDSTONE_PORT.get(), ICBlocks.REACTOR_FLUID_PORT.get()};
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(reactorParts);
        tag(BlockTags.NEEDS_STONE_TOOL).add(reactorParts);
        var gathering = GatheringContent.BLOCKS.getEntries().stream().map(entry -> (Block) entry.get()).toArray(Block[]::new);
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(gathering);
    }

    private static TagKey<Block> commonTag(String path) {
        return TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("c", path));
    }
}
