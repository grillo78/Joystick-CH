package grillo78.better_ships.capability;

import com.mojang.blaze3d.vertex.PoseStack;
import grillo78.better_ships.entity.Spaceship;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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
        this.rotationsO = new Quaternionf(this.rotations);
        this.rotations = rotations;
    }

    public void setRotationsO(Quaternionf rotationsO) {
        this.rotationsO = rotationsO;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("x",rotations.x);
        tag.putFloat("y",rotations.y);
        tag.putFloat("z",rotations.z);
        tag.putFloat("w",rotations.w);
        tag.putFloat("xO",rotationsO.x);
        tag.putFloat("yO",rotationsO.y);
        tag.putFloat("zO",rotationsO.z);
        tag.putFloat("wO",rotationsO.w);
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

    @OnlyIn(Dist.CLIENT)
    public void rotatePlayer(PoseStack poseStack, Entity ship) {
        if (ship instanceof Spaceship){
            ((Spaceship)ship).rotatePlayer(poseStack, this);
        } else {
            poseStack.translate(0, 2 * ship.getPassengersRidingOffset(), 0);
            poseStack.mulPose(new Quaternionf(rotations));
            poseStack.translate(0, 2 * -ship.getPassengersRidingOffset(), 0);
        }
    }
}
