package me.almana.innovativecontraptions.client.equipment;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import me.almana.innovativecontraptions.content.equipment.EquipmentData;
import me.almana.innovativecontraptions.content.equipment.Gear;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public class WornGearLayer<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> extends HumanoidArmorLayer<T, M, A> {
    private static final EquipmentSlot[] ORDER = {EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.HEAD};

    private WornGearLayer(RenderLayerParent<T, M> parent, A inner, A outer, ModelManager models) {
        super(parent, inner, outer, models);
    }

    public static void register(EntityRenderersEvent.AddLayers event) {
        ModelManager models = event.getContext().getModelManager();
        event.getSkins().forEach(skin -> attach(event.getSkin(skin), models));
        event.getEntityTypes().forEach(type -> attach(event.getRenderer(type), models));
    }

    // renderer generics unknowable here
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void attach(EntityRenderer<?> renderer, ModelManager models) {
        if (!(renderer instanceof LivingEntityRenderer living)) return;
        living.layers.replaceAll(layer -> layer instanceof CustomHeadLayer head ? new WornHeadLayer(living, head) : layer);
        for (Object layer : List.copyOf(living.layers)) {
            if (layer instanceof HumanoidArmorLayer armor) {
                living.addLayer(new WornGearLayer(living, armor.innerModel, armor.outerModel, models));
            }
        }
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffers, int light, T entity, float limbSwing,
                       float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity.isBaby() && !(entity instanceof ArmorStand)) return;
        for (EquipmentSlot slot : ORDER) {
            ItemStack stack = entity.getItemBySlot(slot);
            Gear gear = stack.get(EquipmentData.WORN.get());
            if (gear == null || gear.slot() != slot) continue;
            boolean legs = slot == EquipmentSlot.LEGS;
            A model = legs ? this.innerModel : this.outerModel;
            getParentModel().copyPropertiesTo(model);
            setPartVisibility(model, slot);
            ResourceLocation texture = gear.asset().withPath(path ->
                    "textures/entity/equipment/" + (legs ? "humanoid_leggings/" : "humanoid/") + path + ".png");
            model.renderToBuffer(poseStack, buffers.getBuffer(RenderType.armorCutoutNoCull(texture)), light, OverlayTexture.NO_OVERLAY);
            if (stack.hasFoil()) {
                model.renderToBuffer(poseStack, buffers.getBuffer(RenderType.armorEntityGlint()), light, OverlayTexture.NO_OVERLAY);
            }
        }
    }
}
