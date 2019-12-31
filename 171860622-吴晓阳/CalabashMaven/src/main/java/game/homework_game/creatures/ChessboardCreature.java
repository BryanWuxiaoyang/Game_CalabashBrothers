package game.homework_game.creatures;

import basic.basics.Item;

public interface ChessboardCreature extends Item {
    int getX();

    void setX(int x);

    int getY();

    void setY(int y);
}
