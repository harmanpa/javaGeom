/**
 * Hexagon.java
 */
package math.geom3d.csg.primitives;

import java.util.List;
import math.geom3d.csg.CSG;
import math.geom3d.csg.Polygon;
import math.geom3d.quickhull.QuickHullException;

public class Hexagon extends Primitive {

    /**
     * Hexagon circumscribed radius.
     */
    private double flatToFlatDistance = 1;

    /**
     * Hexagon circumscribed radius.
     */
    private double height = 1;

    private final double nunRad;

    /**
     * Constructor.Creates a new Hexagon that would fir a wrench of size
     * flatToFlatDistance radius {@code size}.
     *
     * @param flatToFlatDistance the size of wrench that this nut would fit
     * @param height
     *
     */
    public Hexagon(double flatToFlatDistance, double height) {
        this.flatToFlatDistance = flatToFlatDistance;
        this.height = height;
        nunRad = ((flatToFlatDistance / Math.sqrt(3)));
    }

    /* (non-Javadoc)
     * @see eu.mihosoft.vrl.v3d.Primitive#toPolygons()
     */
    @Override
    public List<Polygon> toPolygons() throws QuickHullException {
        CSG head = new Cylinder(nunRad, nunRad, height, (int) 6).toCSG();
        return head.getPolygons();
    }

    /**
     * Gets the flatToFlatDistance.
     *
     * @return the flatToFlatDistance
     */
    public double getFlatToFlatDistance() {
        return flatToFlatDistance;
    }

    /**
     * Gets the diameter of the outscribed circle. This is the Point To Point
     * Distance
     *
     * @return the Point To Point Distance
     */
    public double getPointToPointDistance() {
        return nunRad * 2;
    }

}
