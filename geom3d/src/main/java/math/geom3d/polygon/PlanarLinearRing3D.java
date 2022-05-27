/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package math.geom3d.polygon;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import math.geom2d.exceptions.Geom2DException;
import math.geom2d.polygon.LinearRing2D;
import math.geom3d.Point3D;
import math.geom3d.Vector3D;
import math.geom3d.circulinear.CirculinearContinuousCurve3D;
import math.geom3d.circulinear.CirculinearElement3D;
import math.geom3d.circulinear.CirculinearRing3D;
import math.geom3d.curve.ContinuousCurve3D;
import math.geom3d.plane.PlanarShape3D;
import math.geom3d.plane.Plane3D;
import math.geom3d.transform.AffineTransform3D;

/**
 *
 * @author peter
 */
public class PlanarLinearRing3D extends PlanarShape3D<LinearRing2D> implements CirculinearRing3D {

    public static PlanarLinearRing3D withDirection(List<Point3D> points, boolean cw) throws Geom2DException {
        PlanarLinearRing3D ring = new PlanarLinearRing3D(points);
        boolean ringCW = ring.getShape().area() < 0;
        if (cw != ringCW) {
            return new PlanarLinearRing3D(ring.getPlane().flip(), points);
        }
        return ring;
    }

    public PlanarLinearRing3D(List<Point3D> points) throws Geom2DException {
        this(Plane3D.fromPoints(points), points);
    }

    public PlanarLinearRing3D(Plane3D plane, List<Point3D> points) {
        this(plane, new LinearRing2D(points.stream().map(p -> plane.pointPosition(p)).collect(Collectors.toList())));
    }

    public PlanarLinearRing3D(Plane3D plane, LinearRing2D shape) {
        super(plane, shape);
    }

    public List<Point3D> vertices() {
        return getShape().vertices().stream().map(v -> getPlane().point(v)).collect(Collectors.toList());
    }

    @Override
    public Collection<? extends CirculinearElement3D> smoothPieces() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public CirculinearContinuousCurve3D subCurve(double t0, double t1) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public CirculinearContinuousCurve3D reverseCurve() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public double length() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public double length(double pos) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public double position(double distance) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Collection<? extends CirculinearContinuousCurve3D> continuousCurves() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public double getT0() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public double getT1() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Point3D point(double t) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Point3D firstPoint() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Point3D lastPoint() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Collection<Point3D> singularPoints() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public double position(Point3D point) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public double project(Point3D point) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean isClosed() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Vector3D leftTangent(double t) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Vector3D rightTangent(double t) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public double curvature(double t) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public LinearCurve3D asPolyline(int n) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public ContinuousCurve3D transform(AffineTransform3D trans) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
