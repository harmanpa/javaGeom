/* File CurveArray2D.java 
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
 */
package math.geom2d.curve;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import math.geom2d.*;
import math.geom2d.line.LinearShape2D;

/**
 * <p>
 * A parameterized set of curves. A curve cannot be included twice in a
 * CurveArray2D.
 * </p>
 * <p>
 * The k-th curve contains points with positions between 2*k and 2*k+1. This
 * allows to differentiate extremities of contiguous curves. The points with
 * positions t between 2*k+1 and 2*k+2 belong to the curve k if t<2*k+1.5, or
 * to the curve k+1 if t>2*k+1.5
 * </p>
 *
 * @author Legland
 * @param <T>
 */
public class CurveArray2D<T extends Curve2D>
        implements CurveSet2D<T>, Iterable<T> {

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
    public static <T extends Curve2D> CurveArray2D<T> create(Collection<T> curves) {
        return new CurveArray2D<>(curves);
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
    public static <T extends Curve2D> CurveArray2D<T> create(
            T... curves) {
        return new CurveArray2D<>(curves);
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
    public CurveArray2D() {
        this.curves = new ArrayList<>();
    }

    /**
     * Empty constructor. Initializes an empty array of curves, with a given
     * size for allocating memory.
     * @param n
     */
    public CurveArray2D(int n) {
        this.curves = new ArrayList<>(n);
    }

    /**
     * Constructor from an array of curves.
     *
     * @param curves the array of curves in the set
     */
    @SafeVarargs
    public CurveArray2D(T... curves) {
        this(curves.length);
        this.curves.addAll(Arrays.asList(curves));
    }

    public CurveArray2D(CurveSet2D<? extends T> set) {
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
    public CurveArray2D(Collection<? extends T> curves) {
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
        double t0 = curve.t0();
        double t1 = curve.t1();
        return Curves2D.fromUnitSegment(t - 2 * i, t0, t1);
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
        double t0 = curve.t0();
        double t1 = curve.t1();
        return Curves2D.toUnitSegment(t, t0, t1) + i * 2;
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

    // ===================================================================
    // methods inherited from interface Curve2D
    @Override
    public Collection<Point2D> intersections(LinearShape2D line) {
        List<Point2D> intersect = new ArrayList<>();

        // add intersections with each curve
        curves.forEach((curve) -> {
            intersect.addAll(curve.intersections(line));
        });

        return intersect;
    }

    @Override
    public double t0() {
        return 0;
    }

    @Override
    public double t1() {
        return Math.max(curves.size() * 2 - 1, 0);
    }

    @Override
    public Point2D point(double t) {
        if (curves.isEmpty()) {
            return null;
        }
        if (t < t0()) {
            return this.firstCurve().firstPoint();
        }
        if (t > t1()) {
            return this.lastCurve().lastPoint();
        }

        // curve index
        int nc = (int) Math.floor(t);

        // check index if even-> corresponds to a curve
        int indc = (int) Math.floor(nc / 2);
        if (indc * 2 == nc) {
            Curve2D curve = curves.get(indc);
            double pos = Curves2D.fromUnitSegment(t - nc,
                    curve.t0(), curve.t1());
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
    public Point2D firstPoint() {
        if (curves.isEmpty()) {
            return null;
        }
        return firstCurve().firstPoint();
    }

    @Override
    public Point2D lastPoint() {
        if (curves.isEmpty()) {
            return null;
        }
        return lastCurve().lastPoint();
    }

    @Override
    public Collection<Point2D> singularPoints() {
        // create array for result
        List<Point2D> points = new ArrayList<>();
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
            if (!Curves2D.isLeftInfinite(curve)) {
                addPointWithGuardDistance(points, curve.firstPoint(), eps);
            }
            // add last extremity
            return curve;
        }).filter((curve) -> (!Curves2D.isRightInfinite(curve))).forEachOrdered((curve) -> {
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
    private void addPointWithGuardDistance(Collection<Point2D> pointSet,
            Point2D point, double eps) {
        for (Point2D p0 : pointSet) {
            if (p0.almostEquals(point, eps)) {
                return;
            }
        }
        pointSet.add(point);
    }

    @Override
    public Collection<Point2D> vertices() {
        return this.singularPoints();
    }

    @Override
    public boolean isSingular(double pos) {
        if (Math.abs(pos - Math.round(pos)) < Tolerance2D.get()) {
            return true;
        }

        int nc = this.curveIndex(pos);
        // int nc = (int) Math.floor(pos);
        if (nc - Math.floor(pos / 2.0) > 0) {
            return true; // if is between 2
        }        // curves

        Curve2D curve = curves.get(nc);
        // double pos2 = fromUnitSegment(pos-2*nc, curve.getT0(),
        // curve.getT1());
        return curve.isSingular(this.localPosition(pos));
    }

    @Override
    public double position(Point2D point) {
        double minDist = Double.MAX_VALUE, dist;
        double x = point.x(), y = point.y();
        double pos = 0, t0, t1;

        int i = 0;
        for (Curve2D curve : curves) {
            dist = curve.distance(x, y);
            if (dist < minDist) {
                pos = curve.position(point);
                minDist = dist;
                // format position
                t0 = curve.t0();
                t1 = curve.t1();
                pos = Curves2D.toUnitSegment(pos, t0, t1) + i * 2;
            }
            i++;
        }
        return pos;
    }

    @Override
    public double project(Point2D point) {
        double minDist = Double.MAX_VALUE, dist;
        double x = point.x(), y = point.y();
        double pos = 0, t0, t1;

        int i = 0;
        for (Curve2D curve : curves) {
            dist = curve.distance(x, y);
            if (dist < minDist) {
                minDist = dist;
                pos = curve.project(point);
                // format position
                t0 = curve.t0();
                t1 = curve.t1();
                pos = Curves2D.toUnitSegment(pos, t0, t1) + i * 2;
            }
            i++;
        }
        return pos;
    }

    @Override
    public Curve2D reverse() {
        // create array of reversed curves
        int n = curves.size();
        Curve2D[] curves2 = new Curve2D[n];

        // reverse each curve
        for (int i = 0; i < n; i++) {
            curves2[i] = curves.get(n - 1 - i).reverse();
        }

        // create the reversed final curve
        return new CurveArray2D<>(curves2);
    }

    @Override
    public CurveSet2D<? extends Curve2D> subCurve(double t0, double t1) {
        // number of curves in the set
        int nc = curves.size();

        // create a new empty curve set
        CurveArray2D<Curve2D> res = new CurveArray2D<>();
        Curve2D curve;

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
            pos0 = Curves2D.fromUnitSegment(t0 - t0f, curve.t0(), curve.t1());
            pos1 = Curves2D.fromUnitSegment(t1 - t1f, curve.t0(), curve.t1());
            res.add(curve.subCurve(pos0, pos1));
            return res;
        }

        // add the end of the curve containing first cut
        curve = curves.get(ind0);
        pos0 = Curves2D.fromUnitSegment(t0 - t0f, curve.t0(), curve.t1());
        res.add(curve.subCurve(pos0, curve.t1()));

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
        pos1 = Curves2D.fromUnitSegment(t1 - t1f, curve.t0(), curve.t1());
        res.add(curve.subCurve(curve.t0(), pos1));

        // return the curve set
        return res;
    }

    @Override
    public double distance(Point2D p) {
        return distance(p.x(), p.y());
    }

    @Override
    public double distance(double x, double y) {
        double dist = Double.POSITIVE_INFINITY;
        for (Curve2D curve : curves) {
            dist = Math.min(dist, curve.distance(x, y));
        }
        return dist;
    }

    @Override
    public boolean isBounded() {
        return curves.stream().noneMatch((curve) -> (!curve.isBounded()));
    }

    @Override
    public CurveSet2D<? extends Curve2D> clip(Box2D box) {
        // Simply calls the generic method in Curve2DUtils
        return Curves2D.clipCurveSet(this, box);
    }

    @Override
    public Box2D boundingBox() {
        double xmin = Double.POSITIVE_INFINITY;
        double ymin = Double.POSITIVE_INFINITY;
        double xmax = Double.NEGATIVE_INFINITY;
        double ymax = Double.NEGATIVE_INFINITY;

        Box2D box;
        for (Curve2D curve : curves) {
            box = curve.boundingBox();
            xmin = Math.min(xmin, box.getMinX());
            ymin = Math.min(ymin, box.getMinY());
            xmax = Math.max(xmax, box.getMaxX());
            ymax = Math.max(ymax, box.getMaxY());
        }

        return new Box2D(xmin, xmax, ymin, ymax);
    }

    @Override
    public CurveArray2D<? extends Curve2D> transform(AffineTransform2D trans) {
        // Allocate array for result
        CurveArray2D<Curve2D> result = new CurveArray2D<>(curves.size());

        // add each transformed curve
        curves.forEach((curve) -> {
            result.add(curve.transform(trans));
        });
        return result;
    }

    @Override
    public Collection<? extends ContinuousCurve2D> continuousCurves() {
        // create array for storing result
        List<ContinuousCurve2D> continuousCurves = new ArrayList<>();

        // Iterate on curves, and add either the curve itself, or the set of
        // continuous curves making the curve
        curves.forEach((curve) -> {
            if (curve instanceof ContinuousCurve2D) {
                continuousCurves.add((ContinuousCurve2D) curve);
            } else {
                continuousCurves.addAll(curve.continuousCurves());
            }
        });

        return continuousCurves;
    }

    @Override
    public boolean contains(Point2D p) {
        return contains(p.x(), p.y());
    }

    @Override
    public boolean contains(double x, double y) {
        return curves.stream().anyMatch((curve) -> (curve.contains(x, y)));
    }

    public java.awt.geom.GeneralPath getGeneralPath() {
        // create new path
        java.awt.geom.GeneralPath path = new java.awt.geom.GeneralPath();

        // check case of empty curve set
        if (curves.isEmpty()) {
            return path;
        }

        // move to the first point of the first curves
        Point2D point;
        for (ContinuousCurve2D curve : this.continuousCurves()) {
            point = curve.firstPoint();
            path.moveTo((float) point.x(), (float) point.y());
            path = curve.appendPath(path);
        }

        // return the final path
        return path;
    }

    @Override
    public Shape asAwtShape() {
        return this.getGeneralPath();
    }

    @Override
    public void draw(Graphics2D g2) {
        curves.forEach((curve) -> {
            curve.draw(g2);
        });
    }

    @Override
    public boolean almostEquals(GeometricObject2D obj, double eps) {
        if (this == obj) {
            return true;
        }

        // check class, and cast type
        if (!(obj instanceof CurveArray2D<?>)) {
            return false;
        }
        CurveArray2D<?> shapeSet = (CurveArray2D<?>) obj;

        // check the number of curves in each set
        if (this.curves.size() != shapeSet.curves.size()) {
            return false;
        }

        // return false if at least one couple of curves does not match
        for (int i = 0; i < curves.size(); i++) {
            if (!curves.get(i).almostEquals(shapeSet.curves.get(i), eps)) {
                return false;
            }
        }

        // otherwise return true
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        // check class, and cast type
        if (!(obj instanceof CurveArray2D<?>)) {
            return false;
        }
        CurveArray2D<?> curveSet = (CurveArray2D<?>) obj;

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

}
