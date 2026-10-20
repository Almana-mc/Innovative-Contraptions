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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.*;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.registries.*;
import net.neoforged.neoforge.transfer.item.WorldlyContainerWrapper;

public final class MatterContent {
    public static final DeferredBlock<Block> REINFORCED_STONE = ICBlocks.BLOCKS.registerBlock("structural_stone", Block::new,
            () -> BlockBehaviour.Properties.of().strength(80F, 180F).requiresCorrectToolForDrops());
    public static final DeferredItem<BlockItem> STRUCTURAL_STONE = ICItems.ITEMS.registerSimpleBlockItem(REINFORCED_STONE);
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, InnovativeContraptions.MODID);
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(BuiltInRegistries.MENU, InnovativeContraptions.MODID);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemStackTemplate>> PATTERN = ICDataComponents.COMPONENTS.register("recorded_pattern",
            () -> DataComponentType.<ItemStackTemplate>builder().persistent(ItemStackTemplate.CODEC).networkSynchronized(ItemStackTemplate.STREAM_CODEC).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ItemStackTemplate>>> ARCHIVED_PATTERNS = ICDataComponents.COMPONENTS.register("archived_patterns",
            () -> DataComponentType.<List<ItemStackTemplate>>builder().persistent(ItemStackTemplate.CODEC.listOf()).networkSynchronized(StreamCodec.of(
                    (buffer, entries) -> { buffer.writeVarInt(entries.size()); for (ItemStackTemplate stack : entries) ItemStackTemplate.STREAM_CODEC.encode(buffer, stack); },
                    buffer -> { int count = buffer.readVarInt(); if (count < 0 || count > 65536) throw new IllegalArgumentException("Invalid pattern count");
                        List<ItemStackTemplate> entries = new ArrayList<>(count); for (int i = 0; i < count; i++) entries.add(ItemStackTemplate.STREAM_CODEC.decode(buffer)); return List.copyOf(entries); })).build());
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MatterBlockEntity>> ENTITY = ENTITIES.register("matter_workbench",
            () -> new BlockEntityType<>(MatterBlockEntity::new, ICBlocks.MATTER_FABRICATOR.get(), ICBlocks.PATTERN_SCANNER.get(), ICBlocks.PATTERN_STORAGE.get(), ICBlocks.REPLICATOR.get()));
    public static final DeferredHolder<MenuType<?>, MenuType<MatterMenu>> MENU = MENUS.register("matter_workbench", () -> IMenuTypeExtension.create(MatterMenu::new));
    public static void register(IEventBus bus) {
        ENTITIES.register(bus); MENUS.register(bus);
        bus.addListener(MatterContent::capabilities);
        bus.addListener(MatterCostSync::register);
        NeoForge.EVENT_BUS.addListener(MatterContent::sync);
    }
    private static void capabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(IcCapabilities.EU_SINK, ENTITY.get(), (entity, side) -> entity.kind() == 2 ? null : entity);
        event.registerBlockEntity(Capabilities.Item.BLOCK, ENTITY.get(), WorldlyContainerWrapper::new);
    }
    private static void sync(OnDatapackSyncEvent event) {
        MatterCosts.invalidate();
        MatterCostSync.send(event);
    }
    private MatterContent() {}
}

