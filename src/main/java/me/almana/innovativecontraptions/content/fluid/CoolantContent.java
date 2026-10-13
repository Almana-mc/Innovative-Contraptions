package me.almana.innovativecontraptions.content.fluid;

import me.almana.innovativecontraptions.InnovativeContraptions;
import me.almana.innovativecontraptions.registry.ICItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class CoolantContent {
    private static final DeferredRegister<FluidType> TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, InnovativeContraptions.MODID);
    private static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(BuiltInRegistries.FLUID, InnovativeContraptions.MODID);
    public static final DeferredHolder<FluidType, FluidType> COOLANT_TYPE = TYPES.register("coolant",
            () -> new FluidType(FluidType.Properties.create().density(1000).viscosity(3000).temperature(300)));
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> COOLANT = FLUIDS.register("coolant",
            () -> new BaseFlowingFluid.Source(new BaseFlowingFluid.Properties(COOLANT_TYPE, CoolantContent.COOLANT, CoolantContent.FLOWING_COOLANT)));
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> FLOWING_COOLANT = FLUIDS.register("flowing_coolant",
            () -> new BaseFlowingFluid.Flowing(new BaseFlowingFluid.Properties(COOLANT_TYPE, COOLANT, CoolantContent.FLOWING_COOLANT)));
    public static final DeferredHolder<FluidType, FluidType> HOT_COOLANT_TYPE = TYPES.register("hot_coolant",
            () -> new FluidType(FluidType.Properties.create().density(1000).viscosity(3000).temperature(1200)));
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> HOT_COOLANT = FLUIDS.register("hot_coolant",
            () -> new BaseFlowingFluid.Source(new BaseFlowingFluid.Properties(HOT_COOLANT_TYPE, CoolantContent.HOT_COOLANT, CoolantContent.FLOWING_HOT_COOLANT)));
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> FLOWING_HOT_COOLANT = FLUIDS.register("flowing_hot_coolant",
            () -> new BaseFlowingFluid.Flowing(new BaseFlowingFluid.Properties(HOT_COOLANT_TYPE, HOT_COOLANT, CoolantContent.FLOWING_HOT_COOLANT)));

    public static void register(IEventBus bus) {
        TYPES.register(bus);
        FLUIDS.register(bus);
        bus.addListener(CoolantContent::capabilities);
    }

    private static void capabilities(RegisterCapabilitiesEvent event) {
        event.registerItem(Capabilities.Fluid.ITEM, (stack, access) -> new FluidCellContents(access),
                ICItems.COOLANT_CELL.get(), ICItems.HOT_COOLANT_CELL.get());
    }

    private CoolantContent() {}
}
