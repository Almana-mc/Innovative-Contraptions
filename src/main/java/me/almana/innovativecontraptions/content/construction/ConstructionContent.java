package me.almana.innovativecontraptions.content.construction;

import me.almana.innovativecontraptions.InnovativeContraptions;
import me.almana.innovativecontraptions.content.agriculture.WoodenScaffoldBlock;
import me.almana.innovativecontraptions.content.equipment.FoamPackItem;
import me.almana.innovativecontraptions.content.fluid.FluidCellContents;
import me.almana.innovativecontraptions.content.fluid.FluidCellItem;
import me.almana.innovativecontraptions.registry.ICBlocks;
import me.almana.innovativecontraptions.registry.ICItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class ConstructionContent {
    private static final DeferredRegister<FluidType> TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, InnovativeContraptions.MODID);
    private static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(BuiltInRegistries.FLUID, InnovativeContraptions.MODID);
    public static final DeferredHolder<FluidType, FluidType> FOAM_TYPE = TYPES.register("construction_foam",
            () -> new FluidType(FluidType.Properties.create().density(1500).viscosity(4000)));
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> FOAM_FLUID = FLUIDS.register("construction_foam",
            () -> new BaseFlowingFluid.Source(new BaseFlowingFluid.Properties(FOAM_TYPE, ConstructionContent.FOAM_FLUID, ConstructionContent.FLOWING_FOAM)));
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> FLOWING_FOAM = FLUIDS.register("flowing_construction_foam",
            () -> new BaseFlowingFluid.Flowing(new BaseFlowingFluid.Properties(FOAM_TYPE, FOAM_FLUID, ConstructionContent.FLOWING_FOAM)));

    public static final DeferredBlock<FoamBlock> FOAM = ICBlocks.BLOCKS.registerBlock("construction_foam", FoamBlock::new,
            () -> BlockBehaviour.Properties.of().strength(0.01F, 6F).noCollision().noOcclusion().randomTicks().sound(SoundType.WOOL));
    public static final DeferredBlock<Block> FOAM_WALL = ICBlocks.BLOCKS.registerBlock("construction_foam_wall", Block::new,
            () -> BlockBehaviour.Properties.of().strength(3F, 18F).requiresCorrectToolForDrops());
    public static final DeferredBlock<WoodenScaffoldBlock> IRON_SCAFFOLD = ICBlocks.BLOCKS.registerBlock("iron_scaffold", WoodenScaffoldBlock::new,
            () -> BlockBehaviour.Properties.of().strength(0.8F, 3.6F).noOcclusion().sound(SoundType.METAL));

    public static final DeferredItem<BlockItem> FOAM_WALL_ITEM = ICItems.ITEMS.registerSimpleBlockItem(FOAM_WALL);
    public static final DeferredItem<BlockItem> IRON_SCAFFOLD_ITEM = ICItems.ITEMS.registerSimpleBlockItem(IRON_SCAFFOLD);
    public static final DeferredItem<Item> CF_POWDER = ICItems.ITEMS.registerSimpleItem("cf_powder");
    public static final DeferredItem<Item> FOAM_CELL = ICItems.ITEMS.registerItem("construction_foam_cell", props -> new FluidCellItem(props, FOAM_FLUID.get()));
    public static final DeferredItem<FoamSprayerItem> FOAM_SPRAYER = ICItems.ITEMS.registerItem("foam_sprayer", FoamSprayerItem::new);

    public static void register(IEventBus bus) {
        TYPES.register(bus);
        FLUIDS.register(bus);
        bus.addListener(ConstructionContent::capabilities);
    }

    private static void capabilities(RegisterCapabilitiesEvent event) {
        event.registerItem(Capabilities.Fluid.ITEM, (stack, access) -> new FluidCellContents(access), FOAM_CELL.get());
        event.registerItem(Capabilities.Fluid.ITEM, (stack, access) -> new FoamTankContents(access, FoamSprayerItem.CAPACITY), FOAM_SPRAYER.get());
        event.registerItem(Capabilities.Fluid.ITEM, (stack, access) -> new FoamTankContents(access, FoamPackItem.CAPACITY), ICItems.CF_PACK.get());
    }

    private ConstructionContent() {}
}
