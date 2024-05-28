/**
 *
 */
package math.geom2d.polygon;

import de.lighti.clipper.Clipper;
import de.lighti.clipper.Clipper.PolyType;
import de.lighti.clipper.DefaultClipper;
import de.lighti.clipper.Path;
import de.lighti.clipper.Paths;
import de.lighti.clipper.Point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import math.geom2d.Box2D;
import math.geom2d.Point2D;
import math.geom2d.Tolerance2D;
import math.geom2d.Vector2D;
import math.geom2d.circulinear.CirculinearDomain2D;
import math.geom2d.circulinear.buffer.BufferCalculator;
import math.geom2d.domain.Boundary2D;
import math.geom2d.domain.Boundaries2D;
import math.geom2d.domain.Contour2D;
import math.geom2d.domain.ContourArray2D;
import math.geom2d.line.LineSegment2D;
import math.geom2d.line.StraightLine2D;
import math.geom2d.point.PointSets2D;
import math.geom2d.polygon.convhull.JarvisMarch2D;
import org.apache.commons.math3.util.FastMath;

/**
 * Several utility functions for working on polygons, including polygon
 * creation, and basic computations such as polygon area or centroid.
 *
 * @author dlegland
 */
public final class Polygons2D {

    /**
     * Creates a new polygon representing a rectangle with edges parallel to the
     * main directions, and having the two specified opposite corners.
     *
     * @since 0.10.3
     */
    public final static SimplePolygon2D createRectangle(Point2D p1, Point2D p2) {
        // corners coordinates
        double x1 = p1.getX();
        double y1 = p1.getY();
        double x2 = p2.getX();
        double y2 = p2.getY();

        return createRectangle(x1, y1, x2, y2);
    }

    /**
     * Creates a new polygon representing a rectangle with edges parallel to the
     * main directions, and having the two specified opposite corners.
     *
     * @since 0.10.3
     */
    public final static SimplePolygon2D createRectangle(double x1, double y1,
            double x2, double y2) {
        // extremes coordinates
        double xmin = Math.min(x1, x2);
        double xmax = Math.max(x1, x2);
        double ymin = Math.min(y1, y2);
        double ymax = Math.max(y1, y2);

        // create result polygon
        return new SimplePolygon2D(
                new Point2D(xmin, ymin),
                new Point2D(xmax, ymin),
                new Point2D(xmax, ymax),
                new Point2D(xmin, ymax));
    }

    /**
     * Creates a new polygon representing a rectangle centered around a point.
     * Rectangle sides are parallel to the main axes. The function returns an
     * instance of SimplePolygon2D.
     *
     * @since 0.9.1
     */
    public final static SimplePolygon2D createCenteredRectangle(Point2D center,
            double length, double width) {
        // extract rectangle parameters
        double xc = center.x();
        double yc = center.y();
        double len = length / 2;
        double wid = width / 2;

        // coordinates of corners
        double x1 = xc - len;
        double y1 = yc - wid;
        double x2 = xc + len;
        double y2 = yc + wid;

        // create result polygon
        return new SimplePolygon2D(new Point2D[]{
            new Point2D(x1, y1),
            new Point2D(x2, y1),
            new Point2D(x2, y2),
            new Point2D(x1, y2),});
    }

    /**
     * Creates a new polygon representing an oriented rectangle centered around
     * a point. The function returns an instance of SimplePolygon2D.
     *
     * @since 0.9.1
     */
    public final static SimplePolygon2D createOrientedRectangle(Point2D center,
            double length, double width, double theta) {
        // extract rectangle parameters
        double xc = center.x();
        double yc = center.y();
        double len = length / 2;
        double wid = width / 2;

        // Pre-compute angle quantities
        double cot = FastMath.cos(theta);
        double sit = FastMath.sin(theta);

        // Create resulting rotated rectangle
        return new SimplePolygon2D(new Point2D[]{
            new Point2D(-len * cot + wid * sit + xc, -len * sit - wid * cot + yc),
            new Point2D(len * cot + wid * sit + xc, len * sit - wid * cot + yc),
            new Point2D(len * cot - wid * sit + xc, len * sit + wid * cot + yc),
            new Point2D(-len * cot - wid * sit + xc, -len * sit + wid * cot + yc),});
    }

    /**
     * Computes the centroid of the given polygon.
     *
     * @since 0.9.1
     */
    public final static Point2D computeCentroid(Polygon2D polygon) {
        // process case of simple polygon 
        if (polygon instanceof SimplePolygon2D) {
            LinearRing2D ring = ((SimplePolygon2D) polygon).getRing();
            return computeCentroid(ring);
        }

        double xc = 0;
        double yc = 0;
        double area;
        double cumArea = 0;
        Point2D centroid;

        for (LinearRing2D ring : polygon.contours()) {
            area = computeArea(ring);
            centroid = computeCentroid(ring);
            xc += centroid.x() * area;
            yc += centroid.y() * area;
            cumArea += area;
        }

        xc /= cumArea;
        yc /= cumArea;
        return new Point2D(xc, yc);
    }

    /**
     * Computes the centroid of the given linear ring.
     *
     * @since 0.9.1
     */
    public final static Point2D computeCentroid(LinearRing2D ring) {
        double xc = 0;
        double yc = 0;

        double x, y;
        double xp, yp;
        double tmp = 0;

        // number of vertices
        int n = ring.vertexNumber();

        // initialize with the last vertex
        Point2D prev = ring.vertex(n - 1);
        xp = prev.x();
        yp = prev.y();

        // iterate on vertices
        for (Point2D point : ring.vertices()) {
            x = point.x();
            y = point.y();
            tmp = xp * y - yp * x;
            xc += (x + xp) * tmp;
            yc += (y + yp) * tmp;

            prev = point;
            xp = x;
            yp = y;
        }

        double denom = computeArea(ring) * 6;
        return new Point2D(xc / denom, yc / denom);
    }

    /**
     * Computes the signed area of the polygon. Algorithm is taken from page: <a
     * href="http://local.wasp.uwa.edu.au/~pbourke/geometry/polyarea/">
     * http://local.wasp.uwa.edu.au/~pbourke/geometry/polyarea/</a>. Signed area
     * is positive if polygon is oriented counter-clockwise, and negative
     * otherwise. Result is wrong if polygon is self-intersecting.
     *
     * @return the signed area of the polygon.
     * @since 0.9.1
     */
    public final static double computeArea(Polygon2D polygon) {
        double area = 0;
        for (LinearRing2D ring : polygon.contours()) {
            area += computeArea(ring);
        }
        return area;
    }

    /**
     * Computes the signed area of the linear ring. Algorithm is taken from
     * page: <a
     * href="http://local.wasp.uwa.edu.au/~pbourke/geometry/polyarea/">
     * http://local.wasp.uwa.edu.au/~pbourke/geometry/polyarea/</a>. Signed area
     * is positive if linear ring is oriented counter-clockwise, and negative
     * otherwise. Result is wrong if linear ring is self-intersecting.
     *
     * @return the signed area of the polygon.
     * @since 0.9.1
     */
    public final static double computeArea(LinearRing2D ring) {
        double area = 0;

        // number of vertices
        int n = ring.vertexNumber();

        if (n == 0) {
            return 0.0;
        }

        // initialize with the last vertex
        Point2D prev = ring.vertex(n - 1);

        // iterate on edges
        for (Point2D point : ring.vertices()) {
            area += prev.x() * point.y() - prev.y() * point.x();
            prev = point;
        }

        return area /= 2;
    }

    /**
     * Computes the winding number of the polygon. Algorithm adapted from
     * http://www.geometryalgorithms.com/Archive/algorithm_0103/algorithm_0103.htm
     * http://softsurfer.com/Archive/algorithm_0103/algorithm_0103.htm
     *
     * @param vertices the vertices of a polygon
     * @param point the reference point
     * @return the number of windings of the curve around the point
     */
    public final static int windingNumber(Collection<Point2D> vertices,
            Point2D point) {
        int wn = 0; // the winding number counter

        // Extract the last point of the collection
        Point2D previous = null;
        for (Point2D vertex : vertices) {
            previous = vertex;
        }
        double y1 = previous.y();
        double y2;

        // keep y-coordinate of test point
        double y = point.y();

        // Iterate on couple of vertices, starting from couple (last,first)
        for (Point2D current : vertices) {
            // second vertex of current edge
            y2 = current.y();

            if (y1 <= y) {
                if (y2 > y) // an upward crossing
                {
                    if (isLeft(previous, current, point) > 0) {
                        wn++;
                    }
                }
            } else {
                if (y2 <= y) // a downward crossing
                {
                    if (isLeft(previous, current, point) < 0) {
                        wn--;
                    }
                }
            }

            // for next iteration
            y1 = y2;
            previous = current;
        }

        return wn;
    }

    public final static boolean rayTestInside(Polygon2D polygon, Point2D point) {
        StraightLine2D ray;
        if (point.distance(polygon.centroid()) < Tolerance2D.get()) {
            ray = new StraightLine2D(point, new Vector2D(1, 0));
        } else {
            ray = new StraightLine2D(point, polygon.centroid());
        }
        Map<Boolean, Set<Point2D>> partitioned = polygon.edges().stream()
                .map(edge -> edge.intersection(ray))
                .filter(i -> i != null)
                .collect(Collectors.partitioningBy(i -> ray.position(i) > 0.0, Collectors.toSet()));
        Set<Point2D> right = partitioned.get(Boolean.TRUE);
        Set<Point2D> left = partitioned.get(Boolean.FALSE);
        return right != null && left != null && !right.isEmpty() && !left.isEmpty() && (right.size() % 2 != 0 || left.size() % 2 != 0);
    }

    /**
     * Tests if a point is Left|On|Right of an infinite line. Input: three
     * points P0, P1, and P2 Return: >0 for P2 left of the line through P0 and
     * P1 =0 for P2 on the line <0 for P2 right of the line See: the January
     * 2001 Algorithm "Area of 2D and 3D Triangles and Polygons"
     */
    private final static int isLeft(Point2D p1, Point2D p2, Point2D pt) {
        double x = p1.x();
        double y = p1.y();
        return (int) Math.signum(
                (p2.x() - x) * (pt.y() - y) - (pt.x() - x) * (p2.y() - y));
    }

    /**
     * Returns the convex hull of the given set of points. Uses the Jarvis March
     * algorithm.
     *
     * @param points a collection of points
     * @return the convex hull of the set of points
     */
    public final static Polygon2D convexHull(Collection<? extends Point2D> points) {
        return new JarvisMarch2D().convexHull(points);
    }

    /**
     * Merge a pair of convex hulls. Incrementally adds points from the smaller
     * to the larger.
     *
     * @param hull1
     * @param hull2
     * @return
     */
    public final static Polygon2D mergeHulls(Polygon2D hull1, Polygon2D hull2) {
        if (hull1.vertexNumber() > hull2.vertexNumber()) {
            return incrementalHull(hull1, hull2.vertices());
        } else {
            return incrementalHull(hull2, hull1.vertices());
        }
    }

    /**
     * Incrementally add points to an existing convex hull
     *
     * @param hull
     * @param points
     * @return
     */
    public final static Polygon2D incrementalHull(Polygon2D hull, Collection<? extends Point2D> points) {
        for (Point2D point : points) {
            hull = incrementalHull(hull, point);
        }
        return hull;
    }

    /**
     * Add a point to an existing convex hull
     *
     * @param hull
     * @param point
     * @return
     */
    public final static Polygon2D incrementalHull(Polygon2D hull, Point2D point) {
        // https://cs.jhu.edu/~misha/Spring16/07.pdf
        int n = hull.vertexNumber();
        if (n < 3) {
            List<Point2D> newVertices = new ArrayList<>(n + 1);
            newVertices.addAll(hull.vertices());
            newVertices.add(point);
            return new SimplePolygon2D(newVertices);
        }
        boolean[] left = new boolean[n];
        int transition1 = -1;
        int transition2 = -1;
        for (int i = 0; i < n; i++) {
            left[i] = new LineSegment2D(hull.vertex(i), hull.vertex(i == n - 1 ? 0 : i + 1)).isInside(point);
        }
        for (int i = 0; i < n; i++) {
            if (left[i == 0 ? n - 1 : i - 1] && !left[i]) {
                transition1 = i;
            } else if (!left[i == 0 ? n - 1 : i - 1] && left[i]) {
                transition2 = i;
            }
        }
        if (transition1 == -1 || transition2 == -1) {
            return hull;
        }
        List<Point2D> newVertices = new ArrayList<>(n + 1);
        if (transition1 > transition2) {
            for (int i = transition2; i <= transition1; i++) {
                newVertices.add(hull.vertex(i));
            }
            newVertices.add(point);
        } else {
            for (int i = 0; i <= transition1; i++) {
                newVertices.add(hull.vertex(i));
            }
            newVertices.add(point);
            for (int i = transition2; i < n; i++) {
                newVertices.add(hull.vertex(i));
            }
        }
        return new SimplePolygon2D(newVertices);
    }

    /**
     * Computes the buffer at a distance d of the input polygon.The result is a
     * domain whose boundary is composed of line segments and circle arcs.
     *
     * @param polygon
     * @param dist
     * @return
     * @see Polygon2D#buffer(double)
     */
    public final static CirculinearDomain2D createBuffer(Polygon2D polygon, double dist) {
        // get current instance of buffer calculator
        BufferCalculator bc = BufferCalculator.getDefaultInstance();

        // compute buffer
        return bc.computeBuffer(polygon.boundary(), dist);
    }

    /**
     * Clips a polygon by a box. The result is a new polygon, that can be
     * multiple.
     *
     * @see Polygon2D#clip(Box2D)
     */
    public final static Polygon2D clipPolygon(Polygon2D polygon, Box2D box) {
        // Clip the boundary using generic method
        Boundary2D boundary = polygon.boundary();
        ContourArray2D<Contour2D> contours
                = Boundaries2D.clipBoundary(boundary, box);

        // convert boundaries to linear rings
        ArrayList<LinearRing2D> rings = new ArrayList<LinearRing2D>();
        for (Contour2D contour : contours) {
            rings.add(convertContourToLinearRing(contour));
        }

        // Create a polygon, either simple or multiple, depending on the ring
        // number
        if (rings.size() == 1) {
            return SimplePolygon2D.create(rings.get(0).vertices());
        } else {
            return MultiPolygon2D.create(rings);
        }
    }

    private final static LinearRing2D convertContourToLinearRing(
            Contour2D contour) {
        // process the basic case of simple class cast
        if (contour instanceof LinearRing2D) {
            return (LinearRing2D) contour;
        }

        // extract all vertices of the contour
        List<Point2D> vertices = new ArrayList<Point2D>();
        for (Point2D v : contour.singularPoints()) {
            vertices.add(v);
        }

        // remove adjacent multiple vertices
        vertices = PointSets2D.filterMultipleVertices(vertices, true);

        // Create new ring with vertices
        return LinearRing2D.create(vertices);
    }

    /**
     * Computes the union of the two polygons.
     */
    public final static Polygon2D union(Polygon2D polygon1,
            Polygon2D polygon2) {
        return compute(polygon1, polygon2, Clipper.ClipType.UNION);
    }

    public final static Polygon2D union(List<Polygon2D> polygon1,
            Polygon2D polygon2) {
        return compute(polygon1, Arrays.asList(polygon2), Clipper.ClipType.UNION);
    }

    public final static Polygon2D union(List<Polygon2D> polygon1,
            List<Polygon2D> polygon2) {
        return compute(polygon1, polygon2, Clipper.ClipType.UNION);
    }

    /**
     * Computes the intersection of the two polygons.
     */
    public final static Polygon2D intersection(Polygon2D polygon1,
            Polygon2D polygon2) {
        return compute(polygon1, polygon2, Clipper.ClipType.INTERSECTION);
    }

    /**
     * Computes the exclusive XOR of the two polygons.
     */
    public final static Polygon2D exclusiveOr(Polygon2D polygon1,
            Polygon2D polygon2) {
        return compute(polygon1, polygon2, Clipper.ClipType.XOR);
    }

    /**
     * Computes the Difference of the two polygons.
     *
     * @since 0.9.1
     */
    public final static Polygon2D difference(Polygon2D polygon1,
            Polygon2D polygon2) {
        return compute(polygon1, polygon2, Clipper.ClipType.DIFFERENCE);
    }

    public final static Polygon2D difference(List<Polygon2D> polygon1,
            Polygon2D polygon2) {
        return compute(polygon1, Arrays.asList(polygon2), Clipper.ClipType.DIFFERENCE);
    }

    public final static Polygon2D difference(Polygon2D polygon1,
            List<Polygon2D> polygon2) {
        return compute(Arrays.asList(polygon1), polygon2, Clipper.ClipType.DIFFERENCE);
    }

    private static Polygon2D compute(Polygon2D polygon1,
            Polygon2D polygon2, Clipper.ClipType clipType) {
        return compute(Arrays.asList(polygon1), Arrays.asList(polygon2), clipType);
    }

    private static Polygon2D compute(List<Polygon2D> polygons1,
            List<Polygon2D> polygons2, Clipper.ClipType clipType) {
        // compute
        final DefaultClipper cp = new DefaultClipper(Clipper.STRICTLY_SIMPLE);
        // convert to Clipper data structures
        polygons1.forEach((polygon1) -> {
            cp.addPath(convertToClipperPath(polygon1, 8), PolyType.SUBJECT, true);
        });
        polygons2.forEach((polygon2) -> {
            cp.addPath(convertToClipperPath(polygon2, 8), PolyType.CLIP, true);
        });
        Paths solution = new Paths();
        if (cp.execute(clipType, solution)) {
            // convert result to javaGeom structure
            return convertFromClipperPaths(solution, 8);
        }
        return null;
    }

    private static Path convertToClipperPath(Polygon2D polygon, int decimalPlaces) {
        double scaling = FastMath.pow(10, decimalPlaces);
        Path path = new Path(polygon.vertexNumber());
        polygon.vertices().forEach(v -> path.add(new Point.LongPoint((long) Math.round(v.getX() * scaling), (long) Math.round(v.getY() * scaling))));
        return path;
    }

    private static Polygon2D convertFromClipperPaths(Paths paths, int decimalPlaces) {
        int n = paths.size();

        // if the result is single, create a SimplePolygon
        if (n == 1) {
            Point2D[] points = extractPathVertices(paths.get(0), decimalPlaces);
            return SimplePolygon2D.create(points);
        }

        // extract the different rings of the resulting polygon
        LinearRing2D[] rings = new LinearRing2D[n];
        for (int i = 0; i < n; i++) {
            rings[i] = LinearRing2D.create(extractPathVertices(paths.get(i), decimalPlaces));
        }

        // create a multiple polygon
        return MultiPolygon2D.create(rings);
    }

    private static Point2D[] extractPathVertices(Path path, int decimalPlaces) {
        double scaling = FastMath.pow(10, decimalPlaces);
        int n = path.size();
        Point2D[] points = new Point2D[n];
        for (int i = 0; i < n; i++) {
            points[i] = new Point2D(path.get(i).getX() / scaling, path.get(i).getY() / scaling);
        }
        return points;
    }
}
