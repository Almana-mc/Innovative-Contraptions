package me.almana.innovativecontraptions.client.matter;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

public record MatterScanDisplay(Identifier id, Item item, int cost, boolean archive) {}
