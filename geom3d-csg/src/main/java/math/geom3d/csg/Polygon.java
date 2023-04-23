/**
 * Polygon.java
 *
 * Copyright 2014-2014 Michael Hoffer info@michaelhoffer.de. All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY Michael Hoffer info@michaelhoffer.de "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL Michael Hoffer info@michaelhoffer.de OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of Michael Hoffer
 * info@michaelhoffer.de.
 */
package math.geom3d.csg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import math.geom3d.Box3D;
import math.geom3d.Point3D;
import math.geom3d.Vector3D;
import math.geom3d.csg.util.PolygonUtil;
//import tech.cae.parts.geometry3.poly2tri.PolygonUtil;

// TODO: Auto-generated Javadoc
/**
 * Represents a convex polygon.
 *
 */
public final class Polygon {
    private static long n = 0L;
    public final long source;
    /**
     * Polygon vertices.
     */
    public final List<Vertex> vertices;
    /**
     * Plane defined by this polygon.
     *
     * Note: uses first three vertices to define the plane.
     */
    public final Plane plane;

    /**
     * Decomposes the specified concave polygon into convex polygons.
     *
     * @param points the points that define the polygon
     * @return the decomposed concave polygon (list of convex polygons)
     */
    public static List<Polygon> fromConcavePoints(Point3D... points) {
        Polygon p = fromPoints(points);

        return PolygonUtil.concaveToConvex(p);
    }

    /**
     * Decomposes the specified concave polygon into convex polygons.
     *
     * @param points the points that define the polygon
     * @return the decomposed concave polygon (list of convex polygons)
     */
    public static List<Polygon> fromConcavePoints(List<Point3D> points) {
        Polygon p = fromPoints(points);

        return PolygonUtil.concaveToConvex(p);
    }

    /**
     * Constructor. Creates a new polygon that consists of the specified
     * vertices.
     *
     * Note: the vertices used to initialize a polygon must be coplanar and form
     * a convex loop.
     *
     * @param vertices polygon vertices
     */
    public Polygon(List<Vertex> vertices) {
        this(vertices, Plane.createFromVertices(vertices), n++);
    }
    
    public Polygon(List<Vertex> vertices, Plane plane, long source) {
        this.vertices = vertices;
        this.plane = plane;
        this.source = source;
    }

    /**
     * Constructor. Creates a new polygon that consists of the specified
     * vertices.
     *
     * Note: the vertices used to initialize a polygon must be coplanar and form
     * a convex loop.
     *
     * @param vertices polygon vertices
     *
     */
    public Polygon(Vertex... vertices) {
        this(Arrays.asList(vertices));
    }

    /**
     * Flips polygon.
     *
     * @return new flipped polygon
     */
    public Polygon flip() {
        List<Vertex> newVertices = vertices.stream().map(vertex -> vertex.flip()).collect(Collectors.toList());
        Collections.reverse(newVertices);
        return new Polygon(newVertices, plane.flip(), source*-1);
    }

    public Plane getPlane() {
        return plane;
    }

    public Vector3D getNormal() {
        return plane.getNormal();
    }

    /**
     * Translates this polygon.
     *
     * @param v the vector that defines the translation
     * @return new polygon
     */
    public Polygon translate(Vector3D vector) {
        List<Vertex> newVertices = this.vertices.stream().map(v -> v.translate(vector)).collect(Collectors.toList());
        Point3D a = newVertices.get(0).pos;
        Point3D b = newVertices.get(1).pos;
        Point3D c = newVertices.get(2).pos;
        Plane newPlane = new Plane(this.plane.getNormal(), this.plane.normal.dot(new Vector3D(a)));
        return new Polygon(newVertices, newPlane, source);
    }

    /**
     * Applies the specified transformation to this polygon.
     *
     * Note: if the applied transformation performs a mirror operation the
     * vertex order of this polygon is reversed.
     *
     * @param transform the transformation to apply
     *
     * @return new polygon
     */
    public Polygon transform(Transform transform) {
        List<Vertex> newVertices = this.vertices.stream().map(v -> v.transform(transform)).collect(Collectors.toList());
        Point3D a = newVertices.get(0).pos;
        Point3D b = newVertices.get(1).pos;
        Point3D c = newVertices.get(2).pos;
        Plane newPlane = new Plane(new Vector3D(a, b).cross(new Vector3D(a, c)).normalize(), this.plane.normal.dot(new Vector3D(a)));
        if (transform.isMirror()) {
            // the transformation includes mirroring. flip polygon
            return new Polygon(newVertices, newPlane, source).flip();
        }
        return new Polygon(newVertices, newPlane, source);
    }

    /**
     * Creates a polygon from the specified point list.
     *
     * @param points the points that define the polygon
     * @return a polygon defined by the specified point list
     */
    public static Polygon fromPoints(List<Point3D> points) {
        return fromPoints(points, null);
    }

    /**
     * Creates a polygon from the specified points.
     *
     * @param points the points that define the polygon
     * @return a polygon defined by the specified point list
     */
    public static Polygon fromPoints(Point3D... points) {
        return fromPoints(Arrays.asList(points), null);
    }

    /**
     * Creates a polygon from the specified point list.
     *
     * @param points the points that define the polygon
     * @param plane may be null
     * @return a polygon defined by the specified point list
     */
    private static Polygon fromPoints(
            List<Point3D> points, Plane plane) {

        Vector3D normal = (plane != null) ? plane.normal : new Vector3D(0, 0, 0);

        List<Vertex> vertices = new ArrayList<>(points.size());

        points.stream().map((p) -> new Vertex(p, normal)).forEachOrdered((vertex) -> {
            vertices.add(vertex);
        });

        return new Polygon(vertices);
    }

    /**
     * Returns the bounds of this polygon.
     *
     * @return bouds of this polygon
     */
    public Box3D getBounds() {
        return Box3D.fromPoints(getPoints());
    }
    
    public static Collection<Polygon> merge(Collection<Polygon> polygons) {
        // TODO
        return polygons;
    }
    
//    public Collection<Polygon> mergeCoplanar(Polygon other) {
//        
//    }
    
    public boolean intersects(Polygon other) {
        List<Integer> types = new ArrayList<>(other.vertices.size());
        int polygonType = plane.categorise(other, types);
        switch(polygonType) {
            case Plane.COPLANAR:
                for(Point3D v : other.getPoints()) {
                    if(contains(v)) {
                        return true;
                    }
                }
                return false;
            case Plane.SPANNING:
                for (int i = 0; i < other.vertices.size(); i++) {
                    int j = (i + 1) % other.vertices.size();
                    int ti = types.get(i);
                    int tj = types.get(j);
                    if ((ti | tj) == Plane.SPANNING) {
                        Vertex vi = other.vertices.get(i);
                        Vertex vj = other.vertices.get(j);
                        double t = (plane.dist - plane.normal.dot(new Vector3D(vi.pos))) / plane.normal.dot(new Vector3D(vi.pos, vj.pos));
                        Vertex v = vi.interpolate(vj, t);
                        if(contains(v.pos)) {
                            return true;
                        }
                    }
                }
                List<Integer> myTypes = new ArrayList<>(vertices.size());
                other.plane.categorise(this, myTypes);
                for (int i = 0; i < vertices.size(); i++) {
                    int j = (i + 1) % vertices.size();
                    int ti = myTypes.get(i);
                    int tj = myTypes.get(j);
                    if ((ti | tj) == Plane.SPANNING) {
                        Vertex vi = vertices.get(i);
                        Vertex vj = vertices.get(j);
                        double t = (other.plane.dist - other.plane.normal.dot(new Vector3D(vi.pos))) / other.plane.normal.dot(new Vector3D(vi.pos, vj.pos));
                        Vertex v = vi.interpolate(vj, t);
                        if(other.contains(v.pos)) {
                            return true;
                        }
                    }
                }
                return false;
            default:
                return false;
        }
    }

    /**
     * Contains.
     *
     * @param p the p
     * @return true, if successful
     */
    public boolean contains(Point3D p) {
        // taken from http://www.java-gaming.org/index.php?topic=26013.0
        // and http://www.ecse.rpi.edu/Homepages/wrf/Research/Short_Notes/pnpoly.html
        double px = p.getX();
        double py = p.getY();
        boolean oddNodes = false;
        double x2 = vertices.get(vertices.size() - 1).pos.getX();
        double y2 = vertices.get(vertices.size() - 1).pos.getY();
        double x1, y1;
        for (int i = 0; i < vertices.size(); x2 = x1, y2 = y1, ++i) {
            x1 = vertices.get(i).pos.getX();
            y1 = vertices.get(i).pos.getY();
            if (((y1 < py) && (y2 >= py))
                    || (y1 >= py) && (y2 < py)) {
                if ((py - y1) / (y2 - y1)
                        * (x2 - x1) < (px - x1)) {
                    oddNodes = !oddNodes;
                }
            }
        }
        return oddNodes;
    }

    /**
     * Contains.
     *
     * @param p the p
     * @return true, if successful
     */
    public boolean contains(Polygon p) {
        return p.vertices.stream().noneMatch((v) -> (!contains(v.pos)));
    }

    public List<Point3D> getPoints() {
        return vertices.stream().map(v -> v.pos).collect(Collectors.toList());
    }

    /**
     * Returns a triangulated version of this polygon. TODO: Replace with
     * poly2tri
     *
     * @return triangles
     */
    public List<Polygon> toTriangles() {
        switch(this.vertices.size()) {
            case 0:
            case 1:
            case 2:
                return Arrays.asList();
            case 3:
                return Arrays.asList(this);
            default:
                List<Polygon> result = new ArrayList<>(this.vertices.size()-2);
                // TODO: improve the triangulation?
                //
                // If our polygon has more vertices, create
                // multiple triangles:
                Vertex firstVertexStl = this.vertices.get(0);
                for (int i = 0; i < this.vertices.size() - 2; i++) {
                    // create triangle
                    Polygon polygon = new Polygon(
                        firstVertexStl,
                        this.vertices.get(i + 1),
                        this.vertices.get(i + 2)
                    );
                    result.add(polygon);
                }
                return result;
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + Objects.hashCode(this.vertices);
        hash = 17 * hash + Objects.hashCode(this.plane);
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
        final Polygon other = (Polygon) obj;
        if (!Objects.equals(this.vertices, other.vertices)) {
            return false;
        }
        return Objects.equals(this.plane, other.plane);
    }
    
    
}
