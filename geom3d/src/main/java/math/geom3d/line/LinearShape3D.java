/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package math.geom3d.line;

import java.util.Collection;
import math.geom3d.Point3D;
import math.geom3d.Vector3D;
import math.geom3d.circulinear.CirculinearContinuousCurve3D;
import math.geom3d.circulinear.CirculinearCurve3D;
import math.geom3d.transform.AffineTransform3D;

/**
 *
 * @author peter
 */
public interface LinearShape3D extends CirculinearCurve3D {

    /**
     * Returns the straight line that contains this linear shape. The direction
     * is the same, and if possible the direction vector should be the same.
     *
     * @return the straight line that contains this linear shape
     */
    public abstract StraightLine3D supportingLine();

    /**
     * Returns a point in the linear shape.
     *
     * @return a point in the linear shape.
     */
    public abstract Point3D origin();

    /**
     * Return one direction vector of the linear shape.
     *
     * @return a direction vector
     */
    public abstract Vector3D direction();

    /**
     * Returns the unique intersection with a linear shape. If the intersection
     * doesn't exist (parallel lines), returns null.
     */
    public abstract Point3D intersection(LinearShape3D line);

    /**
     * Checks if the shape contains the orthogonal projection of the specified
     * point. The result is always true for straight lines. For bounded line
     * shapes, the result depends on the position of the point with respect to
     * shape bounds.
     *
     * @param point a point in the plane
     * @return true if the orthogonal projection of the point on the supporting
     * line belongs to the linear shape.
     */
    public boolean containsProjection(Point3D point);

    /**
     * Transforms this linear shape.
     */
    @Override
    public LinearShape3D transform(AffineTransform3D trans);
}
