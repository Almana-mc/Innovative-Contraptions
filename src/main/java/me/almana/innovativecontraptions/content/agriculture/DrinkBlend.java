package me.almana.innovativecontraptions.content.agriculture;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record DrinkBlend(int alcohol, int strength, int maturity, boolean rum) {
    public static final DrinkBlend SPOILED = new DrinkBlend(0, 0, 5, false);
    public static final Codec<DrinkBlend> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.intRange(0, 6).fieldOf("alcohol").forGetter(DrinkBlend::alcohol),
            Codec.intRange(0, 6).fieldOf("strength").forGetter(DrinkBlend::strength),
            Codec.intRange(0, 5).fieldOf("maturity").forGetter(DrinkBlend::maturity),
            Codec.BOOL.fieldOf("rum").forGetter(DrinkBlend::rum)).apply(instance, DrinkBlend::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, DrinkBlend> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, DrinkBlend::alcohol, ByteBufCodecs.VAR_INT, DrinkBlend::strength,
            ByteBufCodecs.VAR_INT, DrinkBlend::maturity, ByteBufCodecs.BOOL, DrinkBlend::rum, DrinkBlend::new);
    public static DrinkBlend beer(int hops, int wheat, int water, int maturity) {
        double ratio = hops == 0 ? 0 : wheat == 0 ? Double.POSITIVE_INFINITY : hops / (double) wheat;
        double solids = water == 0 ? Double.POSITIVE_INFINITY : (hops + wheat) / (double) water;
        int alcohol = ratio <= .25 ? 0 : ratio <= 1.0 / 3 ? 1 : ratio <= .5 ? 2 : ratio < 2 ? 3 : Math.min(6, (int) ratio + 2);
        int strength = solids <= 5.0 / 12 ? 0 : solids <= .5 ? 1 : solids < 1 ? 2 : solids == 1 ? 3 : solids < 2 ? 4 : solids < 2.4 ? 5 : 6;
        return new DrinkBlend(alcohol, strength, ratio >= 5 || solids >= 4 ? 5 : maturity, false);
    }
    public int duration() { return new int[]{300, 600, 900, 1200, 1600, 2000, 2400}[strength]; }
    public double intensity() { return new double[]{.4, .75, 1, 1.5, 2, 0}[maturity]; }
}
