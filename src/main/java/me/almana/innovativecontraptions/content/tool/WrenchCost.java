package me.almana.innovativecontraptions.content.tool;

public final class WrenchCost {
    public static final int MECH_ROTATE_DAMAGE = 1;
    public static final int MECH_PICKUP_DAMAGE = 10;
    public static final int ELECTRIC_ROTATE_EU = 100;
    public static final int ELECTRIC_PICKUP_EU = 1000;

    public static boolean canAfford(int stored, int cost) {
        return stored >= cost;
    }

    private WrenchCost() {
    }
}
