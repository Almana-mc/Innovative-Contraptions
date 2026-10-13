package me.almana.innovativecontraptions.content.thermal;

import me.almana.innovativecontraptions.content.equipment.EquipmentData;
import me.almana.innovativecontraptions.content.processing.ProcessingAutomation;
import me.almana.innovativecontraptions.registry.ICItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
//? if <26.1
//import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public final class BlastFurnaceBlockEntity extends ThermalInventory {
    public static final int INPUT = 0;
    public static final int AIR_CELL = 1;
    public static final int STEEL = 2;
    public static final int SLAG = 3;
    public static final int EMPTY_CELL = 4;
    public static final int UPGRADES = 5;
    public static final int HEAT_CAPACITY = 50_000;
    public static final int AIR_CAPACITY = 8_000;
    public static final int CELL_AIR = 1_000;
    private static final int[] INPUTS = {INPUT, AIR_CELL};
    private static final int[] OUTPUTS = {STEEL, SLAG, EMPTY_CELL};
    private int heat;
    private int air;
    private int progress;
    private int duration;
    private int recipeAir;
    private int status;
    //? if >=26.1 {
    private ResourceKey<Recipe<?>> activeRecipe;
    //?} else
    //private Identifier activeRecipe;
    private ItemStack activeInput = ItemStack.EMPTY;
    private ItemStack plannedSteel = ItemStack.EMPTY;
    private ItemStack plannedSlag = ItemStack.EMPTY;

    public BlastFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(ThermalContent.FURNACE.get(), pos, state, 7);
    }

    @Override public void tick() {
        for (int slot = UPGRADES; slot < items.size(); slot++) ProcessingAutomation.transfer(this, this, items.get(slot), null, null);
        loadAirCell();
        RecipeHolder<SteelmakingRecipe> holder = ((ServerLevel) level).recipeAccess()
                .getRecipeFor(ThermalContent.STEELMAKING.get(), new SingleRecipeInput(items.get(INPUT)), level).orElse(null);
        if (holder == null) {
            resetRecipe();
            status = 0;
            cool();
            return;
        }
        SteelmakingRecipe recipe = holder.value();
        ItemStack steel = recipe.result().create();
        ItemStack slag = recipe.slag().create();
        if (!holder.id().equals(activeRecipe) || !ItemStack.isSameItemSameComponents(activeInput, items.get(INPUT))
                || duration != recipe.processTicks() || recipeAir != recipe.airMb()
                || !ItemStack.matches(plannedSteel, steel) || !ItemStack.matches(plannedSlag, slag)) {
            progress = 0;
            activeRecipe = holder.id();
            activeInput = items.get(INPUT).copy();
            duration = recipe.processTicks();
            recipeAir = recipe.airMb();
            plannedSteel = steel.copy();
            plannedSlag = slag.copy();
            setChanged();
        }
        if (!fits(STEEL, steel) || !fits(SLAG, slag)) { status = 3; cool(); return; }
        process(recipe, steel, slag);
    }

    private void process(SteelmakingRecipe recipe, ItemStack steel, ItemStack slag) {
        if (!warmUp()) return;
        int cost = (int) (((long) (progress + 1) * recipe.airMb() / duration)
                - ((long) progress * recipe.airMb() / duration));
        if (air < cost) { status = 2; return; }
        air -= cost;
        progress++;
        status = 4;
        if (progress >= duration) {
            items.get(INPUT).shrink(1);
            insert(STEEL, steel);
            insert(SLAG, slag);
            progress = 0;
        }
        setChanged();
    }

    private boolean warmUp() {
        Direction face = getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        int request = heat < HEAT_CAPACITY ? HEAT_CAPACITY - heat : 1;
        int supplied = level.getBlockEntity(worldPosition.relative(face)) instanceof HeatSource heater
                ? heater.supplyHeat(face.getOpposite(), request) : 0;
        if (supplied == 0) { cool(); status = 1; return false; }
        heat = Math.min(HEAT_CAPACITY, heat + supplied);
        setChanged();
        if (heat < HEAT_CAPACITY) { status = 5; return false; }
        return true;
    }

    private void loadAirCell() {
        ItemStack cell = items.get(AIR_CELL);
        ItemStack empty = new ItemStack(ICItems.EMPTY_CELL.get());
        int available = cell.getOrDefault(EquipmentData.AIR.get(), CELL_AIR);
        if (!cell.is(ICItems.AIR_CELL.get()) || air > AIR_CAPACITY - available || !fits(EMPTY_CELL, empty)) return;
        cell.shrink(1);
        insert(EMPTY_CELL, empty);
        air += available;
        setChanged();
    }

    private void cool() { if (heat > 0) { heat--; setChanged(); } }
    private void resetRecipe() {
        if (progress != 0 || activeRecipe != null) setChanged();
        progress = 0;
        duration = 0;
        recipeAir = 0;
        activeRecipe = null;
        activeInput = ItemStack.EMPTY;
        plannedSteel = ItemStack.EMPTY;
        plannedSlag = ItemStack.EMPTY;
    }

    public int heat() { return heat; }
    public int air() { return air; }
    public int progress() { return progress; }
    @Override public boolean canPlaceItem(int slot, ItemStack stack) {
        if (slot >= UPGRADES) return validUpgrade(stack);
        if (slot == AIR_CELL) return stack.is(ICItems.AIR_CELL.get());
        if (slot != INPUT || stack.is(ICItems.AIR_CELL.get())) return false;
        return !(level instanceof ServerLevel server) || server.recipeAccess()
                .getRecipeFor(ThermalContent.STEELMAKING.get(), new SingleRecipeInput(stack), server).isPresent();
    }
    public static boolean validUpgrade(ItemStack stack) {
        return stack.is(ICItems.EJECTOR_UPGRADE.get()) || stack.is(ICItems.PULLING_UPGRADE.get())
                || stack.is(ICItems.ADVANCED_EJECTOR_UPGRADE.get()) || stack.is(ICItems.ADVANCED_PULLING_UPGRADE.get());
    }
    @Override public int[] getSlotsForFace(Direction side) { return side == Direction.DOWN ? OUTPUTS : INPUTS; }
    @Override public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) { return side == Direction.DOWN && slot >= STEEL; }
    @Override protected int displayValue(int index) {
        return switch (index) {
            case 0 -> heat * 100 / HEAT_CAPACITY;
            case 1 -> air;
            case 2 -> progress;
            case 3 -> duration;
            case 4 -> status;
            default -> 0;
        };
    }
    @Override protected void receiveDisplayValue(int index, int value) {}
    @Override protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("heat", heat);
        output.putInt("air", air);
        output.putInt("progress", progress);
        output.putInt("duration", duration);
        output.putInt("recipe_air", recipeAir);
        if (!plannedSteel.isEmpty()) output.store("planned_steel", ItemStack.CODEC, plannedSteel);
        if (!plannedSlag.isEmpty()) output.store("planned_slag", ItemStack.CODEC, plannedSlag);
        //? if >=26.1 {
        if (activeRecipe != null) output.store("active_recipe", Recipe.KEY_CODEC, activeRecipe);
        //?} else
        //if (activeRecipe != null) output.store("active_recipe", Identifier.CODEC, activeRecipe);
    }
    @Override protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        heat = input.getIntOr("heat", 0);
        air = input.getIntOr("air", 0);
        progress = input.getIntOr("progress", 0);
        duration = input.getIntOr("duration", 0);
        recipeAir = input.getIntOr("recipe_air", 0);
        plannedSteel = input.read("planned_steel", ItemStack.CODEC).orElse(ItemStack.EMPTY);
        plannedSlag = input.read("planned_slag", ItemStack.CODEC).orElse(ItemStack.EMPTY);
        //? if >=26.1 {
        activeRecipe = input.read("active_recipe", Recipe.KEY_CODEC).orElse(null);
        //?} else
        //activeRecipe = input.read("active_recipe", Identifier.CODEC).orElse(null);
        activeInput = items.get(INPUT).copy();
    }
}
