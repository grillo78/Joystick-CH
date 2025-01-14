package grillo78.better_ships.network.messages;

import grillo78.better_ships.capability.JoystickControllerProvider;
import grillo78.better_ships.network.IMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SendJoystickInput implements IMessage<SendJoystickInput> {

    private double thrust;
    private double yaw;
    private double pitch;
    private double roll;

    public SendJoystickInput() {
    }

    public SendJoystickInput(double thrust, double yaw, double pitch, double roll) {
        this.thrust = thrust;
        this.yaw = yaw;
        this.pitch = pitch;
        this.roll = roll;
    }

    @Override
    public void encode(SendJoystickInput message, FriendlyByteBuf buffer) {
        buffer.writeDouble(message.thrust);
        buffer.writeDouble(message.yaw);
        buffer.writeDouble(message.pitch);
        buffer.writeDouble(message.roll);
    }

    @Override
    public SendJoystickInput decode(FriendlyByteBuf buffer) {
        return new SendJoystickInput(buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
    }

    @Override
    public void handle(SendJoystickInput message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            supplier.get().getSender().getCapability(JoystickControllerProvider.CONTROLLER).ifPresent(joystickController -> {

                joystickController.setThrust(message.thrust);
                joystickController.setYaw(message.yaw);
                joystickController.setPitch(message.pitch);
                joystickController.setRoll(message.roll);
            });
        });
        supplier.get().setPacketHandled(true);
    }
}
