package me.almana.innovativecontraptions.content.energy;

public enum EuTier {
    LV(32),
    MV(128),
    HV(512),
    EV(2048),
    IV(8192);

    public final int volts;

    EuTier(int volts) {
        this.volts = volts;
    }

    public EuTier next() {
        EuTier[] all = values();
        return all[Math.min(ordinal() + 1, all.length - 1)];
    }

    public static EuTier fromVolts(int volts) {
        for (EuTier t : values()) {
            if (t.volts == volts) return t;
        }
        return null;
    }
}
