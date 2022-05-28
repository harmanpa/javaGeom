/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package math.geom3d.plane;

import java.util.Collection;
import java.util.stream.Collectors;
import math.geom2d.curve.Curve2D;
import math.geom3d.Point3D;
import math.geom3d.curve.ContinuousCurve3D;
import math.geom3d.curve.Curve3D;
import math.geom3d.line.LinearShape3D;
import math.geom3d.transform.AffineTransform3D;

/**
 *
 * @author peter
 * @param <T>
 */
public class PlanarCurve3D<T extends Curve2D> extends PlanarShape3D<T> implements Curve3D {
    
    public PlanarCurve3D(Plane3D plane, T shape) {
        super(plane, shape);
    }
    
    @Override
    public double getT0() {
        return getShape().t0();
    }
    
    @Override
    public double getT1() {
        return getShape().t1();
    }
    
    @Override
    public Point3D point(double t) {
        return getPlane().point(getShape().point(t));
    }
    
    @Override
    public Point3D firstPoint() {
        return getPlane().point(getShape().firstPoint());
    }
    
    @Override
    public Point3D lastPoint() {
        return getPlane().point(getShape().lastPoint());
    }
    
    @Override
    public Collection<Point3D> singularPoints() {
        return getShape().singularPoints().stream()
                .map(sp -> getPlane().point(sp))
                .collect(Collectors.toList());
    }
    
    @Override
    public double position(Point3D point) {
        return getShape().position(getPlane().pointPosition(point));
    }
    
    @Override
    public double project(Point3D point) {
        return getShape().project(getPlane().pointPosition(getPlane().projectPoint(point)));
    }
    
    @Override
    public Curve3D reverseCurve() {
        return new PlanarCurve3D(getPlane(), getShape().reverse());
    }
    
    @Override
    public Collection<? extends ContinuousCurve3D> continuousCurves() {
        return getShape().continuousCurves().stream()
                .map(cc2 -> new PlanarContinuousCurve3D(getPlane(), cc2))
                .collect(Collectors.toList());
    }
    
    @Override
    public Curve3D subCurve(double t0, double t1) {
        return new PlanarCurve3D(getPlane(), getShape().subCurve(t0, t1));
    }
    
    @Override
    public Curve3D transform(AffineTransform3D trans) {
        return (Curve3D) super.transform(trans);
    }

    @Override
    public Collection<Point3D> intersections(LinearShape3D line) {
        
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}
