/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package math.geom2d.math;

import math.geom2d.conic.CircleArc2D;
import org.junit.Test;

/**
 *
 * @author peter
 */
public class PolygonizerTest {

    @Test
    public void test() {
        CircleArc2D arc = new CircleArc2D(0, 0, 1, 0, Math.PI / 2);
        System.out.println("0.5, inside");
        Polygonizer.toPolyline(arc, 0.5, true).vertices().forEach(point -> System.out.println(point));
        System.out.println("0.5, outside");
        Polygonizer.toPolyline(arc, 0.5, false).vertices().forEach(point -> System.out.println(point));
        System.out.println("0.05, inside");
        Polygonizer.toPolyline(arc, 0.05, true).vertices().forEach(point -> System.out.println(point));
        System.out.println("0.05, outside");
        Polygonizer.toPolyline(arc, 0.05, false).vertices().forEach(point -> System.out.println(point));
        System.out.println("0.005, inside");
        Polygonizer.toPolyline(arc, 0.005, true).vertices().forEach(point -> System.out.println(point));
        System.out.println("0.005, outside");
        Polygonizer.toPolyline(arc, 0.005, false).vertices().forEach(point -> System.out.println(point));
        System.out.println("0.0005, inside");
        Polygonizer.toPolyline(arc, 0.0005, true).vertices().forEach(point -> System.out.println(point));
        System.out.println("0.0005, outside");
        Polygonizer.toPolyline(arc, 0.0005, false).vertices().forEach(point -> System.out.println(point));
    }

    @Test
    public void curvatureTest() {
        CircleArc2D arc = new CircleArc2D(0, 0, 1, 0, Math.PI / 2);
        CircleArc2D arc2 = new CircleArc2D(0, 0, 1, 0, -Math.PI / 2);
        System.out.println(arc.curvature(arc.t0() + (arc.t1() - arc.t0()) / 2));
        System.out.println(arc2.curvature(arc2.t0() + (arc2.t1() - arc2.t0()) / 2));
    }
}
