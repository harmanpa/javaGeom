/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package math.geom2d.math;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import math.geom2d.AffineTransform2D;
import math.geom2d.Angle2D;
import math.geom2d.Point2D;
import math.geom2d.Tolerance2D;
import math.geom2d.circulinear.CirculinearCurve2D;
import math.geom2d.circulinear.CirculinearElement2D;
import math.geom2d.conic.Circle2D;
import math.geom2d.conic.CircleArc2D;
import math.geom2d.conic.Ellipse2D;
import math.geom2d.conic.EllipseArc2D;

/**
 *
 * @author peter
 */
public class Similarity2D {

    public static List<SimilaritySet> findSimilarities(CirculinearCurve2D... curves) {
        return findSimilarities(Arrays.asList(curves));
    }

    public static List<SimilaritySet> findSimilarities(List<CirculinearCurve2D> curves) {
        switch (curves.size()) {
            case 0:
            case 1:
                return Arrays.asList();
            case 2:
                Similarity similarity = findSimilarities(curves.get(0), curves.get(1));
                if (similarity.isSimilar()) {
                    SimilaritySet set = new SimilaritySet(curves.get(0));

                    return Arrays.asList(set);
                }
                return Arrays.asList();
            default:
                // Chessboard
                List<SimilaritySet> out = new ArrayList<>();
                for (int column = 0; column < curves.size(); column++) {
                    for (int row = column + 1; row < curves.size(); row++) {
                        Similarity result = findSimilarities(curves.get(column), curves.get(row));
                        if (result.isSimilar()) {
                            List<CirculinearCurve2D> newCurves = new ArrayList<>();
                            // Add any curves other than the row
                            for (int other = 0; other < curves.size(); other++) {
                                if (other > column && other != row) {
                                    newCurves.add(curves.get(other));
                                }
                            }
                            List<SimilaritySet> otherSets = findSimilarities(newCurves);
                            SimilaritySet thisSet = new SimilaritySet(curves.get(column));
                            for (SimilaritySet set : otherSets) {
                                if (set.getRoot().equals(thisSet.getRoot())) {
                                    thisSet.addAll(set.getSimilarities());
                                } else {
                                    out.add(set);
                                }
                            }
                            out.add(thisSet);
                            return out;
                        }
                    }
                }
                return out;
        }
    }

    public static Similarity findSimilarities(CirculinearCurve2D curve1, CirculinearCurve2D curve2) {
        List<CirculinearElement2D> elements1 = Rings2D.getElements(curve1);
        List<CirculinearElement2D> elements2 = Rings2D.getElements(curve2);
        if (elements1.size() == elements2.size()) {
            for (int shift = 0; shift < elements1.size(); shift++) {
                if (similar(elements1, elements2, shift)) {
                    if (elements1.isEmpty()) {
                        return new Similarity(true, AffineTransform2D.createIdentity(), 0);
                    }
                    // Determine the transform
                    Point2D[] firstPoints1 = firstTwoPoints(elements1);
                    Point2D[] firstPoints2 = firstTwoPoints(shifted(elements2, shift));
                    AffineTransform2D transform = AffineTransform2D.createTranslation(
                            firstPoints1[0].getX() - firstPoints2[0].getX(),
                            firstPoints1[0].getY() - firstPoints2[0].getY());
                    double theta = Angle2D.absoluteAngle(firstPoints1[0], transform.transform(firstPoints2[1]), firstPoints1[1]);
                    transform = transform.chain(AffineTransform2D.createRotation(firstPoints1[0], theta));
                    return new Similarity(true, transform, shift);
                }
            }
        }
        return new Similarity(false, null, 0);
    }

    protected static boolean similar(List<CirculinearElement2D> list1, List<CirculinearElement2D> list2, int shift) {
        return similar(list1, list2, shift, TYPE_COMPARATOR)
                && similar(list1, list2, shift, LENGTH_COMPARATOR)
                && similar(list1, list2, shift, CURVATURE_COMPARATOR);
    }

    private static final Comparator<CirculinearElement2D> TYPE_COMPARATOR = (o1, o2) -> {
        return o1.getClass().getSimpleName().compareTo(o2.getClass().getSimpleName());
    };
    private static final Comparator<CirculinearElement2D> LENGTH_COMPARATOR = (o1, o2) -> {
        return compare(o1.length(), o2.length());
    };
    private static final Comparator<CirculinearElement2D> CURVATURE_COMPARATOR = (o1, o2) -> {
        if (o1 instanceof Circle2D && o2 instanceof Circle2D) {
            compare(((Circle2D) o1).radius(), ((Circle2D) o2).radius());
        }
        if (o1 instanceof CircleArc2D && o2 instanceof CircleArc2D) {
            compare(((CircleArc2D) o1).supportingCircle().radius(), ((CircleArc2D) o2).supportingCircle().radius());
        }
        if (o1 instanceof Ellipse2D && o2 instanceof Ellipse2D) {
            compare(((Ellipse2D) o1).semiMajorAxisLength(), ((Ellipse2D) o2).semiMajorAxisLength());
        }
        if (o1 instanceof EllipseArc2D && o2 instanceof EllipseArc2D) {
            compare(((EllipseArc2D) o1).getSupportingEllipse().semiMajorAxisLength(), ((EllipseArc2D) o2).getSupportingEllipse().semiMajorAxisLength());
        }
        return 0;
    };

    static int compare(double a, double b) {
        if (Math.abs(a - b) < Tolerance2D.get()) {
            return 0;
        }
        return (int) Math.signum(a - b);
    }

    protected static <T> boolean similar(List<T> list1, List<T> list2, int shift, Comparator<T> comparator) {
        for (int i = 0; i < list1.size(); i++) {
            if (comparator.compare(list1.get(i), list2.get((i + shift) % list1.size())) != 0) {
                return false;
            }
        }
        return true;
    }

    protected static List<CirculinearElement2D> shifted(List<CirculinearElement2D> elements, int shift) {
        if (shift == 0) {
            return elements;
        }
        List<CirculinearElement2D> out = new ArrayList<>();
        for (int i = 0; i < elements.size(); i++) {
            out.set(i, elements.get((i + shift) % elements.size()));
        }
        return out;
    }

    protected static Point2D[] firstTwoPoints(List<CirculinearElement2D> elements) {
        switch (elements.size()) {
            case 1:
                return new Point2D[]{elements.get(0).firstPoint(), elements.get(0).point(elements.get(0).t0() + (elements.get(0).t1() - elements.get(0).t0()) / 2)};
            default:
                return new Point2D[]{elements.get(0).firstPoint(), elements.get(1).firstPoint()};
        }
    }

    public static class SimilaritySet {

        private final CirculinearCurve2D root;
        private final Map<CirculinearCurve2D, Similarity> similarities = new HashMap<>();

        SimilaritySet(CirculinearCurve2D root) {
            this.root = root;
        }

        public CirculinearCurve2D getRoot() {
            return root;
        }

        public Map<CirculinearCurve2D, Similarity> getSimilarities() {
            return similarities;
        }

        public void add(CirculinearCurve2D curve, Similarity similarity) {
            similarities.put(curve, similarity);
        }

        public void addAll(Map<CirculinearCurve2D, Similarity> map) {
            similarities.putAll(map);
        }

    }

    public static class Similarity {

        private final boolean similar;
        private final AffineTransform2D transform;
        private final int shift;

        Similarity(boolean similar, AffineTransform2D transform, int shift) {
            this.similar = similar;
            this.transform = transform;
            this.shift = shift;
        }

        public boolean isSimilar() {
            return similar;
        }

        public AffineTransform2D getTransform() {
            return transform;
        }

        public int getShift() {
            return shift;
        }

    }
}
