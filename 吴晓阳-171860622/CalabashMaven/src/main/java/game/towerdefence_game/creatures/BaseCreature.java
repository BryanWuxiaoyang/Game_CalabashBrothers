package game.towerdefence_game.creatures;

import basic.Creatures.BasicCreature;
import game.towerdefence_game.effects.BaseAttackEffect;
import game.towerdefence_game.traits.Enemy;
import basic.basics.ItemTypeId;
import game.towerdefence_game.traits.Base;
import basic.traits.Interacted;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;

public class BaseCreature extends BasicCreature implements Base {

    public BaseCreature(){
        this(ItemTypeId.CREATURE, new Image("liuwa.jpg", 50, 50, true, true));
    }
    public BaseCreature(ItemTypeId itemTypeId, Image image) {
        super(itemTypeId, image, 100000000);
    }


    @Override
    public void interactWith(Interacted item) {
        if(item instanceof Enemy){
            item.acceptInteract(new BaseAttackEffect(this, ((Enemy) item), 10));
        }
    }

    @Override
    public Rectangle2D getInteractingRange() {
        return getCurrentBounds();
    }
}
