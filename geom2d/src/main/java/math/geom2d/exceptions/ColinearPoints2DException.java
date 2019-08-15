/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package math.geom2d.exceptions;

import math.geom2d.Point2D;

/**
 *
 * @author peter
 */
public class ColinearPoints2DException extends Geom2DException {

    private final Point2D p1, p2, p3;

    public ColinearPoints2DException(Point2D p1, Point2D p2, Point2D p3) {
        super("Points are colinear");
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
    }

}
