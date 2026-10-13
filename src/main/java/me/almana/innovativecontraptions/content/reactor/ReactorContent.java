package me.almana.innovativecontraptions.content.reactor;

import javax.annotation.Nullable;
import me.almana.innovativecontraptions.InnovativeContraptions;
import me.almana.innovativecontraptions.registry.ICBlocks;
import me.almana.innovativecontraptions.registry.IcCapabilities;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.transfer.item.WorldlyContainerWrapper;

public final class ReactorContent {
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, InnovativeContraptions.MODID);
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(BuiltInRegistries.MENU, InnovativeContraptions.MODID);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<NuclearReactorBlockEntity>> REACTOR = ENTITIES.register("nuclear_reactor",
            () -> new BlockEntityType<>(NuclearReactorBlockEntity::new, ICBlocks.NUCLEAR_REACTOR.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ReactorFluidPortBlockEntity>> FLUID_PORT = ENTITIES.register("reactor_fluid_port",
            () -> new BlockEntityType<>(ReactorFluidPortBlockEntity::new, ICBlocks.REACTOR_FLUID_PORT.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CoolantInjectorBlockEntity>> INJECTOR = ENTITIES.register("coolant_injector",
            () -> new BlockEntityType<>(CoolantInjectorBlockEntity::new, ICBlocks.RSH_COOLANT_INJECTOR.get(), ICBlocks.LZH_COOLANT_INJECTOR.get()));

    public static final DeferredHolder<MenuType<?>, MenuType<ReactorMenu>> MENU = MENUS.register("nuclear_reactor",
            () -> IMenuTypeExtension.create(ReactorMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<CoolantInjectorMenu>> INJECTOR_MENU = MENUS.register("coolant_injector",
            () -> IMenuTypeExtension.create(CoolantInjectorMenu::new));

    public static void register(IEventBus bus) {
        ENTITIES.register(bus);
        MENUS.register(bus);
        bus.addListener(ReactorContent::capabilities);
    }

    private static void capabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(IcCapabilities.EU_SOURCE, REACTOR.get(), (reactor, side) -> reactor);
        event.registerBlockEntity(Capabilities.Item.BLOCK, REACTOR.get(), WorldlyContainerWrapper::new);
        event.registerBlockEntity(Capabilities.Fluid.BLOCK, REACTOR.get(), (reactor, side) -> reactor.fluidMode() ? reactor.fluids() : null);
        event.registerBlockEntity(Capabilities.Fluid.BLOCK, FLUID_PORT.get(), (port, side) -> port.reactorFluids());
        event.registerBlockEntity(IcCapabilities.EU_SINK, INJECTOR.get(),
                (injector, side) -> side == injector.getBlockState().getValue(BlockStateProperties.FACING).getOpposite() ? null : injector);
        event.registerBlockEntity(Capabilities.Item.BLOCK, INJECTOR.get(), WorldlyContainerWrapper::new);
        event.registerBlock(IcCapabilities.EU_SOURCE, (level, pos, state, entity, side) -> ReactorStructure.adjacent(level, pos),
                ICBlocks.REACTOR_CHAMBER.get());
        event.registerBlock(Capabilities.Item.BLOCK, (level, pos, state, entity, side) -> items(ReactorStructure.adjacent(level, pos), side),
                ICBlocks.REACTOR_CHAMBER.get());
        event.registerBlock(Capabilities.Item.BLOCK, (level, pos, state, entity, side) -> items(ReactorStructure.enclosing(level, pos), side),
                ICBlocks.REACTOR_ACCESS_HATCH.get());
    }

    @Nullable
    private static WorldlyContainerWrapper items(@Nullable NuclearReactorBlockEntity reactor, @Nullable Direction side) {
        return reactor == null ? null : new WorldlyContainerWrapper(reactor, side);
    }

    private ReactorContent() {}
}
