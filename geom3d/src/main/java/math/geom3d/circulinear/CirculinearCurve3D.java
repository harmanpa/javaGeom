/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package math.geom3d.circulinear;

import java.util.Collection;
import math.geom3d.curve.Curve3D;

/**
 *
 * @author peter
 */
public interface CirculinearCurve3D extends CirculinearShape3D, Curve3D {

    /**
     * @return the length of the curve
     */
    public double length();

    /**
     * @param pos
     * @return the length from the beginning to the position given by pos
     */
    public double length(double pos);

    /**
     * @param distance
     * @return the position located at a given geodesic distance from the origin
     */
    public double position(double distance);

    /**
     * Returns the collection of continuous circulinear curves which constitute
     * this curve.
     *
     * @return a collection of continuous circulinear curves.
     */
    @Override
    public Collection<? extends CirculinearContinuousCurve3D> continuousCurves();
//
//    public CurveSet3D<? extends CirculinearCurve3D> clip(Box2D box);
    @Override
    public CirculinearCurve3D subCurve(double t0, double t1);

    public CirculinearCurve3D reverseCurve();
}
