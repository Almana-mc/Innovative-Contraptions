package me.almana.innovativecontraptions.content.machine;

import me.almana.innovativecontraptions.compat.SavedBlockEntity;
import me.almana.innovativecontraptions.content.energy.BatteryItem;
import me.almana.innovativecontraptions.content.energy.EuSink;
import me.almana.innovativecontraptions.content.energy.EuTier;
import me.almana.innovativecontraptions.content.energy.net.EuGrids;
import me.almana.innovativecontraptions.content.machine.recipe.MaceratingRecipe;
import me.almana.innovativecontraptions.registry.ICBlockEntities;
import me.almana.innovativecontraptions.registry.ICRecipes;
import me.almana.innovativecontraptions.registry.ICItems;
import me.almana.innovativecontraptions.content.processing.ProcessingAutomation;
import me.almana.innovativecontraptions.content.processing.ProcessingUpgrades;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
//? if <26.1
//import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import javax.annotation.Nullable;

public final class MaceratorBlockEntity extends SavedBlockEntity implements WorldlyContainer, MenuProvider, EuSink {
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_BATTERY = 1;
    public static final int SLOT_OUTPUT = 2;
    public static final int UPGRADE_START = 3;
    public static final int SLOT_COUNT = 7;
    public static final int CAPACITY = 800;
    private static final int[] TOP_SLOTS = {SLOT_INPUT};
    private static final int[] BOTTOM_SLOTS = {SLOT_OUTPUT};
    private static final int[] HORIZONTAL_SLOTS = {SLOT_INPUT, SLOT_BATTERY};

    private final NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
    private final RecipeManager.CachedCheck<SingleRecipeInput, MaceratingRecipe> quickCheck =
            RecipeManager.createCheck(ICRecipes.MACERATING_TYPE.get());
    private ItemStack activeInput = ItemStack.EMPTY;
    private int storedEu;
    private int progress;
    private int progressMax;
    private int statusCode;
    private int recipeTicks = 400, recipePower = 2;
    @Nullable
    //? if >=26.1 {
    private ResourceKey<Recipe<?>> activeRecipeKey;
    //?} else
    //private Identifier activeRecipeKey;

    public final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> storedEu;
                case 1 -> capacity();
                case 2 -> progress;
                case 3 -> progressMax;
                case 4 -> statusCode;
                case 5 -> storedEu >>> 16;
                case 6 -> capacity() >>> 16;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> storedEu = value;
                case 2 -> progress = value;
                case 3 -> progressMax = value;
                case 4 -> statusCode = value;
                default -> {
                }
            }
        }

        @Override
        public int getCount() {
            return 7;
        }
    };

    public MaceratorBlockEntity(BlockPos pos, BlockState state) {
        super(ICBlockEntities.MACERATOR.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, MaceratorBlockEntity macerator) {
        macerator.dischargeBattery();
        for (int slot = UPGRADE_START; slot < SLOT_COUNT; slot++) ProcessingAutomation.transfer(macerator, macerator, macerator.items.get(slot), null, null);
        ItemStack input = macerator.items.get(SLOT_INPUT);
        if (input.isEmpty()) {
            macerator.statusCode = 0;
            macerator.activeInput = ItemStack.EMPTY;
            macerator.resetProgress();
            return;
        }
        if (!ItemStack.isSameItemSameComponents(input, macerator.activeInput)) {
            macerator.activeInput = input.copy();
            macerator.resetProgress();
        }
        RecipeHolder<MaceratingRecipe> holder = macerator.quickCheck
                .getRecipeFor(new SingleRecipeInput(input), (ServerLevel) level)
                .orElse(null);
        if (holder == null) {
            macerator.statusCode = 1;
            macerator.activeRecipeKey = null;
            macerator.resetProgress();
            return;
        }
        if (!holder.id().equals(macerator.activeRecipeKey)) {
            macerator.activeRecipeKey = holder.id();
            macerator.resetProgress();
        }
        MaceratingRecipe recipe = holder.value();
        macerator.recipeTicks = recipe.processTicks(); macerator.recipePower = recipe.euPerTick();
        int overclockers = macerator.upgradeCount(ICItems.OVERCLOCKER_UPGRADE.get());
        int duration = ProcessingUpgrades.ticks(recipe.processTicks(), overclockers);
        if (macerator.progressMax > 0 && macerator.progressMax != duration) macerator.progress = (int) ((long) macerator.progress * duration / macerator.progressMax);
        macerator.progressMax = duration;
        int power = ProcessingUpgrades.energy(recipe.euPerTick(), overclockers);
        ItemStack result = recipe.assemble(new SingleRecipeInput(input));
        if (!macerator.canAccept(result)) {
            macerator.statusCode = 3;
            return;
        }
        if (macerator.storedEu < power) {
            macerator.statusCode = 2;
            return;
        }
        macerator.statusCode = 4;
        macerator.storedEu -= power;
        macerator.progress++;
        if (macerator.progress >= duration) {
            int batch = ProcessingUpgrades.batch(recipe.processTicks(), overclockers);
            for (int operation = 0; operation < batch && input.getCount() >= recipe.inputCount() && macerator.canAccept(result); operation++) {
                input.shrink(recipe.inputCount()); macerator.insertResult(result);
            }
            macerator.progress = 0;
        }
        macerator.setChanged();
    }

    public int storedEu() { return storedEu; }
    public int progress() { return progress; }
    public int progressMax() { return progressMax; }
    public int statusCode() { return statusCode; }
    //? if >=26.1 {
    @Nullable public ResourceKey<Recipe<?>> activeRecipeKey() { return activeRecipeKey; }
    //?} else
    //@Nullable public Identifier activeRecipeKey() { return activeRecipeKey; }

    public int upgradeCount(Item item) { return ProcessingUpgrades.count(this, UPGRADE_START, item); }
    public int capacity() {
        int speed = upgradeCount(ICItems.OVERCLOCKER_UPGRADE.get());
        long operation = (long) ProcessingUpgrades.ticks(recipeTicks, speed) * ProcessingUpgrades.energy(recipePower, speed);
        return (int) Math.min(Integer.MAX_VALUE, Math.max(CAPACITY, operation) + 10_000L * upgradeCount(ICItems.ENERGY_STORAGE_UPGRADE.get()));
    }
    public int inputTier() { return Math.min(5, 1 + upgradeCount(ICItems.TRANSFORMER_UPGRADE.get())); }
    @Override public int maxInputVoltage() { return EuTier.values()[inputTier() - 1].volts; }
    @Override public int demandedEu() { return Math.max(0, capacity() - storedEu); }

    public int receiveEu(int voltage, int amount) {
        if (voltage > maxInputVoltage()) return 0;
        int accepted = Math.clamp(amount, 0, demandedEu());
        if (accepted > 0) {
            storedEu += accepted;
            setChanged();
        }
        return accepted;
    }

    @Override public Component getDisplayName() { return getBlockState().getBlock().getName(); }
    @Override public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) { return new MaceratorMenu(id, inventory, this); }
    @Override public int getContainerSize() { return items.size(); }
    @Override public boolean isEmpty() { return items.stream().allMatch(ItemStack::isEmpty); }
    @Override public ItemStack getItem(int slot) { return items.get(slot); }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack removed = ContainerHelper.removeItem(items, slot, amount);
        if (!removed.isEmpty()) setChanged();
        return removed;
    }

    @Override public ItemStack removeItemNoUpdate(int slot) { return ContainerHelper.takeItem(items, slot); }
    @Override public void setItem(int slot, ItemStack stack) { items.set(slot, stack); setChanged(); }
    @Override public boolean stillValid(Player player) { return Container.stillValidBlockEntity(this, player); }
    @Override public void clearContent() { items.clear(); }
    public static boolean validUpgrade(ItemStack stack) {
        return ProcessingUpgrades.valid(stack) && !stack.is(ICItems.FLUID_PULLING_UPGRADE.get()) && !stack.is(ICItems.FLUID_EJECTOR_UPGRADE.get())
                && !stack.is(ICItems.REDSTONE_INVERTER_UPGRADE.get());
    }
    @Override public boolean canPlaceItem(int slot, ItemStack stack) {
        return slot >= UPGRADE_START ? validUpgrade(stack) : slot == SLOT_INPUT || slot == SLOT_BATTERY && stack.getItem() instanceof BatteryItem;
    }
    @Override
    public int[] getSlotsForFace(Direction direction) {
        return switch (direction) {
            case UP -> TOP_SLOTS;
            case DOWN -> BOTTOM_SLOTS;
            default -> HORIZONTAL_SLOTS;
        };
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction direction) {
        return switch (direction) {
            case UP -> slot == SLOT_INPUT;
            case DOWN -> false;
            default -> slot == SLOT_INPUT || slot == SLOT_BATTERY && stack.getItem() instanceof BatteryItem;
        };
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction direction) {
        return slot == SLOT_OUTPUT && direction == Direction.DOWN
                || slot == SLOT_BATTERY && direction.getAxis().isHorizontal();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level != null && !level.isClientSide()) EuGrids.markDirty(level, worldPosition);
    }

    @Override
    public void setRemoved() {
        if (level != null && !level.isClientSide()) EuGrids.markDirty(level, worldPosition);
        super.setRemoved();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, items);
        output.putInt("stored_eu", storedEu);
        output.putInt("progress", progress);
        //? if >=26.1 {
        if (activeRecipeKey != null) output.store("active_recipe", Recipe.KEY_CODEC, activeRecipeKey);
        //?} else
        //if (activeRecipeKey != null) output.store("active_recipe", Identifier.CODEC, activeRecipeKey);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        ContainerHelper.loadAllItems(input, items);
        activeInput = items.get(SLOT_INPUT).copy();
        storedEu = input.getIntOr("stored_eu", 0);
        progress = input.getIntOr("progress", 0);
        //? if >=26.1 {
        input.read("active_recipe", Recipe.KEY_CODEC).ifPresent(key -> activeRecipeKey = key);
        //?} else
        //input.read("active_recipe", Identifier.CODEC).ifPresent(key -> activeRecipeKey = key);
    }

    private void dischargeBattery() {
        ItemStack stack = items.get(SLOT_BATTERY);
        int transferred = BatteryItem.extractEnergy(stack, demandedEu(), inputTier(), false);
        if (transferred > 0) {
            storedEu += transferred;
            setChanged();
        }
    }

    private void resetProgress() {
        if (progress != 0 || progressMax != 0) {
            progress = 0;
            progressMax = 0;
            setChanged();
        }
    }

    private boolean canAccept(ItemStack result) {
        if (result.isEmpty()) return false;
        ItemStack output = items.get(SLOT_OUTPUT);
        return output.isEmpty() || ItemStack.isSameItemSameComponents(output, result)
                && output.getCount() + result.getCount() <= Math.min(getMaxStackSize(), output.getMaxStackSize());
    }

    private void insertResult(ItemStack result) {
        ItemStack output = items.get(SLOT_OUTPUT);
        if (output.isEmpty()) items.set(SLOT_OUTPUT, result.copy());
        else output.grow(result.getCount());
    }
}
