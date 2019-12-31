package basic.Effects;

import basic.basics.Item;

public abstract class Effect<T1 extends Item, T2 extends Item> {
    protected T1 source;

    protected T2 target;

    public Effect(T1 source, T2 target){
        this.source = source;
        this.target = target;
    }

    public T1 getSource(){
        return source;
    }

    public T2 getTarget(){
        return target;
    }

    public abstract void make();
}
