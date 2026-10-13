package me.almana.innovativecontraptions.client.equipment;

import me.almana.innovativecontraptions.InnovativeContraptions;
import me.almana.innovativecontraptions.content.equipment.EquipmentContent;
import me.almana.innovativecontraptions.content.equipment.EquipmentControls;
import me.almana.innovativecontraptions.content.equipment.EquipmentInputPayload;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;
//? if >=26.1 {
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
//?} else
//import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

public final class EquipmentClient {
    //? if >=26.1 {
    private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, "equipment"));
    //?} else
    //private static final String CATEGORY = "key.category." + InnovativeContraptions.MODID + ".equipment";
    private static final KeyMapping MODE = new KeyMapping("key.innovativecontraptions.equipment_mode", GLFW.GLFW_KEY_M, CATEGORY);
    private static final KeyMapping ALT = new KeyMapping("key.innovativecontraptions.equipment_alt", GLFW.GLFW_KEY_LEFT_ALT, CATEGORY);
    private static final KeyMapping BOOST = new KeyMapping("key.innovativecontraptions.equipment_boost", GLFW.GLFW_KEY_B, CATEGORY);
    private static int previous = -1;
    private static int ticks;
    public static void register(IEventBus bus) {
        bus.addListener(EquipmentClient::keys);
        bus.addListener(EquipmentClient::renderers);
        //? if <26.1
        //bus.addListener(EntityRenderersEvent.AddLayers.class, WornGearLayer::register);
        NeoForge.EVENT_BUS.addListener(EquipmentClient::tick);
        NeoForge.EVENT_BUS.addListener(EquipmentTooltips::describe);
        NeoForge.EVENT_BUS.addListener(EquipmentHud::render);
    }
    private static void keys(RegisterKeyMappingsEvent event) { event.register(MODE); event.register(ALT); event.register(BOOST); }
    private static void renderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EquipmentContent.LASER_BOLT.get(),
                NoopRenderer::new);
    }
    private static void tick(ClientTickEvent.Post event) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.getConnection() == null) { previous = -1; return; }
        int buttons = 0;
        if (client.screen == null) {
            if (client.options.keyJump.isDown()) buttons |= 1;
            if (client.options.keyShift.isDown()) buttons |= 2;
            if (client.options.keyUp.isDown()) buttons |= 4;
            if (MODE.isDown()) buttons |= 8;
            if (ALT.isDown()) buttons |= 16;
            if (BOOST.isDown()) buttons |= 32;
        }
        if (buttons != previous || ++ticks % 5 == 0) {
            EquipmentControls.update(client.player, buttons);
            //? if >=26.1 {
            ClientPacketDistributor.sendToServer(new EquipmentInputPayload(buttons));
            //?} else
            //PacketDistributor.sendToServer(new EquipmentInputPayload(buttons));
            previous = buttons;
        }
    }
}
