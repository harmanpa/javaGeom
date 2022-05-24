/**
 * File: 	AbstractContinuousCurve3D.java
 * Project: javaGeom
 *
 * Distributed under the LGPL License.
 *
 * Created: 21 mai 09
 */
package math.geom3d.curve;

import java.util.ArrayList;
import java.util.Collection;
import math.geom3d.Point3D;
import math.geom3d.exceptions.UnboundedShape3DException;
import math.geom3d.polygon.LinearCurve3D;
import math.geom3d.polygon.NonPlanarLinearRing3D;
import math.geom3d.polygon.Polyline3D;


/**
 * Provides a base implementation for continuous curves.
 *
 * @author dlegland
 */
public abstract class AbstractContinuousCurve3D
        implements ContinuousCurve3D {

    /**
     * Wrap the given curve into an array list with the appropriate generic.
     * @param <T>
     * @param curve
     * @return 
     */
    protected static <T extends ContinuousCurve3D> Collection<T> wrapCurve(T curve) {
        ArrayList<T> list = new ArrayList<>(1);
        list.add(curve);
        return list;
    }
    
    /**
     * Converts this continuous curve to an instance of LinearCurve3D with the
     * given number of edges. Returns either an instance of Polyline3D or
     * LinearRing3D, depending on the curve is closed or not. This method can be
     * overridden to return the correct type.
     *
     * @see math.geom2d.curve.ContinuousCurve3D#asPolyline(int)
     */
    @Override
    public LinearCurve3D asPolyline(int n) {
        // Check that the curve is bounded
        if (!this.isBounded()) {
            throw new UnboundedShape3DException(this);
        }

        if (this.isClosed()) {
            return asPolylineClosed(n);
        } else {
            return asPolylineOpen(n);
        }
    }

    /**
     * Assumes the curve is open, and returns an instance of Polyline3D.
     *
     * @param n the number of edges of the resulting polyline
     * @return a new Polyline3D approximating the original curve
     */
    protected Polyline3D asPolylineOpen(int n) {
        // Check that the curve is bounded
        if (!this.isBounded()) {
            throw new UnboundedShape3DException(this);
        }

        // compute start and increment values
        double t0 = this.getT0();
        double dt = (this.getT1() - t0) / n;

        // allocate array of points, and compute each value.
        // Computes also value for last point.
        Point3D[] points = new Point3D[n + 1];
        for (int i = 0; i < n + 1; i++) {
            points[i] = this.point(t0 + i * dt);
        }

        return new Polyline3D(points);
    }

    /**
     * Assumes the curve is closed, and returns an instance of LinearRing3D.
     *
     * @param n the number of edges of the resulting linear ring
     * @return a new LinearRing3D approximating the original curve
     */
    protected NonPlanarLinearRing3D asPolylineClosed(int n) {
        // Check that the curve is bounded
        if (!this.isBounded()) {
            throw new UnboundedShape3DException(this);
        }

        // compute start and increment values
        double t0 = this.getT0();
        double dt = (this.getT1() - t0) / n;

        // compute position of points, without the last one, 
        // which is included by default with linear rings
        Point3D[] points = new Point3D[n];
        for (int i = 0; i < n; i++) {
            points[i] = this.point(t0 + i * dt);
        }

        return new NonPlanarLinearRing3D(points);
    }

    /* (non-Javadoc)
	 * @see math.geom2d.curve.Curve3D#getContinuousCurves()
     */
    @Override
    public Collection<? extends ContinuousCurve3D> continuousCurves() {
        return wrapCurve(this);
    }

    /* (non-Javadoc)
	 * @see math.geom2d.curve.Curve3D#getFirstPoint()
     */
    @Override
    public Point3D firstPoint() {
        double t0 = this.getT0();
        if (Double.isInfinite(t0)) {
            throw new UnboundedShape3DException(this);
        }
        return this.point(t0);
    }


    /* (non-Javadoc)
	 * @see math.geom2d.curve.Curve3D#getLastPoint()
     */
    @Override
    public Point3D lastPoint() {
        double t1 = this.getT1();
        if (Double.isInfinite(t1)) {
            throw new UnboundedShape3DException(this);
        }
        return this.point(t1);
    }
}
