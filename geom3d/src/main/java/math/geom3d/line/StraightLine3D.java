/**
 *
 */
package math.geom3d.line;

import java.util.ArrayList;
import java.util.Collection;
import math.geom2d.Tolerance2D;

import math.geom3d.Box3D;
import math.geom3d.GeometricObject3D;
import math.geom3d.Point3D;
import math.geom3d.Vector3D;
import math.geom3d.circulinear.CirculinearContinuousCurve3D;
import math.geom3d.circulinear.CirculinearElement3D;
import math.geom3d.fitting.CurveIntersector;
import math.geom3d.polygon.LinearCurve3D;
import math.geom3d.transform.AffineTransform3D;
import org.apache.commons.math3.util.FastMath;

/**
 * @author dlegland
 */
public class StraightLine3D implements LinearShape3D, CirculinearContinuousCurve3D {

    // ===================================================================
    // Class variables
    private final double x0;
    private final double y0;
    private final double z0;
    private final double dx;
    private final double dy;
    private final double dz;

    // ===================================================================
    // Constructors
    public StraightLine3D() {
        this.x0 = 0;
        this.y0 = 0;
        this.z0 = 0;
        this.dx = 0;
        this.dy = 0;
        this.dz = 0;
    }

    public StraightLine3D(Point3D origin, Vector3D direction) {
        this.x0 = origin.getX();
        this.y0 = origin.getY();
        this.z0 = origin.getZ();
        this.dx = direction.getX();
        this.dy = direction.getY();
        this.dz = direction.getZ();
    }

    /**
     * Constructs a line passing through the 2 points.
     *
     * @param p1 the first point
     * @param p2 the second point
     */
    public StraightLine3D(Point3D p1, Point3D p2) {
        this(p1, new Vector3D(p1, p2));
    }

    public StraightLine3D(double x0, double y0, double z0, double dx,
            double dy, double dz) {
        this.x0 = x0;
        this.y0 = y0;
        this.z0 = z0;
        this.dx = dx;
        this.dy = dy;
        this.dz = dz;
    }

    // ===================================================================
    // methods specific to StraightLine3D
    @Override
    public Point3D origin() {
        return new Point3D(x0, y0, z0);
    }

    @Override
    public Vector3D direction() {
        return new Vector3D(dx, dy, dz);
    }

//    /**
//     * not yet implemented
//     */
//    public StraightLine2D project(Plane3D plane) {
//    	// TODO Auto-generated method stub
//        return null;
//    }
    // ===================================================================
    // methods implementing the Shape3D interface

    /*
     * (non-Javadoc)
     * 
     * @see math.geom3d.Shape3D#clip(math.geom3d.Box3D)
     */
//    @Override
//    public CurveSet3D<? extends ContinuousCurve3D> clip(Box3D box) {
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
        return this.distance(point) < Tolerance2D.get();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean isBounded() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom3d.Shape3D#getBoundingBox()
     */
    @Override
    public Box3D boundingBox() {
        Vector3D v = this.direction();

        // line parallel to (Ox) axis
        if (FastMath.hypot(v.getY(), v.getZ()) < Tolerance2D.get()) {
            return new Box3D(x0, x0, Double.NEGATIVE_INFINITY,
                    Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY,
                    Double.POSITIVE_INFINITY);
        }

        // line parallel to (Oy) axis
        if (FastMath.hypot(v.getX(), v.getZ()) < Tolerance2D.get()) {
            return new Box3D(Double.NEGATIVE_INFINITY,
                    Double.POSITIVE_INFINITY, y0, y0, Double.NEGATIVE_INFINITY,
                    Double.POSITIVE_INFINITY);
        }

        // line parallel to (Oz) axis
        if (FastMath.hypot(v.getX(), v.getY()) < Tolerance2D.get()) {
            return new Box3D(Double.NEGATIVE_INFINITY,
                    Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY,
                    Double.POSITIVE_INFINITY, z0, z0);
        }

        return new Box3D(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,
                Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,
                Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom3d.Shape3D#getDistance(math.geom3d.Point3D)
     */
    @Override
    public double distance(Point3D p) {
        Vector3D vl = this.direction();
        Vector3D vp = new Vector3D(this.origin(), p);
        return Vector3D.crossProduct(vl, vp).norm() / vl.norm();
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom3d.Shape3D#transform(math.geom3d.AffineTransform3D)
     */
    @Override
    public StraightLine3D transform(AffineTransform3D trans) {
        return new StraightLine3D(
                origin().transform(trans),
                direction().transform(trans));
    }

    @Override
    public Point3D firstPoint() {
        return new Point3D(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY,
                Double.POSITIVE_INFINITY);
    }

    @Override
    public Point3D lastPoint() {
        return new Point3D(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY,
                Double.POSITIVE_INFINITY);
    }

    @Override
    public Point3D point(double t) {
        return new Point3D(x0 + t * dx, y0 + t * dy, z0 + t * dz);
    }

    @Override
    public double position(Point3D point) {
        return project(point);
    }

    @Override
    public StraightLine3D reverseCurve() {
        return new StraightLine3D(origin(), direction().opposite());
    }

    /**
     * Returns an empty array of Point3D.
     */
    @Override
    public Collection<Point3D> singularPoints() {
        return new ArrayList<>(0);
    }

    @Override
    public StraightLine3D subCurve(double t0, double t1) {
        return this;
    }

    /**
     * Returns -INFINITY;
     *
     * @return
     */
    @Override
    public double getT0() {
        return Double.NEGATIVE_INFINITY;
    }

    /**
     * Returns +INFINITY;
     *
     * @return
     */
    @Override
    public double getT1() {
        return Double.POSITIVE_INFINITY;
    }

    /**
     * Compute the position of the orthogonal projection of the given point on
     * this line.
     */
    @Override
    public double project(Point3D point) {
        Vector3D vl = this.direction();
        Vector3D vp = new Vector3D(this.origin(), point);
        return Vector3D.dotProduct(vl, vp) / vl.normSq();
    }

    @Override
    public Collection<StraightLine3D> continuousCurves() {
        ArrayList<StraightLine3D> array = new ArrayList<>(1);
        array.add(this);
        return array;
    }

    @Override
    public StraightLine3D supportingLine() {
        return this;
    }

    @Override
    public Point3D intersection(LinearShape3D line) {
        return CurveIntersector.intersect(this, line);
    }

    @Override
    public boolean containsProjection(Point3D point) {
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
    public Collection<? extends CirculinearElement3D> smoothPieces() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public LinearCurve3D asPolyline(int n) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Collection<Point3D> intersections(LinearShape3D line) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public int hashCode() {
        return GeometricObject3D.hash(7, 97, x0, y0, z0, dx, dy, dz);
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
