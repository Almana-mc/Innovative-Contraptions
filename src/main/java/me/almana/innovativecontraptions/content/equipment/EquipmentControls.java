package me.almana.innovativecontraptions.content.equipment;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public final class EquipmentControls {
    public static final int JUMP = 1, SNEAK = 2, FORWARD = 4, MODE = 8, ALT = 16, BOOST = 32;
    private static final Map<Player, State> INPUTS = Collections.synchronizedMap(new WeakHashMap<>());
    public static final class State {
        int buttons;
        long updated;
        long lastToggle = -20;
        boolean wasJumping;
        double jumpImpulse;
        BlockPos anchor;
    }
    public static State state(Player player) { return INPUTS.computeIfAbsent(player, ignored -> new State()); }
    public static void update(Player player, int buttons) {
        State state = state(player);
        state.buttons = buttons & 63;
        state.updated = player.level().getGameTime();
    }
    public static boolean held(Player player, int button) {
        State state = state(player);
        return player.level().getGameTime() - state.updated <= 20 && (state.buttons & button) != 0;
    }
    public static boolean mode(Player player) { return held(player, MODE); }
    public static boolean toggleReady(Player player) {
        State state = state(player);
        if (player.level().getGameTime() - state.lastToggle < 10) return false;
        state.lastToggle = player.level().getGameTime();
        return true;
    }
    public static void message(Player player, String name) {
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.sendSystemMessage(Component.translatable("message.innovativecontraptions.equipment." + name), true);
        }
    }
}
