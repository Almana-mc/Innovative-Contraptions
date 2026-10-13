package me.almana.innovativecontraptions.content.machine;

import me.almana.innovativecontraptions.InnovativeContraptions;
import me.almana.innovativecontraptions.registry.ICBlocks;
import me.almana.innovativecontraptions.registry.IcCapabilities;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class PoweredLightContent {
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES = DeferredRegister.create(
            BuiltInRegistries.BLOCK_ENTITY_TYPE, InnovativeContraptions.MODID);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PoweredLightBlockEntity>> ENTITY =
            ENTITIES.register("powered_light", () -> new BlockEntityType<>(PoweredLightBlockEntity::new, ICBlocks.LUMINATOR.get()));
    public static void register(IEventBus bus) { ENTITIES.register(bus); bus.addListener(PoweredLightContent::capabilities); }
    private static void capabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(IcCapabilities.EU_SINK, ENTITY.get(), (light, side) -> light.canConnect(side) ? light : null);
    }
    private PoweredLightContent() {}
}
