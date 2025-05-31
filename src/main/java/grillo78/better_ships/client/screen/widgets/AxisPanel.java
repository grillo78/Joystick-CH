package grillo78.better_ships.client.screen.widgets;

import grillo78.better_ships.BetterShips;
import grillo78.better_ships.util.JoystickConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public class AxisPanel extends WidgetPanel {

    private List<AbstractWidget> children = new ArrayList<>();
    private boolean selectingYaw = false;
    private boolean selectingPitch = false;
    private boolean selectingRoll = false;
    private boolean selectingThrust = false;
    private Button yawButton;
    private Button pitchButton;
    private Button rollButton;
    private Button thrustButton;

    public AxisPanel(int pX, int pY, int pWidth, int pHeight, Component pMessage) {
        super(pX, pY, pWidth, pHeight, pMessage);

        int centerX = pX + pWidth/2;
        int centerY = pY + pHeight/2;

        yawButton = new Button.Builder(Component.translatable("gui.yaw"), pButton -> {
            selectingYaw = true;
            yawButton.active = false;
            pitchButton.active = true;
            rollButton.active = true;
            thrustButton.active = true;
        }).pos(centerX - 95, centerY - 65).size(90, 20).build();
        addRenderableWidget(yawButton);
        pitchButton = new Button.Builder(Component.translatable("gui.pitch"), pButton -> {
            selectingPitch = true;
            yawButton.active = true;
            pitchButton.active = false;
            rollButton.active = true;
            thrustButton.active = true;
        }).pos(centerX - 95, centerY - 35).size(90, 20).build();
        addRenderableWidget(pitchButton);
        rollButton = new Button.Builder(Component.translatable("gui.roll"), pButton -> {
            selectingRoll = true;
            yawButton.active = true;
            pitchButton.active = true;
            rollButton.active = false;
            thrustButton.active = true;
        }).pos(centerX - 95, centerY - 5).size(90, 20).build();
        addRenderableWidget(rollButton);
        thrustButton = new Button.Builder(Component.translatable("gui.thrust"), pButton -> {
            selectingThrust = true;
            yawButton.active = true;
            pitchButton.active = true;
            rollButton.active = true;
            thrustButton.active = false;
        }).pos(centerX - 95, centerY + 25).size(90, 20).build();
        addRenderableWidget(thrustButton);

        Button invertYawAxis = new Button.Builder(Component.translatable("gui." + (JoystickConfig.CLIENT.yawAxisInverted.get() ? "yaw_inverted" : "yaw_not_inverted")), pButton -> {
            JoystickConfig.CLIENT.yawAxisInverted.set(!JoystickConfig.CLIENT.yawAxisInverted.get());
            pButton.setMessage(Component.translatable("gui." + (JoystickConfig.CLIENT.yawAxisInverted.get() ? "yaw_inverted" : "yaw_not_inverted")));
        }).pos(centerX - 95, centerY + 50).size(90, 20).build();
        addRenderableWidget(invertYawAxis);

        Button invertPitchAxis = new Button.Builder(Component.translatable("gui." + (JoystickConfig.CLIENT.pitchAxisInverted.get() ? "pitch_inverted" : "pitch_not_inverted")), pButton -> {
            JoystickConfig.CLIENT.pitchAxisInverted.set(!JoystickConfig.CLIENT.pitchAxisInverted.get());
            pButton.setMessage(Component.translatable("gui." + (JoystickConfig.CLIENT.pitchAxisInverted.get() ? "pitch_inverted" : "pitch_not_inverted")));
        }).pos(centerX - 95, centerY + 75).size(90, 20).build();
        addRenderableWidget(invertPitchAxis);

        Button invertRollAxis = new Button.Builder(Component.translatable("gui." + (JoystickConfig.CLIENT.rollAxisInverted.get() ? "roll_inverted" : "roll_not_inverted")), pButton -> {
            JoystickConfig.CLIENT.rollAxisInverted.set(!JoystickConfig.CLIENT.rollAxisInverted.get());
            pButton.setMessage(Component.translatable("gui." + (JoystickConfig.CLIENT.rollAxisInverted.get() ? "roll_inverted" : "roll_not_inverted")));
        }).pos(centerX + 5, centerY + 50).size(90, 20).build();
        addRenderableWidget(invertRollAxis);

        Button invertThrustAxis = new Button.Builder(Component.translatable("gui." + (JoystickConfig.CLIENT.thrustAxisInverted.get() ? "thrust_inverted" : "thrust_not_inverted")), pButton -> {
            JoystickConfig.CLIENT.thrustAxisInverted.set(!JoystickConfig.CLIENT.thrustAxisInverted.get());
            pButton.setMessage(Component.translatable("gui." + (JoystickConfig.CLIENT.thrustAxisInverted.get() ? "thrust_inverted" : "thrust_not_inverted")));
        }).pos(centerX + 5, centerY + 75).size(90, 20).build();
        addRenderableWidget(invertThrustAxis);
    }

    public List<AbstractWidget> getChildren() {
        return children;
    }

    public void tick(){
        if (GLFW.glfwJoystickPresent(JoystickConfig.CLIENT.joystickSelectedIndex.get())) {
            FloatBuffer selectedJoystick = GLFW.glfwGetJoystickAxes(JoystickConfig.CLIENT.joystickSelectedIndex.get());
            for (int i = 0; i < selectedJoystick.limit(); i++) {
                if (selectingYaw && (selectedJoystick.get(i) == 1 || selectedJoystick.get(i) == -1)) {
                    JoystickConfig.CLIENT.yawAxisSelectedIndex.set(i);
                    selectingYaw = false;
                    yawButton.active = true;
                }
                if (selectingPitch && (selectedJoystick.get(i) == 1 || selectedJoystick.get(i) == -1)) {
                    JoystickConfig.CLIENT.pitchAxisSelectedIndex.set(i);
                    selectingPitch = false;
                    pitchButton.active = true;
                }
                if (selectingRoll && (selectedJoystick.get(i) == 1 || selectedJoystick.get(i) == -1)) {
                    JoystickConfig.CLIENT.rollAxisSelectedIndex.set(i);
                    selectingRoll = false;
                    rollButton.active = true;
                }
                if (selectingThrust && (selectedJoystick.get(i) == 1 || selectedJoystick.get(i) == -1)) {
                    JoystickConfig.CLIENT.thrustAxisSelectedIndex.set(i);
                    selectingThrust = false;
                    thrustButton.active = true;
                }
            }
        }
    }

    @Override
    public void resetPanel(){
        selectingYaw = false;
        selectingPitch = false;
        selectingRoll = false;
        selectingThrust = false;
    }

    @Override
    public ResourceLocation getBackgroundTexture() {
        return new ResourceLocation(BetterShips.MOD_ID, "textures/gui/joystick_config_screen.png");
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        if (GLFW.glfwJoystickPresent(JoystickConfig.CLIENT.joystickSelectedIndex.get())) {
            pGuiGraphics.enableScissor(width / 2 + 5, height / 2 - 50, width / 2 + 95, height / 2 + 25);
            for (int i = 0; i < GLFW.glfwGetJoystickAxes(JoystickConfig.CLIENT.joystickSelectedIndex.get()).limit(); i++) {
                renderJoystickAxis(pGuiGraphics, 20 * i, GLFW.glfwGetJoystickAxes(JoystickConfig.CLIENT.joystickSelectedIndex.get()).get(i));
            }
            pGuiGraphics.disableScissor();
        }
    }

    private void renderJoystickAxis(GuiGraphics pGuiGraphics, int xOffset, double axisValue) {
        pGuiGraphics.fill(xOffset + width / 2 + 10, height / 2 + 25, xOffset + width / 2 + 20, height / 2 - 50, Color.RED.hashCode());
        pGuiGraphics.fill(xOffset + width / 2 + 10, height / 2 + 25, xOffset + width / 2 + 20, (int) (height / 2 + 25 - 75 / 2 * (axisValue + 1)), Color.GREEN.hashCode());
    }
}
