/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package math.geom3d.fitting;

import math.geom3d.Vector3D;
import math.geom3d.plane.Plane3D;
import math.geom3s.Vector3S;

/**
 *
 * @author peter
 */
public class Plane3DFitter extends Shape3DFitter<Plane3D> {

    public Plane3DFitter() {
        super(3, (x) -> Plane3D.fromNormal(new Vector3S(x[0], x[1]).toCartesian(), x[2]),
                (plane) -> {
                    Vector3S normal = Vector3S.fromCartesian(plane.normal());
                    double dist = plane.dist();
                    return new double[]{normal.getTheta(), normal.getPhi(), dist};
                },
                (points) -> Plane3D.createYZPlane());
    }

}
