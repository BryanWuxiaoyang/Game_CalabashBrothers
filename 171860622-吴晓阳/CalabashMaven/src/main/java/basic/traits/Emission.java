package basic.traits;

import basic.basics.Item;

public interface Emission extends Item {
    Emitter getEmitter();

    void setEmitter(Emitter emitter);
}
