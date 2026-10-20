package me.almana.innovativecontraptions.datagen;

import java.util.function.BiConsumer;

public final class MatterLanguage {
    public static void add(BiConsumer<String, String> add) {
        String prefix = "matter.innovativecontraptions.";
        String[] keys = {"clear", "save", "import", "export", "stop", "once", "repeat", "previous", "next"};
        String[] labels = {"Delete", "Save", "Import", "Export", "Stop", "Single", "Repeat", "<", ">"};
        for (int i = 0; i < keys.length; i++) add.accept(prefix + keys[i], labels[i]);
        String[] status = {"Ready", "Working", "Insufficient energy", "Scan complete", "Output full / paused", "Unknown matter cost", "Insert memory or adjacent archive", "Pattern already recorded", "Insufficient matter", "Pattern transfer failed"};
        for (int i = 0; i < status.length; i++) add.accept(prefix + "status." + i, status[i]);
        add.accept(prefix + "progress", "Progress: %s%%");
        add.accept(prefix + "blank_memory", "Blank pattern memory");
        add.accept(prefix + "recorded_memory", "Recorded: %s");
        add.accept(prefix + "costs", "Pattern Scanning");
        add.accept(prefix + "replication", "Matter Replication");
        add.accept(prefix + "synthesis", "Matter Synthesis");
        add.accept(prefix + "archive", "Pattern Archiving");
        add.accept(prefix + "slot.amplifier", "Scrap or Scrap Box: synthesis amplifier");
        add.accept(prefix + "slot.target", "Scan target: consumed on completion");
        add.accept(prefix + "slot.memory", "Crystal Memory: save, import or export patterns");
        add.accept(prefix + "slot.output", "Replicated item");
        add.accept(prefix + "slot.matter_input", "UU-Matter supply");
        add.accept(prefix + "slot.matter_output", "UU-Matter output");
        add.accept(prefix + "slot.battery", "Discharge battery");
        add.accept(prefix + "slot.upgrade", "Machine upgrade");
        add.accept("tooltip.innovativecontraptions.fluid_cell_amount", "%s / %s mB");
    }
    private MatterLanguage() {}
}
