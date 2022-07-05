/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package math.geom3d;

import java.util.Objects;
import math.geom2d.Tolerance2D;
import math.geom3d.transform.AffineTransform3D;

/**
 *
 * @author peter
 */
public class Sphere3D implements Shape3D {

    private final Point3D center;
    private final double radius;

    public Sphere3D(Point3D center, double radius) {
        this.center = center;
        this.radius = radius;
    }

    @Override
    public boolean isEmpty() {
        return radius <= Tolerance2D.get();
    }

    @Override
    public boolean isBounded() {
        return radius < Double.POSITIVE_INFINITY;
    }

    @Override
    public Box3D boundingBox() {
        return new Box3D(center.getX() - radius, center.getX() + radius,
                center.getY() - radius, center.getY() + radius,
                center.getZ() - radius, center.getZ() + radius);
    }

//    @Override
//    public Shape3D clip(Box3D box) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
    @Override
    public Shape3D transform(AffineTransform3D trans) {
        Point3D newCenter = trans.transformPoint(center);
        double newRadius = trans.transformPoint(center.plus(new Vector3D(radius, 0, 0))).distance(newCenter);
        return new Sphere3D(newCenter, newRadius);
    }

    @Override
    public double distance(Point3D p) {
        return center.distance(p) - radius;
    }

    @Override
    public boolean contains(Point3D point) {
        return distance(point) < 0;
    }

    @Override
    public boolean almostEquals(GeometricObject3D obj, double eps) {
        return obj instanceof Sphere3D && ((Sphere3D) obj).center.almostEquals(center, eps) && Math.abs(((Sphere3D) obj).radius - radius) <= eps;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + Objects.hashCode(this.center);
        hash = 29 * hash + Tolerance2D.hash(radius);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Sphere3D other = (Sphere3D) obj;
        if (Tolerance2D.compare(radius, other.radius) != 0) {
            return false;
        }
        return Objects.equals(this.center, other.center);
    }

}
