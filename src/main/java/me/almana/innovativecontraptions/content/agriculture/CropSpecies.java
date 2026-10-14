package me.almana.innovativecontraptions.content.agriculture;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import me.almana.innovativecontraptions.registry.ICItems;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public enum CropSpecies {
    EMPTY(0, 1, 0, 1, 0, 0, 0, 0, 0, ""),
    WEED(0, 5, 300, 1, 0, 0, 1, 0, 5, "weed", "bad"),
    WHEAT(1, 7, 200, 2, 0, 4, 0, 0, 2, "yellow", "food", "wheat"),
    REED(2, 3, 200, 1, 0, 0, 1, 0, 2, "reed"),
    STICKREED(4, 4, 100, 1, 2, 0, 1, 0, 1, "reed", "resin"),
    FERRU(6, 4, 800, 2, 2, 0, 0, 1, 0, "gray", "leaves", "metal"),
    COFFEE(7, 5, 1400, 3, 1, 4, 1, 2, 0, "leaves", "ingredient", "beans"),
    HOPS(5, 7, 600, 3, 2, 2, 0, 1, 1, "green", "ingredient", "wheat"),
    NETHER_WART(5, 3, 1000, 1, 4, 2, 0, 2, 1, "red", "nether", "ingredient", "soulsand"),
    TERRA_WART(5, 3, 1000, 1, 2, 4, 0, 3, 0, "blue", "aether", "consumable", "snow"),
    POTATO(2, 4, 400, 1, 0, 4, 0, 0, 2, "yellow", "food", "potato"),
    CARROT(2, 3, 400, 1, 0, 4, 0, 0, 2, "orange", "food", "carrots"),
    BEETROOT(1, 3, 200, 1, 0, 4, 0, 1, 2, "red", "food", "beetroot"),
    PUMPKIN(1, 4, 200, 3, 0, 1, 0, 3, 1, "orange", "decoration", "stem"),
    MELON(2, 4, 250, 3, 0, 4, 0, 2, 0, "green", "food", "stem"),
    COCOA(3, 4, 400, 3, 1, 3, 0, 4, 0, "brown", "food", "stem"),
    GOLDEN_BLOOM(2, 4, 400, 3, 1, 1, 0, 5, 1, "yellow", "flower"),
    SCARLET_BLOOM(2, 4, 400, 3, 1, 1, 0, 5, 1, "red", "flower", "rose"),
    INK_BLOOM(2, 4, 400, 3, 1, 1, 0, 5, 1, "black", "flower", "rose"),
    VIOLET_BLOOM(2, 4, 400, 3, 1, 1, 0, 5, 1, "purple", "flower", "tulip"),
    AZURE_BLOOM(2, 4, 400, 3, 1, 1, 0, 5, 1, "blue", "flower"),
    VENOM_BLOOM(3, 6, 400, 3, 3, 1, 3, 3, 3, "purple", "flower", "tulip", "poison"),
    FLAX(2, 4, 400, 1, 1, 1, 2, 0, 1, "silk", "vine", "addictive"),
    GOLD_LEAF(6, 5, 750, 2, 2, 0, 0, 2, 0, "gold", "leaves", "metal"),
    RED_GRAIN(6, 7, 600, 2, 3, 0, 0, 2, 0, "red", "redstone", "wheat"),
    RED_MUSHROOM(2, 3, 200, 1, 0, 4, 0, 0, 4, "red", "food", "mushroom"),
    BROWN_MUSHROOM(2, 3, 200, 1, 0, 4, 0, 0, 4, "brown", "food", "mushroom"),
    PREDATOR_VINE(6, 6, 1200, 1, 1, 1, 3, 1, 4, "bad", "food"),
    COPPER_LEAF(6, 4, 800, 2, 2, 0, 0, 1, 0, "orange", "leaves", "metal"),
    TIN_LEAF(6, 4, 800, 2, 2, 0, 0, 1, 0, "shiny", "leaves", "metal"),
    LEAD_LEAF(6, 4, 800, 2, 2, 0, 0, 1, 0, "dense", "leaves", "metal"),
    SILVER_LEAF(6, 5, 750, 2, 2, 0, 0, 2, 0, "silver", "leaves", "metal"),
    OAK(3, 5, 600, 4, 1, 0, 4, 4, 0, "leaves", "sapling", "green"),
    SPRUCE(3, 5, 600, 4, 1, 0, 4, 4, 0, "leaves", "sapling", "green"),
    BIRCH(3, 5, 600, 4, 1, 0, 4, 4, 0, "leaves", "sapling", "green"),
    JUNGLE(3, 5, 600, 4, 1, 0, 4, 4, 0, "leaves", "sapling", "green"),
    ACACIA(3, 5, 600, 4, 1, 0, 4, 4, 0, "leaves", "sapling", "green"),
    DARK_OAK(3, 5, 600, 4, 1, 0, 4, 4, 0, "leaves", "sapling", "green"),
    EMBER_REED(6, 4, 1200, 1, 0, 4, 1, 0, 0, "fire", "blaze", "reed", "sulfur"),
    EMERALD_BERRY(11, 4, 2200, 1, 4, 0, 8, 2, 9, "shiny", "vine", "emerald", "berylium", "crystal"),
    HIDE_VINE(6, 4, 1200, 1, 0, 2, 3, 1, 0, "cow", "silk", "vine"),
    ROT_VINE(5, 4, 1000, 1, 0, 2, 1, 0, 3, "toxic", "undead", "vine", "edible", "rotten"),
    BLAST_VINE(7, 4, 1400, 1, 3, 0, 5, 1, 3, "creeper", "vine", "explosive", "fire", "sulfur", "saltpeter", "coal"),
    CRYSTAL_REED(12, 4, 2400, 1, 5, 0, 10, 2, 10, "fire", "shiny", "reed", "coal", "diamond", "crystal"),
    EGG_BLOOM(6, 3, 5400, 2, 0, 4, 1, 0, 0, "chicken", "egg", "edible", "feather", "flower", "addictive"),
    ENDER_BLOOM(10, 4, 2000, 1, 5, 0, 2, 1, 6, "ender", "flower", "shiny"),
    MEAT_BLOOM(7, 4, 10500, 1, 0, 4, 1, 3, 0, "edible", "flower", "cow", "chicken", "pig", "sheep"),
    MILK_WART(6, 3, 5400, 1, 0, 3, 0, 1, 0, "edible", "milk", "cow"),
    OIL_BERRY(9, 3, 1800, 1, 6, 1, 2, 1, 12, "fire", "dark", "reed", "rotten", "coal", "oil"),
    SLIME_BUSH(6, 4, 1200, 3, 3, 0, 0, 0, 2, "slime", "bouncy", "sticky", "bush"),
    SILK_BLOOM(4, 4, 2400, 1, 2, 1, 4, 1, 3, "toxic", "silk", "spider", "flower", "ingredient", "addictive"),
    TEAR_REED(8, 4, 1600, 1, 1, 2, 0, 0, 0, "healing", "nether", "ingredient", "reed", "ghast"),
    WITHER_REED(8, 4, 1600, 1, 2, 0, 4, 1, 3, "fire", "undead", "reed", "coal", "rotten", "wither");

    public final int tier, stages, points, regrowth;
    public final int[] traits;
    public final Set<String> attributes;
    public final List<String> attributeNames;
    CropSpecies(int tier, int stages, int points, int regrowth, int chemistry, int food,
                int defense, int color, int weed, String... attributes) {
        this.tier = tier; this.stages = stages; this.points = points; this.regrowth = regrowth;
        this.traits = new int[]{chemistry, food, defense, color, weed};
        this.attributeNames = List.of(attributes);
        this.attributes = Set.copyOf(attributeNames);
    }
    public String id() { return name().toLowerCase(Locale.ROOT); }
    public String attributeText() {
        return attributeNames.stream().filter(name -> !name.isEmpty())
                .map(name -> Character.toUpperCase(name.charAt(0)) + name.substring(1)).collect(Collectors.joining(", "));
    }
    public String discoverer() {
        return switch (this) {
            case REED, NETHER_WART, COCOA, GOLDEN_BLOOM, SCARLET_BLOOM -> "Notch";
            case STICKREED, RED_GRAIN -> "raa1337";
            case COFFEE -> "Snoochy";
            case MELON -> "Chao";
            case INK_BLOOM, VIOLET_BLOOM, AZURE_BLOOM -> "Alblaka";
            case VENOM_BLOOM -> "raGan";
            case FLAX -> "Eloraam";
            case PREDATOR_VINE -> "Hasudako";
            case OAK, SPRUCE, BIRCH, JUNGLE, ACACIA, DARK_OAK -> "Speiger";
            case EMBER_REED, MILK_WART -> "Mr. Brain";
            case ROT_VINE, SILK_BLOOM -> "Mr. Kenny";
            case EMERALD_BERRY -> "GenerikB";
            case HIDE_VINE -> "Gregorius Techneticies";
            case BLAST_VINE -> "General Spaz";
            case CRYSTAL_REED -> "Diareed";
            case EGG_BLOOM -> "Link";
            case ENDER_BLOOM -> "RichardG";
            case MEAT_BLOOM -> "VintageBeef";
            case OIL_BERRY -> "Spacetoad";
            case SLIME_BUSH, TEAR_REED -> "Neowulf";
            case WITHER_REED -> "CovertJaguar";
            default -> "IC2 Team";
        };
    }
    public static CropSpecies byName(String id) {
        for (var crop : values()) if (crop.id().equals(id)) return crop;
        return EMPTY;
    }
    public int threshold(int age) {
        if (commonMetal() && age == 3) return 2000;
        if ((this == GOLD_LEAF || this == SILVER_LEAF) && age == 4) return 2200;
        if (this == COFFEE) return age == 3 ? 700 : age == 4 ? 2100 : 1400;
        if (this == PUMPKIN && age == 3 || flower() && age == 3 || this == VENOM_BLOOM && age >= 3) return 600;
        if (this == MELON && age == 3) return 700;
        if (this == COCOA && age == 3) return 900;
        if (sapling() && age >= 4) return 150;
        return points;
    }
    public boolean generic() { return ordinal() >= EMBER_REED.ordinal(); }
    public boolean commonMetal() { return this == FERRU || this == COPPER_LEAF || this == TIN_LEAF || this == LEAD_LEAF; }
    public boolean sapling() { return ordinal() >= OAK.ordinal() && ordinal() <= DARK_OAK.ordinal(); }
    public boolean flower() { return ordinal() >= GOLDEN_BLOOM.ordinal() && ordinal() <= AZURE_BLOOM.ordinal(); }
    public int optimal() { return this == POTATO ? 3 : this == VENOM_BLOOM || this == PREDATOR_VINE ? 4 : stages; }
    public boolean harvestable(int age) {
        if (this == EMPTY || this == WEED) return false;
        if (this == REED || this == STICKREED) return age > 1;
        if (this == COFFEE) return age >= 4;
        if (this == PREDATOR_VINE) return age == 4 || age == 5;
        return age >= optimal();
    }
    public double yieldChance() { return this == RED_GRAIN ? .5 : this == NETHER_WART ? 2 : this == TERRA_WART ? .8 : Math.pow(.95, tier) * (commonMetal() ? .5 : 1); }
    public ItemStack produce(int age, RandomSource random) {
        return switch (this) {
            case EMPTY, WEED -> ItemStack.EMPTY;
            case WHEAT -> new ItemStack(Items.WHEAT);
            case REED -> new ItemStack(Items.SUGAR_CANE, age - 1);
            case STICKREED -> age == 4 ? new ItemStack(ICItems.STICKY_RESIN.get()) : new ItemStack(Items.SUGAR_CANE, age - 1);
            case FERRU -> new ItemStack(ICItems.SMALL_IRON_DUST.get());
            case COFFEE -> age == 5 ? new ItemStack(ICItems.COFFEE_BEANS.get()) : ItemStack.EMPTY;
            case HOPS -> new ItemStack(ICItems.HOPS.get());
            case NETHER_WART -> new ItemStack(Items.NETHER_WART);
            case TERRA_WART -> new ItemStack(ICItems.TERRA_WART.get());
            case POTATO -> new ItemStack(age == 4 && random.nextInt(20) == 0 ? Items.POISONOUS_POTATO : Items.POTATO);
            case CARROT -> new ItemStack(Items.CARROT);
            case BEETROOT -> new ItemStack(Items.BEETROOT);
            case PUMPKIN -> new ItemStack(Items.PUMPKIN);
            case MELON -> random.nextInt(3) == 0 ? new ItemStack(Items.MELON) : new ItemStack(Items.MELON_SLICE, 2 + random.nextInt(4));
            case COCOA -> new ItemStack(Items.COCOA_BEANS);
            default -> CropProducts.produce(this, age);
        };
    }
    public static CropGenome baseSeed(ItemStack stack) {
        CropSpecies crop = EMPTY;
        if (stack.is(Items.WHEAT_SEEDS)) crop = WHEAT;
        else if (stack.is(Items.SUGAR_CANE)) crop = REED;
        else if (stack.is(Items.NETHER_WART)) crop = NETHER_WART;
        else if (stack.is(ICItems.TERRA_WART.get())) crop = TERRA_WART;
        else if (stack.is(ICItems.COFFEE_BEANS.get())) crop = COFFEE;
        else if (stack.is(Items.POTATO)) crop = POTATO;
        else if (stack.is(Items.CARROT)) crop = CARROT;
        else if (stack.is(Items.BEETROOT_SEEDS)) crop = BEETROOT;
        else if (stack.is(Items.PUMPKIN_SEEDS)) crop = PUMPKIN;
        else if (stack.is(Items.MELON_SEEDS)) crop = MELON;
        else if (stack.is(Items.COCOA_BEANS)) crop = COCOA;
        if (crop == EMPTY) return CropGenome.EMPTY;
        if (crop == REED) return new CropGenome(crop.id(), 3, 0, 2, 0);
        if (crop == COCOA) return new CropGenome(crop.id(), 0, 0, 0, 0);
        return new CropGenome(crop.id(), 1, 1, 1, 0);
    }
}
