/*
The MIT License (MIT)

Copyright (c) 2014 CCHall (aka Cyanobacterium aka cyanobacteruim)

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package math.geom3d.io;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import math.geom2d.Tolerance2D;
import math.geom2d.Point2D;
import math.geom2d.polygon.SimplePolygon2D;
import math.geom3d.Point3D;
import math.geom3d.GeometricObject3D;
import math.geom3d.Shape3D;
import math.geom3d.Box3D;
import math.geom3d.transform.AffineTransform3D;
;
import math.geom3d.Vector3D;
import math.geom3d.csg.CSG;
import math.geom3d.csg.Polygon;
import math.geom3d.line.StraightLine3D;
import math.geom3d.plane.Plane3D;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.util.FastMath;

/**
 * This object represents a triangle in 3D space.
 *
 * @author CCHall
 */
public class Triangle3D implements Shape3D {

    private final Point3D[] vertices;
    private final Vector3D normal;

    Triangle3D(List<Point3D> points, Vector3D normal) {
        this(points.get(0), points.get(1), points.get(2), normal);
    }

    /**
     * Creates a triangle with the given vertices at its corners. The normal is
     * calculated by assuming that the vertices were provided in right-handed
     * coordinate space (counter-clockwise)
     *
     * @param v1 A corner vertex
     * @param v2 A corner vertex
     * @param v3 A corner vertex
     */
    public Triangle3D(Point3D v1, Point3D v2, Point3D v3) {
        this.vertices = new Point3D[3];
        this.vertices[0] = v1;
        this.vertices[1] = v2;
        this.vertices[2] = v3;
        Vector3D edge1 = new Vector3D(v1, v2);
        Vector3D edge2 = new Vector3D(v1, v3);
        this.normal = Vector3D.crossProduct(edge1, edge2).normalize();
    }

    /**
     * Creates a triangle with the given vertices at its corners and a given
     * normal.
     *
     * @param v1 A corner vertex
     * @param v2 A corner vertex
     * @param v3 A corner vertex
     * @param normal The normal
     */
    public Triangle3D(Point3D v1, Point3D v2, Point3D v3, Vector3D normal) {
        this.vertices = new Point3D[3];
        this.vertices[0] = v1;
        this.vertices[1] = v2;
        this.vertices[2] = v3;
        this.normal = normal;
    }

    public static List<Triangle3D> fromCSG(CSG csg) {
        return csg.getPolygons().stream()
                .flatMap(polygon -> polygon.toTriangles().stream())
                .map(triangularPolygon -> new Triangle3D(triangularPolygon.getPoints(), triangularPolygon.getNormal()))
                .collect(Collectors.toList());
    }

    public static CSG toCSG(List<Triangle3D> triangles) {
        return CSG.fromPolygons(triangles.stream()
                .map(tri -> Polygon.fromPoints(tri.getVertices()))
                .collect(Collectors.toList()));
    }

    public static double windingNumber(List<Triangle3D> triangles, Point3D p) {
        return triangles.stream().parallel().mapToDouble(tri -> tri.windingNumber(p)).sum();
    }

    public static boolean isInside(List<Triangle3D> triangles, Point3D p) {
        return windingNumber(triangles, p) >= 2 * Math.PI;
    }

    public static double distance(List<Triangle3D> triangles, Point3D p) {
        return triangles.stream().parallel().mapToDouble(tri -> tri.distance(p)).min().orElse(Double.MAX_VALUE);
    }

    public static double signedDistance(List<Triangle3D> triangles, Point3D p) {
        return (isInside(triangles, p) ? -1.0 : 1.0) * distance(triangles, p);
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isEmpty() {
        return vertices[0].distance(vertices[1]) < Tolerance2D.get()
                || vertices[1].distance(vertices[2]) < Tolerance2D.get()
                || vertices[2].distance(vertices[0]) < Tolerance2D.get();
    }

    @Override
    public boolean isBounded() {
        return true;
    }

    @Override
    public Box3D boundingBox() {
        return Box3D.fromPoints(this.vertices);
    }

    @Override
    public Triangle3D transform(AffineTransform3D trans) {
        return new Triangle3D(vertices[0].transform(trans), vertices[1].transform(trans), vertices[2].transform(trans));
    }

    @Override
    public double distance(Point3D p) {
        double minDistance = Double.MAX_VALUE;
        minDistance = Math.min(minDistance, this.vertices[0].distance(p));
        minDistance = Math.min(minDistance, this.vertices[1].distance(p));
        minDistance = Math.min(minDistance, this.vertices[2].distance(p));
        Plane3D plane = getPlane();
        double planarDistance = plane.distance(p);
        if (planarDistance < minDistance) {
            Point2D p2 = plane.pointPosition(plane.projectPoint(p));
            if (new SimplePolygon2D(
                    plane.pointPosition(plane.projectPoint(this.vertices[0])),
                    plane.pointPosition(plane.projectPoint(this.vertices[1])),
                    plane.pointPosition(plane.projectPoint(this.vertices[2]))).contains(p2)) {
                return planarDistance;
            }
        }
        return minDistance;
    }

    public Point3D intersection(StraightLine3D ray) {
        Plane3D plane = getPlane();
        Point3D p = plane.lineIntersection(ray);
        Point2D p2 = plane.pointPosition(plane.projectPoint(p));
        if (new SimplePolygon2D(
                plane.pointPosition(plane.projectPoint(this.vertices[0])),
                plane.pointPosition(plane.projectPoint(this.vertices[1])),
                plane.pointPosition(plane.projectPoint(this.vertices[2]))).contains(p2)) {
            return p;
        }
        return null;
    }

    public double area() {
        Plane3D plane = getPlane();
        return Math.abs(new SimplePolygon2D(
                plane.pointPosition(plane.projectPoint(this.vertices[0])),
                plane.pointPosition(plane.projectPoint(this.vertices[1])),
                plane.pointPosition(plane.projectPoint(this.vertices[2]))).area());
    }

    public Plane3D getPlane() {
        return Plane3D.fromNormal(this.vertices[0], this.normal);
    }

    @Override
    public boolean contains(Point3D point) {
        return distance(point) < Tolerance2D.get();
    }

    /**
     * Based on
     * https://github.com/marmakoide/inside-3d-mesh/blob/master/is_inside_mesh.py
     *
     * @param point
     * @return
     */
    public double windingNumber(Point3D point) {
        Point3D pa = vertices[0].minus(point);
        Point3D pb = vertices[1].minus(point);
        Point3D pc = vertices[2].minus(point);
        double det = new LUDecomposition(new Array2DRowRealMatrix(new double[][]{
            new double[]{pa.getX(), pa.getY(), pa.getZ()},
            new double[]{pb.getX(), pb.getY(), pb.getZ()},
            new double[]{pc.getX(), pc.getY(), pc.getZ()}
        })).getDeterminant();
        double a = vertices[0].distance(point);
        double b = vertices[0].distance(point);
        double c = vertices[0].distance(point);
        double dab = pa.asVector().dot(pb.asVector());
        double dbc = pb.asVector().dot(pc.asVector());
        double dca = pc.asVector().dot(pa.asVector());
        return FastMath.atan2(det, (a * b * c) + c * dab + a * dbc + b * dca);
    }

    /**
     * Moves the triangle in the X,Y,Z direction
     *
     * @param translation A vector of the delta for each coordinate.
     */
    public void translate(Vector3D translation) {
        for (int i = 0; i < vertices.length; i++) {
            vertices[i] = vertices[i].plus(translation);
        }
    }

    /**
     * @see java.lang.Object#toString()
     * @return A string that provides some information about this triangle
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Triangle[");
        for (Point3D v : vertices) {
            sb.append(v.toString());
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Gets the vertices at the corners of this triangle
     *
     * @return An array of vertices
     */
    public Point3D[] getVertices() {
        return vertices;
    }

    /**
     * Gets the normal vector
     *
     * @return A vector pointing in a direction perpendicular to the surface of
     * the triangle.
     */
    public Vector3D getNormal() {
        return normal;
    }

    /**
     * Changes the scale, e.g. for unit translation
     *
     * @param scale
     * @return
     */
    public Triangle3D scale(double scale) {
        return new Triangle3D(vertices[0].times(scale), vertices[1].times(scale), vertices[2].times(scale), normal);
    }

    @Override
    public boolean almostEquals(GeometricObject3D obj, double eps) {
        if (obj instanceof Triangle3D) {
            Triangle3D other = (Triangle3D) obj;
            return (this.vertices[0].almostEquals(other.vertices[0], eps)
                    && this.vertices[1].almostEquals(other.vertices[1], eps)
                    && this.vertices[2].almostEquals(other.vertices[2], eps))
                    || (this.vertices[0].almostEquals(other.vertices[1], eps)
                    && this.vertices[1].almostEquals(other.vertices[2], eps)
                    && this.vertices[2].almostEquals(other.vertices[0], eps))
                    || (this.vertices[0].almostEquals(other.vertices[2], eps)
                    && this.vertices[1].almostEquals(other.vertices[0], eps)
                    && this.vertices[2].almostEquals(other.vertices[1], eps));
        }
        return false;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     * @param obj Object to test equality
     * @return True if the other object is a triangle whose verticese are the
     * same as this one.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Triangle3D other = (Triangle3D) obj;
        if (!Arrays.deepEquals(this.vertices, other.vertices)) {
            return false;
        }
        return true;
    }

    /**
     * @see java.lang.Object#hashCode()
     * @return A hashCode for this triangle
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Arrays.deepHashCode(this.vertices);
        return hash;
    }
}
