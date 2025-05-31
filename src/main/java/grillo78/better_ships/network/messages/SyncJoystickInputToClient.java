package grillo78.better_ships.network.messages;

import grillo78.better_ships.capability.JoystickControllerProvider;
import grillo78.better_ships.network.IMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncJoystickInputToClient implements IMessage<SyncJoystickInputToClient> {

    private double thrust;
    private double yaw;
    private double pitch;
    private double roll;
    private boolean hyperSpeed;
    private int entityID;

    public SyncJoystickInputToClient() {
    }

    public SyncJoystickInputToClient(double thrust, double yaw, double pitch, double roll, boolean hyperSpeed, int entityID) {
        this.thrust = thrust;
        this.yaw = yaw;
        this.pitch = pitch;
        this.roll = roll;
        this.hyperSpeed = hyperSpeed;
        this.entityID = entityID;
    }

    @Override
    public void encode(SyncJoystickInputToClient message, FriendlyByteBuf buffer) {
        buffer.writeDouble(message.thrust);
        buffer.writeDouble(message.yaw);
        buffer.writeDouble(message.pitch);
        buffer.writeDouble(message.roll);
        buffer.writeBoolean(message.hyperSpeed);
        buffer.writeInt(message.entityID);
    }

    @Override
    public SyncJoystickInputToClient decode(FriendlyByteBuf buffer) {
        return new SyncJoystickInputToClient(buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readBoolean(), buffer.readInt());
    }

    @Override
    public void handle(SyncJoystickInputToClient message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            Minecraft.getInstance().level.getEntity(message.entityID).getCapability(JoystickControllerProvider.CONTROLLER).ifPresent(joystickController -> {
                joystickController.setThrust(message.thrust);
                joystickController.setYaw(message.yaw);
                joystickController.setPitch(message.pitch);
                joystickController.setRoll(message.roll);
                joystickController.setHyperSpeed(message.hyperSpeed);
            });
        });
        supplier.get().setPacketHandled(true);
    }
}
