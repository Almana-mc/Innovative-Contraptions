package me.almana.innovativecontraptions.content.energy.debug;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class EuDebugState {
    public static final Set<UUID> SUBSCRIBERS = ConcurrentHashMap.newKeySet();
    public static final int SEND_INTERVAL_TICKS = 2;

    public static boolean toggle(UUID player) {
        if (SUBSCRIBERS.remove(player)) {
            return false;
        }
        SUBSCRIBERS.add(player);
        return true;
    }

    public static boolean any() {
        return !SUBSCRIBERS.isEmpty();
    }

    private EuDebugState() {
    }
}
