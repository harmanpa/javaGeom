/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tech.cae.binpacking;

import math.geom2d.Point2D;
import math.geom2d.polygon.Polygon2D;
import math.geom2d.polygon.Polygons2D;

/**
 *
 * @author peter
 */
public class Placement2D implements Comparable<Placement2D> {

    private final Sheet2D sheet;
    private final Polygon2D part;
    private final Polygon2D outset;
    private Polygon2D hull;

    public Placement2D(Sheet2D sheet, Polygon2D part, Polygon2D outset) {
        this.sheet = sheet;
        this.part = part;
        this.outset = outset;
    }

    public Polygon2D getPart() {
        return part;
    }

    public Polygon2D getOutset() {
        return outset;
    }

    public Polygon2D getConvexHull() {
        if (hull == null) {
            hull = PackingUtils.augmentHull(sheet.getConvexHull(), part);
        }
        return hull;
    }

    public double getArea() {
        Point2D centroid = Polygons2D.computeCentroid(part);
        return getConvexHull().area()
                * (sheet.getPreferX() * Math.abs(centroid.getX())
                + (1 - (sheet.getPreferX()) * Math.abs(centroid.getY())));
    }

    @Override
    public int compareTo(Placement2D o) {
        return Double.compare(getArea(), o.getArea());
    }

}
