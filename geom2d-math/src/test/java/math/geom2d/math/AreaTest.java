/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package math.geom2d.math;

import math.geom2d.Point2D;
import math.geom2d.circulinear.CirculinearCurve2D;
import math.geom2d.circulinear.CirculinearCurves2D;
import math.geom2d.polygon.Polygon2D;
import math.geom2d.polygon.SimplePolygon2D;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author peter
 */
public class AreaTest {

    @Test
    public void test1() {
        Point2D[] points1 = new Point2D[]{
            new Point2D(50, 50),
            new Point2D(100, 50),
            new Point2D(100, 100),
            new Point2D(150, 100),
            new Point2D(50, 200)};
        test(new SimplePolygon2D(points1));
    }

    @Test
    public void test2() {
        Point2D[] points2 = new Point2D[]{
            new Point2D(200, 50),
            new Point2D(350, 50),
            new Point2D(350, 250),
            new Point2D(250, 250),
            new Point2D(200, 200)};
        test(new SimplePolygon2D(points2));
    }

    @Test
    public void test3() {
        Point2D[] points3 = new Point2D[]{
            new Point2D(250, 100),
            new Point2D(250, 150),
            new Point2D(300, 150),
            new Point2D(300, 100)};
        test(new SimplePolygon2D(points3));
    }

    public void test(Polygon2D polygon) {
        test(CirculinearCurves2D.convert(polygon), Math.abs(polygon.area()));
    }

    public void test(CirculinearCurve2D curve, double expected) {
        Assert.assertEquals(expected, Rings2D.integrateArea(curve), 1e-2);
    }
}
