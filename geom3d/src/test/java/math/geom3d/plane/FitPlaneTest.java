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
import math.geom3d.Vector3D;
import math.geom3d.fitting.Plane3DFitter;
import math.geom3s.Vector3S;
import org.junit.Test;

/**
 *
 * @author peter
 */
public class FitPlaneTest {

    @Test
    public void testPlane() throws Geom2DException {
        Random r = new Random();

        // Construct a plane to generate accurate fitting data
        Plane3D examplePlane = Plane3D.fromNormal(new Vector3S(r.nextDouble(), r.nextDouble()).toCartesian(), r.nextDouble());
        // Generate some random 2D points and turn into 3D
        int n = 80;
        List<Point3D> observedPoints = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            observedPoints.add(examplePlane.point(new Point2D(r.nextDouble(), r.nextDouble())));
        }

        Plane3DFitter fitter = new Plane3DFitter();
        Plane3D plane = fitter.fit(observedPoints);
        System.out.println(examplePlane.normal());
        System.out.println(examplePlane.dist());
        System.out.println(plane.normal());
        System.out.println(plane.dist());
        System.out.println(plane.normal().angle(examplePlane.normal()));
    }

    @Test
    public void test2() throws Geom2DException {
        List<Point3D> observedPoints = new ArrayList<>();
        observedPoints.add(new Point3D(-13663.5408714, -65691.9945102, 1372.0));
        observedPoints.add(new Point3D(-13663.5408714, -65801.9945102, 1372.0));
        observedPoints.add(new Point3D(-13626.5408714, -65801.9945102, 1372.0));
        observedPoints.add(new Point3D(-13626.5408714, -65691.9945102, 1372.0));
        Plane3DFitter fitter = new Plane3DFitter();
        Plane3D plane = fitter.fit(observedPoints);
        Vector3D normal = plane.normal();
        double d = plane.dist();
        System.out.println(normal);
        System.out.println(normal.norm());
        System.out.println(d);
    }

    @Test
    public void test3() throws Geom2DException {
        List<Point3D> observedPoints = new ArrayList<>();
        observedPoints.add(new Point3D(-1.36065408735604, -6.58019945102, 0.13150000021603992));
        observedPoints.add(new Point3D(-1.36635408714, -6.58019945102, 0.13720));
        observedPoints.add(new Point3D(-1.36635408714, -6.56919945102, 0.13720));
        observedPoints.add(new Point3D(-1.36065408735604, -6.56919945102, 0.13150000021603992));
        Plane3DFitter fitter = new Plane3DFitter();
        Plane3D plane = fitter.fit(observedPoints);
        Vector3D normal = plane.normal();
        double d = plane.dist();
        System.out.println(normal);
        System.out.println(normal.norm());
        System.out.println(d);
    }

    @Test
    public void test4() throws Geom2DException {
        List<Point3D> observedPoints = new ArrayList<>();
        observedPoints.add(new Point3D(-13606.5408735604, -65801.9945102, 1315.0000021603992));
        observedPoints.add(new Point3D(-13663.5408714, -65801.9945102, 1372.0));
        observedPoints.add(new Point3D(-13663.5408714, -65691.9945102, 1372.0));
        observedPoints.add(new Point3D(-13606.5408735604, -65691.9945102, 1315.0000021603992));
        Plane3DFitter fitter = new Plane3DFitter();
        Plane3D plane = fitter.fit(observedPoints);
        Vector3D normal = plane.normal();
        double d = plane.dist();
        System.out.println(normal);
        System.out.println(normal.norm());
        System.out.println(d);
    }

}
