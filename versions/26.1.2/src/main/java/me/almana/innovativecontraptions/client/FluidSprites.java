package me.almana.innovativecontraptions.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.level.material.Fluid;

public final class FluidSprites {
    public static TextureAtlasSprite sprite(Fluid fluid) {
        return model(fluid).stillMaterial().sprite();
    }

    public static int tint(Fluid fluid) {
        var source = model(fluid).fluidTintSource();
        return source == null ? -1 : 0xFF000000 | source.color(fluid.defaultFluidState());
    }

    private static FluidModel model(Fluid fluid) {
        return Minecraft.getInstance().getModelManager().getFluidStateModelSet().get(fluid.defaultFluidState());
    }

    private FluidSprites() {}
}
