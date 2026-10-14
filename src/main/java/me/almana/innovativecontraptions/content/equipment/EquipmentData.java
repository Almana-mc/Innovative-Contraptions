package me.almana.innovativecontraptions.content.equipment;

import com.mojang.serialization.Codec;
import me.almana.innovativecontraptions.InnovativeContraptions;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class EquipmentData {
    public static final DeferredRegister<DataComponentType<?>> TYPES = DeferredRegister.create(
            BuiltInRegistries.DATA_COMPONENT_TYPE, InnovativeContraptions.MODID);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> ACTIVE = flag("equipment_active");
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> HOVER = flag("equipment_hover");
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> MODE = number("equipment_mode");
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> FUEL = number("equipment_fuel");
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> FOAM = number("foam_amount");
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> AIR = number("remaining_air");
    //? if <26.1
    //public static final DeferredHolder<DataComponentType<?>, DataComponentType<Gear>> WORN = TYPES.register("worn_gear", () -> DataComponentType.<Gear>builder().persistent(Gear.CODEC).build());
    private static DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> flag(String name) {
        return TYPES.register(name, () -> DataComponentType.<Boolean>builder().persistent(Codec.BOOL)
                .networkSynchronized(ByteBufCodecs.BOOL).build());
    }
    private static DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> number(String name) {
        return TYPES.register(name, () -> DataComponentType.<Integer>builder().persistent(Codec.INT)
                .networkSynchronized(ByteBufCodecs.VAR_INT).build());
    }
}
