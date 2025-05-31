package grillo78.better_ships.util;

import com.mojang.blaze3d.vertex.PoseStack;
import grillo78.better_ships.capability.ShipRotations;
import grillo78.better_ships.entity.Spaceship;
import net.lointain.cosmos.entity.RocketSeatEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3f;

public class RenderUtil {

    public static void transformLevel(PoseStack pPoseStack, float partialTicks, ShipRotations shipRotations) {
        if (!Minecraft.getInstance().gameRenderer.getMainCamera().isDetached()) {
            float viewOffset;
            if (Minecraft.getInstance().player.getVehicle() instanceof Spaceship) {
                viewOffset = (float) (Minecraft.getInstance().player.getEyeHeight() - ((Spaceship) Minecraft.getInstance().player.getVehicle()).getCameraOffset() * 2.18);
            } else {
                viewOffset = (float) (Minecraft.getInstance().player.getEyeHeight() - Minecraft.getInstance().player.getVehicle().getPassengersRidingOffset() * 2.18);
            }
            pPoseStack.mulPose(new Quaternionf().rotationX((float) Math.toRadians(Mth.lerp(partialTicks, Minecraft.getInstance().player.xRotO, Minecraft.getInstance().player.getXRot()))));
            pPoseStack.mulPose(new Quaternionf().rotationY((float) Math.toRadians(Mth.lerp(partialTicks, Minecraft.getInstance().player.yHeadRotO, Minecraft.getInstance().player.yHeadRot))));
            pPoseStack.mulPose(new Quaternionf().rotationY((float) Math.toRadians(180)));
            pPoseStack.translate(0, -viewOffset, 0);
            Vector3f invertedEyeHeight;
//            Quaternionf lerpedQuat = shipRotations.getRotations().conjugate(new Quaternionf());
            Quaternionf lerpedQuat = shipRotations.getRotationsO().slerp(shipRotations.getRotations(), partialTicks, new Quaternionf()).invert();
//            if (Minecraft.getInstance().player.getVehicle() instanceof RocketSeatEntity)
//            invertedEyeHeight = new Vector3f(0, viewOffset, 0).rotate(shipRotations.getRotations().invert(new Quaternionf()));
//            else
                invertedEyeHeight = new Vector3f(0, viewOffset, 0).rotate(lerpedQuat);
//                invertedEyeHeight = new Vector3f(0, viewOffset, 0).rotate(shipRotations.getRotationsO().invert(new Quaternionf())).lerp(new Vector3f(0, viewOffset, 0).rotate(shipRotations.getRotations().invert(new Quaternionf())), partialTicks);
            pPoseStack.translate(invertedEyeHeight.x, invertedEyeHeight.y, invertedEyeHeight.z);
            pPoseStack.mulPose(new Quaternionf().rotationX((float) Math.toRadians(180)).rotationY(0));
//            if (Minecraft.getInstance().player.getVehicle() instanceof RocketSeatEntity)
                pPoseStack.mulPose(new Quaternionf(lerpedQuat));
//            else
//                pPoseStack.mulPose(new Quaternionf(shipRotations.getRotations().invert(new Quaternionf())));
            pPoseStack.mulPose(new Quaternionf().rotationY((float) Math.toRadians(180)));
        }
    }
}
