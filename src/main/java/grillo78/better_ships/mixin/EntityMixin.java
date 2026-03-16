package grillo78.better_ships.mixin;

import com.google.common.collect.ImmutableList;
import grillo78.better_ships.phys.OrientedBoundingBox;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.extensions.IForgeEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Entity.class)
public abstract class EntityMixin implements IForgeEntity {

    @Shadow
    private AABB bb;

    @Shadow
    private Level level;

    @Shadow
    public static Vec3 collideBoundingBox(@Nullable Entity pEntity, Vec3 pVec, AABB pCollisionBox, Level pLevel, List<VoxelShape> pPotentialHits) {
        return null;
    }

    @Shadow
    public abstract AABB getBoundingBox();

    @Shadow
    public abstract boolean onGround();

    @Shadow
    private Vec3 deltaMovement;

    @Shadow
    public abstract Level level();

    @Shadow
    protected abstract Vec3 collide(Vec3 pVec);

    @Shadow public boolean verticalCollision;

    @Shadow public boolean horizontalCollision;

    @Shadow public abstract void setOnGround(boolean pOnGround);

    @Shadow public abstract void setDeltaMovement(Vec3 pDeltaMovement);

    @Inject(method = "collide", at = @At("HEAD"), cancellable = true)
    public void onIsColliding(Vec3 pVec, CallbackInfoReturnable<Vec3> cir) {
//        if (getBoundingBox() instanceof OrientedBoundingBox) {
//
//            OrientedBoundingBox aabb = (OrientedBoundingBox) this.getBoundingBox();
//            List<VoxelShape> list = this.level.getEntityCollisions((Entity) (Object) this, aabb);
//
//            List<VoxelShape> shapes = getVoxelShapes((Entity) (Object) this, pVec, aabb, this.level(), list);
//            Vec3 vec3 = pVec;
//            double x = 0;
//            double y = 0;
//            double z = 0;
//            double factor = 0.0001;
//            boolean collided = false;
//            int steps = (int) (pVec.length() / factor);
//            for (int i = 0; i < steps; i++) {
//                Vec3 auxVec = pVec.scale(i * factor);
//                OrientedBoundingBox movedAABB = aabb.move(auxVec);
//                for (int j = 0; j < shapes.size(); j++) {
//                    if(movedAABB.intersects(shapes.get(j).bounds())){
//                        collided = true;
//                        movedAABB = aabb.move(x, auxVec.y, auxVec.z);
//                        if(!movedAABB.intersects(shapes.get(j).bounds())){
//                            x = vec3.x;
//                            vec3 = new Vec3(x, pVec.y, pVec.z);
//                            horizontalCollision = true;
//                        }
//                        movedAABB = aabb.move(auxVec.x, y, auxVec.z);
//                        if(!movedAABB.intersects(shapes.get(j).bounds())){
//                            y = vec3.y;
//                            vec3 = new Vec3(pVec.x, y, pVec.z);
//                            verticalCollision = true;
//                        }
//                        movedAABB = aabb.move(auxVec.x, auxVec.y, z);
//                        if(!movedAABB.intersects(shapes.get(j).bounds())){
//                            z = vec3.z;
//                            vec3 = new Vec3(pVec.x, pVec.y, z);
//                            horizontalCollision = true;
//                        }
//                    }
//                }
//            }
//            cir.setReturnValue(collided? vec3 : pVec);
//            if(collided)
//                setDeltaMovement(vec3);
//        }
    }

    private static ImmutableList<VoxelShape> getVoxelShapes(@javax.annotation.Nullable Entity pEntity, Vec3 pVec, AABB pCollisionBox, Level pLevel, List<VoxelShape> pPotentialHits) {
        ImmutableList.Builder<VoxelShape> builder = ImmutableList.builderWithExpectedSize(pPotentialHits.size() + 1);
        if (!pPotentialHits.isEmpty()) {
            builder.addAll(pPotentialHits);
        }

        WorldBorder worldborder = pLevel.getWorldBorder();
        boolean flag = pEntity != null && worldborder.isInsideCloseToBorder(pEntity, pCollisionBox.expandTowards(pVec));
        if (flag) {
            builder.add(worldborder.getCollisionShape());
        }

        builder.addAll(pLevel.getBlockCollisions(pEntity, pCollisionBox.expandTowards(pVec)));
        return builder.build();
    }
}
