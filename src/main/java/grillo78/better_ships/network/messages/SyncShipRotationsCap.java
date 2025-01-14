package grillo78.better_ships.network.messages;

import grillo78.better_ships.capability.ShipRotationsProvider;
import grillo78.better_ships.network.IMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.joml.Quaternionf;

import java.util.function.Supplier;

public class SyncShipRotationsCap implements IMessage<SyncShipRotationsCap> {

    private float x, y, z, w;
    private int entityId;

    public SyncShipRotationsCap() {
    }

    public SyncShipRotationsCap(float x, float y, float z, float w, int entityId) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        this.entityId = entityId;
    }

    @Override
    public void encode(SyncShipRotationsCap message, FriendlyByteBuf buffer) {
        buffer.writeFloat(message.x);
        buffer.writeFloat(message.y);
        buffer.writeFloat(message.z);
        buffer.writeFloat(message.w);
        buffer.writeInt(message.entityId);
    }

    @Override
    public SyncShipRotationsCap decode(FriendlyByteBuf buffer) {
        return new SyncShipRotationsCap(buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readInt());
    }

    @Override
    public void handle(SyncShipRotationsCap message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            if (Minecraft.getInstance().level.getEntity(message.entityId) != null)
                Minecraft.getInstance().level.getEntity(message.entityId).getCapability(ShipRotationsProvider.CAPABILITY).ifPresent(shipRotations -> {
                    shipRotations.setRotations(new Quaternionf(message.x, message.y, message.z, message.w));
                });
        });
        supplier.get().setPacketHandled(true);
    }
}
