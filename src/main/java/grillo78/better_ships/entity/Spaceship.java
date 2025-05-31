package grillo78.better_ships.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import grillo78.better_ships.capability.JoystickControllerProvider;
import grillo78.better_ships.capability.ShipRotations;
import grillo78.better_ships.capability.ShipRotationsProvider;
import grillo78.better_ships.phys.OBB;
import grillo78.better_ships.util.MovementUtil;
import net.lointain.cosmos.network.CosmosModVariables;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.ITeleporter;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Spaceship extends Mob {

    private float speed = 0.5F;
    private float hyperSpeed = 10F;
    private double partialTick = 0;

    public Spaceship(EntityType<? extends Mob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        setNoGravity(true);
    }

    public void setPartialTick(double partialTick) {
        this.partialTick = partialTick;
    }

    protected double getPartialTick() {
        return partialTick;
    }

    public float getSpeed(boolean hyperSpeed) {
        return hyperSpeed? this.getHyperSpeed() : getSpeed();
    }

    public float getHyperSpeed() {
        return hyperSpeed;
    }

    @Override
    protected AABB makeBoundingBox() {
        return super.makeBoundingBox();
//        return new OBB(super.makeBoundingBox());
    }

    @Override
    protected void positionRider(Entity pPassenger, MoveFunction pCallback) {
        if (this.hasPassenger(pPassenger)) {
            this.getCapability(ShipRotationsProvider.CAPABILITY).ifPresent(shipRotations -> {

                float d0 = (float) (this.getPassengersRidingOffset() + pPassenger.getMyRidingOffset());
                Vector3f position = position().toVector3f().add(0, d0, 0);
                pCallback.accept(pPassenger, position.x, position.y, position.z);
            });
        }
    }

    @Override
    public void checkDespawn() {
    }

    @Override
    public double getPassengersRidingOffset() {
        return 0.5;
    }

    public double getCameraOffset() {
        return getPassengersRidingOffset();
    }

    public Vec3 getModelOffset(){
        return Vec3.ZERO;
    }

    @Override
    public void tick() {
        this.getCapability(ShipRotationsProvider.CAPABILITY).ifPresent(shipRotations -> {
            if (getPassengers().size() > 0 && getControllingPassenger() instanceof Player)
                getControllingPassenger().getCapability(JoystickControllerProvider.CONTROLLER).ifPresent(joystickController -> {
                    boolean inSpace = true;
                    if (CosmosModVariables.WorldVariables.get(level()).atmospheric_collision_data_map.contains(level().dimension().location().toString()))
                        inSpace = false;
                    MovementUtil.applyRotations(getControllingPassenger(), joystickController);
                    MovementUtil.applyThrust(this.getControllingPassenger(), joystickController, shipRotations, inSpace);
                    if (getBoundingBox() instanceof OBB)
                        ((OBB) getBoundingBox()).updateAxes(shipRotations.getRotations());
                });
        });

        super.tick();
        if (!level().isClientSide && CosmosModVariables.WorldVariables.get(level()).atmospheric_collision_data_map.contains(level().dimension().location().toString()) &&
                CosmosModVariables.WorldVariables.get(level()).atmospheric_collision_data_map.getCompound(level().dimension().location().toString()).getDouble("atmosphere_y") <= getY()) {
            CompoundTag atmosphericData = CosmosModVariables.WorldVariables.get(level()).atmospheric_collision_data_map.getCompound(level().dimension().location().toString());

            String travelTo = atmosphericData.getString("travel_to");
            Vec3 travelPos = new Vec3(atmosphericData.getDouble("origin_x") + Mth.nextInt(RandomSource.create(), -10, 10), atmosphericData.getDouble("origin_y") + Mth.nextInt(RandomSource.create(), -5, 5), atmosphericData.getDouble("origin_z") + Mth.nextInt(RandomSource.create(), -10, 10));
            List<Entity> passengers = new ArrayList<>();

            for (int i = 0; i < getPassengers().size(); i++) {
                passengers.add(moveToSpace(getPassengers().get(i), travelTo, travelPos));
            }
            Spaceship newShip = moveToSpace(this, travelTo, travelPos);
            for (int i = 0; i < passengers.size(); i++) {
                passengers.get(i).startRiding(newShip, true);
            }
        }

    }

    public <T extends Entity> T moveToSpace(T entity, String dimension, Vec3 position) {
        return (T) entity.changeDimension(((ServerLevel) entity.level()).getServer().getLevel(ResourceKey.create(Registries.DIMENSION, new ResourceLocation(dimension))), new ITeleporter() {
            @Override
            public boolean playTeleportSound(ServerPlayer player, ServerLevel sourceWorld, ServerLevel destWorld) {
                return false;
            }

            @Override
            public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
                Entity repositionedEntity = repositionEntity.apply(false);
                repositionedEntity.teleportTo(position.x, position.y, position.z);
                return repositionedEntity;
            }
        });
    }

    @Override
    protected void checkFallDamage(double pY, boolean pOnGround, BlockState pState, BlockPos pPos) {
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        return getPassengers().size() > 0 && getPassengers().get(0) instanceof LivingEntity ? (LivingEntity) getPassengers().get(0) : null;
    }

    private void applyMovement() {
//        this.setDeltaMovement(getDeltaMovement().multiply(0.9, 0.9, 0.9));
//        if (getDeltaMovement().length() < 0.001)
//            this.setDeltaMovement(Vec3.ZERO);
    }

    @Override
    protected InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        if (pPlayer.isSecondaryUseActive()) {
            return InteractionResult.PASS;
        } else if (!this.level().isClientSide) {
            if (pPlayer.startRiding(this)) {
                return InteractionResult.CONSUME;
            } else
                return InteractionResult.PASS;
        } else {
            return InteractionResult.SUCCESS;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void rotatePlayer(PoseStack poseStack, ShipRotations shipRotations) {
        poseStack.translate(0, 2 * getPassengersRidingOffset(), 0);
        poseStack.mulPose(new Quaternionf(shipRotations.getRotations()));
        poseStack.translate(0, 2 * -getPassengersRidingOffset(), 0);
    }
}
