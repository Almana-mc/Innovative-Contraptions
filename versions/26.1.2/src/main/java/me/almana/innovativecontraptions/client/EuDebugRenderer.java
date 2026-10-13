package me.almana.innovativecontraptions.client;

import it.unimi.dsi.fastutil.longs.Long2IntMap;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.gizmos.TextGizmo;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RegisterDebugRenderersEvent;

public final class EuDebugRenderer implements DebugRenderer.SimpleDebugRenderer {

    public static void onRegisterDebugRenderers(RegisterDebugRenderersEvent event) {
        event.register(mc -> new EuDebugRenderer());
    }

    @Override
    public void emitGizmos(double camX, double camY, double camZ, DebugValueAccess debugValues,
                           Frustum frustum, float partialTicks) {
        if (EuDebugClient.THROUGHPUT.isEmpty() || EuDebugClient.expired()) {
            return;
        }
        for (Long2IntMap.Entry e : EuDebugClient.entries()) {
            BlockPos pos = BlockPos.of(e.getLongKey());
            int eu = e.getIntValue();
            int color = EuDebugClient.colorFor(eu);
            AABB box = new AABB(
                    pos.getX() + 6 / 16.0, pos.getY() + 6 / 16.0, pos.getZ() + 6 / 16.0,
                    pos.getX() + 10 / 16.0, pos.getY() + 10 / 16.0, pos.getZ() + 10 / 16.0);
            Gizmos.cuboid(box, GizmoStyle.stroke(color, 3.0F));
            Vec3 textPos = new Vec3(pos.getX() + 0.5, pos.getY() + 0.75, pos.getZ() + 0.5);
            Gizmos.billboardText(eu + " EU/t", textPos,
                    TextGizmo.Style.forColorAndCentered(color).withScale(0.5F)).setAlwaysOnTop();
        }
    }

    private EuDebugRenderer() {
    }
}
