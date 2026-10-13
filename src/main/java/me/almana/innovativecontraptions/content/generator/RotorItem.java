package me.almana.innovativecontraptions.content.generator;

import net.minecraft.world.item.Item;

public class RotorItem extends Item {
    private final RotorTier tier;

    public RotorItem(Properties properties, RotorTier tier) {
        super(properties);
        this.tier = tier;
    }

    public RotorTier tier() {
        return tier;
    }
}
