/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package math.geom3d.fitting;

import math.geom3d.Vector3D;
import math.geom3d.plane.Plane3D;

/**
 *
 * @author peter
 */
public class Plane3DFitter extends Shape3DFitter<Plane3D> {

    public Plane3DFitter() {
        super(4, (x) -> Plane3D.fromNormal(new Vector3D(x[0], x[1], x[2]).normalize(), x[3]),
                (plane) -> {
                    Vector3D normal = plane.normal();
                    double dist = plane.dist();
                    return new double[]{normal.getX(), normal.getY(), normal.getZ(), dist};
                },
                (points) -> Plane3D.createYZPlane());
    }

}
