package me.almana.innovativecontraptions.content.generator;

public enum RotorTier {
    WOOD(2, 10, 60, 10800, 0.25F),
    IRON(3, 14, 75, 86400, 0.50F),
    STEEL(4, 17, 90, 172800, 0.75F),
    CARBON(5, 20, 110, 604800, 1.0F);

    public final int radius;
    public final int minWind;
    public final int maxWind;
    public final int durability;
    public final float efficiency;

    RotorTier(int radius, int minWind, int maxWind, int durability, float efficiency) {
        this.radius = radius;
        this.minWind = minWind;
        this.maxWind = maxWind;
        this.durability = durability;
        this.efficiency = efficiency;
    }
}
