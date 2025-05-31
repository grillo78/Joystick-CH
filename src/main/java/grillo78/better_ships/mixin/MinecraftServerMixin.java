package grillo78.better_ships.mixin;

import grillo78.better_ships.BetterShips;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

    @ModifyConstant(method = "runServer()V", constant = @Constant(longValue = 50L))
    private long modifyTickTime(long tickTime) {
        return 1000L/BetterShips.DEBUG_TICK;
    }
}
