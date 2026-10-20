package me.almana.innovativecontraptions.content.matter;

import me.almana.innovativecontraptions.content.processing.ProcessingUpgrades;
import me.almana.innovativecontraptions.registry.ICItems;
import net.minecraft.world.item.ItemStack;

final class MatterCycles {
    static void synthesize(MatterBlockEntity machine) {
        if (machine.paused()) { machine.status = 4; return; }
        if (machine.energy == 0 && machine.progress == 0) { machine.status = 2; return; }
        boolean powered = machine.energy > 0;
        int bonus = Math.min(machine.amplifier, machine.energy);
        long work = (long) machine.progress + machine.energy + 5L * bonus;
        machine.energy = 0; machine.amplifier -= bonus;
        ItemStack scrap = machine.getItem(0);
        if (machine.amplifier < 10000 && (scrap.is(ICItems.SCRAP.get()) || scrap.is(ICItems.SCRAP_BOX.get()))) {
            machine.amplifier += scrap.is(ICItems.SCRAP_BOX.get()) ? 45000 : 5000;
            scrap.shrink(1);
        }
        ItemStack matter = new ItemStack(ICItems.UU_MATTER.get());
        int produced = work >= MatterBlockEntity.SYNTHESIS_EU && machine.outputFits(3, matter) ? 1 : 0;
        machine.progress = (int) (work - (long) produced * MatterBlockEntity.SYNTHESIS_EU);
        if (produced > 0) machine.output(3, matter);
        machine.status = !machine.outputFits(3, matter) ? 4 : powered ? 1 : 2; machine.setChanged();
    }
    static void scan(MatterBlockEntity machine) {
        if (machine.progress >= MatterBlockEntity.SCAN_TICKS) { machine.status = 3; return; }
        ItemStack input = machine.getItem(0);
        if (input.isEmpty() || !machine.pattern.isEmpty() && !ItemStack.isSameItemSameComponents(input, machine.pattern)) {
            machine.pattern = ItemStack.EMPTY; machine.progress = 0; machine.status = 0; machine.setChanged(); return;
        }
        int cost = MatterCosts.cost(machine.getLevel(), input);
        if (cost <= 0) { machine.status = 5; return; }
        ItemStack memory = machine.getItem(1);
        MatterBlockEntity archive = machine.archive();
        if (!memory.is(ICItems.CRYSTAL_MEMORY.get()) && archive == null) { machine.status = 6; machine.progress = 0; return; }
        ItemStack stored = MatterMemoryItem.pattern(memory);
        if (ItemStack.isSameItemSameComponents(stored, input) || archive != null && archive.patterns.stream().anyMatch(stack -> ItemStack.isSameItemSameComponents(stack, input))) {
            machine.status = 7; machine.progress = 0; return;
        }
        if (machine.energy < MatterBlockEntity.SCAN_EU) { machine.status = 2; return; }
        machine.pattern = input.copyWithCount(1); machine.energy -= MatterBlockEntity.SCAN_EU; machine.progress++; machine.status = 1;
        if (machine.progress == MatterBlockEntity.SCAN_TICKS) { input.shrink(1); machine.status = 3; }
        machine.setChanged();
    }
    static void replicate(MatterBlockEntity machine) {
        machine.refreshPattern();
        if (machine.mode == 0 || machine.pattern.isEmpty()) { machine.status = 0; return; }
        if (!machine.outputFits(2, machine.pattern)) { machine.status = 4; return; }
        int clocks = machine.upgrades(ICItems.OVERCLOCKER_UPGRADE.get());
        int eu = ProcessingUpgrades.energy(MatterBlockEntity.REPLICA_EU, clocks);
        if (machine.energy < eu) { machine.status = 2; return; }
        int cost = MatterCosts.cost(machine.getLevel(), machine.pattern);
        if (cost <= 0) { machine.mode = 0; machine.status = 5; return; }
        int amount = Math.min(cost - machine.progress, (int) Math.min(Integer.MAX_VALUE, Math.ceil(100 / Math.pow(ProcessingUpgrades.TIME, clocks))));
        ItemStack matter = machine.getItem(1);
        int available = (matter.is(ICItems.UU_MATTER.get()) ? matter.getCount() : 0) * 1000 + machine.fraction;
        if (available < amount) { machine.status = 8; return; }
        int drain = Math.max(0, (amount - machine.fraction + 999) / 1000);
        machine.fraction += drain * 1000 - amount;
        matter.shrink(drain);
        machine.energy -= eu; machine.progress += amount; machine.status = 1;
        if (machine.progress >= cost) {
            machine.output(2, machine.pattern); machine.progress = 0;
            if (machine.mode == 1) machine.mode = 0;
        }
        machine.setChanged();
    }
    static boolean action(MatterBlockEntity machine, int button) {
        ItemStack memory = machine.getItem(1);
        if (machine.kind() == 1) {
            if (button == 0) { machine.progress = 0; machine.pattern = ItemStack.EMPTY; return true; }
            if (button != 1 || machine.progress < MatterBlockEntity.SCAN_TICKS) return false;
            if (memory.is(ICItems.CRYSTAL_MEMORY.get())) MatterMemoryItem.record(memory, machine.pattern);
            else if (machine.archive() == null || !machine.archive().addPattern(machine.pattern)) { machine.status = 9; return false; }
            machine.progress = 0; machine.pattern = ItemStack.EMPTY; return true;
        }
        if (machine.kind() == 2) {
            if (button == 2 && memory.is(ICItems.CRYSTAL_MEMORY.get())) return machine.addPattern(MatterMemoryItem.pattern(memory));
            if (button == 3 && memory.is(ICItems.CRYSTAL_MEMORY.get()) && !machine.pattern.isEmpty()) { MatterMemoryItem.record(memory, machine.pattern); return true; }
        }
        if (machine.kind() == 3) {
            if (button == 4) { machine.mode = 0; machine.progress = 0; return true; }
            if ((button == 5 || button == 6) && !machine.pattern.isEmpty()) { machine.mode = button == 5 ? 1 : 2; return true; }
        }
        if ((machine.kind() == 2 || machine.kind() == 3 && machine.mode == 0) && (button == 7 || button == 8)) {
            machine.selected += button == 7 ? -1 : 1; machine.refreshPattern(); return true;
        }
        return false;
    }
    private MatterCycles() {}
}

