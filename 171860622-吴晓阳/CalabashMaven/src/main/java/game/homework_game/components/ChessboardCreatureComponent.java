package game.homework_game.components;

import game.homework_game.console.ChessboardGameConsole;

public class ChessboardCreatureComponent {
    private int x = -1;

    private int y = -1;

    private ChessboardGameConsole chessboard = null;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setChessboard(ChessboardGameConsole chessboard){
        this.chessboard = chessboard;
    }

    public ChessboardGameConsole getChessboard(){
        return chessboard;
    }
}
