/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package math.geom3d.fitting;

import java.util.List;
import java.util.Random;
import math.geom3d.Point3D;
import math.geom3d.plane.Plane3D;
import math.geom3s.Vector3S;

/**
 *
 * @author peter
 */
public class Plane3DFitter extends Shape3DFitter<Plane3D> {

    public Plane3DFitter() {
        super(3, (x) -> Plane3D.fromNormal(new Vector3S(x[0], x[1]).toCartesian(), x[2]),
                (plane) -> {
                    Vector3S normal = Vector3S.fromCartesian(plane.normal());
                    double dist = plane.dist();
                    return new double[]{normal.getTheta(), normal.getPhi(), dist};
                },
                (points) -> guess(points),
                true);
    }

    private static Plane3D guess(List<Point3D> points) {
        if (valuesSame(points, Point3D::getX)) {
            return Plane3D.createYZPlane(average(points, Point3D::getZ));
        }
        if (valuesSame(points, Point3D::getY)) {
            return Plane3D.createXZPlane(average(points, Point3D::getZ));
        }
        if (valuesSame(points, Point3D::getZ)) {
            return Plane3D.createXYPlane(average(points, Point3D::getZ));
        }
        return random(points);
    }

    private static Plane3D random(List<Point3D> points) {
        Random r = new Random();
        return Plane3D.fromNormal(center(points), new Vector3S(r.nextDouble(), r.nextDouble()).toCartesian());
    }

}
