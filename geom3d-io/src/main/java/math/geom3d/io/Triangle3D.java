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
import math.geom3d.Point3D;
import math.geom3d.Vector3D;
import math.geom3d.csg.CSG;
import math.geom3d.csg.Polygon;

/**
 * This object represents a triangle in 3D space.
 *
 * @author CCHall
 */
public class Triangle3D {

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

    /**
     *
     * @return
     */
    public boolean isEmpty() {
        return vertices[0].distance(vertices[1]) < Tolerance2D.get()
                || vertices[1].distance(vertices[2]) < Tolerance2D.get()
                || vertices[2].distance(vertices[0]) < Tolerance2D.get();
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
