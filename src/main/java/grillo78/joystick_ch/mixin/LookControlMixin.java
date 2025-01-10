package grillo78.joystick_ch.mixin;

import net.lointain.cosmos.entity.RocketSeatEntity;
import net.lointain.cosmos.procedures.PitchsideProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.LookControl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LookControl.class)
public class LookControlMixin {

    @Shadow @Final protected Mob mob;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void onExecute(CallbackInfo ci){
        if(mob instanceof RocketSeatEntity)
            ci.cancel();
    }
}
