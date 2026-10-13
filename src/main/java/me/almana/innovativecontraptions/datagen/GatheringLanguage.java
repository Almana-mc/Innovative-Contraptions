package me.almana.innovativecontraptions.datagen;

import java.util.function.BiConsumer;

public final class GatheringLanguage {
    public static void add(BiConsumer<String, String> add) {
        add.accept("block.innovativecontraptions.bore_pipe", "Mining Pipe");
        add.accept("block.innovativecontraptions.terrain_station", "Terraformer");
        add.accept("block.innovativecontraptions.relocation_core", "Teleporter");
        add.accept("item.innovativecontraptions.tuning_chip", "Frequency Transmitter");
        String[] programs = {"blank", "frost", "planting", "drying", "leveling", "watering", "fungi"};
        String[] names = {"TFBP - Empty", "TFBP - Chilling", "TFBP - Cultivation", "TFBP - Desertification", "TFBP - Flatification", "TFBP - Irrigation", "TFBP - Mushroom"};
        for (int i = 0; i < programs.length; i++) add.accept("item.innovativecontraptions.terrain_" + programs[i], names[i]);
        add.accept("tooltip.innovativecontraptions.terrain_program", "%s EU per successful attempt; search range %s. Use on Terraformer to install or remove.");
        add.accept("message.innovativecontraptions.ore_probe", "Detected %s ore blocks in a cube extending %s blocks in each direction.");
        String[] buttons = {"pause", "resume", "filter", "silk", "silk_off", "silk_on", "blacklist", "whitelist"};
        String[] labels = {"Pause", "Resume", "Filter mode", "Silk Touch", "Silk: off", "Silk: on", "Blacklist", "Whitelist"};
        for (int i = 0; i < buttons.length; i++) add.accept("gui.innovativecontraptions.gathering." + buttons[i], labels[i]);
        String[] statuses = {"Working", "Needs energy", "Check supplies", "Obstructed / full", "Paused", "Bottom reached", "Chunk unloaded", "Needs adjacent pump"};
        for (int i = 0; i < statuses.length; i++) add.accept("gui.innovativecontraptions.gathering.status." + i, statuses[i]);
        add.accept("jei.innovativecontraptions.gathering.title", "Resource Gathering");
        add.accept("jei.innovativecontraptions.gathering.pump", "%s mB per %s ticks at %s EU/t. Intake faces fluid; adjacent pipe rig can provide a deep fluid target.");
        add.accept("jei.innovativecontraptions.gathering.bore", "Drill: %s ticks, %s EU/t plus tool charge and 2 EU/depth. Scanner radius %s. Pipe is consumed only by the shaft.");
        add.accept("jei.innovativecontraptions.gathering.remote", "Scan every %s ticks, %s EU/scanned block and %s EU/mined block. Radius %s. Filters compare blocks and drops; redstone pauses.");
        add.accept("jei.innovativecontraptions.gathering.terrain", "%s EU/success, 10%% on failure. Search range %s; right-click a Terraformer to install/remove. Alters existing terrain.");
        add.accept("jei.innovativecontraptions.gathering.environment", "Terrain and fluids are environmental targets. Tools are reusable; energy is required.");
        add.accept("jei.innovativecontraptions.gathering.wrong_machine", "Open the matching gathering machine.");
        String[] slots = {"container", "drill", "pipes", "scanner", "battery", "upgrades", "output", "ghost", "buffer"};
        String[] hints = {"Empty or partly filled container", "Mining drill", "Mining pipes", "Charged ore probe", "Discharge battery", "Compatible machine upgrades", "Filled container output", "Click with a carried item to set a filter; no item is consumed. Empty cursor clears it.", "Mined items wait here when no adjacent inventory has room"};
        for (int i = 0; i < slots.length; i++) add.accept("gui.innovativecontraptions.gathering.slot." + slots[i], hints[i]);
    }
    private GatheringLanguage() {}
}
