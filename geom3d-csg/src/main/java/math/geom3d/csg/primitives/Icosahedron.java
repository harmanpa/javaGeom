/**
 * Icosahedron.java
 */
package math.geom3d.csg.primitives;

import java.util.ArrayList;
import java.util.List;
import math.geom3d.Point3D;
import math.geom3d.csg.Polygon;
import math.geom3d.csg.util.HullUtil;
import math.geom3d.quickhull.QuickHullException;

public class Icosahedron extends Primitive {

    /**
     * Center of this icosahedron.
     */
    private Point3D center;
    /**
     * Icosahedron circumscribed radius.
     */
    private double radius;

    /**
     * The centered.
     */
    private boolean centered = true;

    /**
     * Constructor. Creates a new icosahedron with center {@code [0,0,0]} and
     * dimensions {@code [1,1,1]}.
     */
    public Icosahedron() {
        center = new Point3D(0, 0, 0);
        radius = 1;
    }

    /**
     * Constructor. Creates a new icosahedron with center {@code [0,0,0]} and
     * radius {@code size}.
     *
     * @param size size
     */
    public Icosahedron(double size) {
        center = new Point3D(0, 0, 0);
        radius = size;
    }

    /**
     * Constructor. Creates a new icosahedron with the specified center and
     * radius.
     *
     * @param center center of the icosahedron
     * @param circumradius of the icosahedron
     */
    public Icosahedron(Point3D center, double size) {
        this.center = center;
        this.radius = size;
    }

    /* (non-Javadoc)
     * @see eu.mihosoft.vrl.v3d.Primitive#toPolygons()
     */
    @Override
    public List<Polygon> toPolygons() throws QuickHullException {

        double phi = (Math.sqrt(5) + 1) / 2;

        List<Point3D> points = new ArrayList<>();
        points.add(new Point3D(0, 1, phi));
        points.add(new Point3D(0, -1, phi));
        points.add(new Point3D(phi, 0, 1));
        points.add(new Point3D(1, phi, 0));
        points.add(new Point3D(-1, phi, 0));
        points.add(new Point3D(-phi, 0, 1));
        points.add(new Point3D(1, -phi, 0));
        points.add(new Point3D(phi, 0, -1));
        points.add(new Point3D(0, 1, -phi));
        points.add(new Point3D(-phi, 0, -1));
        points.add(new Point3D(-1, -phi, 0));
        points.add(new Point3D(0, -1, -phi));

        List<Polygon> polygons = HullUtil.hull(points).scale(radius / (Math.sqrt(1 + Math.pow(phi, 2)))).getPolygons();

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
     */
    public Icosahedron setCenter(Point3D center) {
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
     * Defines that this icosahedron will not be centered.
     *
     * @return this icosahedron
     */
    public Icosahedron noCenter() {
        centered = false;
        return this;
    }

}
