package me.almana.innovativecontraptions.content.agriculture;

import me.almana.innovativecontraptions.InnovativeContraptions;
import me.almana.innovativecontraptions.registry.ICBlocks;
import me.almana.innovativecontraptions.registry.ICDataComponents;
import me.almana.innovativecontraptions.registry.ICItems;
import me.almana.innovativecontraptions.registry.IcCapabilities;
import me.almana.innovativecontraptions.content.fluid.FluidCellContents;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.transfer.item.WorldlyContainerWrapper;

public final class FarmingContent {
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, InnovativeContraptions.MODID);
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(BuiltInRegistries.MENU, InnovativeContraptions.MODID);
    private static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, InnovativeContraptions.MODID);
    private static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(BuiltInRegistries.FLUID, InnovativeContraptions.MODID);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CropGenome>> GENOME = ICDataComponents.COMPONENTS.register("crop_genome",
            () -> DataComponentType.<CropGenome>builder().persistent(CropGenome.CODEC).networkSynchronized(CropGenome.STREAM_CODEC).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<DrinkBlend>> BLEND = ICDataComponents.COMPONENTS.register("drink_blend",
            () -> DataComponentType.<DrinkBlend>builder().persistent(DrinkBlend.CODEC).networkSynchronized(DrinkBlend.STREAM_CODEC).build());
    public static final DeferredHolder<FluidType, FluidType> TREATMENT_TYPE = FLUID_TYPES.register("crop_treatment", () -> new FluidType(FluidType.Properties.create()));
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> TREATMENT = FLUIDS.register("crop_treatment", () -> new BaseFlowingFluid.Source(fluidProperties()));
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> FLOWING_TREATMENT = FLUIDS.register("flowing_crop_treatment", () -> new BaseFlowingFluid.Flowing(fluidProperties()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CropPlotBlockEntity>> CROP = ENTITIES.register("crop_plot",
            () -> new BlockEntityType<>(CropPlotBlockEntity::new, ICBlocks.CROP_STICKS.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FarmingMachineBlockEntity>> MACHINE = ENTITIES.register("farming_machine",
            () -> new BlockEntityType<>(FarmingMachineBlockEntity::new, ICBlocks.CROP_MATRON.get(), ICBlocks.CROP_HARVESTER.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FermentingBarrelBlockEntity>> BARREL = ENTITIES.register("fermenting_barrel",
            () -> new BlockEntityType<>(FermentingBarrelBlockEntity::new, ICBlocks.FERMENTING_BARREL.get()));
    public static final DeferredHolder<MenuType<?>, MenuType<FarmingMenu>> MENU = MENUS.register("farming_machine", () -> IMenuTypeExtension.create(FarmingMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<AnalyzerMenu>> ANALYZER = MENUS.register("crop_analyzer", () -> IMenuTypeExtension.create(AnalyzerMenu::new));
    private static BaseFlowingFluid.Properties fluidProperties() { return new BaseFlowingFluid.Properties(TREATMENT_TYPE, TREATMENT, FLOWING_TREATMENT); }
    public static void register(IEventBus bus) {
        ENTITIES.register(bus); MENUS.register(bus); FLUID_TYPES.register(bus); FLUIDS.register(bus);
        bus.addListener(FarmingContent::capabilities);
        NeoForge.EVENT_BUS.addListener(CropInteractions::pick);
        NeoForge.EVENT_BUS.addListener(BarrelInteractions::leftClick);
    }
    private static void capabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(IcCapabilities.EU_SINK, MACHINE.get(), (entity, side) -> entity);
        event.registerBlockEntity(Capabilities.Item.BLOCK, MACHINE.get(), WorldlyContainerWrapper::new);
        event.registerBlockEntity(Capabilities.Fluid.BLOCK, MACHINE.get(), (entity, side) -> entity.matron() ? entity.tanks() : null);
        event.registerItem(Capabilities.Fluid.ITEM, (stack, access) -> new FluidCellContents(access), ICItems.CROP_TREATMENT_CELL.get());
    }
    private FarmingContent() {}
}
