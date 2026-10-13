package me.almana.innovativecontraptions.content.agriculture;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record CropGenome(String species, int growth, int gain, int resistance, int scan) {
    public static final CropGenome EMPTY = new CropGenome("empty", 0, 0, 0, 0);
    public static final Codec<CropGenome> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("species").forGetter(CropGenome::species),
            Codec.intRange(0, 31).fieldOf("growth").forGetter(CropGenome::growth),
            Codec.intRange(0, 31).fieldOf("gain").forGetter(CropGenome::gain),
            Codec.intRange(0, 31).fieldOf("resistance").forGetter(CropGenome::resistance),
            Codec.intRange(0, 4).fieldOf("scan").forGetter(CropGenome::scan)).apply(instance, CropGenome::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, CropGenome> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, CropGenome::species, ByteBufCodecs.VAR_INT, CropGenome::growth,
            ByteBufCodecs.VAR_INT, CropGenome::gain, ByteBufCodecs.VAR_INT, CropGenome::resistance,
            ByteBufCodecs.VAR_INT, CropGenome::scan, CropGenome::new);
    public CropSpecies crop() { return CropSpecies.byName(species); }
    public CropGenome scanned() { return new CropGenome(species, growth, gain, resistance, Math.min(4, scan + 1)); }
    public CropGenome mutated(CropSpecies crop) { return new CropGenome(crop.id(), growth, gain, resistance, 0); }
}
