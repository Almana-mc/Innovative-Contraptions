package me.almana.innovativecontraptions.content.matter;

import me.almana.innovativecontraptions.content.processing.ProcessingAutomation;

final class MatterAutomation {
    static void tick(MatterBlockEntity machine) {
        if (machine.kind() != 0 && machine.kind() != 3) return;
        for (int slot = 5; slot < 9; slot++) ProcessingAutomation.transfer(machine, machine, machine.getItem(slot), null, null);
    }
    private MatterAutomation() {}
}
