/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package math.geom3d.circulinear;

import math.geom3d.Box3D;
import math.geom3d.Point3D;
import math.geom3d.curve.CurveSet3D;
import math.geom3d.curve.SmoothCurve3D;

/**
 *
 * @author peter
 */
public interface CirculinearElement3D extends CirculinearContinuousCurve3D, SmoothCurve3D {

//    @Override
//    public CurveSet3D<? extends CirculinearElement3D> clip(Box3D box);
    @Override
    public CirculinearElement3D subCurve(double t0, double t1);

    @Override
    public CirculinearElement3D reverseCurve();

    @Override
    public CurveSet3D<? extends CirculinearElement3D> clip(Box3D box);

    /**
     * Returns true if the orthogonal projection of the point <code>p</code> on
     * the supporting shape of this curve (either e straight line or a circle)
     * also belongs to this curve.
     *
     * @param p a point in the plane
     * @return true if the projection of p on the supporting curve also belongs
     * to this curve
     */
    public boolean containsProjection(Point3D p);

}
