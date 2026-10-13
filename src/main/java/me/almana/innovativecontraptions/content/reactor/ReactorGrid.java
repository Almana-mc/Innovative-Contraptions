package me.almana.innovativecontraptions.content.reactor;

import java.util.ArrayDeque;

public final class ReactorGrid {
    public static final int ROWS = 6, MAX_COLS = 9, BASE_COLS = 3, BASE_MAX_HEAT = 10_000;
    public static final int HU_PER_VENTED = 40, HU_PER_MB = 20, EU_PER_OUTPUT = 5;
    private final int cols;
    private final ReactorPart[] parts = new ReactorPart[ROWS * MAX_COLS];
    private final int[] damage = new int[ROWS * MAX_COLS];
    private final boolean[] spent = new boolean[ROWS * MAX_COLS];
    private int heat, maxHeat = BASE_MAX_HEAT, vented;
    private float hem = 1F, output;
    private boolean on, fluidCooled;

    public ReactorGrid(int cols) { this.cols = cols; }

    public static int slot(int x, int y) { return y * MAX_COLS + x; }
    public ReactorPart part(int x, int y) { return x < 0 || x >= cols || y < 0 || y >= ROWS ? null : parts[slot(x, y)]; }
    public int damage(int x, int y) { return damage[slot(x, y)]; }
    public boolean spent(int x, int y) { return spent[slot(x, y)]; }
    public int heat() { return heat; }
    public int maxHeat() { return maxHeat; }
    public float hem() { return hem; }
    public float output() { return output; }
    public void setHeat(int heat) { this.heat = heat; }
    public void setOn(boolean on) { this.on = on; }
    public void setFluidCooled(boolean fluidCooled) { this.fluidCooled = fluidCooled; }

    public void set(int x, int y, ReactorPart part, int amount) {
        parts[slot(x, y)] = part;
        damage[slot(x, y)] = amount;
    }

    void setDamage(int x, int y, int amount) { damage[slot(x, y)] = amount; }
    void destroy(int x, int y) { parts[slot(x, y)] = null; }
    int[][] around(int x, int y) { return new int[][] {{x - 1, y}, {x + 1, y}, {x, y - 1}, {x, y + 1}}; }

    public void cycle() {
        maxHeat = BASE_MAX_HEAT;
        hem = 1F;
        output = 0F;
        vented = 0;
        for (int pass = 0; pass < 2; pass++) {
            for (int y = 0; y < ROWS; y++) {
                for (int x = 0; x < cols; x++) {
                    ReactorPart part = part(x, y);
                    if (part != null) process(x, y, part, pass == 0);
                }
            }
        }
    }

    private void process(int x, int y, ReactorPart part, boolean heatRun) {
        if (part.fuel()) {
            fuel(x, y, part, heatRun);
            return;
        }
        if (!heatRun) return;
        switch (part.kind) {
            case VENT -> vent(x, y, part);
            case SPREAD_VENT -> { for (int[] at : around(x, y)) cool(at[0], at[1], part.rate); }
            case EXCHANGER -> ReactorHeat.exchange(this, x, y, part);
            case PLATING -> { maxHeat += part.rate; hem *= part.factor; }
            default -> { }
        }
    }

    private void fuel(int x, int y, ReactorPart rod, boolean heatRun) {
        if (!on) return;
        int base = 1 + rod.rate / 2;
        for (int cell = 0; cell < rod.rate; cell++) {
            if (heatRun) {
                int pulses = base + neighbourPulses(x, y, true);
                int produced = pulses * (pulses + 1) / 2 * 4;
                if (rod.kind == ReactorPart.Kind.MOX && fluidCooled && (float) heat / maxHeat > 0.5F) produced *= 2;
                spread(x, y, produced);
            } else {
                for (int pulse = 0; pulse < base; pulse++) accept(x, y, x, y, false);
                neighbourPulses(x, y, false);
            }
        }
        if (heatRun) return;
        int slot = slot(x, y);
        if (damage[slot] >= rod.durability - 1) {
            parts[slot] = ReactorPart.INERT;
            damage[slot] = 0;
            spent[slot] = true;
        } else damage[slot]++;
    }

    private int neighbourPulses(int x, int y, boolean heatRun) {
        int count = 0;
        for (int[] at : around(x, y)) if (accept(at[0], at[1], x, y, heatRun)) count++;
        return count;
    }

    private boolean accept(int x, int y, int fromX, int fromY, boolean heatRun) {
        ReactorPart part = part(x, y);
        if (part == null) return false;
        switch (part.kind) {
            case FUEL -> { if (!heatRun) output += 1F; }
            case MOX -> { if (!heatRun) output += 4F * heat / maxHeat + 1F; }
            case REFLECTOR -> reflect(x, y, fromX, fromY, part, heatRun);
            default -> { return false; }
        }
        return true;
    }

    private void reflect(int x, int y, int fromX, int fromY, ReactorPart reflector, boolean heatRun) {
        if (!heatRun) {
            accept(fromX, fromY, x, y, false);
            return;
        }
        if (reflector.durability == 0) return;
        int slot = slot(x, y);
        if (damage[slot] + 1 >= reflector.durability) parts[slot] = null;
        else damage[slot]++;
    }

    private void spread(int x, int y, int amount) {
        ArrayDeque<int[]> acceptors = new ArrayDeque<>();
        for (int[] at : around(x, y)) if (ReactorHeat.accepts(this, at[0], at[1])) acceptors.add(at);
        while (!acceptors.isEmpty() && amount > 0) {
            int share = amount / acceptors.size();
            amount -= share;
            int[] at = acceptors.remove();
            amount += ReactorHeat.alter(this, at[0], at[1], share);
        }
        if (amount > 0) heat += amount;
    }

    private void vent(int x, int y, ReactorPart vent) {
        if (vent.hullRate > 0) {
            int drain = Math.min(heat, vent.hullRate);
            ReactorHeat.alter(this, x, y, drain);
            heat -= drain;
            if (part(x, y) == null) {
                vented += vent.rate;
                return;
            }
        }
        int left = ReactorHeat.alter(this, x, y, -vent.rate);
        if (left <= 0) vented += left + vent.rate;
    }

    private void cool(int x, int y, int amount) {
        if (!ReactorHeat.accepts(this, x, y)) return;
        int left = ReactorHeat.alter(this, x, y, -amount);
        if (left <= 0) vented += left + amount;
    }

    public float explosionPower() {
        float power = 10F, scale = 1F;
        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < cols; x++) {
                ReactorPart part = part(x, y);
                if (part == null) continue;
                float influence = switch (part.kind) {
                    case FUEL, MOX -> 2F * part.rate;
                    case REFLECTOR -> -1F;
                    case PLATING -> part.factor >= 1F ? 0F : part.factor;
                    default -> 0F;
                };
                if (influence > 0F && influence < 1F) scale *= influence;
                else power += influence;
            }
        }
        return power * hem * scale;
    }

    public int coolWith(int coolant, int room) {
        int hu = HU_PER_VENTED * vented;
        int converted = room > 0 && coolant > 0 ? Math.min(coolant, Math.min(hu / HU_PER_MB, room)) : 0;
        heat += (hu - converted * HU_PER_MB) / HU_PER_VENTED;
        return converted;
    }
}
