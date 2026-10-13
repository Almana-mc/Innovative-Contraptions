package me.almana.innovativecontraptions.content.energy.net;

import java.util.HashMap;
import java.util.Map;
import me.almana.innovativecontraptions.content.energy.cable.CableBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;

final class EuShockExposure {
    private record Path(BlockPos source, BlockPos sink) {}
    private final Map<Path, Map<LivingEntity, Integer>> paths = new HashMap<>();

    void record(ServerLevel level, BlockPos source, BlockPos sink, long end, int packet, Map<Long, Long> previous) {
        if (!EnergySettings.CABLE_MELTING.get()) return;
        var exposure = paths.computeIfAbsent(new Path(source, sink), ignored -> new HashMap<>());
        for (Long node = end; node != null; node = previous.get(node)) {
            BlockPos pos = BlockPos.of(node);
            if (!(level.getBlockState(pos).getBlock() instanceof CableBlock cable) || !cable.spec().shocks()) continue;
            int local = packet - cable.spec().shockAbsorption();
            if (local <= 0) continue;
            for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, new AABB(pos).inflate(1))) {
                exposure.merge(entity, local, Math::max);
            }
        }
    }

    void apply(ServerLevel level) {
        Map<LivingEntity, Long> total = new HashMap<>();
        paths.values().forEach(path -> path.forEach((entity, amount) -> total.merge(entity, (long) amount, Long::sum)));
        //? if >=26.1 {
        total.forEach((entity, amount) -> entity.hurtServer(level, level.damageSources().lightningBolt(), (float) Math.ceil(amount / 64.0)));
        //?} else
        //total.forEach((entity, amount) -> entity.hurt(level.damageSources().lightningBolt(), (float) Math.ceil(amount / 64.0)));
    }
}
