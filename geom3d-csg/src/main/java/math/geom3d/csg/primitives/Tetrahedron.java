/**
 * Tetrahedron.java
 */
package math.geom3d.csg.primitives;

import java.util.ArrayList;
import java.util.List;
import math.geom3d.Point3D;
import math.geom3d.csg.Polygon;
import math.geom3d.csg.util.HullUtil;
import math.geom3d.quickhull.QuickHullException;

public class Tetrahedron extends Primitive {

    /**
     * Center of this tetrahedron.
     */
    private Point3D center;
    /**
     * Tetrahedron circumscribed radius.
     */
    private double radius;

    /**
     * The centered.
     */
    private boolean centered = true;

    /**
     * Constructor. Creates a new tetrahedron with center {@code [0,0,0]} and
     * dimensions {@code [1,1,1]}.
     */
    public Tetrahedron() {
        center = new Point3D(0, 0, 0);
        radius = 1;
    }

    /**
     * Constructor. Creates a new tetrahedron with center {@code [0,0,0]} and
     * radius {@code size}.
     *
     * @param size size
     */
    public Tetrahedron(double size) {
        center = new Point3D(0, 0, 0);
        radius = size;
    }

    /**
     * Constructor.Creates a new tetrahedron with the specified center and
     * radius.
     *
     * @param center center of the tetrahedron
     * @param size
     */
    public Tetrahedron(Point3D center, double size) {
        this.center = center;
        this.radius = size;
    }

    /* (non-Javadoc)
     * @see eu.mihosoft.vrl.v3d.Primitive#toPolygons()
     */
    @Override
    public List<Polygon> toPolygons() throws QuickHullException {

        double _1_sqrt2 = 1 / Math.sqrt(2);

        List<Point3D> points = new ArrayList<>();
        points.add(new Point3D(-1, 0, -_1_sqrt2));
        points.add(new Point3D(+1, 0, -_1_sqrt2));
        points.add(new Point3D(0, -1, +_1_sqrt2));
        points.add(new Point3D(0, +1, +_1_sqrt2));

        List<Polygon> polygons = HullUtil.hull(points).scale(radius / Math.sqrt(3)).getPolygons();

        return polygons;
    }

    /**
     * Gets the center.
     *
     * @return the center
     */
    public Point3D getCenter() {
        return center;
    }

    /**
     * Sets the center.
     *
     * @param center the center to set
     * @return
     */
    public Tetrahedron setCenter(Point3D center) {
        this.center = center;
        return this;
    }

    /**
     * Gets the radius.
     *
     * @return the radius
     */
    public double getRadius() {
        return radius;
    }

    /**
     * Sets the radius.
     *
     * @param radius the radius to set
     */
    public void setRadius(double radius) {
        this.radius = radius;
    }

    /**
     * Defines that this tetrahedron will not be centered.
     *
     * @return this tetrahedron
     */
    public Tetrahedron noCenter() {
        centered = false;
        return this;
    }

}
