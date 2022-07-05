/**
 *
 */
package math.geom3d.line;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import math.geom2d.Tolerance2D;

import math.geom3d.Box3D;
import math.geom3d.GeometricObject3D;
import math.geom3d.Point3D;
import math.geom3d.Vector3D;
import math.geom3d.circulinear.CirculinearElement3D;
import math.geom3d.fitting.CurveIntersector;
import math.geom3d.polygon.LinearCurve3D;
import math.geom3d.transform.AffineTransform3D;

/**
 * @author dlegland
 */
public final class LineSegment3D implements CirculinearElement3D, LinearShape3D {

    // ===================================================================
    // class variables
    private final double x1;
    private final double y1;
    private final double z1;
    private final double x2;
    private final double y2;
    private final double z2;

    // ===================================================================
    // constructors
    public LineSegment3D(Point3D p1, Point3D p2) {
        this.x1 = p1.getX();
        this.y1 = p1.getY();
        this.z1 = p1.getZ();
        this.x2 = p2.getX();
        this.y2 = p2.getY();
        this.z2 = p2.getZ();
    }

    // ===================================================================
    // methods specific to StraightLine3D
    @Override
    public StraightLine3D supportingLine() {
        return new StraightLine3D(x1, y1, z1, x2 - x1, y2 - y1, z2 - z1);
    }

    // ===================================================================
    // methods implementing the Curve3D interface

    /*
     * (non-Javadoc)
     * 
     * @see math.geom3d.curve.Curve3D#getContinuousCurves()
     */
    @Override
    public Collection<LineSegment3D> continuousCurves() {
        ArrayList<LineSegment3D> array = new ArrayList<>(1);
        array.add(this);
        return array;
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom3d.curve.Curve3D#getFirstPoint()
     */
    @Override
    public Point3D firstPoint() {
        return new Point3D(x1, y1, z1);
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom3d.curve.Curve3D#getLastPoint()
     */
    @Override
    public Point3D lastPoint() {
        return new Point3D(x2, y2, z2);
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom3d.curve.Curve3D#getPoint(double)
     */
    @Override
    public Point3D point(double t) {
        t = Math.max(Math.min(t, 1), 0);
        return new Point3D(
                x1 + (x2 - x1) * t,
                y1 + (y2 - y1) * t,
                z1 + (z2 - z1) * t);
    }

    /**
     * If point does not project on the line segment, return Double.NaN.
     *
     * @see math.geom3d.curve.Curve3D#position(math.geom3d.Point3D)
     */
    @Override
    public double position(Point3D point) {
        double t = this.supportingLine().position(point);
        if (t > 1) {
            return Double.NaN;
        }
        if (t < 0) {
            return Double.NaN;
        }
        return t;
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom3d.curve.Curve3D#getReverseCurve()
     */
    @Override
    public CirculinearElement3D reverseCurve() {
        return new LineSegment3D(lastPoint(), firstPoint());
    }

    /**
     * Returns the2 end points.
     *
     * @see math.geom3d.curve.Curve3D#singularPoints()
     */
    @Override
    public Collection<Point3D> singularPoints() {
        List<Point3D> points = new ArrayList<>(2);
        points.add(firstPoint());
        points.add(lastPoint());
        return points;
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom3d.curve.Curve3D#getSubCurve(double, double)
     */
    @Override
    public LineSegment3D subCurve(double t0, double t1) {
        t0 = Math.max(t0, 0);
        t1 = Math.min(t1, 1);
        return new LineSegment3D(point(t0), point(t1));
    }

    /**
     * Return 0, by definition of LineSegment.
     *
     * @return
     * @see math.geom3d.curve.Curve3D#getT0()
     */
    @Override
    public double getT0() {
        return 0;
    }

    /**
     * Return 1, by definition of LineSegment.
     *
     * @return
     * @see math.geom3d.curve.Curve3D#getT1()
     */
    @Override
    public double getT1() {
        return 1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom3d.curve.Curve3D#project(math.geom3d.Point3D)
     */
    @Override
    public double project(Point3D point) {
        double t = supportingLine().project(point);
        return Math.min(Math.max(t, 0), 1);
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom3d.curve.Curve3D#transform(math.geom3d.transform.AffineTransform3D)
     */
    @Override
    public LineSegment3D transform(AffineTransform3D trans) {
        return new LineSegment3D(new Point3D(x1, y1, z1).transform(trans),
                new Point3D(x2, y2, z2).transform(trans));
    }

    // ===================================================================
    // methods implementing the Shape3D interface

    /*
     * (non-Javadoc)
     * 
     * @see math.geom3d.Shape3D#clip(math.geom3d.Box3D)
     */
//    @Override
//    public CurveSet3D<? extends CirculinearElement3D> clip(Box3D box) {
//        // TODO Auto-generated method stub
//        return null;
//    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom3d.Shape3D#contains(math.geom3d.Point3D)
     */
    @Override
    public boolean contains(Point3D point) {
        StraightLine3D line = this.supportingLine();
        if (!line.contains(point)) {
            return false;
        }
        double t = line.position(point);
        if (t < -Tolerance2D.get()) {
            return false;
        }
        return t <= 1 + Tolerance2D.get();
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom3d.Shape3D#getBoundingBox()
     */
    @Override
    public Box3D boundingBox() {
        return new Box3D(x1, x2, y1, y2, z1, z2);
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom3d.Shape3D#getDistance(math.geom3d.Point3D)
     */
    @Override
    public double distance(Point3D point) {
        double t = this.project(point);
        return point(t).distance(point);
    }

    /**
     * Returns true, as a LineSegment3D is always bounded.
     *
     * @return
     * @see math.geom3d.Shape3D#isBounded()
     */
    @Override
    public boolean isBounded() {
        return true;
    }

    /**
     * Returns false, as a LineSegment3D is never empty.
     *
     * @return
     * @see math.geom3d.Shape3D#isEmpty()
     */
    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean containsProjection(Point3D p) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Collection<? extends CirculinearElement3D> smoothPieces() {
        return Arrays.asList(this);
    }

    @Override
    public double length() {
        return lastPoint().distance(firstPoint());
    }

    @Override
    public double length(double pos) {
        return point(pos).distance(firstPoint());
    }

    @Override
    public double position(double distance) {
        return position(firstPoint().plus(tangent(0).times(distance)));
    }

    @Override
    public Vector3D tangent(double t) {
        return new Vector3D(firstPoint(), lastPoint()).normalize();
    }

    @Override
    public Point3D origin() {
        return firstPoint();
    }

    @Override
    public Vector3D direction() {
        return new Vector3D(firstPoint(), lastPoint()).normalize();
    }

    @Override
    public Point3D intersection(LinearShape3D line) {
        return CurveIntersector.intersect(this, line);
    }

    @Override
    public boolean isClosed() {
        return false;
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
        return 0;
    }

    @Override
    public LinearCurve3D asPolyline(int n) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public boolean isParallel(LinearShape3D line) {
        return line.direction().normalize().angle(direction().normalize()) < Tolerance2D.get();
    }

    @Override
    public Collection<Point3D> intersections(LinearShape3D line) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public int hashCode() {
        return GeometricObject3D.hash(7, 93, x1, y1, z1, x2, y2, z2);
    }

    @Override
    public boolean almostEquals(GeometricObject3D obj, double eps) {
        return GeometricObject3D.almostEquals(this, obj, eps);
    }

    @Override
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public boolean equals(Object obj) {
        return GeometricObject3D.equals(this, obj);
    }

}
