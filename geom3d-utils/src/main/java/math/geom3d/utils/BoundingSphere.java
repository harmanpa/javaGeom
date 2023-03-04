/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package math.geom3d.utils;

import com.dreizak.miniball.highdim.Miniball;
import com.dreizak.miniball.model.PointSet;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.Set;
import math.geom3d.Point3D;
import math.geom3d.PointSet3D;
import math.geom3d.Sphere3D;
import math.geom3d.Vector3D;

/**
 *
 * @author peter
 */
public class BoundingSphere {

    public static Sphere3D boundingSphere(Sphere3D a, Sphere3D b) {
        PointSet3D points = new PointSet3D();
        getExtremes(a, b, points);
        return boundingSphere(points);
    }
    
    public static Sphere3D boundingSphere(Set<Sphere3D> spheres) {
        switch(spheres.size()) {
            case 0:
                return null;
            case 1:
                return spheres.iterator().next();
            case 2:
                Iterator<Sphere3D> sphereit = spheres.iterator();
                return boundingSphere(sphereit.next(), sphereit.next());
            default:
                PointSet3D points = new PointSet3D();
                Sets.combinations(spheres, 2).forEach(pair -> {
                    Iterator<Sphere3D> it = pair.iterator();
                    getExtremes(it.next(), it.next(), points);
                });
                return boundingSphere(points);
        }
    }
    
    static void getExtremes(Sphere3D a, Sphere3D b, PointSet3D points) {
        // Determine vector between spheres
        Vector3D direction = new Vector3D(a.getCenter(), b.getCenter()).normalize();
        // Use vector to get extremes along that line, because the resulting
        // sphere will contain both
        points.addPoint(a.getCenter().minus(direction.times(a.getRadius())));
        points.addPoint(b.getCenter().plus(direction.times(b.getRadius())));
    }
    
    public static Sphere3D boundingSphere(PointSet3D points) {
        switch(points.pointNumber()) {
            case 0:
                return null;
            case 1:
                return new Sphere3D(points.getPoint(0), 0);
            case 2:
                return new Sphere3D(
                    Point3D.midpoint(points.getPoint(0), points.getPoint(1)),
                    points.getPoint(0).distance(points.getPoint(1))/2);
            default:
                Miniball ball = new Miniball(new MiniballPointSet3D(points));
                return new Sphere3D(new Point3D(ball.center()[0], ball.center()[1], ball.center()[2]), ball.radius());
        }
    }

    public static Sphere3D innerSphere(Point3D centre, PointSet3D points) {
        return new Sphere3D(centre, points.distance(centre));
    }

    static class MiniballPointSet3D implements PointSet {

        private final PointSet3D points;

        public MiniballPointSet3D(PointSet3D points) {
            this.points = points;
        }

        @Override
        public int size() {
            return points.pointNumber();
        }

        @Override
        public int dimension() {
            return 3;
        }

        @Override
        public double coord(int i, int j) {
            switch (j) {
                case 0:
                    return points.getPoint(i).getX();
                case 1:
                    return points.getPoint(i).getY();
                case 2:
                    return points.getPoint(i).getZ();
                default:
                    return 0.0;
            }
        }
    }
}
