package me.almana.innovativecontraptions.content.tool;

import me.almana.innovativecontraptions.content.fluid.FluidCellItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
//? if <26.1
//import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;

public final class WrenchableSupport {
    public static Direction getHorizontalFacing(Level level, BlockPos pos, Property<Direction> facingProperty) {
        return level.getBlockState(pos).getValue(facingProperty);
    }

    public static boolean setHorizontalFacing(Level level, BlockPos pos, Property<Direction> facingProperty, Direction newFacing) {
        if (newFacing.getAxis().isVertical()) {
            return false;
        }
        level.setBlock(pos, level.getBlockState(pos).setValue(facingProperty, newFacing), Block.UPDATE_ALL);
        return true;
    }

    //? if >=26.1 {
    public static InteractionResult useItemOnResult(ItemStack stack) {
        return stack.getItem() instanceof WrenchTool || stack.getItem() instanceof FluidCellItem
                ? InteractionResult.PASS : InteractionResult.TRY_WITH_EMPTY_HAND;
    }
    //?} else {
    /*public static ItemInteractionResult useItemOnResult(ItemStack stack) {
        return stack.getItem() instanceof WrenchTool || stack.getItem() instanceof FluidCellItem
                ? ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
    *///?}

    //? if >=26.1 {
    public static InteractionResult passToItem() {
        return InteractionResult.PASS;
    }
    //?} else {
    /*public static ItemInteractionResult passToItem() {
        return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
    }
    *///?}

    private WrenchableSupport() {
    }
}
