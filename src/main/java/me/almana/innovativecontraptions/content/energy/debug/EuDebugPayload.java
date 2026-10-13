package me.almana.innovativecontraptions.content.energy.debug;

import io.netty.buffer.ByteBuf;
import me.almana.innovativecontraptions.InnovativeContraptions;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

public record EuDebugPayload(List<Long> positions, List<Integer> throughput) implements CustomPacketPayload {

    public static final Type<EuDebugPayload> TYPE = new Type<>(
            Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, "eu_debug"));

    public static final StreamCodec<ByteBuf, EuDebugPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.collection(ArrayList::new, ByteBufCodecs.VAR_LONG),
            EuDebugPayload::positions,
            ByteBufCodecs.collection(ArrayList::new, ByteBufCodecs.VAR_INT),
            EuDebugPayload::throughput,
            EuDebugPayload::new);

    @Override
    public Type<EuDebugPayload> type() {
        return TYPE;
    }
}
