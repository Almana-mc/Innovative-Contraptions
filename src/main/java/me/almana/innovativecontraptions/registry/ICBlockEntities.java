package me.almana.innovativecontraptions.registry;

import me.almana.innovativecontraptions.InnovativeContraptions;
import me.almana.innovativecontraptions.content.energy.debug.CreativeSourceBlockEntity;
import me.almana.innovativecontraptions.content.energy.debug.EnergyStorageBlockEntity;
import me.almana.innovativecontraptions.content.energy.storage.ElectricalBlockEntity;
import me.almana.innovativecontraptions.content.machine.IronFurnaceBlockEntity;
import me.almana.innovativecontraptions.content.machine.MaceratorBlockEntity;
import me.almana.innovativecontraptions.content.generator.GeneratorBlockEntity;
import me.almana.innovativecontraptions.content.generator.SolarPanelBlockEntity;
import me.almana.innovativecontraptions.content.generator.GeothermalGeneratorBlockEntity;
import me.almana.innovativecontraptions.content.generator.KineticGeneratorBlockEntity;
import me.almana.innovativecontraptions.content.generator.WaterMillBlockEntity;
import me.almana.innovativecontraptions.content.generator.WindmillBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ICBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, InnovativeContraptions.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GeneratorBlockEntity>> GENERATOR =
            BLOCK_ENTITIES.register("generator", () -> new BlockEntityType<>(
                    GeneratorBlockEntity::new,
                    ICBlocks.GENERATOR.get()
            ));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SolarPanelBlockEntity>> SOLAR_PANEL =
            BLOCK_ENTITIES.register("solar_panel", () -> new BlockEntityType<>(
                    SolarPanelBlockEntity::new,
                    ICBlocks.SOLAR_PANEL.get()
            ));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WaterMillBlockEntity>> WATER_MILL =
            BLOCK_ENTITIES.register("water_mill", () -> new BlockEntityType<>(
                    WaterMillBlockEntity::new,
                    ICBlocks.WATER_MILL.get()
            ));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WindmillBlockEntity>> WINDMILL =
            BLOCK_ENTITIES.register("windmill", () -> new BlockEntityType<>(
                    WindmillBlockEntity::new,
                    ICBlocks.WINDMILL.get()
            ));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GeothermalGeneratorBlockEntity>> GEOTHERMAL_GENERATOR =
            BLOCK_ENTITIES.register("geothermal_generator", () -> new BlockEntityType<>(
                    GeothermalGeneratorBlockEntity::new,
                    ICBlocks.GEOTHERMAL_GENERATOR.get()
            ));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<KineticGeneratorBlockEntity>> KINETIC_GENERATOR =
            BLOCK_ENTITIES.register("kinetic_generator", () -> new BlockEntityType<>(
                    KineticGeneratorBlockEntity::new,
                    ICBlocks.KINETIC_GENERATOR.get()
            ));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CreativeSourceBlockEntity>> CREATIVE_SOURCE =
            BLOCK_ENTITIES.register("creative_energy_source", () -> new BlockEntityType<>(
                    CreativeSourceBlockEntity::new,
                    ICBlocks.CREATIVE_ENERGY_SOURCE.get()
            ));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EnergyStorageBlockEntity>> ENERGY_STORAGE =
            BLOCK_ENTITIES.register("basic_energy_storage", () -> new BlockEntityType<>(
                    EnergyStorageBlockEntity::new,
                    ICBlocks.BASIC_ENERGY_STORAGE.get()
            ));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<IronFurnaceBlockEntity>> IRON_FURNACE =
            BLOCK_ENTITIES.register("iron_furnace", () -> new BlockEntityType<>(
                    IronFurnaceBlockEntity::new,
                    ICBlocks.IRON_FURNACE.get()
            ));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MaceratorBlockEntity>> MACERATOR =
            BLOCK_ENTITIES.register("macerator", () -> new BlockEntityType<>(
                    MaceratorBlockEntity::new,
                    ICBlocks.MACERATOR.get()
            ));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ElectricalBlockEntity>> ELECTRICAL =
            BLOCK_ENTITIES.register("electrical", () -> new BlockEntityType<>(
                    ElectricalBlockEntity::create,
                    ICBlocks.BATBOX.get(), ICBlocks.CESU.get(), ICBlocks.MFE.get(), ICBlocks.MFSU.get(),
                    ICBlocks.BATBOX_PAD.get(), ICBlocks.CESU_PAD.get(), ICBlocks.MFE_PAD.get(), ICBlocks.MFSU_PAD.get(),
                    ICBlocks.LV_TRANSFORMER.get(), ICBlocks.MV_TRANSFORMER.get(), ICBlocks.HV_TRANSFORMER.get(), ICBlocks.EV_TRANSFORMER.get()));
    private ICBlockEntities() {
    }
}
