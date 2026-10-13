package me.almana.innovativecontraptions.client.equipment;

import com.mojang.blaze3d.vertex.PoseStack;
import me.almana.innovativecontraptions.content.equipment.EquipmentData;
import me.almana.innovativecontraptions.content.equipment.Gear;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;

public class WornHeadLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    private final RenderLayer<T, M> head;

    public WornHeadLayer(RenderLayerParent<T, M> parent, RenderLayer<T, M> head) {
        super(parent);
        this.head = head;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffers, int light, T entity, float limbSwing,
                       float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        Gear gear = entity.getItemBySlot(EquipmentSlot.HEAD).get(EquipmentData.WORN.get());
        if (gear != null && gear.slot() == EquipmentSlot.HEAD) return;
        head.render(poseStack, buffers, light, entity, limbSwing, limbSwingAmount, partialTick, ageInTicks, netHeadYaw, headPitch);
    }
}
