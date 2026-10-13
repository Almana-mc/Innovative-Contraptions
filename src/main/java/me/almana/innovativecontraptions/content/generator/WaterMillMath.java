package me.almana.innovativecontraptions.content.generator;

final class WaterMillMath {
    static final int BUFFER_MAX = 640;
    private static final int MICRO_EU_PER_EU = 100;

    static int euFromMicro(int microEu) {
        return microEu / MICRO_EU_PER_EU;
    }

    static int microEuLeft(int microEu) {
        return microEu % MICRO_EU_PER_EU;
    }

    static int cappedBuffer(int current, int added) {
        return Math.min(current + added, BUFFER_MAX);
    }

    private WaterMillMath() {
    }
}
