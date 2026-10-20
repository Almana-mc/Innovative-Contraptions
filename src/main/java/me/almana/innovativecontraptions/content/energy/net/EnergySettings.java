package me.almana.innovativecontraptions.content.energy.net;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class EnergySettings {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec.BooleanValue MACHINE_EXPLOSIONS = BUILDER.define("machineOverloadExplosions", true);
    public static final ModConfigSpec.BooleanValue CABLE_MELTING = BUILDER.define("cableOverloadMelting", true);
    public static final ModConfigSpec.DoubleValue RETAINED_ENERGY = BUILDER.defineInRange("storageDropEnergyRetention", 0.8, 0, 1);
    public static final ModConfigSpec.DoubleValue WRENCH_LOSS_CHANCE = BUILDER.defineInRange("wrenchLossChance", 0.2, 0, 1);
    public static final ModConfigSpec.DoubleValue NO_WRENCH_LOSS_CHANCE = BUILDER.defineInRange("noWrenchLossChance", 1.0, 0, 1);
    public static final ModConfigSpec.DoubleValue REACTOR_EXPLOSION_LIMIT = BUILDER.defineInRange("reactorExplosionPowerLimit", 45.0, 0, 100);
    public static final ModConfigSpec.BooleanValue DERIVE_UU_VALUES = BUILDER.define("deriveUuValuesFromRecipes", false);
    public static final ModConfigSpec SPEC = BUILDER.build();
    public static double retention() { return RETAINED_ENERGY.get(); }
    private EnergySettings() {}
}
