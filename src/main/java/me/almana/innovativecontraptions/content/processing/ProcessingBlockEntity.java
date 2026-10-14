package me.almana.innovativecontraptions.content.processing;

import java.util.List;
import me.almana.innovativecontraptions.compat.SavedBlockEntity;
import me.almana.innovativecontraptions.content.energy.BatteryItem;
import me.almana.innovativecontraptions.content.energy.DischargeFuel;
import me.almana.innovativecontraptions.content.energy.EuSink;
import me.almana.innovativecontraptions.content.energy.EuTier;
import me.almana.innovativecontraptions.content.energy.net.EuGrids;
import me.almana.innovativecontraptions.registry.ICBlocks;
import me.almana.innovativecontraptions.registry.ICDataComponents;
import me.almana.innovativecontraptions.registry.ICItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
//? if <26.1
//import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.Recipe.CommonInfo;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import javax.annotation.Nullable;

public final class ProcessingBlockEntity extends SavedBlockEntity implements WorldlyContainer, MenuProvider, EuSink {
    public static final int WATER_CAPACITY = 8000;
    public static final int INPUT = 0, BATTERY = 1, OUTPUT = 2, CANS = 3, FLUID_INPUT = 5, FLUID_RETURN = 6;
    private final NonNullList<ItemStack> items;
    private final ProcessingKind baseKind;
    private int mode;
    int energy;
    int progress;
    int duration;
    int status;
    int heat;
    private int clientWater;
    private double experience;
    private ItemStack activeInput = ItemStack.EMPTY;
    //? if >=26.1 {
    private ResourceKey<Recipe<?>> activeRecipe;
    //?} else
    //private Identifier activeRecipe;
    private ProcessingRecipe activeDefinition;
    private final FluidStacksResourceHandler water = new FluidStacksResourceHandler(1, WATER_CAPACITY) {
        @Override public boolean isValid(int index, FluidResource resource) { return resource.getFluid() == Fluids.WATER; }
        @Override protected void onContentsChanged(int index, FluidStack previous) { setChanged(); }
    };

    public final ContainerData data = new ContainerData() {
        @Override public int get(int index) {
            return switch (index) {
                case 0 -> energy;
                case 1 -> capacity();
                case 2 -> progress;
                case 3 -> duration;
                case 4 -> status;
                case 5 -> mode;
                case 6 -> level != null && level.isClientSide() ? clientWater : waterAmount();
                case 7 -> heat;
                case 8 -> (int) experience;
                default -> 0;
            };
        }
        @Override public void set(int index, int value) {
            switch (index) {
                case 0 -> energy = value;
                case 2 -> progress = value;
                case 3 -> duration = value;
                case 4 -> status = value;
                case 5 -> mode = value;
                case 6 -> clientWater = value;
                case 7 -> heat = value;
                case 8 -> experience = value;
            }
        }
        @Override public int getCount() { return 9; }
    };

    public ProcessingBlockEntity(BlockPos pos, BlockState state) {
        super(ProcessingContent.ENTITY.get(), pos, state);
        baseKind = state.is(ICBlocks.COMPRESSOR.get()) ? ProcessingKind.COMPRESSING
                : state.is(ICBlocks.RECYCLER.get()) ? ProcessingKind.RECYCLING
                : state.is(ICBlocks.SOLID_CANNING_MACHINE.get()) ? ProcessingKind.CANNING
                : state.is(ICBlocks.EXTRACTOR.get()) ? ProcessingKind.EXTRACTING
                : state.is(ICBlocks.ELECTRIC_FURNACE.get()) ? ProcessingKind.SMELTING
                : state.is(ICBlocks.INDUCTION_FURNACE.get()) ? ProcessingKind.INDUCTION
                : state.is(ICBlocks.THERMAL_CENTRIFUGE.get()) ? ProcessingKind.CENTRIFUGING
                : state.is(ICBlocks.BLOCK_SLICER.get()) ? ProcessingKind.SLICING
                : state.is(ICBlocks.ORE_WASHING_PLANT.get()) ? ProcessingKind.WASHING : ProcessingKind.ROLLING;
        items = NonNullList.withSize(baseKind == ProcessingKind.WASHING ? 11
                : baseKind == ProcessingKind.CENTRIFUGING ? 9
                : baseKind == ProcessingKind.CANNING || baseKind == ProcessingKind.SLICING ? 8 : 7, ItemStack.EMPTY);
    }

    public ProcessingKind kind() { return baseKind.forming() ? ProcessingKind.values()[ProcessingKind.EXTRUDING.ordinal() + mode] : baseKind; }
    public int upgradeStart() {
        return switch (baseKind) {
            case CANNING, SLICING -> 4;
            case WASHING -> 7;
            case INDUCTION, CENTRIFUGING -> 5;
            default -> 3;
        };
    }
    public int upgradeSlots() { return baseKind == ProcessingKind.INDUCTION ? 2 : 4; }
    public int upgrades(ItemLike item) { return ProcessingUpgrades.count(this, upgradeStart(), item); }
    public int capacity() {
        if (kind() == ProcessingKind.INDUCTION) return kind().capacity;
        int clocks = upgrades(ICItems.OVERCLOCKER_UPGRADE.get());
        long operation = (long) ProcessingUpgrades.ticks(kind().ticks, clocks) * ProcessingUpgrades.energy(kind().euPerTick, clocks);
        return (int) Math.min(Integer.MAX_VALUE, kind().capacity + 10000L * upgrades(ICItems.ENERGY_STORAGE_UPGRADE.get()) + operation);
    }
    public int chargeTier() { return Math.min(5, (baseKind == ProcessingKind.INDUCTION || baseKind == ProcessingKind.CENTRIFUGING ? 2 : 1)
            + (baseKind == ProcessingKind.INDUCTION ? 0 : upgrades(ICItems.TRANSFORMER_UPGRADE.get()))); }
    @Override public int maxInputVoltage() { return EuTier.values()[chargeTier() - 1].volts; }
    public int waterAmount() { return water.getAmountAsInt(0); }
    public FluidStacksResourceHandler waterTank() { return water; }
    public int storedEu() { return energy; }
    public int progress() { return progress; }
    public int heat() { return heat; }
    public boolean heatingSignal() {
        return level.hasNeighborSignal(worldPosition) ^ (upgrades(ICItems.REDSTONE_INVERTER_UPGRADE.get()) % 2 == 1);
    }

    public boolean setMode(int value) {
        if (!baseKind.forming() || value < 0 || value > 2) return false;
        if (value != mode) {
            mode = value;
            resetProgress();
            activeRecipe = null;
            setChanged();
        }
        return true;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ProcessingBlockEntity machine) {
        ItemStack battery = machine.items.get(BATTERY);
        int space = Math.max(0, machine.capacity() - machine.energy);
        int transferred = DischargeFuel.burn(battery, space, true);
        if (transferred == 0 && battery.getCount() == 1) transferred = BatteryItem.extractEnergy(battery, space, machine.chargeTier(), false);
        if (transferred > 0) {
            machine.energy = (int) Math.min(Integer.MAX_VALUE, (long) machine.energy + transferred);
            machine.setChanged();
        }
        ProcessingAutomation.tick(machine, machine.upgradeStart());
        if (machine.baseKind == ProcessingKind.WASHING) machine.emptyWaterContainer();
        if (machine.baseKind == ProcessingKind.INDUCTION) ProcessingHeat.induction((ServerLevel) level, machine);
        else {
            machine.process((ServerLevel) level);
            if (machine.baseKind == ProcessingKind.CENTRIFUGING) machine.updateHeat((ServerLevel) level);
        }
    }
    private void updateHeat(ServerLevel world) {
        var holder = world.recipeAccess().getRecipeFor(ProcessingContent.recipeType(kind()), new SingleRecipeInput(items.get(INPUT)), world).orElse(null);
        ProcessingHeat.centrifuge(this, holder == null ? 0 : holder.value().heat(), holder != null && canAcceptOutputs(holder.value()));
    }

    private void process(ServerLevel level) {
        ItemStack input = items.get(INPUT);
        if (!ItemStack.isSameItemSameComponents(input, activeInput)) {
            activeInput = input.copy();
            resetProgress();
        }
        var holder = recipeFor(level, input);
        if (holder == null) { status = input.isEmpty() ? 0 : 1; activeRecipe = null; resetProgress(); return; }
        ProcessingRecipe recipe = holder.value();
        if (!holder.id().equals(activeRecipe) || !recipe.sameOperation(activeDefinition)) {
            activeRecipe = holder.id();
            activeDefinition = recipe;
            resetProgress();
            setChanged();
        }
        int overclockers = upgrades(ICItems.OVERCLOCKER_UPGRADE.get());
        int nextDuration = ProcessingUpgrades.ticks(recipe.processTicks(), overclockers);
        if (duration > 0 && duration != nextDuration) progress = (int) Math.floor((double) progress / duration * nextDuration + 0.1);
        duration = nextDuration;
        if (!canAcceptOutputs(recipe)) { status = 3; if (upgradeStart() >= 0) resetProgress(); return; }
        if (recipe.cansRequired() > 0 && (!recipe.packaging().test(items.get(CANS)) || items.get(CANS).getCount() < recipe.cansRequired())) {
            status = 6; resetProgress(); return;
        }
        if (waterAmount() < recipe.waterMb()) { status = 5; return; }
        if (baseKind == ProcessingKind.CENTRIFUGING && heat < recipe.heat()) { status = 7; return; }
        if (baseKind == ProcessingKind.SLICING && ProcessingHeat.bladeStrength(items.get(CANS)) < recipe.hardness()) {
            status = 8; resetProgress(); return;
        }
        int cost = ProcessingUpgrades.energy(recipe.euPerTick(), overclockers);
        if (energy < cost) { status = 2; return; }
        status = 4;
        energy -= cost;
        if (++progress >= duration) {
            int batch = ProcessingUpgrades.batch(recipe.processTicks(), overclockers);
            for (int operation = 0; operation < batch && canComplete(recipe); operation++) complete(recipe);
        }
        setChanged();
    }

    private RecipeHolder<ProcessingRecipe> recipeFor(ServerLevel world, ItemStack input) {
        if (kind() != ProcessingKind.SMELTING) return world.recipeAccess().getRecipeFor(ProcessingContent.recipeType(kind()), new SingleRecipeInput(input), world).orElse(null);
        var holder = world.recipeAccess().getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(input), world).orElse(null);
        if (holder == null) return null;
        return new RecipeHolder<>(holder.id(), new ProcessingRecipe(new CommonInfo(true), kind(),
                //? if >=26.1 {
                holder.value().input(), ItemStackTemplate.fromNonEmptyStack(holder.value().assemble(new SingleRecipeInput(input))),
                //?} else
                //holder.value().getIngredients().getFirst(), ItemStackTemplate.fromNonEmptyStack(holder.value().assemble(new SingleRecipeInput(input), world.registryAccess())),
                1, List.of(), kind().ticks, kind().euPerTick, 0));
    }
    public boolean collectExperience(Player player) {
        if (kind() != ProcessingKind.SMELTING || !(level instanceof ServerLevel world)) return false;
        int amount = (int) experience;
        if (amount > 0) { ExperienceOrb.award(world, player.position(), amount); experience -= amount; setChanged(); }
        return true;
    }
    private boolean canComplete(ProcessingRecipe recipe) {
        return items.get(INPUT).getCount() >= recipe.inputCount() && canAcceptOutputs(recipe)
                && waterAmount() >= recipe.waterMb() && (recipe.cansRequired() == 0
                || recipe.packaging().test(items.get(CANS)) && items.get(CANS).getCount() >= recipe.cansRequired());
    }

    private boolean canAcceptOutputs(ProcessingRecipe recipe) {
        if (kind() == ProcessingKind.RECYCLING && !RecyclingRules.canProduceScrap(items.get(INPUT))) return true;
        if (recipe.byproducts().size() > (baseKind == ProcessingKind.WASHING || baseKind == ProcessingKind.CENTRIFUGING ? 2 : 0)) return false;
        if (!canAccept(OUTPUT, recipe.result().create())) return false;
        for (int i = 0; i < recipe.byproducts().size(); i++) {
            if (!canAccept(OUTPUT + 1 + i, recipe.byproducts().get(i).create())) return false;
        }
        return true;
    }

    private void complete(ProcessingRecipe recipe) {
        boolean producesOutput = kind() != ProcessingKind.RECYCLING
                || RecyclingRules.canProduceScrap(items.get(INPUT)) && level.getRandom().nextInt(RecyclingRules.YIELD_DENOMINATOR) == 0;
        if (kind() == ProcessingKind.SMELTING) ((ServerLevel) level).recipeAccess().getRecipeFor(RecipeType.SMELTING,
                //? if >=26.1 {
                new SingleRecipeInput(items.get(INPUT)), level).ifPresent(holder -> experience += holder.value().experience());
                //?} else
                //new SingleRecipeInput(items.get(INPUT)), level).ifPresent(holder -> experience += holder.value().getExperience());
        items.get(INPUT).shrink(recipe.inputCount());
        if (recipe.cansRequired() > 0) items.get(CANS).shrink(recipe.cansRequired());
        if (producesOutput) insertOutput(OUTPUT, recipe.result().create());
        for (int i = 0; i < recipe.byproducts().size(); i++) insertOutput(OUTPUT + 1 + i, recipe.byproducts().get(i).create());
        if (recipe.waterMb() > 0) water.set(0, FluidResource.of(Fluids.WATER), waterAmount() - recipe.waterMb());
        progress = 0;
    }

    private void emptyWaterContainer() {
        ItemStack input = items.get(FLUID_INPUT);
        ItemStack result = input.is(Items.WATER_BUCKET) ? new ItemStack(Items.BUCKET)
                : input.is(ICItems.WATER_CELL.get()) ? new ItemStack(ICItems.EMPTY_CELL.get()) : ItemStack.EMPTY;
        int amount = input.getOrDefault(ICDataComponents.FLUID_AMOUNT.get(), 1000);
        if (result.isEmpty() || waterAmount() + amount > WATER_CAPACITY || !canAccept(FLUID_RETURN, result)) return;
        input.shrink(1);
        insertOutput(FLUID_RETURN, result);
        water.set(0, FluidResource.of(Fluids.WATER), waterAmount() + amount);
        setChanged();
    }

    boolean canAccept(int slot, ItemStack result) {
        ItemStack current = items.get(slot);
        int maximum = Math.min(getMaxStackSize(), result.getMaxStackSize());
        return result.getCount() <= maximum && (current.isEmpty()
                || ItemStack.isSameItemSameComponents(current, result) && current.getCount() + result.getCount() <= maximum);
    }

    void insertOutput(int slot, ItemStack result) {
        if (items.get(slot).isEmpty()) items.set(slot, result);
        else items.get(slot).grow(result.getCount());
    }

    private void resetProgress() {
        if (progress == 0 && duration == 0) return;
        progress = 0;
        duration = 0;
        setChanged();
    }
    @Override public int demandedEu() { return Math.max(0, capacity() - energy); }

    @Override public int receiveEu(int voltage, int amount) {
        if (voltage <= 0 || voltage > maxInputVoltage() || amount <= 0) return 0;
        int accepted = Math.min(amount, Math.max(0, capacity() - energy));
        if (accepted > 0) { energy += accepted; setChanged(); }
        return accepted;
    }
    @Override public Component getDisplayName() { return getBlockState().getBlock().getName(); }
    @Override public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) { return new ProcessingMenu(id, inventory, this); }
    @Override public int getContainerSize() { return items.size(); }
    @Override public boolean isEmpty() { return items.stream().allMatch(ItemStack::isEmpty); }
    @Override public ItemStack getItem(int slot) { return items.get(slot); }
    @Override public ItemStack removeItem(int slot, int amount) {
        ItemStack result = ContainerHelper.removeItem(items, slot, amount);
        if (!result.isEmpty()) setChanged();
        return result;
    }
    @Override public ItemStack removeItemNoUpdate(int slot) { return ContainerHelper.takeItem(items, slot); }
    @Override public void setItem(int slot, ItemStack stack) { items.set(slot, stack); setChanged(); }
    @Override public boolean stillValid(Player player) { return Container.stillValidBlockEntity(this, player); }
    @Override public void clearContent() { items.clear(); setChanged(); }
    @Override public boolean canPlaceItem(int slot, ItemStack stack) {
        if (slot >= upgradeStart()) return acceptsUpgrade(stack);
        if (baseKind == ProcessingKind.CANNING && slot == CANS) return stack.is(ICItems.TIN_CAN.get()) || stack.is(ICItems.FUEL_ROD.get());
        if (baseKind == ProcessingKind.SLICING && slot == CANS) return ProcessingHeat.bladeStrength(stack) > 0;
        if (baseKind == ProcessingKind.INDUCTION && slot == 3) return processable(stack);
        if (slot == INPUT) return processable(stack);
        return slot == BATTERY && stack.getItem() instanceof BatteryItem battery
                && battery.canSupplyEnergy() && battery.tier().ordinal() + 1 <= chargeTier()
                || slot == BATTERY && DischargeFuel.value(stack) > 0
                || slot == FLUID_INPUT && baseKind == ProcessingKind.WASHING && isWaterContainer(stack);
    }
    private boolean processable(ItemStack stack) {
        if (!(level instanceof ServerLevel world)) return !ProcessingUpgrades.valid(stack) && !(stack.getItem() instanceof BatteryItem);
        ItemStack candidate = stack.copyWithCount(stack.getMaxStackSize());
        if (baseKind == ProcessingKind.INDUCTION || baseKind == ProcessingKind.SMELTING) return world.recipeAccess().getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(candidate), world).isPresent();
        return recipeFor(world, candidate) != null;
    }
    public static boolean isWaterContainer(ItemStack stack) { return stack.is(Items.WATER_BUCKET) || stack.is(ICItems.WATER_CELL.get()); }
    public boolean acceptsUpgrade(ItemStack stack) {
        if (!ProcessingUpgrades.valid(stack)) return false;
        if (stack.is(ICItems.REDSTONE_INVERTER_UPGRADE.get())) return baseKind == ProcessingKind.INDUCTION || baseKind == ProcessingKind.CENTRIFUGING;
        if (stack.is(ICItems.FLUID_PULLING_UPGRADE.get())) return baseKind == ProcessingKind.WASHING;
        if (stack.is(ICItems.FLUID_EJECTOR_UPGRADE.get())) return false;
        return baseKind != ProcessingKind.INDUCTION || stack.getItem() instanceof DirectionalUpgradeItem;
    }
    @Override public int[] getSlotsForFace(Direction side) {
        if (side == Direction.DOWN) return baseKind == ProcessingKind.WASHING ? new int[]{2, 3, 4, 6}
                : baseKind == ProcessingKind.CENTRIFUGING ? new int[]{2, 3, 4}
                : baseKind == ProcessingKind.INDUCTION ? new int[]{2, 4} : new int[]{2};
        if (side == Direction.UP) return baseKind == ProcessingKind.CANNING || baseKind == ProcessingKind.INDUCTION ? new int[]{0, 3} : new int[]{0};
        if (baseKind == ProcessingKind.CANNING) return new int[]{1, 3};
        return baseKind == ProcessingKind.WASHING ? new int[]{1, 5} : new int[]{0, 1};
    }
    @Override public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
        if (!canPlaceItem(slot, stack)) return false;
        if (side == null) return true;
        if (side == Direction.DOWN) return false;
        if (side == Direction.UP) return slot == INPUT || (baseKind == ProcessingKind.CANNING || baseKind == ProcessingKind.INDUCTION) && slot == 3;
        if (baseKind == ProcessingKind.CANNING) return slot == BATTERY || slot == CANS;
        return slot == BATTERY || slot == (baseKind == ProcessingKind.WASHING ? FLUID_INPUT : INPUT);
    }
    @Override public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return side == Direction.DOWN && (slot == OUTPUT || baseKind == ProcessingKind.WASHING && (slot == 3 || slot == 4 || slot == FLUID_RETURN))
                || side == Direction.DOWN && (baseKind == ProcessingKind.CENTRIFUGING && (slot == 3 || slot == 4) || baseKind == ProcessingKind.INDUCTION && slot == 4)
                || side.getAxis().isHorizontal() && slot == BATTERY;
    }
    @Override public void onLoad() { super.onLoad(); if (level != null && !level.isClientSide()) EuGrids.markDirty(level, worldPosition); }
    @Override public void setRemoved() { if (level != null && !level.isClientSide()) EuGrids.markDirty(level, worldPosition); super.setRemoved(); }

    @Override protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, items);
        output.putInt("stored_eu", energy);
        output.putInt("progress", progress);
        output.putInt("mode", mode);
        output.putInt("water", waterAmount());
        output.putInt("heat", heat);
        output.putDouble("experience", experience);
        //? if >=26.1 {
        if (activeRecipe != null) output.store("active_recipe", Recipe.KEY_CODEC, activeRecipe);
        //?} else
        //if (activeRecipe != null) output.store("active_recipe", Identifier.CODEC, activeRecipe);
        if (activeDefinition != null) output.store("active_definition", ProcessingRecipe.MAP_CODEC.codec(), activeDefinition);
    }
    @Override protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        ContainerHelper.loadAllItems(input, items);
        mode = Math.clamp(input.getIntOr("mode", 0), 0, 2);
        energy = Math.max(0, input.getIntOr("stored_eu", 0));
        experience = Math.max(0, input.getDoubleOr("experience", 0));
        progress = Math.max(0, input.getIntOr("progress", 0));
        heat = Math.clamp(input.getIntOr("heat", 0), 0, baseKind == ProcessingKind.INDUCTION ? 10000 : 5001);
        water.set(0, FluidResource.of(Fluids.WATER), Math.clamp(input.getIntOr("water", 0), 0, WATER_CAPACITY));
        //? if >=26.1 {
        activeRecipe = input.read("active_recipe", Recipe.KEY_CODEC).orElse(null);
        //?} else
        //activeRecipe = input.read("active_recipe", Identifier.CODEC).orElse(null);
        activeDefinition = input.read("active_definition", ProcessingRecipe.MAP_CODEC.codec()).orElse(null);
        activeInput = items.get(INPUT).copy();
    }
}
