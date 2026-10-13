package me.almana.innovativecontraptions.client.agriculture;

import java.util.ArrayList;
import java.util.List;
import me.almana.innovativecontraptions.content.agriculture.AnalyzerMenu;
import me.almana.innovativecontraptions.content.agriculture.FarmingContent;
import me.almana.innovativecontraptions.content.agriculture.GeneticSeedsItem;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public final class AnalyzerScreen extends AbstractContainerScreen<AnalyzerMenu> {
    public AnalyzerScreen(AnalyzerMenu menu, Inventory inventory, Component title) { super(menu, inventory, title, 176, 208); }
    @Override public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        int x = leftPos, y = topPos;
        graphics.fill(x - 1, y - 1, x + 177, y + 209, 0xFF182421);
        graphics.fill(x, y, x + 176, y + 208, 0xFF303633);
        graphics.fill(x + 7, y + 28, x + 168, y + 62, 0xFF222A24);
        graphics.fill(x + 7, y + 66, x + 168, y + 112, 0xFF141A16);
        for (var slot : menu.slots) {
            graphics.fill(x + slot.x - 1, y + slot.y - 1, x + slot.x + 17, y + slot.y + 17, 0xFF637566);
            graphics.fill(x + slot.x, y + slot.y, x + slot.x + 16, y + slot.y + 16, 0xFF171E19);
        }
        graphics.fill(x + 64, y + 43, x + 88, y + 50, 0xFF9DBF93);
        graphics.fill(x + 84, y + 39, x + 89, y + 54, 0xFF9DBF93);
    }
    @Override protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(font, title, 8, 8, 0xFFEFF3ED);
        graphics.text(font, Component.translatable("gui.innovativecontraptions.farming.energy", menu.energy(), 100_000), 8, 19, 0xFFE3AB55);
        ItemStack seed = menu.contents().getItem(menu.contents().getItem(1).isEmpty() ? 0 : 1);
        List<Component> info = new ArrayList<>();
        if (seed.has(FarmingContent.GENOME.get())) GeneticSeedsItem.describe(seed.get(FarmingContent.GENOME.get()), info::add);
        else info.add(Component.translatable("gui.innovativecontraptions.analyzer.scan"));
        int line = 70;
        for (var text : info) for (var part : font.split(text, 154)) {
            if (line > 100) break;
            graphics.text(font, part, 11, line, 0xFFCAD9CA);
            line += 10;
        }
        graphics.text(font, playerInventoryTitle, 8, 116, 0xFFCAD9CA);
    }
}
