package me.almana.innovativecontraptions.client.integration.jei;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record GeneratorJeiRecipe(
        Identifier id,
        List<ItemStack> inputs,
        List<ItemStack> conditions,
        List<ItemStack> outputs,
        List<Component> details
) {
}
