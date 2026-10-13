package me.almana.innovativecontraptions.client.integration.jei;

import me.almana.innovativecontraptions.InnovativeContraptions;
import me.almana.innovativecontraptions.client.ICClientRecipes;
import me.almana.innovativecontraptions.client.screen.ThermalScreen;
import me.almana.innovativecontraptions.content.thermal.*;
import me.almana.innovativecontraptions.registry.ICBlocks;
import me.almana.innovativecontraptions.registry.ICItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import mezz.jei.api.recipe.types.IRecipeHolderType;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.registration.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@JeiPlugin
public final class ThermalJei implements IModPlugin {
    static final Supplier<IRecipeHolderType<SteelmakingRecipe>> STEEL = IRecipeHolderType.createDeferred(ThermalContent.STEELMAKING);
    private static final IRecipeType<GeneratorJeiRecipe> HEAT = IRecipeType.create(InnovativeContraptions.MODID, "electric_heater", GeneratorJeiRecipe.class);
    @Override public Identifier getPluginUid() { return id("thermal_jei"); }
    @Override public void registerCategories(IRecipeCategoryRegistration registration) {
        var gui = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new SteelmakingCategory(gui), new GeneratorRecipeCategory(gui, HEAT,
                Component.translatable("jei.innovativecontraptions.category.electric_heater"), ICBlocks.ELECTRIC_HEATER.get()));
    }
    @Override public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(STEEL.get(), ICClientRecipes.byType(ThermalContent.STEELMAKING.get()));
        registration.addRecipes(HEAT, List.of(new GeneratorJeiRecipe(id("jei/electric_heater"),
                List.of(new ItemStack(ICItems.COIL.get())), List.of(), List.of(), List.of(
                Component.translatable("jei.innovativecontraptions.heater_coils", ElectricHeaterBlockEntity.MAX_COILS, ElectricHeaterBlockEntity.HEAT_PER_COIL),
                Component.translatable("jei.innovativecontraptions.heater_energy"),
                Component.translatable("jei.innovativecontraptions.heater_condition")))));
    }
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addCraftingStation(STEEL.get(), ICBlocks.BLAST_FURNACE.get());
        registration.addCraftingStation(STEEL.get(), ICBlocks.ELECTRIC_HEATER.get());
        registration.addCraftingStation(HEAT, ICBlocks.ELECTRIC_HEATER.get());
    }
    @Override public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(transfer(STEEL.get(), false));
        registration.addRecipeTransferHandler(transfer(HEAT, true));
    }
    private static <R> IRecipeTransferInfo<ThermalMenu, R> transfer(IRecipeType<R> type, boolean heater) {
        return new IRecipeTransferInfo<>() {
            public Class<ThermalMenu> getContainerClass() { return ThermalMenu.class; }
            public Optional<MenuType<ThermalMenu>> getMenuType() { return Optional.of(ThermalContent.MENU.get()); }
            public IRecipeType<R> getRecipeType() { return type; }
            public boolean canHandle(ThermalMenu menu, R recipe) { return menu.heater() == heater; }
            public List<Slot> getRecipeSlots(ThermalMenu menu, R recipe) { return menu.slots.subList(0, heater ? 1 : 2); }
            public List<Slot> getInventorySlots(ThermalMenu menu, R recipe) { return menu.slots.subList(menu.machineSlots(), menu.machineSlots() + 36); }
        };
    }
    @Override public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGuiContainerHandler(ThermalScreen.class, new IGuiContainerHandler<>() {
            @Override public Collection<IGuiClickableArea> getGuiClickableAreas(ThermalScreen screen, double mouseX, double mouseY) {
                if (screen.getMenu().heater()) return List.of(IGuiClickableArea.createBasic(44, 94, 40, 7, HEAT));
                return List.of(IGuiClickableArea.createBasic(ThermalScreen.RECIPE_X, ThermalScreen.RECIPE_Y,
                        ThermalScreen.RECIPE_WIDTH, ThermalScreen.RECIPE_HEIGHT, STEEL.get()));
            }
        });
    }
    private static Identifier id(String path) { return Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, path); }
}
