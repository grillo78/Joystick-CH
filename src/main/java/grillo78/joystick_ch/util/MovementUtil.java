package grillo78.joystick_ch.util;

import grillo78.joystick_ch.capability.JoystickController;
import net.lointain.cosmos.entity.RocketSeatEntity;
import net.minecraft.world.entity.Entity;

public class MovementUtil {

    public static void applyRotations(Entity entity, JoystickController joystickController) {
        RocketSeatEntity vehicle = (RocketSeatEntity) entity.getVehicle();
        vehicle.setYRot((float) (vehicle.getYRot() - joystickController.getYaw()));
        vehicle.setYHeadRot((float) (vehicle.getYRot() - joystickController.getYaw()));
        vehicle.setXRot((float) (vehicle.getXRot() + joystickController.getPitch()));
    }

    public static void applyThrust(Entity entity, JoystickController joystickController) {
        entity.getVehicle().setDeltaMovement(entity.getVehicle().getDeltaMovement().add(entity.getVehicle().getViewVector(0).scale(joystickController.getThrust())));
    }
}
