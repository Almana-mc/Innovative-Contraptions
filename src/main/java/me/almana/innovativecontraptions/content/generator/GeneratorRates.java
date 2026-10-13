package me.almana.innovativecontraptions.content.generator;

public final class GeneratorRates {
    public static final int FUEL_EU_PER_TICK = 10;
    public static final int SOLAR_EU_PER_TICK = 2;
    public static final int WATER_BUCKET_TICKS = 40;
    public static final int WATER_BUCKET_EU_PER_TICK = 4;
    public static final int WATER_MAX_BLOCKS = 25;
    public static final int WATER_SCAN_INTERVAL = 128;
    public static final int KU_PER_EU = 4;
    public static final int WIND_KU_MULTIPLIER = 10;
    public static final int PASSIVE_WIND_DIVISOR = 12;
    public static final int PASSIVE_WIND_OBSTRUCTION_DIVISOR = 32;
    public static final int GEOTHERMAL_TANK_CAPACITY = 8000;
    public static final int GEOTHERMAL_BUFFER = 2400;
    public static final int GEOTHERMAL_EU_PER_TICK = 20;
    public static final int GEOTHERMAL_EU_PER_MB = 10;

    public static int fuelPotentialEnergy(int burnTicks) {
        return burnTicks * FUEL_EU_PER_TICK;
    }

    public static float waterPassiveOutput(int waterBlocks) {
        return waterBlocks / 100.0F;
    }

    public static int waterBucketEnergy() {
        return WATER_BUCKET_TICKS * WATER_BUCKET_EU_PER_TICK;
    }

    private GeneratorRates() {
    }
}
