package me.almana.innovativecontraptions.content.reactor;

final class ReactorHeat {
    static boolean accepts(ReactorGrid grid, int x, int y) {
        ReactorPart part = grid.part(x, y);
        if (part == null || !part.holdsHeat()) return false;
        return part.kind != ReactorPart.Kind.CONDENSATOR || grid.damage(x, y) < part.durability;
    }

    static int alter(ReactorGrid grid, int x, int y, int heat) {
        ReactorPart part = grid.part(x, y);
        int stored = grid.damage(x, y);
        if (part.kind == ReactorPart.Kind.CONDENSATOR) {
            if (heat < 0) return heat;
            int taken = Math.min(heat, part.durability - stored);
            grid.setDamage(x, y, stored + taken);
            return heat - taken;
        }
        int total = stored + heat;
        if (total > part.durability) {
            grid.destroy(x, y);
            // ponytail: IC2 negative overflow kept
            return part.durability - total + 1;
        }
        if (total < 0) {
            grid.setDamage(x, y, 0);
            return total;
        }
        grid.setDamage(x, y, total);
        return 0;
    }

    static void exchange(ReactorGrid grid, int x, int y, ReactorPart self) {
        int change = 0;
        double mine = grid.damage(x, y) * 100.0 / self.durability;
        if (self.rate > 0) {
            for (int[] at : grid.around(x, y)) {
                if (!accepts(grid, at[0], at[1])) continue;
                int capacity = grid.part(at[0], at[1]).durability;
                double theirs = grid.damage(at[0], at[1]) * 100.0 / capacity;
                int add = direction(step(capacity / 100.0 * (theirs + mine / 2.0), theirs + mine / 2.0, self.rate, self.rate), theirs, mine);
                change -= add;
                change += alter(grid, at[0], at[1], add);
            }
        }
        if (self.hullRate > 0) {
            double hull = grid.heat() * 100.0 / grid.maxHeat();
            int add = direction(step(Math.round(grid.maxHeat() / 100.0 * (hull + mine / 2.0)), hull + mine / 2.0, self.hullRate, self.rate), hull, mine);
            change -= add;
            grid.setHeat(grid.heat() + add);
        }
        alter(grid, x, y, change);
    }

    private static int step(double amount, double level, int cap, int side) {
        int add = (int) amount;
        if (add > cap) add = cap;
        if (level < 1.0) add = side / 2;
        if (level < 0.75) add = side / 4;
        if (level < 0.5) add = side / 8;
        if (level < 0.25) add = 1;
        return add;
    }

    private static int direction(int add, double theirs, double mine) {
        double other = Math.round(theirs * 10.0) / 10.0;
        double own = Math.round(mine * 10.0) / 10.0;
        if (other > own) return -add;
        return other == own ? 0 : add;
    }

    private ReactorHeat() {}
}
