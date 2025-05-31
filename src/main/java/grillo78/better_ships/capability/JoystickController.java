package grillo78.better_ships.capability;

import com.google.common.util.concurrent.AtomicDouble;
import net.lointain.cosmos.block.SteelLandingPadOFFBlock;
import net.lointain.cosmos.block.SteelLandingPadONBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class JoystickController {
    private double thrust = 0;
    private double inertiaThrust = 0;
    private double yaw = 0;
    private double pitch = 0;
    private double roll = 0;
    private boolean hyperSpeed = false;
    private boolean autolanding = false;
    private BlockPos autolandingPos = null;
    private int autolandingTicks = 0;
    private Entity player;

    public JoystickController(Entity player) {
        this.player = player;
    }

    public double getThrust() {
        return thrust;
    }

    public void setThrust(double thrust) {
        if (thrust == 0)
            this.inertiaThrust *= 0.9;
        else
            this.inertiaThrust = thrust;
        this.thrust = thrust;
    }

    public double getInertiaThrust() {
        return inertiaThrust;
    }

    public double getYaw() {
        return yaw;
    }

    public void setYaw(double yaw) {
        if (yaw == 0)
            this.yaw *= 0.7;
        else
            this.yaw = yaw;
    }

    public double getPitch() {
        return pitch;
    }

    public void setPitch(double pitch) {
        if (pitch == 0)
            this.pitch *= 0.7;
        else
            this.pitch = pitch;
    }

    public double getRoll() {
        return roll;
    }

    public void setRoll(double roll) {
        if (roll == 0)
            this.roll *= 0.7;
        else
            this.roll = roll;
    }

    public void toggleHyperSpeed() {
        hyperSpeed = !hyperSpeed;
    }

    public void setHyperSpeed(boolean hyperSpeed) {
        this.hyperSpeed = hyperSpeed;
    }

    public boolean isHyperSpeed() {
        return hyperSpeed;
    }

    public void toggleAutoland() {
//        autolanding = !autolanding;
//        if (autolanding) {
//            AtomicDouble distance = new AtomicDouble(500);
//            BlockPos.betweenClosed(player.blockPosition().north(10).east(10).below(10), player.blockPosition().south(10).west(10).above(10)).forEach(blockPos -> {
//                if ((player.level().getBlockState(blockPos).getBlock() instanceof SteelLandingPadOFFBlock || player.level().getBlockState(blockPos).getBlock() instanceof SteelLandingPadONBlock) && player.position().distanceTo(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5)) < distance.get()) {
//                    autolandingPos = blockPos;
//                    System.out.println(player.level().getBlockState(autolandingPos));
//                    System.out.println(autolandingPos);
//                    distance.set(player.position().distanceTo(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5)));
//                }
//            });
//            if (autolandingPos == null)
//                autolanding = false;
//        }
    }

    public boolean isAutolanding() {
        return autolanding;
    }

    public BlockPos getAutolandingPos() {
        return autolandingPos;
    }

    public int getAutolandingTicks() {
        return autolandingTicks;
    }

    public void tick() {
        if (autolandingPos != null) {
            System.out.println(player.level().getBlockState(autolandingPos));
            if (player.level().getBlockState(autolandingPos).getBlock() instanceof SteelLandingPadOFFBlock || player.level().getBlockState(autolandingPos).getBlock() instanceof SteelLandingPadONBlock) {
            } else {
                autolandingPos = null;
                autolanding = false;
            }
        }
    }
}
