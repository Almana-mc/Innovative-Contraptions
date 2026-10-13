package me.almana.innovativecontraptions.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;

public final class FluidSprites {
    public static TextureAtlasSprite sprite(Fluid fluid) {
        var texture = IClientFluidTypeExtensions.of(fluid).getStillTexture();
        return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(texture == null ? MissingTextureAtlasSprite.getLocation() : texture);
    }

    public static int tint(Fluid fluid) {
        return IClientFluidTypeExtensions.of(fluid).getTintColor();
    }

    private FluidSprites() {}
}
