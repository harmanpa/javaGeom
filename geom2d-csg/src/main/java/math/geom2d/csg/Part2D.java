/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package math.geom2d.csg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import math.geom2d.Box2D;
import math.geom2d.Point2D;
import math.geom2d.Tolerance2D;
import math.geom2d.circulinear.CirculinearCurve2D;
import math.geom2d.circulinear.CirculinearCurves2D;
import math.geom2d.circulinear.buffer.BufferCalculator;
import math.geom2d.domain.Boundaries2D;
import math.geom2d.math.Rings2D;

/**
 *
 * @author peter
 */
public class Part2D {

    private final CirculinearCurve2D outline;
    private final List<CirculinearCurve2D> holes;

    public Part2D(CirculinearCurve2D outline, List<CirculinearCurve2D> holes) {
        this.outline = outline;
        this.holes = holes;
    }

    public Part2D(CirculinearCurve2D outline) {
        this(outline, Arrays.asList());
    }

    public CirculinearCurve2D getOutline() {
        return outline;
    }

    public List<CirculinearCurve2D> getHoles() {
        return holes;
    }

//    public PartSet2D outset(double distance) {
//        // Outset the main body
//
//        // Inset each hole, remove any curves that are empty
//    }
//
//    public PartSet2D inset(double distance) {
//        // Inset the main body
//List<CirculinearCurve2D> insets = null;
//        // Outset each hole. Union of all.
//        
//        combineHoles(outline, holes)Rings2D.union(holes, distance)
//        // Any that intersect main body replace with intersection. Subtract from body.
//                
//    }

    public PartSet2D add(Part2D part) {
        return combine(part.getOutline(), part.getHoles(), true);
    }

    public PartSet2D add(CirculinearCurve2D curve) {
        return combine(curve, Arrays.asList(), true);
    }

    public PartSet2D subtract(CirculinearCurve2D curve) {
        return combine(curve, Arrays.asList(), false);
    }

    private PartSet2D combine(CirculinearCurve2D curve, List<CirculinearCurve2D> holes, boolean union) {
        List<Point2D> points = new ArrayList<>(CirculinearCurves2D.findIntersections(outline, curve));
        if (isContained(outline, curve, points)) {
            if (union) {
                // TODO: Special case, what if it is in a hole?
                return combineHoles(outline, holes);
            } else {
                return combineHoles(outline, Arrays.asList(curve));
            }
        }
        if (isContained(curve, outline, points)) {
            if (union) {
                return combineHoles(curve, holes);
            } else {
                return new PartSet2D();
            }
        }
        if (isNonOverlapping(outline, curve, points)) {
            if (union) {
                return new PartSet2D(this, new Part2D(curve, holes));
            } else {
                return new PartSet2D(this);
            }
        }
        if (union) {
            List<CirculinearCurve2D> outlines = Rings2D.union(outline, curve, Tolerance2D.get());
            return combineHoles(outlines.iterator().next(), holes);
        }
        return combineHoles(outline, Arrays.asList(curve));
    }

    private PartSet2D combineHoles(CirculinearCurve2D outline, List<CirculinearCurve2D> newHoles) {
        List<CirculinearCurve2D> containedHoles = new ArrayList<>();
        List<CirculinearCurve2D> intersectingHoles = new ArrayList<>();
        for (CirculinearCurve2D hole : newHoles) {
            List<Point2D> points = new ArrayList<>(CirculinearCurves2D.findIntersections(outline, hole));
            if (isContained(outline, hole, points) || !points.isEmpty()) {
                containedHoles.add(hole);
            } else if (!points.isEmpty()) {
                intersectingHoles.add(hole);
            }
        }
        if (!intersectingHoles.isEmpty()) {
            // Subtract all from the outline and call again
            List<CirculinearCurve2D> outlines = new ArrayList<>();
            outlines.add(outline);
            for (CirculinearCurve2D intersectingHole : intersectingHoles) {
                List<CirculinearCurve2D> newOutlines = new ArrayList<>();
                for (CirculinearCurve2D anOutline : outlines) {
                    newOutlines.addAll(Rings2D.difference(anOutline, intersectingHole, Tolerance2D.get()));
                }
                outlines = newOutlines;
            }
            List<Part2D> parts = new ArrayList<>();
            outlines.forEach(ol -> parts.addAll(combineHoles(ol, containedHoles).getParts()));
            return new PartSet2D(parts);
        } else {
            if (containedHoles.isEmpty()) {
                return new PartSet2D(new Part2D(outline, holes));
            }
            if (holes.isEmpty()) {
                return new PartSet2D(new Part2D(outline, containedHoles));
            }
            return new PartSet2D(new Part2D(outline, combineHoles(containedHoles)));
        }
    }

    private List<CirculinearCurve2D> combineHoles(List<CirculinearCurve2D> newHoles) {
        if (holes.isEmpty()) {
            return newHoles;
        }
        if (newHoles.isEmpty()) {
            return holes;
        }
        List<CirculinearCurve2D> allHoles = new ArrayList<>();
        allHoles.addAll(holes);
        allHoles.addAll(newHoles);
        return Rings2D.union(allHoles, Tolerance2D.get());
    }

    static boolean isContained(CirculinearCurve2D outer, CirculinearCurve2D inner, Collection<Point2D> points) {
        return outer.boundingBox().containsBounds(inner) && points.isEmpty();
    }

    static boolean isNonOverlapping(CirculinearCurve2D outer, CirculinearCurve2D inner, Collection<Point2D> points) {
        Box2D bounds = outer.boundingBox().intersection(inner.boundingBox());
        return bounds.getWidth() < 0 || bounds.getHeight() < 0 || points.isEmpty();
    }
}
