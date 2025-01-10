package grillo78.joystick_ch.mixin;

import grillo78.joystick_ch.capability.JoystickControllerProvider;
import grillo78.joystick_ch.util.MovementUtil;
import net.lointain.cosmos.procedures.ProvideThrustProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ProvideThrustProcedure.class)
public class ProvideThrustProcedureMixin {

    @Inject(method = "execute", at = @At("HEAD"), cancellable = true, remap = false)
    private static void onExecute(LevelAccessor world, Entity entity, boolean dimension_check, CallbackInfo ci){
        ci.cancel();
        entity.getCapability(JoystickControllerProvider.CONTROLLER).ifPresent(joystickController -> {
            MovementUtil.applyThrust(entity, joystickController, dimension_check);
        });
    }
}
