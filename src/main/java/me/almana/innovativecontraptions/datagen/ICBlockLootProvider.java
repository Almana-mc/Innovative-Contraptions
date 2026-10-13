package me.almana.innovativecontraptions.datagen;

import java.util.stream.Stream;
import me.almana.innovativecontraptions.content.gathering.GatheringContent;
import me.almana.innovativecontraptions.content.matter.MatterContent;
import me.almana.innovativecontraptions.registry.ICBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;

import java.util.Set;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class ICBlockLootProvider extends BlockLootSubProvider {
    protected ICBlockLootProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        dropSelf(ICBlocks.BIO_DIGESTER.get());
        dropSelf(ICBlocks.LIQUID_FUEL_GENERATOR.get());
        dropSelf(ICBlocks.BLOCK_SLICER.get());
        dropSelf(ICBlocks.PATTERN_SCANNER.get());
        dropSelf(MatterContent.REINFORCED_STONE.get());
        for (var entry : GatheringContent.BLOCKS.getEntries()) dropSelf(entry.get());
        dropSelf(ICBlocks.RUBBER_LOG.get());
        dropSelf(ICBlocks.RUBBER_WOOD.get());
        dropSelf(ICBlocks.STRIPPED_RUBBER_LOG.get());
        dropSelf(ICBlocks.STRIPPED_RUBBER_WOOD.get());
        dropSelf(ICBlocks.RUBBER_SAPLING.get());
        add(ICBlocks.RUBBER_LEAVES.get(), createLeavesDrops(ICBlocks.RUBBER_LEAVES.get(), ICBlocks.RUBBER_SAPLING.get(), NORMAL_LEAVES_SAPLING_CHANCES));

        dropSelf(ICBlocks.TIN_ORE.get());
        dropSelf(ICBlocks.LEAD_ORE.get());
        dropSelf(ICBlocks.SILVER_ORE.get());
        dropSelf(ICBlocks.URANIUM_ORE.get());
        dropSelf(ICBlocks.IRIDIUM_ORE.get());
        dropSelf(ICBlocks.LUMINATOR.get());
        dropSelf(ICBlocks.TIN_BLOCK.get());
        dropSelf(ICBlocks.BRONZE_BLOCK.get());
        dropSelf(ICBlocks.LEAD_BLOCK.get());
        dropSelf(ICBlocks.SILVER_BLOCK.get());
        dropSelf(ICBlocks.STEEL_BLOCK.get());
        dropSelf(ICBlocks.URANIUM_BLOCK.get());
        dropSelf(ICBlocks.CHARCOAL_BLOCK.get());
        dropSelf(ICBlocks.IRON_FURNACE.get());
        dropSelf(ICBlocks.ELECTRIC_FURNACE.get());
        dropSelf(ICBlocks.MACERATOR.get());
        dropSelf(ICBlocks.EXTRACTOR.get());
        dropSelf(ICBlocks.COMPRESSOR.get());
        dropSelf(ICBlocks.RECYCLER.get());
        dropSelf(ICBlocks.CANNING_MACHINE.get());
        dropSelf(ICBlocks.SOLID_CANNING_MACHINE.get());
        dropSelf(ICBlocks.BLAST_FURNACE.get());
        dropSelf(ICBlocks.ELECTRIC_HEATER.get());
        dropSelf(ICBlocks.INDUCTION_FURNACE.get());
        dropSelf(ICBlocks.METAL_FORMER.get());
        dropSelf(ICBlocks.ORE_WASHING_PLANT.get());
        dropSelf(ICBlocks.THERMAL_CENTRIFUGE.get());
        dropSelf(ICBlocks.GENERATOR.get());
        dropSelf(ICBlocks.SOLAR_PANEL.get());
        dropSelf(ICBlocks.WATER_MILL.get());
        dropSelf(ICBlocks.WINDMILL.get());
        dropSelf(ICBlocks.KINETIC_WIND_TURBINE.get());
        dropSelf(ICBlocks.GEOTHERMAL_GENERATOR.get());
        dropSelf(ICBlocks.KINETIC_GENERATOR.get());
        dropSelf(ICBlocks.BATBOX_PAD.get());
        dropSelf(ICBlocks.CESU_PAD.get());
        dropSelf(ICBlocks.MFE_PAD.get());
        dropSelf(ICBlocks.MFSU_PAD.get());
        dropSelf(ICBlocks.BATBOX.get());
        dropSelf(ICBlocks.CESU.get());
        dropSelf(ICBlocks.MFE.get());
        dropSelf(ICBlocks.MFSU.get());
        dropSelf(ICBlocks.LV_TRANSFORMER.get());
        dropSelf(ICBlocks.MV_TRANSFORMER.get());
        dropSelf(ICBlocks.HV_TRANSFORMER.get());
        dropSelf(ICBlocks.EV_TRANSFORMER.get());
        dropSelf(ICBlocks.NUCLEAR_REACTOR.get());
        dropSelf(ICBlocks.REACTOR_CHAMBER.get());
        dropSelf(ICBlocks.REACTOR_VESSEL.get());
        dropSelf(ICBlocks.REACTOR_ACCESS_HATCH.get());
        dropSelf(ICBlocks.REACTOR_REDSTONE_PORT.get());
        dropSelf(ICBlocks.REACTOR_FLUID_PORT.get());
        dropSelf(ICBlocks.RSH_COOLANT_INJECTOR.get());
        dropSelf(ICBlocks.LZH_COOLANT_INJECTOR.get());
        dropSelf(ICBlocks.COMPACT_ITEM_BUFFER.get());
        dropSelf(ICBlocks.LIQUID_HEAT_EXCHANGER.get());
        dropSelf(ICBlocks.MATTER_FABRICATOR.get());
        dropSelf(ICBlocks.REPLICATOR.get());
        add(ICBlocks.PATTERN_STORAGE.get(), createSingleItemTable(ICBlocks.PATTERN_STORAGE.get()).apply(
                //? if >=26.1 {
                CopyComponentsFunction.copyComponentsFromBlockEntity(LootContextParams.BLOCK_ENTITY)
                //?} else
                //CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
                        .include(MatterContent.ARCHIVED_PATTERNS.get())));
        dropSelf(ICBlocks.MINER.get());
        dropSelf(ICBlocks.ADVANCED_MINER.get());
        dropSelf(ICBlocks.CROP_HARVESTER.get());
        dropSelf(ICBlocks.CROP_MATRON.get());
        dropSelf(ICBlocks.CROP_STICKS.get());
        dropSelf(ICBlocks.WOODEN_SCAFFOLD.get());
        dropSelf(ICBlocks.FERMENTING_BARREL.get());
        dropSelf(ICBlocks.PUMP.get());

        dropSelf(ICBlocks.TIN_CABLE.get());
        dropSelf(ICBlocks.INSULATED_TIN_CABLE.get());
        dropSelf(ICBlocks.COPPER_CABLE.get());
        dropSelf(ICBlocks.INSULATED_COPPER_CABLE.get());
        dropSelf(ICBlocks.GOLD_CABLE.get());
        dropSelf(ICBlocks.INSULATED_GOLD_CABLE.get());
        dropSelf(ICBlocks.DOUBLE_INSULATED_GOLD_CABLE.get());
        dropSelf(ICBlocks.HV_CABLE.get());
        dropSelf(ICBlocks.INSULATED_HV_CABLE.get());
        dropSelf(ICBlocks.DOUBLE_INSULATED_HV_CABLE.get());
        dropSelf(ICBlocks.TRIPLE_INSULATED_HV_CABLE.get());
        dropSelf(ICBlocks.GLASS_FIBRE_CABLE.get());
        dropSelf(ICBlocks.BASIC_ENERGY_STORAGE.get());
        dropWhenSilkTouch(ICBlocks.REINFORCED_GLASS.get());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return Stream.concat(ICBlocks.BLOCKS.getEntries().stream(),
                GatheringContent.BLOCKS.getEntries().stream()).map(holder -> (Block) holder.value()).toList();
    }
}
