package me.almana.innovativecontraptions.client.integration.jei;

import me.almana.innovativecontraptions.content.energy.storage.ElectricalBlock;
import me.almana.innovativecontraptions.registry.ICBlocks;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

@JeiPlugin
public final class ElectricalJeiPlugin implements IModPlugin {
    @Override public Identifier getPluginUid() { return Identifier.fromNamespaceAndPath("innovativecontraptions", "electrical"); }
    @Override public void registerRecipes(IRecipeRegistration registration) {
        registration.addIngredientInfo(ICBlocks.LUMINATOR.get(),
                Component.translatable("screen.innovativecontraptions.lamp.guide"));
        for (var registered : ICBlocks.BLOCKS.getEntries()) {
            if (!(registered.get() instanceof ElectricalBlock block)) continue;
            var kind = block.kind;
            String guide = kind.transformer ? "transformer" : kind.pad ? "pad" : "storage";
            registration.addIngredientInfo(block,
                    Component.translatable("screen.innovativecontraptions.electrical.capacity", kind.capacity),
                    kind.transformer ? Component.translatable("screen.innovativecontraptions.electrical.rate", kind.packet() * 4)
                            : Component.translatable("screen.innovativecontraptions.electrical.tier", kind.tier, kind.packet()),
                    Component.translatable("screen.innovativecontraptions." + guide + ".guide"));
        }
    }
}
