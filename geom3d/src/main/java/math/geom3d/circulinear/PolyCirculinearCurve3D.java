/**
 * File: 	PolyCirculinearCurve3D.java
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
import math.geom3d.curve.Curve3D;
import math.geom3d.curve.ContinuousCurve3D;
import math.geom3d.curve.Curves3D;
import math.geom3d.curve.CurveSet3D;
import math.geom3d.curve.PolyCurve3D;
import math.geom3d.Point3D;
import math.geom3d.Box3D;
import math.geom3d.transform.AffineTransform3D;
import math.geom3d.polygon.Polyline3D;

/**
 * A continuous curve which is composed of several continuous circulinear
 * curves.
 *
 * @author dlegland
 * @param <T>
 *
 */
public class PolyCirculinearCurve3D<T extends CirculinearContinuousCurve3D>
        extends PolyCurve3D<T> implements CirculinearContinuousCurve3D {

    // ===================================================================
    // static constructors
    /**
     * Static factory for creating a new PolyCirculinearCurve3D from a
     * collection of curves.
     *
     * @since 0.8.1
     */
    /*public static <T extends CirculinearContinuousCurve3D> 
    PolyCirculinearCurve3D<T> create(Collection<T> curves) {
    	return new PolyCirculinearCurve3D<T>(curves);
    }*/
    /**
     * Static factory for creating a new PolyCirculinearCurve3D from an array of
     * curves.
     *
     * @param <T>
     * @param curves
     * @return
     * @since 0.8.1
     */
    @SafeVarargs
    public static <T extends CirculinearContinuousCurve3D>
            PolyCirculinearCurve3D<T> create(T... curves) {
        return new PolyCirculinearCurve3D<>(curves);
    }

    /**
     * Static factory for creating a new PolyCirculinearCurve3D from a
     * collection of curves and a flag indicating if the curve is closed.
     *
     * @since 0.9.0
     */
    /*public static <T extends CirculinearContinuousCurve3D> 
    PolyCirculinearCurve3D<T> create(Collection<T> curves, boolean closed) {
    	return new PolyCirculinearCurve3D<T>(curves, closed);
    }*/
    /**
     * Static factory for creating a new PolyCirculinearCurve3D from an array of
     * curves and a flag indicating if the curve is closed.
     *
     * @param <T>
     * @param curves
     * @param closed
     * @return
     * @since 0.9.0
     */
    public static <T extends CirculinearContinuousCurve3D>
            PolyCirculinearCurve3D<T> create(T[] curves, boolean closed) {
        return new PolyCirculinearCurve3D<>(curves, closed);
    }

    /**
     * Static factory for creating a new PolyCirculinearCurve3D from an array of
     * curves and a flag indicating if the curve is closed.
     *
     * @param <T>
     * @param curves
     * @return
     * @since 0.9.0
     */
    @SafeVarargs
    public static <T extends CirculinearContinuousCurve3D>
            PolyCirculinearCurve3D<T> createClosed(T... curves) {
        return new PolyCirculinearCurve3D<>(curves, true);
    }

    // ===================================================================
    // constructors
    public PolyCirculinearCurve3D() {
        super();
    }

    public PolyCirculinearCurve3D(int size) {
        super(size);
    }

    public PolyCirculinearCurve3D(T[] curves) {
        super(curves);
    }

    public PolyCirculinearCurve3D(T[] curves, boolean closed) {
        super(curves, closed);
    }

    public PolyCirculinearCurve3D(Collection<? extends T> curves) {
        super(curves);
    }

    public PolyCirculinearCurve3D(Collection<? extends T> curves, boolean closed) {
        super(curves, closed);
    }

    // ===================================================================
    // methods implementing the CirculinearCurve3D interface

    /* (non-Javadoc)
	 * @see math.geom2d.circulinear.CirculinearCurve3D#length()
     */
    @Override
    public double length() {
        double sum = 0;
        sum = this.curves().stream().map((curve) -> curve.length()).reduce(sum, (accumulator, _item) -> accumulator + _item);
        return sum;
    }

    /* (non-Javadoc)
	 * @see math.geom2d.circulinear.CirculinearCurve3D#length(double)
     */
    @Override
    public double length(double pos) {
        return CirculinearCurves3D.getLength(this, pos);
    }

    /* (non-Javadoc)
	 * @see math.geom2d.circulinear.CirculinearCurve3D#position(double)
     */
    @Override
    public double position(double length) {
        return CirculinearCurves3D.getPosition(this, length);
    }

    // ===================================================================
    // methods implementing the ContinuousCurve3D interface

    /*
     * (non-Javadoc)
     * 
     * @see math.geom2d.CirculinearContinuousCurve3D#smoothPieces()
     */
    @Override
    public Collection<? extends CirculinearElement3D> smoothPieces() {
        // create array for storing result
        List<CirculinearElement3D> result
                = new ArrayList<>();

        // add elements of each curve
        curves.forEach((curve) -> {
            result.addAll(curve.smoothPieces());
        });

        // return the collection
        return result;
    }

    // ===================================================================
    // methods implementing the Curve3D interface
    @Override
    public Collection<? extends PolyCirculinearCurve3D<?>>
            continuousCurves() {
        return wrapCurve(this);
    }

//    @Override
//    public CirculinearCurveSet3D<? extends CirculinearContinuousCurve3D>
//            clip(Box3D box) {
////        // Clip the curve
////        CurveSet3D<? extends Curve3D> set = Curves3D.clipCurve(this, box);
////
////        // Stores the result in appropriate structure
////        int n = set.size();
////        CirculinearCurveArray3D<CirculinearContinuousCurve3D> result
////                = new CirculinearCurveArray3D<>(n);
////
////        // convert the result, class cast each curve
////        set.curves().stream().filter((curve) -> (curve instanceof CirculinearContinuousCurve3D)).forEachOrdered((curve) -> {
////            result.add((CirculinearContinuousCurve3D) curve);
////        });
////
////        // return the new set of curves
////        return result;
//        return null;
//    }

    @Override
    public PolyCirculinearCurve3D<? extends CirculinearContinuousCurve3D>
            reverseCurve() {
        // create array of reversed curves
        int n = curves.size();
        CirculinearContinuousCurve3D[] curves2
                = new CirculinearContinuousCurve3D[n];

        // reverse each curve
        for (int i = 0; i < n; i++) {
            curves2[i] = curves.get(n - 1 - i).reverseCurve();
        }

        // create the reversed final curve
        return PolyCirculinearCurve3D.create(curves2, this.closed);
    }

    @Override
    public PolyCirculinearCurve3D<? extends CirculinearContinuousCurve3D>
            subCurve(double t0, double t1) {
        // Call the superclass method
        PolyCurve3D<? extends ContinuousCurve3D> subcurve
                = super.subCurve(t0, t1);

        // prepare result
        int n = subcurve.size();
        PolyCirculinearCurve3D<CirculinearContinuousCurve3D> result
                = new PolyCirculinearCurve3D<>(n);

        // add each curve after class cast
        for (Curve3D curve : subcurve) {
            if (curve instanceof CirculinearContinuousCurve3D) {
                result.add((CirculinearContinuousCurve3D) curve);
            }
        }

        // return the result
        return result;
    }

}
