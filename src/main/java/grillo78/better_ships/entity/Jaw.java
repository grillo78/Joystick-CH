package grillo78.better_ships.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import grillo78.better_ships.capability.ShipRotations;
import grillo78.better_ships.capability.ShipRotationsProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.concurrent.atomic.AtomicReference;

public class Jaw extends Spaceship {

    public Jaw(EntityType<? extends Mob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public float getHyperSpeed() {
        return 13.5F;
    }

    @Override
    public float getSpeed() {
        return 5;
    }

    public static AttributeSupplier.Builder registerMonsterAttributes() {
        return Mob.createMobAttributes();
    }

    @Override
    protected void positionRider(Entity pPassenger, MoveFunction pCallback) {
        if (this.hasPassenger(pPassenger)) {
            this.getCapability(ShipRotationsProvider.CAPABILITY).ifPresent(shipRotations -> {
                AtomicReference<Vec3> position = new AtomicReference<>();
                switch (getPassengers().indexOf(pPassenger)) {
                    default:
                    case 0:
                        Vector3f vector = new Vector3f(-0.625F, (float) (this.getPassengersRidingOffset() + pPassenger.getMyRidingOffset()), -2.05F).rotate(shipRotations.getRotationsO().nlerp(shipRotations.getRotations(), (float) getPartialTick(), new Quaternionf()));
                        position.set(position().add(vector.x, vector.y, vector.z));
                        break;
                    case 1:
                        vector = new Vector3f(0.625F, (float) (this.getPassengersRidingOffset() + pPassenger.getMyRidingOffset()), -2.05F).rotate(shipRotations.getRotationsO().nlerp(shipRotations.getRotations(), (float) getPartialTick(), new Quaternionf()));
                        position.set(position().add(vector.x, vector.y, vector.z));
                        break;
                    case 2:
                        vector = new Vector3f(-0.625F, (float) (this.getPassengersRidingOffset() - 0.45 + pPassenger.getMyRidingOffset()), 1.25F).rotate(shipRotations.getRotationsO().nlerp(shipRotations.getRotations(), (float) getPartialTick(), new Quaternionf()));
                        position.set(position().add(vector.x, vector.y, vector.z));
                        break;
                    case 3:
                        vector = new Vector3f(0.625F, (float) (this.getPassengersRidingOffset() - 0.45 + pPassenger.getMyRidingOffset()), 1.25F).rotate(shipRotations.getRotationsO().nlerp(shipRotations.getRotations(), (float) getPartialTick(), new Quaternionf()));
                        position.set(position().add(vector.x, vector.y, vector.z));
                        break;
                }
                pCallback.accept(pPassenger, position.get().x, position.get().y, position.get().z);
            });
        }
    }

    @Override
    public Vec3 getModelOffset() {
        return new Vec3(0, -1.5, -0.4);
    }

    @Override
    protected boolean canAddPassenger(Entity pPassenger) {
        return getPassengers().size() < 4;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void rotatePlayer(PoseStack poseStack, ShipRotations shipRotations) {
        poseStack.mulPose(new Quaternionf(shipRotations.getRotations()));
    }

    @Override
    public double getCameraOffset() {
        return 0;
    }
}
