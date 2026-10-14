package me.almana.innovativecontraptions;

import me.almana.innovativecontraptions.client.EuDebugClient;
import me.almana.innovativecontraptions.client.EuDebugRenderer;
import me.almana.innovativecontraptions.client.FluidRendering;
import me.almana.innovativecontraptions.client.ICClientSetup;
import me.almana.innovativecontraptions.client.ICClientRecipes;
import me.almana.innovativecontraptions.client.bioenergy.BioenergyScreen;
import me.almana.innovativecontraptions.client.equipment.EquipmentClient;
import me.almana.innovativecontraptions.client.gathering.GatheringScreen;
import me.almana.innovativecontraptions.client.matter.MatterScreen;
import me.almana.innovativecontraptions.content.IridiumChestLoot;
import me.almana.innovativecontraptions.content.agriculture.FarmingContent;
import me.almana.innovativecontraptions.content.bioenergy.BioenergyContent;
import me.almana.innovativecontraptions.content.construction.ConstructionContent;
import me.almana.innovativecontraptions.content.energy.debug.EuDebugCommand;
import me.almana.innovativecontraptions.content.energy.debug.EuDebugPayload;
import me.almana.innovativecontraptions.content.energy.net.EnergySettings;
import me.almana.innovativecontraptions.content.energy.net.EuGrids;
import me.almana.innovativecontraptions.content.equipment.EquipmentContent;
import me.almana.innovativecontraptions.content.fluid.BiogasContent;
import me.almana.innovativecontraptions.content.fluid.CoolantContent;
import me.almana.innovativecontraptions.content.gathering.GatheringContent;
import me.almana.innovativecontraptions.content.machine.PoweredLightContent;
import me.almana.innovativecontraptions.content.matter.MatterContent;
import me.almana.innovativecontraptions.content.tool.MachineLoss;
import me.almana.innovativecontraptions.datagen.ICDataGenerators;
import me.almana.innovativecontraptions.recipe.ChargedCrafting;
import me.almana.innovativecontraptions.registry.IcCapabilities;
import me.almana.innovativecontraptions.registry.ICBlockEntities;
import me.almana.innovativecontraptions.registry.ICDataComponents;
import me.almana.innovativecontraptions.registry.ICBlocks;
import me.almana.innovativecontraptions.registry.ICMenus;
import me.almana.innovativecontraptions.registry.ICCreativeTabs;
import me.almana.innovativecontraptions.registry.ICItems;
import me.almana.innovativecontraptions.registry.ICRecipes;
import me.almana.innovativecontraptions.registry.ICSounds;
import me.almana.innovativecontraptions.registry.ICTrunkPlacers;
import me.almana.innovativecontraptions.recipe.ICRecipeSync;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import me.almana.innovativecontraptions.content.processing.ProcessingContent;
import me.almana.innovativecontraptions.content.thermal.ThermalContent;
import me.almana.innovativecontraptions.content.reactor.ReactorContent;
import me.almana.innovativecontraptions.content.generator.KineticContent;

@Mod(InnovativeContraptions.MODID)
public class InnovativeContraptions {
    public static final String MODID = "innovativecontraptions";

    public InnovativeContraptions(IEventBus modBus, ModContainer container) {
        container.registerConfig(ModConfig.Type.SERVER, EnergySettings.SPEC);
        ICBlocks.BLOCKS.register(modBus);
        ICItems.ITEMS.register(modBus);
        ICSounds.SOUNDS.register(modBus);
        ICRecipes.register(modBus);
        ChargedCrafting.register(modBus);
        ProcessingContent.register(modBus);
        BioenergyContent.register(modBus);
        MatterContent.register(modBus);
        GatheringContent.register(modBus);
        ThermalContent.register(modBus);
        ReactorContent.register(modBus);
        KineticContent.register(modBus);
        FarmingContent.register(modBus);
        EquipmentContent.register(modBus);
        BiogasContent.register(modBus);
        CoolantContent.register(modBus);
        ConstructionContent.register(modBus);
        PoweredLightContent.register(modBus);
        ICDataComponents.COMPONENTS.register(modBus);
        ICBlockEntities.BLOCK_ENTITIES.register(modBus);
        ICMenus.MENUS.register(modBus);
        ICCreativeTabs.TABS.register(modBus);
        ICTrunkPlacers.TRUNK_PLACERS.register(modBus);
        modBus.addListener(IcCapabilities::register);
        modBus.addListener(KineticContent::registerCapabilities);
        //? if <26.1
        //modBus.addListener(net.neoforged.bus.api.EventPriority.LOWEST, me.almana.innovativecontraptions.compat.CapabilityBridge::register);
        modBus.addListener(ICDataGenerators::gatherData);
        modBus.addListener(InnovativeContraptions::registerPayloads);
        NeoForge.EVENT_BUS.addListener(EuDebugCommand::register);
        NeoForge.EVENT_BUS.addListener(IridiumChestLoot::load);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, MachineLoss::onBreak);
        //? if >=26.1
        NeoForge.EVENT_BUS.addListener(ICRecipeSync::sendRecipes);
        //? if >=26.1 {
        if (FMLEnvironment.getDist() == Dist.CLIENT) {
        //?} else
        //if (FMLEnvironment.dist == Dist.CLIENT) {
            modBus.addListener(FluidRendering::register);
            modBus.addListener(BioenergyScreen::register);
            modBus.addListener(MatterScreen::registerScreens);
            modBus.addListener(GatheringScreen::register);
            EquipmentClient.register(modBus);
            //? if >=26.1
            NeoForge.EVENT_BUS.addListener(ICClientRecipes::receive);
            NeoForge.EVENT_BUS.addListener(ICClientRecipes::clear);
            //? if >=26.1 {
            modBus.addListener(EuDebugRenderer::onRegisterDebugRenderers);
            //?} else {
            /*NeoForge.EVENT_BUS.addListener(EuDebugRenderer::render);
            modBus.addListener(me.almana.innovativecontraptions.client.ItemTints::register);
            modBus.addListener(FluidRendering::setup);
            modBus.addListener(me.almana.innovativecontraptions.client.UpgradeIcons::setup);
            modBus.addListener(ICClientRecipes::categorise);
            *///?}
            modBus.addListener(ICClientSetup::onRegisterMenuScreens);
            modBus.addListener(ICClientSetup::onRegisterLayers);
            modBus.addListener(ICClientSetup::onRegisterRenderers);
        }
        EuGrids.register();
    }

    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToClient(
                EuDebugPayload.TYPE, EuDebugPayload.STREAM_CODEC, EuDebugClient::handle);
    }
}
