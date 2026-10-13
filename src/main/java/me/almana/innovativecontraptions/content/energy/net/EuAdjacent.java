package me.almana.innovativecontraptions.content.energy.net;

import me.almana.innovativecontraptions.registry.IcCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import java.util.Set;

public final class EuAdjacent {
    public static void distribute(ServerLevel level, Set<BlockPos> positions, EuTickBudget budget) {
        for (BlockPos pos : Set.copyOf(positions)) {
            if (!level.hasChunkAt(pos)) continue;
            for (Direction side : Direction.values()) send(level, pos, side, budget);
        }
    }
    private static void send(ServerLevel level, BlockPos pos, Direction side, EuTickBudget budget) {
        BlockPos target = pos.relative(side);
        if (!level.hasChunkAt(target)) return;
        var source = IcCapabilities.EU_SOURCE.getCapability(level, pos, null, null, side);
        var sink = IcCapabilities.EU_SINK.getCapability(level, target, null, null, side.getOpposite());
        if (source == null || sink == null) return;
        for (int index = 0; index < source.packetsPerTick(); index++) {
            int voltage = budget.packet(pos, source);
            int packet = Math.min(voltage, sink.demandedEu());
            if (packet <= 0) return;
            if (voltage > sink.maxInputVoltage()) {
                if (EnergySettings.MACHINE_EXPLOSIONS.get()) {
                    budget.spent(pos, source.extractEu(packet));
                    EuOverload.destroy(level, target);
                }
                return;
            }
            int delivered = sink.receiveEu(packet, packet);
            if (delivered <= 0) return;
            budget.spent(pos, source.extractEu(delivered));
        }
    }
    private EuAdjacent() {}
}
