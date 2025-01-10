package grillo78.joystick_ch;

import grillo78.joystick_ch.capability.JoystickControllerProvider;
import grillo78.joystick_ch.capability.ShipRotationsProvider;
import grillo78.joystick_ch.network.PacketHandler;
import grillo78.joystick_ch.network.messages.SendJoystickInput;
import net.lointain.cosmos.entity.RocketSeatEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.lwjgl.glfw.GLFW;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(JoystickCH.MOD_ID)
public class JoystickCH {
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "joystick_ch";

    public JoystickCH(FMLJavaModLoadingContext context) {
        context.getModEventBus().addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, this::attachCapabilities);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            MinecraftForge.EVENT_BUS.addListener(this::preRenderEntity);
            MinecraftForge.EVENT_BUS.addListener(this::postRenderEntity);
            MinecraftForge.EVENT_BUS.addListener(this::inputTick);
        });
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        PacketHandler.init();
    }

    @OnlyIn(Dist.CLIENT)
    private void inputTick(MovementInputUpdateEvent event) {
        double thrust = -GLFW.glfwGetJoystickAxes(0).get(3);
        double roll = -GLFW.glfwGetJoystickAxes(0).get(0);
        double pitch = -GLFW.glfwGetJoystickAxes(0).get(1);
        double yaw = -GLFW.glfwGetJoystickAxes(0).get(2);
        if (Minecraft.getInstance().screen == null)
            setJoystickData(thrust, yaw, pitch, roll);
        else
            setJoystickData(0, 0, 0, 0);
    }

    @OnlyIn(Dist.CLIENT)
    private void setJoystickData(double thrust, double yaw, double pitch, double roll) {

        double deadzone = 0.1;
        PacketHandler.INSTANCE.sendToServer(new SendJoystickInput(thrust < -deadzone || thrust > deadzone ? thrust : 0, yaw < -deadzone || yaw > deadzone ? yaw : 0, pitch < -deadzone || pitch > deadzone ? pitch : 0, roll < -deadzone || roll > deadzone ? roll : 0));
        Minecraft.getInstance().player.getCapability(JoystickControllerProvider.CONTROLLER).ifPresent(joystickController -> {
            joystickController.setThrust(thrust < -deadzone || thrust > deadzone ? thrust : 0);
            joystickController.setYaw(yaw < -deadzone || yaw > deadzone ? yaw : 0);
            joystickController.setPitch(pitch < -deadzone || pitch > deadzone ? pitch : 0);
            joystickController.setRoll(roll < -deadzone || roll > deadzone ? roll : 0);
        });
    }

    @OnlyIn(Dist.CLIENT)
    private void preRenderEntity(RenderLivingEvent.Pre event) {
        if (event.getEntity() instanceof Player) {
            event.getPoseStack().pushPose();
            if (event.getEntity().getVehicle() != null)
                event.getEntity().getVehicle().getCapability(ShipRotationsProvider.CAPABILITY).ifPresent(shipRotations -> {
                    event.getPoseStack().translate(0, 2 * event.getEntity().getVehicle().getPassengersRidingOffset(), 0);
                    event.getPoseStack().mulPose(shipRotations.getRotations());
                    event.getPoseStack().translate(0, 2 * -event.getEntity().getVehicle().getPassengersRidingOffset(), 0);
                });

        }
    }

    @OnlyIn(Dist.CLIENT)
    private void postRenderEntity(RenderLivingEvent.Post event) {
        if (event.getEntity() instanceof Player)
            event.getPoseStack().popPose();
    }

    private void attachCapabilities(final AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(new ResourceLocation(MOD_ID, "joystick_controller"), new JoystickControllerProvider());
        }
        if (event.getObject() instanceof RocketSeatEntity) {
            event.addCapability(new ResourceLocation(MOD_ID, "ship_rotations"), new ShipRotationsProvider());
        }
    }
}
