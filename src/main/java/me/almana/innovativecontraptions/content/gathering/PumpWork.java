package me.almana.innovativecontraptions.content.gathering;

import java.util.ArrayDeque;
import java.util.HashSet;
import me.almana.innovativecontraptions.content.processing.ProcessingUpgrades;
import me.almana.innovativecontraptions.registry.ICItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
//? if >=26.1 {
import net.minecraft.world.level.gamerules.GameRules;
//?} else
//import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public final class PumpWork {
    public static final int BASE_DURATION = 20, BASE_POWER = 1, BUCKET = 1000;
    public static int duration(GatheringBlockEntity machine) { return ProcessingUpgrades.ticks(BASE_DURATION, machine.countUpgrade(ICItems.OVERCLOCKER_UPGRADE.get())); }
    public static int power(GatheringBlockEntity machine) { return ProcessingUpgrades.energy(BASE_POWER, machine.countUpgrade(ICItems.OVERCLOCKER_UPGRADE.get())); }
    public static void tick(GatheringBlockEntity machine) {
        GatheringTransfers.fillContainer(machine);
        if (machine.stopped()) { machine.status = 4; return; }
        if (machine.fluidAmount() >= GatheringBlockEntity.PUMP_TANK) { machine.status = 3; return; }
        if (!draw(machine, true)) { machine.status = 2; return; }
        if (machine.energy < (long) power(machine) * duration(machine)) { machine.status = 1; return; }
        if (machine.progress < duration(machine)) { machine.energy -= power(machine); machine.progress++; machine.status = 0; return; }
        if (draw(machine, false)) { machine.progress = 0; machine.status = 0; }
        GatheringTransfers.fillContainer(machine);
    }
    private static BlockPos intake(GatheringBlockEntity machine) {
        for (Direction side : Direction.values()) {
            if (machine.getLevel().getBlockEntity(machine.getBlockPos().relative(side)) instanceof GatheringBlockEntity bore
                    && !bore.pump() && !bore.terrain() && !bore.advanced() && bore.status == 7 && bore.target != null) return bore.target;
        }
        return machine.getBlockPos().relative(machine.getBlockState().getValue(BlockStateProperties.FACING));
    }
    public static boolean draw(GatheringBlockEntity machine, boolean simulate) {
        BlockPos pos = intake(machine);
        if (!machine.getLevel().hasChunkAt(pos)) return false;
        var adjacent = machine.getLevel().getCapability(Capabilities.Fluid.BLOCK, pos, machine.getBlockState().getValue(BlockStateProperties.FACING).getOpposite());
        if (adjacent != null) {
            machine.acceptingFluid = true;
            try (var transaction = Transaction.openRoot()) {
                int moved = ResourceHandlerUtil.move(adjacent, machine.tank, resource -> true, BUCKET, transaction);
                if (!simulate && moved > 0) transaction.commit();
                return moved > 0;
            } finally { machine.acceptingFluid = false; }
        }
        if (GatheringBlockEntity.PUMP_TANK - machine.fluidAmount() < BUCKET) return false;
        BlockPos source = source(machine, pos);
        if (source == null) return false;
        var fluid = machine.getLevel().getFluidState(source).getType();
        var resource = FluidResource.of(fluid);
        if (machine.fluidAmount() > 0 && !machine.tank.getResource(0).equals(resource)) return false;
        if (!simulate) {
            if (!infiniteWater(machine, source)) machine.getLevel().setBlockAndUpdate(source, Blocks.AIR.defaultBlockState());
            machine.tank.set(0, resource, machine.fluidAmount() + BUCKET);
        }
        return true;
    }
    private static BlockPos source(GatheringBlockEntity machine, BlockPos start) {
        var fluid = machine.getLevel().getFluidState(start);
        if (fluid.isEmpty()) return null;
        ArrayDeque<BlockPos> queue = new ArrayDeque<>(); HashSet<BlockPos> visited = new HashSet<>(); queue.add(start); visited.add(start);
        while (!queue.isEmpty() && visited.size() <= 4096) {
            var pos = queue.removeFirst(); var current = machine.getLevel().getFluidState(pos);
            if (current.isSource()) return pos;
            for (Direction direction : Direction.values()) {
                var next = pos.relative(direction);
                if (!machine.getLevel().hasChunkAt(next) || !visited.add(next)) continue;
                if (machine.getLevel().getFluidState(next).getType().isSame(fluid.getType())) queue.add(next);
            }
        }
        return null;
    }
    private static boolean infiniteWater(GatheringBlockEntity machine, BlockPos pos) {
        //? if >=26.1 {
        if (!((ServerLevel) machine.getLevel()).getGameRules().get(GameRules.WATER_SOURCE_CONVERSION)) return false;
        //?} else
        //if (!((ServerLevel) machine.getLevel()).getGameRules().getBoolean(GameRules.RULE_WATER_SOURCE_CONVERSION)) return false;
        if (machine.getLevel().getFluidState(pos).getType() != Fluids.WATER) return false;
        int neighbors = 0;
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            var fluid = machine.getLevel().getFluidState(pos.relative(direction));
            if (fluid.isSource() && fluid.getType() == Fluids.WATER) neighbors++;
        }
        return neighbors >= 2 && (machine.getLevel().getBlockState(pos.below()).isSolid() || machine.getLevel().getFluidState(pos.below()).isSource());
    }
    private PumpWork() {}
}
