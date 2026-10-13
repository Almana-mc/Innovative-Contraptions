package me.almana.innovativecontraptions.client.gathering;

import me.almana.innovativecontraptions.content.gathering.TerrainProgramItem;
import net.minecraft.world.item.Item;

public record GatheringJeiRecipe(int kind, Item tool, Item scanner, TerrainProgramItem program) {}
