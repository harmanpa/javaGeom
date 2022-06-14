/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package math.geom3s;

import java.util.Random;
import math.geom3d.Vector3D;
import math.geom3d.transform.AffineTransform3D;
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

    @Test
    public void testNorm() {
        Random r = new Random();
        for (int i = 0; i < 100; i++) {
            Vector3S v = new Vector3S(r.nextDouble(), r.nextDouble());
            Assert.assertTrue(Math.abs(v.toCartesian().norm() - 1.0) < 1e-12);
        }
    }

    @Test
    public void testTransform() {
        Random r = new Random();
        for (int i = 0; i < 100; i++) {
            Vector3S v1 = new Vector3S(r.nextDouble(), r.nextDouble());
            Vector3S v2 = new Vector3S(r.nextDouble(), r.nextDouble());
            AffineTransform3D t = v1.transformTo(v2);
            Vector3S v3 = v1.transform(t);
            Vector3S v4 = v2.transform(t.inverse());
            Assert.assertTrue(v3.equals(v2));
            Assert.assertTrue(v4.equals(v1));
        }
    }
}
