package me.almana.innovativecontraptions.content.equipment;

import me.almana.innovativecontraptions.content.energy.BatteryItem;
import me.almana.innovativecontraptions.content.energy.EuTier;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class PoweredMiningItem extends BatteryItem {
    private final int cost;
    private final float speed;
    private final boolean drill;
    private final boolean diamond;
    public PoweredMiningItem(Properties props, int capacity, EuTier tier, int transfer,
                             int cost, float speed, boolean drill, boolean diamond) {
        super(props, capacity, tier, true, transfer, false);
        this.cost = cost;
        this.speed = speed;
        this.drill = drill;
        this.diamond = diamond;
    }
    public int operationCost() { return cost; }
    public boolean drill() { return drill; }
    public boolean effective(BlockState state) {
        return drill ? state.is(BlockTags.MINEABLE_WITH_PICKAXE) || state.is(BlockTags.MINEABLE_WITH_SHOVEL)
                : state.is(BlockTags.MINEABLE_WITH_AXE) || state.is(BlockTags.LEAVES)
                || state.is(BlockTags.WOOL) || state.is(Blocks.COBWEB);
    }
    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return stored(stack) >= cost && effective(state) ? speed : 1;
    }
    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        return stored(stack) >= cost && effective(state)
                && !state.is(diamond ? BlockTags.INCORRECT_FOR_NETHERITE_TOOL : BlockTags.INCORRECT_FOR_IRON_TOOL);
    }
    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity owner) {
        if (!level.isClientSide() && state.getDestroySpeed(level, pos) != 0) PoweredUse.spend(stack, owner, cost);
        return true;
    }
    @Override
    //? if >=26.1 {
    public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        PoweredUse.spend(stack, attacker, cost);
    }
    //?} else {
    /*public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        PoweredUse.spend(stack, attacker, cost);
        return false;
    }
    *///?}
}
