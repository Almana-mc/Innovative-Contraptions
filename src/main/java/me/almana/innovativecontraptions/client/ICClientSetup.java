package me.almana.innovativecontraptions.client;

import me.almana.innovativecontraptions.InnovativeContraptions;
import me.almana.innovativecontraptions.client.agriculture.FarmingScreen;
import me.almana.innovativecontraptions.client.processing.ProcessingClient;
import me.almana.innovativecontraptions.client.render.WaterMillRenderer;
import me.almana.innovativecontraptions.client.render.WindmillRenderer;
import me.almana.innovativecontraptions.client.render.WindmillRotorModel;
import me.almana.innovativecontraptions.client.screen.ElectricalScreen;
import me.almana.innovativecontraptions.client.screen.GeneratorScreen;
import me.almana.innovativecontraptions.client.screen.HeatExchangerScreen;
import me.almana.innovativecontraptions.client.screen.MaceratorScreen;
import me.almana.innovativecontraptions.client.screen.ThermalScreen;
import me.almana.innovativecontraptions.client.screen.ToolboxScreen;
import me.almana.innovativecontraptions.registry.ICBlockEntities;
import me.almana.innovativecontraptions.registry.ICMenus;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import me.almana.innovativecontraptions.client.reactor.CoolantInjectorScreen;
import me.almana.innovativecontraptions.client.reactor.ReactorScreen;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public final class ICClientSetup {
    public static final ModelLayerLocation WINDMILL_ROTOR_LAYER =
            new ModelLayerLocation(Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, "windmill_rotor"), "main");

    public static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
        ProcessingClient.registerScreens(event);
        ThermalScreen.registerScreens(event);
        KineticClient.registerScreens(event);
        FarmingScreen.register(event);
        ReactorScreen.register(event);
        CoolantInjectorScreen.register(event);
        HeatExchangerScreen.register(event);
        event.register(ICMenus.ELECTRICAL.get(), ElectricalScreen::new);
        event.register(ICMenus.GENERATOR.get(), GeneratorScreen::new);
        event.register(ICMenus.MACERATOR.get(), MaceratorScreen::new);
        event.register(ICMenus.TOOLBOX.get(), ToolboxScreen::new);
    }

    public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(WINDMILL_ROTOR_LAYER, WindmillRotorModel::createLayer);
    }

    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        KineticClient.registerRenderers(event);
        event.registerBlockEntityRenderer(ICBlockEntities.WATER_MILL.get(), WaterMillRenderer::new);
        event.registerBlockEntityRenderer(ICBlockEntities.WINDMILL.get(), WindmillRenderer::new);
    }

    private ICClientSetup() {
    }
}
