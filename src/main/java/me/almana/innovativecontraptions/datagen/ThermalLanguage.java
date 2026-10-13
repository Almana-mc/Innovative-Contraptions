package me.almana.innovativecontraptions.datagen;

import java.util.function.BiConsumer;

public final class ThermalLanguage {
    public static void add(BiConsumer<String, String> add) {
        add.accept("screen.innovativecontraptions.heat", "Heat: %s%%");
        add.accept("screen.innovativecontraptions.heater_rate", "%s coils | %s HU/t");
        add.accept("screen.innovativecontraptions.heat_ports", "Place the front heat ports against each other. Rotate with a wrench.");
        add.accept("screen.innovativecontraptions.thermal_status.0", "Insert an iron source");
        add.accept("screen.innovativecontraptions.thermal_status.1", "Check heat source / power");
        add.accept("screen.innovativecontraptions.thermal_status.2", "Needs compressed air");
        add.accept("screen.innovativecontraptions.thermal_status.3", "Output blocked");
        add.accept("screen.innovativecontraptions.thermal_status.4", "Making steel");
        add.accept("screen.innovativecontraptions.thermal_status.5", "Warming up");
        add.accept("jei.innovativecontraptions.category.steelmaking", "Steelmaking");
        add.accept("jei.innovativecontraptions.category.electric_heater", "Electric Heater");
        add.accept("jei.innovativecontraptions.steel_air", "%s mB air total; cells return empty");
        add.accept("jei.innovativecontraptions.steel_heat", "%s HU warmup; 1 HU/t while operating");
        add.accept("jei.innovativecontraptions.heater_coils", "1–%s coils; %s HU/t per coil");
        add.accept("jei.innovativecontraptions.heater_energy", "1 EU per HU actually transferred");
        add.accept("jei.innovativecontraptions.heater_condition", "Point its front at a heat consumer's front");
    }
    private ThermalLanguage() {}
}
