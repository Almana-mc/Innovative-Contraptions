package me.almana.innovativecontraptions.client.matter;

import me.almana.innovativecontraptions.InnovativeContraptions;
import me.almana.innovativecontraptions.client.integration.jei.GeneratorJeiRecipe;
import me.almana.innovativecontraptions.client.integration.jei.GeneratorRecipeCategory;
import me.almana.innovativecontraptions.content.matter.*;
import me.almana.innovativecontraptions.registry.*;
import mezz.jei.api.*;
import mezz.jei.api.gui.handlers.*;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import mezz.jei.api.recipe.types.*;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import java.util.*;

@JeiPlugin
public final class MatterJeiPlugin implements IModPlugin {
    static final IRecipeType<MatterScanDisplay> SCANNING = IRecipeType.create(InnovativeContraptions.MODID, "matter_scanning", MatterScanDisplay.class);
    private static final IRecipeType<GeneratorJeiRecipe> SYNTHESIS = IRecipeType.create(InnovativeContraptions.MODID, "matter_synthesis", GeneratorJeiRecipe.class);
    private static final IRecipeType<GeneratorJeiRecipe> ARCHIVE = IRecipeType.create(InnovativeContraptions.MODID, "matter_archive", GeneratorJeiRecipe.class);
    private static final IRecipeType<GeneratorJeiRecipe> REPLICATION = IRecipeType.create(InnovativeContraptions.MODID, "matter_replication", GeneratorJeiRecipe.class);
    private IJeiRuntime runtime;
    private Map<Item, Integer> displayedCosts = Map.of();
    private List<MatterScanDisplay> scans = List.of();
    private List<GeneratorJeiRecipe> replicas = List.of();
    public MatterJeiPlugin() { NeoForge.EVENT_BUS.addListener(this::receiveCosts); }
    @Override public Identifier getPluginUid() { return id("matter_jei"); }
    @Override public void registerCategories(IRecipeCategoryRegistration registration) {
        var gui = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new MatterRecipeCategory(gui),
                new GeneratorRecipeCategory(gui, SYNTHESIS, text("synthesis"), ICBlocks.MATTER_FABRICATOR.get()),
                new GeneratorRecipeCategory(gui, REPLICATION, text("replication"), ICBlocks.REPLICATOR.get()),
                new GeneratorRecipeCategory(gui, ARCHIVE, text("archive"), ICBlocks.PATTERN_STORAGE.get()));
    }
    @Override public void registerRecipes(IRecipeRegistration registration) {
        rebuild();
        registration.addRecipes(SCANNING, scans);
        registration.addRecipes(REPLICATION, replicas);
        registration.addRecipes(SYNTHESIS, List.of(new GeneratorJeiRecipe(id("jei/synthesis"), List.of(), List.of(), List.of(), List.of(
                Component.literal(MatterBlockEntity.SYNTHESIS_EU + " EU produces 1 mB; tier 3"), Component.literal("Scrap: 5,000 EU of 6x amplification; box: 45,000 EU."),
                Component.literal("Runs without scrap. Redstone pauses synthesis. Tank: 8,000 mB."))),
                new GeneratorJeiRecipe(id("jei/amplification"), List.of(new ItemStack(ICItems.SCRAP.get()), new ItemStack(ICItems.SCRAP_BOX.get())), List.of(), List.of(), List.of(
                        Component.literal("Each boosted EU supplies six progress. Scrap/box credits: 5,000 / 45,000 EU."))),
                new GeneratorJeiRecipe(id("jei/matter_cell"), List.of(new ItemStack(ICItems.EMPTY_CELL.get())), List.of(), List.of(new ItemStack(ICItems.UU_MATTER_CELL.get())), List.of(
                        Component.literal("Fill a Universal Fluid Cell with 1,000 mB. Producing that matter without scrap requires 1,000,000,000 EU."),
                        Component.literal("Tier 3. Cell goes in the supply slot; scrap is optional in the separate amplifier slot. Partial cells remain until full.")))));
        registration.addRecipes(ARCHIVE, List.of(new GeneratorJeiRecipe(id("jei/archive"), List.of(new ItemStack(ICItems.CRYSTAL_MEMORY.get())), List.of(), List.of(), List.of(
                Component.literal("Import recorded memory; Export writes selected pattern."), Component.literal("No energy required. Place beside scanner or replicator.")))));
    }
    private void rebuild() {
        displayedCosts = MatterCostSync.clientCosts;
        var nextScans = new ArrayList<MatterScanDisplay>();
        var nextReplicas = new ArrayList<GeneratorJeiRecipe>();
        displayedCosts.keySet().stream().sorted(Comparator.comparing(BuiltInRegistries.ITEM::getKey)).forEach(item -> {
            int cost = displayedCosts.get(item);
            var key = BuiltInRegistries.ITEM.getKey(item);
            var recipeId = id("matter/" + key.getNamespace() + "/" + key.getPath());
            nextScans.add(new MatterScanDisplay(recipeId.withSuffix("/memory"), item, cost, false));
            nextScans.add(new MatterScanDisplay(recipeId.withSuffix("/archive"), item, cost, true));
            int ticks = Math.ceilDiv(cost, 100);
            ItemStack memory = new ItemStack(ICItems.CRYSTAL_MEMORY.get()); MatterMemoryItem.record(memory, new ItemStack(item));
            nextReplicas.add(new GeneratorJeiRecipe(recipeId, List.of(new ItemStack(ICItems.UU_MATTER_CELL.get())), List.of(memory), List.of(new ItemStack(item)), List.of(
                    Component.literal(cost / 1000.0 + " mB consumed, " + ticks + " ticks, " + MatterBlockEntity.REPLICA_EU + " EU/t = " + (long) ticks * MatterBlockEntity.REPLICA_EU + " EU."),
                    Component.literal("Requires this pattern in an adjacent archive; tier 4. Cell is a supply container; returns empty when drained."))));
        });
        scans = List.copyOf(nextScans); replicas = List.copyOf(nextReplicas);
    }
    private void receiveCosts(MatterCostSync.Received event) {
        var minecraft = Minecraft.getInstance();
        if (minecraft.isSameThread()) refresh(); else minecraft.execute(this::refresh);
    }
    private void refresh() {
        if (runtime == null || displayedCosts.equals(MatterCostSync.clientCosts)) return;
        var recipes = runtime.getRecipeManager();
        recipes.hideRecipes(SCANNING, scans); recipes.hideRecipes(REPLICATION, replicas);
        rebuild();
        recipes.addRecipes(SCANNING, scans); recipes.addRecipes(REPLICATION, replicas);
    }
    @Override public void onRuntimeAvailable(IJeiRuntime runtime) { this.runtime = runtime; refresh(); }
    @Override public void onRuntimeUnavailable() { runtime = null; displayedCosts = Map.of(); scans = List.of(); replicas = List.of(); }
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addCraftingStation(SCANNING, ICBlocks.PATTERN_SCANNER.get()); registration.addCraftingStation(REPLICATION, ICBlocks.REPLICATOR.get());
        registration.addCraftingStation(SYNTHESIS, ICBlocks.MATTER_FABRICATOR.get()); registration.addCraftingStation(ARCHIVE, ICBlocks.PATTERN_STORAGE.get());
    }
    @Override public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(transfer(SCANNING, 1, 0, 2));
        registration.addRecipeTransferHandler(transfer(SYNTHESIS, 0, 0, 1));
        registration.addRecipeTransferHandler(transfer(ARCHIVE, 2, 1, 2));
        registration.addRecipeTransferHandler(transfer(REPLICATION, 3, 1, 2));
    }
    private static <R> IRecipeTransferInfo<MatterMenu, R> transfer(IRecipeType<R> type, int kind, int start, int end) {
        return new IRecipeTransferInfo<>() {
            public Class<MatterMenu> getContainerClass() { return MatterMenu.class; }
            public Optional<MenuType<MatterMenu>> getMenuType() { return Optional.of(MatterContent.MENU.get()); }
            public IRecipeType<R> getRecipeType() { return type; }
            public boolean canHandle(MatterMenu menu, R recipe) { return menu.kind() == kind; }
            public List<Slot> getRecipeSlots(MatterMenu menu, R recipe) {
                if (recipe instanceof MatterScanDisplay scan && scan.archive()) return menu.slots.subList(0, 1);
                if (type == SYNTHESIS && recipe instanceof GeneratorJeiRecipe synthesis && synthesis.id().equals(id("jei/matter_cell"))) return menu.slots.subList(1, 2);
                return menu.slots.subList(start, end);
            }
            public List<Slot> getInventorySlots(MatterMenu menu, R recipe) { return menu.slots.subList(9, 45); }
        };
    }
    @Override public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGuiContainerHandler(MatterScreen.class, new IGuiContainerHandler<>() {
            @Override public Collection<IGuiClickableArea> getGuiClickableAreas(MatterScreen screen, double x, double y) {
                var type = screen.getMenu().kind() == 0 ? SYNTHESIS : screen.getMenu().kind() == 2 ? ARCHIVE : screen.getMenu().kind() == 3 ? REPLICATION : SCANNING;
                return List.of(IGuiClickableArea.createBasic(MatterScreen.RECIPE_X, MatterScreen.RECIPE_Y, MatterScreen.RECIPE_WIDTH, MatterScreen.RECIPE_HEIGHT, type));
            }
        });
    }
    private static Component text(String key) { return Component.translatable("matter.innovativecontraptions." + key); }
    private static Identifier id(String path) { return Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, path); }
}

