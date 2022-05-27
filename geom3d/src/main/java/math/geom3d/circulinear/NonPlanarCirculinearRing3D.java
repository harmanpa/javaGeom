/**
 * File: 	GenericCirculinearRing3D.java
 * Project: javaGeom
 *
 * Distributed under the LGPL License.
 *
 * Created: 11 mai 09
 */
package math.geom3d.circulinear;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import math.geom3d.transform.AffineTransform3D;


/**
 * A basic implementation of a CirculinearRing3D, which is assumed to be always
 * bounded and closed.
 *
 * @author dlegland
 *
 */
public class NonPlanarCirculinearRing3D
        extends PolyCirculinearCurve3D<CirculinearElement3D>
        implements CirculinearRing3D {
//TODO: parameterize with curve type ?

    // ===================================================================
    // static methods
    /**
     * Static factory for creating a new GenericCirculinearRing3D from a
     * collection of curves.
     *
     * @since 0.8.1
     */
    /*public static <T extends CirculinearElement3D> GenericCirculinearRing3D
    create(Collection<T> curves) {
    	return new GenericCirculinearRing3D(curves);
    }*/
    /**
     * Static factory for creating a new GenericCirculinearRing3D from an array
     * of curves.
     *
     * @since 0.8.1
     */
    public static NonPlanarCirculinearRing3D create(
            CirculinearElement3D... curves) {
        return new NonPlanarCirculinearRing3D(curves);
    }

    // ===================================================================
    // constructors
    public NonPlanarCirculinearRing3D() {
        super();
        this.closed = true;
    }

    public NonPlanarCirculinearRing3D(int size) {
        super(size);
        this.closed = true;
    }

    public NonPlanarCirculinearRing3D(CirculinearElement3D... curves) {
        super(curves);
        this.closed = true;
    }

    public NonPlanarCirculinearRing3D(
            Collection<? extends CirculinearElement3D> curves) {
        super(curves);
        this.closed = true;
    }

    public NonPlanarCirculinearRing3D(CirculinearCurve3D curve) {
        super(curve.continuousCurves().stream()
                .flatMap(cc -> cc.smoothPieces().stream())
                .collect(Collectors.toList()));
        this.closed = true;
    }


    @Override
    public NonPlanarCirculinearRing3D reverseCurve() {
        int n = curves.size();
        // create array of reversed curves
        CirculinearElement3D[] curves2 = new CirculinearElement3D[n];

        // reverse each curve
        for (int i = 0; i < n; i++) {
            curves2[i] = curves.get(n - 1 - i).reverseCurve();
        }

        // create the reversed final curve
        return new NonPlanarCirculinearRing3D(curves2);
    }

    @Override
    public NonPlanarCirculinearRing3D transform(AffineTransform3D trans) {
        return new NonPlanarCirculinearRing3D(curves.stream().map(c -> c.transform(trans)).collect(Collectors.toList()));
    }

}
