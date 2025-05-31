package grillo78.better_ships.client.screen.widgets;

import grillo78.better_ships.util.JoystickConfig;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;
import org.lwjgl.glfw.GLFW;

import java.nio.ByteBuffer;

public class KeybindButton extends Button {

    private ForgeConfigSpec.IntValue indexConfig;

    protected KeybindButton(Component pMessage, int pX, int pY, KeybindsPanel panel, ForgeConfigSpec.IntValue indexConfig) {
        super(pX, pY, 90, 20, pMessage, pButton -> {
            for (int i = 0; i < panel.getChildren().size(); i++) {
                if (panel.getChildren().get(i) instanceof KeybindButton)
                    ((KeybindButton) panel.getChildren().get(i)).setActive(true);
                ((KeybindButton) pButton).setActive(false);
            }
        }, Button.DEFAULT_NARRATION);
        this.indexConfig = indexConfig;
    }

    public void tick() {
        if (!active) {
            ByteBuffer buttons = GLFW.glfwGetJoystickButtons(JoystickConfig.CLIENT.joystickSelectedIndex.get());
            for (int i = 0; i < buttons.limit(); i++) {
                if (buttons.get(i) != (byte) 0) {
                    indexConfig.set(i);
                    setActive(true);
                }
            }
        }
    }

    private void setActive(boolean active) {
        this.active = active;
    }
}
