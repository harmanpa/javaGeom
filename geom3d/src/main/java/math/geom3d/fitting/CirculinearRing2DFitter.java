/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package math.geom3d.fitting;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import math.geom2d.Angle2D;
import math.geom2d.Point2D;
import math.geom2d.Tolerance2D;
import math.geom2d.Vector2D;
import math.geom2d.circulinear.CirculinearElement2D;
import math.geom2d.circulinear.CirculinearRing2D;
import math.geom2d.circulinear.GenericCirculinearRing2D;
import math.geom2d.conic.Circle2D;
import math.geom2d.conic.CircleArc2D;
import math.geom2d.conic.CircularShape2D;
import math.geom2d.exceptions.ColinearPoints2DException;
import math.geom2d.line.LineSegment2D;
import math.geom2d.polygon.LinearRing2D;
import math.geom2d.polygon.Polylines2D;

/**
 *
 * @author peter
 */
public class CirculinearRing2DFitter {

    public CirculinearRing2D fit(List<Point2D> points, double maxError) {
        Deque<CirculinearElement2D> elements = FittingUtils.sequentials(Point2D.class, Polylines2D.simplifyPolyline(points, Tolerance2D.get()), 3, true, -1)
                .map(triplet -> fitCircle(triplet))
                .reduce(new ArrayDeque<>(), new PointsAndCircleReducer(maxError), (Deque<CirculinearElement2D> a, Deque<CirculinearElement2D> b) -> {
                    a.addAll(b);
                    return a;
                });
        return make(elements);
    }
    
    private CirculinearRing2D make(Deque<CirculinearElement2D> elements) {
        if(elements.size()==1 && elements.getFirst() instanceof Circle2D) {
            return (Circle2D)elements.getFirst();
        } else if(elements.stream().allMatch(element -> element instanceof LineSegment2D)) {
            return new LinearRing2D(elements.stream().map(element -> element.firstPoint()).collect(Collectors.toList()));
        } else {
            return new GenericCirculinearRing2D(elements);
        }
    }

    private PointsAndCircle fitCircle(Point2D[] points) {
        try {
            return new PointsAndCircle(points, Circle2D.circumCircle(points[0], points[1], points[2]));
        } catch (ColinearPoints2DException ex) {
            return new PointsAndCircle(points);
        }
    }

    static class PointsAndCircleReducer implements BiFunction<Deque<CirculinearElement2D>, PointsAndCircle, Deque<CirculinearElement2D>> {

        private final double maxError;

        public PointsAndCircleReducer(double maxError) {
            this.maxError = maxError;
        }

        @Override
        public Deque<CirculinearElement2D> apply(Deque<CirculinearElement2D> list, PointsAndCircle points) {
            points.append(list, maxError);
            return list;
        }

    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    static class PointsAndCircle {

        private final Point2D[] points;
        private final Circle2D circle;
        private final double error;
        private final double angle;

        PointsAndCircle(Point2D[] points) {
            this(points, null);
        }

        PointsAndCircle(Point2D[] points, Circle2D circle) {
            this.points = points;
            this.circle = circle;
            this.error = circle == null ? 0.0 : Math.max(
                    FittingUtils.error(circle, points[0], points[1]),
                    FittingUtils.error(circle, points[1], points[2]));
            this.angle = Angle2D.angle(new Vector2D(points[0], points[1]), new Vector2D(points[1], points[2]));
        }

        public Point2D[] getPoints() {
            return points;
        }

        public Circle2D getCircle() {
            return circle;
        }

        public double getError() {
            return error;
        }

        public double getAngle() {
            return angle;
        }

        public void append(Deque<CirculinearElement2D> list, double maxError) {
            CirculinearElement2D last = list.peekLast();
            CirculinearElement2D first = list.peekFirst();
            if (circle != null && error < maxError) {
                CircleArc2D arc = makeArc(circle, points[1], points[2]);
                if (last != null
                        && last instanceof CircleArc2D
                        && ((CircleArc2D) last).supportingCircle().almostEquals(arc.supportingCircle(), Tolerance2D.get())) {
                    list.removeLast();
                    if (first != null
                            && first.firstPoint().almostEquals(points[2], Tolerance2D.get())
                            && first instanceof CircleArc2D
                            && ((CircleArc2D) first).supportingCircle().almostEquals(arc.supportingCircle(), Tolerance2D.get())) {
                        if(list.isEmpty()) {
                            list.addLast(makeArc((CircleArc2D) last, arc));
                        } else {
                            list.removeFirst();
                            list.addLast(makeArc((CircleArc2D) last, arc, (CircleArc2D) first));
                        }
                    } else {
                        list.addLast(makeArc((CircleArc2D) last, arc));
                    }
                } else {
                    if (first != null
                            && first.firstPoint().almostEquals(points[2], Tolerance2D.get())
                            && first instanceof CircleArc2D
                            && ((CircleArc2D) first).supportingCircle().almostEquals(arc.supportingCircle(), Tolerance2D.get())) {
                        list.removeFirst();
                        list.addLast(makeArc(arc, (CircleArc2D) first));
                    } else {
                        list.addLast(arc);
                    }
                }
            } else {
                LineSegment2D line = makeLine(points[1], points[2]);
                if (last != null
                        && last instanceof LineSegment2D
                        && ((LineSegment2D) last).direction().almostEquals(line.direction(), Tolerance2D.get())) {
                    list.removeLast();
                    if (first != null
                            && first.firstPoint().almostEquals(points[2], Tolerance2D.get())
                            && first instanceof LineSegment2D
                            && ((LineSegment2D) first).direction().almostEquals(line.direction(), Tolerance2D.get())) {
                        list.removeFirst();
                        list.addLast(makeLine(last.firstPoint(), first.lastPoint()));
                    } else {
                        list.addLast(makeLine(last.firstPoint(), points[2]));
                    }
                } else {
                    if (first != null
                            && first.firstPoint().almostEquals(points[2], Tolerance2D.get())
                            && first instanceof LineSegment2D
                            && ((LineSegment2D) first).direction().almostEquals(line.direction(), Tolerance2D.get())) {
                        list.removeFirst();
                        list.addLast(makeLine(points[1], first.lastPoint()));
                    } else {
                        list.addLast(line);
                    }
                }
            }
        }

        public LineSegment2D makeLine(Point2D start, Point2D end) {
            return new LineSegment2D(start, end);
        }
        
        public CircularShape2D makeArc(CircleArc2D... arcs) {
            switch(arcs.length) {
                case 0:
                    return null;
                case 1:
                    return arcs[0];
                default:
                    if(arcs[arcs.length-1].lastPoint().almostEquals(arcs[0].firstPoint(), Tolerance2D.get())) {
                        return arcs[0].supportingCircle();
                    } else {
                        return new CircleArc2D(arcs[0].supportingCircle(), arcs[0].getStartAngle(), Stream.of(arcs).mapToDouble(arc -> arc.getAngleExtent()).sum());
                    }
            }
        }

        public CircleArc2D makeArc(Circle2D circle, Point2D start, Point2D end) {
            double startAngle = Angle2D.horizontalAngle(circle.center(), start);
            double midAngle = Angle2D.horizontalAngle(circle.center(), Point2D.midPoint(start, end));
            double endAngle = Angle2D.horizontalAngle(circle.center(), end);
            return new CircleArc2D(circle, startAngle, endAngle, Angle2D.containsAngle(startAngle, endAngle, midAngle));
        }

    }
}
