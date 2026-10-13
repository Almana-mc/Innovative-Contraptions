package me.almana.innovativecontraptions.content.gathering;

public enum TerrainProgram {
    BLANK(0, 0), FROST(2000, 50), PLANTING(4000, 40), DRYING(2500, 40), LEVELING(4000, 40), WATERING(3000, 60), FUNGI(8000, 25);
    public final int cost, range;
    TerrainProgram(int cost, int range) { this.cost = cost; this.range = range; }
}
