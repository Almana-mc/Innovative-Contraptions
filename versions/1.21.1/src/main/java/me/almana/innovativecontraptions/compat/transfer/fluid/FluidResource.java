package me.almana.innovativecontraptions.compat.transfer.fluid;

import me.almana.innovativecontraptions.compat.transfer.TransferPreconditions;
import me.almana.innovativecontraptions.compat.transfer.resource.Resource;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;

public final class FluidResource implements Resource {
    public static final FluidResource EMPTY = new FluidResource(FluidStack.EMPTY);

    public static FluidResource of(FluidStack stack) {
        if (stack.isEmpty() || stack.isComponentsPatchEmpty()) {
            return of(stack.getFluid());
        }
        return new FluidResource(stack.copyWithAmount(FluidType.BUCKET_VOLUME));
    }

    public static FluidResource of(Fluid fluid) {
        if (fluid == Fluids.EMPTY) return EMPTY;
        return new FluidResource(new FluidStack(fluid, FluidType.BUCKET_VOLUME));
    }

    private final FluidStack innerStack;

    private FluidResource(FluidStack stack) {
        this.innerStack = stack;
    }

    public Fluid getFluid() {
        return innerStack.getFluid();
    }

    public FluidType getFluidType() {
        return innerStack.getFluidType();
    }

    @Override
    public boolean isEmpty() {
        return innerStack.isEmpty();
    }

    public boolean is(Fluid fluid) {
        return getFluid() == fluid;
    }

    public FluidStack toStack(int amount) {
        TransferPreconditions.checkNonNegative(amount);
        if (amount == 0) return FluidStack.EMPTY;
        return innerStack.copyWithAmount(amount);
    }

    public boolean matches(FluidStack stack) {
        return FluidStack.isSameFluidSameComponents(stack, innerStack);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        return FluidStack.isSameFluidSameComponents(innerStack, ((FluidResource) obj).innerStack);
    }

    @Override
    public int hashCode() {
        return FluidStack.hashFluidAndComponents(innerStack);
    }

    @Override
    public String toString() {
        return getFluidType() + " [" + innerStack.getComponentsPatch().size() + "]";
    }
}
