package grillo78.better_ships.capability;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class JoystickControllerProvider implements ICapabilityProvider {
    public static final Capability<JoystickController> CONTROLLER = CapabilityManager.get(new CapabilityToken<>() {
    });
    private final LazyOptional<JoystickController> PlayerDataOptional;

    public JoystickControllerProvider() {
        this.PlayerDataOptional = LazyOptional.of(() -> new JoystickController());
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CONTROLLER) {
            return PlayerDataOptional.cast();
        }
        return LazyOptional.empty();
    }
}
