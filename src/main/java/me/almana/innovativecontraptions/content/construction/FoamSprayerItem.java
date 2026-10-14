package me.almana.innovativecontraptions.content.construction;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import me.almana.innovativecontraptions.content.equipment.EquipmentControls;
import me.almana.innovativecontraptions.content.equipment.EquipmentData;
import me.almana.innovativecontraptions.registry.ICBlocks;
import me.almana.innovativecontraptions.registry.ICItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
//? if <26.1
//import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
//? if >=26.1
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public final class FoamSprayerItem extends Item {
    public static final int CAPACITY = 8_000, PER_BLOCK = 100;

    public FoamSprayerItem(Properties properties) { super(properties.stacksTo(1)); }

    public static int foam(ItemStack stack) { return stack.getOrDefault(EquipmentData.FOAM.get(), 0); }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null || EquipmentControls.mode(player)) return InteractionResult.PASS;
        Level level = context.getLevel();
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        ItemStack pack = player.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack source = pack.is(ICItems.CF_PACK.get()) && foam(pack) >= PER_BLOCK ? pack : context.getItemInHand();
        boolean single = context.getItemInHand().getOrDefault(EquipmentData.MODE.get(), 0) == 1;
        int max = Math.min(single ? 1 : 10, foam(source) / PER_BLOCK);
        BlockPos clicked = context.getClickedPos();
        boolean scaffold = scaffold(level.getBlockState(clicked));
        BlockPos start = scaffold ? clicked : clicked.relative(context.getClickedFace());
        List<BlockPos> targets = collect(level, player, start, scaffold, player.getNearestViewDirection().getOpposite(), max);
        if (targets.isEmpty()) return InteractionResult.FAIL;
        targets.forEach(pos -> place(level, pos));
        source.set(EquipmentData.FOAM.get(), foam(source) - targets.size() * PER_BLOCK);
        return InteractionResult.SUCCESS;
    }

    @Override
    //? if >=26.1 {
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
    //?} else {
    /*public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return new InteractionResultHolder<>(toggle(level, player, hand), player.getItemInHand(hand));
    }

    private InteractionResult toggle(Level level, Player player, InteractionHand hand) {
    *///?}
        if (!EquipmentControls.mode(player)) return InteractionResult.PASS;
        if (!level.isClientSide()) {
            ItemStack stack = player.getItemInHand(hand);
            int mode = 1 - stack.getOrDefault(EquipmentData.MODE.get(), 0);
            stack.set(EquipmentData.MODE.get(), mode);
            EquipmentControls.message(player, mode == 1 ? "sprayer_single" : "sprayer_normal");
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    //? if >=26.1 {
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
    //?} else {
    /*public void appendHoverText(ItemStack stack, TooltipContext context, java.util.List<Component> components, TooltipFlag flag) {
        Consumer<Component> tooltip = components::add;
    *///?}
        tooltip.accept(Component.translatable("tooltip.innovativecontraptions.fluid_cell_amount", foam(stack), CAPACITY));
    }

    private static List<BlockPos> collect(Level level, Player player, BlockPos start, boolean scaffold, Direction back, int max) {
        List<BlockPos> found = new ArrayList<>();
        Set<BlockPos> seen = new HashSet<>();
        Deque<BlockPos> queue = new ArrayDeque<>(List.of(start));
        while (!queue.isEmpty() && found.size() < max) {
            BlockPos pos = queue.poll();
            BlockState state = level.getBlockState(pos);
            if (!seen.add(pos) || !level.mayInteract(player, pos) || (scaffold ? !scaffold(state) : !state.canBeReplaced())) continue;
            found.add(pos);
            for (Direction direction : Direction.values()) if (direction != back) queue.add(pos.relative(direction));
        }
        return found;
    }

    private static boolean scaffold(BlockState state) {
        return state.is(ICBlocks.WOODEN_SCAFFOLD.get()) || state.is(ConstructionContent.IRON_SCAFFOLD.get());
    }

    private static void place(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.is(ICBlocks.WOODEN_SCAFFOLD.get())) Block.popResource(level, pos, new ItemStack(ICBlocks.WOODEN_SCAFFOLD.get()));
        level.setBlockAndUpdate(pos, ConstructionContent.FOAM.get().defaultBlockState()
                .setValue(FoamBlock.REINFORCED, state.is(ConstructionContent.IRON_SCAFFOLD.get())));
    }
}
