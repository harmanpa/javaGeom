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
import java.util.Collections;
import java.util.List;
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
 * Each convex polygon has a {@code shared} property, which is shared between
 * all polygons that are clones of each other or where split from the same
 * polygon. This can be used to define per-polygon properties (such as surface
 * color).
 */
public final class Polygon {

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
        this.vertices = vertices;
        this.plane = Plane.createFromPoints(
                vertices.get(0).pos,
                vertices.get(1).pos,
                vertices.get(2).pos);
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

    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    @Override
    @SuppressWarnings({"CloneDoesntCallSuperClone", "CloneDeclaresCloneNotSupported"})
    public Polygon clone() {
        List<Vertex> newVertices = new ArrayList<>();
        this.vertices.forEach((vertex) -> {
            newVertices.add(vertex.clone());
        });
        return new Polygon(newVertices);
    }

    /**
     * Flips this polygon.
     *
     * @return this polygon
     */
    public Polygon flip() {
        vertices.forEach((vertex) -> {
            vertex.flip();
        });
        Collections.reverse(vertices);

        plane.flip();

        return this;
    }

    /**
     * Returns a flipped copy of this polygon.Note: this polygon is not
     * modified.
     *
     *
     * @return a flipped copy of this polygon
     */
    public Polygon flipped() {
        return clone().flip();
    }

    /**
     * Translates this polygon.
     *
     * @param v the vector that defines the translation
     * @return this polygon
     */
    public Polygon translate(Vector3D v) {
        vertices.forEach((vertex) -> {
            vertex.pos = vertex.pos.plus(v);
        });

        Point3D a = this.vertices.get(0).pos;
        Point3D b = this.vertices.get(1).pos;
        Point3D c = this.vertices.get(2).pos;

        this.plane.normal = new Vector3D(a, b).cross(new Vector3D(a, c)).normalize();

        return this;
    }

    /**
     * Returns a translated copy of this polygon.
     *
     * Note: this polygon is not modified
     *
     * @param v the vector that defines the translation
     *
     * @return a translated copy of this polygon
     */
    public Polygon translated(Vector3D v) {
        return clone().translate(v);
    }

    /**
     * Applies the specified transformation to this polygon.
     *
     * Note: if the applied transformation performs a mirror operation the
     * vertex order of this polygon is reversed.
     *
     * @param transform the transformation to apply
     *
     * @return this polygon
     */
    public Polygon transform(Transform transform) {

        this.vertices.stream().forEach(
                (v) -> {
                    v.transform(transform);
                }
        );

        Point3D a = this.vertices.get(0).pos;
        Point3D b = this.vertices.get(1).pos;
        Point3D c = this.vertices.get(2).pos;

        this.plane.normal = new Vector3D(a, b).cross(new Vector3D(a, c)).normalize();
        this.plane.dist = this.plane.normal.dot(new Vector3D(a));

        if (transform.isMirror()) {
            // the transformation includes mirroring. flip polygon
            flip();

        }
        return this;
    }

    /**
     * Returns a transformed copy of this polygon.
     *
     * Note: if the applied transformation performs a mirror operation the
     * vertex order of this polygon is reversed.
     *
     * Note: this polygon is not modified
     *
     * @param transform the transformation to apply
     * @return a transformed copy of this polygon
     */
    public Polygon transformed(Transform transform) {
        return clone().transform(transform);
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

        List<Vertex> vertices = new ArrayList<>();

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

        List<Polygon> result = new ArrayList<>();

        if (this.vertices.size() >= 3) {

            // TODO: improve the triangulation?
            //
            // If our polygon has more vertices, create
            // multiple triangles:
            Vertex firstVertexStl = this.vertices.get(0);
            for (int i = 0; i < this.vertices.size() - 2; i++) {

                // create triangle
                Polygon polygon = Polygon.fromPoints(
                        firstVertexStl.pos,
                        this.vertices.get(i + 1).pos,
                        this.vertices.get(i + 2).pos
                );

                result.add(polygon);
            }
        }

        return result;
    }
}
