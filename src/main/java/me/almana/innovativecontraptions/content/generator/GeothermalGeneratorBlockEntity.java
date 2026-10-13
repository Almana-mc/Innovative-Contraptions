package me.almana.innovativecontraptions.content.generator;

import me.almana.innovativecontraptions.registry.ICBlockEntities;
import me.almana.innovativecontraptions.registry.ICDataComponents;
import me.almana.innovativecontraptions.registry.ICItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;

public class GeothermalGeneratorBlockEntity extends GeneratorBlockEntityBase {
    private int output;
    private int shownLava;
    private final FluidStacksResourceHandler lava = new FluidStacksResourceHandler(1, GeneratorRates.GEOTHERMAL_TANK_CAPACITY) {
        @Override public boolean isValid(int index, FluidResource resource) { return resource.is(Fluids.LAVA); }
        @Override protected void onContentsChanged(int index, FluidStack previous) { setChanged(); }
    };

    public GeothermalGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ICBlockEntities.GEOTHERMAL_GENERATOR.get(), pos, state, GeneratorRates.GEOTHERMAL_BUFFER, 3);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, GeothermalGeneratorBlockEntity entity) {
        entity.chargeBattery();
        entity.fillFromItem();
        int consumed = Math.min(entity.lava.getAmountAsInt(0),
                Math.min(GeneratorRates.GEOTHERMAL_EU_PER_TICK, entity.bufferMax - entity.euBuffer) / GeneratorRates.GEOTHERMAL_EU_PER_MB);
        entity.output = consumed * GeneratorRates.GEOTHERMAL_EU_PER_MB;
        if (consumed > 0) {
            entity.lava.set(0, FluidResource.of(Fluids.LAVA), entity.lava.getAmountAsInt(0) - consumed);
            entity.euBuffer += entity.output;
            entity.setChanged();
        }
    }

    private void fillFromItem() {
        ItemStack fuel = getItem(1);
        ItemStack returned;
        if (fuel.is(Items.LAVA_BUCKET)) returned = new ItemStack(Items.BUCKET);
        else if (fuel.is(ICItems.LAVA_CELL.get())) returned = new ItemStack(ICItems.EMPTY_CELL.get());
        else return;
        int amount = fuel.getOrDefault(ICDataComponents.FLUID_AMOUNT.get(), 1000);
        if (lava.getAmountAsInt(0) > GeneratorRates.GEOTHERMAL_TANK_CAPACITY - amount) return;
        ItemStack result = getItem(2);
        if (!result.isEmpty() && (!ItemStack.isSameItemSameComponents(result, returned) || result.getCount() >= result.getMaxStackSize())) return;
        fuel.shrink(1);
        if (result.isEmpty()) setItem(2, returned);
        else result.grow(1);
        lava.set(0, FluidResource.of(Fluids.LAVA), lava.getAmountAsInt(0) + amount);
        setChanged();
    }

    public FluidStacksResourceHandler lavaTank() { return lava; }
    @Override protected float currentOutput() { return output; }
    @Override protected int burnTicks() { return level != null && level.isClientSide() ? shownLava : lava.getAmountAsInt(0); }
    @Override protected int burnTicksMax() { return GeneratorRates.GEOTHERMAL_TANK_CAPACITY; }
    @Override protected void setBurnTicks(int value) { shownLava = value; }
    @Override public boolean canPlaceItem(int slot, ItemStack stack) {
        return slot == 0 ? super.canPlaceItem(slot, stack) : slot == 1 && (stack.is(Items.LAVA_BUCKET) || stack.is(ICItems.LAVA_CELL.get()));
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        lava.serialize(output.child("lava_tank"));
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        lava.deserialize(input.childOrEmpty("lava_tank"));
    }
}
