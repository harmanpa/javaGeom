/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package math.geom3d.csg.primitives;

import java.util.List;
import math.geom3d.Point3D;
import math.geom3d.Vector3D;
import math.geom3d.csg.CSG;
import math.geom3d.csg.Polygon;
import math.geom3d.csg.Transform;
import static math.geom3d.csg.Transform.unity;
import math.geom3d.quickhull.QuickHullException;

// TODO: Auto-generated Javadoc
/**
 * The Class RoundedCube.
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class RoundedCube extends Primitive {

    /**
     * Cube dimensions.
     */
    private Vector3D dimensions;

    /**
     * The center.
     */
    private Point3D center;

    /**
     * The centered.
     */
    private boolean centered = true;

    /**
     * The corner radius.
     */
    private double cornerRadius = 0.1;

    /**
     * The resolution.
     */
    private int resolution = 8;

    /**
     * Constructor. Creates a new rounded cube with center {@code [0,0,0]} and
     * dimensions {@code [1,1,1]}.
     */
    public RoundedCube() {
        center = new Point3D(0, 0, 0);
        dimensions = new Vector3D(1, 1, 1);
    }

    /**
     * Constructor. Creates a new rounded cube with center {@code [0,0,0]} and
     * dimensions {@code [size,size,size]}.
     *
     * @param size size
     */
    public RoundedCube(double size) {
        center = new Point3D(0, 0, 0);
        dimensions = new Vector3D(size, size, size);
    }

    /**
     * Constructor. Creates a new rounded cuboid with the specified center and
     * dimensions.
     *
     * @param center center of the cuboid
     * @param dimensions cube dimensions
     */
    public RoundedCube(Point3D center, Vector3D dimensions) {
        this.center = center;
        this.dimensions = dimensions;
    }

    /**
     * Constructor. Creates a new rounded cuboid with center {@code [0,0,0]} and
     * with the specified dimensions.
     *
     * @param w width
     * @param h height
     * @param d depth
     */
    public RoundedCube(double w, double h, double d) {
        this(new Point3D(), new Vector3D(w, h, d));
    }


    /* (non-Javadoc)
     * @see eu.mihosoft.vrl.v3d.Primitive#toPolygons()
     */
    @Override
    public List<Polygon> toPolygons() throws QuickHullException {
        CSG spherePrototype
                = new Sphere(getCornerRadius(), getResolution() * 2, getResolution()).toCSG();

        double x = dimensions.getX() / 2.0 - getCornerRadius();
        double y = dimensions.getY() / 2.0 - getCornerRadius();
        double z = dimensions.getZ() / 2.0 - getCornerRadius();

        CSG sphere1 = spherePrototype.transformed(unity().translate(-x, -y, -z));
        CSG sphere2 = spherePrototype.transformed(unity().translate(x, -y, -z));
        CSG sphere3 = spherePrototype.transformed(unity().translate(x, y, -z));
        CSG sphere4 = spherePrototype.transformed(unity().translate(-x, y, -z));

        CSG sphere5 = spherePrototype.transformed(unity().translate(-x, -y, z));
        CSG sphere6 = spherePrototype.transformed(unity().translate(x, -y, z));
        CSG sphere7 = spherePrototype.transformed(unity().translate(x, y, z));
        CSG sphere8 = spherePrototype.transformed(unity().translate(-x, y, z));

        List<Polygon> result = sphere1.union(
                sphere2, sphere3, sphere4,
                sphere5, sphere6, sphere7, sphere8).hull().getPolygons();

        if (!centered) {

            Transform centerTransform = Transform.unity().translate(dimensions.getX() / 2.0, dimensions.getY() / 2.0, dimensions.getZ() / 2.0);

            for (Polygon p : result) {
                p.transform(centerTransform);
            }
        }

        return result;
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
    public void setCenter(Point3D center) {
        this.center = center;
    }

    /**
     * Gets the dimensions.
     *
     * @return the dimensions
     */
    public Vector3D getDimensions() {
        return dimensions;
    }

    /**
     * Sets the dimensions.
     *
     * @param dimensions the dimensions to set
     */
    public void setDimensions(Vector3D dimensions) {
        this.dimensions = dimensions;
    }

    /**
     * Defines that this cube will not be centered.
     *
     * @return this cube
     */
    public RoundedCube noCenter() {
        centered = false;
        return this;
    }

    /**
     * Gets the resolution.
     *
     * @return the resolution
     */
    public int getResolution() {
        return resolution;
    }

    /**
     * Sets the resolution.
     *
     * @param resolution the resolution to set
     */
    public void setResolution(int resolution) {
        this.resolution = resolution;
    }

    /**
     * Resolution.
     *
     * @param resolution the resolution to set
     * @return this cube
     */
    public RoundedCube resolution(int resolution) {
        this.resolution = resolution;
        return this;
    }

    /**
     * Gets the corner radius.
     *
     * @return the corner radius
     */
    public double getCornerRadius() {
        return cornerRadius;
    }

    /**
     * Sets the corner radius.
     *
     * @param cornerRadius the corner radius to set
     */
    public void setCornerRadius(double cornerRadius) {
        this.cornerRadius = cornerRadius;
    }

    /**
     * Corner radius.
     *
     * @param cornerRadius the corner radius to set
     * @return this cube
     */
    public RoundedCube cornerRadius(double cornerRadius) {
        this.cornerRadius = cornerRadius;
        return this;
    }

}
