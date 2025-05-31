package grillo78.better_ships.network;

import grillo78.better_ships.BetterShips;
import grillo78.better_ships.network.messages.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {
    public static final String PROTOCOL_VERSION = "1";

    public static SimpleChannel INSTANCE;
    private static int nextId = 0;

    /**
     * create the network channel and register the packets
     */
    public static void init() {
        // Create the Network channel
        INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(BetterShips.MOD_ID, "network"),() -> PROTOCOL_VERSION,
                PROTOCOL_VERSION::equals,
                PROTOCOL_VERSION::equals);
        register(SendJoystickInput.class, new SendJoystickInput());
        register(SyncJoystickInputToClient.class, new SyncJoystickInputToClient());
        register(SyncShipRotationsCap.class, new SyncShipRotationsCap());
        register(ToggleSpaceHyperspeed.class, new ToggleSpaceHyperspeed());
        register(ToggleAutoland.class, new ToggleAutoland());
    }

    private static <T extends IMessage> void register(Class<T> clazz, IMessage<T> message) {
        INSTANCE.registerMessage(nextId++, clazz, message::encode, message::decode, message::handle);
    }
}
