package grillo78.better_ships.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import grillo78.better_ships.util.RenderUtil;
import net.lointain.cosmos.procedures.ShipProcedure;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ShipProcedure.class)
public abstract class ShipProcedureMixin {

    @Shadow(remap = false) private static RenderLevelStageEvent provider;

    @Shadow(remap = false)
    protected static void renderBlockModel(BlockState blockState, BlockPos blockPos, PoseStack poseStack, int packedLight) {
    }

    @Shadow(remap = false)
    protected static void renderBlockEntity(BlockState blockState, BlockPos blockPos, PoseStack poseStack, int packedLight) {
    }

    @OnlyIn(Dist.CLIENT)
    @Inject(method = "renderModels", at = @At("HEAD"), cancellable = true, remap = false)
    private static void onExecute(RenderLevelStageEvent event, CallbackInfo ci){
//        CURRENT_ENTITY = entity.getVehicle();
        ci.cancel();
    }

//    @OnlyIn(Dist.CLIENT)
//    @Inject(method = "renderBlock", at = @At("HEAD"), cancellable = true, remap = false)
//    private static void onRenderModel(BlockState blockState, double x, double y, double z, float yaw, float pitch, float roll, float scale, boolean glowing, CallbackInfo ci) {
//        provider.getPoseStack().pushPose();
//        BlockPos blockPos = BlockPos.containing(x, y, z);
//        ResourceLocation oldBlockLoc = ForgeRegistries.BLOCKS.getKey(blockState.getBlock());
//        blockState = ForgeRegistries.BLOCKS.getDelegate(new ResourceLocation(oldBlockLoc.getNamespace(), oldBlockLoc.getPath().replace("_", "").replace("upright", ""))).get().get().defaultBlockState();
//        Vec3 pos = provider.getCamera().getPosition();
//        int packedLight = glowing ? 15728880 : LevelRenderer.getLightColor(Minecraft.getInstance().level, blockPos);
//        RenderUtil.applyShipRotations(blockState, x, y, z, yaw, pitch, roll, scale, glowing, provider, CURRENT_ENTITY, pos, ci);
//        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
//        renderBlockModel(blockState, blockPos, provider.getPoseStack(), packedLight);
//        renderBlockEntity(blockState, blockPos, provider.getPoseStack(), packedLight);
//        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
//        provider.getPoseStack().popPose();
//    }
}
