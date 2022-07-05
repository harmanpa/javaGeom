/**
 * File: 	CirculinearCurveArray3D.java
 * Project: javaGeom
 *
 * Distributed under the LGPL License.
 *
 * Created: 11 mai 09
 */
package math.geom3d.circulinear;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import math.geom3d.Box3D;
import math.geom3d.curve.Curve3D;
import math.geom3d.curve.CurveArray3D;
import math.geom3d.curve.CurveSet3D;
import math.geom3d.curve.Curves3D;

/**
 * A specialization of CurveArray3D that accepts only instances of
 * CirculinearCurve3D.
 *
 * <blockquote><pre>
 * {@code
 * // create two orthogonal lines
 * StraightLine3D line1 = new StraightLine3D(origin, v1);
 * StraightLine3D line2 = new StraightLine3D(origin, v2);
 *
 * // put lines in a set
 * CirculinearCurveSet3D<StraightLine3D> set =
 *     CirculinearCurveArray3D.create(line1, line2);
 * }
 * </pre></blockquote>
 *
 * @author dlegland
 *
 */
public class CirculinearCurveArray3D<T extends CirculinearCurve3D>
        extends CurveArray3D<T> implements CirculinearCurveSet3D<T> {

    // ===================================================================
    // static constructors
    /**
     * Static factory for creating a new CirculinearCurveArray3D from a
     * collection of curves.
     *
     * @since 0.8.1
     */
    /*public static <T extends CirculinearCurve3D> CirculinearCurveArray3D<T> create(
    		Collection<T> curves) {
    	return new CirculinearCurveArray3D<T>(curves);
    }*/
    /**
     * Static factory for creating a new CirculinearCurveArray3D from an array
     * of curves.
     *
     * @since 0.8.1
     */
    @SafeVarargs
    public static <T extends CirculinearCurve3D> CirculinearCurveArray3D<T> create(
            T... curves) {
        return new CirculinearCurveArray3D<>(curves);
    }

    // ===================================================================
    // constructors
    /**
     * Empty constructor. Initializes an empty array of curves.
     */
    public CirculinearCurveArray3D() {
        this(new ArrayList<T>());
    }

    /**
     * Empty constructor. Initializes an empty array of curves, with a given
     * size for allocating memory.
     */
    public CirculinearCurveArray3D(int n) {
        this(new ArrayList<T>(n));
    }

    /**
     * Constructor from an array of curves.
     *
     * @param curves the array of curves in the set
     */
    @SafeVarargs
    public CirculinearCurveArray3D(T... curves) {
        this(new ArrayList<T>(curves.length));
        for (T element : curves) {
            this.add(element);
        }
    }

    /**
     * Constructor from a collection of curves. The curves are added to the
     * inner collection of curves.
     *
     * @param curves the collection of curves to add to the set
     */
    public CirculinearCurveArray3D(Collection<? extends T> curves) {
        super(curves);
    }

    // ===================================================================
    // methods implementing the CirculinearCurve3D interface

    /* (non-Javadoc)
	 * @see math.geom3d.circulinear.CirculinearCurve3D#length()
     */
    @Override
    public double length() {
        double sum = 0;
        for (CirculinearCurve3D curve : this.curves()) {
            sum += curve.length();
        }
        return sum;
    }

    /* (non-Javadoc)
	 * @see math.geom3d.circulinear.CirculinearCurve3D#length(double)
     */
    @Override
    public double length(double pos) {
        return CirculinearCurves3D.getLength(this, pos);
    }

    /* (non-Javadoc)
	 * @see math.geom3d.circulinear.CirculinearCurve3D#position(double)
     */
    @Override
    public double position(double length) {
        return CirculinearCurves3D.getPosition(this, length);
    }

    // ===================================================================
    // methods implementing the Curve3D interface
    @Override
    public Collection<? extends CirculinearContinuousCurve3D>
            continuousCurves() {
        // create array for storing result
        List<CirculinearContinuousCurve3D> result
                = new ArrayList<>();

        // iterate on curves, and extract each set of continuous curves
        for (CirculinearCurve3D curve : curves()) {
            result.addAll(curve.continuousCurves());
        }

        // return the set of curves
        return result;
    }

//    @Override
//    public CirculinearCurveArray3D<? extends CirculinearCurve3D> clip(Box3D box) {
//        // Clip the curve
//        CurveSet3D<? extends Curve3D> set = Curves3D.clipCurve(this, box);
//
//        // Stores the result in appropriate structure
//        int n = set.size();
//        CirculinearCurveArray3D<CirculinearCurve3D> result
//                = new CirculinearCurveArray3D<>(n);
//
//        // convert the result, class cast each curve
//        for (Curve3D curve : set.curves()) {
//            if (curve instanceof CirculinearCurve3D) {
//                result.add((CirculinearCurve3D) curve);
//            }
//        }
//
//        // return the new set of curves
//        return result;
//    }
    @Override
    public CirculinearCurveArray3D<? extends CirculinearCurve3D>
            subCurve(double t0, double t1) {
        // Call the superclass method
        CurveSet3D<? extends Curve3D> subcurve = super.subCurve(t0, t1);

        // prepare result
        CirculinearCurveArray3D<CirculinearCurve3D> result = new CirculinearCurveArray3D<>(subcurve.size());

        // add each curve after class,cast
        for (Curve3D curve : subcurve) {
            if (curve instanceof CirculinearCurve3D) {
                result.add((CirculinearCurve3D) curve);
            } else {
                result.add(CirculinearCurves3D.convert(curve));
            }
        }

        // return the result
        return result;
    }

    @Override
    public CirculinearCurveArray3D<? extends CirculinearCurve3D>
            reverseCurve() {
        int n = curves().size();
        // create array of reversed curves
        CirculinearCurve3D[] curves2 = new CirculinearCurve3D[n];

        // reverse each curve
        for (int i = 0; i < n; i++) {
            curves2[i] = curves().get(n - 1 - i).reverseCurve();
        }

        // create the reversed final curve
        return new CirculinearCurveArray3D<>(curves2);
    }
}
