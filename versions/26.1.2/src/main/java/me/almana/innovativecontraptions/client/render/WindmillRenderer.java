package me.almana.innovativecontraptions.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import me.almana.innovativecontraptions.InnovativeContraptions;
import me.almana.innovativecontraptions.client.ICClientSetup;
import me.almana.innovativecontraptions.content.generator.WindRotorVisual;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.LevelRenderer;
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

public class WindmillRenderer<T extends BlockEntity & WindRotorVisual> implements BlockEntityRenderer<T, WindmillRenderer.RenderState> {
    private static final Identifier ROTOR_TEXTURE =
            Identifier.fromNamespaceAndPath(InnovativeContraptions.MODID, "textures/block/windmill_side.png");
    private static final float SPIN_RATE = 0.05F;

    private final WindmillRotorModel model;

    public WindmillRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new WindmillRotorModel(context.bakeLayer(ICClientSetup.WINDMILL_ROTOR_LAYER));
    }

    @Override
    public RenderState createRenderState() {
        return new RenderState();
    }

    @Override public boolean shouldRenderOffScreen() { return true; }

    @Override
    public void extractRenderState(T blockEntity, RenderState state, float partialTicks,
                                   Vec3 cameraPosition, ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.render = blockEntity.hasRotor();
        state.radius = blockEntity.rotorRadius();
        state.spin = (blockEntity.getLevel().getGameTime() + partialTicks) * blockEntity.speed() * SPIN_RATE;
        state.facing = blockEntity.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        state.lightCoords = LevelRenderer.getLightCoords(blockEntity.getLevel(), blockEntity.getBlockPos().relative(state.facing));
    }

    @Override
    public void submit(RenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (!state.render) {
            return;
        }
        WindmillRotorModel.State modelState = new WindmillRotorModel.State(state.spin);
        this.model.setupAnim(modelState);

        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(-state.facing.toYRot()));
        poseStack.translate(0.0F, 0.0F, 0.55F);
        poseStack.scale(state.radius, state.radius, 1);
        collector.submitModel(this.model, modelState, poseStack, ROTOR_TEXTURE,
                state.lightCoords, OverlayTexture.NO_OVERLAY, 0, state.breakProgress);
        poseStack.popPose();
    }

    public static class RenderState extends BlockEntityRenderState {
        public float spin;
        public float radius;
        public boolean render;
        public Direction facing = Direction.NORTH;
    }
}
