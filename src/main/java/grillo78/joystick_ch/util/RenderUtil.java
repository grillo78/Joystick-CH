package grillo78.joystick_ch.util;

import com.mojang.blaze3d.vertex.PoseStack;
import grillo78.joystick_ch.capability.ShipRotations;
import grillo78.joystick_ch.capability.ShipRotationsProvider;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class RenderUtil {

    public static void applyShipRotations(BlockState blockState, double x, double y, double z, float yaw, float pitch, float roll, float scale, boolean glowing, RenderLevelStageEvent provider, Entity currentEntity, Vec3 pos, CallbackInfo ci) {
        ci.cancel();
        PoseStack poseStack = provider.getPoseStack();
        poseStack.pushPose();
        poseStack.translate(x - pos.x(), y - pos.y(), z - pos.z());
        if (currentEntity != null)
            currentEntity.getCapability(ShipRotationsProvider.CAPABILITY).ifPresent(capability -> {
                poseStack.mulPose(capability.getRotations());
            });
        poseStack.scale(scale, scale, scale);
        poseStack.translate(-0.5F, -0.5F, -0.5F);
    }

    public static void transformLevel(PoseStack pPoseStack, float pPartialTick, long pFinishNanoTime, boolean pRenderBlockOutline, Camera pCamera, GameRenderer pGameRenderer, LightTexture pLightTexture, Matrix4f pProjectionMatrix, ShipRotations shipRotations) {
        if (!pCamera.isDetached()) {
            float viewOffset = (float) (Minecraft.getInstance().player.getEyeHeight()-Minecraft.getInstance().player.getVehicle().getPassengersRidingOffset()*2.185);
            pPoseStack.translate(0, -viewOffset, 0);
            Vector3f invertedEyeHeight = new Vector3f(0, viewOffset, 0).rotate(shipRotations.getRotations().invert(new Quaternionf()));
            pPoseStack.translate(invertedEyeHeight.x, invertedEyeHeight.y, invertedEyeHeight.z);
            pPoseStack.mulPose(shipRotations.getRotations().conjugate(new Quaternionf()));
        }
    }
}
