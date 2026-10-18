package me.almana.innovativecontraptions.content.agriculture;

import me.almana.innovativecontraptions.registry.ICBlocks;
import me.almana.innovativecontraptions.registry.ICItems;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
//? if <26.1
//import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public final class BarrelInteractions {
    //? if >=26.1 {
    public static InteractionResult use(FermentingBarrelBlockEntity barrel, Player player, InteractionHand hand, Direction side) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(ICItems.TREE_TAP.get()) && !barrel.tapped() && side.getAxis().isHorizontal()) {
            barrel.getLevel().setBlockAndUpdate(barrel.getBlockPos(), barrel.getBlockState().setValue(FermentingBarrelBlock.TAPPED, true)
                    .setValue(BlockStateProperties.HORIZONTAL_FACING, side));
            stack.consume(1, player); return InteractionResult.SUCCESS;
        }
        if (stack.is(ICItems.MUG.get()) && side == barrel.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING)) {
            ItemStack drink = barrel.draw();
            if (drink.isEmpty()) return InteractionResult.PASS;
            if (player.hasInfiniteMaterials()) { if (!player.addItem(drink)) player.drop(drink, false); }
            else player.setItemInHand(hand, drink);
            return InteractionResult.SUCCESS;
        }
        int limit = player.isShiftKeyDown() ? 1 : stack.getCount();
        int added = stack.is(Tags.Items.CROPS_SUGAR_CANE) ? barrel.addCane(limit) : barrel.addSolid(stack, limit);
        if (added > 0) { stack.consume(added, player); return InteractionResult.SUCCESS; }
        return water(barrel, player, hand) ? InteractionResult.SUCCESS : InteractionResult.TRY_WITH_EMPTY_HAND;
    }
    //?} else {
    /*public static ItemInteractionResult use(FermentingBarrelBlockEntity barrel, Player player, InteractionHand hand, Direction side) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(ICItems.TREE_TAP.get()) && !barrel.tapped() && side.getAxis().isHorizontal()) {
            barrel.getLevel().setBlockAndUpdate(barrel.getBlockPos(), barrel.getBlockState().setValue(FermentingBarrelBlock.TAPPED, true)
                    .setValue(BlockStateProperties.HORIZONTAL_FACING, side));
            stack.consume(1, player); return ItemInteractionResult.SUCCESS;
        }
        if (stack.is(ICItems.MUG.get()) && side == barrel.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING)) {
            ItemStack drink = barrel.draw();
            if (drink.isEmpty()) return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
            if (player.hasInfiniteMaterials()) { if (!player.addItem(drink)) player.drop(drink, false); }
            else player.setItemInHand(hand, drink);
            return ItemInteractionResult.SUCCESS;
        }
        int limit = player.isShiftKeyDown() ? 1 : stack.getCount();
        int added = stack.is(Tags.Items.CROPS_SUGAR_CANE) ? barrel.addCane(limit) : barrel.addSolid(stack, limit);
        if (added > 0) { stack.consume(added, player); return ItemInteractionResult.SUCCESS; }
        return water(barrel, player, hand) ? ItemInteractionResult.SUCCESS : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
    *///?}
    private static boolean water(FermentingBarrelBlockEntity barrel, Player player, InteractionHand hand) {
        if (barrel.opened() || barrel.rum() || barrel.water() == 32) return false;
        var handler = ItemAccess.forPlayerInteraction(player, hand).oneByOne().getCapability(Capabilities.Fluid.ITEM);
        if (handler == null) return false;
        var water = FluidResource.of(Fluids.WATER);
        int limit = player.isShiftKeyDown() ? 1 : 32 - barrel.water();
        try (var transaction = Transaction.openRoot()) {
            int amount = handler.extract(water, limit * 1000, transaction);
            if (amount == 0 || amount % 1000 != 0) return false;
            transaction.commit(); barrel.addWater(amount / 1000); return true;
        }
    }
    public static void leftClick(PlayerInteractEvent.LeftClickBlock event) {
        if (!(event.getLevel().getBlockEntity(event.getPos()) instanceof FermentingBarrelBlockEntity barrel)) return;
        event.setCanceled(true);
        if (event.getLevel().isClientSide()) return;
        if (barrel.tapped()) {
            barrel.discardUnit();
            event.getLevel().setBlockAndUpdate(event.getPos(), barrel.getBlockState().setValue(FermentingBarrelBlock.TAPPED, false));
            Block.popResource(event.getLevel(), event.getPos(), new ItemStack(ICItems.TREE_TAP.get()));
        } else {
            event.getLevel().setBlockAndUpdate(event.getPos(), ICBlocks.WOODEN_SCAFFOLD.get().defaultBlockState());
            Block.popResource(event.getLevel(), event.getPos(), new ItemStack(ICItems.FERMENTING_BARREL.get()));
        }
    }
    private BarrelInteractions() {}
}
