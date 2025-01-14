package grillo78.better_ships.util;

import grillo78.better_ships.capability.JoystickController;
import grillo78.better_ships.capability.ShipRotations;
import grillo78.better_ships.capability.ShipRotationsProvider;
import grillo78.better_ships.network.PacketHandler;
import grillo78.better_ships.network.messages.SyncShipRotationsCap;
import net.lointain.cosmos.entity.RocketSeatEntity;
import net.lointain.cosmos.init.CosmosModParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkDirection;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class MovementUtil {

    public static void applyRotations(Entity entity, JoystickController joystickController) {
        RocketSeatEntity vehicle = (RocketSeatEntity) entity.getVehicle();
        ShipRotations cap = vehicle.getCapability(ShipRotationsProvider.CAPABILITY).orElse(null);
        if (!entity.level().isClientSide) {
            float newX = (float) -joystickController.getPitch() * 0.1F;
            float newY = (float) joystickController.getYaw() * 0.1F;
            float newZ = (float) joystickController.getRoll() * 0.1F;
            if (!vehicle.onGround()) {
                Quaternionf deltaQuaternion = new Quaternionf().rotateXYZ(newX, newY, newZ);
                cap.setRotations(cap.getRotations().mul(deltaQuaternion, new Quaternionf()));
            } else {
                cap.setRotations(new Quaternionf().rotateY((float) (joystickController.getYaw() * 0.1F + Math.toRadians(-vehicle.getYRot()))));
            }
            for (ServerPlayer player : ((ServerLevel) entity.level()).players())
                PacketHandler.INSTANCE.sendTo(new SyncShipRotationsCap(cap.getRotations().x, cap.getRotations().y, cap.getRotations().z, cap.getRotations().w, vehicle.getId()), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);

            Vector3f auxVector = new Vector3f(0, 0, 1).rotate(cap.getRotations());
            double pitch = -Math.toDegrees(Math.asin(auxVector.y));
            double yaw = -Math.toDegrees(Math.atan2(auxVector.x, auxVector.z));
            vehicle.setYRot((float) yaw);
            vehicle.yRotO = ((float) yaw);
            vehicle.setYHeadRot((float) yaw);
            vehicle.yHeadRotO = ((float) yaw);
            vehicle.setXRot(180 + (float) pitch);
            vehicle.xRotO = (180 + (float) pitch);
        } else {
            if (joystickController.getThrust() != 0) {
                Vector3f auxVector = new Vector3f(1.5F, 0.5F, 2F).rotate(cap.getRotations()).add((float) vehicle.getPosition(0).x, (float) vehicle.getPosition(0).y, (float) vehicle.getPosition(0).z);
                for (int i = 0; i < 10; i++)
                    vehicle.level().addAlwaysVisibleParticle(CosmosModParticleTypes.THRUSTED.get(), auxVector.x, auxVector.y, auxVector.z, (vehicle.getRandom().nextBoolean() ?0.5:-0.5) + vehicle.getRandom().nextDouble(), (vehicle.getRandom().nextBoolean() ?0.5:-0.5) + vehicle.getRandom().nextDouble(), (vehicle.getRandom().nextBoolean() ?0.5:-0.5) + vehicle.getRandom().nextDouble());
                auxVector = new Vector3f(-1.5F, 0.5F, 2F).rotate(cap.getRotations()).add((float) vehicle.getPosition(0).x, (float) vehicle.getPosition(0).y, (float) vehicle.getPosition(0).z);
                for (int i = 0; i < 10; i++)
                    vehicle.level().addAlwaysVisibleParticle(CosmosModParticleTypes.THRUSTED.get(), auxVector.x, auxVector.y, auxVector.z, (vehicle.getRandom().nextBoolean() ?0.5:-0.5) + vehicle.getRandom().nextDouble(), (vehicle.getRandom().nextBoolean() ?0.5:-0.5) + vehicle.getRandom().nextDouble(), (vehicle.getRandom().nextBoolean() ?0.5:-0.5) + vehicle.getRandom().nextDouble());
            }
        }
    }

    public static void applyThrust(Entity entity, JoystickController joystickController, boolean dimension_check) {
        entity.getVehicle().setDeltaMovement(entity.getVehicle().getDeltaMovement().multiply(dimension_check ? 0 : (-joystickController.getInertiaThrust() != 0 ? 0 : 1), dimension_check ? 0 : (-joystickController.getInertiaThrust() != 0 ? 0 : 0.5), dimension_check ? 0 : (-joystickController.getInertiaThrust() != 0 ? 0 : 1)).add(entity.getVehicle().getViewVector(0).scale(-joystickController.getInertiaThrust() * (dimension_check && joystickController.isHyperSpeed() ? 10 : 1))));
    }
}
