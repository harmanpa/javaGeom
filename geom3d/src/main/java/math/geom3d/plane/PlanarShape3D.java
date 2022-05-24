/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package math.geom3d.plane;

import math.geom2d.Box2D;
import math.geom2d.Shape2D;
import math.geom3d.Box3D;
import math.geom3d.Point3D;
import math.geom3d.Shape3D;
import math.geom3d.transform.AffineTransform3D;

/**
 *
 * @author peter
 * @param <T>
 */
public class PlanarShape3D<T extends Shape2D> implements Shape3D {

    private final Plane3D plane;
    private final T shape;

    public PlanarShape3D(Plane3D plane, T shape) {
        this.plane = plane;
        this.shape = shape;
    }

    public Plane3D getPlane() {
        return plane;
    }

    public T getShape() {
        return shape;
    }

    @Override
    public boolean isEmpty() {
        return shape.isEmpty();
    }

    @Override
    public boolean isBounded() {
        return shape.isBounded();
    }

    @Override
    public Box3D boundingBox() {
        Box2D bb2 = shape.boundingBox();
        return Box3D.fromPoints(
                plane.point(bb2.getMinX(), bb2.getMinY()),
                plane.point(bb2.getMinX(), bb2.getMaxY()),
                plane.point(bb2.getMaxX(), bb2.getMinY()),
                plane.point(bb2.getMaxX(), bb2.getMaxY())
        );
    }

    @Override
    public Shape3D clip(Box3D box) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Shape3D transform(AffineTransform3D trans) {
        return new PlanarShape3D((Plane3D) plane.transform(trans), shape);
    }

    @Override
    public double distance(Point3D p) {
        Point3D pointOnPlane = plane.projectPoint(p);
        double planarDistance = shape.distance(plane.pointPosition(pointOnPlane));
        return Math.hypot(planarDistance, pointOnPlane.distance(p));
    }

    @Override
    public boolean contains(Point3D point) {
        return shape.contains(plane.pointPosition(point));
    }

}
