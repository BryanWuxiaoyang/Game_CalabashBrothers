package path_generation;

import factory.Creator;
import utils.Utils;
import javafx.geometry.Point2D;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import maps.grids.Grid;

import java.util.Iterator;
import java.util.ListIterator;

public class MapPathCreator implements Creator<MapPath> {
    private Iterator<Grid> iterator;

    private double offsetX;

    private double offsetY;

    private MapPath resultPath = null;

    private boolean finished = false;

    public MapPathCreator(Iterator<Grid> iterator){
        this(iterator, 0, 0);
    }

    public MapPathCreator(Iterator<Grid> iterator, double offsetX, double offsetY){
        this.iterator = iterator;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    @Override
    public MapPath create() {
        if(finished) return resultPath;
        else{
            Point2D srcPoint, dstPoint = null;
            double length = 0;
            Path path = new Path();

            srcPoint = Utils.getCenter(iterator.next().getNode().getBoundsInParent());
            path.getElements().add(new MoveTo(srcPoint.getX() - offsetX, srcPoint.getY() - offsetY));


            Point2D prevPoint = srcPoint;
            while(iterator.hasNext()){
                Grid grid = iterator.next();
                dstPoint = Utils.getCenter(grid.getNode().getBoundsInParent());

                path.getElements().add(new LineTo(dstPoint.getX() - offsetX, dstPoint.getY() - offsetY));
                length += Utils.calculateDistance(prevPoint.getX(), prevPoint.getY(), dstPoint.getX(), dstPoint.getY());

                prevPoint = dstPoint;
            }

            finished = true;
            resultPath = new MapPath(path, srcPoint, dstPoint, length);
            return resultPath;
        }
    }
}
