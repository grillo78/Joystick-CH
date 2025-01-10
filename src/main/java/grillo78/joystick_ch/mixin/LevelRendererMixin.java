package grillo78.joystick_ch.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import grillo78.joystick_ch.capability.ShipRotationsProvider;
import grillo78.joystick_ch.util.RenderUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    @Inject(method = "renderLevel", at = @At("HEAD"))
    private void onExecute(PoseStack pPoseStack, float pPartialTick, long pFinishNanoTime, boolean pRenderBlockOutline, Camera pCamera, GameRenderer pGameRenderer, LightTexture pLightTexture, Matrix4f pProjectionMatrix, CallbackInfo ci) {
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.getVehicle() != null) {
            Minecraft.getInstance().player.getVehicle().getCapability(ShipRotationsProvider.CAPABILITY).ifPresent(shipRotations -> {
                RenderUtil.transformLevel(pPoseStack,pPartialTick, pFinishNanoTime, pRenderBlockOutline, pCamera, pGameRenderer, pLightTexture, pProjectionMatrix, shipRotations);
            });
        }
    }
}
