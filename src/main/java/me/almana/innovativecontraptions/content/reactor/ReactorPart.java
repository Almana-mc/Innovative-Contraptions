package me.almana.innovativecontraptions.content.reactor;

public enum ReactorPart {
    URANIUM(Kind.FUEL, 20_000, 1, 0),
    DUAL_URANIUM(Kind.FUEL, 20_000, 2, 0),
    QUAD_URANIUM(Kind.FUEL, 20_000, 4, 0),
    MOX(Kind.MOX, 10_000, 1, 0),
    DUAL_MOX(Kind.MOX, 10_000, 2, 0),
    QUAD_MOX(Kind.MOX, 10_000, 4, 0),
    HEAT_VENT(Kind.VENT, 1_000, 6, 0),
    ADVANCED_HEAT_VENT(Kind.VENT, 1_000, 12, 0),
    REACTOR_HEAT_VENT(Kind.VENT, 1_000, 5, 5),
    OVERCLOCKED_HEAT_VENT(Kind.VENT, 1_000, 20, 36),
    COMPONENT_HEAT_VENT(Kind.SPREAD_VENT, 0, 4, 0),
    HEAT_EXCHANGER(Kind.EXCHANGER, 2_500, 12, 4),
    ADVANCED_HEAT_EXCHANGER(Kind.EXCHANGER, 10_000, 24, 8),
    REACTOR_HEAT_EXCHANGER(Kind.EXCHANGER, 5_000, 0, 72),
    COMPONENT_HEAT_EXCHANGER(Kind.EXCHANGER, 5_000, 36, 0),
    COOLANT_10K(Kind.STORAGE, 10_000, 0, 0),
    COOLANT_30K(Kind.STORAGE, 30_000, 0, 0),
    COOLANT_60K(Kind.STORAGE, 60_000, 0, 0),
    RSH_CONDENSATOR(Kind.CONDENSATOR, 20_000, 0, 0),
    LZH_CONDENSATOR(Kind.CONDENSATOR, 100_000, 0, 0),
    PLATING(Kind.PLATING, 0, 1_000, 0, 0.95F),
    HEAT_PLATING(Kind.PLATING, 0, 2_000, 0, 0.99F),
    CONTAINMENT_PLATING(Kind.PLATING, 0, 500, 0, 0.9F),
    NEUTRON_REFLECTOR(Kind.REFLECTOR, 30_000, 0, 0),
    THICK_NEUTRON_REFLECTOR(Kind.REFLECTOR, 120_000, 0, 0),
    IRIDIUM_REFLECTOR(Kind.REFLECTOR, 0, 0, 0),
    INERT(Kind.INERT, 0, 0, 0);

    public enum Kind { FUEL, MOX, VENT, SPREAD_VENT, EXCHANGER, STORAGE, CONDENSATOR, PLATING, REFLECTOR, INERT }

    public final Kind kind;
    public final int durability;
    public final int rate;
    public final int hullRate;
    public final float factor;

    ReactorPart(Kind kind, int durability, int rate, int hullRate) {
        this(kind, durability, rate, hullRate, 1F);
    }

    ReactorPart(Kind kind, int durability, int rate, int hullRate, float factor) {
        this.kind = kind;
        this.durability = durability;
        this.rate = rate;
        this.hullRate = hullRate;
        this.factor = factor;
    }

    public boolean fuel() { return kind == Kind.FUEL || kind == Kind.MOX; }

    public boolean holdsHeat() {
        return kind == Kind.VENT || kind == Kind.EXCHANGER || kind == Kind.STORAGE || kind == Kind.CONDENSATOR;
    }
}
