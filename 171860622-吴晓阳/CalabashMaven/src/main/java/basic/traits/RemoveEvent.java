package basic.traits;

import basic.basics.Item;
import game_console.GameConsole;
import javafx.event.Event;

public class RemoveEvent extends Event {
    private GameConsole console;

    public RemoveEvent(Item source, GameConsole console){
        super(source, null, null);
        this.console = console;
    }

    public GameConsole getConsole() {
        return console;
    }
}
