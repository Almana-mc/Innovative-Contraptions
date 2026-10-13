package me.almana.innovativecontraptions.content.gathering;

import me.almana.innovativecontraptions.InnovativeContraptions;
import me.almana.innovativecontraptions.registry.ICBlocks;
import me.almana.innovativecontraptions.registry.IcCapabilities;
//? if <26.1 {
/*import me.almana.innovativecontraptions.compat.BlockRegister;
import me.almana.innovativecontraptions.compat.ItemRegister;
*///?}
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.transfer.item.WorldlyContainerWrapper;

public final class GatheringContent {
    //? if >=26.1 {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(InnovativeContraptions.MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(InnovativeContraptions.MODID);
    //?} else {
    /*public static final BlockRegister BLOCKS = new BlockRegister(InnovativeContraptions.MODID);
    public static final ItemRegister ITEMS = new ItemRegister(InnovativeContraptions.MODID);
    *///?}
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, InnovativeContraptions.MODID);
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(BuiltInRegistries.MENU, InnovativeContraptions.MODID);
    public static final DeferredBlock<BorePipeBlock> PIPE = BLOCKS.registerBlock("bore_pipe", BorePipeBlock::new, () -> BlockBehaviour.Properties.of().strength(.5F).sound(SoundType.METAL).noOcclusion());
    public static final DeferredBlock<GatheringBlock> TERRAIN = BLOCKS.registerBlock("terrain_station", GatheringBlock::new, GatheringContent::machineProperties);
    public static final DeferredBlock<Block> RELOCATION_CORE = BLOCKS.registerBlock("relocation_core", Block::new, GatheringContent::machineProperties);
    public static final DeferredItem<BlockItem> PIPE_ITEM = ITEMS.registerSimpleBlockItem(PIPE);
    public static final DeferredItem<BlockItem> TERRAIN_ITEM = ITEMS.registerSimpleBlockItem(TERRAIN);
    public static final DeferredItem<BlockItem> RELOCATION_ITEM = ITEMS.registerSimpleBlockItem(RELOCATION_CORE);
    public static final DeferredItem<Item> TUNING_CHIP = ITEMS.registerSimpleItem("tuning_chip");
    public static final DeferredItem<TerrainProgramItem> BLANK = program("blank", TerrainProgram.BLANK);
    public static final DeferredItem<TerrainProgramItem> FROST = program("frost", TerrainProgram.FROST);
    public static final DeferredItem<TerrainProgramItem> PLANTING = program("planting", TerrainProgram.PLANTING);
    public static final DeferredItem<TerrainProgramItem> DRYING = program("drying", TerrainProgram.DRYING);
    public static final DeferredItem<TerrainProgramItem> LEVELING = program("leveling", TerrainProgram.LEVELING);
    public static final DeferredItem<TerrainProgramItem> WATERING = program("watering", TerrainProgram.WATERING);
    public static final DeferredItem<TerrainProgramItem> FUNGI = program("fungi", TerrainProgram.FUNGI);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GatheringBlockEntity>> MACHINES = ENTITIES.register("gathering_station",
            () -> new BlockEntityType<>(GatheringBlockEntity::new, ICBlocks.MINER.get(), ICBlocks.ADVANCED_MINER.get(), ICBlocks.PUMP.get(), TERRAIN.get()));
    public static final DeferredHolder<MenuType<?>, MenuType<GatheringMenu>> MENU = MENUS.register("gathering_station", () -> IMenuTypeExtension.create(GatheringMenu::new));
    private static DeferredItem<TerrainProgramItem> program(String name, TerrainProgram program) {
        return ITEMS.registerItem("terrain_" + name, properties -> new TerrainProgramItem(properties, program));
    }
    private static BlockBehaviour.Properties machineProperties() { return BlockBehaviour.Properties.of().strength(2, 6).sound(SoundType.METAL).requiresCorrectToolForDrops(); }
    public static void register(IEventBus bus) {
        BLOCKS.register(bus); ITEMS.register(bus); ENTITIES.register(bus); MENUS.register(bus);
        bus.addListener(GatheringContent::capabilities);
    }
    private static void capabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(IcCapabilities.EU_SINK, MACHINES.get(), (machine, side) -> machine);
        event.registerBlockEntity(Capabilities.Item.BLOCK, MACHINES.get(), WorldlyContainerWrapper::new);
        event.registerBlockEntity(Capabilities.Fluid.BLOCK, MACHINES.get(), (machine, side) -> machine.pump() ? machine.fluidOutput() : null);
    }
    private GatheringContent() {}
}
