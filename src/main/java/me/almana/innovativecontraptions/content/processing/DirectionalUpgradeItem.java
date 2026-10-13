package me.almana.innovativecontraptions.content.processing;

import java.util.function.Consumer;
import me.almana.innovativecontraptions.registry.ICDataComponents;
import me.almana.innovativecontraptions.registry.ICItems;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
//? if <26.1
//import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
//? if >=26.1
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public final class DirectionalUpgradeItem extends Item {
    public DirectionalUpgradeItem(Properties properties) { super(properties); }
    public static boolean allows(ItemStack stack, Direction side) {
        int selected = stack.getOrDefault(ICDataComponents.UPGRADE_SIDE.get(), -1);
        return selected == -1 || selected == side.ordinal();
    }
    @Override public InteractionResult useOn(UseOnContext context) {
        if (!context.isSecondaryUseActive()) return InteractionResult.PASS;
        if (!context.getLevel().isClientSide()) {
            int face = context.getClickedFace().ordinal();
            select(context.getItemInHand(), context.getPlayer(), context.getItemInHand().getOrDefault(ICDataComponents.UPGRADE_SIDE.get(), -1) == face ? -1 : face);
        }
        return InteractionResult.SUCCESS;
    }
    //? if >=26.1 {
    @Override public InteractionResult use(Level level, Player player, InteractionHand hand) {
    //?} else {
    /*@Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return new InteractionResultHolder<>(useResult(level, player, hand), player.getItemInHand(hand));
    }

    private InteractionResult useResult(Level level, Player player, InteractionHand hand) {
    *///?}
        if (!ProcessingFilter.advanced(player.getItemInHand(hand))) return InteractionResult.PASS;
        if (!level.isClientSide()) ((ServerPlayer) player).openMenu(
                new SimpleMenuProvider((id, inventory, user) -> new ProcessingFilterMenu(id, inventory, hand),
                        Component.translatable("gui.innovativecontraptions.routing.title")), buffer -> buffer.writeEnum(hand));
        return InteractionResult.SUCCESS;
    }
    private static void select(ItemStack stack, Player player, int direction) {
        stack.set(ICDataComponents.UPGRADE_SIDE.get(), direction);
        ((ServerPlayer) player).sendSystemMessage(label(stack), true);
    }
    private static Component label(ItemStack stack) {
        int direction = stack.getOrDefault(ICDataComponents.UPGRADE_SIDE.get(), -1);
        boolean output = stack.is(ICItems.EJECTOR_UPGRADE.get()) || stack.is(ICItems.FLUID_EJECTOR_UPGRADE.get())
                || stack.is(ICItems.ADVANCED_EJECTOR_UPGRADE.get());
        String key = "tooltip.innovativecontraptions.upgrade." + (output ? "output" : "input");
        if (direction < 0) return Component.translatable(key + "_any");
        return Component.translatable(key, Component.translatable("tooltip.innovativecontraptions.direction." + Direction.values()[direction].getName()));
    }
    //? if >=26.1 {
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display,
            Consumer<Component> lines, TooltipFlag flag) {
    //?} else {
    /*@Override public void appendHoverText(ItemStack stack, TooltipContext context, java.util.List<Component> components, TooltipFlag flag) {
        Consumer<Component> lines = components::add;
    *///?}
        lines.accept(label(stack));
        lines.accept(Component.translatable("tooltip.innovativecontraptions.upgrade_direction_controls"));
    }
}
