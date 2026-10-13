package me.almana.innovativecontraptions.client.reactor;

import java.util.List;
import me.almana.innovativecontraptions.InnovativeContraptions;
import me.almana.innovativecontraptions.client.integration.jei.GeneratorJeiRecipe;
import me.almana.innovativecontraptions.client.integration.jei.GeneratorRecipeCategory;
import me.almana.innovativecontraptions.client.screen.HeatExchangerScreen;
import me.almana.innovativecontraptions.content.reactor.CondensatorRefill;
import me.almana.innovativecontraptions.content.reactor.CoolantInjectorBlockEntity;
import me.almana.innovativecontraptions.content.reactor.CoolantInjectorMenu;
import me.almana.innovativecontraptions.content.reactor.ReactorContent;
import me.almana.innovativecontraptions.content.reactor.ReactorGrid;
import me.almana.innovativecontraptions.content.thermal.HeatExchangerMenu;
import me.almana.innovativecontraptions.content.thermal.LiquidHeatExchangerBlockEntity;
import me.almana.innovativecontraptions.content.thermal.ThermalContent;
import me.almana.innovativecontraptions.registry.ICBlocks;
import me.almana.innovativecontraptions.registry.ICItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.registration.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

@JeiPlugin
public final class ReactorJei implements IModPlugin {
    private static final IRecipeType<GeneratorJeiRecipe> EXCHANGE = IRecipeType.create(InnovativeContraptions.MODID, "liquid_heat_exchanger", GeneratorJeiRecipe.class);
    private static final IRecipeType<GeneratorJeiRecipe> INJECTION = IRecipeType.create(InnovativeContraptions.MODID, "coolant_injector", GeneratorJeiRecipe.class);

    @Override public Identifier getPluginUid() { return id("reactor_jei"); }

    @Override public void registerCategories(IRecipeCategoryRegistration registration) {
        var gui = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(
                new GeneratorRecipeCategory(gui, EXCHANGE, Component.translatable("jei.innovativecontraptions.category.liquid_heat_exchanger"),
                        ICBlocks.LIQUID_HEAT_EXCHANGER.get()),
                new GeneratorRecipeCategory(gui, INJECTION, Component.translatable("jei.innovativecontraptions.category.coolant_injector"),
                        ICBlocks.RSH_COOLANT_INJECTOR.get()));
    }

    @Override public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(EXCHANGE, List.of(new GeneratorJeiRecipe(id("jei/liquid_heat_exchanger"),
                List.of(new ItemStack(ICItems.HOT_COOLANT_CELL.get())), List.of(new ItemStack(ICItems.HEAT_CONDUCTOR.get())),
                List.of(new ItemStack(ICItems.COOLANT_CELL.get())), List.of(
                Component.translatable("jei.innovativecontraptions.exchanger_rate", LiquidHeatExchangerBlockEntity.HU_PER_MB),
                Component.translatable("jei.innovativecontraptions.exchanger_conductors",
                        LiquidHeatExchangerBlockEntity.HU_PER_CONDUCTOR, LiquidHeatExchangerBlockEntity.CONDUCTORS),
                Component.translatable("jei.innovativecontraptions.exchanger_front")))));
        registration.addRecipes(INJECTION, List.of(injection(false), injection(true)));
        info(registration);
    }

    private static GeneratorJeiRecipe injection(boolean lzh) {
        ItemStack condensator = new ItemStack(lzh ? ICItems.LZH_CONDENSATOR.get() : ICItems.RSH_CONDENSATOR.get());
        ItemStack injector = new ItemStack(lzh ? ICItems.LZH_COOLANT_INJECTOR.get() : ICItems.RSH_COOLANT_INJECTOR.get());
        return new GeneratorJeiRecipe(id(lzh ? "jei/lzh_coolant_injector" : "jei/rsh_coolant_injector"),
                List.of(new ItemStack(CoolantInjectorBlockEntity.refill(lzh))), List.of(condensator, injector), List.of(condensator), List.of(
                Component.translatable("jei.innovativecontraptions.injector_refill",
                        Math.round(CoolantInjectorBlockEntity.REFILL_AT * 100), CoolantInjectorBlockEntity.REFILL_EU),
                Component.translatable("jei.innovativecontraptions.injector_behind")));
    }

    private static void info(IRecipeRegistration registration) {
        registration.addIngredientInfo(ICBlocks.NUCLEAR_REACTOR.get(),
                Component.translatable("jei.innovativecontraptions.info.nuclear_reactor", ReactorGrid.EU_PER_OUTPUT));
        registration.addIngredientInfo(ICBlocks.REACTOR_CHAMBER.get(),
                Component.translatable("jei.innovativecontraptions.info.reactor_chamber", ReactorGrid.MAX_COLS - ReactorGrid.BASE_COLS));
        registration.addIngredientInfo(ICBlocks.REACTOR_VESSEL.get(),
                Component.translatable("jei.innovativecontraptions.info.reactor_vessel", ReactorGrid.HU_PER_MB));
        registration.addIngredientInfo(ICBlocks.REACTOR_ACCESS_HATCH.get(), Component.translatable("jei.innovativecontraptions.info.reactor_access_hatch"));
        registration.addIngredientInfo(ICBlocks.REACTOR_REDSTONE_PORT.get(), Component.translatable("jei.innovativecontraptions.info.reactor_redstone_port"));
        registration.addIngredientInfo(ICBlocks.REACTOR_FLUID_PORT.get(), Component.translatable("jei.innovativecontraptions.info.reactor_fluid_port"));
        registration.addIngredientInfo(ICBlocks.COMPACT_ITEM_BUFFER.get(), Component.translatable("jei.innovativecontraptions.info.compact_item_buffer"));
        registration.addIngredientInfo(ICItems.RSH_CONDENSATOR.get(),
                Component.translatable("jei.innovativecontraptions.info.rsh_condensator", CondensatorRefill.RSH_REDSTONE));
        registration.addIngredientInfo(ICItems.LZH_CONDENSATOR.get(),
                Component.translatable("jei.innovativecontraptions.info.lzh_condensator", CondensatorRefill.LZH_REDSTONE, CondensatorRefill.LZH_LAPIS));
        for (var injector : List.of(ICBlocks.RSH_COOLANT_INJECTOR.get(), ICBlocks.LZH_COOLANT_INJECTOR.get()))
            registration.addIngredientInfo(injector, Component.translatable("jei.innovativecontraptions.info.coolant_injector",
                    Math.round(CoolantInjectorBlockEntity.REFILL_AT * 100), CoolantInjectorBlockEntity.REFILL_EU));
    }

    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addCraftingStation(EXCHANGE, ICBlocks.LIQUID_HEAT_EXCHANGER.get());
        registration.addCraftingStation(INJECTION, ICBlocks.RSH_COOLANT_INJECTOR.get());
        registration.addCraftingStation(INJECTION, ICBlocks.LZH_COOLANT_INJECTOR.get());
    }

    @Override public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(HeatExchangerMenu.class, ThermalContent.EXCHANGER_MENU.get(), EXCHANGE,
                LiquidHeatExchangerBlockEntity.HOT_IN, 1, LiquidHeatExchangerBlockEntity.SLOTS, 36);
        registration.addRecipeTransferHandler(CoolantInjectorMenu.class, ReactorContent.INJECTOR_MENU.get(), INJECTION,
                0, CoolantInjectorBlockEntity.REFILLS, CoolantInjectorBlockEntity.SLOTS, 36);
    }

    @Override public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(HeatExchangerScreen.class, HeatExchangerScreen.RECIPE_X, HeatExchangerScreen.RECIPE_Y,
                HeatExchangerScreen.RECIPE_WIDTH, HeatExchangerScreen.RECIPE_HEIGHT, EXCHANGE);
        registration.addRecipeClickArea(CoolantInjectorScreen.class, CoolantInjectorScreen.RECIPE_X, CoolantInjectorScreen.RECIPE_Y,
                CoolantInjectorScreen.RECIPE_WIDTH, CoolantInjectorScreen.RECIPE_HEIGHT, INJECTION);
    }

    private static Identifier id(String path) { return Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, path); }
}
