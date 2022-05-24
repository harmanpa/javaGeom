/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package math.geom3d.circulinear;

import java.util.Collection;
import math.geom2d.curve.CurveSet2D;
import math.geom3d.Box3D;
import math.geom3d.curve.CurveSet3D;

/**
 *
 * @author peter
 */
public interface CirculinearContinuousCurve3D extends CirculinearCurve3D {

    /**
     * Returns a set of circulinear elements, which are basis for circulinear
     * curves.
     *
     * @return
     */
    public abstract Collection<? extends CirculinearElement3D> smoothPieces();

    public CurveSet3D<? extends CirculinearContinuousCurve3D> clip(Box3D box);

    @Override
    public CirculinearContinuousCurve3D subCurve(double t0, double t1);

    @Override
    public CirculinearContinuousCurve3D reverseCurve();
}
