/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package math.geom3d.conic;

import math.geom2d.conic.Circle2D;
import math.geom3d.Point3D;
import math.geom3d.Vector3D;
import math.geom3d.circulinear.PlanarCirculinearRing3D;
import math.geom3d.plane.Plane3D;

/**
 *
 * @author peter
 */
public class Circle3D extends PlanarCirculinearRing3D<Circle2D> {

    public Circle3D(Plane3D plane, Circle2D shape) {
        super(plane, shape);
    }

    public Circle3D(Point3D centre, Vector3D normal, double radius) {
        super(Plane3D.fromNormal(centre, normal), new Circle2D(0, 0, radius));
    }

}
