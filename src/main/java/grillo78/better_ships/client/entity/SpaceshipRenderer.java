package grillo78.better_ships.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import grillo78.better_ships.BetterShips;
import grillo78.better_ships.capability.ShipRotations;
import grillo78.better_ships.capability.ShipRotationsProvider;
import grillo78.better_ships.entity.Spaceship;
import grillo78.better_ships.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.joml.Quaternionf;
import org.joml.Quaternionf;

public class SpaceshipRenderer extends EntityRenderer<Spaceship> {

    private EntityModel model;

    public SpaceshipRenderer(EntityRendererProvider.Context pContext, EntityModel model) {
        super(pContext);
        this.model = model;
    }

    @Override
    public void render(Spaceship pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        Minecraft.getInstance().timer.msPerTick = 1000/ BetterShips.DEBUG_TICK;
        super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
        ShipRotations shipRotations = pEntity.getCapability(ShipRotationsProvider.CAPABILITY).orElse(null);
        pPoseStack.pushPose();
        VertexConsumer vertexConsumer = pBuffer.getBuffer(RenderType.lines());

//        vertexConsumer.vertex(pPoseStack.last().pose(),0.1F,0,0.1F).color(255,255,255,255).normal(pPoseStack.last().normal(),0,0,0).endVertex();
//        vertexConsumer.vertex(pPoseStack.last().pose(),-0.1F,0,-0.1F).color(255,255,255,255).normal(pPoseStack.last().normal(),0,0,0).endVertex();
//        vertexConsumer.vertex(pPoseStack.last().pose(),-0.1F,0,0.1F).color(255,255,255,255).normal(pPoseStack.last().normal(),0,0,0).endVertex();
//        vertexConsumer.vertex(pPoseStack.last().pose(),0.1F,0,-0.1F).color(255,255,255,255).normal(pPoseStack.last().normal(),0,0,0).endVertex();

//        pPoseStack.mulPose(new Quaternionf(shipRotations.getRotations()));
        pPoseStack.mulPose(new Quaternionf(shipRotations.getRotationsO().nlerp(shipRotations.getRotations(), pPartialTick, new Quaternionf())));
        pPoseStack.mulPose(new Quaternionf().rotateZ((float) Math.toRadians(180)));
        pPoseStack.translate(pEntity.getModelOffset().x,pEntity.getModelOffset().y,pEntity.getModelOffset().z);
        model.renderToBuffer(pPoseStack, pBuffer.getBuffer(RenderType.entityTranslucent(getTextureLocation(pEntity))), pPackedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
        pPoseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(Spaceship pEntity) {
        ResourceLocation key = ForgeRegistries.ENTITY_TYPES.getKey(pEntity.getType());
        return new ResourceLocation(key.getNamespace(), "textures/entity/ships/" + key.getPath() + ".png");
    }
}
