/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jgeom.geom2d.contours;

import math.geom2d.Box2D;
import math.geom2d.Point2D;

/**
 *
 * @author peter
 */
public interface HeightFunction {

    public Box2D bounds();

    public double height(Point2D point);
}
