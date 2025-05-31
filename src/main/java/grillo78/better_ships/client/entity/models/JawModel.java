package grillo78.better_ships.client.entity.models;

// Made with Blockbench 4.12.4
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import grillo78.better_ships.entity.Spaceship;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

public class JawModel<T extends Spaceship> extends EntityModel<T> {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    private final ModelPart wing1;
    private final ModelPart wing2;
    private final ModelPart bb_main;

    public JawModel(ModelPart root) {
        this.wing1 = root.getChild("wing1");
        this.wing2 = root.getChild("wing2");
        this.bb_main = root.getChild("bb_main");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition wing1 = partdefinition.addOrReplaceChild("wing1", CubeListBuilder.create(), PartPose.offset(43.4703F, -6.6312F, -10.3065F));

        PartDefinition cube_r1 = wing1.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 177).mirror().addBox(0.0F, 0.0F, 12.0F, 27.0F, 7.0F, 50.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(0, 101).mirror().addBox(0.0F, 0.0F, 0.0F, 5.0F, 7.0F, 12.0F, new CubeDeformation(0.02F)).mirror(false)
                .texOffs(0, 35).addBox(5.0F, 0.0F, 9.0F, 5.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.9163F));

        PartDefinition cube_r2 = wing1.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 35).addBox(15.0F, 7.0F, 42.0F, 11.0F, 11.0F, 24.0F, new CubeDeformation(0.0F))
                .texOffs(114, 115).addBox(0.0F, 0.0F, 0.0F, 27.0F, 7.0F, 62.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(-24.4703F, -11.4107F, 0.0F, 0.0F, 0.0F, 0.4363F));

        PartDefinition cube_r3 = wing1.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(6.0F, 0.0F, 0.0F, 25.0F, 7.0F, 8.0F, new CubeDeformation(0.01F)).mirror(false), PartPose.offsetAndRotation(-0.1194F, -0.1556F, -3.0F, 0.0F, -0.5236F, 0.9163F));

        PartDefinition wing2 = partdefinition.addOrReplaceChild("wing2", CubeListBuilder.create(), PartPose.offset(-43.4703F, -6.6312F, -10.3065F));

        PartDefinition cube_r4 = wing2.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(0, 177).addBox(-27.0F, 0.0F, 12.0F, 27.0F, 7.0F, 50.0F, new CubeDeformation(0.0F))
                .texOffs(0, 101).addBox(-5.0F, 0.0F, 0.0F, 5.0F, 7.0F, 12.0F, new CubeDeformation(0.02F))
                .texOffs(0, 35).addBox(-10.0F, 0.0F, 9.0F, 5.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.9163F));

        PartDefinition cube_r5 = wing2.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(0, 0).addBox(-31.0F, 0.0F, 0.0F, 25.0F, 7.0F, 8.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.1194F, -0.1556F, -3.0F, 0.0F, 0.5236F, -0.9163F));

        PartDefinition cube_r6 = wing2.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(0, 35).addBox(-26.0F, 7.0F, 42.0F, 11.0F, 11.0F, 24.0F, new CubeDeformation(0.0F))
                .texOffs(114, 115).mirror().addBox(-27.0F, 0.0F, 0.0F, 27.0F, 7.0F, 62.0F, new CubeDeformation(0.01F)).mirror(false), PartPose.offsetAndRotation(24.4703F, -11.4107F, 0.0F, 0.0F, 0.0F, -0.4363F));

        PartDefinition bb_main = partdefinition.addOrReplaceChild("bb_main", CubeListBuilder.create().texOffs(0, 0).addBox(-19.0F, -42.0419F, -33.3065F, 38.0F, 16.0F, 85.0F, new CubeDeformation(0.0F))
                .texOffs(0, 101).addBox(-19.0F, -26.0419F, 1.6935F, 38.0F, 26.0F, 50.0F, new CubeDeformation(0.0F))
                .texOffs(126, 118).addBox(2.0F, -19.0419F, 51.6935F, 11.0F, 11.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(126, 118).addBox(-5.5F, -34.0419F, 51.6935F, 11.0F, 11.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(126, 118).addBox(-13.0F, -19.0419F, 51.6935F, 11.0F, 11.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(18, 127).addBox(16.0F, -14.0F, 23.0F, 3.0F, 3.0F, 12.0F, new CubeDeformation(0.0F))
                .texOffs(72, 184).addBox(-24.0F, -4.0F, -41.3F, 48.0F, 0.0F, 32.0F, new CubeDeformation(0.0F))
                .texOffs(126, 133).addBox(-16.0F, -6.0F, 21.0F, 12.0F, 6.0F, 12.0F, new CubeDeformation(0.0F))
                .texOffs(126, 133).addBox(4.0F, -6.0F, 21.0F, 12.0F, 6.0F, 12.0F, new CubeDeformation(0.0F))
                .texOffs(18, 127).addBox(-4.0F, -14.0F, 23.0F, 3.0F, 3.0F, 12.0F, new CubeDeformation(0.0F))
                .texOffs(126, 103).addBox(-19.0F, -14.0F, 23.0F, 3.0F, 3.0F, 12.0F, new CubeDeformation(0.0F))
                .texOffs(126, 103).addBox(1.0F, -14.0F, 23.0F, 3.0F, 3.0F, 12.0F, new CubeDeformation(0.0F))
                .texOffs(161, 69).addBox(-16.0F, -14.0F, -32.0F, 12.0F, 4.0F, 12.0F, new CubeDeformation(0.0F))
                .texOffs(0, 70).addBox(-14.0F, -7.0F, -30.0F, 8.0F, 3.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(161, 60).addBox(-13.0F, -10.0F, -29.0F, 6.0F, 3.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(20, 70).addBox(-4.0F, -19.0F, -32.0F, 3.0F, 3.0F, 12.0F, new CubeDeformation(0.0F))
                .texOffs(20, 70).addBox(-19.0F, -19.0F, -32.0F, 3.0F, 3.0F, 12.0F, new CubeDeformation(0.0F))
                .texOffs(32, 15).addBox(-16.0F, -29.0F, -20.0F, 12.0F, 15.0F, 4.0F, new CubeDeformation(0.01F))
                .texOffs(0, 70).addBox(6.0F, -7.0F, -30.0F, 8.0F, 3.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(161, 60).addBox(7.0F, -10.0F, -29.0F, 6.0F, 3.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(161, 69).addBox(4.0F, -14.0F, -32.0F, 12.0F, 4.0F, 12.0F, new CubeDeformation(0.0F))
                .texOffs(32, 15).addBox(4.0F, -29.0F, -20.0F, 12.0F, 15.0F, 4.0F, new CubeDeformation(0.01F))
                .texOffs(20, 70).addBox(1.0F, -19.0F, -32.0F, 3.0F, 3.0F, 12.0F, new CubeDeformation(0.0F))
                .texOffs(20, 70).addBox(16.0F, -19.0F, -32.0F, 3.0F, 3.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition cube_r7 = bb_main.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(46, 41).addBox(-2.0F, -10.7695F, -1.6169F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(10.0F, -12.7305F, -40.8831F, -0.5236F, 0.0F, 0.0F));

        PartDefinition cube_r8 = bb_main.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(70, 59).addBox(-1.0F, -8.9136F, -0.6169F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(10.0F, -10.7305F, -41.8831F, -0.5236F, 0.0F, 0.0F));

        PartDefinition cube_r9 = bb_main.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(0, 45).addBox(-17.0F, -9.0F, 0.1831F, 8.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(23.0F, -6.7305F, -41.8831F, 0.1309F, 0.0F, 0.0F));

        PartDefinition cube_r10 = bb_main.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(46, 49).addBox(-11.0F, -6.0F, -4.0F, 12.0F, 6.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(46, 49).addBox(-31.0F, -6.0F, -4.0F, 12.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(15.0F, -10.0F, -20.0F, -0.7854F, 0.0F, 0.0F));

        PartDefinition cube_r11 = bb_main.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(0, 120).addBox(0.0F, -7.0F, -6.0F, 3.0F, 7.0F, 12.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(4.0F, -10.0F, -26.0F, 0.0F, 0.0F, -0.4363F));

        PartDefinition cube_r12 = bb_main.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(52, 22).addBox(-3.0F, -7.0F, -6.0F, 3.0F, 7.0F, 12.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(16.0F, -10.0F, -26.0F, 0.0F, 0.0F, 0.4363F));

        PartDefinition cube_r13 = bb_main.addOrReplaceChild("cube_r13", CubeListBuilder.create().texOffs(52, 22).addBox(-3.0F, -7.0F, -6.0F, 3.0F, 7.0F, 12.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(-4.0F, -10.0F, -26.0F, 0.0F, 0.0F, 0.4363F));

        PartDefinition cube_r14 = bb_main.addOrReplaceChild("cube_r14", CubeListBuilder.create().texOffs(0, 120).addBox(0.0F, -7.0F, -6.0F, 3.0F, 7.0F, 12.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(-16.0F, -10.0F, -26.0F, 0.0F, 0.0F, -0.4363F));

        PartDefinition cube_r15 = bb_main.addOrReplaceChild("cube_r15", CubeListBuilder.create().texOffs(0, 15).addBox(-11.0F, -16.0F, 7.0F, 12.0F, 16.0F, 4.0F, new CubeDeformation(-0.01F))
                .texOffs(0, 15).addBox(-31.0F, -16.0F, 7.0F, 12.0F, 16.0F, 4.0F, new CubeDeformation(-0.01F)), PartPose.offsetAndRotation(15.0F, -7.0F, 23.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition cube_r16 = bb_main.addOrReplaceChild("cube_r16", CubeListBuilder.create().texOffs(161, 0).addBox(-43.0F, -28.0F, 0.0F, 38.0F, 28.0F, 32.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(24.0F, -15.7305F, -42.8831F, -0.3491F, 0.0F, 0.0F));

        PartDefinition cube_r17 = bb_main.addOrReplaceChild("cube_r17", CubeListBuilder.create().texOffs(0, 234).addBox(-24.0F, -16.0F, -24.0F, 48.0F, 16.0F, 32.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.0F, -17.0F, 0.1309F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 512, 512);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        wing1.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        wing2.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        bb_main.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
