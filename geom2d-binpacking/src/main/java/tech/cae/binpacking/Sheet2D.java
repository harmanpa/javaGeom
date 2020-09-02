/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tech.cae.binpacking;

import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import math.geom2d.Box2D;
import math.geom2d.Point2D;
import math.geom2d.polygon.Polygon2D;
import math.geom2d.polygon.SimplePolygon2D;

/**
 *
 * @author peter
 */
public class Sheet2D {

    private final Box2D bounds;
    private final PolygonFastIntersector intersector;
    private final Set<Point2D> vertices;
    private final List<Placement2D> placements;
    private final double preferX;

    public Sheet2D(double length, double width, double preferX) {
        this.bounds = new Box2D(0, length, 0, width);
        this.intersector = new PolygonFastIntersector();
        this.vertices = Sets.newLinkedHashSet();
        this.placements = new ArrayList<>();
        this.preferX = preferX;
    }

// size, list of placements
    public void place(Placement2D placement) {
        this.placements.add(placement);
        this.intersector.add(placement.getPart());
        this.vertices.addAll(placement.getPart().vertices());
    }

    public Polygon2D getConvexHull() {
        return placements.isEmpty() ? new SimplePolygon2D() : placements.get(placements.size() - 1).getConvexHull();
    }

    public Set<Point2D> vertices() {
        return this.vertices;
    }

    public double getPreferX() {
        return preferX;
    }

    public boolean feasible(Placement2D placement) {
        // Is the placement actually inside the sheet?
        if (this.bounds.containsBounds(placement.getPart())) {
            // Does it overlap with existing parts?
            return !this.intersector.overlaps(placement.getOutset());
        }
        return false;
    }
}
