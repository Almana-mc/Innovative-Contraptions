package me.almana.innovativecontraptions.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import me.almana.innovativecontraptions.InnovativeContraptions;
import me.almana.innovativecontraptions.client.ICClientSetup;
import me.almana.innovativecontraptions.content.generator.WaterMillBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;

public class WaterMillRenderer implements BlockEntityRenderer<WaterMillBlockEntity, WaterMillRenderer.RenderState> {
    private static final Identifier ROTOR_TEXTURE =
            Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, "textures/block/water_mill_side.png");
    private static final float SPIN_RATE = 0.01F;

    private final WindmillRotorModel model;

    public WaterMillRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new WindmillRotorModel(context.bakeLayer(ICClientSetup.WINDMILL_ROTOR_LAYER));
    }

    @Override
    public RenderState createRenderState() {
        return new RenderState();
    }

    @Override
    public void extractRenderState(WaterMillBlockEntity blockEntity, RenderState state, float partialTicks,
                                   Vec3 cameraPosition, ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        int water = WaterMillBlockEntity.countNearbyWater(blockEntity.getLevel(), blockEntity.getBlockPos());
        state.spin = (blockEntity.getLevel().getGameTime() + partialTicks) * water * SPIN_RATE;
        state.facing = blockEntity.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
    }

    @Override
    public void submit(RenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        WindmillRotorModel.State modelState = new WindmillRotorModel.State(state.spin);
        this.model.setupAnim(modelState);

        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(-state.facing.toYRot()));
        poseStack.translate(0.0F, 0.0F, 0.58F);
        poseStack.scale(0.45F, 0.45F, 0.45F);
        collector.submitModel(this.model, modelState, poseStack, ROTOR_TEXTURE,
                state.lightCoords, OverlayTexture.NO_OVERLAY, 0, state.breakProgress);
        poseStack.popPose();
    }

    public static class RenderState extends BlockEntityRenderState {
        public float spin;
        public Direction facing = Direction.NORTH;
    }
}
