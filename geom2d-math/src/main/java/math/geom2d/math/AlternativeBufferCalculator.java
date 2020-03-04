/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package math.geom2d.math;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import math.geom2d.Point2D;
import math.geom2d.Tolerance2D;
import math.geom2d.circulinear.BoundaryPolyCirculinearCurve2D;
import math.geom2d.circulinear.CirculinearContinuousCurve2D;
import math.geom2d.circulinear.CirculinearContour2D;
import math.geom2d.circulinear.CirculinearContourArray2D;
import math.geom2d.circulinear.CirculinearCurve2D;
import math.geom2d.circulinear.CirculinearCurves2D;
import math.geom2d.circulinear.CirculinearDomain2D;
import math.geom2d.circulinear.CirculinearElement2D;
import math.geom2d.circulinear.GenericCirculinearDomain2D;
import math.geom2d.circulinear.GenericCirculinearRing2D;
import math.geom2d.circulinear.PolyCirculinearCurve2D;
import math.geom2d.circulinear.buffer.BufferCalculator;
import math.geom2d.circulinear.buffer.CapFactory;
import math.geom2d.circulinear.buffer.InternalCornerFactory;
import math.geom2d.circulinear.buffer.JoinFactory;
import math.geom2d.circulinear.buffer.RoundCapFactory;
import math.geom2d.curve.Curves2D;
import math.geom2d.line.LineSegment2D;

/**
 *
 * @author peter
 */
public class AlternativeBufferCalculator extends BufferCalculator {

    public AlternativeBufferCalculator() {
    }

    public AlternativeBufferCalculator(JoinFactory joinFactory, CapFactory capFactory) {
        super(joinFactory, capFactory);
    }

    public AlternativeBufferCalculator(JoinFactory joinFactory, CapFactory capFactory, InternalCornerFactory internalCornerFactory) {
        super(joinFactory, capFactory, internalCornerFactory);
    }

    /**
     * Compute the buffer of a circulinear curve.<p>
     * The algorithm is as follow:
     * <ol>
     * <li> split the curve into a set of curves without self-intersections
     * <li> for each split curve, compute the contour of its buffer
     * <li> split self-intersecting contours into set of disjoint contours
     * <li> split all contour which intersect each other to disjoint contours
     * <li> remove contours which are too close from the original curve
     * <li> create a new domain with the final set of contours
     * </ol>
     *
     * @param curve
     * @param dist
     * @return
     */
    @Override
    public CirculinearDomain2D computeBuffer(CirculinearCurve2D curve, double dist) {

        List<CirculinearContour2D> contours = new ArrayList<>();

        // iterate on all continuous curves
        curve.continuousCurves().forEach((cont) -> {
            // split the curve into a set of non self-intersecting curves
            CirculinearCurves2D.splitContinuousCurve(cont).forEach((splitted) -> {
                // compute the rings composing the simple curve buffer
                contours.addAll(computeBufferSimpleCurve(splitted, dist));
            });
        });

        // All the rings are created, we can now create a new domain with the
        // set of rings
        return new GenericCirculinearDomain2D(
                CirculinearContourArray2D.create(contours.toArray(new CirculinearContour2D[0])));
    }

    /**
     * Computes the buffer of a simple curve. This method should replace the
     * method 'computeBufferSimpleContour'.
     */
    public Collection<? extends CirculinearContour2D>
            computeBufferSimpleCurve(CirculinearContinuousCurve2D curve, double d) {

        Collection<CirculinearContour2D> contours = new ArrayList<>(2);

        // the parallel in each side
        List<CirculinearContinuousCurve2D> parallel1, parallel2;
        //parallel1 = createContinuousParallel(curve, d, false);
        //parallel2 = createContinuousParallel(curve, -d, true);
        parallel2 = createContinuousParallel(curve, -d, false);
        if (curve.isClosed()) {
            // each parallel is itself a contour
            //parallel1.forEach(parallel -> contours.add(convertCurveToBoundary(parallel)));
            parallel2.forEach(parallel -> contours.add(convertCurveToBoundary(parallel)));
        } else {
            // create a new contour from the two parallels and 2 caps
            // TODO: This could have odd effects now we allow multiple loops
            //contours.addAll(createSingleContourFromTwoParallels(parallel1.get(0), parallel2.get(0)));
            parallel2.forEach(parallel -> contours.add(convertCurveToBoundary(parallel)));
        }
        return contours;
    }

    /**
     * Compute the parallel curve of a Circulinear and continuous curve. The
     * result is itself an instance of CirculinearContinuousCurve2D.
     *
     * @param ofCurve
     * @param dist
     * @param reversed
     * @return
     */
    public List<CirculinearContinuousCurve2D> createContinuousParallel(
            CirculinearContinuousCurve2D ofCurve, double dist, boolean reversed) {

        // For circulinear elements, getParallel() is already implemented
        if (ofCurve instanceof CirculinearElement2D) {
            CirculinearElement2D parallel = ((CirculinearElement2D) ofCurve).parallel(dist);
            return Arrays.asList(reversed ? parallel.reverse() : parallel);
        }
        CirculinearContinuousCurve2D curve = repair(ofCurve);
        // extract collection of parallel curves, that connect each other
        Collection<CirculinearElement2D> parallelCurves = getParallelElements(curve, dist);

        // Split any elements that intersect so that unrequired segments can be removed
        Collection<CirculinearElement2D> splitCurves = splitAtIntersections(new ArrayList<>(parallelCurves));

        if (splitCurves.size() > parallelCurves.size()) {
            List<CirculinearElement2D> newCurves = new ArrayList<>();
            double dAccept = Math.abs(dist) - Tolerance2D.get();
            splitCurves.forEach((subcurve) -> {
                double subdist = CirculinearCurves2D.getDistanceCurvePoints(curve, getCurvePoints(subcurve, Math.abs(dist) / 2));
                if (subdist > dAccept) {
                    newCurves.add(subcurve);
                }
            });
            return reconstruct(newCurves, reversed);
        }
        CirculinearContinuousCurve2D parallel = PolyCirculinearCurve2D.create(parallelCurves.toArray(new CirculinearElement2D[0]), curve.isClosed());
        return Arrays.asList(reversed ? parallel.reverse() : parallel);
    }

    public static Collection<Point2D> getCurvePoints(CirculinearCurve2D curve, double spacing) {
        spacing = Math.abs(spacing);
        int segments = (int) Math.ceil(curve.length() / spacing);
        if (segments <= 1) {
            return curve.vertices();
        }
        double tSpacing = (curve.t1() - curve.t0()) / segments;
        List<Point2D> points = new ArrayList<>(segments + 1);
        for (int i = 0; i < segments + 1; i++) {
            points.add(curve.point(curve.t0() + i * tSpacing));
        }
        return points;
    }

    private Collection<Point2D> findIntersections(CirculinearElement2D elem1, CirculinearElement2D elem2) {
        Collection<Point2D> points = CirculinearCurves2D.findIntersections(elem1, elem2);
        Collection<Point2D> out = new ArrayList<>();
        points.stream().filter((point) -> (!CirculinearCurves2D.isCommonVertex(point, elem1, elem2))).forEach((point) -> {
            out.add(point);
        });
        return out;
    }

    private List<CirculinearElement2D> splitAtIntersections(List<CirculinearElement2D> curves) {
        Map<CirculinearElement2D, List<Point2D>> intersections = new HashMap<>();
        for (int i = 0; i < curves.size(); i++) {
            for (int j = i + 1; j < curves.size(); j++) {
                Collection<Point2D> points = findIntersections(curves.get(i), curves.get(j));
                if (!points.isEmpty()) {
                    if (intersections.containsKey(curves.get(i))) {
                        intersections.get(curves.get(i)).addAll(points);
                    } else {
                        intersections.put(curves.get(i), new ArrayList<>(points));
                    }
                    if (intersections.containsKey(curves.get(j))) {
                        intersections.get(curves.get(j)).addAll(points);
                    } else {
                        intersections.put(curves.get(j), new ArrayList<>(points));
                    }
                }
            }
        }
        List<CirculinearElement2D> elements = new ArrayList<>();
        curves.forEach((curve) -> {
            if (intersections.containsKey(curve)) {
                List<Point2D> points = intersections.get(curve);
                List<Double> positions = new ArrayList<>(points.size());
                points.forEach(p -> positions.add(curve.position(p)));
                Collections.sort(positions);
                elements.add(curve.subCurve(curve.t0(), positions.get(0)));
                for (int i = 0; i < positions.size() - 1; i++) {
                    elements.add(curve.subCurve(positions.get(i), positions.get(i + 1)));
                }
                elements.add(curve.subCurve(positions.get(positions.size() - 1), curve.t1()));
            } else {
                elements.add(curve);
            }
        });
        return elements;
    }

    private List<CirculinearContinuousCurve2D> reconstruct(List<CirculinearElement2D> elements, boolean reversed) {
        List<CirculinearContinuousCurve2D> loops = new ArrayList<>();
        Deque<CirculinearElement2D> loop = new ArrayDeque<>();
        Point2D start = null;
        for (CirculinearElement2D element : elements) {
            if (loop.isEmpty()) {
                start = element.firstPoint();
            } else {
                double gap = loop.peekLast().lastPoint().distance(element.firstPoint());
//                double multiplier = gap / Tolerance2D.get();
//                double loopGap = start.distance(element.firstPoint());
                double loopEndGap = start.distance(element.lastPoint());
                if (gap > Tolerance2D.get()) {
                    CirculinearContinuousCurve2D curve = repair(loop, loopEndGap < Tolerance2D.get());//PolyCirculinearCurve2D.create(loop.toArray(new CirculinearElement2D[0]), true);
                    if (!curve.isEmpty()) {
                        loops.add(reversed ? curve.reverse() : curve);
                        loop = new ArrayDeque<>();
                    }
                }
//                System.out.println("Gap of " + gap + " (" + multiplier + "x tolerance) - " + element);
//                System.out.println("Gap of " + loopGap + " from start and " + loopEndGap + " closing");
            }
            loop.add(element);
        }
        if (!loop.isEmpty()) {
            if (loops.isEmpty()) {
                CirculinearContinuousCurve2D curve = repair(loop, false);//PolyCirculinearCurve2D.create(loop.toArray(new CirculinearElement2D[0]), false);
                if (!curve.isEmpty()) {
                    loops.add(reversed ? curve.reverse() : curve);
                }
            } else {
                Deque<CirculinearElement2D> firstLoop = new ArrayDeque<>();
                loops.get(0).continuousCurves().forEach((cc) -> cc.smoothPieces().forEach((sp) -> firstLoop.add(sp)));
                firstLoop.addAll(loop);
                double loopEndGap = firstLoop.peekFirst().firstPoint().distance(firstLoop.peekLast().lastPoint());
                CirculinearContinuousCurve2D curve = repair(firstLoop, loopEndGap < Tolerance2D.get());//PolyCirculinearCurve2D.create(firstLoop.toArray(new CirculinearElement2D[0]), true);
                loops.set(0, reversed ? curve.reverse() : curve);
            }
        }
        // Loops need to be closed, this might involve matching start/end with others
        List<CirculinearContinuousCurve2D> closedLoops = new ArrayList<>();
        Deque<CirculinearContinuousCurve2D> openLoops = new ArrayDeque<>();
        loops.forEach((curve) -> {
            if (curve.isClosed()) {
                closedLoops.add(curve);
            } else {
                openLoops.add(curve);
            }
        });
        while (!openLoops.isEmpty()) {
            // Get a curve
            CirculinearContinuousCurve2D curve = openLoops.pop();
            // Find a curve with matching start point
            Optional<CirculinearContinuousCurve2D> following = openLoops.stream()
                    .filter((other) -> other.firstPoint().distance(curve.lastPoint()) < Tolerance2D.get())
                    .findAny();
            if (following.isPresent()) {
                openLoops.remove(following.get());
                // Join them. If ends also match close, else put back in queue.
                boolean close = following.get().lastPoint().distance(curve.firstPoint()) < Tolerance2D.get();
                if (close) {
                    closedLoops.add(repair(curve, following.get(), true));
                } else {
                    openLoops.add(repair(curve, following.get(), false));
                }
            } else {
                closedLoops.add(repair(curve, false));
            }
        }
        return closedLoops;
    }

    private CirculinearContinuousCurve2D repair(CirculinearContinuousCurve2D curve) {
        return repair(curve, curve.isClosed());
    }

    private CirculinearContinuousCurve2D repair(CirculinearContinuousCurve2D curveA, CirculinearContinuousCurve2D curveB, boolean closed) {
        Deque<CirculinearElement2D> elements = new ArrayDeque<>();
        curveA.continuousCurves().forEach(cc -> cc.smoothPieces().forEach(sp -> elements.add(sp)));
        curveB.continuousCurves().forEach(cc -> cc.smoothPieces().forEach(sp -> elements.add(sp)));
        return repair(elements, closed);
    }

    private CirculinearContinuousCurve2D repair(CirculinearContinuousCurve2D curve, boolean closed) {
        Deque<CirculinearElement2D> elements = new ArrayDeque<>();
        curve.continuousCurves().forEach(cc -> cc.smoothPieces().forEach(sp -> elements.add(sp)));
        return repair(elements, closed);
    }

    private CirculinearContinuousCurve2D repair(Deque<CirculinearElement2D> elements, boolean closed) {
        Deque<CirculinearElement2D> queue = new ArrayDeque<>();
        elements.forEach((element) -> repair(queue, element));
        if (closed && !queue.isEmpty() && queue.peekLast().lastPoint().distance(queue.peekFirst().firstPoint()) > Tolerance2D.get()) {
            repair(queue, new LineSegment2D(queue.peekLast().lastPoint(), queue.peekFirst().firstPoint()));
        }
        return PolyCirculinearCurve2D.create(queue.toArray(new CirculinearElement2D[0]), closed);
    }

    private void repair(Deque<CirculinearElement2D> elements, CirculinearElement2D element) {
        if (element.length() < Tolerance2D.get()) {
            return;
        }
        elements.addLast(element);
//        if (elements.isEmpty()) {
//            elements.addLast(element);
//        } else {
//            CirculinearElement2D last = elements.peekLast();
//            if (last.lastPoint().distance(element.firstPoint()) > Tolerance2D.get()) {
//                repair(elements, new LineSegment2D(last.lastPoint(), element.firstPoint()));
//                repair(elements, element);
//            } else if (last instanceof LineSegment2D
//                    && element instanceof LineSegment2D
//                    && ((LineSegment2D) last).tangent(last.t1()).almostEquals(((LineSegment2D) element).tangent(element.t0()), Tolerance2D.get())) {
//                elements.removeLast();
//                elements.addLast(new LineSegment2D(last.firstPoint(), element.lastPoint()));
//            } else if (last instanceof LineSegment2D
//                    && element instanceof LineSegment2D
//                    && ((LineSegment2D) last).tangent(last.t1()).opposite().almostEquals(((LineSegment2D) element).tangent(element.t0()), Tolerance2D.get())) {
//                elements.removeLast();
//                elements.addLast(new LineSegment2D(last.firstPoint(), element.lastPoint()));
//            } else {
//                elements.addLast(element);
//            }
//        }
    }

    /**
     * Converts the given continuous curve to an instance of
     * CirculinearContour2D. This can be the curve itself, a new instance of
     * GenericCirculinearRing2D if the curve is bounded, or a new instance of
     * BoundaryPolyCirculinearCurve2D if the curve is unbounded.
     */
    private CirculinearContour2D convertCurveToBoundary(
            CirculinearContinuousCurve2D curve) {
        // basic case: curve is already a contour
        if (curve instanceof CirculinearContour2D) {
            return (CirculinearContour2D) curve;
        }

        // if the curve is closed, return an instance of GenericCirculinearRing2D
        if (curve.isClosed()) {
            return GenericCirculinearRing2D.create(curve.smoothPieces().toArray(new CirculinearElement2D[0]));
        }

        return BoundaryPolyCirculinearCurve2D.create(curve.smoothPieces().toArray(new CirculinearContinuousCurve2D[0]));
    }

    /**
     * Creates the unique contour based on two parallels of the base curve, by
     * adding appropriate circle arcs at extremities of the base curve.
     */
    private Collection<CirculinearContour2D>
            createSingleContourFromTwoParallels(
                    CirculinearContinuousCurve2D curve1,
                    CirculinearContinuousCurve2D curve2) {
        CapFactory capFactory = new RoundCapFactory();
        // create array for storing result
        List<CirculinearContour2D> contours
                = new ArrayList<>();

        CirculinearContinuousCurve2D cap;

        // create new ring using two open curves and two circle arcs
        if (curve1 != null && curve2 != null) {
            // array of elements for creating new ring.
            List<CirculinearElement2D> elements
                    = new ArrayList<>();

            // some shortcuts for computing infinity of curve
            boolean b0 = !Curves2D.isLeftInfinite(curve1);
            boolean b1 = !Curves2D.isRightInfinite(curve1);

            if (b0 && b1) {
                // case of a curve finite at each extremity

                // extremity points
                Point2D p11 = curve1.firstPoint();
                Point2D p12 = curve1.lastPoint();
                Point2D p21 = curve2.firstPoint();
                Point2D p22 = curve2.lastPoint();

                // Check how to associate open curves and circle arcs
                elements.addAll(curve1.smoothPieces());
                cap = capFactory.createCap(p12, p21);
                elements.addAll(cap.smoothPieces());
                elements.addAll(curve2.smoothPieces());
                cap = capFactory.createCap(p22, p11);
                elements.addAll(cap.smoothPieces());

                // create the last ring
                contours.add(new GenericCirculinearRing2D(elements));

            } else if (!b0 && !b1) {
                // case of an infinite curve at both extremities
                // In this case, the two parallel curves do not join,
                // and are added as contours individually					
                contours.add(convertCurveToBoundary(curve1));
                contours.add(convertCurveToBoundary(curve2));

            } else if (b0 && !b1) {
                // case of a curve starting from infinity, and finishing
                // on a given point

                // extremity points
                Point2D p11 = curve1.firstPoint();
                Point2D p22 = curve2.lastPoint();

                // add elements of the new contour
                elements.addAll(curve2.smoothPieces());
                cap = capFactory.createCap(p22, p11);
                elements.addAll(cap.smoothPieces());
                elements.addAll(curve1.smoothPieces());

                // create the last ring
                contours.add(new GenericCirculinearRing2D(elements));

            } else if (b1 && !b0) {
                // case of a curve starting at a point and finishing at
                // the infinity

                // extremity points
                Point2D p12 = curve1.lastPoint();
                Point2D p21 = curve2.firstPoint();

                // add elements of the new contour
                elements.addAll(curve1.smoothPieces());
                cap = capFactory.createCap(p12, p21);
                elements.addAll(cap.smoothPieces());
                elements.addAll(curve2.smoothPieces());

                // create the last contour
                contours.add(new GenericCirculinearRing2D(elements));

            }
        }

        return contours;
    }
}
