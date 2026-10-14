package me.almana.innovativecontraptions.datagen;

import me.almana.innovativecontraptions.InnovativeContraptions;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.List;
import java.util.Set;

public final class ICDataGenerators {
    //? if >=26.1 {
    public static void gatherData(GatherDataEvent.Client event) {
    //?} else
    //public static void gatherData(GatherDataEvent event) {
        event.createDatapackRegistryObjects(ICDatapackProvider.BUILDER, Set.of(InnovativeContraptions.MODID));
        event.createProvider((output, lookupProvider) -> new ICLanguageProvider(output));
        event.createProvider(ICRecipeProvider.Runner::new);
        event.createProvider(ICDataMapProvider::new);
        event.createProvider((output, lookupProvider) -> new LootTableProvider(
                output,
                Set.of(),
                List.of(new LootTableProvider.SubProviderEntry(ICBlockLootProvider::new, LootContextParamSets.BLOCK)),
                lookupProvider
        ));
        //? if >=26.1 {
        event.createBlockAndItemTags(
                (output, lookupProvider) -> new ICBlockTagsProvider(output, lookupProvider),
                ICItemTagsProvider::new
        );
        //?} else {
        /*event.createBlockAndItemTags(
                (output, lookupProvider) -> new ICBlockTagsProvider(output, lookupProvider, event.getExistingFileHelper()),
                (output, lookupProvider, blockTags) -> new ICItemTagsProvider(output, lookupProvider, blockTags, event.getExistingFileHelper())
        );
        *///?}
    }

    private ICDataGenerators() {
    }
}
