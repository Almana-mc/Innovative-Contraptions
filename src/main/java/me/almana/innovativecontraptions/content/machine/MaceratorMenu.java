package me.almana.innovativecontraptions.content.machine;

import me.almana.innovativecontraptions.content.energy.BatteryItem;
import me.almana.innovativecontraptions.content.machine.recipe.MaceratingRecipe;
import me.almana.innovativecontraptions.registry.ICMenus;
import me.almana.innovativecontraptions.registry.ICRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class MaceratorMenu extends AbstractContainerMenu {
    public static final int INPUT_SLOT_X = 54;
    public static final int INPUT_SLOT_Y = 40;
    public static final int BATTERY_SLOT_X = 54;
    public static final int BATTERY_SLOT_Y = 62;
    public static final int OUTPUT_SLOT_X = 98;
    public static final int OUTPUT_SLOT_Y = 51;

    public static final int MACHINE_SLOTS = 3;
    private static final int PLAYER_INVENTORY_START = MACHINE_SLOTS;
    private static final int HOTBAR_START = PLAYER_INVENTORY_START + 27;
    private static final int PLAYER_INVENTORY_END = HOTBAR_START + 9;
    private static final int UPGRADE_MENU_START = PLAYER_INVENTORY_END;

    private final MaceratorBlockEntity macerator;
    private final ContainerData data;

    public MaceratorMenu(int containerId, Inventory inventory, RegistryFriendlyByteBuf data) {
        this(containerId, inventory, resolve(inventory, data));
    }

    public MaceratorMenu(int containerId, Inventory inventory, MaceratorBlockEntity macerator) {
        super(ICMenus.MACERATOR.get(), containerId);
        this.macerator = macerator;
        data = inventory.player.level().isClientSide() ? new SimpleContainerData(7) : macerator.dataAccess;
        addSlot(new Slot(macerator, MaceratorBlockEntity.SLOT_INPUT, INPUT_SLOT_X, INPUT_SLOT_Y));
        addSlot(new Slot(macerator, MaceratorBlockEntity.SLOT_BATTERY, BATTERY_SLOT_X, BATTERY_SLOT_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() instanceof BatteryItem;
            }
        });
        addSlot(new Slot(macerator, MaceratorBlockEntity.SLOT_OUTPUT, OUTPUT_SLOT_X, OUTPUT_SLOT_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        addPlayerInventory(inventory);
        for (int slot = MaceratorBlockEntity.UPGRADE_START; slot < MaceratorBlockEntity.SLOT_COUNT; slot++) {
            addSlot(new Slot(macerator, slot, 14 + (slot - MaceratorBlockEntity.UPGRADE_START) * 22, 94) {
                @Override public boolean mayPlace(ItemStack stack) { return MaceratorBlockEntity.validUpgrade(stack); }
            });
        }
        addDataSlots(data);
    }

    private static MaceratorBlockEntity resolve(Inventory inventory, RegistryFriendlyByteBuf data) {
        BlockEntity entity = inventory.player.level().getBlockEntity(data.readBlockPos());
        if (entity instanceof MaceratorBlockEntity macerator) {
            return macerator;
        }
        throw new IllegalStateException("Macerator menu opened without a Macerator block entity");
    }

    private void addPlayerInventory(Inventory inventory) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(inventory, column + row * 9 + 9, 8 + column * 18, 130 + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(inventory, column, 8 + column * 18, 188));
        }
    }

    public int energy() {
        return (data.get(0) & 65535) | (data.get(5) << 16);
    }

    public int energyCapacity() {
        return (data.get(1) & 65535) | (data.get(6) << 16);
    }

    public int progress() {
        return data.get(2);
    }

    public int progressMax() {
        return data.get(3);
    }

    public int statusCode() {
        return data.get(4);
    }

    @Override
    public boolean stillValid(Player player) {
        return macerator.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        if (!(macerator.getLevel() instanceof ServerLevel level)) {
            return ItemStack.EMPTY;
        }
        Slot slot = slots.get(slotIndex);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack moved = stack.copy();
        if (slotIndex < MACHINE_SLOTS || slotIndex >= UPGRADE_MENU_START) {
            if (!moveItemStackTo(stack, PLAYER_INVENTORY_START, PLAYER_INVENTORY_END, true)) {
                return ItemStack.EMPTY;
            }
        } else if (MaceratorBlockEntity.validUpgrade(stack)) {
            if (!moveItemStackTo(stack, UPGRADE_MENU_START, UPGRADE_MENU_START + 4, false)) return ItemStack.EMPTY;
        } else if (isMaceratingInput(level, stack)) {
            if (!moveItemStackTo(stack, MaceratorBlockEntity.SLOT_INPUT, MaceratorBlockEntity.SLOT_INPUT + 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (stack.getItem() instanceof BatteryItem) {
            if (!moveItemStackTo(stack, MaceratorBlockEntity.SLOT_BATTERY, MaceratorBlockEntity.SLOT_BATTERY + 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (slotIndex < HOTBAR_START) {
            if (!moveItemStackTo(stack, HOTBAR_START, PLAYER_INVENTORY_END, false)) {
                return ItemStack.EMPTY;
            }
        } else if (!moveItemStackTo(stack, PLAYER_INVENTORY_START, HOTBAR_START, false)) {
            return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        if (stack.getCount() == moved.getCount()) {
            return ItemStack.EMPTY;
        }
        slot.onTake(player, stack);
        return moved;
    }

    private boolean isMaceratingInput(ServerLevel level, ItemStack stack) {
        ItemStack recipeInput = stack.copy();
        recipeInput.setCount(recipeInput.getMaxStackSize());
        return level.recipeAccess()
                .getRecipeFor(ICRecipes.MACERATING_TYPE.get(), new SingleRecipeInput(recipeInput), level)
                .isPresent();
    }
}
