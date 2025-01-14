package grillo78.better_ships;

import grillo78.better_ships.capability.JoystickControllerProvider;
import grillo78.better_ships.capability.ShipRotationsProvider;
import grillo78.better_ships.client.control.KeyMappings;
import grillo78.better_ships.client.screen.JoystickConfigurationScreen;
import grillo78.better_ships.network.PacketHandler;
import grillo78.better_ships.network.messages.SendJoystickInput;
import grillo78.better_ships.network.messages.ToggleSpaceHyperspeed;
import grillo78.better_ships.util.JoystickConfig;
import net.lointain.cosmos.entity.RocketSeatEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.lwjgl.glfw.GLFW;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(BetterShips.MOD_ID)
public class BetterShips {
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "better_ships";
    private boolean rolling = false;

    public BetterShips(FMLJavaModLoadingContext context) {
        context.registerConfig(ModConfig.Type.CLIENT, JoystickConfig.clientSpec);
        context.getModEventBus().addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, this::attachCapabilities);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            context.getModEventBus().addListener(this::registerKeys);
            MinecraftForge.EVENT_BUS.addListener(this::preRenderEntity);
            MinecraftForge.EVENT_BUS.addListener(this::postRenderEntity);
            MinecraftForge.EVENT_BUS.addListener(this::inputTick);
            MinecraftForge.EVENT_BUS.addListener(this::keyInput);
            MinecraftForge.EVENT_BUS.addListener(this::onCameraAnglesUpdate);
        });
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        PacketHandler.init();
    }

    @OnlyIn(Dist.CLIENT)
    private void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(KeyMappings.OPEN_CONFIG_SCREEN);
        event.register(KeyMappings.TOGGLE_HYPERSPEED);
        event.register(KeyMappings.TOGGLE_INPUT);
        event.register(KeyMappings.TOGGLE_ROLL);
    }

    @OnlyIn(Dist.CLIENT)
    private void keyInput(InputEvent.Key event) {
        if(Minecraft.getInstance().screen == null){
            if (event.getKey() == KeyMappings.OPEN_CONFIG_SCREEN.getKey().getValue() && event.getAction() == GLFW.GLFW_PRESS) {
                Minecraft.getInstance().setScreen(new JoystickConfigurationScreen());
            }
            if (event.getKey() == KeyMappings.TOGGLE_HYPERSPEED.getKey().getValue() && event.getAction() == GLFW.GLFW_PRESS) {
                PacketHandler.INSTANCE.sendToServer(new ToggleSpaceHyperspeed());
            }
            if (event.getKey() == KeyMappings.TOGGLE_INPUT.getKey().getValue() && event.getAction() == GLFW.GLFW_PRESS) {
                JoystickConfig.CLIENT.usingJoystick.set(!JoystickConfig.CLIENT.usingJoystick.get());
            }
            if (event.getKey() == KeyMappings.TOGGLE_ROLL.getKey().getValue() && event.getAction() == GLFW.GLFW_PRESS) {
                rolling = !rolling;
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void onCameraAnglesUpdate(ViewportEvent.ComputeCameraAngles event) {
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.getVehicle() != null && Minecraft.getInstance().player.getVehicle() instanceof RocketSeatEntity && !Minecraft.getInstance().gameRenderer.getMainCamera().isDetached()) {
            Minecraft.getInstance().player.getVehicle().getCapability(ShipRotationsProvider.CAPABILITY).ifPresent(shipRotations -> {
                event.setYaw(0);
                event.setPitch(0);
                event.setRoll(0);
            });
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void inputTick(MovementInputUpdateEvent event) {
        double thrust = 0;
        double roll = 0;
        double pitch = 0;
        double yaw = 0;
        if (Minecraft.getInstance().screen == null) {
            if (JoystickConfig.CLIENT.usingJoystick.get() && JoystickConfig.CLIENT.joystickSelectedIndex.get()>=0 && GLFW.glfwJoystickPresent(JoystickConfig.CLIENT.joystickSelectedIndex.get()) && GLFW.glfwGetJoystickAxes(JoystickConfig.CLIENT.joystickSelectedIndex.get()).limit() >= 4) {
                if(JoystickConfig.CLIENT.thrustAxisSelectedIndex.get()>=0)
                    thrust = -GLFW.glfwGetJoystickAxes(JoystickConfig.CLIENT.joystickSelectedIndex.get()).get(JoystickConfig.CLIENT.thrustAxisSelectedIndex.get()) * (JoystickConfig.CLIENT.thrustAxisInverted.get() ? -1 : 1);
                if(JoystickConfig.CLIENT.rollAxisSelectedIndex.get()>=0)
                    roll = -GLFW.glfwGetJoystickAxes(JoystickConfig.CLIENT.joystickSelectedIndex.get()).get(JoystickConfig.CLIENT.rollAxisSelectedIndex.get()) * (JoystickConfig.CLIENT.rollAxisInverted.get() ? -1 : 1);
                if(JoystickConfig.CLIENT.pitchAxisSelectedIndex.get()>=0)
                    pitch = -GLFW.glfwGetJoystickAxes(JoystickConfig.CLIENT.joystickSelectedIndex.get()).get(JoystickConfig.CLIENT.pitchAxisSelectedIndex.get()) * (JoystickConfig.CLIENT.pitchAxisInverted.get() ? -1 : 1);
                if(JoystickConfig.CLIENT.yawAxisSelectedIndex.get()>=0)
                    yaw = -GLFW.glfwGetJoystickAxes(JoystickConfig.CLIENT.joystickSelectedIndex.get()).get(JoystickConfig.CLIENT.yawAxisSelectedIndex.get()) * (JoystickConfig.CLIENT.yawAxisInverted.get() ? -1 : 1);

            } else {
                thrust = ((Minecraft.getInstance().options.keySprint.isDown() ? 1 : 0) + (event.getInput().jumping ? -1 : 0) )* (JoystickConfig.CLIENT.thrustAxisInverted.get()? -1:1);
                pitch = ((event.getInput().down ? -0.5 : 0) + (event.getInput().up ? 0.5 : 0)) * (JoystickConfig.CLIENT.pitchAxisInverted.get()? -0.5:0.5);
                if (rolling)
                    roll = ((event.getInput().left ? 0.5 : 0) + (event.getInput().right ? -0.5 : 0)) * (JoystickConfig.CLIENT.rollAxisInverted.get()? -0.5:0.5);
                else
                    yaw = ((event.getInput().left ? 0.5 : 0) + (event.getInput().right ? -0.5 : 0)) * (JoystickConfig.CLIENT.yawAxisInverted.get()? -0.5:0.5);
            }
        }
        setJoystickData(thrust, yaw, pitch, roll);
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
