package me.almana.innovativecontraptions.content.generator;

import me.almana.innovativecontraptions.registry.ICMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class GeneratorMenu extends AbstractContainerMenu {
    private final GeneratorBlockEntityBase be;
    public final boolean hasFuel;
    public final boolean kinetic;
    public final boolean hasContainerReturn;
    private final int containerSlots;

    public GeneratorMenu(int containerId, Inventory inventory, RegistryFriendlyByteBuf data) {
        this(containerId, inventory, resolve(inventory, data));
    }

    public GeneratorMenu(int containerId, Inventory inventory, GeneratorBlockEntityBase be) {
        super(be instanceof KineticWindTurbineBlockEntity ? KineticContent.MENU.get() : ICMenus.GENERATOR.get(), containerId);
        this.be = be;
        this.kinetic = be instanceof KineticWindTurbineBlockEntity;
        this.hasFuel = be.getContainerSize() > 1;
        this.containerSlots = be.getContainerSize();
        this.hasContainerReturn = containerSlots > 2;

        int slotX = 54;
        addSlot(new Slot(be, 0, slotX, hasFuel ? 40 : 49) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return be.canPlaceItem(0, stack);
            }
        });
        if (hasFuel) {
            addSlot(new Slot(be, 1, slotX, 62) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return be.canPlaceItem(1, stack);
                }
            });
        }
        if (hasContainerReturn) {
            addSlot(new Slot(be, 2, 98, 62) {
                @Override public boolean mayPlace(ItemStack stack) { return false; }
            });
        }
        //? if >=26.1 {
        addStandardInventorySlots(inventory, 8, 106);
        //?} else {
        /*for (int row = 0; row < 3; row++) for (int col = 0; col < 9; col++) addSlot(new Slot(inventory, col + (row + 1) * 9, 8 + col * 18, 106 + row * 18));
        for (int col = 0; col < 9; col++) addSlot(new Slot(inventory, col, 8 + col * 18, 164));
        *///?}
        addDataSlots(be.dataAccess);
    }

    private static GeneratorBlockEntityBase resolve(Inventory inventory, RegistryFriendlyByteBuf data) {
        return (GeneratorBlockEntityBase) inventory.player.level().getBlockEntity(data.readBlockPos());
    }

    public int energy() { return be.dataAccess.get(0); }
    public int energyMax() { return be.dataAccess.get(1); }
    public int burn() { return be.dataAccess.get(2); }
    public int burnMax() { return be.dataAccess.get(3); }

    /** EU produced per tick right now, times 100 (fixed-point, 2 decimals). */
    public int outputPerTick() { return be.dataAccess.get(4); }

    public Block generatorBlock() { return be.getBlockState().getBlock(); }
    public int machineSlots() { return containerSlots; }
    public int status() { return be.dataAccess.get(6); }

    @Override
    public boolean stillValid(Player player) {
        return be.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        ItemStack moved = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);
        if (slot == null || !slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        moved = stack.copy();
        int invStart = containerSlots;
        int invEnd = containerSlots + 36;

        if (slotIndex < containerSlots) {
            if (!moveItemStackTo(stack, invStart, invEnd, true)) {
                return ItemStack.EMPTY;
            }
        } else if (this.slots.get(0).mayPlace(stack)) {
            if (!moveItemStackTo(stack, 0, 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (hasFuel && this.slots.get(1).mayPlace(stack)) {
            if (!moveItemStackTo(stack, 1, 2, false)) {
                return ItemStack.EMPTY;
            }
        } else {
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
}
