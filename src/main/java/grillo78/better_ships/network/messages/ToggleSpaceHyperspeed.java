package grillo78.better_ships.network.messages;

import grillo78.better_ships.capability.JoystickControllerProvider;
import grillo78.better_ships.network.IMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ToggleSpaceHyperspeed implements IMessage<ToggleSpaceHyperspeed> {

    public ToggleSpaceHyperspeed() {
    }

    @Override
    public void encode(ToggleSpaceHyperspeed message, FriendlyByteBuf buffer) {
    }

    @Override
    public ToggleSpaceHyperspeed decode(FriendlyByteBuf buffer) {
        return new ToggleSpaceHyperspeed();
    }

    @Override
    public void handle(ToggleSpaceHyperspeed message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            supplier.get().getSender().getCapability(JoystickControllerProvider.CONTROLLER).ifPresent(joystickController -> {
                joystickController.toggleHyperSpeed();
            });
        });
        supplier.get().setPacketHandled(true);
    }
}
