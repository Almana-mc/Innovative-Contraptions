package me.almana.innovativecontraptions.datagen;

import java.util.concurrent.CompletableFuture;
import me.almana.innovativecontraptions.content.matter.MatterCosts;
import me.almana.innovativecontraptions.registry.ICItems;
import me.almana.innovativecontraptions.registry.ICTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.builtin.FurnaceFuel;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;

final class ICDataMapProvider extends DataMapProvider {
    ICDataMapProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) { super(output, lookupProvider); }

    @Override protected void gather(HolderLookup.Provider provider) {
        builder(NeoForgeDataMaps.FURNACE_FUELS)
                .add(Tags.Items.CROPS_CACTUS, new FurnaceFuel(50), false)
                .add(Tags.Items.CROPS_SUGAR_CANE, new FurnaceFuel(50), false)
                .add(ICItems.RUBBER_SAPLING, new FurnaceFuel(80), false)
                .add(ICItems.SCRAP, new FurnaceFuel(350), false)
                .add(ICItems.SCRAP_BOX, new FurnaceFuel(3150), false);
        builder(MatterCosts.UU_VALUE)
                .add(Tags.Items.COBBLESTONES_NORMAL, 1.0, false)
                .add(BuiltInRegistries.ITEM.wrapAsHolder(Items.DIRT), 1.0, false)
                .add(BuiltInRegistries.ITEM.wrapAsHolder(Items.COAL), 1.0, false)
                .add(Tags.Items.SANDS_COLORLESS, 1.0, false)
                .add(Tags.Items.GRAVELS, 1.0, false)
                .add(Tags.Items.DUSTS_REDSTONE, 1.0, false)
                .add(Tags.Items.ORES_IRON, 1.0, false)
                .add(Tags.Items.ORES_COPPER, 1.0, false)
                .add(ICTags.ORES_TIN, 1.0, false)
                .add(Tags.Items.GEMS_LAPIS, 4.0, false)
                .add(Tags.Items.ORES_GOLD, 10.0, false)
                .add(BuiltInRegistries.ITEM.wrapAsHolder(Items.CLAY_BALL), 17.0, false)
                .add(Tags.Items.GEMS_DIAMOND, 24.0, false)
                .add(RecipeTags.logs("spruce"), 29.0, false)
                .add(RecipeTags.logs("oak"), 35.0, false)
                .add(RecipeTags.logs("birch"), 56.0, false)
                .add(Tags.Items.GEMS_EMERALD, 268.0, false);
    }
}
