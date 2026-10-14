package me.almana.innovativecontraptions.datagen;

import java.util.concurrent.CompletableFuture;
import me.almana.innovativecontraptions.registry.ICItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.builtin.FurnaceFuel;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;

final class ICDataMapProvider extends DataMapProvider {
    ICDataMapProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) { super(output, lookupProvider); }

    @Override protected void gather(HolderLookup.Provider provider) {
        builder(NeoForgeDataMaps.FURNACE_FUELS)
                .add(Items.CACTUS.builtInRegistryHolder(), new FurnaceFuel(50), false)
                .add(Items.SUGAR_CANE.builtInRegistryHolder(), new FurnaceFuel(50), false)
                .add(ICItems.RUBBER_SAPLING, new FurnaceFuel(80), false)
                .add(ICItems.SCRAP, new FurnaceFuel(350), false)
                .add(ICItems.SCRAP_BOX, new FurnaceFuel(3150), false);
    }
}
