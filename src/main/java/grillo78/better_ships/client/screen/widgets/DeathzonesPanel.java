package grillo78.better_ships.client.screen.widgets;

import grillo78.better_ships.util.JoystickConfig;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.widget.ForgeSlider;

public class DeathzonesPanel extends WidgetPanel{

    private ForgeSlider thrustDeathzone;

    public DeathzonesPanel(int pX, int pY, int pWidth, int pHeight, Component pMessage) {
        super(pX, pY, pWidth, pHeight, pMessage);
        int centerX = pX + pWidth / 2;
        int centerY = pY + pHeight / 2;
        int sliderWidth = 150;
        addRenderableWidget(thrustDeathzone = new ForgeSlider(centerX-sliderWidth/2, centerY,sliderWidth, 20, Component.translatable("gui.thrust_deathzone"), Component.literal("%"),0F,0.5F, JoystickConfig.CLIENT.thrustDeathzone.get().floatValue(), 0,2, true));
    }

    @Override
    public void tick() {
        super.tick();
        JoystickConfig.CLIENT.thrustDeathzone.set(thrustDeathzone.getValue());
    }
}
