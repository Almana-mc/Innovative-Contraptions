package me.almana.innovativecontraptions.content.gathering;

import java.util.List;
import me.almana.innovativecontraptions.content.energy.BatteryItem;
import me.almana.innovativecontraptions.content.equipment.DrillItem;
import me.almana.innovativecontraptions.registry.ICItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public final class MiningWork {
    public static final int REMOTE_INTERVAL = 20, REMOTE_SCAN_COST = 64, REMOTE_MINE_COST = 512;
    public static int drillDuration(ItemStack drill) { return drill.is(ICItems.IRIDIUM_DRILL.get()) ? 20 : drill.is(ICItems.DIAMOND_DRILL.get()) ? 50 : 200; }
    public static int drillPower(ItemStack drill) { return drill.is(ICItems.IRIDIUM_DRILL.get()) ? 200 : drill.is(ICItems.DIAMOND_DRILL.get()) ? 20 : 6; }
    public static void remote(GatheringBlockEntity machine) {
        if (machine.stopped()) { machine.status = 4; return; }
        if (machine.energy < REMOTE_MINE_COST) { machine.status = 1; return; }
        ItemStack scanner = machine.getItem(GatheringBlockEntity.SCANNER);
        if (!(scanner.getItem() instanceof OreProbeItem probe) || BatteryItem.stored(scanner) < REMOTE_SCAN_COST) { machine.status = 2; return; }
        if (++machine.clock < REMOTE_INTERVAL) return;
        machine.clock = 0; machine.status = 0;
        int width = probe.minerRadius() * 2 + 1;
        for (int index = 0; index < 5 * (1 + machine.countUpgrade(ICItems.OVERCLOCKER_UPGRADE.get())); index++) {
            int layer = machine.cursor / (width * width);
            int within = machine.cursor % (width * width);
            BlockPos pos = machine.getBlockPos().offset(within % width - probe.minerRadius(), -1 - layer, within / width - probe.minerRadius());
            if (machine.getLevel().isOutsideBuildHeight(pos)) { machine.status = 5; return; }
            if (!machine.getLevel().hasChunkAt(pos)) { machine.status = 6; return; }
            if (BatteryItem.stored(scanner) < REMOTE_SCAN_COST) return;
            machine.target = pos; machine.cursor++;
            BatteryItem.extract(scanner, REMOTE_SCAN_COST);
            BlockState state = machine.getLevel().getBlockState(pos);
            if (!mineable(machine, pos, state) || !machine.whitelist && !OreProbeItem.ore(state)) continue;
            List<ItemStack> drops = drops(machine, pos, state, remoteTool(machine));
            if (drops.isEmpty() || !filter(machine, state, drops)) continue;
            machine.energy -= REMOTE_MINE_COST;
            machine.getLevel().removeBlock(pos, false);
            drops.forEach(drop -> GatheringTransfers.drop(machine, drop));
            return;
        }
    }
    public static boolean filter(GatheringBlockEntity machine, BlockState state, List<ItemStack> drops) {
        boolean matched = false;
        for (int slot = GatheringBlockEntity.FILTER_START; slot < GatheringBlockEntity.OUTPUT; slot++) {
            ItemStack filter = machine.getItem(slot);
            if (filter.isEmpty()) continue;
            if (state.getBlock().asItem() == filter.getItem() || drops.stream().anyMatch(drop -> drop.is(filter.getItem()))) matched = true;
        }
        return machine.whitelist == matched;
    }
    private static ItemStack remoteTool(GatheringBlockEntity machine) {
        ItemStack tool = new ItemStack(Items.NETHERITE_PICKAXE);
        if (machine.silk) tool.enchant(machine.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SILK_TOUCH), 1);
        return tool;
    }
    public static boolean mineable(GatheringBlockEntity machine, BlockPos pos, BlockState state) {
        return !state.isAir() && state.getFluidState().isEmpty() && state.getDestroySpeed(machine.getLevel(), pos) >= 0
                && !state.hasBlockEntity() && !state.is(GatheringContent.PIPE.get());
    }
    private static List<ItemStack> drops(GatheringBlockEntity machine, BlockPos pos, BlockState state, ItemStack tool) {
        return Block.getDrops(state, (ServerLevel) machine.getLevel(), pos, machine.getLevel().getBlockEntity(pos), null, tool);
    }
    public static void bore(GatheringBlockEntity machine) {
        if (!(machine.getItem(0).getItem() instanceof DrillItem)) { retract(machine); return; }
        if (!machine.getItem(1).is(GatheringContent.PIPE_ITEM.get())) { machine.status = 2; return; }
        if (machine.depth < machine.getLevel().getMinY()) { machine.status = 5; return; }
        if (machine.target == null && !selectTarget(machine)) return;
        excavate(machine);
    }
    private static boolean selectTarget(GatheringBlockEntity machine) {
        BlockPos center = new BlockPos(machine.getBlockPos().getX(), machine.depth, machine.getBlockPos().getZ());
        ItemStack scanner = machine.getItem(GatheringBlockEntity.SCANNER);
        if (machine.depth < machine.getBlockPos().getY() && scanner.getItem() instanceof OreProbeItem probe) {
            if (machine.scanDepth != machine.depth) {
                if (BatteryItem.stored(scanner) < probe.scanCost()) { machine.status = 2; return false; }
                BatteryItem.extract(scanner, probe.scanCost()); machine.scanDepth = machine.depth;
            }
            for (BlockPos pos : BlockPos.betweenClosed(center.offset(-probe.minerRadius(), 0, -probe.minerRadius()), center.offset(probe.minerRadius(), 0, probe.minerRadius()))) {
                if (!machine.getLevel().hasChunkAt(pos)) continue;
                BlockState state = machine.getLevel().getBlockState(pos);
                if (OreProbeItem.ore(state) && mineable(machine, pos, state)) {
                    BlockPos path = firstObstruction(machine, center, pos);
                    if (path == null) continue;
                    machine.target = path; return true;
                }
            }
        }
        machine.target = center.below();
        return true;
    }
    private static BlockPos firstObstruction(GatheringBlockEntity machine, BlockPos center, BlockPos ore) {
        BlockPos pos = center;
        while (!pos.equals(ore)) {
            int dx = Integer.signum(ore.getX() - pos.getX()), dz = Integer.signum(ore.getZ() - pos.getZ());
            pos = dx != 0 ? pos.offset(dx, 0, 0) : pos.offset(0, 0, dz);
            BlockState state = machine.getLevel().getBlockState(pos);
            if (state.isAir()) continue;
            return state.getFluidState().isEmpty() && !mineable(machine, pos, state) ? null : pos;
        }
        return ore.immutable();
    }
    private static void excavate(GatheringBlockEntity machine) {
        var level = machine.getLevel(); BlockPos pos = machine.target;
        if (level.isOutsideBuildHeight(pos)) { machine.status = 5; return; }
        if (!level.hasChunkAt(pos)) { machine.status = 6; return; }
        BlockState state = level.getBlockState(pos);
        if (!state.getFluidState().isEmpty()) { machine.status = 7; return; }
        if (!state.isAir() && !mineable(machine, pos, state)) { machine.status = 3; machine.target = null; return; }
        ItemStack drill = machine.getItem(0);
        DrillItem item = (DrillItem) drill.getItem();
        if (!state.isAir() && !item.isCorrectToolForDrops(drill, state) && state.requiresCorrectToolForDrops()) { machine.status = 2; return; }
        int duration = state.isAir() ? 20 : drillDuration(drill);
        int power = state.isAir() ? 3 : drillPower(drill);
        int harvestCost = 2 * (machine.getBlockPos().getY() - pos.getY());
        if (machine.progress < duration) {
            if (machine.energy < power) { machine.status = 1; return; }
            machine.energy -= power; machine.progress++; machine.status = 0; return;
        }
        if (!state.isAir() && (machine.energy < harvestCost || BatteryItem.stored(drill) < item.operationCost())) { machine.status = 1; return; }
        if (!state.isAir()) {
            var drops = drops(machine, pos, state, drill); machine.energy -= harvestCost; BatteryItem.extract(drill, item.operationCost());
            level.removeBlock(pos, false); drops.forEach(drop -> GatheringTransfers.drop(machine, drop));
        }
        if (pos.getX() == machine.getBlockPos().getX() && pos.getZ() == machine.getBlockPos().getZ()) {
            level.setBlockAndUpdate(pos, GatheringContent.PIPE.get().defaultBlockState()); machine.getItem(1).shrink(1); machine.depth = pos.getY();
        }
        machine.target = null; machine.progress = 0; machine.status = 0;
    }
    private static void retract(GatheringBlockEntity machine) {
        machine.target = null; machine.scanDepth = Integer.MIN_VALUE;
        if (machine.depth >= machine.getBlockPos().getY()) { machine.status = 2; return; }
        if (machine.energy < 3) { machine.status = 1; return; }
        machine.energy -= 3;
        if (++machine.progress < 20) return;
        machine.progress = 0;
        BlockPos pos = new BlockPos(machine.getBlockPos().getX(), machine.depth++, machine.getBlockPos().getZ());
        if (machine.getLevel().getBlockState(pos).is(GatheringContent.PIPE.get())) {
            machine.getLevel().setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            ItemStack pipes = machine.getItem(1);
            if (pipes.isEmpty()) machine.setItem(1, new ItemStack(GatheringContent.PIPE_ITEM.get()));
            else if (pipes.is(GatheringContent.PIPE_ITEM.get()) && pipes.getCount() < pipes.getMaxStackSize()) pipes.grow(1);
            else GatheringTransfers.drop(machine, new ItemStack(GatheringContent.PIPE_ITEM.get()));
        }
    }
    private MiningWork() {}
}
