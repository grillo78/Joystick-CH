package grillo78.joystick_ch.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;
import org.joml.Quaternionf;

public class ShipRotations implements INBTSerializable<CompoundTag> {
    private Quaternionf rotations = new Quaternionf();

    public Quaternionf getRotations() {
        return rotations;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("x",rotations.x);
        tag.putFloat("y",rotations.y);
        tag.putFloat("z",rotations.z);
        tag.putFloat("w",rotations.w);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        rotations.x = nbt.getFloat("x");
        rotations.y = nbt.getFloat("y");
        rotations.z = nbt.getFloat("z");
        rotations.w = nbt.getFloat("w");
    }
}
