package me.almana.innovativecontraptions.datagen;

import java.util.function.BiConsumer;

public final class BioenergyLanguage {
    public static void add(BiConsumer<String, String> text) {
        text.accept("fluid_type.innovativecontraptions.biomass", "Biomass");
        text.accept("gui.innovativecontraptions.bioenergy.status.0", "Awaiting fuel");
        text.accept("gui.innovativecontraptions.bioenergy.status.1", "Heat port needs heat");
        text.accept("gui.innovativecontraptions.bioenergy.status.2", "Output full");
        text.accept("gui.innovativecontraptions.bioenergy.status.3", "Working");
        text.accept("gui.innovativecontraptions.bioenergy.heat", "Buffered heat: %s HU");
        text.accept("gui.innovativecontraptions.bioenergy.energy", "Stored: %s / 32000 EU");
        text.accept("gui.innovativecontraptions.bioenergy.tank", "Fuel: %s mB");
        text.accept("gui.innovativecontraptions.bioenergy.gas", "Gas: %s mB");
        text.accept("jei.innovativecontraptions.bioenergy.digestion_title", "Fermenter");
        text.accept("jei.innovativecontraptions.bioenergy.fuel_title", "Semifluid Generator");
        text.accept("gui.innovativecontraptions.bioenergy.slot.fuel_cell", "Fuel container input");
        text.accept("gui.innovativecontraptions.bioenergy.slot.returned_cell", "Returned container");
        text.accept("gui.innovativecontraptions.bioenergy.slot.empty_cell", "Empty or partial biogas cell");
        text.accept("gui.innovativecontraptions.bioenergy.slot.gas_cell", "Filled biogas cell");
        text.accept("gui.innovativecontraptions.bioenergy.slot.fertilizer", "Fertilizer output");
        text.accept("gui.innovativecontraptions.bioenergy.slot.upgrade", "Transfer upgrade");
        text.accept("gui.innovativecontraptions.bioenergy.slot.battery", "Charge a tier-one battery");
        text.accept("jei.innovativecontraptions.bioenergy.fermentation", "%s mB biomass + %s HU -> %s mB gas");
        text.accept("jei.innovativecontraptions.bioenergy.fertilizer", "1 fertilizer / %s mB biomass");
        text.accept("jei.innovativecontraptions.bioenergy.heat", "Fronts must touch; up to %s HU/t");
        text.accept("jei.innovativecontraptions.bioenergy.fuel", "%s EU/mB; %s EU/t; %s EU/cell");
        text.accept("jei.innovativecontraptions.bioenergy.container", "Cell shown for fluid identity; partial cells work");
    }
    private BioenergyLanguage() {}
}
