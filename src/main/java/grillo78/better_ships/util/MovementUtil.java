package grillo78.better_ships.util;

import grillo78.better_ships.capability.JoystickController;
import grillo78.better_ships.capability.ShipRotations;
import grillo78.better_ships.capability.ShipRotationsProvider;
import grillo78.better_ships.entity.Spaceship;
import grillo78.better_ships.network.PacketHandler;
import grillo78.better_ships.network.messages.SyncShipRotationsCap;
import net.lointain.cosmos.entity.RocketSeatEntity;
import net.lointain.cosmos.init.CosmosModParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkDirection;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Random;

public class MovementUtil {

    public static void applyRotations(Entity entity, JoystickController joystickController) {
        Entity vehicle = entity.getVehicle();
        vehicle.getCapability(ShipRotationsProvider.CAPABILITY).ifPresent(shipRotations -> {
            if (!joystickController.isAutolanding()) {
                float newX = (float) -joystickController.getPitch() * 0.1F;
                float newY = (float) joystickController.getYaw() * 0.1F;
                float newZ = (float) joystickController.getRoll() * 0.1F;
                if (!vehicle.onGround()) {
                    Quaternionf deltaQuaternion = new Quaternionf().rotateXYZ(newX, newY, newZ);
                    shipRotations.setRotations(shipRotations.getRotations().mul(deltaQuaternion, new Quaternionf()));
                } else {
                    Vector3f auxVector = new Vector3f(0, 0, 1).rotate(new Quaternionf(shipRotations.getRotations()));
                    double yaw = newY + Math.atan2(auxVector.x, auxVector.z);
                    Quaternionf deltaQuaternion = new Quaternionf().rotateXYZ(0, (float) yaw, 0);
                    shipRotations.setRotations(deltaQuaternion);
                }
                if (!entity.level().isClientSide) {
                    for (ServerPlayer player : ((ServerLevel) entity.level()).players())
                        PacketHandler.INSTANCE.sendTo(new SyncShipRotationsCap(shipRotations.getRotations().x, shipRotations.getRotations().y, shipRotations.getRotations().z, shipRotations.getRotations().w, shipRotations.getRotationsO().x, shipRotations.getRotationsO().y, shipRotations.getRotationsO().z, shipRotations.getRotationsO().w, vehicle.getId()), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
                } else {
                    if (joystickController.getThrust() != 0 && vehicle instanceof RocketSeatEntity) {
                        Random random = new Random();
                        Vector3f speed = new Vector3f(0, 0, joystickController.getThrust() < 0 ? 10 : -10).rotate(shipRotations.getRotations());
                        Vector3f auxVector = new Vector3f(1.5F, 0.5F, joystickController.getThrust() < 0 ? 2 : -3).rotate(shipRotations.getRotations()).add((float) vehicle.getPosition(0).x, (float) vehicle.getPosition(0).y, (float) vehicle.getPosition(0).z);
                        for (int i = 0; i < 10; i++)
                            vehicle.level().addAlwaysVisibleParticle(CosmosModParticleTypes.THRUSTED.get(), auxVector.x, auxVector.y, auxVector.z, speed.x + (random.nextBoolean() ? 0.5 : -0.5) + random.nextDouble(), speed.y + (random.nextBoolean() ? 0.5 : -0.5) + random.nextDouble(), speed.z + (random.nextBoolean() ? 0.5 : -0.5) + random.nextDouble());
                        auxVector = new Vector3f(-1.5F, 0.5F, joystickController.getThrust() < 0 ? 2 : -3).rotate(shipRotations.getRotations()).add((float) vehicle.getPosition(0).x, (float) vehicle.getPosition(0).y, (float) vehicle.getPosition(0).z);
                        for (int i = 0; i < 10; i++)
                            vehicle.level().addAlwaysVisibleParticle(CosmosModParticleTypes.THRUSTED.get(), auxVector.x, auxVector.y, auxVector.z, speed.x + (random.nextBoolean() ? 0.5 : -0.5) + random.nextDouble(), speed.y + (random.nextBoolean() ? 0.5 : -0.5) + random.nextDouble(), speed.z + (random.nextBoolean() ? 0.5 : -0.5) + random.nextDouble());
                    }
                }
                Vector3f auxVector = new Vector3f(0, 0, 1).rotate(shipRotations.getRotations());
                double pitch = -Math.toDegrees(Math.asin(auxVector.y));
                double yaw = -Math.toDegrees(Math.atan2(auxVector.x, auxVector.z));
                vehicle.setYRot((float) yaw);
                vehicle.yRotO = ((float) yaw);
                vehicle.setYHeadRot((float) yaw);
                if (vehicle instanceof LivingEntity)
                    ((LivingEntity) vehicle).yHeadRotO = ((float) yaw);
                vehicle.setXRot(180 + (float) pitch);
                vehicle.xRotO = (180 + (float) pitch);
            } else {
                shipRotations.getRotations().nlerp(new Quaternionf().rotateY(90), joystickController.getAutolandingTicks() / 20F);
            }

        });
    }

    public static void applyThrust(Entity entity, JoystickController joystickController, ShipRotations shipRotations, boolean dimension_check) {
        if (joystickController.isAutolanding()) {
            entity.getVehicle().setDeltaMovement(Vec3.ZERO);
        } else {
            Vec3 thrustVector = new Vec3(new Vector3f(0, 0, -1).rotate(new Quaternionf(shipRotations.getRotations())));
            entity.getVehicle().setDeltaMovement(entity.getVehicle().getDeltaMovement().multiply(dimension_check ? 0 : (-joystickController.getInertiaThrust() != 0 ? 0 : 1),
                    dimension_check ? 0 : (-joystickController.getInertiaThrust() != 0 ? 0 : 0.5),
                    dimension_check ? 0 : (-joystickController.getInertiaThrust() != 0 ? 0 : 1))
                    .add(thrustVector.scale(-joystickController.getInertiaThrust() * (entity.getVehicle() instanceof Spaceship? ((Spaceship)entity.getVehicle()).getSpeed(joystickController.isHyperSpeed()) : 1))));
        }
    }
}
