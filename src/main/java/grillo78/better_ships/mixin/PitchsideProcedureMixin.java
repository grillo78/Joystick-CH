package grillo78.better_ships.mixin;

import net.lointain.cosmos.procedures.PitchsideProcedure;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PitchsideProcedure.class)
public class PitchsideProcedureMixin {

    @Inject(method = "execute", at = @At("HEAD"), cancellable = true, remap = false)
    private static void onExecute(Entity entity, boolean dimension_check, CallbackInfo ci){
        ci.cancel();
    }
}
