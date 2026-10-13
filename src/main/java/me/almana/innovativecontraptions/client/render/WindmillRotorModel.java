package me.almana.innovativecontraptions.client.render;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
//? if >=26.1 {
import net.minecraft.client.renderer.rendertype.RenderTypes;
//?} else {
/*import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
*///?}

//? if >=26.1 {
public class WindmillRotorModel extends Model<WindmillRotorModel.State> {
//?} else
//public class WindmillRotorModel extends Model {
    private final ModelPart rotor;
    //? if <26.1
    //private final ModelPart root;

    public WindmillRotorModel(ModelPart root) {
        //? if >=26.1 {
        super(root, RenderTypes::entityCutout);
        //?} else {
        /*super(RenderType::entityCutout);
        this.root = root;
        *///?}
        this.rotor = root.getChild("rotor");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        CubeListBuilder rotor = CubeListBuilder.create()
                .texOffs(0, 0).addBox(-2.5F, -2.5F, -1.0F, 5.0F, 5.0F, 2.0F)
                .texOffs(0, 0).addBox(-1.5F, -16.0F, -0.5F, 3.0F, 13.0F, 1.0F)
                .texOffs(0, 0).addBox(-1.5F, 3.0F, -0.5F, 3.0F, 13.0F, 1.0F)
                .texOffs(0, 0).addBox(-16.0F, -1.5F, -0.5F, 13.0F, 3.0F, 1.0F)
                .texOffs(0, 0).addBox(3.0F, -1.5F, -0.5F, 13.0F, 3.0F, 1.0F);
        root.addOrReplaceChild("rotor", rotor, PartPose.ZERO);
        return LayerDefinition.create(mesh, 16, 16);
    }

    //? if >=26.1
    @Override
    public void setupAnim(State state) {
        //? if >=26.1
        super.setupAnim(state);
        this.rotor.zRot = state.spin;
    }

    //? if <26.1 {
    /*@Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int light, int overlay, int color) {
        this.root.render(poseStack, buffer, light, overlay, color);
    }
    *///?}

    public record State(float spin) {
    }
}
