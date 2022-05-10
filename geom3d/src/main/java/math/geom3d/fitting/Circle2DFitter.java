/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package math.geom3d.fitting;

import java.util.List;
import math.geom2d.Box2D;
import math.geom2d.Point2D;
import math.geom2d.conic.Circle2D;
import math.geom2d.point.PointArray2D;

/**
 *
 * @author peter
 */
public class Circle2DFitter extends Shape2DFitter<Circle2D> {

    public Circle2DFitter() {
        super(3,
                (double[] args) -> new Circle2D(args[0], args[1], args[2], true),
                (Circle2D c) -> new double[]{c.center().getX(), c.center().getY(), c.radius()},
                (List<Point2D> target) -> {
                    Box2D box = new PointArray2D(target).boundingBox();
                    return new Circle2D(center(target), (box.getWidth() + box.getHeight()) / 4);
                }, true);
    }

}
