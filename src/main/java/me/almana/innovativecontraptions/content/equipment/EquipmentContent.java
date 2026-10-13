package me.almana.innovativecontraptions.content.equipment;

import me.almana.innovativecontraptions.InnovativeContraptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
//? if >=26.1 {
import net.minecraft.world.item.ToolMaterial;
//?} else {
/*import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.SimpleTier;
*///?}
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class EquipmentContent {
    //? if >=26.1 {
    public static final ToolMaterial BRONZE = new ToolMaterial(BlockTags.INCORRECT_FOR_IRON_TOOL, 350, 6, 2, 13,
            TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "ingots/bronze")));
    public static final ToolMaterial BRONZE_AXE = new ToolMaterial(BlockTags.INCORRECT_FOR_IRON_TOOL, 350, 5, 2, 13, BRONZE.repairItems());
    //?} else {
    /*private static final TagKey<net.minecraft.world.item.Item> BRONZE_INGOTS = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "ingots/bronze"));
    public static final Tier BRONZE = new SimpleTier(BlockTags.INCORRECT_FOR_IRON_TOOL, 350, 6, 2, 13, () -> Ingredient.of(BRONZE_INGOTS));
    public static final Tier BRONZE_AXE = new SimpleTier(BlockTags.INCORRECT_FOR_IRON_TOOL, 350, 5, 2, 13, () -> Ingredient.of(BRONZE_INGOTS));
    *///?}
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, InnovativeContraptions.MODID);
    public static final DeferredHolder<MobEffect, MobEffect> RADIATION = EFFECTS.register("radiation", RadiationEffect::new);
    public static final ResourceKey<DamageType> RADIATION_DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE,
            Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, "radiation"));
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, InnovativeContraptions.MODID);
    public static final DeferredHolder<EntityType<?>, EntityType<LaserBolt>> LASER_BOLT = ENTITIES.register("laser_bolt", () ->
            EntityType.Builder.<LaserBolt>of(LaserBolt::new, MobCategory.MISC).sized(0.1F, 0.1F)
                    .clientTrackingRange(64).updateInterval(1)
                    //? if >=26.1 {
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, "laser_bolt"))));
                    //?} else
                    //.build(InnovativeContraptions.MODID + ":laser_bolt"));
    public static void register(IEventBus bus) {
        EquipmentData.TYPES.register(bus);
        EFFECTS.register(bus);
        ENTITIES.register(bus);
        bus.addListener(EquipmentContent::payloads);
        NeoForge.EVENT_BUS.addListener(ArmorProtection::incoming);
        NeoForge.EVENT_BUS.addListener(ArmorProtection::landing);
        NeoForge.EVENT_BUS.addListener(WornEquipmentTick::tick);
        NeoForge.EVENT_BUS.addListener(EquipmentContent::miningSpeed);
        NeoForge.EVENT_BUS.addListener(LaserBlast::collect);
        //? if <26.1
        //WornItem.register(bus);
    }
    private static void payloads(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToServer(EquipmentInputPayload.TYPE, EquipmentInputPayload.STREAM_CODEC, EquipmentInputPayload::handle);
    }
    private static void miningSpeed(PlayerEvent.BreakSpeed event) {
        var player = event.getEntity();
        var stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof PoweredMiningItem item)) return;
        float originalSpeed = item.getDestroySpeed(stack, event.getState());
        PoweredUse.recharge(stack, player);
        float speed = item.getDestroySpeed(stack, event.getState());
        if (speed <= 1) return;
        float adjusted = event.getNewSpeed() * speed / originalSpeed;
        if (item.drill()) {
            if (player.isEyeInFluid(FluidTags.WATER)) {
                double underwater = player.getAttributeValue(Attributes.SUBMERGED_MINING_SPEED);
                if (underwater > 0) adjusted /= (float) underwater;
            }
            if (!player.onGround()) adjusted *= 5;
        }
        event.setNewSpeed(adjusted);
    }
}
