package me.almana.innovativecontraptions.content.energy.storage;

public enum ElectricalKind {
    BATBOX(1, 40_000, false, false), CESU(2, 300_000, false, false),
    MFE(3, 4_000_000, false, false), MFSU(4, 40_000_000, false, false),
    BATBOX_PAD(1, 40_000, true, false), CESU_PAD(2, 300_000, true, false),
    MFE_PAD(3, 4_000_000, true, false), MFSU_PAD(4, 40_000_000, true, false),
    LV_TRANSFORMER(1, 256, false, true), MV_TRANSFORMER(2, 1_024, false, true),
    HV_TRANSFORMER(3, 4_096, false, true), EV_TRANSFORMER(4, 16_384, false, true);

    public final int tier;
    public final int capacity;
    public final boolean pad;
    public final boolean transformer;

    ElectricalKind(int tier, int capacity, boolean pad, boolean transformer) {
        this.tier = tier;
        this.capacity = capacity;
        this.pad = pad;
        this.transformer = transformer;
    }

    public int packet() { return 32 << (2 * (tier - 1)); }
}
