package me.almana.innovativecontraptions.content.agriculture;

import me.almana.innovativecontraptions.content.energy.BatteryItem;
import me.almana.innovativecontraptions.registry.ICItems;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
//? if <26.1
//import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public final class CropInteractions {
    //? if >=26.1 {
    public static InteractionResult use(CropPlotBlockEntity crop, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(ICItems.CROP_STICKS.get()) && crop.cross()) { stack.consume(1, player); return InteractionResult.SUCCESS; }
        CropGenome seed = stack.getOrDefault(FarmingContent.GENOME.get(), CropSpecies.baseSeed(stack));
        if (crop.plant(seed)) { stack.consume(1, player); return InteractionResult.SUCCESS; }
        if (stack.is(ICItems.WEEDING_TROWEL.get()) && crop.species() == CropSpecies.WEED) {
            Block.popResource(crop.getLevel(), crop.getBlockPos(), new ItemStack(ICItems.WEED.get(), crop.age()));
            crop.clearPlant(); return InteractionResult.SUCCESS;
        }
        if (stack.is(ICItems.FERTILIZER.get())) {
            crop.fertilize(false); stack.consume(1, player); return InteractionResult.SUCCESS;
        }
        if (stack.is(ICItems.CROP_ANALYZER.get())) { report(crop, player, stack); return InteractionResult.SUCCESS; }
        if (fluid(crop, player, hand)) return InteractionResult.SUCCESS;
        if (stack.is(Items.BONE_MEAL)) return InteractionResult.FAIL;
        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }
    //?} else {
    /*public static ItemInteractionResult use(CropPlotBlockEntity crop, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(ICItems.CROP_STICKS.get()) && crop.cross()) { stack.consume(1, player); return ItemInteractionResult.SUCCESS; }
        CropGenome seed = stack.getOrDefault(FarmingContent.GENOME.get(), CropSpecies.baseSeed(stack));
        if (crop.plant(seed)) { stack.consume(1, player); return ItemInteractionResult.SUCCESS; }
        if (stack.is(ICItems.WEEDING_TROWEL.get()) && crop.species() == CropSpecies.WEED) {
            Block.popResource(crop.getLevel(), crop.getBlockPos(), new ItemStack(ICItems.WEED.get(), crop.age()));
            crop.clearPlant(); return ItemInteractionResult.SUCCESS;
        }
        if (stack.is(ICItems.FERTILIZER.get())) {
            crop.fertilize(false); stack.consume(1, player); return ItemInteractionResult.SUCCESS;
        }
        if (stack.is(ICItems.CROP_ANALYZER.get())) { report(crop, player, stack); return ItemInteractionResult.SUCCESS; }
        if (fluid(crop, player, hand)) return ItemInteractionResult.SUCCESS;
        if (stack.is(Items.BONE_MEAL)) return ItemInteractionResult.FAIL;
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
    *///?}
    private static boolean fluid(CropPlotBlockEntity crop, Player player, InteractionHand hand) {
        var handler = ItemAccess.forPlayerInteraction(player, hand).oneByOne().getCapability(Capabilities.Fluid.ITEM);
        if (handler == null) return false;
        for (int index = 0; index < handler.size(); index++) {
            var fluid = handler.getResource(index);
            boolean water = fluid.getFluid() == Fluids.WATER;
            if (!water && fluid.getFluid() != FarmingContent.TREATMENT.get()) continue;
            int amount = Math.max(0, (water ? 200 - crop.water() : 100 - crop.treatment()));
            if (amount == 0) return true;
            try (var transaction = Transaction.openRoot()) {
                int drained = handler.extract(index, fluid, amount, transaction);
                if (drained == 0) return false;
                transaction.commit();
                if (water) crop.hydrate(drained); else crop.protect(drained, false);
                return true;
            }
        }
        return false;
    }
    private static void report(CropPlotBlockEntity crop, Player player, ItemStack stack) {
        if (BatteryItem.extract(stack, 900) == 0) return;
        if (player instanceof ServerPlayer serverPlayer) serverPlayer.sendSystemMessage(Component.translatable("message.innovativecontraptions.crop_report",
                Component.translatable("crop.innovativecontraptions." + crop.species().id()), crop.age(), crop.species().stages,
                crop.nutrients(), crop.water(), crop.treatment(), crop.progress()), false);
    }
    public static void pick(PlayerInteractEvent.LeftClickBlock event) {
        if (event.getEntity().isShiftKeyDown() || !(event.getLevel().getBlockEntity(event.getPos()) instanceof CropPlotBlockEntity crop) || crop.empty()) return;
        event.setCanceled(true);
        if (!event.getLevel().isClientSide()) crop.pick().forEach(stack -> Block.popResource(event.getLevel(), event.getPos(), stack));
    }
    private CropInteractions() {}
}
