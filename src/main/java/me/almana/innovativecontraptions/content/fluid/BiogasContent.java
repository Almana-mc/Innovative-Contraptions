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

public final class BiogasContent {
    private static final DeferredRegister<FluidType> TYPES = DeferredRegister.create(
            NeoForgeRegistries.Keys.FLUID_TYPES, InnovativeContraptions.MODID);
    private static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(
            BuiltInRegistries.FLUID, InnovativeContraptions.MODID);
    public static final DeferredHolder<FluidType, FluidType> TYPE = TYPES.register("biogas",
            () -> new FluidType(FluidType.Properties.create().density(-100).viscosity(100)));
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> BIOGAS = FLUIDS.register("biogas",
            () -> new BaseFlowingFluid.Source(properties()));
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> FLOWING = FLUIDS.register("flowing_biogas",
            () -> new BaseFlowingFluid.Flowing(properties()));
    public static final DeferredHolder<FluidType, FluidType> BIOMASS_TYPE = TYPES.register("biomass",
            () -> new FluidType(FluidType.Properties.create().density(1100).viscosity(1500)));
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> BIOMASS = FLUIDS.register("biomass",
            () -> new BaseFlowingFluid.Source(new BaseFlowingFluid.Properties(BIOMASS_TYPE, BiogasContent.BIOMASS, BiogasContent.FLOWING_BIOMASS)));
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> FLOWING_BIOMASS = FLUIDS.register("flowing_biomass",
            () -> new BaseFlowingFluid.Flowing(new BaseFlowingFluid.Properties(BIOMASS_TYPE, BIOMASS, BiogasContent.FLOWING_BIOMASS)));

    private static BaseFlowingFluid.Properties properties() {
        return new BaseFlowingFluid.Properties(TYPE, BIOGAS, FLOWING);
    }

    public static void register(IEventBus bus) {
        TYPES.register(bus);
        FLUIDS.register(bus);
        bus.addListener(BiogasContent::capabilities);
    }

    private static void capabilities(RegisterCapabilitiesEvent event) {
        event.registerItem(Capabilities.Fluid.ITEM, (stack, access) -> new FuelPackContents(access), ICItems.JETPACK.get());
        event.registerItem(Capabilities.Fluid.ITEM, (stack, access) -> new FluidCellContents(access), ICItems.BIOGAS_CELL.get());
        event.registerItem(Capabilities.Fluid.ITEM, (stack, access) -> new FluidCellContents(access), ICItems.BIOMASS_CELL.get());
    }

    private BiogasContent() {}
}
