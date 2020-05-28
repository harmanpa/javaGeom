/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tech.cae.binpacking;

import math.geom2d.polygon.Polygon2D;

/**
 *
 * @author peter
 */
public class Placement2D {

    private final Polygon2D part;
    private final Polygon2D outset;
    private Polygon2D hull;

    public Placement2D(Polygon2D part, Polygon2D outset) {
        this.part = part;
        this.outset = outset;
    }

    public Polygon2D getConvexHull(Sheet2D sheet) {
        if (hull == null) {
            hull = PackingUtils.augmentHull(sheet.getConvexHull(), part);
        }
        return hull;
    }

}
