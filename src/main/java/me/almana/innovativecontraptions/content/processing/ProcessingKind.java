package me.almana.innovativecontraptions.content.processing;

import com.mojang.serialization.Codec;
import java.util.Locale;
import net.minecraft.util.StringRepresentable;

public enum ProcessingKind implements StringRepresentable {
    COMPRESSING(300, 2, 600), EXTRACTING(300, 2, 600),
    EXTRUDING(200, 10, 2000), ROLLING(200, 10, 2000), CUTTING(200, 10, 2000),
    WASHING(500, 16, 8000), RECYCLING(45, 1, 45), CANNING(200, 2, 400),
    SMELTING(100, 3, 300), INDUCTION(4000, 16, 10000), SLICING(450, 4, 1800), CENTRIFUGING(500, 48, 24000);

    public static final Codec<ProcessingKind> CODEC = StringRepresentable.fromEnum(ProcessingKind::values);
    public final int ticks;
    public final int euPerTick;
    public final int capacity;

    ProcessingKind(int ticks, int euPerTick, int capacity) {
        this.ticks = ticks;
        this.euPerTick = euPerTick;
        this.capacity = capacity;
    }

    public boolean forming() { return this == ROLLING || this == CUTTING || this == EXTRUDING; }
    @Override public String getSerializedName() { return name().toLowerCase(Locale.ROOT); }
}
