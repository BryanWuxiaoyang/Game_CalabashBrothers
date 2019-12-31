package basic.Effects;

import basic.basics.Item;
import basic.traits.HasLife;

public class DamageEffect extends Effect<Item, HasLife>{
    private double damage;

    public DamageEffect(Item source, HasLife target, double damage){
        super(source, target);
        this.damage = damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public double getDamage() {
        return damage;
    }

    @Override
    public void make() {
        target.decreaseLife(damage);
    }
}
