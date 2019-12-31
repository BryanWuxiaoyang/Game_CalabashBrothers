package path_generation;

import javafx.geometry.Point2D;
import javafx.scene.shape.Path;

public class MapPath {
    private final Path path;

    private final Point2D srcPoint;

    private final Point2D dstPoint;

    private final double totalLength;

    public MapPath(Path path, Point2D srcPoint, Point2D dstPoint, double totalLength) {
        this.path = path;
        this.srcPoint = srcPoint;
        this.dstPoint = dstPoint;
        this.totalLength = totalLength;
    }

    public Path getPath() {
        return path;
    }

    public Point2D getSrcPoint() {
        return srcPoint;
    }

    public Point2D getDstPoint() {
        return dstPoint;
    }

    public double getTotalLength() {
        return totalLength;
    }
}
