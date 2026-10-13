package me.almana.innovativecontraptions.content.energy.net;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.util.HashSet;
import java.util.Set;
import me.almana.innovativecontraptions.content.energy.cable.CableBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class EuGrid {
    private final Set<BlockPos> endpoints = new HashSet<>();
    private final List<EuNetwork> networks = new ArrayList<>();
    private final Map<Long, EuNetwork> byCable = new HashMap<>();
    private final LongOpenHashSet dirtySeeds = new LongOpenHashSet();

    public void markDirty(BlockPos pos) {
        dirtySeeds.add(pos.asLong());
        endpoints.add(pos.immutable());
        for (Direction dir : Direction.values()) {
            dirtySeeds.add(pos.relative(dir).asLong());
            endpoints.add(pos.relative(dir).immutable());
        }
    }

    public void tick(ServerLevel level, Map<Long, Integer> debugOut) {
        if (!dirtySeeds.isEmpty()) {
            rebuildDirty(level);
        }
        EuTickBudget budget = new EuTickBudget();
        EuShockExposure exposure = new EuShockExposure();
        endpoints.removeIf(pos -> level.hasChunkAt(pos) && level.getBlockEntity(pos) == null);
        EuAdjacent.distribute(level, endpoints, budget);
        for (EuNetwork network : List.copyOf(networks)) {
            if (network.hasChargedSource(level)) {
                network.distribute(level, debugOut, budget, exposure);
            }
        }
        exposure.apply(level);
    }

    private void rebuildDirty(ServerLevel level) {
        LongOpenHashSet seeds = new LongOpenHashSet(dirtySeeds);
        dirtySeeds.clear();

        Map<EuNetwork, Boolean> stale = new IdentityHashMap<>();
        for (long seed : seeds) {
            EuNetwork existing = byCable.get(seed);
            if (existing != null) {
                stale.put(existing, Boolean.TRUE);
            }
        }
        for (EuNetwork network : stale.keySet()) {
            network.cables().forEach((long c) -> byCable.remove(c));
            networks.remove(network);
        }

        for (long seed : seeds) {
            if (byCable.containsKey(seed)) {
                continue;
            }
            BlockPos pos = BlockPos.of(seed);
            if (!level.hasChunkAt(pos)) continue;
            if (!(level.getBlockState(pos).getBlock() instanceof CableBlock)) {
                continue;
            }
            EuNetwork network = new EuNetwork();
            network.build(level, pos);
            if (!network.isEmpty()) {
                networks.add(network);
                network.cables().forEach((long c) -> byCable.put(c, network));
            }
        }
    }
}
