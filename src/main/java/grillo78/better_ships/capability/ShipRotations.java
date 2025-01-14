package grillo78.better_ships.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;
import org.joml.Quaternionf;

public class ShipRotations implements INBTSerializable<CompoundTag> {
    private Quaternionf rotations = new Quaternionf();
    private Quaternionf rotationsO = new Quaternionf();

    public Quaternionf getRotationsO() {
        return rotationsO;
    }

    public Quaternionf getRotations() {
        return rotations;
    }

    public void setRotations(Quaternionf rotations) {
        this.rotationsO = this.rotations;
        this.rotations = rotations;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("x",rotations.x);
        tag.putFloat("y",rotations.y);
        tag.putFloat("z",rotations.z);
        tag.putFloat("w",rotations.w);
        tag.putFloat("xO",rotations.x);
        tag.putFloat("yO",rotations.y);
        tag.putFloat("zO",rotations.z);
        tag.putFloat("wO",rotations.w);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        rotations.x = nbt.getFloat("x");
        rotations.y = nbt.getFloat("y");
        rotations.z = nbt.getFloat("z");
        rotations.w = nbt.getFloat("w");
        rotationsO.x = nbt.getFloat("xO");
        rotationsO.y = nbt.getFloat("yO");
        rotationsO.z = nbt.getFloat("zO");
        rotationsO.w = nbt.getFloat("wO");
    }
}
