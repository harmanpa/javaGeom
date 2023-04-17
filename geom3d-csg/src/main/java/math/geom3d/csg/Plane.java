/**
 * Plane.java
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

// # class Plane
import java.util.ArrayList;
import java.util.List;
import math.geom2d.Tolerance2D;
import math.geom3d.Point3D;
import math.geom3d.Vector3D;
import math.geom3d.fitting.FittingUtils;

// TODO: Auto-generated Javadoc
/**
 * Represents a plane in 3D space.
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class Plane implements Cloneable {

    /**
     * XY plane.
     */
    public static final Plane XY_PLANE = new Plane(new Vector3D(0, 0, 1), 1);
    /**
     * XZ plane.
     */
    public static final Plane XZ_PLANE = new Plane(new Vector3D(0, 1, 0), 1);
    /**
     * YZ plane.
     */
    public static final Plane YZ_PLANE = new Plane(new Vector3D(1, 0, 0), 1);

    /**
     * Normal vector.
     */
    public final Vector3D normal;
    /**
     * Distance to origin.
     */
    public final double dist;

    /**
     * Constructor. Creates a new plane defined by its normal vector and the
     * distance to the origin.
     *
     * @param normal plane normal
     * @param dist distance from origin
     */
    public Plane(Vector3D normal, double dist) {
        this.normal = normal.normalize();
        this.dist = dist;
    }

    public Vector3D getNormal() {
        return normal;
    }

    public double getDist() {
        return dist;
    }

    public static Plane createFromVertices(List<Vertex> vs) {
        return FittingUtils.sequentials(Vertex.class, vs, 3, true)
                .map(triple -> createFromPoints(triple[0].pos, triple[1].pos, triple[2].pos))
                .reduce((Plane a, Plane b) -> average(a, b))
                .get();
    }

    public static Plane createFromPoints(List<Point3D> vs) {
        return FittingUtils.sequentials(Point3D.class, vs, 3, true)
                .map(triple -> createFromPoints(triple[0], triple[1], triple[2]))
                .reduce((Plane a, Plane b) -> average(a, b))
                .get();
    }

    public static Plane average(Plane a, Plane b) {
        return new Plane(a.getNormal().plus(b.getNormal()).normalize(), (a.getDist() + b.getDist()) / 2);
    }

    /**
     * Creates a plane defined by the the specified points.
     *
     * @param a first point
     * @param b second point
     * @param c third point
     * @return a plane
     */
    public static Plane createFromPoints(Point3D a, Point3D b, Point3D c) {
        Vector3D n = new Vector3D(a, b).cross(new Vector3D(a, c)).normalize();
        return new Plane(n, n.dot(new Vector3D(a)));
    }

    /**
     * Flips this plane.
     */
    public Plane flip() {
        return new Plane(normal.opposite(), -dist);
    }

    public void splitPolygons(
            List<Polygon> polygons,
            List<Polygon> coplanarFront,
            List<Polygon> coplanarBack,
            List<Polygon> front,
            List<Polygon> back) {
        polygons.stream().parallel().forEach(polygon -> splitPolygon(polygon, coplanarFront, coplanarBack, front, back));
    }

    public static final int COPLANAR = 0;
    public static final int FRONT = 1;
    public static final int BACK = 2;
    public static final int SPANNING = 3;

    /**
     * Splits a {@link Polygon} by this plane if needed. After that it puts the
     * polygons or the polygon fragments in the appropriate lists
     * ({@code front}, {@code back}). Coplanar polygons go into either
     * {@code coplanarFront}, {@code coplanarBack} depending on their
     * orientation with respect to this plane. Polygons in front or back of this
     * plane go into either {@code front} or {@code back}.
     *
     * @param polygon polygon to split
     * @param coplanarFront "coplanar front" polygons
     * @param coplanarBack "coplanar back" polygons
     * @param front front polygons
     * @param back back polgons
     */
    public void splitPolygon(
            Polygon polygon,
            List<Polygon> coplanarFront,
            List<Polygon> coplanarBack,
            List<Polygon> front,
            List<Polygon> back) {

        // Classify each point as well as the entire polygon into one of the above
        // four classes.
        int polygonType = 0;
        List<Integer> types = new ArrayList<>(polygon.vertices.size());
        for (int i = 0; i < polygon.vertices.size(); i++) {
            double t = this.normal.dot(new Vector3D(polygon.vertices.get(i).pos)) - this.dist;
            int type = (t < -Tolerance2D.get()) ? BACK : (t > Tolerance2D.get()) ? FRONT : COPLANAR;
            polygonType |= type;
            types.add(type);
        }

        //System.out.println("> switching");
        // Put the polygon in the correct list, splitting it when necessary.
        switch (polygonType) {
            case COPLANAR:
                //System.out.println(" -> coplanar");
                (this.normal.dot(polygon.plane.normal) > 0 ? coplanarFront : coplanarBack).add(polygon);
                break;
            case FRONT:
                //System.out.println(" -> front");
                front.add(polygon);
                break;
            case BACK:
                //System.out.println(" -> back");
                back.add(polygon);
                break;
            case SPANNING:
                //System.out.println(" -> spanning");
                List<Vertex> f = new ArrayList<>(polygon.vertices.size());
                List<Vertex> b = new ArrayList<>(polygon.vertices.size());
                for (int i = 0; i < polygon.vertices.size(); i++) {
                    int j = (i + 1) % polygon.vertices.size();
                    int ti = types.get(i);
                    int tj = types.get(j);
                    Vertex vi = polygon.vertices.get(i);
                    Vertex vj = polygon.vertices.get(j);
                    if (ti != BACK) {
                        f.add(vi);
                    }
                    if (ti != FRONT) {
                        b.add(vi);
                    }
                    if ((ti | tj) == SPANNING) {
                        double t = (this.dist - this.normal.dot(new Vector3D(vi.pos))) / this.normal.dot(new Vector3D(vi.pos, vj.pos));
                        Vertex v = vi.interpolate(vj, t);
                        f.add(v);
                        b.add(v);
                    }
                }
                if (f.size() >= 3) {
                    front.add(new Polygon(f));
                }
                if (b.size() >= 3) {
                    back.add(new Polygon(b));
                }
                break;
        }
    }

    public int categorise(
            Polygon polygon, List<Integer> types) {
        // Classify each point as well as the entire polygon into one of the above
        // four classes.
        int polygonType = 0;
        for (int i = 0; i < polygon.vertices.size(); i++) {
            double t = this.normal.dot(new Vector3D(polygon.vertices.get(i).pos)) - this.dist;
            int type = (t < -Tolerance2D.get()) ? BACK : (t > Tolerance2D.get()) ? FRONT : COPLANAR;
            polygonType |= type;
            types.add(type);
        }
        return polygonType;
    }

    public double tolerance(Polygon polygon) {
        double tolerance = 0.0;
        for (int i = 0; i < polygon.vertices.size(); i++) {
            double t = this.normal.dot(new Vector3D(polygon.vertices.get(i).pos)) - this.dist;
            tolerance = Math.max(tolerance, Math.abs(t));
        }
        return tolerance;
    }
}
