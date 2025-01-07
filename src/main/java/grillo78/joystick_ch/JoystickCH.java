package grillo78.joystick_ch;

import grillo78.joystick_ch.capability.JoystickController;
import grillo78.joystick_ch.capability.JoystickControllerProvider;
import grillo78.joystick_ch.network.PacketHandler;
import grillo78.joystick_ch.network.messages.SendJoystickInput;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.lwjgl.glfw.GLFW;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(JoystickCH.MOD_ID)
public class JoystickCH
{
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "joystick_ch";

    public JoystickCH(FMLJavaModLoadingContext context)
    {
        context.getModEventBus().addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.addListener(this::inputTick);
        MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, this::attachCapabilities);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        PacketHandler.init();
    }

    private void inputTick(MovementInputUpdateEvent event){
        double thrust = -GLFW.glfwGetJoystickAxes(0).get(3);
        double yaw = -GLFW.glfwGetJoystickAxes(0).get(0);
        double pitch = -GLFW.glfwGetJoystickAxes(0).get(1);
        double roll = -GLFW.glfwGetJoystickAxes(0).get(2);
        PacketHandler.INSTANCE.sendToServer(new SendJoystickInput(thrust, yaw, pitch, roll));
        Minecraft.getInstance().player.getCapability(JoystickControllerProvider.CONTROLLER).ifPresent(joystickController -> {
            joystickController.setThrust(thrust);
            joystickController.setYaw(yaw);
            joystickController.setPitch(pitch);
            joystickController.setRoll(roll);
        });
    }

    private void attachCapabilities(final AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(new ResourceLocation(MOD_ID, "joystick_controller"), new JoystickControllerProvider());
        }
    }
}
