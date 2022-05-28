/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package math.geom3d.circulinear;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import math.geom2d.AffineTransform2D;
import math.geom2d.Point2D;
import math.geom2d.circulinear.RoundedRectangle2D;
import math.geom2d.conic.Circle2D;
import math.geom3d.fitting.CirculinearRing2DFitter;
import org.junit.Test;

/**
 *
 * @author peter
 */
public class CirculinearFitTest {

//    @Test
//    public void testCircle() {
//        Random r = new Random();
//        Circle2D circle = new Circle2D(r.nextDouble(), r.nextDouble(), r.nextDouble());
//        test(new ArrayList<>(circle.asPolyline(100).vertices()));
//    }

    @Test
    public void testRoundedRectangle() {
        Random r = new Random();
        RoundedRectangle2D rr = new RoundedRectangle2D(r.nextDouble(), r.nextDouble(), r.nextDouble());
        test(new ArrayList<>(rr.transform(AffineTransform2D.createRotation(r.nextDouble()).chain(AffineTransform2D.createTranslation(r.nextDouble(), r.nextDouble()))).asPolyline(100).vertices()));
    }

    public void test(List<Point2D> points) {
        new CirculinearRing2DFitter().fit(points, 0.1);
    }
}
