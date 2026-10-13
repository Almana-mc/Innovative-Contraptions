package me.almana.innovativecontraptions.content.energy.net;

import me.almana.innovativecontraptions.content.energy.EuSource;
import net.minecraft.core.BlockPos;
import java.util.HashMap;
import java.util.Map;

public final class EuTickBudget {
    private final Map<BlockPos, Allowance> sources = new HashMap<>();
    private static final class Allowance {
        int packets;
        int energy;
        Allowance(EuSource source) {
            packets = source.packetsPerTick();
            energy = Math.min(source.availableEu(), source.sourceVoltage() * packets);
        }
    }
    public int packet(BlockPos pos, EuSource source) {
        Allowance budget = sources.computeIfAbsent(pos.immutable(), ignored -> new Allowance(source));
        return budget.packets <= 0 ? 0 : Math.min(source.availableEu(), Math.min(source.sourceVoltage(), budget.energy));
    }
    public void spent(BlockPos pos, int energy) {
        Allowance budget = sources.get(pos);
        budget.packets--;
        budget.energy -= energy;
    }
}
