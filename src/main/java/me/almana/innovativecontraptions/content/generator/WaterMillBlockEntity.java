package me.almana.innovativecontraptions.content.generator;

import me.almana.innovativecontraptions.registry.ICBlockEntities;
import me.almana.innovativecontraptions.registry.ICDataComponents;
import me.almana.innovativecontraptions.registry.ICItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;

public class WaterMillBlockEntity extends GeneratorBlockEntityBase {
    private static final int WATER_SLOT = 1;

    private int waterBlocks;
    private int microEu;
    private int waterTicks;
    private int waterTicksMax;
    private int checkTimer;

    public WaterMillBlockEntity(BlockPos pos, BlockState state) {
        super(ICBlockEntities.WATER_MILL.get(), pos, state, WaterMillMath.BUFFER_MAX, 3);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, WaterMillBlockEntity entity) {
        if (entity.checkTimer == 0) {
            entity.waterBlocks = countNearbyWater(level, pos);
            entity.setChanged();
        }
        entity.checkTimer = (entity.checkTimer + 1) % GeneratorRates.WATER_SCAN_INTERVAL;

        if (entity.euBuffer <= WaterMillMath.BUFFER_MAX - GeneratorRates.WATER_BUCKET_EU_PER_TICK) {
            entity.startWaterFuel();
            if (entity.waterTicks > 0) {
                entity.waterTicks--;
                entity.addEu(GeneratorRates.WATER_BUCKET_EU_PER_TICK);
            }
            entity.addPassiveEu();
        }
        entity.chargeBattery();
    }

    public static int countNearbyWater(Level level, BlockPos pos) {
        Set<BlockPos> seen = new HashSet<>();
        ArrayDeque<BlockPos> water = new ArrayDeque<>();
        for (Direction direction : Direction.values()) {
            addWater(level, pos, pos.relative(direction), seen, water);
        }

        int count = 0;
        while (!water.isEmpty() && count < GeneratorRates.WATER_MAX_BLOCKS) {
            BlockPos current = water.removeFirst();
            count++;
            for (Direction direction : Direction.values()) {
                addWater(level, pos, current.relative(direction), seen, water);
            }
        }
        return count;
    }

    private static void addWater(Level level, BlockPos center, BlockPos pos, Set<BlockPos> seen, ArrayDeque<BlockPos> water) {
        BlockPos immutable = pos.immutable();
        if (seen.contains(immutable) || !inRange(center, immutable) || !level.getFluidState(immutable).is(FluidTags.WATER)) {
            return;
        }
        seen.add(immutable);
        water.add(immutable);
    }

    private static boolean inRange(BlockPos center, BlockPos pos) {
        return Math.abs(pos.getX() - center.getX()) <= 1
                && Math.abs(pos.getY() - center.getY()) <= 1
                && Math.abs(pos.getZ() - center.getZ()) <= 1;
    }

    private void startWaterFuel() {
        ItemStack fuel = items.get(WATER_SLOT);
        if (waterTicks <= 0 && canPlaceItem(WATER_SLOT, fuel)) {
            ItemStack returned = fuel.is(Items.WATER_BUCKET) ? new ItemStack(Items.BUCKET) : new ItemStack(ICItems.EMPTY_CELL.get());
            ItemStack output = getItem(2);
            if (!output.isEmpty() && (!ItemStack.isSameItemSameComponents(output, returned) || output.getCount() >= output.getMaxStackSize())) return;
            fuel.shrink(1);
            if (output.isEmpty()) setItem(2, returned);
            else output.grow(1);
            waterTicks = GeneratorRates.WATER_BUCKET_TICKS;
            waterTicksMax = GeneratorRates.WATER_BUCKET_TICKS;
            setChanged();
        }
    }

    private void addPassiveEu() {
        if (waterBlocks <= 0) {
            return;
        }
        microEu += waterBlocks;
        int eu = WaterMillMath.euFromMicro(microEu);
        microEu = WaterMillMath.microEuLeft(microEu);
        addEu(eu);
    }

    private void addEu(int amount) {
        int next = WaterMillMath.cappedBuffer(euBuffer, amount);
        if (next != euBuffer) {
            euBuffer = next;
            setChanged();
        }
    }

    @Override
    protected float currentOutput() {
        float rate = GeneratorRates.waterPassiveOutput(waterBlocks);
        if (waterTicks > 0) rate += GeneratorRates.WATER_BUCKET_EU_PER_TICK;
        return rate;
    }

    @Override
    protected int burnTicks() { return waterTicks; }

    @Override
    protected int burnTicksMax() { return waterTicksMax; }

    @Override
    protected void setBurnTicks(int value) { waterTicks = value; }

    @Override
    protected void setBurnTicksMax(int value) { waterTicksMax = value; }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        if (slot == 0) {
            return super.canPlaceItem(slot, stack);
        }
        return slot == WATER_SLOT && (stack.is(Items.WATER_BUCKET) || stack.is(ICItems.WATER_CELL.get())
                && stack.getOrDefault(ICDataComponents.FLUID_AMOUNT.get(), 1000) == 1000);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("water_blocks", waterBlocks);
        output.putInt("micro_eu", microEu);
        output.putInt("water_ticks", waterTicks);
        output.putInt("water_ticks_max", waterTicksMax);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        waterBlocks = input.getIntOr("water_blocks", 0);
        microEu = input.getIntOr("micro_eu", 0);
        waterTicks = input.getIntOr("water_ticks", 0);
        waterTicksMax = input.getIntOr("water_ticks_max", 0);
    }

}
