package me.almana.innovativecontraptions.datagen;

import java.util.function.BiConsumer;

public final class ICProcessingLanguage {
    public static void add(BiConsumer<String, String> language) {
        language.accept("gui.innovativecontraptions.processing.collect_xp", "Collect experience");
        String prefix = "gui.innovativecontraptions.processing.";
        language.accept(prefix + "mode.extruding", "Extruding");
        language.accept(prefix + "mode.rolling", "Rolling");
        language.accept(prefix + "mode.cutting", "Cutting");
        language.accept(prefix + "energy", "%s / %s EU");
        language.accept(prefix + "water", "%s / %s mB water");
        language.accept(prefix + "progress", "%s / %s ticks");
        language.accept(prefix + "work", "%s / %s work units");
        language.accept(prefix + "heat", "Heat: %s%%");
        language.accept(prefix + "heat_value", "Heat: %s / %s");
        String[] statuses = {"Ready", "No recipe", "No power", "Output blocked", "Processing", "Needs water"};
        for (int i = 0; i < statuses.length; i++) language.accept(prefix + "status." + i, statuses[i]);
        language.accept(prefix + "status.7", "Preheating");
        language.accept(prefix + "status.8", "Needs stronger blade");
        language.accept("jei.innovativecontraptions.processing.water", "Water consumed: %s mB");
        language.accept("jei.innovativecontraptions.processing.mode", "Mode: %s");
        language.accept("jei.innovativecontraptions.processing.select_machine_mode", "Open the matching machine and select this mode first.");
        language.accept("jei.innovativecontraptions.processing.heat", "Requires %s heat; warming costs 1 EU/t.");
        language.accept("jei.innovativecontraptions.processing.blade", "Blade strength ≥ %s; blade is retained.");
        language.accept("jei.innovativecontraptions.processing.induction", "Values at full heat, for up to two lanes. Cold work is slower. Standby: 1 EU/t.");
        String packager = "gui.innovativecontraptions.packager.";
        language.accept(packager + "swap", "Switch Tanks");
        language.accept(packager + "input", "Input tank");
        language.accept(packager + "output", "Output tank");
        language.accept(packager + "empty", "Empty tank");
        String[] modes = {"Canning", "Drain cell to tank", "Fill cell from tank", "Fluid enrich"};
        for (int i = 0; i < modes.length; i++) language.accept(packager + "mode." + i, modes[i]);
        language.accept("jei.innovativecontraptions.packager.tanks", "8,000 mB per tank. Switch tanks when idle. Fill draws input; drain stores output.");
        String routing = "gui.innovativecontraptions.routing.";
        language.accept(routing + "title", "Routing filter");
        language.accept(routing + "ghosts", "Copy items; right-click clears");
        language.accept(routing + "whitelist", "Allow listed");
        language.accept(routing + "blacklist", "Block listed");
        language.accept(routing + "option.1", "Components");
        language.accept(routing + "option.2", "Damage");
        language.accept(routing + "option.3", "Match charge");
        language.accept(routing + "charge.0", "Any stored charge");
        language.accept(routing + "charge.1", "Charge at least threshold");
        language.accept(routing + "charge.2", "Charge at most threshold");
        language.accept(routing + "charge.3", "Charge equals threshold");
    }
    private ICProcessingLanguage() {}
}
