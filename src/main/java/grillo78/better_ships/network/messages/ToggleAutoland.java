package grillo78.better_ships.network.messages;

import grillo78.better_ships.capability.JoystickControllerProvider;
import grillo78.better_ships.network.IMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ToggleAutoland implements IMessage<ToggleAutoland> {

    public ToggleAutoland() {
    }

    @Override
    public void encode(ToggleAutoland message, FriendlyByteBuf buffer) {
    }

    @Override
    public ToggleAutoland decode(FriendlyByteBuf buffer) {
        return new ToggleAutoland();
    }

    @Override
    public void handle(ToggleAutoland message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            supplier.get().getSender().getCapability(JoystickControllerProvider.CONTROLLER).ifPresent(joystickController -> {
                joystickController.toggleAutoland();
            });
        });
        supplier.get().setPacketHandled(true);
    }
}
