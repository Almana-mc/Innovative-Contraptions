package me.almana.innovativecontraptions.recipe;

import me.almana.innovativecontraptions.registry.ICRecipes;
import me.almana.innovativecontraptions.content.processing.ProcessingContent;
import me.almana.innovativecontraptions.content.processing.ProcessingKind;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;

public final class ICRecipeSync {
    //? if >=26.1 {
    public static void sendRecipes(OnDatapackSyncEvent event) {
        event.sendRecipes(ICRecipes.MACERATING_TYPE.get());
        for (ProcessingKind kind : ProcessingKind.values()) {
            event.sendRecipes(ProcessingContent.recipeType(kind));
        }
    }
    //?}

    private ICRecipeSync() {
    }
}
