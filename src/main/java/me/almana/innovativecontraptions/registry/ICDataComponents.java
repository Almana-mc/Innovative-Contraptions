package me.almana.innovativecontraptions.registry;

import com.mojang.serialization.Codec;
import me.almana.innovativecontraptions.InnovativeContraptions;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ICDataComponents {
    public static final DeferredRegister<DataComponentType<?>> COMPONENTS =
            DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, InnovativeContraptions.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ENERGY =
            COMPONENTS.register("energy", () -> DataComponentType.<Integer>builder()
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.VAR_INT)
                    .build());

    private ICDataComponents() {
    }
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> UPGRADE_SIDE =
            COMPONENTS.register("upgrade_side", () -> DataComponentType.<Integer>builder()
                    .persistent(Codec.intRange(-1, 5)).networkSynchronized(ByteBufCodecs.VAR_INT).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> FLUID_AMOUNT =
            COMPONENTS.register("fluid_amount", () -> DataComponentType.<Integer>builder()
                    .persistent(Codec.intRange(0, 1000)).networkSynchronized(ByteBufCodecs.VAR_INT).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> REACTOR_DAMAGE =
            COMPONENTS.register("reactor_damage", () -> DataComponentType.<Integer>builder()
                    .persistent(Codec.intRange(0, Integer.MAX_VALUE)).networkSynchronized(ByteBufCodecs.VAR_INT).build());
}
