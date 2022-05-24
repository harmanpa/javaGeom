/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package math.geom3d.polygon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import math.geom2d.Tolerance2D;
import math.geom3d.Point3D;
import math.geom3d.circulinear.CirculinearRing3D;
import math.geom3d.line.LineSegment3D;
import math.geom3d.transform.AffineTransform3D;

/**
 *
 * @author peter
 */
public class NonPlanarLinearRing3D extends LinearCurve3D implements CirculinearRing3D {

    // ===================================================================
    // Static methods
    /**
     * Static factory for creating a new LinearRing3D from a collection of
     * points.
     *
     * @since 0.8.1
     */
    public static NonPlanarLinearRing3D create(Collection<? extends Point3D> points) {
        return new NonPlanarLinearRing3D(points);
    }

    /**
     * Static factory for creating a new LinearRing3D from an array of points.
     *
     * @since 0.8.1
     */
    public static NonPlanarLinearRing3D create(Point3D... vertices) {
        return new NonPlanarLinearRing3D(vertices);
    }

    // ===================================================================
    // Constructors
    public NonPlanarLinearRing3D() {
        super();
    }

    public NonPlanarLinearRing3D(int n) {
        super(n);
    }

    public NonPlanarLinearRing3D(Point3D... vertices) {
        super(vertices);
    }

    public NonPlanarLinearRing3D(double[] xcoords, double[] ycoords, double[] zcoords) {
        super(xcoords, ycoords, zcoords);
    }

    public NonPlanarLinearRing3D(Collection<? extends Point3D> points) {
        super(points);
    }

    public NonPlanarLinearRing3D(LinearCurve3D lineString) {
        super(lineString.vertices);
    }

    // ===================================================================
    // Methods specific to LinearCurve3D
    /**
     * Returns a simplified version of this linear ring, by using
     * Douglas-Peucker algorithm.
     */
    @Override
    public NonPlanarLinearRing3D simplify(double distMax) {
        // TODO
        return this;
//        return new NonPlanarLinearRing3D(Polylines3D.simplifyClosedPolyline(this.vertices, distMax));
    }

    /**
     * Returns an array of LineSegment3D. The number of edges is the same as the
     * number of vertices.
     *
     * @return the edges of the polyline
     */
    @Override
    public Collection<LineSegment3D> edges() {
        // create resulting array
        int n = vertices.size();
        List<LineSegment3D> edges = new ArrayList<>(n);

        // do not process empty polylines
        if (n < 2) {
            return edges;
        }

        // create one edge for each couple of vertices
        for (int i = 0; i < n - 1; i++) {
            edges.add(new LineSegment3D(vertices.get(i), vertices.get(i + 1)));
        }

        // add a supplementary edge at the end, but only if vertices differ
        Point3D p0 = vertices.get(0);
        Point3D pn = vertices.get(n - 1);

        // TODO: should not make the test...
        if (pn.distance(p0) > Tolerance2D.get()) {
            edges.add(new LineSegment3D(pn, p0));
        }

        // return resulting array
        return edges;
    }

    @Override
    public int edgeNumber() {
        int n = vertices.size();
        if (n > 1) {
            return n;
        }
        return 0;
    }

    @Override
    public LineSegment3D edge(int index) {
        int i2 = (index + 1) % vertices.size();
        return new LineSegment3D(vertices.get(index), vertices.get(i2));
    }

    /**
     * Returns the last edge of this linear ring. The last edge connects the
     * last vertex with the first one.
     */
    @Override
    public LineSegment3D lastEdge() {
        int n = vertices.size();
        if (n < 2) {
            return null;
        }
        return new LineSegment3D(vertices.get(n - 1), vertices.get(0));
    }

    // ===================================================================
    // Methods inherited from interface ContinuousCurve3D
    /**
     * Returns true, by definition of linear ring.
     */
    @Override
    public boolean isClosed() {
        return true;
    }

    // ===================================================================
    // Methods inherited from interface Curve3D
    /**
     * Returns point from position as double. Position t can be from 0 to n,
     * with n equal to the number of vertices of the linear ring.
     */
    @Override
    public Point3D point(double t) {
        // format position to stay between limits
        double t0 = this.getT0();
        double t1 = this.getT1();
        t = Math.max(Math.min(t, t1), t0);

        int n = vertices.size();

        // index of vertex before point
        int ind0 = (int) Math.floor(t + Tolerance2D.get());
        double tl = t - ind0;

        if (ind0 == n) {
            ind0 = 0;
        }
        Point3D p0 = vertices.get(ind0);

        // check if equal to a vertex
        if (Math.abs(t - ind0) < Tolerance2D.get()) {
            return p0;
        }

        // index of vertex after point
        int ind1 = ind0 + 1;
        if (ind1 == n) {
            ind1 = 0;
        }
        Point3D p1 = vertices.get(ind1);

        // position on line;
        double x0 = p0.getX();
        double y0 = p0.getY();
        double z0 = p0.getZ();
        double dx = p1.getX() - x0;
        double dy = p1.getY() - y0;
        double dz = p1.getZ() - z0;

        return new Point3D(x0 + tl * dx, y0 + tl * dy, z0 + t1 * dz);
    }
    
    public double getT0() {
        return 0;
    }

    /**
     * Returns the number of points in the linear ring.
     */
    public double getT1() {
        return vertices.size();
    }

    /**
     * Returns the first point, as this is the same as the last point.
     */
    @Override
    public Point3D lastPoint() {
        if (vertices.isEmpty()) {
            return null;
        }
        return vertices.get(0);
    }

    @Override
    public Collection<? extends LinearCurve3D> continuousCurves() {
        return wrapCurve(this);
    }

    /**
     * Returns the linear ring with same points taken in reverse order. The
     * first points is still the same. Points of reverse curve are the same as
     * the original curve (same references).
     */
    @Override
    public NonPlanarLinearRing3D reverseCurve() {
        Point3D[] points2 = new Point3D[vertices.size()];
        int n = vertices.size();
        if (n > 0) {
            points2[0] = vertices.get(0);
        }

        for (int i = 1; i < n; i++) {
            points2[i] = vertices.get(n - i);
        }

        return new NonPlanarLinearRing3D(points2);
    }

    /**
     * Return an instance of Polyline3D. If t1 is lower than t0, the returned
     * Polyline contains the origin of the curve.
     */
    public Polyline3D subCurve(double t0, double t1) {
        // code adapted from CurveSet3D

        Polyline3D res = new Polyline3D();

        // number of points in the polyline
        int indMax = this.vertexNumber();

        // format to ensure t is between T0 and T1
        t0 = Math.min(Math.max(t0, 0), indMax);
        t1 = Math.min(Math.max(t1, 0), indMax);

        // find curves index
        int ind0 = (int) Math.floor(t0 + Tolerance2D.get());
        int ind1 = (int) Math.floor(t1 + Tolerance2D.get());

        // need to subdivide only one line segment
        if (ind0 == ind1 && t0 < t1) {
            // extract limit points
            res.addVertex(this.point(t0));
            res.addVertex(this.point(t1));
            // return result
            return res;
        }

        // add the point corresponding to t0
        res.addVertex(this.point(t0));

        if (ind1 > ind0) {
            // add all the whole points between the 2 cuts
            for (int n = ind0 + 1; n <= ind1; n++) {
                res.addVertex(vertices.get(n));
            }
        } else {
            // add all points until the end of the set
            for (int n = ind0 + 1; n < indMax; n++) {
                res.addVertex(vertices.get(n));
            }

            // add all points from the beginning of the set
            for (int n = 0; n <= ind1; n++) {
                res.addVertex(vertices.get(n));
            }
        }

        // add the last point
        res.addVertex(this.point(t1));

        // return the curve set
        return res;
    }

    // ===================================================================
    // Methods inherited from interface Shape3D
    /**
     * Returns the transformed shape, as a LinerRing3D.
     */
    @Override
    public NonPlanarLinearRing3D transform(AffineTransform3D trans) {
        Point3D[] pts = new Point3D[vertices.size()];
        for (int i = 0; i < vertices.size(); i++) {
            pts[i] = trans.transformPoint(vertices.get(i));
        }
        return new NonPlanarLinearRing3D(pts);
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom2d.ContinuousCurve3D#appendPath(java.awt.geom.GeneralPath)
     */
    public java.awt.geom.GeneralPath appendPath(java.awt.geom.GeneralPath path) {

        if (vertices.size() < 2) {
            return path;
        }

        // move to last first point of the curve (then a line will be drawn to
        // the first point)
        Point3D p0 = this.lastPoint();
        path.moveTo((float) p0.getX(), (float) p0.getY());

        // process each point
        for (Point3D point : vertices) {
            path.lineTo((float) point.getX(), (float) point.getY());
        }

        // close the path, even if the path is already at the right position
        path.closePath();

        return path;
    }

    // ===================================================================
    // methods implementing the GeometricObject3D interface

    /* (non-Javadoc)
	 * @see math.geom2d.GeometricObject3D#almostEquals(math.geom2d.GeometricObject3D, double)
     */
//    public boolean almostEquals(GeometricObject3D obj, double eps) {
//        if (this == obj) {
//            return true;
//        }
//
//        if (!(obj instanceof NonPlanarLinearRing3D)) {
//            return false;
//        }
//        NonPlanarLinearRing3D ring = (NonPlanarLinearRing3D) obj;
//
//        if (vertices.size() != ring.vertices.size()) {
//            return false;
//        }
//
//        for (int i = 0; i < vertices.size(); i++) {
//            if (!(vertices.get(i)).almostEquals(ring.vertices.get(i), eps)) {
//                return false;
//            }
//        }
//        return true;
//    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof NonPlanarLinearRing3D)) {
            return false;
        }
        NonPlanarLinearRing3D ring = (NonPlanarLinearRing3D) object;

        if (vertices.size() != ring.vertices.size()) {
            return false;
        }
        for (int i = 0; i < vertices.size(); i++) {
            if (!(vertices.get(i)).equals(ring.vertices.get(i))) {
                return false;
            }
        }
        return true;
    }

}
