/**
 *
 */
package math.geom3d;

import java.util.Arrays;
import java.util.Collection;
import java.util.DoubleSummaryStatistics;
import java.util.stream.Stream;

/**
 * A 3-dimensional box, defined by its extent in each direction.
 *
 * @author dlegland
 */
public class Box3D {

    // ===================================================================
    // class variables
    private double xmin = 0;
    private double xmax = 0;
    private double ymin = 0;
    private double ymax = 0;
    private double zmin = 0;
    private double zmax = 0;

    /**
     * Empty constructor (size and position zero)
     */
    public Box3D() {
        this(0, 0, 0, 0, 0, 0);
    }

    /**
     * Main constructor, given bounds for x coord, bounds for y coord, and
     * bounds for z coord.A check is performed to ensure first bound is lower
     * than second bound.
     *
     * @param x0
     * @param x1
     * @param y0
     * @param y1
     * @param z0
     * @param z1
     */
    public Box3D(double x0, double x1, double y0, double y1, double z0,
            double z1) {
        xmin = Math.min(x0, x1);
        xmax = Math.max(x0, x1);
        ymin = Math.min(y0, y1);
        ymax = Math.max(y0, y1);
        zmin = Math.min(z0, z1);
        zmax = Math.max(z0, z1);
    }

    public static Box3D fromPoints(Point3D... points) {
        return fromPoints(Arrays.asList(points));
    }

    /**
     * Creates a bounding box for any set of points
     *
     * @param points
     * @return
     */
    public static Box3D fromPoints(Collection<Point3D> points) {
        DoubleSummaryStatistics xStats = points.stream().mapToDouble(point -> point.getX()).summaryStatistics();
        DoubleSummaryStatistics yStats = points.stream().mapToDouble(point -> point.getX()).summaryStatistics();
        DoubleSummaryStatistics zStats = points.stream().mapToDouble(point -> point.getX()).summaryStatistics();
        return new Box3D(xStats.getMin(), xStats.getMax(), yStats.getMin(), yStats.getMax(), zStats.getMin(), zStats.getMax());
    }

    /**
     * Constructor from 2 points, giving extreme coordinates of the box.
     *
     * @param p1
     * @param p2
     */
    public Box3D(Point3D p1, Point3D p2) {
        this(p1.getX(), p2.getX(), p1.getY(), p2.getY(), p1.getZ(), p2.getZ());
    }

    // ===================================================================
    // accessors to Box2D fields
    public double getMinX() {
        return xmin;
    }

    public double getMaxX() {
        return xmax;
    }

    public double getMinY() {
        return ymin;
    }

    public double getMaxY() {
        return ymax;
    }

    public double getMinZ() {
        return zmin;
    }

    public double getMaxZ() {
        return zmax;
    }

    /**
     * Returns the width, i.e.the difference between the min and max x coord
     *
     * @return
     */
    public double getWidth() {
        return xmax - xmin;
    }

    /**
     * Returns the height, i.e.the difference between the min and max y coord
     *
     * @return
     */
    public double getHeight() {
        return ymax - ymin;
    }

    /**
     * Returns the depth, i.e.the difference between the min and max z coord
     *
     * @return
     */
    public double getDepth() {
        return zmax - zmin;
    }

    public Point3D getCenter() {
        return new Point3D((xmax + xmin) / 2, (ymax + ymin) / 2, (zmax + zmin) / 2);
    }

    public Vector3D getDimensions() {
        return new Vector3D(xmax - xmin, ymax - ymin, zmax - zmin);
    }

    public Point3D[] getExtremes() {
        return new Point3D[]{new Point3D(xmin, ymin, zmin), new Point3D(xmax, ymax, zmax)};
    }

    /**
     * Returns the Box2D which contains both this box and the specified box.
     *
     * @param box the bounding box to include
     * @return this
     */
    public Box3D union(Box3D box) {
        return new Box3D(
                Math.min(this.xmin, box.xmin),
                Math.max(this.xmax, box.xmax),
                Math.min(this.ymin, box.ymin),
                Math.max(this.ymax, box.ymax),
                Math.min(this.zmin, box.zmin),
                Math.max(this.zmax, box.zmax));
    }

    /**
     * Returns the Box2D which is contained both by this box and by the
     * specified box.
     *
     * @param box the bounding box to include
     * @return this
     */
    public Box3D intersection(Box3D box) {
        return new Box3D(
                Math.max(this.xmin, box.xmin),
                Math.min(this.xmax, box.xmax),
                Math.max(this.ymin, box.ymin),
                Math.min(this.ymax, box.ymax),
                Math.max(this.zmin, box.zmin),
                Math.min(this.zmax, box.zmax));
    }

    public boolean intersects(Box3D box) {
        if (box.getMinX() > this.xmax || box.getMaxX() < this.getMinX()) {
            return false;
        }
        if (box.getMinY() > this.getMaxY() || box.getMaxY() < this.getMinY()) {
            return false;
        }
        return !(box.getMinZ() > this.getMaxZ() || box.getMaxZ() < this.getMinZ());
    }

    public boolean contains(Point3D point) {
        return point.getX() >= xmin && point.getX() <= xmax
                && point.getY() >= ymin && point.getY() <= ymax
                && point.getZ() >= zmin && point.getZ() <= zmax;
    }

    public boolean contains(Box3D box) {
        return containsAll(box.getExtremes());
    }

    public boolean containsAll(Point3D... points) {
        return containsAll(Arrays.asList(points));
    }

    public boolean containsAll(Collection<Point3D> points) {
        return points.stream().allMatch(p -> contains(p));
    }

}
