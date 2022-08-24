/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package math.geom3d.plane;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import math.geom2d.Point2D;
import math.geom2d.exceptions.Geom2DException;
import math.geom3d.Point3D;
import math.geom3d.line.StraightLine3D;
import math.geom3d.transform.AffineTransform3D;
import math.geom3s.Vector3S;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 *
 * @author peter
 */
@RunWith(Parameterized.class)
public class PlaneTransformTest {

    private final Plane3D a;
    private final Plane3D b;

    public PlaneTransformTest(Plane3D a, Plane3D b) {
        this.a = a;
        this.b = b;
    }

    @Parameterized.Parameters
    public static List<Object[]> parameters() {
        int n = 1;
        Random r = new Random();
        List<Object[]> out = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            out.add(new Object[]{
                Plane3D.fromNormal(new Vector3S(r.nextDouble(), r.nextDouble()).toCartesian(), r.nextDouble()),
                Plane3D.fromNormal(new Vector3S(r.nextDouble(), r.nextDouble()).toCartesian(), r.nextDouble())
            });
        }
        return out;
    }

    @Test
    public void test() throws Geom2DException {
        Random r = new Random();
        AffineTransform3D at3 = a.transform3D(Plane3D.createXYPlane());
        Point2D p2 = new Point2D(r.nextDouble(), r.nextDouble());
        System.out.println(p2);
        System.out.println(a.point(p2));
        System.out.println(a.point(p2).transform(at3));
        System.out.println(a.point(p2).distance(new Point3D()));
        System.out.println(a.point(p2).transform(at3).distance(new Point3D()));
        System.out.println(Plane3D.createXYPlane().pointPosition(a.point(p2).transform(at3)));
    }
}
