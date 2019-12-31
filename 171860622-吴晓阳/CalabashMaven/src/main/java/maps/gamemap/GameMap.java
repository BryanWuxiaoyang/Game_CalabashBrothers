package maps.gamemap;

import javafx.scene.Group;
import maps.GameBackground;
import maps.grids.Grid;

import java.util.*;
import java.util.List;

public interface GameMap {
    List<Grid> getEnemySrc();

    List<Grid> getEnemyDst();

    Group getNode();

    double getWidth();

    double getHeight();

    Grid getGrid(double x, double y);

    Collection<Grid> getGrids();

    GameBackground getBackgound();
}
