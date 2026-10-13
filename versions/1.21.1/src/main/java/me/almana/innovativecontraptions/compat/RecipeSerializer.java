package me.almana.innovativecontraptions.compat;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Recipe;

public record RecipeSerializer<T extends Recipe<?>>(MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec)
        implements net.minecraft.world.item.crafting.RecipeSerializer<T> {
}
