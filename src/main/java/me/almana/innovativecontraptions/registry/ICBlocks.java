package me.almana.innovativecontraptions.registry;

import me.almana.innovativecontraptions.InnovativeContraptions;
import me.almana.innovativecontraptions.content.agriculture.*;
import me.almana.innovativecontraptions.content.bioenergy.BioenergyBlock;
import me.almana.innovativecontraptions.content.energy.storage.ElectricalBlock;
import me.almana.innovativecontraptions.content.energy.storage.ElectricalKind;
import me.almana.innovativecontraptions.content.energy.cable.CableBlock;
import me.almana.innovativecontraptions.content.energy.cable.CableSpec;
import me.almana.innovativecontraptions.content.energy.debug.CreativeSourceBlock;
import me.almana.innovativecontraptions.content.energy.debug.EnergyStorageBlock;
import me.almana.innovativecontraptions.content.gathering.GatheringBlock;
import me.almana.innovativecontraptions.content.generator.GeneratorBlock;
import me.almana.innovativecontraptions.content.generator.SolarPanelBlock;
import me.almana.innovativecontraptions.content.generator.GeothermalGeneratorBlock;
import me.almana.innovativecontraptions.content.generator.KineticGeneratorBlock;
import me.almana.innovativecontraptions.content.generator.KineticWindTurbineBlock;
import me.almana.innovativecontraptions.content.generator.WaterMillBlock;
import me.almana.innovativecontraptions.content.generator.WindmillBlock;
import me.almana.innovativecontraptions.content.machine.IronFurnaceBlock;
import me.almana.innovativecontraptions.content.machine.MaceratorBlock;
import me.almana.innovativecontraptions.content.machine.PoweredLightBlock;
import me.almana.innovativecontraptions.content.machine.ReinforcedGlassBlock;
import me.almana.innovativecontraptions.content.matter.MatterBlock;
import me.almana.innovativecontraptions.content.processing.ProcessingCannerBlock;
import me.almana.innovativecontraptions.content.reactor.CoolantInjectorBlock;
import me.almana.innovativecontraptions.content.reactor.NuclearReactorBlock;
import me.almana.innovativecontraptions.content.reactor.ReactorCasingBlock;
import me.almana.innovativecontraptions.content.reactor.ReactorFluidPortBlock;
import me.almana.innovativecontraptions.content.reactor.ReactorChamberBlock;
import me.almana.innovativecontraptions.content.thermal.ThermalBlock;
import me.almana.innovativecontraptions.content.machine.MachineBlock;
import me.almana.innovativecontraptions.content.processing.ProcessingBlock;
import me.almana.innovativecontraptions.content.rubber.RubberLogBlock;
//? if <26.1
//import me.almana.innovativecontraptions.compat.BlockRegister;
import me.almana.innovativecontraptions.world.ICWorldgenKeys;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SoundType;
//? if >=26.1
import net.minecraft.world.level.block.UntintedParticleLeavesBlock;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Optional;

public final class ICBlocks {
    //? if >=26.1 {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(InnovativeContraptions.MODID);
    //?} else
    //public static final BlockRegister BLOCKS = new BlockRegister(InnovativeContraptions.MODID);

    public static final DeferredBlock<RubberLogBlock> RUBBER_LOG = BLOCKS.registerBlock(
            "rubber_log",
            RubberLogBlock::new,
            () -> woodProperties().randomTicks()
    );

    public static final DeferredBlock<RotatedPillarBlock> RUBBER_WOOD = BLOCKS.registerBlock(
            "rubber_wood",
            RotatedPillarBlock::new,
            ICBlocks::woodProperties
    );

    public static final DeferredBlock<RotatedPillarBlock> STRIPPED_RUBBER_LOG = BLOCKS.registerBlock(
            "stripped_rubber_log",
            RotatedPillarBlock::new,
            ICBlocks::strippedWoodProperties
    );

    public static final DeferredBlock<RotatedPillarBlock> STRIPPED_RUBBER_WOOD = BLOCKS.registerBlock(
            "stripped_rubber_wood",
            RotatedPillarBlock::new,
            ICBlocks::strippedWoodProperties
    );

    public static final DeferredBlock<LeavesBlock> RUBBER_LEAVES = BLOCKS.registerBlock(
            "rubber_leaves",
            //? if >=26.1 {
            properties -> new UntintedParticleLeavesBlock(0.01F, ParticleTypes.PALE_OAK_LEAVES, properties),
            //?} else
            //LeavesBlock::new,
            () -> BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .strength(0.2F)
                    .randomTicks()
                    .sound(SoundType.GRASS)
                    .noOcclusion()
                    .ignitedByLava()
    );

    public static final TreeGrower RUBBER_TREE = new TreeGrower(
            "rubber_tree",
            Optional.empty(),
            Optional.of(ICWorldgenKeys.RUBBER_TREE_CONFIGURED),
            Optional.empty()
    );

    public static final DeferredBlock<SaplingBlock> RUBBER_SAPLING = BLOCKS.registerBlock(
            "rubber_sapling",
            properties -> new SaplingBlock(RUBBER_TREE, properties),
            () -> BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .noCollision()
                    .randomTicks()
                    .instabreak()
                    .sound(SoundType.GRASS)
    );

    public static final DeferredBlock<Block> TIN_ORE = ore("tin_ore");
    public static final DeferredBlock<Block> LEAD_ORE = ore("lead_ore");
    public static final DeferredBlock<Block> SILVER_ORE = ore("silver_ore");
    public static final DeferredBlock<Block> URANIUM_ORE = ore("uranium_ore");
    public static final DeferredBlock<Block> IRIDIUM_ORE = ore("iridium_ore");
    public static final DeferredBlock<PoweredLightBlock> LUMINATOR =
            BLOCKS.registerBlock("luminator", PoweredLightBlock::new,
                    () -> BlockBehaviour.Properties.of().strength(0.5F).noOcclusion().sound(SoundType.GLASS)
                            .lightLevel(state -> state.getValue(PoweredLightBlock.LIT) ? 15 : 0));
    public static final DeferredBlock<ReinforcedGlassBlock> REINFORCED_GLASS =
            BLOCKS.registerBlock("reinforced_glass", ReinforcedGlassBlock::new,
                    () -> BlockBehaviour.Properties.of().strength(5.0F, 108.0F).noOcclusion().sound(SoundType.GLASS));

    public static final DeferredBlock<Block> TIN_BLOCK = metalBlock("tin_block");
    public static final DeferredBlock<Block> BRONZE_BLOCK = metalBlock("bronze_block");
    public static final DeferredBlock<Block> LEAD_BLOCK = metalBlock("lead_block");
    public static final DeferredBlock<Block> SILVER_BLOCK = metalBlock("silver_block");
    public static final DeferredBlock<Block> STEEL_BLOCK = metalBlock("steel_block");
    public static final DeferredBlock<Block> URANIUM_BLOCK = metalBlock("uranium_block");
    public static final DeferredBlock<Block> CHARCOAL_BLOCK = BLOCKS.registerBlock(
            "charcoal_block",
            Block::new,
            () -> BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(5.0F, 6.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)
    );

    public static final DeferredBlock<IronFurnaceBlock> IRON_FURNACE = BLOCKS.registerBlock(
            "iron_furnace", IronFurnaceBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(2.0F, 6.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL));
    public static final DeferredBlock<ProcessingBlock> ELECTRIC_FURNACE = BLOCKS.registerBlock("electric_furnace", ProcessingBlock::new, () -> BlockBehaviour.Properties.of().strength(2.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<MaceratorBlock> MACERATOR = BLOCKS.registerBlock(
            "macerator", MaceratorBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(2.0F, 6.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL));
    public static final DeferredBlock<ProcessingBlock> EXTRACTOR = BLOCKS.registerBlock("extractor", ProcessingBlock::new, () -> BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<ProcessingBlock> COMPRESSOR = BLOCKS.registerBlock("compressor", ProcessingBlock::new, () -> BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<ProcessingBlock> RECYCLER = BLOCKS.registerBlock("recycler", ProcessingBlock::new,
            () -> BlockBehaviour.Properties.of().strength(2.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<ProcessingCannerBlock> CANNING_MACHINE = BLOCKS.registerBlock("canning_machine", ProcessingCannerBlock::new, () -> BlockBehaviour.Properties.of().strength(2.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<ProcessingBlock> SOLID_CANNING_MACHINE = BLOCKS.registerBlock("solid_canning_machine", ProcessingBlock::new,
            () -> BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.METAL));

    public static final DeferredBlock<ThermalBlock> BLAST_FURNACE = BLOCKS.registerBlock("blast_furnace", ThermalBlock::new,
            () -> BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<ThermalBlock> ELECTRIC_HEATER = BLOCKS.registerBlock("electric_heater", ThermalBlock::new,
            () -> BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<ThermalBlock> LIQUID_HEAT_EXCHANGER = BLOCKS.registerBlock("liquid_heat_exchanger", ThermalBlock::new,
            () -> BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<ProcessingBlock> INDUCTION_FURNACE = BLOCKS.registerBlock("induction_furnace", ProcessingBlock::new, () -> BlockBehaviour.Properties.of().strength(2.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<ProcessingBlock> METAL_FORMER = BLOCKS.registerBlock("metal_former", ProcessingBlock::new, () -> BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<ProcessingBlock> ORE_WASHING_PLANT = BLOCKS.registerBlock("ore_washing_plant", ProcessingBlock::new, () -> BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<ProcessingBlock> THERMAL_CENTRIFUGE = BLOCKS.registerBlock("thermal_centrifuge", ProcessingBlock::new, () -> BlockBehaviour.Properties.of().strength(2.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.METAL));

    public static final DeferredBlock<GeneratorBlock> GENERATOR = BLOCKS.registerBlock(
            "generator", GeneratorBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(2.0F, 6.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL));
    public static final DeferredBlock<SolarPanelBlock> SOLAR_PANEL = BLOCKS.registerBlock(
            "solar_panel", SolarPanelBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(2.0F, 6.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL));
    public static final DeferredBlock<WaterMillBlock> WATER_MILL = BLOCKS.registerBlock(
            "water_mill", WaterMillBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(2.0F, 6.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL));
    public static final DeferredBlock<WindmillBlock> WINDMILL = BLOCKS.registerBlock(
            "windmill", WindmillBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL).strength(2.0F, 6.0F)
                    .requiresCorrectToolForDrops().sound(SoundType.METAL));

    public static final DeferredBlock<GeothermalGeneratorBlock> GEOTHERMAL_GENERATOR = BLOCKS.registerBlock(
            "geothermal_generator", GeothermalGeneratorBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL).strength(2.0F, 6.0F)
                    .requiresCorrectToolForDrops().sound(SoundType.METAL));

    public static final DeferredBlock<KineticWindTurbineBlock> KINETIC_WIND_TURBINE = BLOCKS.registerBlock(
            "kinetic_wind_turbine", KineticWindTurbineBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL).strength(2.0F, 6.0F)
                    .requiresCorrectToolForDrops().sound(SoundType.METAL));

    public static final DeferredBlock<KineticGeneratorBlock> KINETIC_GENERATOR = BLOCKS.registerBlock(
            "kinetic_generator", KineticGeneratorBlock::new, () -> BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL).strength(2.0F, 6.0F)
                    .requiresCorrectToolForDrops().sound(SoundType.METAL));

    public static final DeferredBlock<ElectricalBlock> BATBOX = electrical("batbox", ElectricalKind.BATBOX);
    public static final DeferredBlock<ElectricalBlock> CESU = electrical("cesu", ElectricalKind.CESU);
    public static final DeferredBlock<ElectricalBlock> MFE = electrical("mfe", ElectricalKind.MFE);
    public static final DeferredBlock<ElectricalBlock> MFSU = electrical("mfsu", ElectricalKind.MFSU);

    public static final DeferredBlock<ElectricalBlock> LV_TRANSFORMER = electrical("lv_transformer", ElectricalKind.LV_TRANSFORMER);
    public static final DeferredBlock<ElectricalBlock> MV_TRANSFORMER = electrical("mv_transformer", ElectricalKind.MV_TRANSFORMER);
    public static final DeferredBlock<ElectricalBlock> HV_TRANSFORMER = electrical("hv_transformer", ElectricalKind.HV_TRANSFORMER);
    public static final DeferredBlock<ElectricalBlock> EV_TRANSFORMER = electrical("ev_transformer", ElectricalKind.EV_TRANSFORMER);

    public static final DeferredBlock<NuclearReactorBlock> NUCLEAR_REACTOR = BLOCKS.registerBlock("nuclear_reactor", NuclearReactorBlock::new,
            () -> BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<ReactorChamberBlock> REACTOR_CHAMBER = BLOCKS.registerBlock("reactor_chamber", ReactorChamberBlock::new,
            () -> BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<ReactorCasingBlock> REACTOR_VESSEL = vessel("reactor_vessel", false);
    public static final DeferredBlock<ReactorCasingBlock> REACTOR_ACCESS_HATCH = vessel("reactor_access_hatch", true);
    public static final DeferredBlock<ReactorCasingBlock> REACTOR_REDSTONE_PORT = vessel("reactor_redstone_port", false);
    public static final DeferredBlock<ReactorFluidPortBlock> REACTOR_FLUID_PORT = BLOCKS.registerBlock("reactor_fluid_port", ReactorFluidPortBlock::new,
            () -> BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(40.0F, 90.0F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<CoolantInjectorBlock> RSH_COOLANT_INJECTOR = BLOCKS.registerBlock("rsh_coolant_injector", CoolantInjectorBlock::new,
            () -> BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<CoolantInjectorBlock> LZH_COOLANT_INJECTOR = BLOCKS.registerBlock("lzh_coolant_injector", CoolantInjectorBlock::new,
            () -> BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<MachineBlock> COMPACT_ITEM_BUFFER = machine("compact_item_buffer");

    public static final DeferredBlock<MatterBlock> MATTER_FABRICATOR = BLOCKS.registerBlock("matter_fabricator", MatterBlock::new, () -> BlockBehaviour.Properties.of().strength(2.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<MatterBlock> REPLICATOR = BLOCKS.registerBlock("replicator", MatterBlock::new, () -> BlockBehaviour.Properties.of().strength(2.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<MatterBlock> PATTERN_STORAGE = BLOCKS.registerBlock("pattern_storage", MatterBlock::new, () -> BlockBehaviour.Properties.of().strength(2.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<GatheringBlock> MINER = BLOCKS.registerBlock("miner", GatheringBlock::new, () -> BlockBehaviour.Properties.of().strength(2.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<GatheringBlock> ADVANCED_MINER = BLOCKS.registerBlock("advanced_miner", GatheringBlock::new, () -> BlockBehaviour.Properties.of().strength(2.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<FarmingMachineBlock> CROP_HARVESTER = BLOCKS.registerBlock("crop_harvester", FarmingMachineBlock::new, () -> BlockBehaviour.Properties.of().strength(3.5F).requiresCorrectToolForDrops());
    public static final DeferredBlock<FarmingMachineBlock> CROP_MATRON = BLOCKS.registerBlock("crop_matron", FarmingMachineBlock::new, () -> BlockBehaviour.Properties.of().strength(3.5F).requiresCorrectToolForDrops());
    public static final DeferredBlock<CropPlotBlock> CROP_STICKS = BLOCKS.registerBlock("crop_sticks", CropPlotBlock::new, () -> BlockBehaviour.Properties.of().strength(0.2F).noCollision().noOcclusion().lightLevel(state -> state.getValue(CropPlotBlock.SPECIES) == CropSpecies.RED_GRAIN.ordinal() && state.getValue(CropPlotBlock.AGE) == 7 ? 7 : 0));
    public static final DeferredBlock<WoodenScaffoldBlock> WOODEN_SCAFFOLD = BLOCKS.registerBlock("wooden_scaffold", WoodenScaffoldBlock::new, () -> BlockBehaviour.Properties.of().strength(0.5F).noOcclusion());
    public static final DeferredBlock<FermentingBarrelBlock> FERMENTING_BARREL = BLOCKS.registerBlock("fermenting_barrel", FermentingBarrelBlock::new, () -> BlockBehaviour.Properties.of().strength(2F));
    public static final DeferredBlock<GatheringBlock> PUMP = BLOCKS.registerBlock("pump", GatheringBlock::new, () -> BlockBehaviour.Properties.of().strength(2.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.METAL));

    public static final DeferredBlock<CableBlock> TIN_CABLE = cable("tin_cable", new CableSpec(32, 0.20, false, true));
    public static final DeferredBlock<CableBlock> INSULATED_TIN_CABLE = cable("insulated_tin_cable", new CableSpec(32, 0.20, true, false));
    public static final DeferredBlock<CableBlock> COPPER_CABLE = cable("copper_cable", new CableSpec(128, 0.30, false, true));
    public static final DeferredBlock<CableBlock> INSULATED_COPPER_CABLE = cable("insulated_copper_cable", new CableSpec(128, 0.20, true, false));
    public static final DeferredBlock<CableBlock> GOLD_CABLE = cable("gold_cable", new CableSpec(512, 0.45, false, true));
    public static final DeferredBlock<CableBlock> INSULATED_GOLD_CABLE = cable("insulated_gold_cable", new CableSpec(512, 0.35, true, false));
    public static final DeferredBlock<CableBlock> DOUBLE_INSULATED_GOLD_CABLE = cable("double_insulated_gold_cable", new CableSpec(512, 0.25, true, false));
    public static final DeferredBlock<CableBlock> HV_CABLE = cable("hv_cable", new CableSpec(2048, 0.8, false, true, 6, 32));
    public static final DeferredBlock<CableBlock> INSULATED_HV_CABLE = cable("insulated_hv_cable", new CableSpec(2048, 0.8, true, true, 8, 128));
    public static final DeferredBlock<CableBlock> DOUBLE_INSULATED_HV_CABLE = cable("double_insulated_hv_cable", new CableSpec(2048, 0.8, true, true, 10, 512));
    public static final DeferredBlock<CableBlock> TRIPLE_INSULATED_HV_CABLE = cable("triple_insulated_hv_cable", new CableSpec(2048, 0.8, true, true, 12, 2048));
    public static final DeferredBlock<CableBlock> GLASS_FIBRE_CABLE = cable("glass_fibre_cable", new CableSpec(8192, 0.025, true, false));

    public static final DeferredBlock<CreativeSourceBlock> CREATIVE_ENERGY_SOURCE = BLOCKS.registerBlock(
            "creative_energy_source",
            CreativeSourceBlock::new,
            () -> BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE)
                    .strength(-1.0F, 3600000.0F)
                    .noLootTable()
    );

    public static final DeferredBlock<EnergyStorageBlock> BASIC_ENERGY_STORAGE = BLOCKS.registerBlock(
            "basic_energy_storage",
            EnergyStorageBlock::new,
            () -> BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(2.0F, 6.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL)
    );

    private static DeferredBlock<CableBlock> cable(String name, CableSpec spec) {
        return BLOCKS.registerBlock(
                name,
                properties -> new CableBlock(spec, properties),
                () -> BlockBehaviour.Properties.of()
                        .mapColor(MapColor.METAL)
                        .strength(0.3F)
                        .sound(SoundType.WOOL)
                        .noOcclusion()
                        .noCollision()
        );
    }

    private static DeferredBlock<Block> ore(String name) {
        return BLOCKS.registerBlock(name, Block::new, () -> BlockBehaviour.Properties.of()
                .mapColor(MapColor.STONE)
                .strength(3.0F, 3.0F)
                .requiresCorrectToolForDrops()
                .sound(SoundType.STONE));
    }

    private static DeferredBlock<Block> metalBlock(String name) {
        return BLOCKS.registerBlock(name, Block::new, () -> BlockBehaviour.Properties.of()
                .mapColor(MapColor.METAL)
                .strength(5.0F, 6.0F)
                .requiresCorrectToolForDrops()
                .sound(SoundType.METAL));
    }

    public static final DeferredBlock<ElectricalBlock> BATBOX_PAD = electrical("batbox_pad", ElectricalKind.BATBOX_PAD);
    public static final DeferredBlock<ElectricalBlock> CESU_PAD = electrical("cesu_pad", ElectricalKind.CESU_PAD);
    public static final DeferredBlock<ElectricalBlock> MFE_PAD = electrical("mfe_pad", ElectricalKind.MFE_PAD);
    public static final DeferredBlock<ElectricalBlock> MFSU_PAD = electrical("mfsu_pad", ElectricalKind.MFSU_PAD);

    private static DeferredBlock<ElectricalBlock> electrical(String name, ElectricalKind kind) {
        return BLOCKS.registerBlock(name, properties -> new ElectricalBlock(properties, kind),
                () -> BlockBehaviour.Properties.of().strength(2.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    }

    private static DeferredBlock<MachineBlock> machine(String name) {
        return BLOCKS.registerBlock(name, MachineBlock::new, () -> BlockBehaviour.Properties.of()
                .mapColor(MapColor.METAL)
                .strength(2.0F, 6.0F)
                .requiresCorrectToolForDrops()
                .sound(SoundType.METAL));
    }

    private static DeferredBlock<ReactorCasingBlock> vessel(String name, boolean hatch) {
        return BLOCKS.registerBlock(name, properties -> new ReactorCasingBlock(properties, hatch), () -> BlockBehaviour.Properties.of()
                .mapColor(MapColor.METAL).strength(40.0F, 90.0F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    }

    private static BlockBehaviour.Properties woodProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.WOOD)
                .strength(2.0F)
                .sound(SoundType.WOOD)
                .ignitedByLava();
    }

    private static BlockBehaviour.Properties strippedWoodProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.WOOD)
                .strength(2.0F)
                .sound(SoundType.WOOD)
                .ignitedByLava();
    }

    public static final DeferredBlock<ProcessingBlock> BLOCK_SLICER = BLOCKS.registerBlock("block_slicer", ProcessingBlock::new, () -> BlockBehaviour.Properties.of().strength(2.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<MatterBlock> PATTERN_SCANNER = BLOCKS.registerBlock("pattern_scanner", MatterBlock::new, () -> BlockBehaviour.Properties.of().strength(2.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<BioenergyBlock> BIO_DIGESTER = BLOCKS.registerBlock("bio_digester", BioenergyBlock::new, () -> BlockBehaviour.Properties.of().strength(2F, 6F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    public static final DeferredBlock<BioenergyBlock> LIQUID_FUEL_GENERATOR = BLOCKS.registerBlock("liquid_fuel_generator", BioenergyBlock::new, () -> BlockBehaviour.Properties.of().strength(2F, 6F).requiresCorrectToolForDrops().sound(SoundType.METAL));
    private ICBlocks() {
    }
}
