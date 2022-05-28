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
import math.geom3d.fitting.Plane3DFitter;
import math.geom3d.line.StraightLine3D;
import math.geom3s.Vector3S;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 *
 * @author peter
 */
@RunWith(Parameterized.class)
public class PlaneIntersectionTest {

    private final Plane3D a;
    private final Plane3D b;

    public PlaneIntersectionTest(Plane3D a, Plane3D b) {
        this.a = a;
        this.b = b;
    }

    @Parameterized.Parameters
    public static List<Object[]> parameters() {
        int n = 100;
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
    public void test() {
        StraightLine3D line = a.intersection(b);
        if (line == null) {
            System.out.println("Parallel, no intersection");
        } else {
            System.out.println(line.origin());
            System.out.println(line.direction());
        }
    }
}
