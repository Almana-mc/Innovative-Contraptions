package me.almana.innovativecontraptions.content.energy.net;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import me.almana.innovativecontraptions.content.energy.EuSink;
import me.almana.innovativecontraptions.content.energy.EuSource;
import me.almana.innovativecontraptions.content.energy.cable.CableBlock;
import me.almana.innovativecontraptions.registry.IcCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class EuNetwork {
    private final LongOpenHashSet cables = new LongOpenHashSet();
    private final List<Endpoint> sources = new ArrayList<>();
    private final List<Endpoint> sinks = new ArrayList<>();

    private record Endpoint(long cablePos, Direction fromCable, BlockPos blockPos) {
    }

    public LongOpenHashSet cables() {
        return cables;
    }

    public boolean isEmpty() {
        return cables.isEmpty();
    }

    public void build(ServerLevel level, BlockPos seed) {
        cables.clear();
        sources.clear();
        sinks.clear();

        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        queue.add(seed.immutable());
        cables.add(seed.asLong());

        while (!queue.isEmpty()) {
            BlockPos pos = queue.poll();
            for (Direction dir : Direction.values()) {
                BlockPos next = pos.relative(dir);
                if (!level.hasChunkAt(next)) continue;
                BlockState neighbor = level.getBlockState(next);
                if (neighbor.getBlock() instanceof CableBlock) {
                    if (cables.add(next.asLong())) {
                        queue.add(next.immutable());
                    }
                } else {
                    EuSource source = IcCapabilities.EU_SOURCE.getCapability(level, next, null, null, dir.getOpposite());
                    if (source != null) {
                        sources.add(new Endpoint(pos.asLong(), dir.getOpposite(), next.immutable()));
                    }
                    EuSink sink = IcCapabilities.EU_SINK.getCapability(level, next, null, null, dir.getOpposite());
                    if (sink != null) {
                        sinks.add(new Endpoint(pos.asLong(), dir.getOpposite(), next.immutable()));
                    }
                }
            }
        }
    }

    public boolean hasChargedSource(ServerLevel level) {
        for (Endpoint endpoint : sources) {
            if (!level.hasChunkAt(endpoint.blockPos)) continue;
            EuSource source = IcCapabilities.EU_SOURCE.getCapability(level, endpoint.blockPos, null, null, endpoint.fromCable);
            if (source != null && source.availableEu() > 0) {
                return true;
            }
        }
        return false;
    }

    public void distribute(ServerLevel level, Map<Long, Integer> debugOut) {
        distribute(level, debugOut, new EuTickBudget());
    }

    public void distribute(ServerLevel level, Map<Long, Integer> debugOut, EuTickBudget budget) {
        EuShockExposure exposure = new EuShockExposure();
        distribute(level, debugOut, budget, exposure);
        exposure.apply(level);
    }

    void distribute(ServerLevel level, Map<Long, Integer> debugOut, EuTickBudget budget, EuShockExposure exposure) {
        if (sources.isEmpty() || sinks.isEmpty()) return;
        if (debugOut != null) cables.forEach((long cable) -> debugOut.putIfAbsent(cable, 0));
        LongOpenHashSet toMelt = new LongOpenHashSet();
        for (Endpoint endpoint : sources) distributeSource(level, endpoint, budget, exposure, toMelt, debugOut);
        meltCables(level, toMelt);
    }

    private void distributeSource(ServerLevel level, Endpoint endpoint, EuTickBudget budget,
                                  EuShockExposure exposure, LongOpenHashSet toMelt, Map<Long, Integer> debugOut) {
        if (!level.hasChunkAt(endpoint.blockPos)) return;
        EuSource source = IcCapabilities.EU_SOURCE.getCapability(level, endpoint.blockPos, null, null, endpoint.fromCable);
        if (source == null || budget.packet(endpoint.blockPos, source) <= 0) return;
        Map<Long, Double> lossTo = new HashMap<>();
        Map<Long, Integer> capTo = new HashMap<>();
        Map<Long, Long> prev = new HashMap<>();
        dijkstra(level, endpoint.cablePos, lossTo, capTo, prev);
        for (Endpoint target : sinks) {
            if (source instanceof BlockEntity entity && entity.isRemoved()) return;
            if (!level.hasChunkAt(target.blockPos)) continue;
            if (target.blockPos.equals(endpoint.blockPos) || !lossTo.containsKey(target.cablePos)) continue;
            EuSink sink = IcCapabilities.EU_SINK.getCapability(level, target.blockPos, null, null, target.fromCable);
            if (sink == null) continue;
            for (int index = 0; index < source.packetsPerTick(); index++) {
                int loss = (int) Math.ceil(lossTo.get(target.cablePos));
                int voltage = budget.packet(endpoint.blockPos, source) - loss;
                int arriving = Math.min(sink.demandedEu(), voltage);
                if (arriving <= 0) break;
                int conducted = arriving + loss;
                boolean melted = pathOverloaded(level, target.cablePos, conducted, prev, toMelt);
                if (melted || voltage > sink.maxInputVoltage()) {
                    if (melted || EnergySettings.MACHINE_EXPLOSIONS.get()) {
                        budget.spent(endpoint.blockPos, source.extractEu(conducted));
                        if (!melted) EuOverload.destroy(level, target.blockPos);
                    }
                    break;
                }
                int delivered = sink.receiveEu(conducted, arriving);
                if (delivered <= 0) break;
                budget.spent(endpoint.blockPos, source.extractEu(delivered + loss));
                markPath(target.cablePos, delivered + loss, prev, capTo, toMelt);
                exposure.record(level, endpoint.blockPos, target.blockPos, target.cablePos, delivered + loss, prev);
                if (debugOut != null) EuFlow.accumulatePath(target.cablePos, delivered, lossTo, prev, debugOut);
            }
        }
    }

    private boolean pathOverloaded(ServerLevel level, long start, int packet, Map<Long, Long> prev, LongOpenHashSet melted) {
        Long node = start;
        boolean overloaded = false;
        while (node != null) {
            if (melted.contains(node)) return true;
            if (packet > cableMaxVoltage(level, node) && EnergySettings.CABLE_MELTING.get()) {
                melted.add(node);
                overloaded = true;
            }
            node = prev.get(node);
        }
        return overloaded;
    }
    private void dijkstra(ServerLevel level, long start, Map<Long, Double> lossTo, Map<Long, Integer> capTo, Map<Long, Long> prev) {
        PriorityQueue<long[]> frontier = new PriorityQueue<>((a, b) -> Double.compare(
                Double.longBitsToDouble(a[1]), Double.longBitsToDouble(b[1])));
        lossTo.put(start, 0.0);
        capTo.put(start, cableMaxVoltage(level, start));
        frontier.add(new long[]{start, Double.doubleToLongBits(0.0)});

        while (!frontier.isEmpty()) {
            long[] head = frontier.poll();
            long pos = head[0];
            double dist = Double.longBitsToDouble(head[1]);
            if (dist > lossTo.getOrDefault(pos, Double.MAX_VALUE)) {
                continue;
            }
            BlockPos blockPos = BlockPos.of(pos);
            for (Direction dir : Direction.values()) {
                long next = blockPos.relative(dir).asLong();
                if (!cables.contains(next) || !level.hasChunkAt(BlockPos.of(next))) {
                    continue;
                }
                double edge = cableLoss(level, next);
                double newDist = dist + edge;
                if (newDist < lossTo.getOrDefault(next, Double.MAX_VALUE)) {
                    lossTo.put(next, newDist);
                    capTo.put(next, Math.min(capTo.getOrDefault(pos, Integer.MAX_VALUE), cableMaxVoltage(level, next)));
                    prev.put(next, pos);
                    frontier.add(new long[]{next, Double.doubleToLongBits(newDist)});
                }
            }
        }
    }

    private void markPath(long sinkCable, int voltage, Map<Long, Long> prev,
                          Map<Long, Integer> capTo, LongOpenHashSet toMelt) {
        Long node = sinkCable;
        while (node != null) {
            if (voltage > capTo.getOrDefault(node, Integer.MAX_VALUE) && EnergySettings.CABLE_MELTING.get()) {
                toMelt.add(node);
            }
            node = prev.get(node);
        }
    }

    private void meltCables(ServerLevel level, LongOpenHashSet toMelt) {
        for (long packed : toMelt) {
            level.destroyBlock(BlockPos.of(packed), false, null, 512);
        }
    }

    private int cableMaxVoltage(ServerLevel level, long packed) {
        return level.getBlockState(BlockPos.of(packed)).getBlock() instanceof CableBlock cable
                ? cable.spec().maxVoltage() + (cable.spec().maxVoltage() == 2048 ? 1 : 0) : Integer.MAX_VALUE;
    }

    private double cableLoss(ServerLevel level, long packed) {
        return level.getBlockState(BlockPos.of(packed)).getBlock() instanceof CableBlock cable
                ? cable.spec().lossPerBlock() : 0.0;
    }
}
