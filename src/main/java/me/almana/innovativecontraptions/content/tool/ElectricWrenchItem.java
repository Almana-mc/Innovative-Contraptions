package me.almana.innovativecontraptions.content.tool;

import me.almana.innovativecontraptions.content.energy.BatteryItem;
import me.almana.innovativecontraptions.content.energy.EuTier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;

public class ElectricWrenchItem extends BatteryItem implements WrenchTool {
    @Override
    public boolean canSupplyEnergy() { return false; }
    public ElectricWrenchItem(Properties properties, int capacity, EuTier tier, boolean rechargeable) {
        super(properties, capacity, tier, rechargeable);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return WrenchTool.super.useOn(context);
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        return state.getBlock() instanceof Wrenchable || super.isCorrectToolForDrops(stack, state);
    }

    @Override
    public boolean payRotateCost(ItemStack stack, Player player, InteractionHand hand) {
        return spend(stack, WrenchCost.ELECTRIC_ROTATE_EU);
    }

    @Override
    public boolean payPickupCost(ItemStack stack, Player player, InteractionHand hand) {
        return spend(stack, WrenchCost.ELECTRIC_PICKUP_EU);
    }

    private static boolean spend(ItemStack stack, int cost) {
        int current = BatteryItem.stored(stack);
        if (!WrenchCost.canAfford(current, cost)) {
            return false;
        }
        BatteryItem.setStored(stack, current - cost);
        return true;
    }
}
