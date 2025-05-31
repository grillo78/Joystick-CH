package grillo78.better_ships.item;

import grillo78.better_ships.BetterShips;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, BetterShips.MOD_ID);

    public static final RegistryObject<SpaceshipItem> PROTOTYPE = register("prototype", ()->new SpaceshipItem());

    public static <T extends Item, V extends Supplier<T>> RegistryObject<T> register(String name, V itemSupplier) {
        return ITEMS.register(name, itemSupplier);
    }
}
