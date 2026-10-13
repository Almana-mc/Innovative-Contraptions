package me.almana.innovativecontraptions.content.bioenergy;

import me.almana.innovativecontraptions.InnovativeContraptions;
import me.almana.innovativecontraptions.registry.ICBlocks;
import me.almana.innovativecontraptions.registry.IcCapabilities;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
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

public final class BioenergyContent {
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, InnovativeContraptions.MODID);
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(BuiltInRegistries.MENU, InnovativeContraptions.MODID);
    private static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, InnovativeContraptions.MODID);
    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, InnovativeContraptions.MODID);
    public static final DeferredHolder<RecipeType<?>, RecipeType<BioenergyRecipe>> RECIPES = TYPES.register("bioenergy", () -> new RecipeType<>() {});
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<BioenergyRecipe>> SERIALIZER = SERIALIZERS.register("bioenergy", () -> new RecipeSerializer<>(BioenergyRecipe.CODEC, BioenergyRecipe.STREAM));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BioDigester>> DIGESTER = ENTITIES.register("bio_digester",
            () -> new BlockEntityType<>(BioDigester::new, ICBlocks.BIO_DIGESTER.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LiquidFuelGenerator>> GENERATOR = ENTITIES.register("liquid_fuel_generator",
            () -> new BlockEntityType<>(LiquidFuelGenerator::new, ICBlocks.LIQUID_FUEL_GENERATOR.get()));
    public static final DeferredHolder<MenuType<?>, MenuType<BioenergyMenu>> MENU = MENUS.register("bioenergy", () -> IMenuTypeExtension.create(BioenergyMenu::new));
    public static void register(IEventBus bus) {
        ENTITIES.register(bus);
        MENUS.register(bus);
        TYPES.register(bus);
        SERIALIZERS.register(bus);
        //? if >=26.1
        NeoForge.EVENT_BUS.addListener(BioenergyContent::sync);
        bus.addListener(BioenergyContent::capabilities);
    }
    //? if >=26.1
    private static void sync(OnDatapackSyncEvent event) { event.sendRecipes(RECIPES.get()); }
    private static void capabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.Item.BLOCK, DIGESTER.get(), WorldlyContainerWrapper::new);
        event.registerBlockEntity(Capabilities.Item.BLOCK, GENERATOR.get(), WorldlyContainerWrapper::new);
        event.registerBlockEntity(Capabilities.Fluid.BLOCK, DIGESTER.get(), (machine, side) -> machine.fluids());
        event.registerBlockEntity(Capabilities.Fluid.BLOCK, GENERATOR.get(), (machine, side) -> machine.fluids());
        event.registerBlockEntity(IcCapabilities.EU_SOURCE, GENERATOR.get(), (machine, side) -> machine);
    }
    private BioenergyContent() {}
}
