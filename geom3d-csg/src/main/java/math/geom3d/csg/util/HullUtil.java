/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package math.geom3d.csg.util;

import java.util.ArrayList;
import java.util.List;
import math.geom3d.Point3D;
import math.geom3d.csg.CSG;
import math.geom3d.csg.Polygon;
import math.geom3d.quickhull.Point3d;
import math.geom3d.quickhull.QuickHull3D;
import math.geom3d.quickhull.QuickHullException;

// TODO: Auto-generated Javadoc
/**
 * The Class HullUtil.
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class HullUtil {

    /**
     * Instantiates a new hull util.
     */
    private HullUtil() {
        throw new AssertionError("Don't instantiate me!", null);
    }

    /**
     * Hull.
     *
     * @param points the points
     * @return the csg
     * @throws math.geom3d.quickhull.QuickHullException
     */
    public static CSG hull(List<Point3D> points) throws QuickHullException {
        Point3d[] hullPoints = points.stream().map((vec) -> new Point3d(vec.getX(), vec.getY(), vec.getZ())).toArray(Point3d[]::new);
        QuickHull3D hull = new QuickHull3D();
        hull.build(hullPoints);
        hull.triangulate();
        int[][] faces = hull.getFaces();
        List<Polygon> polygons = new ArrayList<>();
        List<Point3D> vertices = new ArrayList<>();
        for (int[] verts : faces) {
            for (int i : verts) {
                vertices.add(points.get(hull.getVertexPointIndices()[i]));
            }
            polygons.add(Polygon.fromPoints(vertices));
            vertices.clear();
        }
        return CSG.fromPolygons(polygons);
    }

    /**
     * Hull.
     *
     * @param csg the csg
     * @return the csg
     * @throws math.geom3d.quickhull.QuickHullException
     */
    public static CSG hull(CSG csg) throws QuickHullException {
        List<Point3D> points = new ArrayList<>(csg.getPolygons().size() * 3);
        csg.getPolygons().forEach((p) -> p.vertices.forEach((v) -> points.add(v.pos)));
        return hull(points);
    }

    /**
     * Hull.
     *
     * @param csgList
     * @return the csg
     * @throws math.geom3d.quickhull.QuickHullException
     */
    public static CSG hull(CSG... csgList) throws QuickHullException {
        List<Point3D> points = new ArrayList<>();
        for (CSG csg : csgList) {
            csg.getPolygons().forEach((p) -> p.vertices.forEach((v) -> points.add(v.pos)));
        }
        return hull(points);
    }
}
