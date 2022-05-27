/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package math.geom3d.circulinear;

import java.util.Collection;
import math.geom3d.Box3D;
import math.geom3d.curve.CurveSet3D;
import math.geom3d.curve.ContinuousCurve3D;

/**
 *
 * @author peter
 */
public interface CirculinearContinuousCurve3D extends CirculinearCurve3D, ContinuousCurve3D {


    /**
     * Returns a set of circulinear elements, which are basis for circulinear
     * curves.
     *
     * @return
     */
    @Override
    public abstract Collection<? extends CirculinearElement3D> smoothPieces();

//    @Override
//    public CurveSet3D<? extends CirculinearContinuousCurve3D> clip(Box3D box);

    @Override
    public CirculinearContinuousCurve3D subCurve(double t0, double t1);

    @Override
    public CirculinearContinuousCurve3D reverseCurve();
}
