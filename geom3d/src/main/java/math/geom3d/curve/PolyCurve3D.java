/* file : PolyCurve3D.java
 * 
 * Project : geometry
 *
 * ===========================================
 * 
 * This library is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 2.1 of the License, or (at
 * your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY, without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. if not, write to :
 * The Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 * 
 * Created on 1 mai 2006
 *
 */
package math.geom3d.curve;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import math.geom3d.Point3D;
import math.geom3d.GeometricObject3D;
import math.geom3d.Vector3D;
import math.geom3d.transform.AffineTransform3D;
import math.geom3d.polygon.Polyline3D;

/**
 * A PolyCurve3D is a set of piecewise smooth curve arcs, such that the end of a
 * curve is the beginning of the next curve, and such that they do not intersect
 * nor self-intersect.
 * <p>
 *
 * @author dlegland
 */
public class PolyCurve3D<T extends ContinuousCurve3D> extends CurveArray3D<T>
        implements ContinuousCurve3D {

    // ===================================================================
    // static factories
    /**
     * Static factory for creating a new PolyCurve3D from a collection of
     * curves.
     *
     * @since 0.8.1
     */
    /*public static <T extends ContinuousCurve3D> PolyCurve3D<T> create(
    		Collection<T> curves) {
    	return new PolyCurve3D<T>(curves);
    }*/
    /**
     * Static factory for creating a new PolyCurve3D from an array of curves.
     *
     * @since 0.8.1
     */
    @SafeVarargs
    public static <T extends ContinuousCurve3D> PolyCurve3D<T> create(
            T... curves) {
        return new PolyCurve3D<>(curves);
    }

    /**
     * Static factory for creating a new closed PolyCurve3D from an array of
     * curves.
     *
     * @since 0.10.0
     */
    @SafeVarargs
    public static <T extends ContinuousCurve3D> PolyCurve3D<T> createClosed(
            T... curves) {
        return new PolyCurve3D<>(curves, true);
    }

    /**
     * Static factory for creating a new PolyCurve3D from a collection of curves
     * and a flag indicating if the curve is closed or not.
     *
     * @since 0.9.0
     */
    public static <T extends ContinuousCurve3D> PolyCurve3D<T> create(
            Collection<T> curves, boolean closed) {
        return new PolyCurve3D<>(curves, closed);
    }

    /**
     * Static factory for creating a new PolyCurve3D from an array of curves and
     * a flag indicating if the curve is closed or not.
     *
     * @since 0.9.0
     */
    public static <T extends ContinuousCurve3D> PolyCurve3D<T> create(
            T[] curves, boolean closed) {
        return new PolyCurve3D<>(curves, closed);
    }

    /**
     * Shortcut function to convert a curve of class T to a collection of T
     * containing only the input curve.
     */
    protected static <T extends ContinuousCurve3D> Collection<T> wrapCurve(T curve) {
        List<T> list = new ArrayList<>(1);
        list.add(curve);
        return list;
    }

    // ===================================================================
    // class variables
    /**
     * flag for indicating if the curve is closed or not (default is false, for
     * open)
     */
    private final boolean closed;

    // ===================================================================
    // Constructors
    /**
     * Empty constructor.
     */
    public PolyCurve3D() {
        this(0, false);
    }

    public PolyCurve3D(int n, boolean closed) {
        super(n);
        this.closed = closed;
    }

    /**
     * Constructor that reserves space for the specified number of inner curves.
     */
    public PolyCurve3D(int n) {
        this(n, false);
    }

    /**
     * Creates a new PolyCurve3D from the specified list of curves.
     *
     * @param curves the curves that constitutes this PolyCurve3D
     */
    @SafeVarargs
    public PolyCurve3D(T... curves) {
        this(curves, false);
    }

    /**
     * Creates a new closed PolyCurve3D from the specified list of curves.
     *
     * @param curves the curves that constitutes this PolyCurve3D
     */
    public PolyCurve3D(T[] curves, boolean closed) {
        super(curves);
        this.closed = closed;
    }

    /**
     * Creates a new PolyCurve3D from the specified collection of curves.
     *
     * @param curves the curves that constitutes this PolyCurve3D
     */
    public PolyCurve3D(Collection<? extends T> curves) {
        this(curves, false);
    }

    /**
     * Creates a new PolyCurve3D from the specified collection of curves.
     *
     * @param curves the curves that constitutes this PolyCurve3D
     */
    public PolyCurve3D(Collection<? extends T> curves, boolean closed) {
        super(curves);
        this.closed = closed;
    }

    /**
     * Copy constructor of PolyCurve3D.
     *
     * @param polyCurve the polyCurve object to copy.
     */
    public PolyCurve3D(PolyCurve3D<? extends T> polyCurve) {
        this(polyCurve.curves(), polyCurve.closed);
    }

    // ===================================================================
    // Methods implementing the ContinuousCurve3D interface

    /* (non-Javadoc)
	 * @see math.geom2d.curve.ContinuousCurve3D#leftTangent(double)
     */
    @Override
    public double curvature(double t) {
        return this.childCurve(t).curvature(this.localPosition(t));
    }

    /**
     * Returns true if the PolyCurve3D is closed.
     */
    @Override
    public boolean isClosed() {
        return closed;
    }

    /**
     * Converts this PolyCurve3D into a polyline with the given number of edges.
     *
     * @param n the number of edges of the resulting polyline
     * @see Polyline3D
     */
    public Polyline3D asPolyline(int n) {
        // allocate point array
        Point3D[] points = new Point3D[n + 1];

        // get parameterisation bounds
        double t0 = this.getT0();
        double t1 = this.getT1();
        double dt = (t1 - t0) / n;

        // create vertices
        for (int i = 0; i < n; i++) {
            points[i] = this.point(i * dt + t0);
        }
        points[n] = this.lastPoint();

        // return new polyline
        return new Polyline3D(points);
    }

    /**
     * Returns a collection containing only instances of SmoothCurve3D.
     *
     * @return a collection of SmoothCurve3D
     */
    @Override
    public Collection<? extends SmoothCurve3D> smoothPieces() {
        List<SmoothCurve3D> list = new ArrayList<>();
        for (Curve3D curve : this.curves()) {
            list.addAll(PolyCurve3D.getSmoothCurves(curve));
        }
        return list;
    }

    /**
     * Returns a collection containing only instances of SmoothCurve3D.
     *
     * @param curve the curve to decompose
     * @return a collection of SmoothCurve3D
     */
    private static Collection<SmoothCurve3D> getSmoothCurves(Curve3D curve) {
        // create array for result
        List<SmoothCurve3D> array = new ArrayList<>();

        // If curve is smooth, add it to the array and return.
        if (curve instanceof SmoothCurve3D) {
            array.add((SmoothCurve3D) curve);
            return array;
        }

        // Otherwise, iterate on curves of the curve set
        if (curve instanceof CurveSet3D<?>) {
            for (Curve3D curve2 : ((CurveSet3D<?>) curve).curves()) {
                array.addAll(getSmoothCurves(curve2));
            }
            return array;
        }

        if (curve == null) {
            return array;
        }

        throw new IllegalArgumentException("could not find smooth parts of curve with class "
                + curve.getClass().getName());
    }

    // ===================================================================
    // Methods implementing the ContinuousCurve3D interface
    /**
     * Returns a collection of PolyCurve3D that contains only this instance.
     */
    @Override
    public Collection<? extends PolyCurve3D<?>> continuousCurves() {
        return wrapCurve(this);
    }

    /**
     * Returns the reverse curve of this PolyCurve3D.
     */
    @Override
    public PolyCurve3D<? extends ContinuousCurve3D> reverseCurve() {
        // create array for storing reversed curves
        int n = curves().size();
        ContinuousCurve3D[] curves2 = new ContinuousCurve3D[n];

        // reverse each curve
        for (int i = 0; i < n; i++) {
            curves2[i] = curves().get(n - 1 - i).reverseCurve();
        }

        // create the new reversed curve
        return new PolyCurve3D<>(curves2, this.closed);
    }

    /**
     * Returns an instance of PolyCurve3D. If t0>t1 and curve is not closed,
     * return a PolyCurve3D without curves inside.
     */
    @Override
    public PolyCurve3D<? extends ContinuousCurve3D> subCurve(double t0,
            double t1) {
        // check limit conditions
        if (t1 < t0 & !this.isClosed()) {
            return new PolyCurve3D<>();
        }

        // Call the parent method
        CurveSet3D<?> set = super.subCurve(t0, t1);

        // create result object, with appropriate numbe of curves
        PolyCurve3D<ContinuousCurve3D> subCurve
                = new PolyCurve3D<>(set.size());

        // convert to PolySmoothCurve by adding curves, after class cast
        for (Curve3D curve : set.curves()) {
            subCurve.add((ContinuousCurve3D) curve);
        }

        // return the resulting portion of curve
        return subCurve;
    }

    /**
     * Clip the PolyCurve3D by a box. The result is an instance of
     * CurveSet3D<ContinuousCurve3D>, which contains only instances of
     * ContinuousCurve3D. If the PolyCurve3D is not clipped, the result is an
     * instance of CurveSet3D<ContinuousCurve3D>
     * which contains 0 curves.
     */
//    @Override
//    public CurveSet3D<? extends ContinuousCurve3D> clip(Box3D box) {
////        // Clip the curve
////        CurveSet3D<? extends Curve3D> set = Curves3D.clipCurve(this, box);
////
////        // Stores the result in appropriate structure
////        CurveArray3D<ContinuousCurve3D> result
////                = new CurveArray3D<>(set.size());
////
////        // convert the result
////        for (Curve3D curve : set.curves()) {
////            if (curve instanceof ContinuousCurve3D) {
////                result.add((ContinuousCurve3D) curve);
////            }
////        }
////        return result;
//        return null;
//    }
    /**
     * Transforms each smooth piece in this PolyCurve3D and returns a new
     * instance of PolyCurve3D.
     */
    @Override
    public PolyCurve3D<? extends ContinuousCurve3D> transform(
            AffineTransform3D trans) {
        PolyCurve3D<ContinuousCurve3D> result = new PolyCurve3D<>(curves().size(), this.isClosed());
        for (ContinuousCurve3D curve : curves()) {
            result.add(curve.transform(trans));
        }
        return result;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.closed ? 1 : 0);
        hash = 79 * hash + Objects.hashCode(this.curves());
        return hash;
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
    public Vector3D leftTangent(double t) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Vector3D rightTangent(double t) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
