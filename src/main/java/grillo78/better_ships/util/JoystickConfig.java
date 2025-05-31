package grillo78.better_ships.util;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.glfw.GLFW;

public class JoystickConfig {
    public static class Client {
        public final ForgeConfigSpec.IntValue joystickSelectedIndex;
        public final ForgeConfigSpec.IntValue yawAxisSelectedIndex;
        public final ForgeConfigSpec.IntValue pitchAxisSelectedIndex;
        public final ForgeConfigSpec.IntValue rollAxisSelectedIndex;
        public final ForgeConfigSpec.IntValue thrustAxisSelectedIndex;
        public final ForgeConfigSpec.IntValue shootButtonSelectedIndex;
        public final ForgeConfigSpec.IntValue hyperspeedButtonSelectedIndex;
        public final ForgeConfigSpec.IntValue autolandButtonSelectedIndex;
        public final ForgeConfigSpec.BooleanValue yawAxisInverted;
        public final ForgeConfigSpec.BooleanValue pitchAxisInverted;
        public final ForgeConfigSpec.BooleanValue rollAxisInverted;
        public final ForgeConfigSpec.BooleanValue thrustAxisInverted;
        public final ForgeConfigSpec.BooleanValue usingJoystick;

        Client(ForgeConfigSpec.Builder builder) {
            builder.push("client");

            this.joystickSelectedIndex = builder.comment("The index of the joystick in the game devices list").defineInRange("joystickSelectedIndex", -1, -1, GLFW.GLFW_JOYSTICK_LAST);
            this.yawAxisSelectedIndex = builder.comment("The index of the yaw axis on the joystick").defineInRange("yawAxisSelectedIndex", -1, -1, Integer.MAX_VALUE);
            this.pitchAxisSelectedIndex = builder.comment("The index of the pitch axis on the joystick").defineInRange("pitchAxisSelectedIndex", -1, -1, Integer.MAX_VALUE);
            this.rollAxisSelectedIndex = builder.comment("The index of the roll axis on the joystick").defineInRange("rollAxisSelectedIndex", -1, -1, Integer.MAX_VALUE);
            this.thrustAxisSelectedIndex = builder.comment("The index of the thrust axis on the joystick").defineInRange("thrustAxisSelectedIndex", -1, -1, Integer.MAX_VALUE);
            this.shootButtonSelectedIndex = builder.comment("The index of the shoot button on the joystick").defineInRange("shootButtonSelectedIndex", -1, -1, Integer.MAX_VALUE);
            this.autolandButtonSelectedIndex = builder.comment("The index of the autoland button on the joystick").defineInRange("autolandButtonSelectedIndex", -1, -1, Integer.MAX_VALUE);
            this.hyperspeedButtonSelectedIndex = builder.comment("The index of the hyperspeed button on the joystick").defineInRange("hyperspeedButtonSelectedIndex", -1, -1, Integer.MAX_VALUE);
            this.yawAxisInverted = builder.comment("If the yaw axis is inverted").define("yawAxisInverted", false);
            this.pitchAxisInverted = builder.comment("If the pitch axis is inverted").define("pitchAxisInverted",false);
            this.rollAxisInverted = builder.comment("If the roll axis is inverted").define("rollAxisInverted",false);
            this.thrustAxisInverted = builder.comment("If the thrust axis is inverted").define("thrustAxisInverted",true);
            this.usingJoystick = builder.comment("If the joystick is gonna be used").define("usingJoystick",true);

            builder.pop();
        }
    }

    public static final ForgeConfigSpec clientSpec;
    public static final Client CLIENT;

    static {
        final Pair<Client, ForgeConfigSpec> clientSpecPair = new ForgeConfigSpec.Builder().configure(Client::new);
        clientSpec = clientSpecPair.getRight();
        CLIENT = clientSpecPair.getLeft();
    }
}
