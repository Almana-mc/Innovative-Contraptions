package me.almana.innovativecontraptions.content.fluid;

import me.almana.innovativecontraptions.registry.ICItems;
import me.almana.innovativecontraptions.registry.ICDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
//? if <26.1
//import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
//? if >=26.1
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.TooltipFlag;
//? if >=26.1
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import java.util.function.Consumer;

public final class FluidCellItem extends Item {
    private final Fluid fluid;

    public FluidCellItem(Properties properties, Fluid fluid) {
        super(properties);
        this.fluid = fluid;
    }

    public Fluid fluid() {
        return fluid;
    }

    @Override
    //? if >=26.1 {
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
    //?} else {
    /*public void appendHoverText(ItemStack stack, TooltipContext context, java.util.List<Component> components, TooltipFlag flag) {
        Consumer<Component> tooltip = components::add;
    *///?}
        int amount = fluid == Fluids.EMPTY ? 0 : stack.getOrDefault(ICDataComponents.FLUID_AMOUNT.get(), FluidCellContents.CAPACITY);
        tooltip.accept(Component.translatable("tooltip.innovativecontraptions.fluid_cell_amount", amount, FluidCellContents.CAPACITY));
    }

    //? if >=26.1 {
    @Override
    public ItemStackTemplate getCraftingRemainder(ItemInstance instance) {
    //?} else {
    /*@Override
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return getCraftingRemainder(stack) != null;
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack stack) {
        ItemStackTemplate remainder = getCraftingRemainder(stack);
        return remainder == null ? ItemStack.EMPTY : remainder.create();
    }

    private ItemStackTemplate getCraftingRemainder(ItemStack instance) {
    *///?}
        return fluid == Fluids.EMPTY ? null : new ItemStackTemplate(ICItems.EMPTY_CELL.get());
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) return InteractionResult.PASS;
        if (context.getLevel().isClientSide()) {
            return context.getLevel().getCapability(Capabilities.Fluid.BLOCK, context.getClickedPos(), context.getClickedFace()) != null
                    ? InteractionResult.SUCCESS : InteractionResult.PASS;
        }
        return FluidUtil.interactWithFluidHandler(player, context.getHand(), context.getLevel(),
                context.getClickedPos(), context.getClickedFace(), null)
                ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }

    @Override
    //? if >=26.1 {
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
    //?} else {
    /*public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return new InteractionResultHolder<>(useResult(level, player, hand), player.getItemInHand(hand));
    }

    private InteractionResult useResult(Level level, Player player, InteractionHand hand) {
    *///?}
        var hit = getPlayerPOVHitResult(level, player,
                fluid == Fluids.EMPTY ? ClipContext.Fluid.SOURCE_ONLY : ClipContext.Fluid.NONE);
        if (hit.getType() != HitResult.Type.BLOCK) return InteractionResult.PASS;
        BlockPos pos = hit.getBlockPos();
        BlockPos target = pos.relative(hit.getDirection());
        if (!level.mayInteract(player, pos) || !player.mayUseItemAt(target, hit.getDirection(), player.getItemInHand(hand))) {
            return InteractionResult.FAIL;
        }
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        var handler = ItemAccess.forPlayerInteraction(player, hand).oneByOne().getCapability(Capabilities.Fluid.ITEM);
        if (fluid == Fluids.EMPTY) {
            return FluidUtil.tryPickupFluid(handler, player, level, pos, hit.getDirection(), null).isEmpty()
                    ? InteractionResult.FAIL : InteractionResult.SUCCESS;
        }
        var clicked = level.getBlockState(pos);
        if (!player.isShiftKeyDown() && clicked.getBlock() instanceof LiquidBlockContainer container
                && container.canPlaceLiquid(player, level, pos, clicked, fluid)) target = pos;
        return FluidUtil.tryPlaceFluid(handler, player, level, target, true, null).isEmpty()
                ? InteractionResult.FAIL : InteractionResult.SUCCESS;
    }
}
