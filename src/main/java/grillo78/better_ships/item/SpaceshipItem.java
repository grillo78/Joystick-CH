package grillo78.better_ships.item;

import grillo78.better_ships.entity.Jaw;
import grillo78.better_ships.entity.ModEntities;
import grillo78.better_ships.entity.Spaceship;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

public class SpaceshipItem extends Item {
    public SpaceshipItem() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        if (!pContext.getLevel().isClientSide){
            Jaw spaceShip = new Jaw(ModEntities.JAW.get(), pContext.getLevel());
            spaceShip.setPos(pContext.getClickedPos().above().getCenter());
            pContext.getLevel().addFreshEntity(spaceShip);
            System.out.println();
        }
        return InteractionResult.SUCCESS;
    }
}
