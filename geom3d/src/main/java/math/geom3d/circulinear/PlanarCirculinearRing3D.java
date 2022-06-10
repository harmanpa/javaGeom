/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package math.geom3d.circulinear;

import java.util.Collection;
import math.geom2d.circulinear.GenericCirculinearRing2D;
import math.geom3d.Vector3D;
import math.geom3d.curve.ContinuousCurve3D;
import math.geom3d.plane.PlanarCurve3D;
import math.geom3d.plane.Plane3D;
import math.geom3d.polygon.LinearCurve3D;
import math.geom3d.transform.AffineTransform3D;

/**
 *
 * @author peter
 */
public class PlanarCirculinearRing3D extends PlanarCurve3D<GenericCirculinearRing2D> implements CirculinearRing3D {

    public PlanarCirculinearRing3D(Plane3D plane, GenericCirculinearRing2D shape) {
        super(plane, shape);
    }

    @Override
    public Collection<? extends CirculinearElement3D> smoothPieces() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public CirculinearContinuousCurve3D subCurve(double t0, double t1) {
       return map((GenericCirculinearRing2D ring) -> ring.subCurve(t0, t1), CirculinearContinuousCurve3D.class);
    }

    @Override
    public CirculinearContinuousCurve3D reverseCurve() {
        return map((GenericCirculinearRing2D ring) -> ring.reverse(), CirculinearContinuousCurve3D.class);
    }

    @Override
    public double length() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public double length(double pos) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public double position(double distance) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Collection<? extends CirculinearContinuousCurve3D> continuousCurves() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
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
    public LinearCurve3D asPolyline(int n) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public ContinuousCurve3D transform(AffineTransform3D trans) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
