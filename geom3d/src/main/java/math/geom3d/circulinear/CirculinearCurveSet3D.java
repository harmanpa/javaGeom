/**
 * File: 	CirculinearCurveSet2D.java
 * Project: javaGeom
 *
 * Distributed under the LGPL License.
 *
 * Created: 11 mai 09
 */
package math.geom3d.circulinear;

import math.geom3d.curve.CurveSet3D;

/**
 * A specialization of CurveSet2D that accepts only instances of
 * CirculinearCurve2D.
 *
 * @author dlegland
 *
 */
public interface CirculinearCurveSet3D<T extends CirculinearCurve3D>
        extends CurveSet3D<T>, CirculinearCurve3D {

    // ===================================================================
    // methods implementing the CirculinearCurve2D interface
//    public CirculinearCurveSet3D<? extends CirculinearCurve3D> clip(Box2D box);

    @Override
    public CirculinearCurveSet3D<? extends CirculinearCurve3D> subCurve(
            double t0, double t1);

    @Override
    public CirculinearCurveSet3D<? extends CirculinearCurve3D> reverseCurve();
}
