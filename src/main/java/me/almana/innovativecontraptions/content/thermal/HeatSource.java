package me.almana.innovativecontraptions.content.thermal;

import net.minecraft.core.Direction;

public interface HeatSource {
    int supplyHeat(Direction side, int requested);
}
