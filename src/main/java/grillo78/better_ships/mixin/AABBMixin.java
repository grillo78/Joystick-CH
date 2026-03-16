package grillo78.better_ships.mixin;

import grillo78.better_ships.phys.OrientedBoundingBox;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AABB.class)
public class AABBMixin {

    @Inject(method = "intersects(Lnet/minecraft/world/phys/AABB;)Z", at = @At("HEAD"), cancellable = true)
    public void onSetBoundingBox(AABB pOther, CallbackInfoReturnable<Boolean> cir) {
        if (pOther instanceof OrientedBoundingBox) {
            cir.setReturnValue(pOther.intersects((AABB) (Object) this));
        }
    }
}
