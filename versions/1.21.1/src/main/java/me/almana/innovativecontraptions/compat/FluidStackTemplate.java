package me.almana.innovativecontraptions.compat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;

public record FluidStackTemplate(Holder<Fluid> fluid, int amount, DataComponentPatch components) {
    private static final Codec<FluidStackTemplate> FULL_CODEC = RecordCodecBuilder.create(i -> i.group(
            FluidStack.FLUID_NON_EMPTY_CODEC.fieldOf("id").forGetter(FluidStackTemplate::fluid),
            ExtraCodecs.POSITIVE_INT.fieldOf("amount").forGetter(FluidStackTemplate::amount),
            DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY).forGetter(FluidStackTemplate::components)
    ).apply(i, FluidStackTemplate::new));
    public static final Codec<FluidStackTemplate> CODEC = Codec.withAlternative(FULL_CODEC, FluidStack.FLUID_NON_EMPTY_CODEC,
            fluid -> new FluidStackTemplate(fluid, FluidType.BUCKET_VOLUME));

    public FluidStackTemplate {
        if (fluid.is(Fluids.EMPTY.builtInRegistryHolder()) || amount <= 0) throw new IllegalStateException("Fluid must be non-empty");
    }

    public FluidStackTemplate(Holder<Fluid> fluid, int amount) {
        this(fluid, amount, DataComponentPatch.EMPTY);
    }

    public FluidStack create() {
        return new FluidStack(fluid, amount, components);
    }
}
