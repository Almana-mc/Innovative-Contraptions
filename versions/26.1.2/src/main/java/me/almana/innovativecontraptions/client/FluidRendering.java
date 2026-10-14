package me.almana.innovativecontraptions.client;

import me.almana.innovativecontraptions.InnovativeContraptions;
import me.almana.innovativecontraptions.content.agriculture.FarmingContent;
import me.almana.innovativecontraptions.content.construction.ConstructionContent;
import me.almana.innovativecontraptions.content.fluid.BiogasContent;
import me.almana.innovativecontraptions.content.fluid.CoolantContent;
import me.almana.innovativecontraptions.content.matter.MatterContent;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.RegisterFluidModelsEvent;

public final class FluidRendering {
    public static void register(RegisterFluidModelsEvent event) {
        Material treatment = new Material(Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, "block/crop_treatment"));
        Material biogas = new Material(Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, "block/biogas"));
        event.register(new FluidModel.Unbaked(treatment, treatment, null, null), FarmingContent.TREATMENT, FarmingContent.FLOWING_TREATMENT);
        event.register(new FluidModel.Unbaked(biogas, biogas, null, null), BiogasContent.BIOGAS, BiogasContent.FLOWING);
        Material biomassStill = new Material(Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, "block/biomass_still"));
        Material biomassFlow = new Material(Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, "block/biomass_flow"));
        event.register(new FluidModel.Unbaked(biomassStill, biomassFlow, null, null), BiogasContent.BIOMASS, BiogasContent.FLOWING_BIOMASS);
        Material matterStill = new Material(Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, "block/uu_matter_still"));
        Material matterFlow = new Material(Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, "block/uu_matter_flow"));
        event.register(new FluidModel.Unbaked(matterStill, matterFlow, null, null), MatterContent.MATTER, MatterContent.FLOWING_MATTER);
        Material coolantStill = new Material(Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, "block/coolant_still"));
        Material coolantFlow = new Material(Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, "block/coolant_flow"));
        event.register(new FluidModel.Unbaked(coolantStill, coolantFlow, null, null), CoolantContent.COOLANT, CoolantContent.FLOWING_COOLANT);
        Material hotStill = new Material(Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, "block/hot_coolant_still"));
        Material hotFlow = new Material(Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, "block/hot_coolant_flow"));
        event.register(new FluidModel.Unbaked(hotStill, hotFlow, null, null), CoolantContent.HOT_COOLANT, CoolantContent.FLOWING_HOT_COOLANT);
        Material foamStill = new Material(Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, "block/construction_foam_still"));
        Material foamFlow = new Material(Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, "block/construction_foam_flow"));
        event.register(new FluidModel.Unbaked(foamStill, foamFlow, null, null), ConstructionContent.FOAM_FLUID, ConstructionContent.FLOWING_FOAM);
    }

    private FluidRendering() {}
}
