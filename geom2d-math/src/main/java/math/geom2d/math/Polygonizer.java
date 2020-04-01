/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package math.geom2d.math;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import math.geom2d.Angle2D;
import math.geom2d.Point2D;
import math.geom2d.Tolerance2D;
import math.geom2d.circulinear.CirculinearCurve2D;
import math.geom2d.curve.ContinuousCurve2D;
import math.geom2d.curve.SmoothCurve2D;
import math.geom2d.line.AbstractLine2D;
import math.geom2d.line.LinearElement2D;
import math.geom2d.line.StraightLine2D;
import math.geom2d.polygon.LinearCurve2D;
import math.geom2d.polygon.Polygon2D;
import math.geom2d.polygon.Polyline2D;
import math.geom2d.polygon.SimplePolygon2D;
import math.geom2d.polygon.convhull.JarvisMarch2D;

/**
 *
 * @author peter
 */
public class Polygonizer {

    public static Polygon2D toPolygon(CirculinearCurve2D curve, double maxError, boolean inside) {
        boolean curveCW = Rings2D.isClockwise(curve);
        Collection<Point2D> points = curve.continuousCurves().stream()
                .flatMap(subcurve -> subcurve.smoothPieces().stream())
                .map(sp -> toPolyline(sp, maxError, curveCW == isClockwise(sp) ? inside : !inside).vertices())
                .reduce(new ArrayList<>(), (c1, c2) -> {
                    c1.addAll(c2);
                    return c1;
                });
        return new SimplePolygon2D(removeColinearEdges(removeCoincidentPoints(points)));
    }

    static boolean isClockwise(SmoothCurve2D segment) {
        return segment.curvature(segment.t0() + (segment.t1() - segment.t0()) / 2) < 0;
    }

    public static Polygon2D toPolygon(CirculinearCurve2D curve, double maxError, boolean inside, double jumpDistance) {
        boolean curveCW = Rings2D.isClockwise(curve);
        Collection<Point2D> points = curve.continuousCurves().stream()
                .flatMap(subcurve -> subcurve.smoothPieces().stream())
                .map(sp -> toPolyline(sp, maxError, curveCW == isClockwise(sp) ? inside : !inside).vertices())
                .reduce(new ArrayList<>(), (c1, c2) -> {
                    c1.addAll(c2);
                    return c1;
                });
        return new SimplePolygon2D(removeColinearEdges(wrapWithJumps(removeCoincidentPoints(points), jumpDistance)));
    }

    public static LinearCurve2D toPolyline(SmoothCurve2D segment, double maxError, boolean inside) {
        if (segment instanceof LinearElement2D) {
            return segment.asPolyline(1);
        }
        int n = inside ? 1 : 2;
        double error;
        LinearCurve2D out;
        do {
            out = toPolyline(segment, n, inside);
            error = calculateError(segment, out, inside);
            n++;
        } while (error > maxError);
        return out;
    }

    private static LinearCurve2D toPolyline(SmoothCurve2D segment, int nSubSegments, boolean inside) {
        switch (nSubSegments) {
            case 1:
                return new Polyline2D(segment.firstPoint(), segment.lastPoint());
            case 2:
                if (!inside) {
                    if (segment.tangent(segment.t0()).isColinear(segment.tangent(segment.t1()))) {
                        return toPolyline(segment, 3, inside);
                    }
                    List<AbstractLine2D> lines = new ArrayList<>();
                    lines.add(new StraightLine2D(segment.firstPoint(), segment.tangent(segment.t0())));
                    lines.add(new StraightLine2D(segment.lastPoint(), segment.tangent(segment.t1())));
                    return toPolyline(segment.firstPoint(), lines, segment.lastPoint());
                }
            default:
                if (inside) {
                    return segment.asPolyline(nSubSegments);
                } else {
                    double[] splits = splitPoints(segment, nSubSegments - 2);
                    List<AbstractLine2D> lines = new ArrayList<>();
                    lines.add(new StraightLine2D(segment.firstPoint(), segment.tangent(segment.t0())));
                    for (double split : splits) {
                        lines.add(new StraightLine2D(segment.point(split), segment.tangent(split)));
                    }
                    lines.add(new StraightLine2D(segment.lastPoint(), segment.tangent(segment.t1())));
                    return toPolyline(segment.firstPoint(), lines, segment.lastPoint());
                }
        }
    }

    private static double[] splitPoints(SmoothCurve2D segment, int nPoints) {
        double tEach = (segment.t1() - segment.t0()) / (nPoints + 1);
        double[] out = new double[nPoints];
        for (int i = 0; i < nPoints; i++) {
            out[i] = segment.t0() + (i + 1) * tEach;
        }
        return out;
    }

    private static Polyline2D toPolyline(Point2D start, List<AbstractLine2D> lines, Point2D end) {
        List<Point2D> points = new ArrayList<>();
        points.add(start);
        for (int i = 0; i < lines.size() - 1; i++) {
            points.add(lines.get(i).intersection(lines.get(i + 1)));
        }
        points.add(end);
        return new Polyline2D(points);
    }

    private static double calculateError(SmoothCurve2D segment, LinearCurve2D polyline, boolean inside) {
        return (inside
                ? polyline.edges().stream().map(line -> line.point(line.t0() + (line.t1() - line.t0()) / 2))
                : polyline.vertices().stream())
                .mapToDouble(p -> segment.distance(p)).max().getAsDouble();
    }

    public static LinearCurve2D reduce(LinearCurve2D polyline) {
        return new Polyline2D(removeColinearEdges(removeCoincidentPoints(polyline.vertices())));
    }

    private static Collection<Point2D> removeCoincidentPoints(Collection<Point2D> vertices) {
        Deque<Point2D> newVertices = new ArrayDeque<>(vertices.size());
        Iterator<Point2D> it = vertices.iterator();
        newVertices.addLast(it.next());
        while (it.hasNext()) {
            Point2D next = it.next();
            if (!newVertices.peekLast().almostEquals(next, Tolerance2D.get())) {
                newVertices.addLast(next);
            }
        }
        return newVertices;
    }

    private static Collection<Point2D> removeColinearEdges(Collection<Point2D> vertices) {
        List<Point2D> newVertices = new ArrayList<>(vertices.size());
        Iterator<Point2D> it = vertices.iterator();
        Point2D first = it.next();
        Point2D second = it.next();
        Point2D a = first;
        Point2D b = second;
        while (it.hasNext()) {
            Point2D c = it.next();
            if (!Point2D.isColinear(a, b, c)) {
                newVertices.add(b);
            }
            a = b;
            b = c;
        }
        Point2D c = first;
        if (!Point2D.isColinear(a, b, c)) {
            newVertices.add(b);
        }
        a = b;
        b = c;
        c = second;
        if (!Point2D.isColinear(a, b, c)) {
            newVertices.add(b);
        }
        return newVertices;
    }

    public static List<Point2D> wrapWithJumps(Collection<Point2D> points, double jumpDistance) {
        List<Point2D> pointList = new ArrayList<>(points);
        // Ensure points are ccw
        if (new SimplePolygon2D(points).area() < 0) {
            Collections.reverse(pointList);
        }
        Polygon2D hull = new JarvisMarch2D().convexHull(pointList);
        // Position the list to start with the first point in the hull
        Collections.rotate(pointList, -pointList.indexOf(hull.vertex(0)));
//        List<Point2D> pointList = shiftToLowestPoint(points);
        Deque<Point2D> pointQueue = new ArrayDeque<>(pointList.size());
        pointQueue.addLast(pointList.remove(0));
        while (!pointList.isEmpty()) {
            addNextPoint(pointList, pointQueue, jumpDistance, hull);
        }
        return new ArrayList<>(pointQueue);
    }

    static List<Point2D> shiftToLowestPoint(Collection<? extends Point2D> points) {
        List<Point2D> pointList = new ArrayList<>(points);
        // Ensure points are ccw
        if (new SimplePolygon2D(points).area() < 0) {
            Collections.reverse(pointList);
        }
        // Find index of lowest point
        double y;
        double ymin = Double.MAX_VALUE;
        int nPoint = 0;

        // Iteration on the set of points to find point with lowest y-coord
        for (int i = 0; i < pointList.size(); i++) {
            y = pointList.get(i).getY();
            if (y < ymin) {
                ymin = y;
                nPoint = i;
            }
        }

        // Shuffle the 
        Collections.rotate(pointList, -nPoint);

        return pointList;
    }

    static void addNextPoint(List<Point2D> available, Deque<Point2D> out, double jumpDistance, Polygon2D hull) {
        Point2D previous = out.peekLast();
        int nPoint = findNextIndex(previous, available, jumpDistance, hull);
        // Shift list nPoint to the left
        Point2D point = available.remove(0);
        for (int i = 1; i < nPoint; i++) {
            point = available.remove(0);
        }
        out.addLast(point);
    }

    static int findNextIndex(Point2D previous, List<Point2D> available, double jumpDistance, Polygon2D hull) {
        // Find next member of the hull, we must include all hull members so mustn't skip them
        int iFirstHull;
        for (iFirstHull = 0; iFirstHull < available.size(); iFirstHull++) {
            if (Math.abs(hull.boundary().signedDistance(available.get(iFirstHull))) < Tolerance2D.get()) {
                break;
            }
        }
        // If it is within jumping range, jump to it
        if (iFirstHull < available.size() && available.get(iFirstHull).distance(previous) < jumpDistance) {
            return iFirstHull;
        }
        // Else just continue
        return 0;
    }
}