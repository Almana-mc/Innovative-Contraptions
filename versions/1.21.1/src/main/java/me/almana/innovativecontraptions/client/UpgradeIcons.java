package me.almana.innovativecontraptions.client;

import java.util.List;
import me.almana.innovativecontraptions.InnovativeContraptions;
import me.almana.innovativecontraptions.registry.ICDataComponents;
import me.almana.innovativecontraptions.registry.ICItems;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

public final class UpgradeIcons {
    public static void setup(FMLClientSetupEvent event) {
        ResourceLocation side = ResourceLocation.fromNamespaceAndPath(InnovativeContraptions.MODID, "upgrade_side");
        event.enqueueWork(() -> List.of(ICItems.EJECTOR_UPGRADE, ICItems.PULLING_UPGRADE, ICItems.FLUID_EJECTOR_UPGRADE, ICItems.FLUID_PULLING_UPGRADE)
                .forEach(item -> ItemProperties.register(item.get(), side,
                        (stack, level, entity, seed) -> (stack.getOrDefault(ICDataComponents.UPGRADE_SIDE.get(), -1) + 1) / 6.0F)));
    }

    private UpgradeIcons() {}
}
