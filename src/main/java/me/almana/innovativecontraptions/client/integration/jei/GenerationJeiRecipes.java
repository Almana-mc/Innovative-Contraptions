package me.almana.innovativecontraptions.client.integration.jei;

import me.almana.innovativecontraptions.InnovativeContraptions;
import me.almana.innovativecontraptions.content.generator.GeneratorRates;
import me.almana.innovativecontraptions.content.generator.RotorItem;
import me.almana.innovativecontraptions.content.generator.RotorTier;
import me.almana.innovativecontraptions.registry.ICBlocks;
import me.almana.innovativecontraptions.registry.ICItems;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import java.util.List;

public final class GenerationJeiRecipes {
    public static final IRecipeType<GeneratorJeiRecipe> TURBINE = IRecipeType.create(
            InnovativeContraptions.MODID, "kinetic_wind_turbine", GeneratorJeiRecipe.class);

    public static GeneratorJeiRecipe wind() {
        return recipe("windmill/passive", List.of(), List.of(), List.of(),
                line("wind_passive"), line("wind_environment"),
                line("wind_formula", GeneratorRates.PASSIVE_WIND_DIVISOR, GeneratorRates.PASSIVE_WIND_OBSTRUCTION_DIVISOR),
                line("duration_continuous"));
    }

    public static GeneratorJeiRecipe kinetic() {
        return recipe("kinetic_generator/turbine", List.of(), List.of(new ItemStack(ICBlocks.KINETIC_WIND_TURBINE.get())), List.of(),
                line("kinetic_connection"), line("kinetic_conversion", GeneratorRates.KU_PER_EU), line("kinetic_no_redstone"));
    }

    public static List<GeneratorJeiRecipe> geothermal() {
        return List.of(lava(Items.LAVA_BUCKET, Items.BUCKET, "bucket"),
                lava(ICItems.LAVA_CELL.get(), ICItems.EMPTY_CELL.get(), "cell"));
    }

    private static GeneratorJeiRecipe lava(Item input, Item output, String path) {
        int total = 1000 * GeneratorRates.GEOTHERMAL_EU_PER_MB;
        int ticks = total / GeneratorRates.GEOTHERMAL_EU_PER_TICK;
        return recipe("geothermal_generator/" + path, List.of(new ItemStack(input)), List.of(), List.of(new ItemStack(output)),
                line("fluid_amount", 1000), line("energy_rate", GeneratorRates.GEOTHERMAL_EU_PER_TICK),
                line("burn_time", ticks, ticks / 20.0), line("total_energy", total), line("lava_storage", GeneratorRates.GEOTHERMAL_TANK_CAPACITY));
    }

    public static List<GeneratorJeiRecipe> water() {
        return List.of(waterFuel(Items.WATER_BUCKET, Items.BUCKET, "bucket"),
                waterFuel(ICItems.WATER_CELL.get(), ICItems.EMPTY_CELL.get(), "cell"),
                recipe("water_mill/passive", List.of(), List.of(new ItemStack(Items.WATER_BUCKET)), List.of(),
                        line("water_surrounding", GeneratorRates.WATER_MAX_BLOCKS),
                        line("water_passive", GeneratorRates.waterPassiveOutput(1), GeneratorRates.waterPassiveOutput(GeneratorRates.WATER_MAX_BLOCKS)),
                        line("water_modes_stack"), line("duration_continuous")));
    }

    private static GeneratorJeiRecipe waterFuel(Item input, Item output, String path) {
        return recipe("water_mill/" + path, List.of(new ItemStack(input)), List.of(), List.of(new ItemStack(output)),
                line("fluid_amount", 1000), line("energy_rate", GeneratorRates.WATER_BUCKET_EU_PER_TICK),
                line("burn_time", GeneratorRates.WATER_BUCKET_TICKS, GeneratorRates.WATER_BUCKET_TICKS / 20.0),
                line("total_energy", GeneratorRates.waterBucketEnergy()), line("water_modes_stack"));
    }

    public static List<GeneratorJeiRecipe> turbines() {
        return List.of(rotor(ICItems.WOOD_ROTOR.get(), "wood"), rotor(ICItems.IRON_ROTOR.get(), "iron"),
                rotor(ICItems.STEEL_ROTOR.get(), "steel"), rotor(ICItems.CARBON_ROTOR.get(), "carbon"));
    }

    private static GeneratorJeiRecipe rotor(Item item, String path) {
        RotorTier tier = ((RotorItem) item).tier();
        return recipe("kinetic_wind_turbine/" + path, List.of(new ItemStack(item)),
                List.of(new ItemStack(ICBlocks.KINETIC_GENERATOR.get())), List.of(),
                line("rotor_space", tier.radius * 2 + 1), line("rotor_wind", tier.minWind, tier.maxWind),
                line("rotor_output", tier.efficiency * GeneratorRates.WIND_KU_MULTIPLIER),
                line("rotor_lifetime", tier.durability), line("rotor_overspeed"), line("kinetic_connection"));
    }

    private static Component line(String key, Object... args) { return Component.translatable("jei.innovativecontraptions." + key, args); }
    private static GeneratorJeiRecipe recipe(String path, List<ItemStack> inputs, List<ItemStack> conditions, List<ItemStack> outputs, Component... lines) {
        return new GeneratorJeiRecipe(Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, "jei/" + path), inputs, conditions, outputs, List.of(lines));
    }
    private GenerationJeiRecipes() {}
}
