package me.almana.innovativecontraptions.compat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record CommonInfo(boolean showNotification) {
    public static final MapCodec<CommonInfo> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.BOOL.optionalFieldOf("show_notification", true).forGetter(CommonInfo::showNotification)
    ).apply(i, CommonInfo::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, CommonInfo> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, CommonInfo::showNotification, CommonInfo::new);
}
