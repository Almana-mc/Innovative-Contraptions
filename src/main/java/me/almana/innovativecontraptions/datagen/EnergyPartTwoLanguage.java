package me.almana.innovativecontraptions.datagen;

import java.util.function.BiConsumer;

final class EnergyPartTwoLanguage {
    static void add(BiConsumer<String, String> add) {
        String prefix = "screen.innovativecontraptions.";
        String[] storage = {"Always output EU", "Signal near full", "Signal partial charge", "Signal below full",
                "Signal empty", "Stop EU with signal", "Release excess with signal"};
        for (int mode = 0; mode < storage.length; mode++) add.accept(prefix + "storage.mode." + mode, storage[mode]);
        add.accept(prefix + "pad.mode.0", "Signal while active");
        add.accept(prefix + "pad.mode.1", "Signal while inactive");
        add.accept(prefix + "transformer.mode.0", "Redstone controlled");
        add.accept(prefix + "transformer.mode.1", "Fixed step down");
        add.accept(prefix + "transformer.mode.2", "Fixed step up");
        add.accept(prefix + "electrical.tier", "Tier %s | %s EU/t");
        add.accept(prefix + "electrical.capacity", "Capacity: %s EU");
        add.accept(prefix + "electrical.charge", "Charge");
        add.accept(prefix + "electrical.discharge", "Discharge");
        add.accept(prefix + "electrical.input", "Input: %s EU packet");
        add.accept(prefix + "electrical.output", "Output: %s x %s EU");
        add.accept(prefix + "electrical.rate", "Throughput: %s EU/t");
        add.accept(prefix + "lamp.guide", "Attach to a solid face or energy emitter. The back accepts up to tier-4 EU into a 5 EU network buffer. Lights whenever powered, consuming 0.25 EU/t; a redstone signal turns it off. Right-click to invert redstone behavior, or use a tier-1 supply battery to charge up to 10,000 EU at once. A full lamp toggles mode instead. Active contact burns hostile mobs for 10 seconds, or undead hostiles for 20 seconds.");
        add.accept(prefix + "lamp.status", "%s EU | %s");
        add.accept(prefix + "lamp.mode.0", "Light without signal");
        add.accept(prefix + "lamp.mode.1", "Light with signal");
        add.accept(prefix + "storage.guide", "The marked face outputs EU; five other faces accept it. Top automation charges items, bottom discharges supply items. Charging respects item tier and transfer rate. Redstone supplies 800 EU, disposable batteries 1,200 EU, and energium dust 16,000 EU. Consumable fuel needs positive free space but adds its entire energy. Wrench rotation changes the output face. Drops retain 80% energy by default.");
        add.accept(prefix + "pad.guide", "Stand on the pad to charge armor, then main inventory items every two ticks. Each item can receive two packets per update, respecting its tier. Top rejects network input; the marked face outputs EU. Offhand items are not charged.");
        add.accept(prefix + "transformer.guide", "The marked face is the high side. Step down accepts one high packet and can output four low packets per tick. Step up collects low packets into one high packet. Automatic mode steps up when powered by redstone. Fixed modes ignore redstone.");
    }
    private EnergyPartTwoLanguage() {}
}
