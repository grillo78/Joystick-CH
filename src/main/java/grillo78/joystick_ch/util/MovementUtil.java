package grillo78.joystick_ch.util;

import grillo78.joystick_ch.capability.JoystickController;
import net.lointain.cosmos.entity.RocketSeatEntity;
import net.minecraft.world.entity.Entity;

public class MovementUtil {

    public static void applyRotations(Entity entity, JoystickController joystickController) {
        RocketSeatEntity vehicle = (RocketSeatEntity) entity.getVehicle();
        vehicle.getEntityData().set(RocketSeatEntity.DATA_pitch, (int) (vehicle.getEntityData().get(RocketSeatEntity.DATA_pitch) - joystickController.getPitch() * 2));
        vehicle.getEntityData().set(RocketSeatEntity.DATA_yaw, (int) (vehicle.getEntityData().get(RocketSeatEntity.DATA_yaw) - joystickController.getYaw() * 2));
        if(!entity.level().isClientSide){
            vehicle.setYRot(vehicle.getEntityData().get(RocketSeatEntity.DATA_yaw));
            vehicle.setYHeadRot(vehicle.getEntityData().get(RocketSeatEntity.DATA_yaw));
            vehicle.setXRot(180 + vehicle.getEntityData().get(RocketSeatEntity.DATA_pitch));
        }
    }

    public static void applyThrust(Entity entity, JoystickController joystickController) {
        entity.getVehicle().setDeltaMovement(entity.getVehicle().getDeltaMovement().add(entity.getVehicle().getViewVector(0).scale(joystickController.getThrust() * 0.5)));
    }
}
