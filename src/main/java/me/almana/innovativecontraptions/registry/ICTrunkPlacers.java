package me.almana.innovativecontraptions.registry;

import me.almana.innovativecontraptions.InnovativeContraptions;
import me.almana.innovativecontraptions.world.RubberTrunkPlacer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ICTrunkPlacers {
    public static final DeferredRegister<TrunkPlacerType<?>> TRUNK_PLACERS =
            DeferredRegister.create(BuiltInRegistries.TRUNK_PLACER_TYPE, InnovativeContraptions.MODID);

    public static final DeferredHolder<TrunkPlacerType<?>, TrunkPlacerType<RubberTrunkPlacer>> RUBBER =
            TRUNK_PLACERS.register("rubber_trunk_placer", () -> new TrunkPlacerType<>(RubberTrunkPlacer.CODEC));

    private ICTrunkPlacers() {
    }
}
