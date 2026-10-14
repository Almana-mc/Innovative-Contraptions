package me.almana.innovativecontraptions.content.generator;

import me.almana.innovativecontraptions.registry.ICBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
//? if <26.1
//import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class GeneratorBlockEntity extends GeneratorBlockEntityBase {
    private static final int BUFFER_MAX = 4000;
    private static final int FUEL_SLOT = 1;

    private int fuelTicks = 0;
    private int fuelTicksMax = 0;
    private int output;

    public GeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ICBlockEntities.GENERATOR.get(), pos, state, BUFFER_MAX, 2);
    }

    @SuppressWarnings("deprecation")
    public static void serverTick(Level level, BlockPos pos, BlockState state, GeneratorBlockEntity entity) {
        entity.chargeBattery();
        entity.output = 0;
        if (entity.euBuffer > BUFFER_MAX - GeneratorRates.FUEL_EU_PER_TICK) return;
        ItemStack fuel = entity.items.get(FUEL_SLOT);

        if (entity.fuelTicks > 0) {
            entity.output = GeneratorRates.FUEL_EU_PER_TICK;
            entity.fuelTicks--;
            entity.euBuffer = Math.min(entity.euBuffer + GeneratorRates.FUEL_EU_PER_TICK, BUFFER_MAX);
            entity.setChanged();
        } else if (!fuel.isEmpty()) {
            //? if >=26.1 {
            int burnTime = level.fuelValues().burnDuration(fuel);
            //?} else {
            /*int burnTime = fuel.getBurnTime(RecipeType.SMELTING);
            *///?}
            if (burnTime > 0) {
                entity.output = GeneratorRates.FUEL_EU_PER_TICK;
                //? if >=26.1 {
                ItemStackTemplate remainder = fuel.getCraftingRemainder();
                //?} else {
                /*ItemStack leftover = fuel.getCraftingRemainingItem();
                ItemStackTemplate remainder = leftover.isEmpty() ? null : ItemStackTemplate.fromNonEmptyStack(leftover);
                *///?}
                fuel.shrink(1);
                if (fuel.isEmpty() && remainder != null) {
                    entity.items.set(FUEL_SLOT, remainder.create());
                }
                entity.fuelTicks = burnTime - 1;
                entity.fuelTicksMax = burnTime;
                entity.euBuffer = Math.min(entity.euBuffer + GeneratorRates.FUEL_EU_PER_TICK, BUFFER_MAX);
                entity.setChanged();
            }
        }

    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return slot == 0 ? super.canPlaceItem(slot, stack)
                //? if >=26.1 {
                : slot == FUEL_SLOT && level.fuelValues().burnDuration(stack) > 0;
                //?} else {
                /*: slot == FUEL_SLOT && stack.getBurnTime(RecipeType.SMELTING) > 0;
                *///?}
    }

    @Override
    protected float currentOutput() { return output; }

    @Override
    protected int burnTicks() { return fuelTicks; }

    @Override
    protected int burnTicksMax() { return fuelTicksMax; }

    @Override
    protected void setBurnTicks(int value) { fuelTicks = value; }

    @Override
    protected void setBurnTicksMax(int value) { fuelTicksMax = value; }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("fuel_ticks", fuelTicks);
        output.putInt("fuel_ticks_max", fuelTicksMax);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        fuelTicks = input.getIntOr("fuel_ticks", 0);
        fuelTicksMax = input.getIntOr("fuel_ticks_max", 0);
    }
}
