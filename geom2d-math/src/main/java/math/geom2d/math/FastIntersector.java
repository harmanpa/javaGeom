/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package math.geom2d.math;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import math.geom2d.Box2D;
import math.geom2d.Point2D;
import math.geom2d.circulinear.CirculinearCurve2D;
import math.geom2d.circulinear.CirculinearCurves2D;
import math.geom2d.circulinear.CirculinearElement2D;
import org.joml.AABBf;
import pl.pateman.dynamicaabbtree.AABBTree;
import pl.pateman.dynamicaabbtree.Boundable;
import pl.pateman.dynamicaabbtree.Identifiable;

/**
 *
 * @author peter
 */
public class FastIntersector {

    private final List<CirculinearElement2D> elements;
    private final AABBTree<CirculinearElement2DBoundable> tree;

    public FastIntersector(Set<CirculinearCurve2D> curves) {
        this(curves.stream().flatMap(curve -> curve.continuousCurves().stream().flatMap(ccc -> ccc.smoothPieces().stream())).collect(Collectors.toList()));
    }

    public FastIntersector(Set<CirculinearCurve2D> curves, Set<CirculinearCurve2D> excluding) {
        this(ImmutableList.copyOf(
                Sets.difference(
                        curves.stream().flatMap(curve -> curve.continuousCurves().stream()
                        .flatMap(ccc -> ccc.smoothPieces().stream())).collect(Collectors.toSet()),
                        excluding.stream().flatMap(curve -> curve.continuousCurves().stream()
                        .flatMap(ccc -> ccc.smoothPieces().stream())).collect(Collectors.toSet()))));
    }

    public FastIntersector(CirculinearCurve2D curve) {
        this(curve.continuousCurves().stream().flatMap(ccc -> ccc.smoothPieces().stream()).collect(Collectors.toList()));
    }

    public FastIntersector(List<CirculinearElement2D> elements) {
        this.elements = elements;
        this.tree = new AABBTree<>();
        for (int i = 0; i < elements.size(); i++) {
            this.tree.add(new CirculinearElement2DBoundable(i, elements.get(i)));
        }
    }

    public boolean isEmpty() {
        return this.tree.size() == 0;
    }

    public boolean intersects(CirculinearCurve2D curve) {
        return !isEmpty() && intersects(curve.continuousCurves().stream().flatMap(ccc -> ccc.smoothPieces().stream()).collect(Collectors.toList()), new AABBf(), new ArrayList<>());
    }

    private boolean intersects(List<CirculinearElement2D> elements, AABBf dest, List<CirculinearElement2DBoundable> candidates) {
        return elements.stream().anyMatch(element -> intersects(element, dest, candidates));
    }

    private boolean intersects(CirculinearElement2D element, AABBf dest, List<CirculinearElement2DBoundable> candidates) {
        candidates.clear();
        this.tree.detectOverlaps(getAABB(element.boundingBox(), dest), candidates);
        return candidates.stream().anyMatch(candidate -> !CirculinearCurves2D.findIntersections(element, candidate.getShape()).isEmpty());
    }

    public List<Point2D> findIntersections(CirculinearCurve2D curve) {
        List<Point2D> intersections = new ArrayList<>();
        if (!isEmpty()) {
            findIntersections(curve.continuousCurves().stream().flatMap(ccc -> ccc.smoothPieces().stream()).collect(Collectors.toList()), new AABBf(), new ArrayList<>(), intersections);
        }
        return intersections;
    }

    private void findIntersections(List<CirculinearElement2D> elements, AABBf dest, List<CirculinearElement2DBoundable> candidates, List<Point2D> intersections) {
        elements.forEach(element -> findIntersections(element, dest, candidates, intersections));
    }

    private void findIntersections(CirculinearElement2D element, AABBf dest, List<CirculinearElement2DBoundable> candidates, List<Point2D> intersections) {
        candidates.clear();
        this.tree.detectOverlaps(getAABB(element.boundingBox(), dest), candidates);
        candidates.forEach(candidate -> intersections.addAll(CirculinearCurves2D.findIntersections(element, candidate.getShape())));
    }

    public Stream<CirculinearElement2D> streamIntersections(CirculinearElement2D with) {
        List<CirculinearElement2DBoundable> candidates = new ArrayList<>();
        this.tree.detectOverlaps(getAABB(with.boundingBox(), null), candidates);
        return candidates.stream()
                .map(candidate -> candidate.getShape())
                .filter(shape -> !CirculinearCurves2D.findIntersections(with, shape).isEmpty());
    }

    public List<CirculinearElement2D> getIntersected(Collection<CirculinearElement2D> elements) {
        if (isEmpty()) {
            return Arrays.asList();
        }
        List<CirculinearElement2DBoundable> candidates = new ArrayList<>();
        AABBf dest = new AABBf();
        return elements.stream().filter(element -> intersects(element, dest, candidates)).collect(Collectors.toList());
    }

    static AABBf getAABB(Box2D box, AABBf dest) {
        if (dest == null) {
            dest = new AABBf();
        }
        dest.setMin((float) box.getMinX(), (float) box.getMinY(), 0.0f);
        dest.setMax((float) box.getMaxX(), (float) box.getMaxY(), 0.0f);
        return dest;
    }

    static class CirculinearElement2DBoundable implements Boundable, Identifiable {

        private final int id;
        private final CirculinearElement2D shape;
        private final Box2D box;

        public CirculinearElement2DBoundable(int id, CirculinearElement2D shape) {
            this.id = id;
            this.shape = shape;
            this.box = shape.boundingBox();
        }

        public CirculinearElement2D getShape() {
            return shape;
        }

        public Box2D getBox() {
            return box;
        }

        @Override
        public AABBf getAABB(AABBf dest) {
            return FastIntersector.getAABB(box, dest);
        }

        @Override
        public long getID() {
            return id;
        }

    }

}
