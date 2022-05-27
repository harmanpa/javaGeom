/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package math.geom3d.conic;

import java.util.Collection;
import math.geom2d.conic.Circle2D;
import math.geom3d.Point3D;
import math.geom3d.Vector3D;
import math.geom3d.circulinear.CirculinearContinuousCurve3D;
import math.geom3d.circulinear.CirculinearElement3D;
import math.geom3d.circulinear.CirculinearRing3D;
import math.geom3d.plane.PlanarContinuousCurve3D;
import math.geom3d.plane.Plane3D;

/**
 *
 * @author peter
 */
public class Circle3D extends PlanarContinuousCurve3D<Circle2D> implements CirculinearRing3D {

    public Circle3D(Point3D centre, Vector3D normal, double radius) {
        super(Plane3D.fromNormal(centre, normal), new Circle2D(0, 0, radius));
    }

    @Override
    public Collection<? extends CirculinearElement3D> smoothPieces() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public CirculinearContinuousCurve3D subCurve(double t0, double t1) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public CirculinearContinuousCurve3D reverseCurve() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
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

}
