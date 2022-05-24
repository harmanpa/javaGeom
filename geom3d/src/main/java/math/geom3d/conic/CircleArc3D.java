/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package math.geom3d.conic;

import math.geom2d.conic.CircleArc2D;
import math.geom3d.Point3D;
import math.geom3d.Vector3D;
import math.geom3d.plane.PlanarContinuousCurve3D;
import math.geom3d.plane.Plane3D;

/**
 *
 * @author peter
 */
public class CircleArc3D extends PlanarContinuousCurve3D<CircleArc2D> {

    public CircleArc3D(Point3D centre, Vector3D normal, double radius, double start, double extent) {
        super(Plane3D.fromNormal(centre, normal), new CircleArc2D(0, 0, radius, start, extent));
    }
}
