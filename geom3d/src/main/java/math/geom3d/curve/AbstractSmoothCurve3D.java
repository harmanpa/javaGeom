/**
 * File: 	AbstractSmoothCurve3D.java
 * Project: javaGeom
 *
 * Distributed under the LGPL License.
 *
 * Created: 21 mai 09
 */
package math.geom3d.curve;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import math.geom3d.GeometricObject3D;
import math.geom3d.Point3D;
import math.geom3d.Vector3D;

/**
 * Provides a base implementation for smooth curves.
 *
 * @author dlegland
 */
public abstract class AbstractSmoothCurve3D extends AbstractContinuousCurve3D
        implements SmoothCurve3D {


    /* (non-Javadoc)
	 * @see math.geom2d.curve.ContinuousCurve3D#leftTangent(double)
     */
    @Override
    public Vector3D leftTangent(double t) {
        return this.tangent(t);
    }

    /* (non-Javadoc)
	 * @see math.geom2d.curve.ContinuousCurve3D#rightTangent(double)
     */
    @Override
    public Vector3D rightTangent(double t) {
        return this.tangent(t);
    }

    /* (non-Javadoc)
	 * @see math.geom2d.curve.ContinuousCurve3D#smoothPieces()
     */
    @Override
    public Collection<? extends SmoothCurve3D> smoothPieces() {
        return wrapCurve(this);
    }

    /**
     * Returns an empty set of Point3D, as a smooth curve does not have singular
     * points by definition.
     *
     * @see math.geom2d.curve.Curve3D#singularPoints()
     */
    @Override
    public Collection<Point3D> singularPoints() {
        return new ArrayList<>(0);
    }

    /**
     * Returns a set of Point3D, containing the extremities of the curve if they
     * are not infinite.
     *
     * @see math.geom2d.curve.Curve3D#vertices()
     */
    public Collection<Point3D> vertices() {
        List<Point3D> array = new ArrayList<>(2);
        if (!Double.isInfinite(this.getT0())) {
            array.add(this.firstPoint());
        }
        if (!Double.isInfinite(this.getT1())) {
            array.add(this.lastPoint());
        }
        return array;
    }

    /**
     * Returns always false, as a smooth curve does not have singular points by
     * definition.
     *
     * @see math.geom2d.curve.Curve3D#isSingular(double)
     */
    public boolean isSingular(double pos) {
        return false;
    }

    @Override
    public boolean almostEquals(GeometricObject3D obj, double eps) {
        return GeometricObject3D.almostEquals(this, obj, eps);
    }

    @Override
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public boolean equals(Object obj) {
        return GeometricObject3D.equals(this, obj);
    }

    @Override
    public int hashCode() {
        int hash = 477;
        return hash;
    }

}
