package me.almana.innovativecontraptions.content.equipment;

import io.netty.buffer.ByteBuf;
import me.almana.innovativecontraptions.InnovativeContraptions;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record EquipmentInputPayload(int buttons) implements CustomPacketPayload {
    public static final Type<EquipmentInputPayload> TYPE = new Type<>(
            Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, "equipment_input"));
    public static final StreamCodec<ByteBuf, EquipmentInputPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, EquipmentInputPayload::buttons, EquipmentInputPayload::new);
    @Override
    public Type<EquipmentInputPayload> type() { return TYPE; }
    public static void handle(EquipmentInputPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> EquipmentControls.update(context.player(), payload.buttons()));
    }
}
