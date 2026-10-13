package me.almana.innovativecontraptions.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import me.almana.innovativecontraptions.InnovativeContraptions;
import me.almana.innovativecontraptions.client.ICClientSetup;
import me.almana.innovativecontraptions.content.generator.WaterMillBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class WaterMillRenderer implements BlockEntityRenderer<WaterMillBlockEntity> {
    private static final ResourceLocation ROTOR_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(InnovativeContraptions.MODID, "textures/block/water_mill_side.png");
    private static final float SPIN_RATE = 0.01F;

    private final WindmillRotorModel model;

    public WaterMillRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new WindmillRotorModel(context.bakeLayer(ICClientSetup.WINDMILL_ROTOR_LAYER));
    }

    @Override
    public void render(WaterMillBlockEntity blockEntity, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffers, int light, int overlay) {
        int water = WaterMillBlockEntity.countNearbyWater(blockEntity.getLevel(), blockEntity.getBlockPos());
        this.model.setupAnim(new WindmillRotorModel.State((blockEntity.getLevel().getGameTime() + partialTicks) * water * SPIN_RATE));

        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(-blockEntity.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot()));
        poseStack.translate(0.0F, 0.0F, 0.58F);
        poseStack.scale(0.45F, 0.45F, 0.45F);
        this.model.renderToBuffer(poseStack, buffers.getBuffer(this.model.renderType(ROTOR_TEXTURE)), light, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }
}
