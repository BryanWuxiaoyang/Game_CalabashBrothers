package game.homework_game.creatures;

import basic.Creatures.BasicCreature;
import basic.basics.ItemTypeId;
import game.homework_game.components.ChessboardCreatureComponent;
import game.homework_game.console.ChessboardGameConsole;
import javafx.scene.image.Image;

public class DeadCreature extends BasicCreature implements ChessboardCreature {
    ChessboardCreatureComponent component = new ChessboardCreatureComponent();

    public DeadCreature(Image image, int x, int y){
        super(ItemTypeId.CREATURE, image);
        setX(x);
        setY(y);
        decreaseLife(getLife());
    }

    @Override
    public int getX() {
        return component.getX();
    }

    @Override
    public void setX(int x) {
        component.setX(x);
    }

    @Override
    public int getY() {
        return component.getY();
    }

    @Override
    public void setY(int y) {
        component.setY(y);
    }

    public void setChessboard(ChessboardGameConsole chessboard) {
        component.setChessboard(chessboard);
    }

    public ChessboardGameConsole getChessboard() {
        return component.getChessboard();
    }
}
