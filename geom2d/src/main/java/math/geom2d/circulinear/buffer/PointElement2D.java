/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package math.geom2d.circulinear.buffer;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.util.Arrays;
import java.util.Collection;
import math.geom2d.AffineTransform2D;
import math.geom2d.Box2D;
import math.geom2d.GeometricObject2D;
import math.geom2d.Point2D;
import math.geom2d.Tolerance2D;
import math.geom2d.Vector2D;
import math.geom2d.circulinear.CirculinearContinuousCurve2D;
import math.geom2d.circulinear.CirculinearDomain2D;
import math.geom2d.circulinear.CirculinearElement2D;
import math.geom2d.circulinear.GenericCirculinearDomain2D;
import math.geom2d.conic.Circle2D;
import math.geom2d.curve.CurveSet2D;
import math.geom2d.domain.SmoothOrientedCurve2D;
import math.geom2d.line.LinearShape2D;
import math.geom2d.polygon.LinearCurve2D;
import math.geom2d.transform.CircleInversion2D;

/**
 *
 * @author peter
 */
public class PointElement2D implements CirculinearElement2D {

    private final Point2D point;

    public PointElement2D(Point2D point) {
        this.point = point;
    }

    @Override
    public CirculinearElement2D parallel(double d) {
        return null;
    }

    @Override
    public CirculinearElement2D transform(CircleInversion2D inv) {
        return null;
    }

    @Override
    public CurveSet2D<? extends CirculinearElement2D> clip(Box2D box) {
        return null;
    }

    @Override
    public CirculinearElement2D subCurve(double t0, double t1) {
        return this;
    }

    @Override
    public CirculinearElement2D reverse() {
        return this;
    }

    @Override
    public boolean containsProjection(Point2D p) {
        return p.almostEquals(point, Tolerance2D.get());
    }

    @Override
    public Collection<? extends CirculinearElement2D> smoothPieces() {
        return Arrays.asList(this);
    }

    @Override
    public double length() {
        return 0;
    }

    @Override
    public double length(double pos) {
        return 0;
    }

    @Override
    public double position(double distance) {
        return 0;
    }

    @Override
    public Collection<? extends CirculinearContinuousCurve2D> continuousCurves() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public CirculinearDomain2D buffer(double dist) {
        return new GenericCirculinearDomain2D(new Circle2D(point, dist));
    }

    @Override
    public boolean contains(double x, double y) {
        return contains(new Point2D(x, y));
    }

    @Override
    public boolean contains(Point2D p) {
        return p.almostEquals(point, Tolerance2D.get());
    }

    @Override
    public double distance(Point2D p) {
        return p.distance(point);
    }

    @Override
    public double distance(double x, double y) {
        return distance(new Point2D(x, y));
    }

    @Override
    public boolean isBounded() {
        return true;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public Box2D boundingBox() {
        return point.boundingBox();
    }

    @Override
    public void draw(Graphics2D g2) {

    }

    @Override
    public boolean almostEquals(GeometricObject2D obj, double eps) {
        if (obj instanceof PointElement2D) {
            return obj.almostEquals(point, eps);
        }
        if (obj instanceof Point2D) {
            return point.almostEquals(obj, eps);
        }
        return false;
    }

    @Override
    public double t0() {
        return 0;
    }

    @Override
    public double t1() {
        return 0;
    }

    @Override
    public Point2D point(double t) {
        return point;
    }

    @Override
    public Point2D firstPoint() {
        return point;
    }

    @Override
    public Point2D lastPoint() {
        return point;
    }

    @Override
    public Collection<Point2D> singularPoints() {
        return Arrays.asList(point);
    }

    @Override
    public Collection<Point2D> vertices() {
        return Arrays.asList(point);
    }

    @Override
    public boolean isSingular(double pos) {
        return true;
    }

    @Override
    public double position(Point2D point) {
        return point.almostEquals(point, Tolerance2D.get()) ? 0 : Double.NaN;
    }

    @Override
    public double project(Point2D point) {
        return 0;
    }

    @Override
    public Collection<Point2D> intersections(LinearShape2D line) {
        if (line.contains(point)) {
            return Arrays.asList(point);
        }
        return Arrays.asList();
    }

    @Override
    public Shape asAwtShape() {
        return null;
    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public Vector2D leftTangent(double t) {
        return null;
    }

    @Override
    public Vector2D rightTangent(double t) {
        return null;
    }

    @Override
    public double curvature(double t) {
        return 0;
    }

    @Override
    public LinearCurve2D asPolyline(int n) {
        return null;
    }

    @Override
    public GeneralPath appendPath(GeneralPath path) {
        return path;
    }

    @Override
    public double windingAngle(Point2D point) {
        return 0;
    }

    @Override
    public double signedDistance(Point2D point) {
        return point.distance(this.point);
    }

    @Override
    public double signedDistance(double x, double y) {
        return signedDistance(new Point2D(x, y));
    }

    @Override
    public boolean isInside(Point2D pt) {
        return false;
    }

    @Override
    public SmoothOrientedCurve2D transform(AffineTransform2D trans) {
        return new PointElement2D(trans.transform(point));
    }

    @Override
    public Vector2D tangent(double t) {
        return null;
    }

    @Override
    public Vector2D normal(double t) {
        return null;
    }

}
