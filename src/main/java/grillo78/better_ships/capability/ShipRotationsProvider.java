package grillo78.better_ships.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ShipRotationsProvider implements ICapabilitySerializable<CompoundTag> {
    public static final Capability<ShipRotations> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });
    private final LazyOptional<ShipRotations> shipRotationsLazyOptional;

    public ShipRotationsProvider() {
        this.shipRotationsLazyOptional = LazyOptional.of(() -> new ShipRotations());
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CAPABILITY) {
            return shipRotationsLazyOptional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return shipRotationsLazyOptional.orElse(null).serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        shipRotationsLazyOptional.ifPresent(rotations->rotations.deserializeNBT(nbt));
    }
}
