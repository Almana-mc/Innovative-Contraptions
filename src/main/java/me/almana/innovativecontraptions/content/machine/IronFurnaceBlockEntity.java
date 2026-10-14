package me.almana.innovativecontraptions.content.machine;

import me.almana.innovativecontraptions.compat.SavedBlockEntity;
import me.almana.innovativecontraptions.registry.ICBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
//? if <26.1
//import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.FurnaceMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class IronFurnaceBlockEntity extends SavedBlockEntity implements Container, MenuProvider {
    private static final int SLOT_INPUT = 0;
    private static final int SLOT_FUEL = 1;
    private static final int SLOT_RESULT = 2;
    private static final int COOK_TICKS = 160;

    private final NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY);
    private final RecipeManager.CachedCheck<SingleRecipeInput, ? extends AbstractCookingRecipe> quickCheck =
            RecipeManager.createCheck(RecipeType.SMELTING);

    private int litTime;
    private int litDuration;
    private int cookTime;

    public final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> litTime;
                case 1 -> litDuration;
                case 2 -> cookTime;
                case 3 -> COOK_TICKS;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> litTime = value;
                case 1 -> litDuration = value;
                case 2 -> cookTime = value;
                default -> {
                }
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    public IronFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(ICBlockEntities.IRON_FURNACE.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, IronFurnaceBlockEntity be) {
        ServerLevel serverLevel = (ServerLevel) level;
        boolean wasLit = be.isLit();
        boolean changed = false;
        if (be.isLit()) be.litTime--;

        ItemStack fuel = be.items.get(SLOT_FUEL);
        ItemStack input = be.items.get(SLOT_INPUT);
        boolean hasFuel = !fuel.isEmpty();

        SingleRecipeInput recipeInput = new SingleRecipeInput(input);
        RecipeHolder<? extends AbstractCookingRecipe> recipe =
                input.isEmpty() ? null : be.quickCheck.getRecipeFor(recipeInput, serverLevel).orElse(null);

        if (be.isLit() || (!input.isEmpty() && hasFuel)) {
            int maxStack = be.getMaxStackSize();
            //? if >=26.1 {
            if (!be.isLit() && canBurn(recipe, be.items, maxStack)) {
            //?} else
            //if (!be.isLit() && canBurn(recipe, serverLevel.registryAccess(), be.items, maxStack)) {
                //? if >=26.1 {
                be.litDuration = serverLevel.fuelValues().burnDuration(fuel);
                //?} else {
                /*be.litDuration = fuel.getBurnTime(RecipeType.SMELTING);
                *///?}
                be.litTime = be.litDuration;
                if (be.isLit() && hasFuel) {
                    changed = true;
                    //? if >=26.1 {
                    ItemStackTemplate remainder = fuel.getCraftingRemainder();
                    //?} else {
                    /*ItemStack leftover = fuel.getCraftingRemainingItem();
                    ItemStackTemplate remainder = leftover.isEmpty() ? null : ItemStackTemplate.fromNonEmptyStack(leftover);
                    *///?}
                    fuel.shrink(1);
                    if (fuel.isEmpty() && remainder != null) {
                        be.items.set(SLOT_FUEL, remainder.create());
                    }
                }
            }

            //? if >=26.1 {
            if (be.isLit() && canBurn(recipe, be.items, maxStack)) {
            //?} else
            //if (be.isLit() && canBurn(recipe, serverLevel.registryAccess(), be.items, maxStack)) {
                be.cookTime++;
                if (be.cookTime >= COOK_TICKS) {
                    be.cookTime = 0;
                    //? if >=26.1 {
                    burn(recipe, recipeInput, be.items, maxStack);
                    //?} else
                    //burn(recipe, recipeInput, serverLevel.registryAccess(), be.items, maxStack);
                    changed = true;
                }
            } else {
                be.cookTime = 0;
            }
        } else if (!be.isLit() && be.cookTime > 0) {
            be.cookTime = Mth.clamp(be.cookTime - 2, 0, COOK_TICKS);
        }

        if (wasLit != be.isLit()) changed = true;
        if (changed) be.setChanged();
    }

    private boolean isLit() {
        return litTime > 0;
    }

    private static boolean canBurn(RecipeHolder<? extends AbstractCookingRecipe> recipe,
                                    //? if <26.1
                                    //RegistryAccess registries,
                                    NonNullList<ItemStack> items, int maxStack) {
        if (items.get(SLOT_INPUT).isEmpty() || recipe == null) return false;
        //? if >=26.1 {
        ItemStack output = recipe.value().assemble(new SingleRecipeInput(items.get(SLOT_INPUT)));
        //?} else
        //ItemStack output = recipe.value().assemble(new SingleRecipeInput(items.get(SLOT_INPUT)), registries);
        if (output.isEmpty()) return false;
        ItemStack result = items.get(SLOT_RESULT);
        if (result.isEmpty()) return true;
        if (!ItemStack.isSameItemSameComponents(result, output)) return false;
        return result.getCount() < maxStack && result.getCount() < result.getMaxStackSize()
                || result.getCount() < output.getMaxStackSize();
    }

    private static void burn(RecipeHolder<? extends AbstractCookingRecipe> recipe, SingleRecipeInput input,
                              //? if <26.1
                              //RegistryAccess registries,
                              NonNullList<ItemStack> items, int maxStack) {
        //? if >=26.1 {
        if (recipe == null || !canBurn(recipe, items, maxStack)) return;
        //?} else
        //if (recipe == null || !canBurn(recipe, registries, items, maxStack)) return;
        ItemStack ingredient = items.get(SLOT_INPUT);
        //? if >=26.1 {
        ItemStack output = recipe.value().assemble(input);
        //?} else
        //ItemStack output = recipe.value().assemble(input, registries);
        ItemStack result = items.get(SLOT_RESULT);
        if (result.isEmpty()) {
            items.set(SLOT_RESULT, output.copy());
        } else if (ItemStack.isSameItemSameComponents(result, output)) {
            result.grow(1);
        }
        ingredient.shrink(1);
    }

    @Override
    public Component getDisplayName() {
        return getBlockState().getBlock().getName();
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new FurnaceMenu(containerId, inventory, this, dataAccess);
    }

    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        return items.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getItem(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack removed = ContainerHelper.removeItem(items, slot, amount);
        if (!removed.isEmpty()) setChanged();
        return removed;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        items.clear();
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        if (slot == SLOT_RESULT) return false;
        //? if >=26.1 {
        if (slot == SLOT_FUEL) return level != null && level.fuelValues().isFuel(stack);
        //?} else {
        /*if (slot == SLOT_FUEL) return level != null && stack.getBurnTime(RecipeType.SMELTING) > 0;
        *///?}
        return true;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("lit_time", litTime);
        output.putInt("lit_duration", litDuration);
        output.putInt("cook_time", cookTime);
        for (int i = 0; i < items.size(); i++) {
            if (!items.get(i).isEmpty()) output.store("item_" + i, ItemStack.CODEC, items.get(i));
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        litTime = input.getIntOr("lit_time", 0);
        litDuration = input.getIntOr("lit_duration", 0);
        cookTime = input.getIntOr("cook_time", 0);
        for (int i = 0; i < items.size(); i++) {
            final int idx = i;
            input.read("item_" + i, ItemStack.CODEC).ifPresent(s -> items.set(idx, s));
        }
    }
}
