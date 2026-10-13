package me.almana.innovativecontraptions.content.agriculture;

import java.util.ArrayList;
import java.util.List;
import me.almana.innovativecontraptions.registry.ICItems;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

public final class CropProducts {
    public static ItemStack produce(CropSpecies crop, int age) {
        ItemLike product = switch (crop) {
            case GOLDEN_BLOOM -> Items.YELLOW_DYE;
            case SCARLET_BLOOM -> Items.RED_DYE;
            case INK_BLOOM -> Items.BLACK_DYE;
            case VIOLET_BLOOM -> Items.PURPLE_DYE;
            case AZURE_BLOOM -> Items.CYAN_DYE;
            case VENOM_BLOOM -> age == 5 ? ICItems.CROP_TREATMENT_POWDER.get() : Items.PURPLE_DYE;
            case FLAX, SILK_BLOOM -> Items.STRING;
            case GOLD_LEAF -> ICItems.SMALL_GOLD_DUST.get();
            case COPPER_LEAF -> ICItems.SMALL_COPPER_DUST.get();
            case TIN_LEAF -> ICItems.SMALL_TIN_DUST.get();
            case LEAD_LEAF -> ICItems.SMALL_LEAD_DUST.get();
            case SILVER_LEAF -> ICItems.SMALL_SILVER_DUST.get();
            case RED_GRAIN -> Items.REDSTONE;
            case RED_MUSHROOM -> Items.RED_MUSHROOM;
            case BROWN_MUSHROOM -> Items.BROWN_MUSHROOM;
            case PREDATOR_VINE -> Items.CACTUS;
            case OAK -> Items.OAK_LOG;
            case SPRUCE -> Items.SPRUCE_LOG;
            case BIRCH -> Items.BIRCH_LOG;
            case JUNGLE -> Items.JUNGLE_LOG;
            case ACACIA -> Items.ACACIA_LOG;
            case DARK_OAK -> Items.DARK_OAK_LOG;
            case EMBER_REED -> Items.BLAZE_POWDER;
            case EMERALD_BERRY -> ICItems.EMERALD_BERRY.get();
            case HIDE_VINE -> Items.LEATHER;
            case ROT_VINE -> Items.ROTTEN_FLESH;
            case BLAST_VINE -> Items.GUNPOWDER;
            case CRYSTAL_REED -> ICItems.SMALL_DIAMOND_DUST.get();
            case EGG_BLOOM -> Items.EGG;
            case ENDER_BLOOM -> ICItems.ENDER_PEARL_DUST.get();
            case MEAT_BLOOM -> Items.PINK_DYE;
            case MILK_WART -> ICItems.MILK_WART.get();
            case OIL_BERRY -> ICItems.OIL_BERRY.get();
            case SLIME_BUSH -> Items.SLIME_BALL;
            case TEAR_REED -> Items.GHAST_TEAR;
            case WITHER_REED -> ICItems.COAL_DUST.get();
            default -> Items.AIR;
        };
        return new ItemStack(product);
    }
    public static List<ItemStack> special(CropPlotBlockEntity crop) {
        var random = crop.getLevel().getRandom();
        List<ItemStack> drops = new ArrayList<>();
        ItemLike[] options = specialOptions(crop.species());
        if (options.length > 0) {
            int choice = random.nextInt(options.length * 2 + 2);
            if (choice < options.length) drops.add(new ItemStack(options[choice]));
        }
        if (crop.species().sapling() && random.nextInt(4) == 0) drops.add(new ItemStack(sapling(crop.species())));
        if (crop.species() == CropSpecies.OAK && random.nextInt(4) == 0) drops.add(new ItemStack(Items.APPLE));
        return drops;
    }
    private static ItemLike[] specialOptions(CropSpecies crop) {
        return switch (crop) {
            case EMBER_REED -> new ItemLike[]{Items.BLAZE_ROD, ICItems.SULFUR_DUST.get()};
            case EMERALD_BERRY -> new ItemLike[]{Items.EMERALD};
            case ROT_VINE -> new ItemLike[]{Items.BONE, Items.BONE_MEAL, Items.BONE_MEAL};
            case CRYSTAL_REED -> new ItemLike[]{Items.DIAMOND};
            case EGG_BLOOM -> new ItemLike[]{Items.CHICKEN, Items.FEATHER, Items.FEATHER, Items.FEATHER};
            case ENDER_BLOOM -> new ItemLike[]{Items.ENDER_PEARL, Items.ENDER_PEARL, Items.ENDER_EYE};
            case MEAT_BLOOM -> new ItemLike[]{Items.BEEF, Items.PORKCHOP, Items.CHICKEN, Items.MUTTON};
            case SILK_BLOOM -> new ItemLike[]{Items.SPIDER_EYE, Items.COBWEB};
            case WITHER_REED -> new ItemLike[]{Items.COAL, Items.COAL};
            default -> new ItemLike[0];
        };
    }
    public static List<ItemStack> possible(CropSpecies crop) {
        List<ItemStack> products = new ArrayList<>();
        var random = RandomSource.create(0);
        for (int age = 2; age <= crop.stages; age++) {
            if (!crop.harvestable(age)) continue;
            ItemStack product = crop.produce(age, random);
            if (!product.isEmpty() && products.stream().noneMatch(stack -> stack.is(product.getItem()))) products.add(product.copyWithCount(1));
        }
        for (ItemLike item : specialOptions(crop)) if (products.stream().noneMatch(stack -> stack.is(item.asItem()))) products.add(new ItemStack(item));
        if (crop.sapling()) products.add(new ItemStack(sapling(crop)));
        if (crop == CropSpecies.OAK) products.add(new ItemStack(Items.APPLE));
        if (crop == CropSpecies.POTATO) products.add(new ItemStack(Items.POISONOUS_POTATO));
        if (crop == CropSpecies.RED_GRAIN) products.add(new ItemStack(Items.WHEAT));
        if (crop == CropSpecies.MELON) {
            products.clear(); products.add(new ItemStack(Items.MELON)); products.add(new ItemStack(Items.MELON_SLICE));
        }
        return products;
    }
    private static ItemLike sapling(CropSpecies crop) {
        return switch (crop) {
            case SPRUCE -> Items.SPRUCE_SAPLING; case BIRCH -> Items.BIRCH_SAPLING;
            case JUNGLE -> Items.JUNGLE_SAPLING; case ACACIA -> Items.ACACIA_SAPLING;
            case DARK_OAK -> Items.DARK_OAK_SAPLING; default -> Items.OAK_SAPLING;
        };
    }
    private CropProducts() {}
}
