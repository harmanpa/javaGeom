/**
 *
 */
package math.geom3d;

import java.util.Arrays;
import java.util.Collection;
import java.util.DoubleSummaryStatistics;
import java.util.function.Supplier;
import java.util.stream.Stream;
import math.geom2d.Range1D;
import math.geom3s.Vector3S;
import org.apache.commons.math3.util.FastMath;
import math.geom3d.transform.AffineTransform3D;

/**
 * A 3-dimensional box, defined by its extent in each direction.
 *
 * @author dlegland
 */
public class Box3D implements GeometricObject3D {

    // ===================================================================
    // class variables
    private final double xmin;
    private final double xmax;
    private final double ymin;
    private final double ymax;
    private final double zmin;
    private final double zmax;

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
        DoubleSummaryStatistics yStats = points.stream().mapToDouble(point -> point.getY()).summaryStatistics();
        DoubleSummaryStatistics zStats = points.stream().mapToDouble(point -> point.getZ()).summaryStatistics();
        return new Box3D(xStats.getMin(), xStats.getMax(), yStats.getMin(), yStats.getMax(), zStats.getMin(), zStats.getMax());
    }

    public static Box3D fromShapes(Shape3D... shapes) {
        return fromShapes(Arrays.asList(shapes));
    }

    public static Box3D fromShapes(Collection<Shape3D> shapes) {
        Box3D box = new Box3D();
        for (Shape3D shape : shapes) {
            box = box.union(shape.boundingBox());
        }
        return box;
    }

    public static Box3D fromSuppliers(Supplier<Box3D>... suppliers) {
        return fromSuppliers(Arrays.asList(suppliers));
    }

    public static Box3D fromSuppliers(Collection<Supplier<Box3D>> suppliers) {
        Box3D box = new Box3D();
        for (Supplier<Box3D> supplier : suppliers) {
            box = box.union(supplier.get());
        }
        return box;
    }

    public static Box3D reduce(Stream<Box3D> stream) {
        return stream.reduce(new Box3D(), (a, b) -> a.union(b));
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

    @Override
    public int hashCode() {
        return GeometricObject3D.hash(7, 23, xmin, xmax, ymin, ymax, zmin, zmax);
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

    public static Box3D rotated(Shape3D shape, Vector3D direction) {
        Box3D box = shape.transform(
                Vector3S.fromCartesian(direction)
                        .transformTo(Vector3S.fromCartesian(new Vector3D(0, 0, 1))))
                .boundingBox();
        return box;
    }

    public Range1D[] getRanges() {
        return new Range1D[]{new Range1D(getMinX(), getMaxX()), new Range1D(getMinY(), getMaxY()), new Range1D(getMinZ(), getMaxZ())};
    }

    public Stream<Point3D> streamVertices() {
        return Stream.of(true, false).flatMap(x -> 
            Stream.of(true, false).flatMap(y -> 
                Stream.of(true, false).map(z -> new Point3D(x ? getMaxX() : getMinX(), y ? getMaxY() : getMinY(), z ? getMaxZ() : getMinZ()))));
    }

    public Range1D getRange(Vector3D vector) {
        AffineTransform3D transform = Vector3S.fromCartesian(vector.normalize())
                .transformTo(Vector3S.fromCartesian(new Vector3D(0, 0, 1)));
        DoubleSummaryStatistics ss = streamVertices().mapToDouble(v -> v.getZ()).summaryStatistics();
        return new Range1D(ss.getMin(), ss.getMax());
    }

    public double diagonal() {
        Point3D[] corners = getExtremes();
        return corners[0].distance(corners[1]);
    }

    /**
     * Returns an approximate distance unless the approximate distance is below a given value
     */
    public double fastDistance(Box3D other, double accurateBelow) {
        double fDistance = getCenter().distance(other.getCenter()) - (diagonal()/2 + other.diagonal()/2);
        if(fDistance<accurateBelow) {
            return distance(other);
        }
        return fDistance;
    }

    public double distance(Box3D other) {
        int[] overlaps = new int[3];
        Range1D[] ranges = getRanges();
        Range1D[] otherRanges = other.getRanges();
        for(int i=0; i<3; i++) {
            overlaps[i] = ranges[i].compareTo(otherRanges[i]);
        }
        if(overlaps[0]==0 && overlaps[1]==0 && overlaps[2]!=0) {
            return ranges[2].distance(otherRanges[2]);
        } else if(overlaps[0]==0 && overlaps[1]!=0 && overlaps[2]==0) {
            return ranges[1].distance(otherRanges[1]);
        } else if(overlaps[0]!=0 && overlaps[1]==0 && overlaps[2]==0) {
            return ranges[0].distance(otherRanges[0]);
        } else if(overlaps[0]==0 && overlaps[1]==0 && overlaps[2]==0) {
            return Math.max(Math.max(ranges[0].distance(otherRanges[0]), ranges[1].distance(otherRanges[1])), ranges[2].distance(otherRanges[2]));
        } else {
            return streamVertices().mapToDouble(v -> other.streamVertices().mapToDouble(v2 -> v.distance(v2)).min().getAsDouble()).min().getAsDouble();
        }
    }
}
