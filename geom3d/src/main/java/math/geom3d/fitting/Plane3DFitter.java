/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package math.geom3d.fitting;

import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Random;
import java.util.stream.DoubleStream;
import math.geom2d.Tolerance2D;
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
                (points) -> guess(points));
    }

    private static Plane3D guess(List<Point3D> points) {
        if (valuesSame(points.stream().mapToDouble(point -> point.getX()))) {
            return Plane3D.createYZPlane();
        }
        if (valuesSame(points.stream().mapToDouble(point -> point.getY()))) {
            return Plane3D.createXZPlane();
        }
        if (valuesSame(points.stream().mapToDouble(point -> point.getZ()))) {
            return Plane3D.createXYPlane();
        }
        return random();
    }

    private static Plane3D random() {
        Random r = new Random();
        return Plane3D.fromNormal(new Vector3S(r.nextDouble(), r.nextDouble()).toCartesian(), r.nextDouble());
    }

    private static boolean valuesSame(DoubleStream stream) {
        DoubleSummaryStatistics stats = stream.summaryStatistics();
        return stats.getMax() - stats.getMin() <= Tolerance2D.get();
    }

}
