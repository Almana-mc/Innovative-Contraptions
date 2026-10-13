package me.almana.innovativecontraptions.datagen;

import java.util.function.BiConsumer;

public final class EquipmentLanguage {
    public static void add(BiConsumer<String, String> add) {
        add.accept("gui.innovativecontraptions.processing.status.6", "Needs matching container");
        add.accept("key.category.innovativecontraptions.equipment", "Innovative Contraptions Equipment");
        add.accept("key.innovativecontraptions.equipment_mode", "Equipment mode");
        add.accept("key.innovativecontraptions.equipment_alt", "Vision modifier");
        add.accept("key.innovativecontraptions.equipment_boost", "Powered jump boost");
        add.accept("tooltip.innovativecontraptions.flight_fuel", "%s / %s mB biogas");
        add.accept("tooltip.innovativecontraptions.equipment_controls", "Mode + use: change tool mode. Alt + Mode: night vision.");
        add.accept("tooltip.innovativecontraptions.flight_controls", "Jump: thrust. Jump + Mode: hover. Sneak: descend.");
        add.accept("tooltip.innovativecontraptions.equipment_charge", "Charge tier %s; maximum %s EU per charge operation");
        add.accept("tooltip.innovativecontraptions.backpack_supply", "Supplies compatible equipment when used; wear on chest");
        add.accept("tooltip.innovativecontraptions.hazard_protection", "5% ordinary protection. Complete suit blocks heat, electricity and radiation.");
        add.accept("tooltip.innovativecontraptions.air_remaining", "%s mB compressed air");
        add.accept("tooltip.innovativecontraptions.helmet_hud", "Sneak + Mode: cycle equipment HUD");
        add.accept("message.innovativecontraptions.equipment.hud_0", "Equipment HUD hidden");
        add.accept("message.innovativecontraptions.equipment.hud_1", "Equipment HUD: percentages");
        add.accept("message.innovativecontraptions.equipment.hud_2", "Equipment HUD: stored energy");
        add.accept("hud.innovativecontraptions.equipment.head", "Head");
        add.accept("hud.innovativecontraptions.equipment.chest", "Chest");
        add.accept("hud.innovativecontraptions.equipment.legs", "Legs");
        add.accept("hud.innovativecontraptions.equipment.feet", "Feet");
        add.accept("effect.innovativecontraptions.radiation", "Radiation");
        add.accept("death.attack.radiation", "%s succumbed to radiation");
        add.accept("death.attack.radiation.player", "%s succumbed to radiation while fighting %s");
        String[] messages = {"silk_touch", "fortune", "shearing_off", "shearing_on", "blade_on", "blade_off",
                "hover_on", "hover_off", "vision_on", "vision_off"};
        String[] names = {"Silk Touch enabled", "Fortune III enabled", "Shearing disabled", "Shearing enabled",
                "Nano Saber activated", "Nano Saber deactivated", "Hover enabled", "Hover disabled", "Night vision enabled", "Night vision disabled"};
        for (int i = 0; i < messages.length; i++) add.accept("message.innovativecontraptions.equipment." + messages[i], names[i]);
        String[] modes = {"Mining", "Low focus", "Long range", "Horizontal (use on block)", "Superheat", "Scatter", "Explosive", "3 by 3 (use on block)"};
        for (int i = 0; i < modes.length; i++) add.accept("message.innovativecontraptions.equipment.laser_mode_" + i, "Laser: " + modes[i]);
    }
    private EquipmentLanguage() { }
}
