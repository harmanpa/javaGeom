/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package math.geom3d.plane;

import java.util.Collection;
import math.geom2d.curve.ContinuousCurve2D;
import math.geom3d.Box3D;
import math.geom3d.Vector3D;
import math.geom3d.curve.ContinuousCurve3D;
import math.geom3d.curve.CurveSet3D;
import math.geom3d.curve.SmoothCurve3D;
import math.geom3d.polygon.LinearCurve3D;
import math.geom3d.transform.AffineTransform3D;

/**
 *
 * @author peter
 * @param <T>
 */
public class PlanarContinuousCurve3D<T extends ContinuousCurve2D> extends PlanarCurve3D<T> implements ContinuousCurve3D {

    public PlanarContinuousCurve3D(Plane3D plane, T shape) {
        super(plane, shape);
    }

    @Override
    public boolean isClosed() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Vector3D leftTangent(double t) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Vector3D rightTangent(double t) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public double curvature(double t) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Collection<? extends SmoothCurve3D> smoothPieces() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public LinearCurve3D asPolyline(int n) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public ContinuousCurve3D reverseCurve() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public ContinuousCurve3D subCurve(double t0, double t1) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public CurveSet3D<? extends ContinuousCurve3D> clip(Box3D box) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public ContinuousCurve3D transform(AffineTransform3D trans) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
