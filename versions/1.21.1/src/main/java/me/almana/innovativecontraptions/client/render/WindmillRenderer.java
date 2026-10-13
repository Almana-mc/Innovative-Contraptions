package me.almana.innovativecontraptions.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import me.almana.innovativecontraptions.InnovativeContraptions;
import me.almana.innovativecontraptions.client.ICClientSetup;
import me.almana.innovativecontraptions.content.generator.WindRotorVisual;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class WindmillRenderer<T extends BlockEntity & WindRotorVisual> implements BlockEntityRenderer<T> {
    private static final ResourceLocation ROTOR_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(InnovativeContraptions.MODID, "textures/block/windmill_side.png");
    private static final float SPIN_RATE = 0.05F;

    private final WindmillRotorModel model;

    public WindmillRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new WindmillRotorModel(context.bakeLayer(ICClientSetup.WINDMILL_ROTOR_LAYER));
    }

    @Override public boolean shouldRenderOffScreen(T blockEntity) { return true; }

    @Override
    public void render(T blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int light, int overlay) {
        if (!blockEntity.hasRotor()) {
            return;
        }
        Direction facing = blockEntity.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        float radius = blockEntity.rotorRadius();
        this.model.setupAnim(new WindmillRotorModel.State(
                (blockEntity.getLevel().getGameTime() + partialTicks) * blockEntity.speed() * SPIN_RATE));

        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
        poseStack.translate(0.0F, 0.0F, 0.55F);
        poseStack.scale(radius, radius, 1);
        this.model.renderToBuffer(poseStack, buffers.getBuffer(this.model.renderType(ROTOR_TEXTURE)),
                LevelRenderer.getLightColor(blockEntity.getLevel(), blockEntity.getBlockPos().relative(facing)),
                OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }
}
