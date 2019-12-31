package basic.components;

import basic.traits.Emitter;
import factory.Creator;
import basic.basics.Item;
import basic.traits.Emission;
import basic.traits.EmitEvent;
import game_console.GameConsole;
import javafx.event.EventHandler;
import javafx.util.Duration;
import utils.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.Callable;

public class EmitComponent {
    private Emitter emitter;

    private Creator<? extends Emission> creator;

    private Duration duration;

    private boolean emitRunning = false;

    private boolean emitEnable = false;

    private GameConsole console = null;

    private Callable<Boolean> singleEmitCall = new Callable<Boolean>() {
        public Boolean call() throws Exception {
            if (emitter.isRemoved() || !isEmitEnabled() || creator == null) {
                emitRunning = false;
                return false;
            }

            emit();
            return true;
        }
    };

    private Runnable emitTask = new Runnable() {
        public void run() {
            emitRunning = true;

            while (!emitter.isRemoved() && isEmitEnabled() && creator != null) {
                emit();

                if (duration != null) {
                    try {
                        Thread.sleep((int) duration.toMillis());
                    } catch (InterruptedException ignore) {
                    }
                }
            }
            emitRunning = false;
        }

    };

    public EmitComponent(Emitter emitter) {
        this(emitter, null);
    }

    public EmitComponent(Emitter emitter, Creator<? extends Emission> creator) {
        this(emitter, creator, null);
    }

    public EmitComponent(Emitter emitter, Creator<? extends Emission> creator, Duration duration) {
        this.emitter = emitter;
        this.creator = creator;
        this.duration = duration;
    }

    public GameConsole getConsole() {
        return console;
    }

    public void setConsole(GameConsole console) {
        this.console = console;
    }

    public void emit(){
        if(creator == null) return;
        Emission emission = creator.create();
        if (emission == null) return;
        emission.setConsole(getConsole());
        emission.setEmitter(emitter);

        EmitEvent event = new EmitEvent(emitter, emission);
        if(getConsole() != null) getConsole().addWaitRunning(emission);

        notifyOnEmit(event);
    }

    private EventHandler<EmitEvent> handler = null;

    public void setOnEmit(EventHandler<EmitEvent> handler) {
        this.handler = handler;
    }

    private void notifyOnEmit(EmitEvent event) {
        if (handler != null) handler.handle(event);
    }

    public synchronized void setEmitEnabled(boolean tag) {
        if (tag && !emitEnable && creator != null) {
            emitRunning = true;
            //Utils.getThreadPool().execute(emitTask);
            if (duration == null) Utils.getThreadPool().execute(emitTask);
            else Utils.getAssignmentPool((int) duration.toMillis()).execute(singleEmitCall);
        }
        this.emitEnable = tag;
    }

    public boolean isEmitEnabled() {
        return emitEnable;
    }

    public void setCreator(Creator<? extends Emission> creator){
        this.creator = creator;
    }

    public void setCreator(Creator<? extends Emission> creator, Duration duration){
        this.creator = creator;
        this.duration = duration;
    }
}
