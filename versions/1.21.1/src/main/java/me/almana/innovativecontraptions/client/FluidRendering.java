package me.almana.innovativecontraptions.client;

import java.util.List;
import me.almana.innovativecontraptions.InnovativeContraptions;
import me.almana.innovativecontraptions.content.agriculture.FarmingContent;
import me.almana.innovativecontraptions.content.fluid.BiogasContent;
import me.almana.innovativecontraptions.content.fluid.CoolantContent;
import me.almana.innovativecontraptions.content.matter.MatterContent;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.fluids.FluidType;

public final class FluidRendering {
    public static void register(RegisterClientExtensionsEvent event) {
        fluid(event, texture("crop_treatment"), texture("crop_treatment"), FarmingContent.TREATMENT_TYPE.get());
        fluid(event, texture("biogas"), texture("biogas"), BiogasContent.TYPE.get());
        fluid(event, texture("biomass_still"), texture("biomass_flow"), BiogasContent.BIOMASS_TYPE.get());
        fluid(event, texture("uu_matter_still"), texture("uu_matter_flow"), MatterContent.MATTER_TYPE.get());
        fluid(event, texture("coolant_still"), texture("coolant_flow"), CoolantContent.COOLANT_TYPE.get());
        fluid(event, texture("hot_coolant_still"), texture("hot_coolant_flow"), CoolantContent.HOT_COOLANT_TYPE.get());
    }

    private static ResourceLocation texture(String name) {
        return ResourceLocation.fromNamespaceAndPath(InnovativeContraptions.MODID, "block/" + name);
    }

    // matches 26.1 alpha layer
    public static void setup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> List.of(FarmingContent.TREATMENT, FarmingContent.FLOWING_TREATMENT,
                BiogasContent.BIOGAS, BiogasContent.FLOWING, BiogasContent.BIOMASS, BiogasContent.FLOWING_BIOMASS,
                MatterContent.MATTER, MatterContent.FLOWING_MATTER,
                CoolantContent.COOLANT, CoolantContent.FLOWING_COOLANT, CoolantContent.HOT_COOLANT, CoolantContent.FLOWING_HOT_COOLANT)
                .forEach(fluid -> ItemBlockRenderTypes.setRenderLayer(fluid.get(), RenderType.translucent())));
    }

    private static void fluid(RegisterClientExtensionsEvent event, ResourceLocation still, ResourceLocation flowing, FluidType type) {
        event.registerFluidType(new IClientFluidTypeExtensions() {
            @Override public ResourceLocation getStillTexture() { return still; }
            @Override public ResourceLocation getFlowingTexture() { return flowing; }
        }, type);
    }

    private FluidRendering() {}
}
