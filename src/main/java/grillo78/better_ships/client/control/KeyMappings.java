package grillo78.better_ships.client.control;

import com.mojang.blaze3d.platform.InputConstants;
import grillo78.better_ships.BetterShips;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class KeyMappings {
    public static final KeyMapping OPEN_CONFIG_SCREEN = new KeyMapping("key."+ BetterShips.MOD_ID +".open_config_screen", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_J, "key.categories."+ BetterShips.MOD_ID);
    public static final KeyMapping TOGGLE_HYPERSPEED = new KeyMapping("key."+ BetterShips.MOD_ID +".toggle_hyperspeed", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_H, "key.categories."+ BetterShips.MOD_ID);
    public static final KeyMapping TOGGLE_INPUT = new KeyMapping("key."+ BetterShips.MOD_ID +".toggle_input", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_I, "key.categories."+ BetterShips.MOD_ID);
    public static final KeyMapping TOGGLE_ROLL = new KeyMapping("key."+ BetterShips.MOD_ID +".toggle_roll", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, "key.categories."+ BetterShips.MOD_ID);
}
