/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package math.geom3d.curve;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import math.geom2d.Tolerance2D;
import math.geom3d.Box3D;
import math.geom3d.Point3D;
import math.geom3d.Shape3D;
import math.geom3d.transform.AffineTransform3D;

/**
 *
 * @author peter
 */
public class CurveArray3D<T extends Curve3D>
        implements CurveSet3D<T>, Iterable<T> {

    // ===================================================================
    // Static Constructors
    /**
     * Static factory for creating a new CurveArray2D from a collection of
     * curves.
     *
     * @param <T>
     * @param curves
     * @return
     * @since 0.8.1
     */
    public static <T extends Curve3D> CurveArray3D<T> create(Collection<T> curves) {
        return new CurveArray3D<>(curves);
    }

    /**
     * Static factory for creating a new CurveArray2D from an array of curves.
     *
     * @param <T>
     * @param curves
     * @return
     * @since 0.8.1
     */
    @SafeVarargs
    public static <T extends Curve3D> CurveArray3D<T> create(
            T... curves) {
        return new CurveArray3D<>(curves);
    }

    // ===================================================================
    // Class variables
    /**
     * The inner array of curves
     */
    protected ArrayList<T> curves;

    // ===================================================================
    // Constructors
    /**
     * Empty constructor. Initializes an empty array of curves.
     */
    public CurveArray3D() {
        this.curves = new ArrayList<>();
    }

    /**
     * Empty constructor. Initializes an empty array of curves, with a given
     * size for allocating memory.
     *
     * @param n
     */
    public CurveArray3D(int n) {
        this.curves = new ArrayList<>(n);
    }

    /**
     * Constructor from an array of curves.
     *
     * @param curves the array of curves in the set
     */
    @SafeVarargs
    public CurveArray3D(T... curves) {
        this(curves.length);
        this.curves.addAll(Arrays.asList(curves));
    }

    public CurveArray3D(CurveSet3D<? extends T> set) {
        this(set.size());
        for (T curve : set) {
            this.curves.add(curve);
        }
    }

    /**
     * Constructor from a collection of curves. The curves are added to the
     * inner collection of curves.
     *
     * @param curves the collection of curves to add to the set
     */
    public CurveArray3D(Collection<? extends T> curves) {
        this.curves = new ArrayList<>(curves.size());
        this.curves.addAll(curves);
    }

    // ===================================================================
    // methods specific to CurveArray2D
    /**
     * Converts the position on the curve set, which is comprised between 0 and
     * 2*Nc-1 with Nc being the number of curves, to the position on the curve
     * which contains the position. The result is comprised between the t0 and
     * the t1 of the child curve.
     *
     * @see #globalPosition(int, double)
     * @see #curveIndex(double)
     * @param t the position on the curve set
     * @return the position on the subcurve
     */
    @Override
    public double localPosition(double t) {
        int i = this.curveIndex(t);
        T curve = curves.get(i);
        double t0 = curve.getT0();
        double t1 = curve.getT1();
        return Curves3D.fromUnitSegment(t - 2 * i, t0, t1);
    }

    /**
     * Converts a position on a curve (between t0 and t1 of the curve) to the
     * position on the curve set (between 0 and 2*Nc-1).
     *
     * @see #localPosition(double)
     * @see #curveIndex(double)
     * @param i the index of the curve to consider
     * @param t the position on the curve
     * @return the position on the curve set, between 0 and 2*Nc-1
     */
    @Override
    public double globalPosition(int i, double t) {
        T curve = curves.get(i);
        double t0 = curve.getT0();
        double t1 = curve.getT1();
        return Curves3D.toUnitSegment(t, t0, t1) + i * 2;
    }

    /**
     * Returns the index of the curve corresponding to a given position.
     *
     * @param t the position on the set of curves, between 0 and twice the
     * number of curves minus 1
     * @return the index of the curve which contains position t
     */
    @Override
    public int curveIndex(double t) {

        // check bounds
        if (curves.isEmpty()) {
            return 0;
        }
        if (t > curves.size() * 2 - 1) {
            return curves.size() - 1;
        }

        // curve index
        int nc = (int) Math.floor(t);

        // check index if even-> corresponds to a curve
        int indc = (int) Math.floor(nc / 2);
        if (indc * 2 == nc) {
            return indc;
        } else {
            return t - nc < .5 ? indc : indc + 1;
        }
    }

    // ===================================================================
    // Management of curves
    /**
     * Adds the curve to the curve set, if it does not already belongs to the
     * set.
     *
     * @param curve the curve to add
     */
    @Override
    public boolean add(T curve) {
        if (curves.contains(curve)) {
            return false;
        }
        return curves.add(curve);
    }

    @Override
    public void add(int index, T curve) {
        this.curves.add(index, curve);
    }

    /**
     * Removes the specified curve from the curve set.
     *
     * @param curve the curve to remove
     */
    @Override
    public boolean remove(T curve) {
        return curves.remove(curve);
    }

    @Override
    public T remove(int index) {
        return this.curves.remove(index);
    }

    /**
     * Checks if the curve set contains the given curve.
     */
    @Override
    public boolean contains(T curve) {
        return curves.contains(curve);
    }

    /**
     * Returns index of the given curve within the inner array.
     */
    @Override
    public int indexOf(T curve) {
        return this.curves.indexOf(curve);
    }

    /**
     * Clears the inner curve collection.
     */
    @Override
    public void clear() {
        curves.clear();
    }

    /**
     * Returns the collection of curves
     *
     * @return the inner collection of curves
     */
    @Override
    public Collection<T> curves() {
        return curves;
    }

    /**
     * Returns the inner curve corresponding to the given index.
     *
     * @param index index of the curve
     * @return the i-th inner curve
     * @since 0.6.3
     */
    @Override
    public T get(int index) {
        return curves.get(index);
    }

    /**
     * Returns the child curve corresponding to a given position.
     *
     * @param t the position on the set of curves, between 0 and twice the
     * number of curves
     * @return the curve corresponding to the position.
     * @since 0.6.3
     */
    @Override
    public T childCurve(double t) {
        if (curves.isEmpty()) {
            return null;
        }
        return curves.get(curveIndex(t));
    }

    /**
     * Returns the first curve of the collection if it exists, null otherwise.
     *
     * @return the first curve of the collection
     */
    @Override
    public T firstCurve() {
        if (curves.isEmpty()) {
            return null;
        }
        return curves.get(0);
    }

    /**
     * Returns the last curve of the collection if it exists, null otherwise.
     *
     * @return the last curve of the collection
     */
    @Override
    public T lastCurve() {
        if (curves.isEmpty()) {
            return null;
        }
        return curves.get(curves.size() - 1);
    }

    /**
     * Returns the number of curves in the collection
     *
     * @return the number of curves in the collection
     */
    @Override
    public int size() {
        return curves.size();
    }

    /**
     * Returns true if the CurveSet does not contain any curve.
     */
    @Override
    public boolean isEmpty() {
        return curves.isEmpty();
    }

    @Override
    public double getT0() {
        return 0;
    }

    @Override
    public double getT1() {
        return Math.max(curves.size() * 2 - 1, 0);
    }

    @Override
    public Point3D point(double t) {
        if (curves.isEmpty()) {
            return null;
        }
        if (t < getT0()) {
            return this.firstCurve().firstPoint();
        }
        if (t > getT1()) {
            return this.lastCurve().lastPoint();
        }

        // curve index
        int nc = (int) Math.floor(t);

        // check index if even-> corresponds to a curve
        int indc = (int) Math.floor(nc / 2);
        if (indc * 2 == nc) {
            Curve3D curve = curves.get(indc);
            double pos = Curves3D.fromUnitSegment(t - nc,
                    curve.getT0(), curve.getT1());
            return curve.point(pos);
        } else {
            // return either last point of preceding curve,
            // or first point of next curve
            if (t - nc < .5) {
                return curves.get(indc).lastPoint();
            } else {
                return curves.get(indc + 1).firstPoint();
            }
        }
    }

    @Override
    public Point3D firstPoint() {
        if (curves.isEmpty()) {
            return null;
        }
        return firstCurve().firstPoint();
    }

    @Override
    public Point3D lastPoint() {
        if (curves.isEmpty()) {
            return null;
        }
        return lastCurve().lastPoint();
    }

    @Override
    public Collection<Point3D> singularPoints() {
        // create array for result
        List<Point3D> points = new ArrayList<>();
        double eps = Tolerance2D.get();

        // iterate on curves composing the array
        curves.stream().map((curve) -> {
            // Add singular points inside curve
            curve.singularPoints().forEach((point) -> {
                addPointWithGuardDistance(points, point, eps);
            });
            return curve;
        }).map((curve) -> {
            // add first extremity
            if (!Curves3D.isLeftInfinite(curve)) {
                addPointWithGuardDistance(points, curve.firstPoint(), eps);
            }
            // add last extremity
            return curve;
        }).filter((curve) -> (!Curves3D.isRightInfinite(curve))).forEachOrdered((curve) -> {
            addPointWithGuardDistance(points, curve.lastPoint(), eps);
        });
        // return the set of singular points
        return points;
    }
/**
     * Add a point to the set only if the distance between the candidate and the
     * closest point in the set is greater than the given threshold.
     *
     * @param set
     * @param point
     * @param eps
     */
    private void addPointWithGuardDistance(Collection<Point3D> pointSet,
            Point3D point, double eps) {
        for (Point3D p0 : pointSet) {
            if (p0.distance(point)<=eps) {
                return;
            }
        }
        pointSet.add(point);
    }
    
    @Override
    public double position(Point3D point) {
        double minDist = Double.MAX_VALUE, dist;
        double pos = 0, t0, t1;

        int i = 0;
        for (Curve3D curve : curves) {
            dist = curve.distance(point);
            if (dist < minDist) {
                pos = curve.position(point);
                minDist = dist;
                // format position
                t0 = curve.getT0();
                t1 = curve.getT1();
                pos = Curves3D.toUnitSegment(pos, t0, t1) + i * 2;
            }
            i++;
        }
        return pos;
    }

    @Override
    public double project(Point3D point) {
        double minDist = Double.MAX_VALUE, dist;
        double pos = 0, t0, t1;

        int i = 0;
        for (Curve3D curve : curves) {
            dist = curve.distance(point);
            if (dist < minDist) {
                minDist = dist;
                pos = curve.project(point);
                // format position
                t0 = curve.getT0();
                t1 = curve.getT1();
                pos = Curves3D.toUnitSegment(pos, t0, t1) + i * 2;
            }
            i++;
        }
        return pos;
    }

    @Override
    public CurveSet3D<? extends Curve3D> subCurve(double t0, double t1) {
        // number of curves in the set
        int nc = curves.size();

        // create a new empty curve set
        CurveArray3D<Curve3D> res = new CurveArray3D<>();
        Curve3D curve;

        // format to ensure t is between T0 and T1
        t0 = Math.min(Math.max(t0, 0), nc * 2 - .6);
        t1 = Math.min(Math.max(t1, 0), nc * 2 - .6);

        // find curves index
        double t0f = Math.floor(t0);
        double t1f = Math.floor(t1);

        // indices of curves supporting points
        int ind0 = (int) Math.floor(t0f / 2);
        int ind1 = (int) Math.floor(t1f / 2);

        // case of t a little bit after a curve
        if (t0 - 2 * ind0 > 1.5) {
            ind0++;
        }
        if (t1 - 2 * ind1 > 1.5) {
            ind1++;
        }

        // start at the beginning of a curve
        t0f = 2 * ind0;
        t1f = 2 * ind1;

        double pos0, pos1;

        // need to subdivide only one curve
        if (ind0 == ind1 && t0 < t1) {
            curve = curves.get(ind0);
            pos0 = Curves3D.fromUnitSegment(t0 - t0f, curve.getT0(), curve.getT1());
            pos1 = Curves3D.fromUnitSegment(t1 - t1f, curve.getT0(), curve.getT1());
            res.add(curve.subCurve(pos0, pos1));
            return res;
        }

        // add the end of the curve containing first cut
        curve = curves.get(ind0);
        pos0 = Curves3D.fromUnitSegment(t0 - t0f, curve.getT0(), curve.getT1());
        res.add(curve.subCurve(pos0, curve.getT1()));

        if (ind1 > ind0) {
            // add all the whole curves between the 2 cuts
            for (int n = ind0 + 1; n < ind1; n++) {
                res.add(curves.get(n));
            }
        } else {
            // add all curves until the end of the set
            for (int n = ind0 + 1; n < nc; n++) {
                res.add(curves.get(n));
            }

            // add all curves from the beginning of the set
            for (int n = 0; n < ind1; n++) {
                res.add(curves.get(n));
            }
        }

        // add the beginning of the last cut curve
        curve = curves.get(ind1);
        pos1 = Curves3D.fromUnitSegment(t1 - t1f, curve.getT0(), curve.getT1());
        res.add(curve.subCurve(curve.getT0(), pos1));

        // return the curve set
        return res;
    }

    @Override
    public double distance(Point3D p) {
        double dist = Double.POSITIVE_INFINITY;
        for (Curve3D curve : curves) {
            dist = Math.min(dist, curve.distance(p));
        }
        return dist;
    }

    @Override
    public boolean isBounded() {
        return curves.stream().noneMatch((curve) -> (!curve.isBounded()));
    }

    @Override
    public Box3D boundingBox() {
        double xmin = Double.POSITIVE_INFINITY;
        double ymin = Double.POSITIVE_INFINITY;
        double zmin = Double.POSITIVE_INFINITY;
        double xmax = Double.NEGATIVE_INFINITY;
        double ymax = Double.NEGATIVE_INFINITY;
        double zmax = Double.NEGATIVE_INFINITY;

        Box3D box;
        for (Curve3D curve : curves) {
            box = curve.boundingBox();
            xmin = Math.min(xmin, box.getMinX());
            ymin = Math.min(ymin, box.getMinY());
            zmin = Math.min(zmin, box.getMinZ());
            xmax = Math.max(xmax, box.getMaxX());
            ymax = Math.max(ymax, box.getMaxY());
            zmax = Math.max(zmax, box.getMaxZ());
        }

        return new Box3D(xmin, xmax, ymin, ymax, zmin, zmax);
    }

    @Override
    public CurveArray3D<? extends Curve3D> transform(AffineTransform3D trans) {
        // Allocate array for result
        CurveArray3D<Curve3D> result = new CurveArray3D<>(curves.size());

        // add each transformed curve
        curves.forEach((curve) -> {
            result.add(curve.transform(trans));
        });
        return result;
    }

    @Override
    public Collection<? extends ContinuousCurve3D> continuousCurves() {
        // create array for storing result
        List<ContinuousCurve3D> continuousCurves = new ArrayList<>();

        // Iterate on curves, and add either the curve itself, or the set of
        // continuous curves making the curve
        curves.forEach((curve) -> {
            if (curve instanceof ContinuousCurve3D) {
                continuousCurves.add((ContinuousCurve3D) curve);
            } else {
                continuousCurves.addAll(curve.continuousCurves());
            }
        });

        return continuousCurves;
    }

    @Override
    public boolean equals(Object obj) {
        // check class, and cast type
        if (!(obj instanceof CurveArray3D<?>)) {
            return false;
        }
        CurveArray3D<?> curveSet = (CurveArray3D<?>) obj;

        // check the number of curves in each set
        if (this.size() != curveSet.size()) {
            return false;
        }

        // return false if at least one couple of curves does not match
        for (int i = 0; i < curves.size(); i++) {
            if (!curves.get(i).equals(curveSet.curves.get(i))) {
                return false;
            }
        }

        // otherwise return true
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.curves);
        return hash;
    }

    @Override
    public Iterator<T> iterator() {
        return curves.iterator();
    }

    @Override
    public Curve3D reverseCurve() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Shape3D clip(Box3D box) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean contains(Point3D point) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
