package me.almana.innovativecontraptions.registry;

import me.almana.innovativecontraptions.InnovativeContraptions;
import me.almana.innovativecontraptions.content.gathering.GatheringContent;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ICCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, InnovativeContraptions.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN = TABS.register(
            "main",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + InnovativeContraptions.MODID))
                    .icon(() -> new ItemStack(ICItems.TREE_TAP.get()))
                    .displayItems((params, output) -> {
                        ICItems.ITEMS.getEntries().forEach(holder -> output.accept(holder.get()));
                        GatheringContent.ITEMS.getEntries().forEach(holder -> output.accept(holder.get()));
                    })
                    .build()
    );

    private ICCreativeTabs() {
    }
}
