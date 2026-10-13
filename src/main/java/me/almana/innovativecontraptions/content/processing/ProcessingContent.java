package me.almana.innovativecontraptions.content.processing;

import com.mojang.serialization.Codec;
import me.almana.innovativecontraptions.InnovativeContraptions;
import me.almana.innovativecontraptions.registry.ICBlocks;
import me.almana.innovativecontraptions.registry.IcCapabilities;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.transfer.item.WorldlyContainerWrapper;
import java.util.EnumMap;

public final class ProcessingContent {
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, InnovativeContraptions.MODID);
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(BuiltInRegistries.MENU, InnovativeContraptions.MODID);
    private static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, InnovativeContraptions.MODID);
    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, InnovativeContraptions.MODID);
    private static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, InnovativeContraptions.MODID);
    private static final EnumMap<ProcessingKind, DeferredHolder<RecipeType<?>, RecipeType<ProcessingRecipe>>> RECIPE_TYPES = new EnumMap<>(ProcessingKind.class);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ProcessingBlockEntity>> ENTITY = ENTITIES.register("processor",
            () -> new BlockEntityType<>(ProcessingBlockEntity::new, ICBlocks.COMPRESSOR.get(), ICBlocks.EXTRACTOR.get(),
                    ICBlocks.METAL_FORMER.get(), ICBlocks.ORE_WASHING_PLANT.get(), ICBlocks.RECYCLER.get(), ICBlocks.SOLID_CANNING_MACHINE.get(),
                    ICBlocks.INDUCTION_FURNACE.get(), ICBlocks.THERMAL_CENTRIFUGE.get(), ICBlocks.BLOCK_SLICER.get(), ICBlocks.ELECTRIC_FURNACE.get()));
    public static final DeferredHolder<MenuType<?>, MenuType<ProcessingMenu>> MENU = MENUS.register("processor", () -> IMenuTypeExtension.create(ProcessingMenu::new));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ProcessingCannerBlockEntity>> CANNER = ENTITIES.register("fluid_packager", () -> new BlockEntityType<>(ProcessingCannerBlockEntity::new, ICBlocks.CANNING_MACHINE.get()));
    public static final DeferredHolder<MenuType<?>, MenuType<ProcessingCannerMenu>> CANNER_MENU = MENUS.register("fluid_packager", () -> IMenuTypeExtension.create(ProcessingCannerMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<ProcessingFilterMenu>> FILTER_MENU = MENUS.register("routing_filter", () -> IMenuTypeExtension.create(ProcessingFilterMenu::new));
    public static final DeferredHolder<RecipeType<?>, RecipeType<EnrichmentRecipe>> ENRICHMENT = TYPES.register("fluid_enrichment", () -> new RecipeType<>() { });
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<EnrichmentRecipe>> ENRICH_SERIALIZER = SERIALIZERS.register("fluid_enrichment", () -> new RecipeSerializer<>(EnrichmentRecipe.CODEC, EnrichmentRecipe.STREAM));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> FILTER_FLAGS = COMPONENTS.register("routing_flags", () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> FILTER_CHARGE = COMPONENTS.register("routing_charge", () -> DataComponentType.<Integer>builder().persistent(Codec.intRange(0, Integer.MAX_VALUE)).networkSynchronized(ByteBufCodecs.VAR_INT).build());
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ProcessingRecipe>> SERIALIZER = SERIALIZERS.register("processing",
            () -> new RecipeSerializer<>(ProcessingRecipe.MAP_CODEC, ProcessingRecipe.STREAM_CODEC));
    static {
        for (ProcessingKind kind : ProcessingKind.values()) {
            RECIPE_TYPES.put(kind, TYPES.register(kind.getSerializedName(), () -> new RecipeType<>() {}));
        }
    }

    public static void register(IEventBus bus) {
        ENTITIES.register(bus);
        MENUS.register(bus);
        TYPES.register(bus);
        SERIALIZERS.register(bus);
        COMPONENTS.register(bus);
        bus.addListener(ProcessingContent::registerCapabilities);
        //? if >=26.1
        NeoForge.EVENT_BUS.addListener(ProcessingContent::sync);
    }
    //? if >=26.1
    private static void sync(OnDatapackSyncEvent event) { event.sendRecipes(ENRICHMENT.get(), RecipeType.SMELTING); }

    public static RecipeType<ProcessingRecipe> recipeType(ProcessingKind kind) { return RECIPE_TYPES.get(kind).get(); }
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(IcCapabilities.EU_SINK, ENTITY.get(), (entity, side) -> entity);
        event.registerBlockEntity(Capabilities.Item.BLOCK, ENTITY.get(), WorldlyContainerWrapper::new);
        event.registerBlockEntity(Capabilities.Fluid.BLOCK, ENTITY.get(), (entity, side) -> entity.kind() == ProcessingKind.WASHING ? entity.waterTank() : null);
        event.registerBlockEntity(IcCapabilities.EU_SINK, CANNER.get(), (entity, side) -> entity);
        event.registerBlockEntity(Capabilities.Item.BLOCK, CANNER.get(), WorldlyContainerWrapper::new);
        event.registerBlockEntity(Capabilities.Fluid.BLOCK, CANNER.get(), (entity, side) -> entity.fluids());
    }

    public static Block block(ProcessingKind kind) {
        return switch (kind) {
            case COMPRESSING -> ICBlocks.COMPRESSOR.get();
            case EXTRACTING -> ICBlocks.EXTRACTOR.get();
            case WASHING -> ICBlocks.ORE_WASHING_PLANT.get();
            case RECYCLING -> ICBlocks.RECYCLER.get();
            case CANNING -> ICBlocks.SOLID_CANNING_MACHINE.get();
            case SMELTING -> ICBlocks.ELECTRIC_FURNACE.get();
            case INDUCTION -> ICBlocks.INDUCTION_FURNACE.get();
            case SLICING -> ICBlocks.BLOCK_SLICER.get();
            case CENTRIFUGING -> ICBlocks.THERMAL_CENTRIFUGE.get();
            default -> ICBlocks.METAL_FORMER.get();
        };
    }

    private ProcessingContent() {}
}
