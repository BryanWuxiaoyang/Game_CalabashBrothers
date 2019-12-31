package basic.traits;

import javafx.event.Event;
import javafx.event.EventType;

public class EmitEvent extends Event {
    private Emitter emitter;

    private Emission emission;

    public EmitEvent(Emitter emitter, Emission emission){
        super(emitter, null, EventType.ROOT);
        this.emitter = emitter;
        this.emission = emission;
    }

    public Emitter getEmitter() {
        return emitter;
    }

    public Emission getEmission() {
        return emission;
    }
}
