package grillo78.better_ships.client.screen.widgets;

import grillo78.better_ships.util.JoystickConfig;
import net.minecraft.network.chat.Component;

public class KeybindsPanel extends WidgetPanel {
    public KeybindsPanel(int pX, int pY, int pWidth, int pHeight, Component pMessage) {
        super(pX, pY, pWidth, pHeight, pMessage);

        int centerX = pX + pWidth / 2;
        int centerY = pY + pHeight / 2;
        addRenderableWidget(new KeybindButton(Component.translatable("key.better_ships.shoot"), centerX - 95, centerY - 65, this, JoystickConfig.CLIENT.shootButtonSelectedIndex));
        addRenderableWidget(new KeybindButton(Component.translatable("key.better_ships.toggle_hyperspeed"), centerX - 95, centerY - 35, this, JoystickConfig.CLIENT.hyperspeedButtonSelectedIndex));
        addRenderableWidget(new KeybindButton(Component.translatable("key.better_ships.autoland"), centerX - 95, centerY - 5, this, JoystickConfig.CLIENT.autolandButtonSelectedIndex));
    }

    @Override
    public void tick() {
        super.tick();
        for (int i = 0; i < getChildren().size(); i++) {
            if (getChildren().get(i) instanceof KeybindButton)
                ((KeybindButton) getChildren().get(i)).tick();
        }
    }
}
