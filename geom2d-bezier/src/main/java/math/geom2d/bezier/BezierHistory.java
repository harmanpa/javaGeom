package math.geom2d.bezier;

import math.geom2d.Point2D;

public class BezierHistory {

    Point2D startPoint = new Point2D();
    Point2D lastPoint = new Point2D();
    Point2D lastKnot = new Point2D();

    public BezierHistory() {
    }

    public void setStartPoint(double x, double y) {
        startPoint = new Point2D(x, y);
    }

    public void setLastPoint(double x, double y) {
        lastPoint = new Point2D(x, y);
    }

    public void setLastKnot(double x, double y) {
        lastKnot = new Point2D(x, y);
    }
}
