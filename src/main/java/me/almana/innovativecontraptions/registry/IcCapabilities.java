package me.almana.innovativecontraptions.registry;

import me.almana.innovativecontraptions.InnovativeContraptions;
import me.almana.innovativecontraptions.content.energy.EuSink;
import me.almana.innovativecontraptions.content.energy.EuSource;
import me.almana.innovativecontraptions.content.energy.debug.CreativeSourceBlockEntity;
import me.almana.innovativecontraptions.content.energy.debug.EnergyStorageBlockEntity;
import me.almana.innovativecontraptions.content.machine.MaceratorBlockEntity;
import me.almana.innovativecontraptions.content.fluid.FluidCellContents;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.item.WorldlyContainerWrapper;
import me.almana.innovativecontraptions.content.generator.GeneratorBlockEntity;
import me.almana.innovativecontraptions.content.generator.SolarPanelBlockEntity;
import me.almana.innovativecontraptions.content.generator.GeothermalGeneratorBlockEntity;
import me.almana.innovativecontraptions.content.generator.KineticGeneratorBlockEntity;
import me.almana.innovativecontraptions.content.generator.WaterMillBlockEntity;
import me.almana.innovativecontraptions.content.generator.WindmillBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public final class IcCapabilities {
    public static final BlockCapability<EuSink, Direction> EU_SINK = BlockCapability.createSided(
            Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, "eu_sink"),
            EuSink.class
    );
    public static final BlockCapability<EuSource, Direction> EU_SOURCE = BlockCapability.createSided(
            Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, "eu_source"),
            EuSource.class
    );

    public static void register(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(EU_SOURCE, ICBlockEntities.ELECTRICAL.get(), (be, side) -> be.emits(side) ? be : null);
        event.registerBlockEntity(EU_SINK, ICBlockEntities.ELECTRICAL.get(), (be, side) -> be.accepts(side) ? be : null);
        event.registerBlockEntity(Capabilities.Item.BLOCK, ICBlockEntities.ELECTRICAL.get(), WorldlyContainerWrapper::new);
        event.registerItem(Capabilities.Fluid.ITEM, (stack, access) -> new FluidCellContents(access),
                ICItems.EMPTY_CELL.get(), ICItems.WATER_CELL.get(), ICItems.LAVA_CELL.get());
        event.registerBlockEntity(Capabilities.Item.BLOCK, ICBlockEntities.MACERATOR.get(), WorldlyContainerWrapper::new);
        event.registerBlockEntity(
                EU_SOURCE,
                ICBlockEntities.GENERATOR.get(),
                (GeneratorBlockEntity be, Direction side) -> be
        );
        event.registerBlockEntity(
                EU_SOURCE,
                ICBlockEntities.SOLAR_PANEL.get(),
                (SolarPanelBlockEntity be, Direction side) -> be
        );
        event.registerBlockEntity(
                EU_SOURCE,
                ICBlockEntities.WATER_MILL.get(),
                (WaterMillBlockEntity be, Direction side) -> be
        );
        event.registerBlockEntity(
                EU_SOURCE,
                ICBlockEntities.WINDMILL.get(),
                (WindmillBlockEntity be, Direction side) -> be
        );
        event.registerBlockEntity(
                EU_SOURCE,
                ICBlockEntities.GEOTHERMAL_GENERATOR.get(),
                (GeothermalGeneratorBlockEntity be, Direction side) -> be
        );
        event.registerBlockEntity(
                EU_SOURCE,
                ICBlockEntities.KINETIC_GENERATOR.get(),
                (KineticGeneratorBlockEntity be, Direction side) -> be
        );
        event.registerBlockEntity(
                EU_SOURCE,
                ICBlockEntities.CREATIVE_SOURCE.get(),
                (CreativeSourceBlockEntity be, Direction side) -> be
        );
        event.registerBlockEntity(
                EU_SINK,
                ICBlockEntities.ENERGY_STORAGE.get(),
                (EnergyStorageBlockEntity be, Direction side) -> be
        );
        event.registerBlockEntity(
                EU_SINK,
                ICBlockEntities.MACERATOR.get(),
                (MaceratorBlockEntity be, Direction side) -> be
        );
    }

    private IcCapabilities() {
    }
}
