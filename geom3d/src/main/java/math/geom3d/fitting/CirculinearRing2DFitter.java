/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package math.geom3d.fitting;

import java.util.List;
import java.util.stream.Collectors;
import math.geom2d.Point2D;
import math.geom2d.circulinear.CirculinearRing2D;
import math.geom2d.conic.Circle2D;
import math.geom2d.exceptions.ColinearPoints2DException;

/**
 *
 * @author peter
 */
public class CirculinearRing2DFitter {

    public void fit(List<Point2D> points, double maxError) {
        List<Circle2D> arcCircles = FittingUtils.sequentials(Point2D.class, points, 3, true)
                .map(triplet -> fitCircle(triplet))
                .collect(Collectors.toList());
    }

    private Circle2D fitCircle(Point2D[] points) {
        try {
            return Circle2D.circumCircle(points[0], points[1], points[2]);
        } catch (ColinearPoints2DException ex) {
            return null;
        }
    }
}
