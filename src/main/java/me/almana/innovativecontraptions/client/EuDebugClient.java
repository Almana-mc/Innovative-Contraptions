package me.almana.innovativecontraptions.client;

import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import me.almana.innovativecontraptions.content.energy.debug.EuDebugPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class EuDebugClient {
    public static final Long2IntOpenHashMap THROUGHPUT = new Long2IntOpenHashMap();
    public static final long EXPIRY_TICKS = 40L;
    private static final int SCALE_EU = 8192;
    private static long lastUpdateTick;

    public static void handle(EuDebugPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            THROUGHPUT.clear();
            for (int i = 0; i < payload.positions().size(); i++) {
                THROUGHPUT.put((long) payload.positions().get(i), (int) payload.throughput().get(i));
            }
            Minecraft mc = Minecraft.getInstance();
            lastUpdateTick = mc.level != null ? mc.level.getGameTime() : 0L;
        });
    }

    public static boolean expired() {
        Minecraft mc = Minecraft.getInstance();
        return mc.level == null || mc.level.getGameTime() - lastUpdateTick > EXPIRY_TICKS;
    }

    public static Long2IntMap.FastEntrySet entries() {
        return THROUGHPUT.long2IntEntrySet();
    }

    public static int colorFor(int eu) {
        float t = Mth.clamp(eu / (float) SCALE_EU, 0f, 1f);
        int red = (int) (t * 255);
        int green = (int) ((1 - t) * 255);
        return 0xFF000000 | (red << 16) | (green << 8);
    }

    private EuDebugClient() {
    }
}
