package me.almana.innovativecontraptions.client.processing;

import me.almana.innovativecontraptions.content.processing.ProcessingContent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public final class ProcessingClient {
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ProcessingContent.MENU.get(), ProcessingScreen::new);
        event.register(ProcessingContent.CANNER_MENU.get(), ProcessingCannerScreen::new);
        event.register(ProcessingContent.FILTER_MENU.get(), ProcessingFilterScreen::new);
    }
    private ProcessingClient() {}
}
