/**
 * PolygonUtil.java
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
package math.geom3d.csg.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import math.geom3d.Point3D;
import math.geom3d.Vector3D;
import math.geom3d.csg.Extrude;
import math.geom3d.csg.Vertex;
import math.geom3d.poly2tri.DelaunayTriangle;
import math.geom3d.poly2tri.PolygonPoint;
import math.geom3d.poly2tri.TriangulationPoint;

// TODO: Auto-generated Javadoc
/**
 * The Class PolygonUtil.
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class PolygonUtil {

    /**
     * Instantiates a new polygon util.
     */
    private PolygonUtil() {
        throw new AssertionError("Don't instantiate me!", null);
    }

    /**
     * Converts a CSG polygon to a poly2tri polygon (including holes).
     *
     * @param polygon the polygon to convert
     * @return a CSG polygon to a poly2tri polygon (including holes)
     */
    public static math.geom3d.poly2tri.Polygon fromCSGPolygon(
            math.geom3d.csg.Polygon polygon) {

        // convert polygon
        List< PolygonPoint> points = new ArrayList<>();
        polygon.vertices.stream().map((v) -> new PolygonPoint(v.pos.getX(), v.pos.getY(), v.pos.getZ())).forEachOrdered((vp) -> {
            points.add(vp);
        });
        math.geom3d.poly2tri.Polygon result
                = new math.geom3d.poly2tri.Polygon(points);
        return result;
    }

    /**
     * Concave to convex.
     *
     * @param concave the concave
     * @return the list
     */
    public static List<math.geom3d.csg.Polygon> concaveToConvex(
            math.geom3d.csg.Polygon concave) {

        List<math.geom3d.csg.Polygon> result = new ArrayList<>();

        Vector3D normal = concave.vertices.get(0).normal;

        boolean cw = !Extrude.isCCW(concave);

        math.geom3d.poly2tri.Polygon p
                = fromCSGPolygon(concave);

        math.geom3d.poly2tri.Poly2Tri.triangulate(p);

        List<DelaunayTriangle> triangles = p.getTriangles();

        List<Vertex> triPoints = new ArrayList<>();

        for (DelaunayTriangle t : triangles) {

            int counter = 0;
            for (TriangulationPoint tp : t.points) {

                triPoints.add(new Vertex(
                        new Point3D(tp.getX(), tp.getY(), tp.getZ()),
                        normal));

                if (counter == 2) {
                    if (!cw) {
                        Collections.reverse(triPoints);
                    }
                    math.geom3d.csg.Polygon poly
                            = new math.geom3d.csg.Polygon(
                                    triPoints);
                    result.add(poly);
                    counter = 0;
                    triPoints = new ArrayList<>();

                } else {
                    counter++;
                }
            }
        }

        return result;
    }

    /**
     * Converts a DelaunayTriangle to a standard Polygon
     *
     * @param dt
     *
     * @return the coordinates of the point as a Vector3d
     */
    public static math.geom3d.csg.Polygon toPolygon(math.geom3d.poly2tri.DelaunayTriangle dt) {
        Vector3D normal = new Vector3D(0, 0, 1);
        Point3D p0 = new Point3D(dt.points[0].getX(), dt.points[0].getY(), dt.points[0].getZ());
        Point3D p1 = new Point3D(dt.points[1].getX(), dt.points[1].getY(), dt.points[1].getZ());
        Point3D p2 = new Point3D(dt.points[2].getX(), dt.points[2].getY(), dt.points[2].getZ());
        math.geom3d.csg.Vertex v0 = new math.geom3d.csg.Vertex(p0, normal);
        math.geom3d.csg.Vertex v1 = new math.geom3d.csg.Vertex(p1, normal);
        math.geom3d.csg.Vertex v2 = new math.geom3d.csg.Vertex(p2, normal);
        return new math.geom3d.csg.Polygon(v0, v1, v2);
    }
}
