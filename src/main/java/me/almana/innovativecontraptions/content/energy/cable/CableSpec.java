package me.almana.innovativecontraptions.content.energy.cable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record CableSpec(int maxVoltage, double lossPerBlock, boolean insulated, boolean shocks, int diameter, int shockAbsorption) {
    public CableSpec(int maxVoltage, double lossPerBlock, boolean insulated, boolean shocks) {
        this(maxVoltage, lossPerBlock, insulated, shocks, 4, 0);
    }
    public static final Codec<CableSpec> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("max_voltage").forGetter(CableSpec::maxVoltage),
            Codec.DOUBLE.fieldOf("loss_per_block").forGetter(CableSpec::lossPerBlock),
            Codec.BOOL.fieldOf("insulated").forGetter(CableSpec::insulated),
            Codec.BOOL.fieldOf("shocks").forGetter(CableSpec::shocks),
            Codec.intRange(1, 16).optionalFieldOf("diameter", 4).forGetter(CableSpec::diameter),
            Codec.INT.optionalFieldOf("shock_absorption", 0).forGetter(CableSpec::shockAbsorption)
    ).apply(instance, CableSpec::new));
}
