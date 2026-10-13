package me.almana.innovativecontraptions.client;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.FastColor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

public final class EuDebugRenderer {

    public static void render(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES
                || EuDebugClient.THROUGHPUT.isEmpty() || EuDebugClient.expired()) {
            return;
        }
        Vec3 cam = event.getCamera().getPosition();
        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
        for (Long2IntMap.Entry e : EuDebugClient.entries()) {
            BlockPos pos = BlockPos.of(e.getLongKey());
            int eu = e.getIntValue();
            int color = EuDebugClient.colorFor(eu);
            AABB box = new AABB(
                    pos.getX() + 6 / 16.0, pos.getY() + 6 / 16.0, pos.getZ() + 6 / 16.0,
                    pos.getX() + 10 / 16.0, pos.getY() + 10 / 16.0, pos.getZ() + 10 / 16.0);
            LevelRenderer.renderLineBox(poseStack, buffers.getBuffer(RenderType.lines()), box.move(cam.reverse()),
                    FastColor.ARGB32.red(color) / 255F, FastColor.ARGB32.green(color) / 255F, 0F, 1F);
            DebugRenderer.renderFloatingText(poseStack, buffers, eu + " EU/t",
                    pos.getX() + 0.5, pos.getY() + 0.68, pos.getZ() + 0.5, color, 0.5F / 16, true, 0F, true);
        }
    }

    private EuDebugRenderer() {
    }
}
