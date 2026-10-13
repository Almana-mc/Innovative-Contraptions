package me.almana.innovativecontraptions.content.thermal;

import me.almana.innovativecontraptions.InnovativeContraptions;
import me.almana.innovativecontraptions.registry.ICBlocks;
import me.almana.innovativecontraptions.registry.IcCapabilities;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.item.WorldlyContainerWrapper;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ThermalContent {
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, InnovativeContraptions.MODID);
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(BuiltInRegistries.MENU, InnovativeContraptions.MODID);
    private static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, InnovativeContraptions.MODID);
    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, InnovativeContraptions.MODID);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ElectricHeaterBlockEntity>> HEATER = ENTITIES.register("electric_heater",
            () -> new BlockEntityType<>(ElectricHeaterBlockEntity::new, ICBlocks.ELECTRIC_HEATER.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlastFurnaceBlockEntity>> FURNACE = ENTITIES.register("blast_furnace",
            () -> new BlockEntityType<>(BlastFurnaceBlockEntity::new, ICBlocks.BLAST_FURNACE.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LiquidHeatExchangerBlockEntity>> EXCHANGER = ENTITIES.register("liquid_heat_exchanger",
            () -> new BlockEntityType<>(LiquidHeatExchangerBlockEntity::new, ICBlocks.LIQUID_HEAT_EXCHANGER.get()));
    public static final DeferredHolder<MenuType<?>, MenuType<HeatExchangerMenu>> EXCHANGER_MENU = MENUS.register("liquid_heat_exchanger",
            () -> IMenuTypeExtension.create(HeatExchangerMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<ThermalMenu>> MENU = MENUS.register("thermal", () -> IMenuTypeExtension.create(ThermalMenu::new));
    public static final DeferredHolder<RecipeType<?>, RecipeType<SteelmakingRecipe>> STEELMAKING = TYPES.register("steelmaking", () -> new RecipeType<>() {});
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<SteelmakingRecipe>> STEEL_SERIALIZER = SERIALIZERS.register("steelmaking",
            () -> new RecipeSerializer<>(SteelmakingRecipe.MAP_CODEC, SteelmakingRecipe.STREAM_CODEC));

    public static void register(IEventBus bus) {
        ENTITIES.register(bus);
        MENUS.register(bus);
        TYPES.register(bus);
        SERIALIZERS.register(bus);
        bus.addListener(ThermalContent::capabilities);
        //? if >=26.1
        NeoForge.EVENT_BUS.addListener(ThermalContent::sync);
    }
    private static void capabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(IcCapabilities.EU_SINK, HEATER.get(), (entity, side) -> entity);
        event.registerBlockEntity(Capabilities.Item.BLOCK, HEATER.get(), WorldlyContainerWrapper::new);
        event.registerBlockEntity(Capabilities.Item.BLOCK, FURNACE.get(), WorldlyContainerWrapper::new);
        event.registerBlockEntity(Capabilities.Item.BLOCK, EXCHANGER.get(), WorldlyContainerWrapper::new);
        event.registerBlockEntity(Capabilities.Fluid.BLOCK, EXCHANGER.get(), (exchanger, side) -> exchanger.fluids());
    }
    //? if >=26.1
    private static void sync(OnDatapackSyncEvent event) { event.sendRecipes(STEELMAKING.get()); }
    private ThermalContent() {}
}
