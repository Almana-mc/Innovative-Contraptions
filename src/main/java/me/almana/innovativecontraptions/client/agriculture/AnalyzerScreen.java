package me.almana.innovativecontraptions.client.agriculture;

import java.util.ArrayList;
import java.util.List;
import me.almana.innovativecontraptions.client.screen.MachineScreen;
import me.almana.innovativecontraptions.content.agriculture.AnalyzerMenu;
import me.almana.innovativecontraptions.content.agriculture.FarmingContent;
import me.almana.innovativecontraptions.content.agriculture.GeneticSeedsItem;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public final class AnalyzerScreen extends MachineScreen<AnalyzerMenu> {
    public AnalyzerScreen(AnalyzerMenu menu, Inventory inventory, Component title) { super(menu, inventory, title, 176, 208); }
    @Override protected void init() { super.init(); titleLabelX = 8; titleLabelY = 8; inventoryLabelX = 8; inventoryLabelY = 115; }
    @Override protected void extractMachine(GuiGraphicsExtractor graphics, int mouseX, int mouseY) { arrow(graphics, 62, 39, 0, 1); }
    @Override protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);
        label(graphics, Component.translatable("gui.innovativecontraptions.farming.energy", menu.energy(), 100_000), 8, 19);
        ItemStack seed = menu.contents().getItem(menu.contents().getItem(1).isEmpty() ? 0 : 1);
        List<Component> info = new ArrayList<>();
        if (seed.has(FarmingContent.GENOME.get())) GeneticSeedsItem.describe(seed.get(FarmingContent.GENOME.get()), info::add);
        else info.add(Component.translatable("gui.innovativecontraptions.analyzer.scan"));
        int line = 70;
        for (var text : info) for (var part : font.split(text, 154)) {
            if (line > 100) break;
            label(graphics, part, 8, line);
            line += 10;
        }
    }
}
