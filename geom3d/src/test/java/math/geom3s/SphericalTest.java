/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package math.geom3s;

import java.util.Random;
import math.geom3d.Vector3D;
import org.junit.Test;
import org.junit.Assert;

/**
 *
 * @author peter
 */
public class SphericalTest {

    @Test
    public void test() {
        Random r = new Random();
        for (int i = 0; i < 100; i++) {
            Vector3D v = new Vector3D(r.nextDouble(), r.nextDouble(), r.nextDouble());
            Assert.assertTrue(Vector3S.fromCartesian(v).toCartesian().minus(v).norm() < 1e-12);
        }
    }
}
