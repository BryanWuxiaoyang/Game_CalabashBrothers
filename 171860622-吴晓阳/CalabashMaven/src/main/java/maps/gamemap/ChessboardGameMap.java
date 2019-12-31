package maps.gamemap;

import maps.grids.ChessboardGrid;
import maps.grids.Grid;

public interface ChessboardGameMap extends GameMap{
    int getRowSize();

    int getColSize();

    double getGridWidth();

    double getGridHeight();

    ChessboardGrid getGridByGridPos(int x, int y);
}
