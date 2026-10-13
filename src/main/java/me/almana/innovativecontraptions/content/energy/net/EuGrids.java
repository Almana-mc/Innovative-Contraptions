package me.almana.innovativecontraptions.content.energy.net;

import me.almana.innovativecontraptions.content.energy.debug.EuDebugPayload;
import me.almana.innovativecontraptions.content.energy.debug.EuDebugState;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class EuGrids {
    private static final Map<ResourceKey<Level>, EuGrid> GRIDS = new HashMap<>();

    public static void register() {
        NeoForge.EVENT_BUS.addListener(EuGrids::onServerTick);
        NeoForge.EVENT_BUS.addListener(EuGrids::onLevelUnload);
    }

    public static void markDirty(LevelAccessor level, BlockPos pos) {
        if (level instanceof ServerLevel serverLevel) {
            GRIDS.computeIfAbsent(serverLevel.dimension(), key -> new EuGrid()).markDirty(pos);
        }
    }

    private static int tickCounter;

    private static void onServerTick(ServerTickEvent.Post event) {
        boolean debug = EuDebugState.any();
        tickCounter++;
        for (ServerLevel level : event.getServer().getAllLevels()) {
            EuGrid grid = GRIDS.get(level.dimension());
            if (grid == null) {
                continue;
            }
            Map<Long, Integer> snapshot = debug ? new HashMap<>() : null;
            grid.tick(level, snapshot);
            if (snapshot != null && tickCounter % EuDebugState.SEND_INTERVAL_TICKS == 0) {
                sendSnapshot(level, snapshot);
            }
        }
    }

    private static void sendSnapshot(ServerLevel level, Map<Long, Integer> snapshot) {
        int viewBlocks = level.getServer().getPlayerList().getViewDistance() * 16;
        long viewSq = (long) viewBlocks * viewBlocks;
        for (UUID id : EuDebugState.SUBSCRIBERS) {
            ServerPlayer player = level.getServer().getPlayerList().getPlayer(id);
            if (player == null || player.level() != level) {
                continue;
            }
            BlockPos eye = player.blockPosition();
            List<Long> positions = new ArrayList<>();
            List<Integer> throughput = new ArrayList<>();
            for (Map.Entry<Long, Integer> e : snapshot.entrySet()) {
                BlockPos pos = BlockPos.of(e.getKey());
                if (eye.distSqr(pos) <= viewSq) {
                    positions.add(e.getKey());
                    throughput.add(e.getValue());
                }
            }
            PacketDistributor.sendToPlayer(player, new EuDebugPayload(positions, throughput));
        }
    }

    private static void onLevelUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            GRIDS.remove(serverLevel.dimension());
        }
    }

    private EuGrids() {
    }
}
