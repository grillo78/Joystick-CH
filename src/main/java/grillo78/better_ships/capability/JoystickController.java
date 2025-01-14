package grillo78.better_ships.capability;

public class JoystickController {
    private double thrust = 0;
    private double inertiaThrust = 0;
    private double yaw = 0;
    private double pitch = 0;
    private double roll = 0;
    private boolean hyperSpeed = false;

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
            this.yaw *= 0.9;
        else
            this.yaw = yaw;
    }

    public double getPitch() {
        return pitch;
    }

    public void setPitch(double pitch) {
        if (pitch == 0)
            this.pitch *= 0.9;
        else
            this.pitch = pitch;
    }

    public double getRoll() {
        return roll;
    }

    public void setRoll(double roll) {
        if (roll == 0)
            this.roll *= 0.9;
        else
            this.roll = roll;
    }

    public void toggleHyperSpeed() {
        hyperSpeed = !hyperSpeed;
    }

    public boolean isHyperSpeed() {
        return hyperSpeed;
    }
}
