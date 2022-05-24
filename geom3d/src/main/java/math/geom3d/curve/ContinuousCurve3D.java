/**
 * 
 */

package math.geom3d.curve;

import java.util.Collection;
import math.geom3d.Box3D;
import math.geom3d.Vector3D;
import math.geom3d.polygon.LinearCurve3D;
import math.geom3d.transform.AffineTransform3D;

/**
 * @author dlegland
 */
public interface ContinuousCurve3D extends Curve3D {

    // ===================================================================
    // constants
    // ===================================================================
    // general methods
    /**
     * Returns true if the curve makes a loop, that is come back to starting
     * point after covering the path.
     *
     * @return
     */
    public abstract boolean isClosed();

    /**
     * Computes the left tangent at the given position. If the curve is smooth
     * at position <code>t</code>, the result is the same as the tangent
     * computed for the corresponding smooth curve, and is equal to the result
     * of rightTangent(double). If the position <code>t</code> corresponds to a
     * singular point, the tangent of the smooth portion before <code>t</code>
     * is computed.
     *
     * @param t the position on the curve
     * @return the left tangent vector at the curve for position t
     */
    public Vector3D leftTangent(double t);

    /**
     * Computes the right tangent at the given position. If the curve is smooth
     * at position <code>t</code>, the result is the same as the tangent
     * computed for the corresponding smooth curve, and is equal to the result
     * of leftTangent(double). If the position <code>t</code> corresponds to a
     * singular point, the tangent of the smooth portion after <code>t</code> is
     * computed.
     *
     * @param t the position on the curve
     * @return the right tangent vector at the curve for position t
     */
    public Vector3D rightTangent(double t);

    /**
     * Computes the curvature at the given position. The curvature is finite for
     * positions <code>t</code> that correspond to smooth parts, and is infinite
     * for singular points.
     *
     * @param t the position on the curve
     * @return the curvature of the curve for position t
     */
    public abstract double curvature(double t);

    /**
     * Returns a set of smooth curves.
     *
     * @return
     */
    public abstract Collection<? extends SmoothCurve3D> smoothPieces();

//    /**
//     * Returns an approximation of the curve as a polyline with the number of
//     * line segments calculated to minimise error.If the curve is closed, the
//     * method should return an instance of LinearRing3D.Otherwise, it returns an
//     * instance of Polyline3D. The parameter inside indicates whether arcs
//     * should circumscribe the polyline, or vice versa.
//     *
//     * @param maxError
//     * @param inside
//     * @return a polyline with maxError maximum error *
//     */
//    public abstract LinearCurve3D asPolyline(double maxError, boolean inside);
    /**
     * Returns an approximation of the curve as a polyline with <code>n</code>
     * line segments. If the curve is closed, the method should return an
     * instance of LinearRing3D. Otherwise, it returns an instance of
     * Polyline3D.
     *
     * @param n the number of line segments
     * @return a polyline with <code>n</code> line segments.
     */
    public abstract LinearCurve3D asPolyline(int n);

    // ===================================================================
    // Curve3D methods

    /* (non-Javadoc)
	 * @see math.geom2d.curve.Curve3D#reverse(D)
     */
    @Override
    public abstract ContinuousCurve3D reverseCurve();

    /* (non-Javadoc)
	 * @see math.geom2d.curve.Curve3D#subCurve(double, double)
     */
    @Override
    public abstract ContinuousCurve3D subCurve(double t0, double t1);

    // ===================================================================
    // Shape3D methods

    /* (non-Javadoc)
	 * @see math.geom2d.curve.Curve3D#clip(Box3D)
     */
    @Override
    public abstract CurveSet3D<? extends ContinuousCurve3D> clip(Box3D box);

    /* (non-Javadoc)
	 * @see math.geom2d.curve.Curve3D#transform(AffineTransform3D)
     */
    @Override
    public abstract ContinuousCurve3D transform(AffineTransform3D trans);
}
