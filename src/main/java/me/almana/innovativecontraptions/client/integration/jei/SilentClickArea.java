package me.almana.innovativecontraptions.client.integration.jei;

import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.recipe.IFocusFactory;
import mezz.jei.api.runtime.IRecipesGui;
import net.minecraft.client.renderer.Rect2i;

// screen draws its own tooltip
public record SilentClickArea(IGuiClickableArea area) implements IGuiClickableArea {
    @Override public Rect2i getArea() { return area.getArea(); }
    @Override public boolean isTooltipEnabled() { return false; }
    @Override public void onClick(IFocusFactory focusFactory, IRecipesGui recipesGui) { area.onClick(focusFactory, recipesGui); }
}
