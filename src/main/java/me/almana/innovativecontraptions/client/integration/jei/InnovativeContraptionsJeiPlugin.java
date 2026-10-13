package me.almana.innovativecontraptions.client.integration.jei;

import me.almana.innovativecontraptions.InnovativeContraptions;
import me.almana.innovativecontraptions.client.ICClientRecipes;
import me.almana.innovativecontraptions.client.screen.GeneratorScreen;
import me.almana.innovativecontraptions.client.screen.MaceratorScreen;
import me.almana.innovativecontraptions.content.generator.GeneratorRates;
import me.almana.innovativecontraptions.content.generator.GeneratorMenu;
import me.almana.innovativecontraptions.content.generator.KineticContent;
import me.almana.innovativecontraptions.content.machine.MaceratorMenu;
import me.almana.innovativecontraptions.registry.ICBlocks;
import me.almana.innovativecontraptions.registry.ICItems;
import me.almana.innovativecontraptions.registry.ICMenus;
import me.almana.innovativecontraptions.registry.ICRecipes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
//? if >=26.1
import net.minecraft.world.level.block.entity.FuelValues;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@JeiPlugin
public final class InnovativeContraptionsJeiPlugin implements IModPlugin {
    private static final Identifier UID = id("jei");

    @Override
    public Identifier getPluginUid() {
        return UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper gui = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(
                generatorCategory(gui, ICJeiRecipeTypes.GENERATOR, "generator", ICBlocks.GENERATOR.get()),
                generatorCategory(gui, ICJeiRecipeTypes.SOLAR_PANEL, "solar_panel", ICBlocks.SOLAR_PANEL.get()),
                generatorCategory(gui, ICJeiRecipeTypes.WATER_MILL, "water_mill", ICBlocks.WATER_MILL.get()),
                generatorCategory(gui, ICJeiRecipeTypes.WINDMILL, "windmill", ICBlocks.WINDMILL.get()),
                generatorCategory(gui, ICJeiRecipeTypes.GEOTHERMAL_GENERATOR, "geothermal_generator",
                        ICBlocks.GEOTHERMAL_GENERATOR.get()),
                generatorCategory(gui, ICJeiRecipeTypes.KINETIC_GENERATOR, "kinetic_generator",
                        ICBlocks.KINETIC_GENERATOR.get()),
                generatorCategory(gui, GenerationJeiRecipes.TURBINE, "kinetic_wind_turbine",
                        ICBlocks.KINETIC_WIND_TURBINE.get()),
                new MaceratorRecipeCategory(gui, ICJeiRecipeTypes.MACERATING.get(),
                        title("macerator"), ICBlocks.MACERATOR.get())
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(ICJeiRecipeTypes.GENERATOR, fuelRecipes());
        registration.addRecipes(ICJeiRecipeTypes.SOLAR_PANEL, List.of(solarRecipe()));
        registration.addRecipes(ICJeiRecipeTypes.WATER_MILL, GenerationJeiRecipes.water());
        registration.addRecipes(ICJeiRecipeTypes.WINDMILL, List.of(GenerationJeiRecipes.wind()));
        registration.addRecipes(ICJeiRecipeTypes.GEOTHERMAL_GENERATOR, GenerationJeiRecipes.geothermal());
        registration.addRecipes(ICJeiRecipeTypes.KINETIC_GENERATOR, List.of(GenerationJeiRecipes.kinetic()));
        registration.addRecipes(GenerationJeiRecipes.TURBINE, GenerationJeiRecipes.turbines());
        registration.addRecipes(ICJeiRecipeTypes.MACERATING.get(),
                ICClientRecipes.byType(ICRecipes.MACERATING_TYPE.get()));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addCraftingStation(GenerationJeiRecipes.TURBINE, ICBlocks.KINETIC_WIND_TURBINE.get());
        registration.addCraftingStation(ICJeiRecipeTypes.GENERATOR, ICBlocks.GENERATOR.get());
        registration.addCraftingStation(ICJeiRecipeTypes.SOLAR_PANEL, ICBlocks.SOLAR_PANEL.get());
        registration.addCraftingStation(ICJeiRecipeTypes.WATER_MILL, ICBlocks.WATER_MILL.get());
        registration.addCraftingStation(ICJeiRecipeTypes.WINDMILL, ICBlocks.WINDMILL.get());
        registration.addCraftingStation(ICJeiRecipeTypes.GEOTHERMAL_GENERATOR,
                ICBlocks.GEOTHERMAL_GENERATOR.get());
        registration.addCraftingStation(ICJeiRecipeTypes.KINETIC_GENERATOR,
                ICBlocks.KINETIC_GENERATOR.get());
        registration.addCraftingStation(ICJeiRecipeTypes.MACERATING.get(), ICBlocks.MACERATOR.get());
        registration.addCraftingStation(RecipeTypes.SMELTING, ICBlocks.IRON_FURNACE.get());
        registration.addCraftingStation(RecipeTypes.SMELTING_FUEL, ICBlocks.IRON_FURNACE.get());
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(new GeneratorTransferInfo(ICJeiRecipeTypes.GENERATOR, ICBlocks.GENERATOR.get()));
        registration.addRecipeTransferHandler(new GeneratorTransferInfo(ICJeiRecipeTypes.WATER_MILL, ICBlocks.WATER_MILL.get()));
        registration.addRecipeTransferHandler(new GeneratorTransferInfo(ICJeiRecipeTypes.GEOTHERMAL_GENERATOR, ICBlocks.GEOTHERMAL_GENERATOR.get()));
        registration.addRecipeTransferHandler(GeneratorMenu.class, KineticContent.MENU.get(), GenerationJeiRecipes.TURBINE, 0, 1, 1, 36);
        registration.addRecipeTransferHandler(MaceratorMenu.class, ICMenus.MACERATOR.get(),
                ICJeiRecipeTypes.MACERATING.get(), 0, 1, MaceratorMenu.MACHINE_SLOTS, 36);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGuiContainerHandler(MaceratorScreen.class, new IGuiContainerHandler<>() {
            @Override
            public Collection<IGuiClickableArea> getGuiClickableAreas(
                    MaceratorScreen screen,
                    double mouseX,
                    double mouseY
            ) {
                return List.of(new SilentClickArea(IGuiClickableArea.createBasic(
                        MaceratorScreen.RECIPE_X,
                        MaceratorScreen.RECIPE_Y,
                        MaceratorScreen.RECIPE_WIDTH,
                        MaceratorScreen.RECIPE_HEIGHT,
                        ICJeiRecipeTypes.MACERATING.get()
                )));
            }
        });
        registration.addGuiContainerHandler(GeneratorScreen.class, new IGuiContainerHandler<>() {
            @Override
            public Collection<IGuiClickableArea> getGuiClickableAreas(
                    GeneratorScreen screen,
                    double mouseX,
                    double mouseY
            ) {
                return List.of(new SilentClickArea(IGuiClickableArea.createBasic(
                        GeneratorScreen.RECIPE_X,
                        GeneratorScreen.RECIPE_Y,
                        GeneratorScreen.RECIPE_WIDTH,
                        GeneratorScreen.RECIPE_HEIGHT,
                        generatorType(screen.getMenu().generatorBlock())
                )));
            }
        });
    }

    private static GeneratorRecipeCategory generatorCategory(
            IGuiHelper gui,
            IRecipeType<GeneratorJeiRecipe> type,
            String title,
            Block icon
    ) {
        return new GeneratorRecipeCategory(gui, type, title(title), icon);
    }

    private static List<GeneratorJeiRecipe> fuelRecipes() {
        //? if >=26.1 {
        FuelValues fuels = Minecraft.getInstance().level.fuelValues();
        return fuels.fuelItems().stream()
                .sorted(Comparator.comparing(item -> BuiltInRegistries.ITEM.getKey(item).toString()))
                .map(item -> fuelRecipe(fuels, item))
                .toList();
        //?} else {
        /*return BuiltInRegistries.ITEM.stream()
                .filter(item -> item.getDefaultInstance().getBurnTime(net.minecraft.world.item.crafting.RecipeType.SMELTING) > 0)
                .sorted(Comparator.comparing(item -> BuiltInRegistries.ITEM.getKey(item).toString()))
                .map(InnovativeContraptionsJeiPlugin::fuelRecipe)
                .toList();
        *///?}
    }

    //? if >=26.1 {
    private static GeneratorJeiRecipe fuelRecipe(FuelValues fuels, Item item) {
        ItemStack fuel = item.getDefaultInstance();
        int ticks = fuels.burnDuration(fuel);
        ItemStackTemplate remainder = item.getCraftingRemainder(fuel);
    //?} else {
    /*private static GeneratorJeiRecipe fuelRecipe(Item item) {
        ItemStack fuel = item.getDefaultInstance();
        int ticks = fuel.getBurnTime(net.minecraft.world.item.crafting.RecipeType.SMELTING);
        ItemStack leftover = item.getCraftingRemainingItem(fuel);
        ItemStackTemplate remainder = leftover.isEmpty() ? null : ItemStackTemplate.fromNonEmptyStack(leftover);
    *///?}
        List<ItemStack> outputs = remainder == null ? List.of() : List.of(remainder.create());
        Identifier itemId = BuiltInRegistries.ITEM.getKey(item);
        return recipe("generator/" + itemId.getNamespace() + "/" + itemId.getPath(),
                List.of(fuel), List.of(), outputs,
                Component.translatable("jei.innovativecontraptions.burn_time", ticks, ticks / 20.0D),
                Component.translatable("jei.innovativecontraptions.energy_rate", GeneratorRates.FUEL_EU_PER_TICK),
                Component.translatable("jei.innovativecontraptions.potential_energy",
                        GeneratorRates.fuelPotentialEnergy(ticks)));
    }

    private static GeneratorJeiRecipe solarRecipe() {
        return recipe("solar_panel/daylight", List.of(), List.of(new ItemStack(Items.DAYLIGHT_DETECTOR)), List.of(),
                Component.translatable("jei.innovativecontraptions.solar_condition"),
                Component.translatable("jei.innovativecontraptions.duration_continuous"),
                Component.translatable("jei.innovativecontraptions.energy_rate", GeneratorRates.SOLAR_EU_PER_TICK));
    }

    private static GeneratorJeiRecipe recipe(
            String path,
            List<ItemStack> inputs,
            List<ItemStack> conditions,
            List<ItemStack> outputs,
            Component... details
    ) {
        return new GeneratorJeiRecipe(id("jei/" + path), inputs, conditions, outputs, List.of(details));
    }

    private static IRecipeType<GeneratorJeiRecipe> generatorType(Block block) {
        if (block == ICBlocks.KINETIC_WIND_TURBINE.get()) return GenerationJeiRecipes.TURBINE;
        if (block == ICBlocks.SOLAR_PANEL.get()) return ICJeiRecipeTypes.SOLAR_PANEL;
        if (block == ICBlocks.WATER_MILL.get()) return ICJeiRecipeTypes.WATER_MILL;
        if (block == ICBlocks.WINDMILL.get()) return ICJeiRecipeTypes.WINDMILL;
        if (block == ICBlocks.GEOTHERMAL_GENERATOR.get()) return ICJeiRecipeTypes.GEOTHERMAL_GENERATOR;
        if (block == ICBlocks.KINETIC_GENERATOR.get()) return ICJeiRecipeTypes.KINETIC_GENERATOR;
        return ICJeiRecipeTypes.GENERATOR;
    }

    private static Component title(String path) {
        return Component.translatable("jei.innovativecontraptions.category." + path);
    }

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, path);
    }
}

