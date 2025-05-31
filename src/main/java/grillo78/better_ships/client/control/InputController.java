package grillo78.better_ships.client.control;

import grillo78.better_ships.util.JoystickConfig;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import org.lwjgl.glfw.GLFW;

public class InputController {

    private float thrust = 0;
    private float roll = 0;
    private float pitch = 0;
    private float yaw = 0;
    private boolean oHyperspeed = false;
    private boolean hyperspeed = false;
    private boolean oShooting = false;
    private boolean shooting = false;
    private boolean oAutoland = false;
    private boolean autoland = false;

    @OnlyIn(Dist.CLIENT)
    public void tick(){
        if (JoystickConfig.CLIENT.thrustAxisSelectedIndex.get() >= 0)
            thrust = -GLFW.glfwGetJoystickAxes(JoystickConfig.CLIENT.joystickSelectedIndex.get()).get(JoystickConfig.CLIENT.thrustAxisSelectedIndex.get()) * (JoystickConfig.CLIENT.thrustAxisInverted.get() ? -1 : 1);
        if (JoystickConfig.CLIENT.rollAxisSelectedIndex.get() >= 0)
            roll = -GLFW.glfwGetJoystickAxes(JoystickConfig.CLIENT.joystickSelectedIndex.get()).get(JoystickConfig.CLIENT.rollAxisSelectedIndex.get()) * (JoystickConfig.CLIENT.rollAxisInverted.get() ? -1 : 1);
        if (JoystickConfig.CLIENT.pitchAxisSelectedIndex.get() >= 0)
            pitch = -GLFW.glfwGetJoystickAxes(JoystickConfig.CLIENT.joystickSelectedIndex.get()).get(JoystickConfig.CLIENT.pitchAxisSelectedIndex.get()) * (JoystickConfig.CLIENT.pitchAxisInverted.get() ? -1 : 1);
        if (JoystickConfig.CLIENT.yawAxisSelectedIndex.get() >= 0)
            yaw = -GLFW.glfwGetJoystickAxes(JoystickConfig.CLIENT.joystickSelectedIndex.get()).get(JoystickConfig.CLIENT.yawAxisSelectedIndex.get()) * (JoystickConfig.CLIENT.yawAxisInverted.get() ? -1 : 1);
        if(JoystickConfig.CLIENT.hyperspeedButtonSelectedIndex.get() >= 0){
            oHyperspeed = hyperspeed;
            hyperspeed = GLFW.glfwGetJoystickButtons(JoystickConfig.CLIENT.joystickSelectedIndex.get()).get(JoystickConfig.CLIENT.hyperspeedButtonSelectedIndex.get()) != (byte) 0;
        }
        if(JoystickConfig.CLIENT.shootButtonSelectedIndex.get() >= 0){
            oShooting = hyperspeed;
            shooting = GLFW.glfwGetJoystickButtons(JoystickConfig.CLIENT.joystickSelectedIndex.get()).get(JoystickConfig.CLIENT.shootButtonSelectedIndex.get()) != (byte) 0;
        }
        if(JoystickConfig.CLIENT.autolandButtonSelectedIndex.get() >= 0){
            oAutoland = autoland;
            autoland = GLFW.glfwGetJoystickButtons(JoystickConfig.CLIENT.joystickSelectedIndex.get()).get(JoystickConfig.CLIENT.autolandButtonSelectedIndex.get()) != (byte) 0;
        }
    }

    public float getThrust() {
        return thrust;
    }

    public float getRoll() {
        return roll;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public boolean isoHyperspeed() {
        return oHyperspeed;
    }

    public boolean isHyperspeed() {
        return hyperspeed;
    }

    public boolean isoShooting() {
        return oShooting;
    }

    public boolean isShooting() {
        return shooting;
    }

    public boolean isoAutoland() {
        return oAutoland;
    }

    public boolean isAutoland() {
        return autoland;
    }
}
