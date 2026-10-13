package me.almana.innovativecontraptions.content.matter;

import me.almana.innovativecontraptions.InnovativeContraptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import java.util.HashMap;
import java.util.Map;

public record MatterCostSync(Map<Item, Integer> costs) implements CustomPacketPayload {
    public static Map<Item, Integer> clientCosts = Map.of();
    public static final class Received extends Event {}
    public static void clearClient() { acceptClient(Map.of()); }
    public static void acceptClient(Map<Item, Integer> costs) {
        clientCosts = Map.copyOf(costs);
        NeoForge.EVENT_BUS.post(new Received());
    }
    public static final Type<MatterCostSync> TYPE = new Type<>(Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, "matter_costs"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MatterCostSync> STREAM_CODEC = StreamCodec.of((buffer, payload) -> {
        buffer.writeVarInt(payload.costs.size());
        payload.costs.forEach((item, cost) -> { buffer.writeVarInt(BuiltInRegistries.ITEM.getId(item)); buffer.writeVarInt(cost); });
    }, buffer -> {
        int count = buffer.readVarInt();
        if (count < 0 || count > BuiltInRegistries.ITEM.size()) throw new IllegalArgumentException("Invalid matter costs");
        Map<Item, Integer> values = new HashMap<>();
        for (int i = 0; i < count; i++) values.put(BuiltInRegistries.ITEM.byId(buffer.readVarInt()), buffer.readVarInt());
        return new MatterCostSync(Map.copyOf(values));
    });
    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToClient(TYPE, STREAM_CODEC, (payload, context) -> context.enqueueWork(() -> acceptClient(payload.costs)));
    }
    public static void send(OnDatapackSyncEvent event) {
        var payload = new MatterCostSync(MatterCosts.values(event.getPlayerList().getServer().getRecipeManager()));
        if (event.getPlayer() != null) PacketDistributor.sendToPlayer(event.getPlayer(), payload);
        else for (var player : event.getPlayerList().getPlayers()) PacketDistributor.sendToPlayer(player, payload);
    }
    @Override public Type<MatterCostSync> type() { return TYPE; }
}
