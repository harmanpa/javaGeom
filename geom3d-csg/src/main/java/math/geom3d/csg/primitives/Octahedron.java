/**
 * Octahedron.java
 */
package math.geom3d.csg.primitives;

import java.util.ArrayList;
import java.util.List;
import math.geom3d.Point3D;
import math.geom3d.csg.Polygon;
import math.geom3d.csg.util.HullUtil;
import math.geom3d.quickhull.QuickHullException;

public class Octahedron extends Primitive {

    /**
     * Center of this octahedron.
     */
    private Point3D center;
    /**
     * Octahedron circumscribed radius.
     */
    private double radius;

    /**
     * The centered.
     */
    private boolean centered = true;

    /**
     * Constructor. Creates a new octahedron with center {@code [0,0,0]} and
     * dimensions {@code [1,1,1]}.
     */
    public Octahedron() {
        center = new Point3D(0, 0, 0);
        radius = 1;
    }

    /**
     * Constructor. Creates a new octahedron with center {@code [0,0,0]} and
     * radius {@code size}.
     *
     * @param size size
     */
    public Octahedron(double size) {
        center = new Point3D(0, 0, 0);
        radius = size;
    }

    /**
     * Constructor. Creates a new octahedron with the specified center and
     * radius.
     *
     * @param center center of the octahedron
     * @param circumradius of the octahedron
     */
    public Octahedron(Point3D center, double circumradius) {
        this.center = center;
        this.radius = circumradius;
    }

    /* (non-Javadoc)
     * @see eu.mihosoft.vrl.v3d.Primitive#toPolygons()
     */
    @Override
    public List<Polygon> toPolygons() throws QuickHullException {

        double sqrt2_2 = Math.sqrt(2) / 2;

        List<Point3D> points = new ArrayList<>();
        points.add(new Point3D(0, 0, -1));
        points.add(new Point3D(0, 0, +1));
        points.add(new Point3D(-sqrt2_2, -sqrt2_2, 0));
        points.add(new Point3D(-sqrt2_2, +sqrt2_2, 0));
        points.add(new Point3D(+sqrt2_2, -sqrt2_2, 0));
        points.add(new Point3D(+sqrt2_2, +sqrt2_2, 0));

        List<Polygon> polygons = HullUtil.hull(points).scale(radius).getPolygons();

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
    public Octahedron setCenter(Point3D center) {
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
     * Defines that this octahedron will not be centered.
     *
     * @return this octahedron
     */
    public Octahedron noCenter() {
        centered = false;
        return this;
    }

}
