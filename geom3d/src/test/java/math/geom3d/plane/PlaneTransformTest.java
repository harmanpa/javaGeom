/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package math.geom3d.plane;

import math.geom3d.transform.AffineTransform3D;
import org.junit.Test;

/**
 *
 * @author peter
 */
public class PlaneTransformTest {

    @Test
    public void test() {
        AffineTransform3D at3 = Plane3D.createXZPlane().transform3D(Plane3D.createYZPlane());
        
    }
}
