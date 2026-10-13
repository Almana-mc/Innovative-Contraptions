package me.almana.innovativecontraptions.compat;

import me.almana.innovativecontraptions.InnovativeContraptions;
import me.almana.innovativecontraptions.compat.transfer.ResourceHandler;
import me.almana.innovativecontraptions.compat.transfer.access.ItemAccess;
import me.almana.innovativecontraptions.compat.transfer.fluid.FluidResource;
import me.almana.innovativecontraptions.compat.transfer.item.ItemResource;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.ItemCapability;
import org.jetbrains.annotations.Nullable;

public final class Capabilities {
    public static final class Fluid {
        public static final BlockCapability<ResourceHandler<FluidResource>, @Nullable Direction> BLOCK = BlockCapability.createSided(create("fluid_handler"), ResourceHandler.asClass());
        public static final ItemCapability<ResourceHandler<FluidResource>, ItemAccess> ITEM = ItemCapability.create(create("fluid_handler"), ResourceHandler.asClass(), ItemAccess.class);

        private Fluid() {}
    }

    public static final class Item {
        public static final BlockCapability<ResourceHandler<ItemResource>, @Nullable Direction> BLOCK = BlockCapability.createSided(create("item_handler"), ResourceHandler.asClass());

        private Item() {}
    }

    private static ResourceLocation create(String path) {
        return ResourceLocation.fromNamespaceAndPath(InnovativeContraptions.MODID, path);
    }

    private Capabilities() {}
}
