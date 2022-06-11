/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package math.geom3d.circulinear;

import java.util.Collection;
import java.util.stream.Collectors;
import math.geom2d.circulinear.CirculinearContinuousCurve2D;
import math.geom2d.conic.Circle2D;
import math.geom2d.conic.CircleArc2D;
import math.geom3d.Vector3D;
import math.geom3d.conic.Circle3D;
import math.geom3d.conic.CircleArc3D;
import math.geom3d.curve.ContinuousCurve3D;
import math.geom3d.line.LineSegment3D;
import math.geom3d.plane.Plane3D;
import math.geom3d.polygon.LinearCurve3D;
import math.geom3d.transform.AffineTransform3D;

/**
 *
 * @author peter
 */
public class PlanarCirculinearContinuousCurve3D<T extends CirculinearContinuousCurve2D> extends PlanarCirculinearCurve3D<T> implements CirculinearContinuousCurve3D {

    public PlanarCirculinearContinuousCurve3D(Plane3D plane, T shape) {
        super(plane, shape);
    }

    @Override
    public Collection<? extends CirculinearElement3D> smoothPieces() {
        return getShape().smoothPieces().stream().map(sp -> {
            if (sp instanceof Circle2D) {
                return new Circle3D(getPlane(), (Circle2D) sp);
            }
            if (sp instanceof CircleArc2D) {
                return new CircleArc3D(getPlane(), (CircleArc2D) sp);
            }
            return new LineSegment3D(getPlane().point(sp.firstPoint()), getPlane().point(sp.lastPoint()));
        }).map(sp -> (CirculinearElement3D) sp).collect(Collectors.toList());
    }

    @Override
    public CirculinearContinuousCurve3D subCurve(double t0, double t1) {
        return map(c -> c.subCurve(t0, t1), CirculinearContinuousCurve3D.class);
    }

    @Override
    public CirculinearContinuousCurve3D reverseCurve() {
        return map(c -> c.reverse(), CirculinearContinuousCurve3D.class);
    }

    @Override
    public boolean isClosed() {
        return getShape().isClosed();
    }

    @Override
    public Vector3D leftTangent(double t) {
        return getPlane().vector(getShape().leftTangent(t));
    }

    @Override
    public Vector3D rightTangent(double t) {
        return getPlane().vector(getShape().rightTangent(t));
    }

    @Override
    public double curvature(double t) {
        return getShape().curvature(t);
    }

    @Override
    public LinearCurve3D asPolyline(int n) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public ContinuousCurve3D transform(AffineTransform3D trans) {
        return (ContinuousCurve3D) super.transform(trans);
    }

}
