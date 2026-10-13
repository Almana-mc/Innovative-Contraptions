package me.almana.innovativecontraptions.content.generator;

import me.almana.innovativecontraptions.InnovativeContraptions;
import me.almana.innovativecontraptions.registry.ICBlocks;
import me.almana.innovativecontraptions.registry.ICBlockEntities;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.transfer.item.VanillaContainerWrapper;

public final class KineticContent {
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, InnovativeContraptions.MODID);
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(BuiltInRegistries.MENU, InnovativeContraptions.MODID);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<KineticWindTurbineBlockEntity>> TURBINE = ENTITIES.register(
            "kinetic_wind_turbine", () -> new BlockEntityType<>(KineticWindTurbineBlockEntity::new, ICBlocks.KINETIC_WIND_TURBINE.get()));
    public static final DeferredHolder<MenuType<?>, MenuType<GeneratorMenu>> MENU = MENUS.register(
            "kinetic_wind_turbine", () -> IMenuTypeExtension.create(GeneratorMenu::new));

    public static void register(IEventBus bus) {
        ENTITIES.register(bus);
        MENUS.register(bus);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.Item.BLOCK, TURBINE.get(), (entity, side) -> VanillaContainerWrapper.of(entity));
        event.registerBlockEntity(Capabilities.Item.BLOCK, ICBlockEntities.GENERATOR.get(), (entity, side) -> VanillaContainerWrapper.of(entity));
        event.registerBlockEntity(Capabilities.Item.BLOCK, ICBlockEntities.WATER_MILL.get(), (entity, side) -> VanillaContainerWrapper.of(entity));
        event.registerBlockEntity(Capabilities.Item.BLOCK, ICBlockEntities.GEOTHERMAL_GENERATOR.get(), (entity, side) -> VanillaContainerWrapper.of(entity));
        event.registerBlockEntity(Capabilities.Item.BLOCK, ICBlockEntities.WINDMILL.get(), (entity, side) -> VanillaContainerWrapper.of(entity));
        event.registerBlockEntity(Capabilities.Item.BLOCK, ICBlockEntities.SOLAR_PANEL.get(), (entity, side) -> VanillaContainerWrapper.of(entity));
        event.registerBlockEntity(Capabilities.Item.BLOCK, ICBlockEntities.KINETIC_GENERATOR.get(), (entity, side) -> VanillaContainerWrapper.of(entity));
        event.registerBlockEntity(Capabilities.Fluid.BLOCK, ICBlockEntities.GEOTHERMAL_GENERATOR.get(), (entity, side) -> entity.lavaTank());
    }

    private KineticContent() {}
}
