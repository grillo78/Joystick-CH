package grillo78.joystick_ch.util;

import grillo78.joystick_ch.capability.JoystickController;
import grillo78.joystick_ch.capability.ShipRotations;
import grillo78.joystick_ch.capability.ShipRotationsProvider;
import grillo78.joystick_ch.network.PacketHandler;
import grillo78.joystick_ch.network.messages.SyncShipRotationsCap;
import net.lointain.cosmos.entity.RocketSeatEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkDirection;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class MovementUtil {

    public static void applyRotations(Entity entity, JoystickController joystickController) {
        if (!entity.level().isClientSide) {
            RocketSeatEntity vehicle = (RocketSeatEntity) entity.getVehicle();
            ShipRotations cap = vehicle.getCapability(ShipRotationsProvider.CAPABILITY).orElse(null);
            float newX = (float) -joystickController.getPitch()*0.1F;
            float newY = (float) joystickController.getYaw()*0.1F;
            float newZ = (float) joystickController.getRoll()*0.1F;
            Quaternionf deltaQuaternion = new Quaternionf().rotateXYZ(newX, newY, newZ);
            cap.getRotations().mul(deltaQuaternion);
            for (ServerPlayer player : ((ServerLevel) entity.level()).players())
                PacketHandler.INSTANCE.sendTo(new SyncShipRotationsCap(cap.getRotations().x,cap.getRotations().y,cap.getRotations().z,cap.getRotations().w, vehicle.getId()),player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);

            Vector3f auxVector = new Vector3f(0,0,1).rotate(cap.getRotations());
            double pitch = -Math.toDegrees(Math.asin(auxVector.y));
            double yaw = -Math.toDegrees(Math.atan2(auxVector.x, auxVector.z));
            vehicle.setYRot((float) yaw);
            vehicle.yRotO = ((float) yaw);
            vehicle.setYHeadRot((float) yaw);
            vehicle.yHeadRotO = ((float) yaw);
            vehicle.setXRot(180 + (float) pitch);
            vehicle.xRotO = (180 + (float) pitch);
        }
    }

    public static void applyThrust(Entity entity, JoystickController joystickController) {
        entity.getVehicle().setDeltaMovement(entity.getVehicle().getDeltaMovement().add(entity.getVehicle().getViewVector(0).scale(joystickController.getThrust() * 0.5)));
    }
}
