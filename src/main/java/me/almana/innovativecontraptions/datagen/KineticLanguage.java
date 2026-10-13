package me.almana.innovativecontraptions.datagen;

import java.util.function.BiConsumer;

public final class KineticLanguage {
    public static void add(BiConsumer<String, String> add) {
        add.accept("jei.innovativecontraptions.category.kinetic_wind_turbine", "Kinetic Wind Turbine");
        add.accept("gui.innovativecontraptions.kinetic.health", "Rotor: %s%%");
        add.accept("gui.innovativecontraptions.kinetic.status.0", "Insert rotor");
        add.accept("gui.innovativecontraptions.kinetic.status.1", "Low wind");
        add.accept("gui.innovativecontraptions.kinetic.status.2", "Blades blocked");
        add.accept("gui.innovativecontraptions.kinetic.status.3", "Generating KU");
        add.accept("gui.innovativecontraptions.kinetic.connection", "Rotor faces outward; connect converter input to rear");
        add.accept("gui.innovativecontraptions.generator.output", "Connect an EU cable or insert a rechargeable battery");
        add.accept("jei.innovativecontraptions.wind_passive", "Passive wind power; no rotor item required");
        add.accept("jei.innovativecontraptions.wind_environment", "Wind varies with height and weather; nearby blocks reduce output");
        add.accept("jei.innovativecontraptions.wind_formula", "EU/t = max(0, wind / %s - nearby blocks / %s)");
        add.accept("jei.innovativecontraptions.kinetic_connection", "Converter input must face the turbine rear");
        add.accept("jei.innovativecontraptions.kinetic_conversion", "%s KU produces 1 EU");
        add.accept("jei.innovativecontraptions.kinetic_no_redstone", "Redstone supplies no mechanical energy");
        add.accept("jei.innovativecontraptions.fluid_amount", "Consumes %s mB; returns empty container");
        add.accept("jei.innovativecontraptions.lava_storage", "%s mB tank; full EU storage pauses consumption");
        add.accept("jei.innovativecontraptions.rotor_space", "Clear %s-block blade diameter in front");
        add.accept("jei.innovativecontraptions.rotor_wind", "Wind range: %s to %s");
        add.accept("jei.innovativecontraptions.rotor_output", "KU/t = wind strength x %s");
        add.accept("jei.innovativecontraptions.rotor_lifetime", "Rotor life: %s operating seconds");
        add.accept("jei.innovativecontraptions.rotor_overspeed", "Excessive wind causes 4x rotor wear");
    }
    private KineticLanguage() {}
}
