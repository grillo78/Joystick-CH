package grillo78.better_ships.client.screen;

import grillo78.better_ships.client.screen.widgets.AxisPanel;
import grillo78.better_ships.client.screen.widgets.KeybindsPanel;
import grillo78.better_ships.client.screen.widgets.WidgetPanel;
import grillo78.better_ships.util.JoystickConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class JoystickConfigurationScreen extends Screen {

    private boolean foundJoysticks = false;
    private WidgetPanel currentPanel;
    private List<WidgetPanel> panels = new ArrayList<>();
    private int panelIndex;

    public JoystickConfigurationScreen() {
        super(Component.empty());
    }

    @Override
    protected void init() {
        super.init();
        int centerX = width / 2;
        int centerY = height / 2;
        foundJoysticks = GLFW.glfwJoystickPresent(0);
        if (foundJoysticks) {
            Button deviceButton = new Button.Builder(Component.literal(GLFW.glfwGetJoystickName(JoystickConfig.CLIENT.joystickSelectedIndex.get())), pButton -> {
                if (GLFW.glfwJoystickPresent(JoystickConfig.CLIENT.joystickSelectedIndex.get() + 1))
                    JoystickConfig.CLIENT.joystickSelectedIndex.set(JoystickConfig.CLIENT.joystickSelectedIndex.get() + 1);
                else
                    JoystickConfig.CLIENT.joystickSelectedIndex.set(0);
                pButton.setMessage(Component.literal(GLFW.glfwGetJoystickName(JoystickConfig.CLIENT.joystickSelectedIndex.get())));
                currentPanel.resetPanel();
            }).pos(centerX - 95, centerY - 95).size(90, 20).build();
            addRenderableWidget(deviceButton);
            Button nextPanel = new Button.Builder(Component.literal(">"), pButton -> {
                if(panelIndex != panels.size()-1)
                    ++panelIndex;
                else{
                    panelIndex = 0;
                }
                setCurrentPanel(panels.get(panelIndex));
            }).pos(centerX + 75, centerY - 95).size(20, 20).build();
            addRenderableWidget(nextPanel);
            Button previousPanel = new Button.Builder(Component.literal("<"), pButton -> {
                if(panelIndex != 0)
                    --panelIndex;
                else{
                    panelIndex = panels.size()-1;
                }
                setCurrentPanel(panels.get(panelIndex));
            }).pos(centerX + 5, centerY - 95).size(20, 20).build();
            addRenderableWidget(previousPanel);
            currentPanel = new AxisPanel(centerX-width/2, centerY-height/2, width, height, Component.empty());
            panels.add(currentPanel);
            addRenderableWidget(currentPanel);

            panels.add(new KeybindsPanel(centerX-width/2, centerY-height/2, width, height, Component.empty()));
        }
    }

    public void setCurrentPanel(WidgetPanel currentPanel) {
        children().remove(this.currentPanel);
        renderables.remove(this.currentPanel);
        this.currentPanel = currentPanel;
        addRenderableWidget(currentPanel);
    }

    @Override
    public void tick() {
        super.tick();
        if (currentPanel != null)
            currentPanel.tick();
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pGuiGraphics);
        pGuiGraphics.blit(currentPanel.getBackgroundTexture(), width / 2 - 100, height / 2 - 100, 0, 0, 200, 200, 200, 200);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }
}
