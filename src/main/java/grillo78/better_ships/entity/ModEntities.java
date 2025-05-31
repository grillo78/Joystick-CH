package grillo78.better_ships.entity;

import grillo78.better_ships.BetterShips;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, BetterShips.MOD_ID);

    public static final RegistryObject<EntityType<Jaw>> JAW = register("jaw", () -> EntityType.Builder
            .of(Jaw::new, MobCategory.MISC).sized(6, 3)
            .build(BetterShips.MOD_ID + ":jaw"));


    private static <T extends EntityType<?>> RegistryObject<T> register(String name, Supplier<T> containerType) {
        return ENTITIES.register(name, containerType);
    }
}
