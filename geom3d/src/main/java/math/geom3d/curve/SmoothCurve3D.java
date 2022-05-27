/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package math.geom3d.curve;

import math.geom3d.Box3D;
import math.geom3d.Vector3D;
import math.geom3d.transform.AffineTransform3D;

/**
 *
 * @author peter
 */
public interface SmoothCurve3D extends ContinuousCurve3D {

    /**
     * Returns the tangent of the curve at the given position.
     *
     * @param t a position on the curve
     * @return the tangent vector computed for position t
     * @see #normal(double)
     */
    public abstract Vector3D tangent(double t);

    /* (non-Javadoc)
	 * @see math.geom2d.curve.Curve2D#reverse()
     */
    @Override
    public abstract SmoothCurve3D reverseCurve();

    /* (non-Javadoc)
	 * @see math.geom2d.curve.Curve2D#subCurve(double, double)
     */
    @Override
    public abstract SmoothCurve3D subCurve(double t0, double t1);

    /* (non-Javadoc)
	 * @see math.geom2d.curve.Curve2D#clip(Box2D)
     */
//    @Override
//    public abstract CurveSet3D<? extends SmoothCurve3D> clip(Box3D box);

    /* (non-Javadoc)
	 * @see math.geom2d.curve.Curve2D#transform(AffineTransform2D)
     */
    @Override
    public abstract SmoothCurve3D transform(AffineTransform3D trans);
}
