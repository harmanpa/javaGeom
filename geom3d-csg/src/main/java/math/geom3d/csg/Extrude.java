/**
 * Extrude.java
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
import math.geom2d.Point2D;
import math.geom2d.bezier.BezierPath;
import math.geom3d.Point3D;
import math.geom3d.Vector3D;
import math.geom3d.csg.util.HullUtil;
import math.geom3d.csg.util.PolygonUtil;
import math.geom3d.quickhull.QuickHullException;

//import javax.vecmath.Vector3d;
// TODO: Auto-generated Javadoc
/**
 * Extrudes concave and convex polygons.
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class Extrude {

    /**
     * Extrudes the specified path (convex or concave polygon without holes or
     * intersections, specified in CCW) into the specified direction.
     *
     * @param dir direction
     * @param points path (convex or concave polygon without holes or
     * intersections)
     *
     * @return a CSG object that consists of the extruded polygon
     */
    public static CSG points(Vector3D dir, List<Point3D> points) {

        List<Point3D> newList = new ArrayList<>(points);

        return extrude(dir, Polygon.fromPoints(toCCW(newList)));
    }

    /**
     * Extrude.
     *
     * @param dir the dir
     * @param polygon1 the polygon1
     * @return the csg
     */
    public static CSG extrude(Vector3D dir, Polygon polygon1) {
        return monotoneExtrude(dir, polygon1);
    }

    private static CSG monotoneExtrude(Vector3D dir, Polygon polygon1) {
        List<Polygon> newPolygons = new ArrayList<>();
        CSG extrude;

        newPolygons.addAll(PolygonUtil.concaveToConvex(polygon1));
        Polygon polygon2 = polygon1.translate(dir);

        int numvertices = polygon1.vertices.size();
        for (int i = 0; i < numvertices; i++) {

            int nexti = (i + 1) % numvertices;

            Point3D bottomV1 = polygon1.vertices.get(i).pos;
            Point3D topV1 = polygon2.vertices.get(i).pos;
            Point3D bottomV2 = polygon1.vertices.get(nexti).pos;
            Point3D topV2 = polygon2.vertices.get(nexti).pos;

            List<Point3D> pPoints = Arrays.asList(bottomV2, topV2, topV1, bottomV1);

            newPolygons.add(Polygon.fromPoints(pPoints));

        }

        polygon2 = polygon2.flip();
        List<Polygon> topPolygons = PolygonUtil.concaveToConvex(polygon2);

        newPolygons.addAll(topPolygons);
        extrude = CSG.fromPolygons(newPolygons);

        return extrude;
    }

    public static CSG extrude(Vector3D dir, List<Point3D> points) {
        return points(dir, points);
    }

    /**
     * Instantiates a new extrude.
     */
    private Extrude() {
        throw new AssertionError("Don't instantiate me!", null);
    }

    public static CSG polygons(Polygon polygon1, Polygon polygon2) {
        // if(!isCCW(polygon1)) {
        // polygon1=Polygon.fromPoints(toCCW(polygon1.getPoints()));
        // }
        // if(!isCCW(polygon2)) {
        // polygon2=Polygon.fromPoints(toCCW(polygon2.getPoints()));
        // }

        List<Polygon> newPolygons = new ArrayList<>();
        CSG extrude;
        newPolygons.addAll(PolygonUtil.concaveToConvex(polygon1));
        if (polygon1.vertices.size() != polygon2.vertices.size()) {
            throw new RuntimeException("These polygons do not match");
        }

        int numvertices = polygon1.vertices.size();
        for (int i = 0; i < numvertices; i++) {

            int nexti = (i + 1) % numvertices;

            Point3D bottomV1 = polygon1.vertices.get(i).pos;
            Point3D topV1 = polygon2.vertices.get(i).pos;
            Point3D bottomV2 = polygon1.vertices.get(nexti).pos;
            Point3D topV2 = polygon2.vertices.get(nexti).pos;

            List<Point3D> pPoints = Arrays.asList(bottomV2, topV2, topV1, bottomV1);

            newPolygons.add(Polygon.fromPoints(pPoints));

        }

        polygon2 = polygon2.flip();
        List<Polygon> topPolygons = PolygonUtil.concaveToConvex(polygon2);

        newPolygons.addAll(topPolygons);
        extrude = CSG.fromPolygons(newPolygons);

        return extrude;
    }

    public static ArrayList<CSG> polygons(Polygon polygon1, ArrayList<Transform> transforms) {

        polygon1 = Polygon.fromPoints(toCCW(polygon1.getPoints()));
        if (transforms.size() < 2) {
            transforms.add(0, new Transform());
        }
        ArrayList<CSG> parts = new ArrayList<>();
        Transform transform = new Transform();
        //transform.rotY(90);
        for (int i = 0; i < transforms.size() - 1; i++) {
            CSG tmp = polygons(polygon1.transform(transform).transform(transforms.get(i)),
                    polygon1.transform(transform).transform(transforms.get(i + 1)));
            parts.add(tmp);
        }
        return parts;

    }

    public static ArrayList<CSG> polygons(Polygon polygon1, Transform... transformparts) {
        return polygons(polygon1, (ArrayList<Transform>) Arrays.asList(transformparts));
    }

    /**
     * Extrudes the specified path (convex or concave polygon without holes or
     * intersections, specified in CCW) into the specified direction.
     *
     * @param dir direction
     * @param points path (convex or concave polygon without holes or
     * intersections)
     *
     * @return a CSG object that consists of the extruded polygon
     */
    public static CSG points(Vector3D dir, Point3D... points) {

        return points(dir, Arrays.asList(points));
    }

    /**
     * To ccw.
     *
     * @param points the points
     * @return the list
     */
    public static List<Point3D> toCCW(List<Point3D> points) {
        List<Point3D> result = new ArrayList<>(points);
        if (!isCCW(Polygon.fromPoints(result))) {
            Collections.reverse(result);
        }
        return result;
    }

    /**
     * To cw.
     *
     * @param points the points
     * @return the list
     */
    static List<Point3D> toCW(List<Point3D> points) {

        List<Point3D> result = new ArrayList<>(points);

        if (isCCW(Polygon.fromPoints(result))) {
            Collections.reverse(result);
        }

        return result;
    }

    /**
     * Checks if is ccw.
     *
     * @param polygon the polygon
     * @return true, if is ccw
     */
    public static boolean isCCW(Polygon polygon) {

        // thanks to Sepp Reiter for explaining me the algorithm!
        if (polygon.vertices.size() < 3) {
            throw new IllegalArgumentException("Only polygons with at least 3 vertices are supported!");
        }

        // search highest left vertex
        int highestLeftVertexIndex = 0;
        Vertex highestLeftVertex = polygon.vertices.get(0);
        for (int i = 0; i < polygon.vertices.size(); i++) {
            Vertex v = polygon.vertices.get(i);

            if (v.pos.getY() > highestLeftVertex.pos.getY()) {
                highestLeftVertex = v;
                highestLeftVertexIndex = i;
            } else if (v.pos.getY() == highestLeftVertex.pos.getY() && v.pos.getX() < highestLeftVertex.pos.getX()) {
                highestLeftVertex = v;
                highestLeftVertexIndex = i;
            }
        }

        // determine next and previous vertex indices
        int nextVertexIndex = (highestLeftVertexIndex + 1) % polygon.vertices.size();
        int prevVertexIndex = highestLeftVertexIndex - 1;
        if (prevVertexIndex < 0) {
            prevVertexIndex = polygon.vertices.size() - 1;
        }
        Vertex nextVertex = polygon.vertices.get(nextVertexIndex);
        Vertex prevVertex = polygon.vertices.get(prevVertexIndex);

        // edge 1
        double a1 = normalizedX(highestLeftVertex.pos, nextVertex.pos);

        // edge 2
        double a2 = normalizedX(highestLeftVertex.pos, prevVertex.pos);

        // select vertex with lowest x value
        int selectedVIndex;

        if (a2 > a1) {
            selectedVIndex = nextVertexIndex;
        } else {
            selectedVIndex = prevVertexIndex;
        }

        if (selectedVIndex == 0 && highestLeftVertexIndex == polygon.vertices.size() - 1) {
            selectedVIndex = polygon.vertices.size();
        }

        if (highestLeftVertexIndex == 0 && selectedVIndex == polygon.vertices.size() - 1) {
            highestLeftVertexIndex = polygon.vertices.size();
        }

        // indicates whether edge points from highestLeftVertexIndex towards
        // the sel index (ccw)
        return selectedVIndex > highestLeftVertexIndex;

    }

    /**
     * Normalized x.
     *
     * @param v1 the v1
     * @param v2 the v2
     * @return the double
     */
    private static double normalizedX(Point3D v1, Point3D v2) {
        Vector3D v2MinusV1 = new Vector3D(v1, v2);

        return v2MinusV1.normalize().cross(new Vector3D(1, 0, 0)).getX();
    }

    public static CSG byPath(List<List<Point3D>> points, double height) {
        return byPath(points, height, 200);
    }

    public static CSG byPath(List<List<Point3D>> points, double height, int resolution) {
        List<Transform> trPath = pathToTransforms(points, resolution);
        List<Point3D> finalPath = new ArrayList<>();
        trPath.stream().map((tr) -> {
            javax.vecmath.Vector3d t1 = new javax.vecmath.Vector3d();
            tr.getInternalMatrix().get(t1);
            return t1;
        }).map((t1) -> new Point3D(t1.getX(), t1.getY(), 0)).forEachOrdered((finalPoint) -> {
            finalPath.add(finalPoint);
        });
        return Extrude.points(new Vector3D(0, 0, height), finalPath);
    }

    public static List<Transform> pathToTransforms(List<List<Point3D>> points, int resolution) {
        Point3D start = points.get(0).get(0);
        String pathStringA = "M " + start.getX() + "," + start.getY();
        String pathStringB = pathStringA;

        for (List<Point3D> sections : points) {
            if (sections.size() == 4) {
                Point3D controlA = sections.get(1);
                Point3D controlB = sections.get(2);
                Point3D endPoint = sections.get(3);
                /*
				 * ArrayList<Double> controlA = (ArrayList<Double>)
				 * Arrays.asList(sections.get(1).getX() - start.get(0), sections.get(1).getY() -
				 * start.get(1), sections.get(1).getZ() - start.get(2));
				 * 
				 * ArrayList<Double> controlB = (ArrayList<Double>)
				 * Arrays.asList(sections.get(2).getX() - start.get(0), sections.get(2).getY() -
				 * start.get(1), sections.get(2).getZ() - start.get(2)); ; ArrayList<Double> endPoint
				 * = (ArrayList<Double>) Arrays.asList(sections.get(3).getX() - start.get(0),
				 * sections.get(3).getY() - start.get(1), sections.get(3).getZ() - start.get(2)); ;
                 */

                pathStringA += ("C " + controlA.getX() + "," + controlA.getY() + " " + controlB.getX() + "," + controlB.getY() + " "
                        + endPoint.getX() + "," + endPoint.getY() + "\n");
                pathStringB += ("C " + controlA.getX() + "," + controlA.getZ() + " " + controlB.getX() + "," + controlB.getZ() + " "
                        + endPoint.getX() + "," + endPoint.getZ() + "\n");
                // start.set(0, sections.get(3).getX());
                // start.set(1, sections.get(3).getY());
                // start.set(2,sections.get(3).getZ());

            } else if (sections.size() == 1) {

                pathStringA += "L " + (double) sections.get(0).getX() + "," + (double) sections.get(0).getY() + "\n";
                pathStringB += "L " + (double) sections.get(0).getX() + "," + (double) sections.get(0).getZ() + "\n";
                // start.set(0, sections.get(0).getX());
                // start.set(1, sections.get(0).getY());
                // start.set(2, sections.get(0).getZ());
            }
        }
        // println "A string = " +pathStringA
        // println "B String = " +pathStringB
        BezierPath path = new BezierPath();
        path.parsePathString(pathStringA);
        BezierPath path2 = new BezierPath();
        path2.parsePathString(pathStringB);

        return bezierToTransforms(path, path2, resolution);
    }

    public static List<CSG> moveAlongProfile(CSG object, List<List<Point3D>> points, int resolution) {
        return Extrude.move(object, pathToTransforms(points, resolution));
    }

    public static List<Transform> bezierToTransforms(Point3D controlA, Point3D controlB, Point3D endPoint,
            int iterations) {
        BezierPath path = new BezierPath();
        path.parsePathString("C " + controlA.getX() + "," + controlA.getY() + " " + controlB.getX() + "," + controlB.getY() + " "
                + endPoint.getX() + "," + endPoint.getY());
        BezierPath path2 = new BezierPath();
        path2.parsePathString("C " + controlA.getX() + "," + controlA.getZ() + " " + controlB.getX() + "," + controlB.getZ() + " "
                + endPoint.getX() + "," + endPoint.getZ());
        return bezierToTransforms(path, path2, iterations);
    }

    public static List<Transform> bezierToTransforms(List<Point3D> parts, int iterations) {
        // System.out.println("Bezier type "+parts.size());
        if (parts.size() == 3) {
            return bezierToTransforms(parts.get(0), parts.get(1), parts.get(2), iterations);
        }
        if (parts.size() == 2) {
            return bezierToTransforms(parts.get(0), parts.get(0), parts.get(1), parts.get(1), iterations);
        }
        if (parts.size() == 1) {
            return bezierToTransforms(new Point3D(0, 0, 0), new Point3D(0, 0, 0), parts.get(0), parts.get(0),
                    iterations);
        }
        return bezierToTransforms(parts.get(0), parts.get(1), parts.get(2), parts.get(3), iterations);
    }

    public static List<Transform> bezierToTransforms(BezierPath pathA, BezierPath pathB, int iterations) {
        List<Transform> p = new ArrayList<>();
        Point2D pointAStart = pathA.eval(0);
        Point2D pointBStart = pathB.eval(0);
        double x = pointAStart.getX(), y = pointAStart.getY(), z = pointBStart.getY();
        double lastx = x, lasty = y, lastz = z;
        // float min = (float) 0.0001;
        for (int i = 0; i < iterations - 1; i++) {
            float pathFunction = (float) (((float) i) / ((float) (iterations - 1)));

            Point2D pointA = pathA.eval(pathFunction);
            Point2D pointB = pathB.eval(pathFunction);

            x = pointA.getX();
            y = pointA.getY();
            z = pointB.getY();

            Transform t = new Transform();
            t.translateX(x);
            t.translateY(y);
            t.translateZ(z);

            Point2D pointAEst = pathA.eval((pathFunction + (1.0 / (double) iterations)));
            Point2D pointBEst = pathB.eval((pathFunction + (1.0 / (double) iterations)));
            double xest = pointAEst.getX();
            double yest = pointAEst.getY();
            double zest = pointBEst.getY();
            double ydiff = yest - y;
            double zdiff = zest - z;
            double xdiff = xest - x;
            // t.rotX(45-Math.toDegrees(Math.atan2(zdiff,ydiff)))

            double rise = zdiff;
            double run = Math.sqrt((ydiff * ydiff) + (xdiff * xdiff));
            double rotz = 90 - Math.toDegrees(Math.atan2(xdiff, ydiff));
            // System.out.println("Rot z = "+rotz+" x="+xdiff+" y="+ydiff);
            double roty = Math.toDegrees(Math.atan2(rise, run));

            t.rotZ(-rotz);
            t.rotY(roty);
            // if(i==0)
            // System.out.println( " Tr = "+x+" "+y+" "+z+" path = "+pathFunction);
            // println "z = "+rotz+" y = "+roty
            p.add(t);
            lastx = x;
            lasty = y;
            lastz = z;
        }
        Point2D pointA = pathA.eval(1);
        Point2D pointB = pathB.eval(1);

        x = pointA.getX();
        y = pointA.getY();
        z = pointB.getY();
        Transform t = new Transform();
        t.translateX(x);
        t.translateY(y);
        t.translateZ(z);

        double ydiff = y - lasty;
        double zdiff = z - lastz;
        double xdiff = x - lastx;

        double rise = zdiff;
        double run = Math.sqrt((ydiff * ydiff) + (xdiff * xdiff));
        double rotz = 90 - Math.toDegrees(Math.atan2(xdiff, ydiff));
        double roty = Math.toDegrees(Math.atan2(rise, run));

        t.rotZ(-rotz);
        t.rotY(roty);
        p.add(t);

        return p;
    }

    public static List<Transform> bezierToTransforms(Point3D start, Point3D controlA, Point3D controlB,
            Point3D endPoint, int iterations) {
        String startString = "M " + start.getX() + "," + start.getY() + "\n" + "C " + controlA.getX() + "," + controlA.getY() + " "
                + controlB.getX() + "," + controlB.getY() + " " + endPoint.getX() + "," + endPoint.getY();
        String b = "M " + start.getX() + "," + start.getZ() + "\n" + "C " + controlA.getX() + "," + controlA.getZ() + " " + controlB.getX() + ","
                + controlB.getZ() + " " + endPoint.getX() + "," + endPoint.getZ();
        // println "Start = "+startString
        BezierPath path = new BezierPath();
        path.parsePathString(startString);
        BezierPath path2 = new BezierPath();
        path2.parsePathString(b);
        // newParts.remove(parts.size()-1)
        // newParts.remove(0)
        // System.out.println("Parsing "+startString+" \nand\n"+b);
        return bezierToTransforms(path, path2, iterations);
    }

    public static List<CSG> revolve(CSG slice, double radius, int numSlices) throws QuickHullException {
        return revolve(slice, radius, 360.0, null, numSlices);
    }

    public static List<CSG> revolve(CSG slice, double radius, double archLen, int numSlices) throws QuickHullException {
        return revolve(slice, radius, archLen, null, numSlices);
    }

    public static List<CSG> revolve(CSG slice, double radius, double archLen, List<List<Point3D>> points,
            int numSlices) throws QuickHullException {
        List<CSG> parts = new ArrayList<>();
        double increment = archLen / ((double) numSlices);
        CSG slicePRofile = slice.movey(radius);
        for (int i = 0; i < archLen + increment; i += increment) {
            parts.add(slicePRofile.rotz(i));
        }
        if (points != null) {
            List<Transform> pathtransforms = pathToTransforms(points, numSlices);
            for (int i = 0; i < parts.size(); i++) {
                CSG sweep = parts.get(i).transformed(pathtransforms.get(i));
                parts.set(i, sweep);
            }
        }
        for (int i = 0; i < parts.size() - 1; i++) {
            CSG sweep = CSG.hullAll(parts.get(i), parts.get(i + 1));
            parts.set(i, sweep);
        }
        return parts;
    }

    public static List<CSG> bezier(CSG slice, List<Double> controlA, List<Double> controlB,
            List<Double> endPoint, int numSlices) throws QuickHullException {
        List<CSG> parts = new ArrayList<>();
        for (int i = 0; i < numSlices; i++) {
            parts.add(0, slice);
        }
        return bezier(parts, controlA, controlB, endPoint);
    }

    public static List<CSG> bezier(List<CSG> s, List<Double> controlA, List<Double> controlB,
            List<Double> endPoint) throws QuickHullException {
        List<CSG> slice = moveBezier(s, controlA, controlB, endPoint);
        for (int i = 0; i < slice.size() - 1; i++) {
            // Polygon p1 =Slice.slice(slice.get(i), new Transform(), 0).get(0);
            // Polygon p2 =Slice.slice(slice.get(i+1), new Transform(), 0).get(0);
            // CSG sweep = polygons(p1, p2);
            CSG sweep = HullUtil.hull(slice.get(i), slice.get(i + 1));
            slice.set(i, sweep);
        }
        return slice;
    }

    public static List<CSG> linear(List<CSG> s, List<Double> endPoint) throws QuickHullException {
        List<Double> start = (ArrayList<Double>) Arrays.asList(0.0, 0.0, 0.0);
        return bezier(s, start, endPoint, endPoint);
    }

    public static List<CSG> linear(CSG s, List<Double> endPoint, int numSlices) throws QuickHullException {
        List<Double> start = (ArrayList<Double>) Arrays.asList(0.0, 0.0, 0.0);
        return bezier(s, start, endPoint, endPoint, numSlices);
    }

    public static List<CSG> move(List<CSG> slice, List<Transform> p) {
        return CSG.move(slice, p);
    }

    public static List<CSG> move(CSG slice, List<Transform> p) {
        return slice.move(p);
    }

    public static List<CSG> moveBezier(CSG slice, List<Double> controlA, List<Double> controlB,
            List<Double> endPoint, int numSlices) {
        List<Transform> p = bezierToTransforms(fromDouble(controlA), fromDouble(controlB), fromDouble(endPoint),
                numSlices);
        return move(slice, p);
    }

    public static List<CSG> moveBezier(List<CSG> slice, List<Double> controlA,
            List<Double> controlB, List<Double> endPoint) {
        int numSlices = slice.size();
        List<Transform> p = bezierToTransforms(fromDouble(controlA), fromDouble(controlB), fromDouble(endPoint),
                numSlices);
        return move(slice, p);
    }

    private static Point3D fromDouble(List<Double> controlA) {
        return new Point3D(controlA.get(0), controlA.get(1), controlA.get(2));
    }

    public static List<CSG> moveBezier(CSG slice, BezierPath pathA, int numSlices) {
        Point2D pointA = pathA.eval(1.0);
        String zpath = "C 0,0 " + pointA.getX() + "," + pointA.getY() + " " + pointA.getX() + "," + pointA.getY();
        BezierPath pathB = new BezierPath();
        pathB.parsePathString(zpath);
        return moveBezier(slice, pathA, pathB, numSlices);
    }

    public static List<CSG> moveBezier(CSG slice, BezierPath pathA, BezierPath pathB, int iterations) {
        List<Transform> p = bezierToTransforms(pathA, pathB, iterations);
        return move(slice, p);
    }
}
