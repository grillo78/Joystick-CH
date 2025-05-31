package grillo78.better_ships.network.messages;

import grillo78.better_ships.capability.ShipRotationsProvider;
import grillo78.better_ships.network.IMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.joml.Quaternionf;

import java.util.function.Supplier;

public class SyncShipRotationsCap implements IMessage<SyncShipRotationsCap> {

    private double x, y, z, w, xO, yO, zO, wO;
    private int entityId;

    public SyncShipRotationsCap() {
    }

    public SyncShipRotationsCap(double x, double y, double z, double w, double xO, double yO, double zO, double wO, int entityId) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        this.xO = xO;
        this.yO = yO;
        this.zO = zO;
        this.wO = wO;
        this.entityId = entityId;
    }

    @Override
    public void encode(SyncShipRotationsCap message, FriendlyByteBuf buffer) {
        buffer.writeDouble(message.x);
        buffer.writeDouble(message.y);
        buffer.writeDouble(message.z);
        buffer.writeDouble(message.w);
        buffer.writeDouble(message.xO);
        buffer.writeDouble(message.yO);
        buffer.writeDouble(message.zO);
        buffer.writeDouble(message.wO);
        buffer.writeInt(message.entityId);
    }

    @Override
    public SyncShipRotationsCap decode(FriendlyByteBuf buffer) {
        return new SyncShipRotationsCap(buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readInt());
    }

    @Override
    public void handle(SyncShipRotationsCap message, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            if (Minecraft.getInstance().level.getEntity(message.entityId) != null)
                Minecraft.getInstance().level.getEntity(message.entityId).getCapability(ShipRotationsProvider.CAPABILITY).ifPresent(shipRotations -> {
                    shipRotations.setRotations(new Quaternionf(message.x, message.y, message.z, message.w));
                    shipRotations.setRotationsO(new Quaternionf(message.xO, message.yO, message.zO, message.wO));
                });
        });
        supplier.get().setPacketHandled(true);
    }
}
