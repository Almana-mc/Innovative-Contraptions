package me.almana.innovativecontraptions.registry;

import me.almana.innovativecontraptions.InnovativeContraptions;
import me.almana.innovativecontraptions.content.machine.recipe.MaceratingRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ICRecipes {
    private static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(
            BuiltInRegistries.RECIPE_TYPE, InnovativeContraptions.MODID);
    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(
            BuiltInRegistries.RECIPE_SERIALIZER, InnovativeContraptions.MODID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<MaceratingRecipe>> MACERATING_TYPE =
            TYPES.register("macerating", () -> new RecipeType<>() {
            });
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<MaceratingRecipe>> MACERATING_SERIALIZER =
            SERIALIZERS.register("macerating", () -> new RecipeSerializer<>(
                    MaceratingRecipe.MAP_CODEC, MaceratingRecipe.STREAM_CODEC));

    public static void register(IEventBus modBus) {
        TYPES.register(modBus);
        SERIALIZERS.register(modBus);
    }

    private ICRecipes() {
    }
}
