/**
 * File: 	CirculinearCurve3DUtils.java
 * Project: javaGeom
 *
 * Distributed under the LGPL License.
 *
 * Created: 16 mai 09
 */
package math.geom3d.circulinear;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import math.geom2d.Tolerance2D;

import math.geom3d.Point3D;
import math.geom3d.Shape3D;
import math.geom3d.Vector3D;
import math.geom3d.conic.Circle3D;
import math.geom3d.curve.ContinuousCurve3D;
import math.geom3d.curve.Curve3D;
import math.geom3d.curve.Curves3D;
import math.geom3d.curve.CurveSet3D;
import math.geom3d.curve.SmoothCurve3D;
import math.geom3d.line.LinearShape3D;
import math.geom3d.polygon.LinearCurve3D;

/**
 * Some utilities for working with circulinear curves.
 *
 * @author dlegland
 *
 */
public class CirculinearCurves3D {

    /**
     * Converts a shape to a circulinear curve, by concatenating all elements of
     * the shape to the appropriate circulinear curve type. If the curve
     * contains one or more non-circulinear smooth curve, null is returned
     *
     * @param shape
     * @return
     */
    public static CirculinearCurve3D convert(Shape3D shape) {
        return convert(shape, c -> null);
    }

    /**
     * Converts a shape to a circulinear curve, by concatenating all elements of
     * the shape to the appropriate circulinear curve type.If the curve contains
     * one or more non-circulinear smooth curve, null is returned
     *
     * @param shape
     * @param subcurveConverter
     * @return
     */
    public static CirculinearCurve3D convert(Shape3D shape,
            Function<Curve3D, CirculinearCurve3D> subcurveConverter) {
        if (shape instanceof Curve3D) {
            return convert((Curve3D) shape, subcurveConverter);
        }
        if (shape instanceof LinearCurve3D) {
            List<CirculinearElement3D> elements = new ArrayList<>();
            ((LinearCurve3D) shape).edges().forEach(e -> elements.add(e));
            return new PolyCirculinearCurve3D<>(elements);
        }
        return null;
    }

    /**
     * Converts a curve to a circulinear curve, by concatenating all elements of
     * the curve to the appropriate circulinear curve type. If the curve
     * contains one or more non-circulinear smooth curve, null is returned
     *
     * @param curve
     * @return
     */
    public static CirculinearCurve3D convert(Curve3D curve) {
        return convert(curve, c -> null);
    }

    /**
     * Converts a curve to a circulinear curve, by concatenating all elements of
     * the curve to the appropriate circulinear curve type.If the curve contains
     * one or more non-circulinear smooth curve, null is returned
     *
     * @param curve
     * @param subcurveConverter
     * @return
     */
    public static CirculinearCurve3D convert(Curve3D curve,
            Function<Curve3D, CirculinearCurve3D> subcurveConverter) {
        // first check type, to avoid unnecessary computations
        if (curve instanceof CirculinearCurve3D) {
            return (CirculinearCurve3D) curve;
        }

        // If the curve is continuous, creates a CirculinearContinuousCurve3D
        if (curve instanceof ContinuousCurve3D) {
            // extract smooth pieces
            ContinuousCurve3D continuous = (ContinuousCurve3D) curve;
            Collection<? extends SmoothCurve3D> smoothPieces
                    = continuous.smoothPieces();

            // prepare array of elements
            ArrayList<CirculinearElement3D> elements = new ArrayList<>(
                    smoothPieces.size());

            // class cast for each element, or throw an exception
            for (SmoothCurve3D smooth : smoothPieces) {
                if (smooth instanceof CirculinearElement3D) {
                    elements.add((CirculinearElement3D) smooth);
                } else if (subcurveConverter != null) {
                    CirculinearCurve3D sub = subcurveConverter.apply(smooth);
                    if (sub == null) {
                        return null;
                    }
                    sub.continuousCurves()
                            .forEach(cc -> cc.smoothPieces()
                            .forEach(sp -> elements.add(sp)));
                } else {
                    return null;
                }
            }

            // create the resulting CirculinearContinuousCurve3D
            return new PolyCirculinearCurve3D<>(elements, continuous.isClosed());
        }

        // If the curve is continuous, creates a CirculinearContinuousCurve3D
        if (curve instanceof CurveSet3D<?>) {
            // extract smooth pieces
            CurveSet3D<?> set = (CurveSet3D<?>) curve;
            Collection<? extends ContinuousCurve3D> continuousCurves = set
                    .continuousCurves();

            // prepare array of elements
            ArrayList<CirculinearContinuousCurve3D> curves
                    = new ArrayList<>(continuousCurves.size());

            // class cast for each element, or throw an exception
            for (ContinuousCurve3D continuous : continuousCurves) {
                if (continuous instanceof CirculinearContinuousCurve3D) {
                    curves.add((CirculinearContinuousCurve3D) continuous);
                } else {
                    curves.add((CirculinearContinuousCurve3D) convert(continuous));
                }
            }

            // create the resulting CirculinearContinuousCurve3D
            return CirculinearCurveArray3D.create(curves.toArray(new CirculinearContinuousCurve3D[0]));
        }
        if (subcurveConverter != null) {
            return subcurveConverter.apply(curve);
        } else {
            return null;
        }
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see math.geom3d.circulinear.CirculinearCurve3D#length(double)
     */
    public static double getLength(
            CurveSet3D<? extends CirculinearCurve3D> curve, double pos) {
        // init
        double length = 0;

        // add length of each curve before current curve
        int index = curve.curveIndex(pos);
        for (int i = 0; i < index; i++) {
            length += curve.get(i).length();
        }

        // add portion of length for last curve
        if (index < curve.size()) {
            double pos2 = curve.localPosition(pos - 2 * index);
            length += curve.get(index).length(pos2);
        }

        // return result
        return length;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see math.geom3d.circulinear.CirculinearCurve3D#position(double)
     */
    public static double getPosition(
            CurveSet3D<? extends CirculinearCurve3D> curveSet, double length) {

        // position to compute
        double pos = 0;

        // index of current curve
        int index = 0;

        // cumulative length
        double cumLength = getLength(curveSet, curveSet.getT0());

        // iterate on all curves
        for (CirculinearCurve3D curve : curveSet.curves()) {
            // length of current curve
            double curveLength = curve.length();

            // add either 2, or fraction of length
            if (cumLength + curveLength < length) {
                cumLength += curveLength;
                index++;
            } else {
                // add local position on current curve
                double pos2 = curve.position(length - cumLength);
                pos = curveSet.globalPosition(index, pos2);
                break;
            }
        }

        // return the result
        return pos;
    }

    /**
     * Computes intersection point of a single curve, by iterating on pair of
     * Circulinear elements composing the curve.
     *
     * @param curve
     * @return the set of self-intersection points
     */
    public static Collection<Point3D> findSelfIntersections(
            CirculinearCurve3D curve) {

        // create array of circulinear elements
        List<CirculinearElement3D> elements = new ArrayList<>();

        // extract all circulinear elements of the curve
        for (CirculinearContinuousCurve3D cont : curve.continuousCurves()) {
            elements.addAll(cont.smoothPieces());
        }

        // create array for storing result
        List<Point3D> result = new ArrayList<>(0);

        // iterate on each couple of elements
        int n = elements.size();
        for (int i = 0; i < n - 1; i++) {
            CirculinearElement3D elem1 = elements.get(i);
            for (int j = i; j < n; j++) {
                CirculinearElement3D elem2 = elements.get(j);
                // iterate on intersections between consecutive elements
                for (Point3D inter : findIntersections(elem1, elem2)) {
                    // do not keep extremities
                    if (isCommonVertex(inter, elem1, elem2)) {
                        continue;
                    }

                    result.add(inter);
                }
            }
        }

        // return the set of intersections
        return result;
    }

    public static double[][] locateSelfIntersections(
            CurveSet3D<? extends CirculinearElement3D> curve) {

        // create array for storing result
        List<Double> list1 = new ArrayList<>(0);
        List<Double> list2 = new ArrayList<>(0);
        double dt;

        // iterate on each couple of elements
        int n = curve.size();
        for (int i = 0; i < n - 1; i++) {
            CirculinearElement3D elem1 = curve.get(i);
            for (int j = i + 1; j < n; j++) {
                CirculinearElement3D elem2 = curve.get(j);
                // iterate on intersection between consecutive elements
                for (Point3D inter : findIntersections(elem1, elem2)) {
                    // do not keep extremities
                    if (isCommonVertex(inter, elem1, elem2)) {
                        continue;
                    }

                    // add the intersection if we keep it
                    dt = Curves3D.toUnitSegment(elem1.position(inter),
                            elem1.getT0(), elem1.getT1());
                    list1.add(2 * i + dt);

                    dt = Curves3D.toUnitSegment(elem2.position(inter),
                            elem2.getT0(), elem2.getT1());
                    list2.add(2 * j + dt);
                }
            }
        }

        // convert the 2 lists into a n*2 array
        int np = list1.size();
        double[][] result = new double[np][2];
        for (int i = 0; i < np; i++) {
            result[i][0] = list1.get(i);
            result[i][1] = list2.get(i);
        }

        // return the array of positions
        return result;
    }

    /**
     * Checks if the point is a common extremity between the two curve elements.
     * @param inter
     * @param elem1
     * @param elem2
     * @return 
     */
    public static boolean isCommonVertex(Point3D inter,
            CirculinearCurve3D elem1, CirculinearCurve3D elem2) {

        double eps = Tolerance2D.get();

        // Test end of elem1 and start of elem2
        if (!Double.isInfinite(elem1.getT1())
                && !Double.isInfinite(elem2.getT0())) {
            if (inter.almostEquals(elem1.lastPoint(), eps)
                    && inter.almostEquals(elem2.firstPoint(), eps)) {
                return true;
            }
        }

        // Test end of elem2 and start of elem1
        if (!Double.isInfinite(elem1.getT0())
                && !Double.isInfinite(elem2.getT1())) {
            if (inter.almostEquals(elem1.firstPoint(), eps)
                    && inter.almostEquals(elem2.lastPoint(), eps)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Compute the set of intersection points between the two curves.
     *
     * @param curve1
     * @param curve2
     * @return a collection of intersection points
     */
    public static Collection<Point3D> findIntersections(
            CirculinearCurve3D curve1, CirculinearCurve3D curve2) {

        // create array of circulinear elements
        List<CirculinearElement3D> elements1 = new ArrayList<>();
        List<CirculinearElement3D> elements2 = new ArrayList<>();

        // extract all circulinear elements of the curve
        curve1.continuousCurves().forEach((cont) -> {
            elements1.addAll(cont.smoothPieces());
        });
        curve2.continuousCurves().forEach((cont) -> {
            elements2.addAll(cont.smoothPieces());
        });

        // create array for storing result
        List<Point3D> result = new ArrayList<>(0);

        // iterate on each couple of elements
        int n1 = elements1.size();
        int n2 = elements2.size();
        for (int i = 0; i < n1; i++) {
            CirculinearElement3D elem1 = elements1.get(i);
            for (int j = 0; j < n2; j++) {
                CirculinearElement3D elem2 = elements2.get(j);
                // iterate on intersection between consecutive elements
                for (Point3D inter : findIntersections(elem1, elem2)) {
                    // add the intersection if we keep it
                    result.add(inter);
                }
            }
        }

        // return the set of intersections
        return result;
    }

//    /**
//     * Locate intersection points of two curves. The result is a N-by-2 array of
//     * double, where N is the number of intersections. For each row, the first
//     * element is the position on the first curve, and the second element is the
//     * position on the second curve.
//     *
//     * @param curve1
//     * @param curve2
//     * @return
//     */
//    public static double[][] locateIntersections(CirculinearCurve3D curve1,
//            CirculinearCurve3D curve2) {
//
//        // create array for storing result
//        List<Double> list1 = new ArrayList<>(0);
//        List<Double> list2 = new ArrayList<>(0);
//
//        // create array of circulinear elements
//        List<CirculinearElement3D> elements1 = new ArrayList<>();
//        List<CirculinearElement3D> elements2 = new ArrayList<>();
//
//        // extract all circulinear elements of the curve
//        curve1.continuousCurves().forEach((cont) -> {
//            elements1.addAll(cont.smoothPieces());
//        });
//        curve2.continuousCurves().forEach((cont) -> {
//            elements2.addAll(cont.smoothPieces());
//        });
//
//        // iterate on each couple of elements
//        int n1 = elements1.size();
//        int n2 = elements2.size();
//        for (int i = 0; i < n1; i++) {
//            CirculinearElement3D elem1 = elements1.get(i);
//            for (int j = 0; j < n2; j++) {
//                CirculinearElement3D elem2 = elements2.get(j);
//                // iterate on intersections between consecutive elements
//                for (Point3D inter : findIntersections(elem1, elem2)) {
//                    double pos1 = curve1.position(inter);
//                    double pos2 = curve2.position(inter);
//                    if (curve1.isSingular(pos1) && curve2.isSingular(pos2)) {
//                        continue;
//                    }
//                    // add the intersection if we keep it
//                    list1.add(pos1);
//                    list2.add(pos2);
//                }
//            }
//        }
//
//        // convert the 2 lists into a n*2 array
//        int np = list1.size();
//        double[][] result = new double[np][2];
//        for (int i = 0; i < np; i++) {
//            result[i][0] = list1.get(i);
//            result[i][1] = list2.get(i);
//        }
//
//        // return the array of positions
//        return result;
//    }
//
//    /**
//     * Computes the intersections, if they exist, of two circulinear elements.
//     *
//     * @param elem1
//     * @param elem2
//     * @return
//     */
//    public static Collection<Point3D> findIntersections(
//            CirculinearElement3D elem1, CirculinearElement3D elem2) {
//
//        if (elem1 == null || elem2 == null) {
//            return new ArrayList<>(0);
//        }
//
//        // find which shapes are linear
//        boolean b1 = elem1 instanceof LinearShape3D;
//        boolean b2 = elem2 instanceof LinearShape3D;
//
//        // if both elements are linear, check parallism to avoid computing
//        // intersection of parallel lines
//        if (b1 && b2) {
//            LinearShape3D line1 = (LinearShape3D) elem1;
//            LinearShape3D line2 = (LinearShape3D) elem2;
//
//            // test parallel elements
//            Vector3D v1 = line1.direction();
//            Vector3D v2 = line2.direction();
//            if (Vector3D.isColinear(v1, v2)) {
//                return new ArrayList<>(0);
//            }
//
//            return line1.intersections(line2);
//        }
//
//        // First try to use linear shape methods
//        if (elem1 instanceof LinearShape3D) {
//            return elem2.intersections((LinearShape3D) elem1);
//        }
//        if (elem2 instanceof LinearShape3D) {
//            return elem1.intersections((LinearShape3D) elem2);
//        }
//
//        // From now, both elem1 and elem2 are instances of CircleShape3D
//        // It is therefore possible to extract support circles
//        Circle3D circ1 = ((CircularShape3D) elem1).supportingCircle();
//        Circle3D circ2 = ((CircularShape3D) elem2).supportingCircle();
//
//        // create array for storing result (max 2 possible intersections)
//        List<Point3D> pts = new ArrayList<>(2);
//
//        // for each of the circle intersections, check if they belong to
//        // both elements
//        Circle3D.circlesIntersections(circ1, circ2).stream()
//                .filter((inter) -> (elem1.contains(inter) && elem2.contains(inter))).forEachOrdered((inter) -> {
//            pts.add(inter);
//        });
//
//        // return found intersections
//        return pts;
//    }
//
//    /**
//     * Split a continuous curve which self-intersects into a set of continuous
//     * circulinear curves which do not self-intersect.
//     *
//     * @param curve the curve to split
//     * @return a set of non-self-intersecting continuous curves
//     */
//    public static Collection<CirculinearContinuousCurve3D> splitContinuousCurve(
//            CirculinearContinuousCurve3D curve) {
//
//        double pos0, pos1, pos2;
//
//        // create the array of resulting curves
//        List<CirculinearContinuousCurve3D> result = new ArrayList<>();
//
//        // Instances of CirculinearElement3D can not self-intersect
//        if (curve instanceof CirculinearElement3D) {
//            result.add(curve);
//            return result;
//        }
//
//        // convert the curve to a poly-circulinear curve, to be able to call
//        // the "locateSelfIntersections" method.
//        PolyCirculinearCurve3D<CirculinearElement3D> polyCurve = createPolyCurve(
//                curve.smoothPieces(), curve.isClosed());
//
//        // identify couples of intersections
//        double[][] couples = locateSelfIntersections(polyCurve);
//
//        // case of curve without self-intersections
//        if (couples.length == 0) {
//            // create continuous curve formed only by circulinear elements
//            result.add(createPolyCurve(polyCurve.smoothPieces(),
//                    curve.isClosed()));
//            return result;
//        }
//
//        // put all positions into a tree map
//        NavigableMap<Double, Double> twins = new TreeMap<>();
//        for (int i = 0; i < couples.length; i++) {
//            pos1 = couples[i][0];
//            pos2 = couples[i][1];
//            twins.put(pos1, pos2);
//            twins.put(pos2, pos1);
//        }
//
//        // an array for the portions of curves
//        ArrayList<CirculinearElement3D> elements;
//
//        // Process the first curve
//        // create new empty array of elements for current continuous curve
//        elements = new ArrayList<>();
//
//        // get first intersection
//        pos1 = polyCurve.getT0();
//        pos2 = twins.firstKey();
//        pos0 = pos2;
//
//        // add the first portion of curve, starting from the beginning
//        addElements(elements, polyCurve.subCurve(pos1, pos2));
//        do {
//            // get the position of the new portion of curve
//            pos1 = twins.remove(pos2);
//
//            // check if there are still intersections to process
//            if (twins.higherKey(pos1) == null) {
//                break;
//            }
//
//            // get position of next intersection on the curve
//            pos2 = twins.higherKey(pos1);
//
//            // add elements
//            addElements(elements, polyCurve.subCurve(pos1, pos2));
//        } while (true);
//
//        // add the last portion of curve, going to the end of original curve
//        if (polyCurve.getT1() - pos1 > Tolerance2D.get()) {
//            pos2 = polyCurve.getT1();
//            addElements(elements, polyCurve.subCurve(pos1, pos2));
//        } else {
//            twins.remove(pos2);
//        }
//
//        // add the continuous curve formed only by circulinear elements
//        result.add(createPolyCurve(elements, curve.isClosed()));
//
//        // Process other curves, while there are intersections left
//        while (!twins.isEmpty()) {
//            // create new empty array of elements for current continuous curve
//            elements = new ArrayList<>();
//
//            // get first intersection            
//            pos0 = twins.firstKey();
//            pos1 = twins.get(pos0);
//            pos2 = nextValue(twins.navigableKeySet(), pos1);//twins.higherKey(pos1);
//
//            // add the portion of curve
//            addElements(elements, polyCurve, pos1, pos2);//addElements(elements, polyCurve.subCurve(pos1, pos2));
//
//            boolean pos2Removed = false;
//            while (pos2 != pos0) {
//                // get the position of the new portion of curve
//                pos1 = twins.remove(pos2);
//                pos2Removed = true;
//// check if there are still intersections to process
//                if (twins.higherKey(pos1) == null) {
//                    break;
//                }
//
//                // get position of next intersection on the curve
//                pos2 = twins.higherKey(pos1);
//                pos2Removed = false;
//
//                // add elements
//                addElements(elements, polyCurve.subCurve(pos1, pos2));
//            }
//
//            if (!pos2Removed) {
//                twins.remove(pos2);
//            }
//
//            // create continuous curve formed only by circulinear elements
//            // and add it to the set of curves
//            result.add(createPolyCurve(elements, true));
//        }
//
//        return clean(result);
//    }

    /**
     * Remove any elements that are short lines or circles
     *
     * @param curve
     * @return
     */
    static CirculinearContinuousCurve3D clean(CirculinearContinuousCurve3D curve) {
        if (!curve.isBounded()) {
            return curve;
        }
        List<CirculinearElement3D> elements = new ArrayList<>();
        curve.continuousCurves().forEach(cc -> cc.smoothPieces().stream()
                .filter(sp -> sp.firstPoint().distance(sp.lastPoint()) > 10 * Tolerance2D.get())
                .forEachOrdered(sp -> elements.add(sp)));
        return new PolyCirculinearCurve3D(elements.toArray(new CirculinearElement3D[0]), true);
    }

    /**
     * Remove any elements that are short lines or circles
     *
     * @param curve
     * @return
     */
    static Collection<CirculinearContinuousCurve3D> clean(Collection<CirculinearContinuousCurve3D> curve) {
        return curve.stream().map(c -> clean(c)).collect(Collectors.toList());
    }

    /**
     * This is a helper method, used to avoid excessive use of generics within
     * other methods of the class.
     */
    private static PolyCirculinearCurve3D<CirculinearElement3D> createPolyCurve(
            Collection<? extends CirculinearElement3D> elements, boolean closed) {
        return new PolyCirculinearCurve3D<>(elements,
                closed);
    }

    /**
     * Add all circulinear elements of the given curve to the collection of
     * circulinear elements.
     */
    private static void addElements(Collection<CirculinearElement3D> elements,
            CirculinearContinuousCurve3D curve) {
        elements.addAll(curve.smoothPieces());
    }

    private static void addElements(Collection<CirculinearElement3D> elements,
            CirculinearContinuousCurve3D curve, double t1, double t2) {
        if (t2 > t1) {
            elements.addAll(curve.subCurve(t1, t2).smoothPieces());
        } else {
            elements.addAll(curve.subCurve(t1, curve.getT1()).smoothPieces());
            elements.addAll(curve.subCurve(curve.getT0(), t2).smoothPieces());
        }
    }

    private static boolean isAllEmpty(Collection<TreeMap<Double, Double>> coll) {
        for (TreeMap<?, ?> map : coll) {
            if (!map.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns either the next value, or the first value of the tree if the
     * given value is the last one of the tree.
     */
    private static double nextValue(NavigableSet<Double> tree, double value) {
        if (tree.higher(value) == null) {
            return tree.first();
        } else {
            return tree.higher(value);
        }
    }

    public static double getDistanceCurvePoints(CirculinearCurve3D curve,
            Collection<? extends Point3D> points) {
        double minDist = Double.MAX_VALUE;
        for (Point3D point : points) {
            minDist = Math.min(minDist, curve.distance(point));
        }
        return minDist;
    }
}
