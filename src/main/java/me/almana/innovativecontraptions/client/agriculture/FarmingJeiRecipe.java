package me.almana.innovativecontraptions.client.agriculture;

import me.almana.innovativecontraptions.content.agriculture.CropSpecies;

public record FarmingJeiRecipe(CropSpecies crop, int supply) {
    public boolean care() { return supply >= 0; }
}
