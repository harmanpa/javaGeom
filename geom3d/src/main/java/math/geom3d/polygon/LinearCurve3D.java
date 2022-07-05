/**
 *
 */
package math.geom3d.polygon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import math.geom2d.Tolerance2D;
import math.geom3d.Box3D;
import math.geom3d.GeometricObject3D;
import math.geom3d.Point3D;
import math.geom3d.Vector3D;
import math.geom3d.circulinear.CirculinearContinuousCurve3D;
import math.geom3d.curve.AbstractContinuousCurve3D;
import math.geom3d.line.LineSegment3D;
import math.geom3d.line.LinearShape3D;

/**
 * Abstract class that is the base implementation of Polyline3D and
 * LinearRing3D.
 *
 * @author dlegland
 *
 */
public abstract class LinearCurve3D extends AbstractContinuousCurve3D
        implements CirculinearContinuousCurve3D {

    // ===================================================================
    // class variables
    private final ArrayList<Point3D> vertices;

    // ===================================================================
    // Contructors
    protected LinearCurve3D() {
        this.vertices = new ArrayList<>();
    }

    /**
     * Creates a new linear curve by allocating enough memory for the specified
     * number of vertices.
     *
     * @param nVertices
     */
    protected LinearCurve3D(int nVertices) {
        this.vertices = new ArrayList<>(nVertices);
    }

    protected LinearCurve3D(Point3D... vertices) {
        this.vertices = new ArrayList<>(vertices.length);
        for (Point3D vertex : vertices) {
            this.vertices.add(vertex);
        }
    }

    protected LinearCurve3D(Collection<? extends Point3D> vertices) {
        this.vertices = new ArrayList<>(vertices.size());
        this.vertices.addAll(vertices);
    }

    protected LinearCurve3D(double[] xcoords, double[] ycoords, double[] zcoords) {
        this.vertices = new ArrayList<>(xcoords.length);
        int n = xcoords.length;
        this.vertices.ensureCapacity(n);
        for (int i = 0; i < n; i++) {
            vertices.add(new Point3D(xcoords[i], ycoords[i], zcoords[i]));
        }
    }

    // ===================================================================
    // Methods specific to LinearCurve3D
    /**
     * Returns a simplified version of this linear curve. Sub classes may
     * override this method to return a more specialized type.
     */
    public abstract LinearCurve3D simplify(double distMax);

    @Override
    public abstract LinearCurve3D reverseCurve();

    @Override
    public abstract LinearCurve3D subCurve(double t0, double t1);

    /**
     * Returns an iterator on the collection of points.
     */
    public Iterator<Point3D> vertexIterator() {
        return vertices.iterator();
    }

    /**
     * Returns the collection of vertices as an array of Point3D.
     *
     * @return an array of Point3D
     */
    public Point3D[] vertexArray() {
        return this.vertices.toArray(new Point3D[]{});
    }

    /**
     * Adds a vertex at the end of this polyline.
     *
     * @return true if the vertex was correctly added
     * @since 0.9.3
     */
    public boolean addVertex(Point3D vertex) {
        return vertices.add(vertex);
    }

    /**
     * Inserts a vertex at a given position in the polyline.
     *
     * @since 0.9.3
     */
    public void insertVertex(int index, Point3D vertex) {
        vertices.add(index, vertex);
    }

    /**
     * Removes the first instance of the given vertex from this polyline.
     *
     * @param vertex the position of the vertex to remove
     * @return true if the vertex was actually removed
     * @since 0.9.3
     */
    public boolean removeVertex(Point3D vertex) {
        return vertices.remove(vertex);
    }

    /**
     * Removes the vertex specified by the index.
     *
     * @param index the index of the vertex to remove
     * @return the position of the vertex removed from the polyline
     * @since 0.9.3
     */
    public Point3D removeVertex(int index) {
        return this.vertices.remove(index);
    }

    /**
     * Changes the position of the i-th vertex.
     *
     * @since 0.9.3
     */
    public void setVertex(int index, Point3D position) {
        this.vertices.set(index, position);
    }

    public void clearVertices() {
        vertices.clear();
    }

    /**
     * Returns the vertices of the polyline.
     */
    public List<Point3D> vertices() {
        return vertices;
    }

    /**
     * Returns the i-th vertex of the polyline.
     *
     * @param i index of the vertex, between 0 and the number of vertices
     */
    public Point3D vertex(int i) {
        return vertices.get(i);
    }

    /**
     * Returns the number of vertices.
     *
     * @return the number of vertices
     */
    public int vertexNumber() {
        return vertices.size();
    }

    /**
     * Computes the index of the closest vertex to the input point.
     */
    public int closestVertexIndex(Point3D point) {
        double minDist = Double.POSITIVE_INFINITY;
        int index = -1;

        for (int i = 0; i < vertices.size(); i++) {
            double dist = vertices.get(i).distance(point);
            if (dist < minDist) {
                index = i;
                minDist = dist;
            }
        }

        return index;
    }

    // ===================================================================
    // Management of edges
    /**
     * Returns the i-th edge of this linear curve.
     */
    public abstract LineSegment3D edge(int index);

    /**
     * Returns the number of edges of this linear curve.
     */
    public abstract int edgeNumber();

    /**
     * Returns a collection of LineSegment3D that represent the individual edges
     * of this linear curve.
     *
     * @return the edges of the polyline
     */
    public abstract Collection<LineSegment3D> edges();

    public LineSegment3D firstEdge() {
        if (vertices.size() < 2) {
            return null;
        }
        return new LineSegment3D(vertices.get(0), vertices.get(1));
    }

    public abstract LineSegment3D lastEdge();

    // ===================================================================
    // methods implementing the CirculinearCurve3D interface

    /* (non-Javadoc)
	 * @see math.geom2d.circulinear.CirculinearCurve3D#length()
     */
    @Override
    public double length() {
        double sum = 0;
        for (LineSegment3D edge : this.edges()) {
            sum += edge.length();
        }
        return sum;
    }

    /* (non-Javadoc)
	 * @see math.geom2d.circulinear.CirculinearCurve3D#length(double)
     */
    @Override
    public double length(double pos) {
        //init
        double length = 0;

        // add length of each curve before current curve
        int index = (int) Math.floor(pos);
        for (int i = 0; i < index; i++) {
            length += this.edge(i).length();
        }

        // add portion of length for last curve
        if (index < vertices.size() - 1) {
            double pos2 = pos - index;
            length += this.edge(index).length(pos2);
        }

        // return computed length
        return length;
    }

    /* (non-Javadoc)
	 * @see math.geom2d.circulinear.CirculinearCurve3D#position(double)
     */
    @Override
    public double position(double length) {

        // position to compute
        double pos = 0;

        // index of current curve
        int index = 0;

        // cumulative length
        double cumLength = this.length(this.getT0());

        // iterate on all curves
        for (LineSegment3D edge : edges()) {
            // length of current curve
            double edgeLength = edge.length();

            // add either 2, or fraction of length
            if (cumLength + edgeLength < length) {
                cumLength += edgeLength;
                index++;
            } else {
                // add local position on current curve
                double pos2 = edge.position(length - cumLength);
                pos = index + pos2;
                break;
            }
        }

        // return the result
        return pos;
    }

    // ===================================================================
    // Methods implementing OrientedCurve3D interface

    /*
     * (non-Javadoc)
     * 
     * @see math.geom2d.OrientedCurve3D#signedDistance(Point3D)
     */
//    public double signedDistance(Point3D point) {
//        double dist = this.distance(point);
//        if (isInside(point)) {
//            return -dist;
//        } else {
//            return dist;
//        }
//    }
    // ===================================================================
    // Methods inherited from ContinuousCurve3D

    /* (non-Javadoc)
	 * @see math.geom2d.curve.ContinuousCurve3D#leftTangent(double)
     */
    @Override
    public Vector3D leftTangent(double t) {
        int index = (int) Math.floor(t);
        if (Math.abs(t - index) < Tolerance2D.get()) {
            index--;
        }
        return this.edge(index).tangent(0);
    }

    /* (non-Javadoc)
	 * @see math.geom2d.curve.ContinuousCurve3D#rightTangent(double)
     */
    @Override
    public Vector3D rightTangent(double t) {
        int index = (int) Math.ceil(t);
        return this.edge(index).tangent(0);
    }

    /* (non-Javadoc)
	 * @see math.geom2d.curve.ContinuousCurve3D#curvature(double)
     */
    @Override
    public double curvature(double t) {
        double index = Math.round(t);
        if (Math.abs(index - t) > Tolerance2D.get()) {
            return 0;
        } else {
            return Double.POSITIVE_INFINITY;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom2d.ContinuousCurve3D#smoothPieces()
     */
    @Override
    public Collection<? extends LineSegment3D> smoothPieces() {
        return edges();
    }

    /**
     * Returns the first point of the linear curve.
     */
    @Override
    public Point3D firstPoint() {
        if (vertices.isEmpty()) {
            return null;
        }
        return vertices.get(0);
    }

    @Override
    public Collection<Point3D> singularPoints() {
        return vertices;
    }

    public boolean isSingular(double pos) {
        return Math.abs(pos - Math.round(pos)) < Tolerance2D.get();
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom2d.Curve3D#position(math.geom2d.Point3D)
     */
    @Override
    public double position(Point3D point) {
        int ind = 0;
        double dist, minDist = Double.POSITIVE_INFINITY;

        int i = 0;
        LineSegment3D closest = null;
        for (LineSegment3D edge : this.edges()) {
            dist = edge.distance(point);
            if (dist < minDist) {
                minDist = dist;
                ind = i;
                closest = edge;
            }
            i++;
        }

        return closest.position(point) + ind;
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom2d.Curve3D#intersections(math.geom2d.LinearShape3D)
     */
    @Override
    public Collection<Point3D> intersections(LinearShape3D line) {
        ArrayList<Point3D> list = new ArrayList<>();

        // extract intersections with each edge, and add to a list
        Point3D point;
        for (LineSegment3D edge : this.edges()) {
            // do not process edges parallel to intersection line
            if (edge.isParallel(line)) {
                continue;
            }

            point = edge.intersection(line);
            if (point != null) {
                if (!list.contains(point)) {
                    list.add(point);
                }
            }
        }

        // return result
        return list;
    }

    @Override
    public Collection<? extends LinearCurve3D> continuousCurves() {
        return wrapCurve(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see math.geom2d.Curve3D#position(math.geom2d.Point3D)
     */
    @Override
    public double project(Point3D point) {
        double dist, minDist = Double.POSITIVE_INFINITY;
        double pos = Double.NaN;

        int i = 0;
        for (LineSegment3D edge : this.edges()) {
            dist = edge.distance(point);
            if (dist < minDist) {
                minDist = dist;
                pos = edge.project(point) + i;
            }
            i++;
        }

        return pos;
    }

    // ===================================================================
    // Methods inherited from interface Shape3D

    /*
     * (non-Javadoc)
     * 
     * @see math.geom2d.Shape3D#distance(Point3D)
     */
    @Override
    public double distance(Point3D point) {
        double dist = Double.MAX_VALUE;
        for (LineSegment3D edge : this.edges()) {
            if (edge.length() == 0) {
                continue;
            }
            dist = Math.min(dist, edge.distance(point));
        }
        return dist;
    }

    /**
     * Returns true if the polyline does not contain any point.
     */
    @Override
    public boolean isEmpty() {
        return vertices.isEmpty();
    }

    /**
     * Always returns true, because a linear curve is always bounded.
     */
    @Override
    public boolean isBounded() {
        return true;
    }

    /**
     * Returns the bounding box of this linear curve.
     */
    @Override
    public Box3D boundingBox() {
        double xmin = Double.MAX_VALUE;
        double ymin = Double.MAX_VALUE;
        double zmin = Double.MAX_VALUE;
        double xmax = Double.MIN_VALUE;
        double ymax = Double.MIN_VALUE;
        double zmax = Double.MIN_VALUE;

        Iterator<Point3D> iter = vertices.iterator();
        Point3D point;
        double x, y, z;
        while (iter.hasNext()) {
            point = iter.next();
            x = point.getX();
            y = point.getY();
            z = point.getZ();
            xmin = Math.min(xmin, x);
            xmax = Math.max(xmax, x);
            ymin = Math.min(ymin, y);
            ymax = Math.max(ymax, y);
            zmin = Math.min(zmin, z);
            zmax = Math.max(zmax, z);
        }

        return new Box3D(xmin, xmax, ymin, ymax, zmin, zmax);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.Shape#contains(Point3D)
     */
    @Override
    public boolean contains(Point3D point) {
        for (LineSegment3D edge : this.edges()) {
            if (edge.length() == 0) {
                continue;
            }
            if (edge.contains(point)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Clips the polyline by a box. The result is an instance of CurveSet3D,
     * which contains only instances of Polyline3D. If the polyline is not
     * clipped, the result is an instance of CurveSet3D which contains 0 curves.
     */
//    @Override
//    public CurveSet3D<? extends LinearCurve3D> clip(Box3D box) {
//        // Clip the curve
//        CurveSet3D<? extends Curve3D> set = null;//Curves3D.clipCurve(this, box);
//
//        // Stores the result in appropriate structure
//        CurveArray3D<LinearCurve3D> result
//                = new CurveArray3D<>(set.size());
//
//        // convert the result
//        for (Curve3D curve : set.curves()) {
//            if (curve instanceof LinearCurve3D) {
//                result.add((LinearCurve3D) curve);
//            }
//        }
//        return result;
//    }
    @Override
    public boolean almostEquals(GeometricObject3D obj, double eps) {
        return GeometricObject3D.almostEquals(this, obj, eps);
    }

    @Override
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public boolean equals(Object obj) {
        return GeometricObject3D.equals(this, obj);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(this.vertices);
        return hash;
    }

}
