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
import math.geom2d.Point2D;
import math.geom2d.polygon.Polygon2D;
import math.geom2d.polygon.SimplePolygon2D;

/**
 *
 * @author peter
 */
public class Sheet2D {

    private final PolygonFastIntersector intersector;
    private final Set<Point2D> vertices;
    private final List<Placement2D> placements;

    public Sheet2D() {
        this.intersector = new PolygonFastIntersector();
        this.vertices = Sets.newLinkedHashSet();
        this.placements = new ArrayList<>();
    }

// size, list of placements
    public void place(Placement2D placement) {

    }

    public Polygon2D getConvexHull() {
        return placements.isEmpty() ? new SimplePolygon2D() : placements.get(placements.size() - 1).getConvexHull(this);
    }

    public Set<Point2D> vertices() {
        return this.vertices;
    }

    public boolean feasible(Placement2D placement) {

        return false;
    }
}
