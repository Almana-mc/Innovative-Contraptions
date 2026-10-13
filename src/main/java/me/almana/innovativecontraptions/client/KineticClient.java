package me.almana.innovativecontraptions.client;

import me.almana.innovativecontraptions.client.render.WindmillRenderer;
import me.almana.innovativecontraptions.client.screen.GeneratorScreen;
import me.almana.innovativecontraptions.content.generator.KineticContent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public final class KineticClient {
    public static void registerScreens(RegisterMenuScreensEvent event) { event.register(KineticContent.MENU.get(), GeneratorScreen::new); }
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) { event.registerBlockEntityRenderer(KineticContent.TURBINE.get(), WindmillRenderer::new); }
    private KineticClient() {}
}
