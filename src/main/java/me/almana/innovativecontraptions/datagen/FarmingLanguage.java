package me.almana.innovativecontraptions.datagen;

import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import me.almana.innovativecontraptions.content.agriculture.CropSpecies;

final class FarmingLanguage {
    static void add(BiConsumer<String, String> text) {
        for (var crop : CropSpecies.values()) text.accept("crop.innovativecontraptions." + crop.id(),
                Arrays.stream(crop.id().split("_")).map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1)).collect(Collectors.joining(" ")));
        for (var crop : CropSpecies.values()) text.accept("jei.innovativecontraptions.farming.environment." + crop.id(), environment(crop));
        text.accept("fluid_type.innovativecontraptions.crop_treatment", "Weed-EX");
        text.accept("message.innovativecontraptions.crop_report", "%s · Stage %s/%s · Nutrients %s · Water %s · Weed-EX %s · Growth %s");
        text.accept("message.innovativecontraptions.barrel", "Water %s L · Wheat %s · Hops %s · Maturity %s · Age %s ticks");
        text.accept("tooltip.innovativecontraptions.crop_scan", "Analysis level: %s / 4");
        text.accept("tooltip.innovativecontraptions.crop_genes", "Growth %s · Gain %s · Resistance %s");
        text.accept("tooltip.innovativecontraptions.crop_tier", "Tier: %s");
        text.accept("tooltip.innovativecontraptions.crop_discoverer", "Discovered by: %s");
        text.accept("tooltip.innovativecontraptions.crop_attributes", "Attributes: %s");
        text.accept("tooltip.innovativecontraptions.brew", "Alcohol %s · Strength %s · Maturity %s");
        text.accept("gui.innovativecontraptions.farming.energy", "%s / %s EU");
        text.accept("gui.innovativecontraptions.farming.scan", "%s / %s");
        text.accept("gui.innovativecontraptions.farming.fluids", "Water %s · Care %s mB");
        text.accept("gui.innovativecontraptions.farming.upgrades", "Automation upgrades");
        text.accept("gui.innovativecontraptions.farming.status.0", "Scanning 9 × 9 × 3");
        text.accept("gui.innovativecontraptions.farming.status.1", "Insufficient energy");
        text.accept("gui.innovativecontraptions.farming.status.2", "Output blocked");
        text.accept("gui.innovativecontraptions.analyzer.scan", "Scan: 10 / 90 / 900 / 9000 EU");
        text.accept("jei.innovativecontraptions.farming.harvesting", "Crop harvesting");
        text.accept("jei.innovativecontraptions.farming.care", "Crop support");
        text.accept("jei.innovativecontraptions.farming.scan", "Scan: %s EU each %s ticks; 9 × 9 × 3");
        text.accept("jei.innovativecontraptions.farming.harvest", "%s EU per drop stack; genes vary yield");
        text.accept("jei.innovativecontraptions.farming.care_cost", "%s EU per treatment; one scan costs %s EU");
        text.accept("jei.innovativecontraptions.farming.fertilizer", "1 fertilizer: +90 nutrients when below 100");
        text.accept("jei.innovativecontraptions.farming.water", "1 mB per hydration unit, up to 200");
        text.accept("jei.innovativecontraptions.farming.treatment", "1 mB per protection unit, up to 150");
        text.accept("jei.innovativecontraptions.farming.maturity", "Harvest at stage %s or %s; retain crop");
        text.accept("jei.innovativecontraptions.farming.wrong_machine", "This operation requires a crop matron");
        text.accept("jei.innovativecontraptions.farming.growth", "Growth: %s points per stage; environment varies");
        text.accept("jei.innovativecontraptions.farming.genetics", "Cross sticks breed with two ready neighbors; all 52 crops compete");
    }
    private static String environment(CropSpecies crop) {
        String soil = "Farmland; water, nutrients and air affect growth. ";
        if (crop.commonMetal()) return soil + "Final stage requires matching ore or metal within 4 blocks below, without an air gap.";
        if (crop == CropSpecies.GOLD_LEAF || crop == CropSpecies.SILVER_LEAF) return soil + "Final stage requires matching ore or metal within 4 blocks below, without an air gap.";
        if (crop.flower()) return soil + "Light 12 or higher.";
        if (crop.generic() || crop == CropSpecies.REED || crop == CropSpecies.STICKREED) return soil;
        return soil + switch (crop) {
            case RED_GRAIN -> "Light 5–10; a redstone signal prevents wheat replacement.";
            case COCOA -> "At least 3 stored nutrients.";
            case RED_MUSHROOM, BROWN_MUSHROOM -> "Stored water must be positive.";
            case PREDATOR_VINE -> "Light above 10; lava below for stage 3 onward.";
            case NETHER_WART -> "Soul sand below boosts growth; snow can mutate the crop.";
            case TERRA_WART -> "Snow below boosts growth; soul sand can mutate the crop.";
            case VENOM_BLOOM -> "Light 12+ except at stage 5. Stage 5 is poisonous.";
            default -> "Light 9 or higher.";
        };
    }
    private FarmingLanguage() {}
}
