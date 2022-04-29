/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package math.geom2d.conic;

import java.awt.Graphics2D;
import math.geom2d.AffineTransform2D;
import math.geom2d.Box2D;
import math.geom2d.ClosedShape2D;
import math.geom2d.GeometricObject2D;
import math.geom2d.Point2D;
import math.geom2d.Shape2D;
import math.geom2d.Tolerance2D;
import math.geom2d.circulinear.CirculinearCurve2D;
import math.geom2d.circulinear.GenericCirculinearRing2D;
import math.geom2d.line.LineSegment2D;

/**
 *
 * @author peter
 */
public class ArcSegment2D implements ClosedShape2D {

    private final CircleArc2D arc;

    public ArcSegment2D(CircleArc2D arc) {
        this.arc = arc;
    }

    protected CirculinearCurve2D asCirculinear() {
        return new GenericCirculinearRing2D(
                arc,
                new LineSegment2D(arc.point(arc.t1()), arc.point(arc.t0())));
    }

    @Override
    public boolean isInside(Point2D p) {
        return arc.supportingCircle().isInside(p)
                && !(new CircleSector2D(arc).getInnerTriangle().isInside(p));
    }
    
    @Override
    public double area() {
        CircleSector2D sector = new CircleSector2D(arc);
        return sector.area() - sector.getInnerTriangle().area();
    }

    @Override
    public boolean contains(double x, double y) {
        return asCirculinear().contains(x, y);
    }

    @Override
    public boolean contains(Point2D p) {
        return asCirculinear().contains(p);
    }

    @Override
    public double distance(Point2D p) {
        return asCirculinear().distance(p);
    }

    @Override
    public double distance(double x, double y) {
        return asCirculinear().distance(x, y);
    }

    @Override
    public boolean isBounded() {
        return true;
    }

    @Override
    public boolean isEmpty() {
        return Math.abs(arc.getAngleExtent()) > Tolerance2D.get();
    }

    @Override
    public Box2D boundingBox() {
        return asCirculinear().boundingBox();
    }

    @Override
    public Shape2D clip(Box2D box) {
        return asCirculinear().clip(box);
    }

    @Override
    public Shape2D transform(AffineTransform2D trans) {
        return asCirculinear().transform(trans);
    }

    @Override
    public void draw(Graphics2D g2) {
        asCirculinear().draw(g2);
    }

    @Override
    public boolean almostEquals(GeometricObject2D obj, double eps) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
