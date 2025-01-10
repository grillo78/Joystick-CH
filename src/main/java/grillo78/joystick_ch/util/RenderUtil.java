package grillo78.joystick_ch.util;

import com.mojang.blaze3d.vertex.PoseStack;
import grillo78.joystick_ch.capability.ShipRotationsProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class RenderUtil {

    public static void applyShipRotations(BlockState blockState, double x, double y, double z, float yaw, float pitch, float roll, float scale, boolean glowing, RenderLevelStageEvent provider, Entity currentEntity, Vec3 pos, CallbackInfo ci){
        ci.cancel();
        PoseStack poseStack = provider.getPoseStack();
        poseStack.pushPose();
        poseStack.translate(x - pos.x(), y - pos.y(), z - pos.z());
        if(currentEntity != null)
            currentEntity.getCapability(ShipRotationsProvider.CAPABILITY).ifPresent(capability ->{
            poseStack.mulPose(capability.getRotations());
        });
        poseStack.scale(scale, scale, scale);
        poseStack.translate(-0.5F, -0.5F, -0.5F);
    }
}
