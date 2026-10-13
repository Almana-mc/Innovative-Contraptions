package me.almana.innovativecontraptions.content.matter;

import java.util.ArrayList;
import java.util.List;
import me.almana.innovativecontraptions.InnovativeContraptions;
import me.almana.innovativecontraptions.registry.*;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.*;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.fluids.*;
import net.neoforged.neoforge.registries.*;
import net.neoforged.neoforge.transfer.item.WorldlyContainerWrapper;

public final class MatterContent {
    public static final DeferredBlock<Block> REINFORCED_STONE = ICBlocks.BLOCKS.registerBlock("structural_stone", Block::new,
            () -> BlockBehaviour.Properties.of().strength(8F, 180F).requiresCorrectToolForDrops());
    public static final DeferredItem<BlockItem> STRUCTURAL_STONE = ICItems.ITEMS.registerSimpleBlockItem(REINFORCED_STONE);
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, InnovativeContraptions.MODID);
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(BuiltInRegistries.MENU, InnovativeContraptions.MODID);
    private static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, InnovativeContraptions.MODID);
    private static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(BuiltInRegistries.FLUID, InnovativeContraptions.MODID);
    private static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, InnovativeContraptions.MODID);
    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, InnovativeContraptions.MODID);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemStackTemplate>> PATTERN = ICDataComponents.COMPONENTS.register("recorded_pattern",
            () -> DataComponentType.<ItemStackTemplate>builder().persistent(ItemStackTemplate.CODEC).networkSynchronized(ItemStackTemplate.STREAM_CODEC).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ItemStackTemplate>>> ARCHIVED_PATTERNS = ICDataComponents.COMPONENTS.register("archived_patterns",
            () -> DataComponentType.<List<ItemStackTemplate>>builder().persistent(ItemStackTemplate.CODEC.listOf()).networkSynchronized(StreamCodec.of(
                    (buffer, entries) -> { buffer.writeVarInt(entries.size()); for (ItemStackTemplate stack : entries) ItemStackTemplate.STREAM_CODEC.encode(buffer, stack); },
                    buffer -> { int count = buffer.readVarInt(); if (count < 0 || count > 65536) throw new IllegalArgumentException("Invalid pattern count");
                        List<ItemStackTemplate> entries = new ArrayList<>(count); for (int i = 0; i < count; i++) entries.add(ItemStackTemplate.STREAM_CODEC.decode(buffer)); return List.copyOf(entries); })).build());
    public static final DeferredHolder<FluidType, FluidType> MATTER_TYPE = FLUID_TYPES.register("synthesis_matter", () -> new FluidType(FluidType.Properties.create()));
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> MATTER = FLUIDS.register("synthesis_matter", () -> new BaseFlowingFluid.Source(fluidProperties()));
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> FLOWING_MATTER = FLUIDS.register("flowing_synthesis_matter", () -> new BaseFlowingFluid.Flowing(fluidProperties()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MatterBlockEntity>> ENTITY = ENTITIES.register("matter_workbench",
            () -> new BlockEntityType<>(MatterBlockEntity::new, ICBlocks.MATTER_FABRICATOR.get(), ICBlocks.PATTERN_SCANNER.get(), ICBlocks.PATTERN_STORAGE.get(), ICBlocks.REPLICATOR.get()));
    public static final DeferredHolder<MenuType<?>, MenuType<MatterMenu>> MENU = MENUS.register("matter_workbench", () -> IMenuTypeExtension.create(MatterMenu::new));
    public static final DeferredHolder<RecipeType<?>, RecipeType<MatterRecipe>> COSTS = TYPES.register("matter_cost", () -> new RecipeType<>() {});
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<MatterRecipe>> SERIALIZER = SERIALIZERS.register("matter_cost", () -> new RecipeSerializer<>(MatterRecipe.MAP_CODEC, MatterRecipe.STREAM_CODEC));
    private static BaseFlowingFluid.Properties fluidProperties() { return new BaseFlowingFluid.Properties(MATTER_TYPE, MATTER, FLOWING_MATTER); }
    public static void register(IEventBus bus) {
        ENTITIES.register(bus); MENUS.register(bus); FLUID_TYPES.register(bus); FLUIDS.register(bus); TYPES.register(bus); SERIALIZERS.register(bus);
        bus.addListener(MatterContent::capabilities);
        bus.addListener(MatterCostSync::register);
        NeoForge.EVENT_BUS.addListener(MatterContent::sync);
    }
    private static void capabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(IcCapabilities.EU_SINK, ENTITY.get(), (entity, side) -> entity.kind() == 2 ? null : entity);
        event.registerBlockEntity(Capabilities.Item.BLOCK, ENTITY.get(), WorldlyContainerWrapper::new);
        event.registerBlockEntity(Capabilities.Fluid.BLOCK, ENTITY.get(), (entity, side) -> entity.kind() == 0 || entity.kind() == 3 ? entity.tank() : null);
    }
    private static void sync(OnDatapackSyncEvent event) {
        //? if >=26.1
        event.sendRecipes(COSTS.get());
        MatterCosts.invalidate();
        MatterCostSync.send(event);
    }
    private MatterContent() {}
}

