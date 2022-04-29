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
import math.geom2d.Vector2D;
import math.geom2d.circulinear.CirculinearCurve2D;
import math.geom2d.circulinear.GenericCirculinearRing2D;
import math.geom2d.line.LineSegment2D;
import math.geom2d.polygon.LinearRing2D;

/**
 *
 * @author peter
 */
public class CircleSector2D implements ClosedShape2D {

    private final CircleArc2D arc;

    public CircleSector2D(CircleArc2D arc) {
        this.arc = arc;
    }

    protected LinearRing2D getInnerTriangle() {
        return new LinearRing2D(
                arc.supportingCircle().center(),
                arc.point(arc.t0()),
                arc.point(arc.t1()));
    }

    protected LinearRing2D getOuterTriangle() {
        Vector2D centreLine = getCentreLine();
        double halfLength = arc.supportingCircle().radius() * Math.tan(Math.abs(arc.getAngleExtent()) / 2);
        Vector2D halfVector = centreLine.rotate(Math.PI / 2).normalize().times(halfLength);
        return new LinearRing2D(
                arc.supportingCircle().center().plus(centreLine),
                arc.supportingCircle().center().plus(centreLine).plus(halfVector),
                arc.supportingCircle().center().plus(centreLine).minus(halfVector));
    }

    protected Vector2D getCentreLine() {
        return new Vector2D(arc.supportingCircle().center(), arc.point(arc.t0()))
                .plus(new Vector2D(arc.supportingCircle().center(), arc.point(arc.t1())))
                .normalize()
                .times(arc.supportingCircle().radius());
    }

    protected CirculinearCurve2D asCirculinear() {
        return new GenericCirculinearRing2D(
                new LineSegment2D(arc.supportingCircle().center(), arc.point(arc.t0())),
                arc,
                new LineSegment2D(arc.point(arc.t1()), arc.supportingCircle().center()));
    }

    @Override
    public double area() {
        return arc.supportingCircle().area() * Math.abs(arc.getAngleExtent() / (2 * Math.PI));
    }

    @Override
    public boolean isInside(Point2D p) {
        return arc.supportingCircle().isInside(p) && getOuterTriangle().isInside(p);
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
        return arc.boundingBox().merge(getInnerTriangle().boundingBox());
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
